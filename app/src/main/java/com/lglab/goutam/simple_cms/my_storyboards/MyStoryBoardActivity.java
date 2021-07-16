package com.lglab.goutam.simple_cms.my_storyboards;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lglab.goutam.simple_cms.R;
import com.lglab.goutam.simple_cms.create.CreateStoryBoardActivity;
import com.lglab.goutam.simple_cms.db.AppDatabase;
import com.lglab.goutam.simple_cms.db.entity.StoryBoardDB;
import com.lglab.goutam.simple_cms.top_bar.TobBarActivity;

import java.util.ArrayList;
import java.util.List;

public class MyStoryBoardActivity extends TobBarActivity implements
        StoryBoardRecyclerAdapter.OnNoteListener{

    private Button buttMyStoryBoard;

    private RecyclerView mRecyclerView;
    List<StoryBoardDB> storyBoards = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_storyboards);

        mRecyclerView = findViewById(R.id.my_recycler_view);

        View topBar = findViewById(R.id.top_bar);
        buttMyStoryBoard = topBar.findViewById(R.id.butt_my_storyboards_menu);

        changeButtonClickableBackgroundColor();
    }

    @Override
    protected void onResume() {
        loadStoryBoards();
        initRecyclerView();
        super.onResume();
    }

    /**
     * Load the data for the database
     */
    private void loadStoryBoards() {
        AppDatabase db = AppDatabase.getAppDatabase(this);
        storyBoards = db.storyBoardDao().getStoryBoards();
    }

    /**
     * Initiate the recycleview
     */
    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.Adapter mAdapter = new StoryBoardRecyclerAdapter(this, storyBoards, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Change the background color and the option clickable to false of the button_connect
     */
    private void changeButtonClickableBackgroundColor() {
        changeButtonClickableBackgroundColor(getApplicationContext(), buttMyStoryBoard);
    }

    @Override
    public void onNoteClick(int position) {
        StoryBoardDB selected = storyBoards.get(position);
        Intent intent = new Intent(getApplicationContext(), CreateStoryBoardActivity.class);
        intent.putExtra(StoryBoardConstant.STORY_BOARD_ID.name(), selected.storyBoardId);
        startActivity(intent);
    }
}
