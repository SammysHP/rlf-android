package org.milderjoghurt.rlf.android;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class CreateSessionActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
