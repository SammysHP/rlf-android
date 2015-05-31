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
import android.widget.Button;
import android.widget.EditText;
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
                Intent intent = new Intent(getView().getContext(), StudentLiveActivity.class);

                EditText editText = (EditText) getView().findViewById(R.id.SessionID);
                if (editText.getText().toString().length() != 6) {
                    Toast.makeText(getActivity(), "Falsche ID", Toast.LENGTH_SHORT).show();
                } else {
                    intent.putExtra("Titel", editText.getText().toString());
                    startActivity(intent);
                }
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
}
