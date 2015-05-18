package org.milderjoghurt.rlf.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import java.util.Random;

import com.getbase.floatingactionbutton.FloatingActionButton;

public class SessionListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_list);

        final FloatingActionButton addSession = (FloatingActionButton) findViewById(R.id.session_list_fab);
        addSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random rnd = new Random();
                addSession.setColorNormal(rnd.nextInt());
            }
        });
    }
}
