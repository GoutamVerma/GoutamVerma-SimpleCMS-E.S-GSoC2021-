package com.lglab.goutam.simple_cms.my_storyboards;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.lglab.goutam.simple_cms.R;
import com.lglab.goutam.simple_cms.db.AppDatabase;
import com.lglab.goutam.simple_cms.db.entity.StoryBoardDB;

import java.util.List;

/**
 * This class is the storyboard recycler view adapter of MyStoryBoard class
 */
public class StoryBoardRecyclerAdapter extends RecyclerView.Adapter<StoryBoardRecyclerAdapter.ViewHolder> {

    private static final String TAG_DEBUG = "StoryBoardRecyclerAdapter";


    private AppCompatActivity activity;
    private List<StoryBoardDB> storyBoardsDB;
    private StoryBoardRecyclerAdapter.OnNoteListener mOnNoteListener;

    StoryBoardRecyclerAdapter(AppCompatActivity activity, List<StoryBoardDB> storyBoardsDB, StoryBoardRecyclerAdapter.OnNoteListener onNoteListener) {
        this.activity = activity;
        this.storyBoardsDB = storyBoardsDB;
        this.mOnNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public StoryBoardRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_my_storyboard, parent, false);
        return new StoryBoardRecyclerAdapter.ViewHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryBoardRecyclerAdapter.ViewHolder holder, int position) {
        Log.w(TAG_DEBUG, "onBindViewHolder called");
        StoryBoardDB currentItem = storyBoardsDB.get(position);
        holder.fileNameText.setText(currentItem.name);
    }

    @Override
    public int getItemCount() {
        return storyBoardsDB.size();
    }


    /**
     * This is the most efficient way to have the view holder and the click listener
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView fileName, fileNameText;
        Button butt_delete;
        StoryBoardRecyclerAdapter.OnNoteListener mOnNoteListener;

        ViewHolder(View itemView, StoryBoardRecyclerAdapter.OnNoteListener onNoteListener) {
            super(itemView);

            this.fileName = itemView.findViewById(R.id.file_name);
            this.fileNameText = itemView.findViewById(R.id.file_name_text);
            butt_delete = itemView.findViewById(R.id.butt_delete);
            butt_delete.setOnClickListener((view) -> deleteStoryboard(activity));
            this.mOnNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
        }

        /**
         * Clean the actions of the recyclerview
         */
        private void deleteStoryboard(AppCompatActivity activity) {
            @SuppressLint("InflateParams") View v = activity.getLayoutInflater().inflate(R.layout.dialog_fragment, null);
            v.getBackground().setAlpha(220);
            Button ok = v.findViewById(R.id.ok);
            TextView textMessage = v.findViewById(R.id.message);
            textMessage.setText(activity.getResources().getString(R.string.alert_message_delete_storyboard));
            textMessage.setTextSize(23);
            textMessage.setGravity(View.TEXT_ALIGNMENT_CENTER);
            Button cancel = v.findViewById(R.id.cancel);
            cancel.setVisibility(View.VISIBLE);
            createAlertDialog(activity, v, ok, cancel);
        }

        /**
         * Create a alert dialog for the user
         * @param v view
         * @param ok button ok
         * @param cancel button cancel
         */
        private void createAlertDialog(AppCompatActivity activity, View v, Button ok, Button cancel) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setView(v);
            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            ok.setOnClickListener(v1 -> {
                AppDatabase db = AppDatabase.getAppDatabase(itemView.getContext());
                StoryBoardDB storyBoardDB = storyBoardsDB.get(getAdapterPosition());
                db.storyBoardDao().deleteStoryBoardDBWithJson(storyBoardDB);
                storyBoardsDB.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
                dialog.dismiss();
            });
            cancel.setOnClickListener(v1 -> dialog.dismiss());
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
