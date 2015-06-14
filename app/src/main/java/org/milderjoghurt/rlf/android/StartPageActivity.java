package org.milderjoghurt.rlf.android;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.EditText;

public class StartPageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startpage);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (((EditText) findViewById(R.id.SessionID)).getText().toString().length() != 6) {
            ((EditText) findViewById(R.id.SessionID)).setHintTextColor(Color.GRAY);
            ((EditText) findViewById(R.id.SessionID)).setTextColor(Color.GRAY);
        } else {
            ((EditText) findViewById(R.id.SessionID)).setHintTextColor(Color.BLACK);
            ((EditText) findViewById(R.id.SessionID)).setTextColor(Color.BLACK);
        }
        return true;
    }
}
