package org.milderjoghurt.rlf.android;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StartPageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_start_page, container, false);
        setHasOptionsMenu(true);

        Button btnEnter = (Button) view.findViewById(R.id.btnEnterSession);
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterSession();
            }
        });

        Button btnListSessions = (Button) view.findViewById(R.id.btnListSessions);
        btnListSessions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listSessions();
            }
        });

        EditText idInput = (EditText) view.findViewById(R.id.SessionID);
        idInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                enterSession();
                return true;
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getView().findViewById(R.id.SessionID).requestFocus();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void enterSession() {
        Intent intent = new Intent(getView().getContext(), StudentLiveActivity.class);

        EditText editText = (EditText) getView().findViewById(R.id.SessionID);
        if (editText.getText().toString().length() != 6) {
            Toast.makeText(getActivity(), "Falsche ID", Toast.LENGTH_SHORT).show();
        } else {
            intent.putExtra("Titel", editText.getText().toString());
            startActivity(intent);
        }
    }

    private void listSessions() {
        Intent intent = new Intent(getView().getContext(), SessionListActivity.class);
        getView().getContext().startActivity(intent);
    }
}
