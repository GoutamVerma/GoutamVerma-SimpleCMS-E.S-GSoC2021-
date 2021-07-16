package com.lglab.goutam.simple_cms.help;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.lglab.goutam.simple_cms.R;
import com.lglab.goutam.simple_cms.top_bar.TobBarActivity;

public class HelpActivity extends TobBarActivity {

    private Button buttHelp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        View topBar = findViewById(R.id.top_bar);
        buttHelp = topBar.findViewById(R.id.butt_help);

        changeButtonClickableBackgroundColor();
    }

    /**
     * Change the background color and the option clickable to false of the button_connect
     */
    private void changeButtonClickableBackgroundColor() {
        changeButtonClickableBackgroundColor(getApplicationContext(), buttHelp);
    }
}
