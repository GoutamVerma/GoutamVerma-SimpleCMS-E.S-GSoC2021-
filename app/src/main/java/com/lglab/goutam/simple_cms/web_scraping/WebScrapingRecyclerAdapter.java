package com.lglab.goutam.simple_cms.web_scraping;

import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.lglab.goutam.simple_cms.R;
import com.lglab.goutam.simple_cms.create.utility.connection.LGConnectionTest;
import com.lglab.goutam.simple_cms.create.utility.model.ActionController;
import com.lglab.goutam.simple_cms.create.utility.model.balloon.Balloon;
import com.lglab.goutam.simple_cms.create.utility.model.poi.POI;
import com.lglab.goutam.simple_cms.create.utility.model.poi.POICamera;
import com.lglab.goutam.simple_cms.create.utility.model.poi.POILocation;
import com.lglab.goutam.simple_cms.utility.ConstantPrefs;
import com.lglab.goutam.simple_cms.web_scraping.data.GDG;
import com.lglab.goutam.simple_cms.web_scraping.data.InfoScraping;
import com.lglab.goutam.simple_cms.web_scraping.data.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.content.Context.MODE_PRIVATE;

/**
 * This is the class in charge of the adapter of the WebScraping recyclerview of the class WebScraping
 */
public class WebScrapingRecyclerAdapter extends RecyclerView.Adapter<WebScrapingRecyclerAdapter.ViewHolder> implements Filterable {

    private static final String TAG_DEBUG = "WebScrapingRecyclerAdapter";

    private AppCompatActivity activity;
    private List<InfoScraping> infoScrapings;
    private List<InfoScraping> infoScrapingsFull;
    private WebScrapingRecyclerAdapter.OnNoteListener mOnNoteListener;
    private TextView connectionStatus;


    WebScrapingRecyclerAdapter(AppCompatActivity activity, List<InfoScraping> infoScrapings,
                               WebScrapingRecyclerAdapter.OnNoteListener onNoteListener,
                               TextView connectionStatus) {
        this.activity = activity;
        this.infoScrapings = infoScrapings;
        infoScrapingsFull = new ArrayList<>(infoScrapings);
        this.mOnNoteListener = onNoteListener;
        this.connectionStatus = connectionStatus;
    }

    @NonNull
    @Override
    public WebScrapingRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_web_scrapping, parent, false);
        return new WebScrapingRecyclerAdapter.ViewHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull WebScrapingRecyclerAdapter.ViewHolder holder, int position) {
        InfoScraping currentItem = infoScrapings.get(position);
        int type = currentItem.getType();
         if(type == Constant.GDG.getId()){
            GDG gdg = (GDG) currentItem;
            holder.name.setText(gdg.getName());
            holder.city.setText(gdg.getCity());
            holder.country.setText(gdg.getCountry());
        }else{
            Log.w(TAG_DEBUG, "ERROR TYPE");
        }
    }

    @Override
    public int getItemCount() {
        return infoScrapings.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<InfoScraping> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(infoScrapingsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (InfoScraping infoScraping : infoScrapingsFull) {
                    GDG gdg = (GDG) infoScraping;
                    if(gdg.getCountry().toLowerCase().contains(filterPattern)
                            || gdg.getCity().toLowerCase().contains(filterPattern)
                            || gdg.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(gdg);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
}

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        infoScrapings.clear();
        infoScrapings.addAll((List) results.values);
        notifyDataSetChanged();
    }
};


    /**
     * This is the most efficient way to have the view holder and the click listener
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name, city, country;
        Button buttonShowLG;
        WebScrapingRecyclerAdapter.OnNoteListener mOnNoteListener;

        private Handler handler = new Handler();

        ViewHolder(View itemView, WebScrapingRecyclerAdapter.OnNoteListener mOnNoteListener) {
            super(itemView);
            this.name = itemView.findViewById(R.id.file_name);
            this.city = itemView.findViewById(R.id.city);
            this.country = itemView.findViewById(R.id.country);
            buttonShowLG = itemView.findViewById(R.id.butt_show);
            buttonShowLG.setOnClickListener(view -> showLG());
            this.mOnNoteListener = mOnNoteListener;
            itemView.setOnClickListener(this);
        }

        private void showLG(){
            AtomicBoolean isConnected = new AtomicBoolean(false);
            LGConnectionTest.testPriorConnection(activity, isConnected);
            SharedPreferences sharedPreferences = activity.getSharedPreferences(ConstantPrefs.SHARED_PREFS.name(), MODE_PRIVATE);
            handler.postDelayed(() -> {
                if(isConnected.get()){
                    GDG gdg = (GDG) infoScrapings.get(getAdapterPosition());
                    POILocation poiLocation = new POILocation(gdg.getName(), gdg.getLongitude(), gdg.getLatitude(), 500);
                    POICamera poiCamera = new POICamera(10, 0, 1000, "absolute", 4);
                    POI poi = new POI().setPoiLocation(poiLocation).setPoiCamera(poiCamera);

                    Balloon balloon = new Balloon();
                    String description = gdg.getCity() + ", " + gdg.getCountry();
                    balloon.setPoi(poi).setDescription(description)
                            .setImageUri(null).setImagePath(null).setVideoPath(null).setDuration(15);
                    ActionController.getInstance().TourGDG(poi, balloon);
                    ActionController.getInstance().cleanFileKMLs(balloon.getDuration() * 1000);
                }
                loadConnectionStatus(sharedPreferences);
            }, 1300);
        }

        /**
         * Set the connection status on the view
         * @param sharedPreferences sharedPreferences
         */
        private void loadConnectionStatus(SharedPreferences sharedPreferences) {
            boolean isConnected = sharedPreferences.getBoolean(ConstantPrefs.IS_CONNECTED.name(), false);
            if (isConnected) {
                connectionStatus.setBackground(ContextCompat.getDrawable(activity, R.drawable.ic_status_connection_green));
            }else{
                connectionStatus.setBackground(ContextCompat.getDrawable(activity, R.drawable.ic_status_connection_red));
            }
        }

        @Override
        public void onClick(View view) {
            Log.w(TAG_DEBUG, "onClick: " + getAdapterPosition());
            mOnNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    public interface OnNoteListener{
        void onNoteClick(int position);
    }
}
