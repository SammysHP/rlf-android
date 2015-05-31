package org.milderjoghurt.rlf.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Activity for votings
 */
public class VoteActivity extends AppCompatActivity {

    private static final String ACTIONBAR_TITLE_LABEL = "Titel"; // could be in a central "constants class" ..

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        // title for actionbar
        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(ACTIONBAR_TITLE_LABEL))
            setTitle(intent.getStringExtra(ACTIONBAR_TITLE_LABEL));
    }
}
