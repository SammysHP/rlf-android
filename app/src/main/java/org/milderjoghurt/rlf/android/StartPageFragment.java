/*
 * Copyright (C) 2015 MilderJoghurt
 *
 * This file is part of Realtime Lecture Feedback for Android.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See COPYING, CONTRIBUTORS for more details.
 */

package org.milderjoghurt.rlf.android;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StartPageFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_startpage, container, false);
        setHasOptionsMenu(true);

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
                        getText().toString().length() == 6) {
                    ((Button) view.findViewById(R.id.btnEnterSession)).setEnabled(true);
                } else {
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
    }

    private void enterSession() {
        Intent intent = new Intent(getView().getContext(), StudentLiveActivity.class);
        final EditText editText = (EditText) getView().findViewById(R.id.SessionID);
        if (editText.getText().toString().length() != 6) {
            Toast.makeText(getActivity(), "zu kurze ID", Toast.LENGTH_SHORT).show();
            editText.getBackground().setColorFilter(getResources().getColor(R.color.material_red_a400), PorterDuff.Mode.SRC_ATOP);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    editText.setBackground(getResources().getDrawable(R.drawable.rounded_edittext_bg));
                }
            }, 500);
        } else {
            intent.putExtra(StudentLiveActivity.EXTRA_ID, editText.getText().toString());
            startActivity(intent);
        }
    }

    private void listSessions() {
        Intent intent = new Intent(getView().getContext(), SessionListActivity.class);
        getView().getContext().startActivity(intent);
    }
}
