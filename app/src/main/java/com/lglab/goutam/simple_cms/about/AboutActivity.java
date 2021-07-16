package com.lglab.goutam.simple_cms.about;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.lglab.goutam.simple_cms.R;
import com.lglab.goutam.simple_cms.top_bar.TobBarActivity;

public class AboutActivity extends TobBarActivity {

    private Button buttAbout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

         View topBar = findViewById(R.id.top_bar);
         buttAbout = topBar.findViewById(R.id.butt_about);
         changeButtonClickableBackgroundColor();

        TextView linkLinkedId= findViewById(R.id.linked_id);
        linkLinkedId.setMovementMethod(LinkMovementMethod.getInstance());

        TextView linkGithub= findViewById(R.id.github_account);
        linkGithub.setMovementMethod(LinkMovementMethod.getInstance());
    }


    /**
     * Change the background color and the option clickable to false of the button_connect
     */
    private void changeButtonClickableBackgroundColor() {
        changeButtonClickableBackgroundColor(getApplicationContext(), buttAbout);
    }

}
