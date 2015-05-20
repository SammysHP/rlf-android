package org.milderjoghurt.rlf.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class CreateSessionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
