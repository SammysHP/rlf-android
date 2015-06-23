package org.milderjoghurt.rlf.android;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
        final View view = inflater.inflate(R.layout.fragment_start_page, container, false);
        setHasOptionsMenu(true);

        ((Button) view.findViewById(R.id.btnEnterSession)).setEnabled(false);
        EditText editText = (EditText) view.findViewById(R.id.SessionID);

        editText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (((EditText) getView().findViewById(R.id.SessionID)).
                        getText().toString().length() == 6){
                    ((Button) view.findViewById(R.id.btnEnterSession)).setEnabled(true);
                }else{
                    ((Button) view.findViewById(R.id.btnEnterSession)).setEnabled(false);
                }
            }
        });
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

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        final EditText editText = (EditText) getView().findViewById(R.id.SessionID);
        if (editText.getText().toString().length() != 6) {
            Toast.makeText(getActivity(), "zu kurze ID", Toast.LENGTH_SHORT).show();
            editText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    editText.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                }
            }, 500);
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
