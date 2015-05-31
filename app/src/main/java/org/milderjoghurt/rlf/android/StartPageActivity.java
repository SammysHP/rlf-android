package org.milderjoghurt.rlf.android;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.milderjoghurt.rlf.android.R;
import org.milderjoghurt.rlf.android.SessionListActivity;
import org.milderjoghurt.rlf.android.StudentLiveActivity;
import org.milderjoghurt.rlf.android.dummy.Session;

import java.util.ArrayList;
import java.util.List;


public class StartPageActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "";
    List<Session> sessions;
    private ArrayAdapter<Session> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startpage);

        //View view = inflater.inflate(R.layout.fragment_start_page, container, false);
        //setHasOptionsMenu(true);
        sessions = new ArrayList<>();
        sessions.add(new Session("ABCDEF", "Software Qualitaet", true));
        sessions.add(new Session("GHIJKL", "Software Technik", true));
        sessions.add(new Session("MNOPQR", "Software Projekt 1", false));
        sessions.add(new Session("MNOPQR", "Software Projekt 2", false));
        sessions.add(new Session("MNOPQR", "Software Projekt 3", false));
        sessions.add(new Session("MNOPQR", "Software Projekt 4", false));
        sessions.add(new Session("MNOPQR", "Software Projekt 5", false));
        sessions.add(new Session("MNOPQR", "Software Projekt 6", false));

        mAdapter = new ArrayAdapter<>(this.getWindow().getDecorView().getContext(),  R.layout.fragment_startpage_entry, R.id.session_name, sessions);

        ListView mSessionView = (ListView) findViewById(R.id.listViewOpenSessions);
        mSessionView.setAdapter(mAdapter);
        mSessionView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), StudentLiveActivity.class);

                intent.putExtra("Titel", sessions.get(position).toString());
                view.getContext().startActivity(intent);
            }
        });

    }


    //@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_start_page, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.session_start:
                Intent intent = new Intent(this.getWindow().getDecorView().getContext(), SessionListActivity.class);
                this.getWindow().getDecorView().getContext().startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void createSession(View view) {
        Intent intent = new Intent(this, CreateSessionActivity.class);
        startActivity(intent);
    }

    public void enterSession(View view) {
        Intent intent = new Intent(this, StudentLiveActivity.class);
        EditText editText = (EditText) this.getWindow().getDecorView().findViewById(R.id.SessionID);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

}
