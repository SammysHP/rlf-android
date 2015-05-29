package org.milderjoghurt.rlf.android;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.milderjoghurt.rlf.android.dummy.Session;

import java.util.ArrayList;
import java.util.List;

public class StartPageFragment extends Fragment {

    List<Session> sessions;
    private ArrayAdapter<Session> mAdapter;
    String EXTRA_MESSAGE = "bla";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_start_page, container, false);
        setHasOptionsMenu(true);
        sessions = new ArrayList<>();
        sessions.add(new Session("ABCDEF", "Software Qualitaet", true));
        sessions.add(new Session("GHIJKL", "Software Technik", true));
        sessions.add(new Session("MNOPQR", "Software Projekt 1", false));
        sessions.add(new Session("MNOPQR", "Software Projekt 2", false));
        sessions.add(new Session("MNOPQR", "Software Projekt 3", false));
        sessions.add(new Session("MNOPQR", "Software Projekt 4", false));
        sessions.add(new Session("MNOPQR", "Software Projekt 5", false));
        sessions.add(new Session("MNOPQR", "Software Projekt 6", false));

        mAdapter = new ArrayAdapter<>(view.getContext(), R.layout.fragment_startpage_entry, R.id.session_name, sessions);

        ListView mSessionView = (ListView) view.findViewById(R.id.listViewOpenSessions);
        mSessionView.setAdapter(mAdapter);
        mSessionView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getView().getContext(), StudentLiveActivity.class);

                intent.putExtra("Titel", sessions.get(position).toString());
                getView().getContext().startActivity(intent);
            }
        });
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_start_page, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.session_start:
                Intent intent = new Intent(getView().getContext(), SessionListActivity.class);
                getView().getContext().startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void createSession(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);


        startActivity(intent);
    }

    public void enterSession(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);

        EditText editText = (EditText) getView().findViewById(R.id.SessionID);
        String message = editText.getText().toString();

        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}
