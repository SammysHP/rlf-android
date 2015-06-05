package org.milderjoghurt.rlf.android;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.EditText;



public class StartPageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startpage);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_1:
                blinkGreen();
                return true;
            case KeyEvent.KEYCODE_2:
                blinkGreen();
                return true;
            case KeyEvent.KEYCODE_3:
                blinkGreen();
                return true;
            case KeyEvent.KEYCODE_4:
                blinkGreen();
                return true;
            case KeyEvent.KEYCODE_5:
                blinkGreen();
                return true;
            case KeyEvent.KEYCODE_6:
                blinkGreen();
                return true;
            case KeyEvent.KEYCODE_7:
                blinkGreen();
                return true;
            case KeyEvent.KEYCODE_8:
                blinkGreen();
                return true;
            case KeyEvent.KEYCODE_9:
                blinkGreen();
                return true;
            case KeyEvent.KEYCODE_0:
                blinkGreen();
                return true;
            case KeyEvent.KEYCODE_NUMPAD_COMMA:
                blinkRed();
                return true;
            default:
                blinkRed();
                return true;
        }
    }




    public void blinkGreen() {
        green();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                white();
            }
        }, 500);
    }

    public void blinkRed() {
        red();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                white();
            }
        }, 500);
    }


    public void green(){
        ((EditText) findViewById(R.id.SessionID)).getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
    }

    public void white(){
        ((EditText) findViewById(R.id.SessionID)).getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
    }

    public void red(){
        ((EditText) findViewById(R.id.SessionID)).getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
    }


}