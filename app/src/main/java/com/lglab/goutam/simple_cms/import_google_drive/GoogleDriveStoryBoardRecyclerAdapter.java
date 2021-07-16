package com.lglab.goutam.simple_cms.import_google_drive;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lglab.goutam.simple_cms.R;
import com.lglab.goutam.simple_cms.create.utility.model.StoryBoard;

import java.util.List;

/**
 * This is class is in charge of the recycler view with the story board imported from google drive
 */
public class GoogleDriveStoryBoardRecyclerAdapter extends RecyclerView.Adapter<GoogleDriveStoryBoardRecyclerAdapter.ViewHolder> {

    private static final String TAG_DEBUG = "GoogleDriveStoryBoardRecyclerAdapter";


    private List<StoryBoard> storyBoards;
    private OnNoteListener mOnNoteListener;

    GoogleDriveStoryBoardRecyclerAdapter(List<StoryBoard> storyBoards, OnNoteListener onNoteListener) {
        this.storyBoards = storyBoards;
        this.mOnNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public GoogleDriveStoryBoardRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_storyboard_google_drive, parent, false);
        return new ViewHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StoryBoard currentItem = storyBoards.get(position);
        Log.w(TAG_DEBUG, "onBindViewHolder called: " + currentItem.getName());
        holder.fileNameText.setText(currentItem.getName());
    }

    @Override
    public int getItemCount() {
        return storyBoards.size();
    }


    /**
     * This is the most efficient way to have the view holder and the click listener
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView fileName, fileNameText;
        OnNoteListener mOnNoteListener;

        ViewHolder(View itemView, OnNoteListener onNoteListener) {
            super(itemView);

            this.fileName = itemView.findViewById(R.id.file_name);
            this.fileNameText = itemView.findViewById(R.id.file_name_text);
            this.mOnNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.w(TAG_DEBUG, "onClick: " + getAdapterPosition());
            mOnNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    public interface OnNoteListener {
        void onNoteClick(int position);
    }

}

