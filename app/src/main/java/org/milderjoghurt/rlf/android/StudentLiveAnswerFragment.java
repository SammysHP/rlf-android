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
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.milderjoghurt.rlf.android.models.QuestionAnswer;
import org.milderjoghurt.rlf.android.models.Session;
import org.milderjoghurt.rlf.android.net.ApiConnector;
import org.milderjoghurt.rlf.android.net.ApiResponseHandler;


/**
 * Fragment for voting functionality
 */
public class StudentLiveAnswerFragment extends Fragment {

    // local res / logging
    private static final String LOG_TAG = "Voting";
    private static final String MSG_VOTE_SENDED = "Auswahl gesendet";

    private Session currentSession = null;

    private Button sendButton = null;
    private Button btnA;
    private Button btnB;
    private Button btnC;
    private Button btnD;

    public StudentLiveAnswerFragment() {

    }

    public void setSession(String sessionID) {
        ApiConnector.getSession(sessionID, new ApiResponseHandler<Session>() {
            @Override
            public void onSuccess(Session session) {
                StudentLiveAnswerFragment.this.currentSession = session;
            }

            @Override
            public void onFailure(Throwable e) {
                StudentLiveAnswerFragment.this.currentSession = null; // TODO network error ..
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_live_answer, container, false);
    }

    public void onVoteClick(View v) {
        btnA.setActivated(false);
        btnB.setActivated(false);
        btnC.setActivated(false);
        btnD.setActivated(false);
        v.setActivated(true);
        sendButton.setEnabled(true);
    }


    /**
     * Should be called when vote was chosen by user and is about to be sended to host
     */
    public void onVoteSend(View src) {
        sendButton.setEnabled(false); // disable as long as server is working on answer

        if (currentSession == null) {
            Log.e("rlf-android", "session for anwering invalid");
            return;
        }

        QuestionAnswer q;

        if (btnA.isActivated()) {
            q = new QuestionAnswer(QuestionAnswer.Answer.A);
        } else if (btnB.isActivated()) {
            q = new QuestionAnswer(QuestionAnswer.Answer.B);
        } else if (btnC.isActivated()) {
            q = new QuestionAnswer(QuestionAnswer.Answer.C);
        } else {
            q = new QuestionAnswer(QuestionAnswer.Answer.D);
        }

        ApiConnector.createAnswer(currentSession, q, ApiConnector.getOwnerId(getActivity().getApplicationContext()), new ApiResponseHandler<QuestionAnswer>() {
            @Override
            public void onFailure(Throwable e) {
                // sending answer to server was not successful, give feedback
                Toast.makeText(getActivity().getApplicationContext(), "Fehler, Auswahl wurde nicht akzeptiert!", Toast.LENGTH_SHORT).show();
                Log.e("rlf-android", e.toString());
            }

            @Override
            public void onSuccess(QuestionAnswer answer) {
                // give more ui feedback:
                Toast.makeText(getActivity().getApplicationContext(), MSG_VOTE_SENDED, Toast.LENGTH_SHORT).show();
                sendButton.setEnabled(true);
                btnA.setActivated(false);
                btnB.setActivated(false);
                btnC.setActivated(false);
                btnD.setActivated(false);
            }
        });

    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {

        super.onActivityCreated(savedInstance);

        // fill vote map
        try {
            btnA = (Button) getView().findViewById(R.id.btnVoteA);
            btnB = (Button) getView().findViewById(R.id.btnVoteB);
            btnC = (Button) getView().findViewById(R.id.btnVoteC);
            btnD = (Button) getView().findViewById(R.id.btnVoteD);
            sendButton = (Button) getView().findViewById(R.id.btnVoteSend);

            View.OnClickListener voteClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StudentLiveAnswerFragment.this.onVoteClick(view);
                }
            };

            // set listeners manually
            btnA.setOnClickListener(voteClickListener);
            btnB.setOnClickListener(voteClickListener);
            btnC.setOnClickListener(voteClickListener);
            btnD.setOnClickListener(voteClickListener);
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StudentLiveAnswerFragment.this.onVoteSend(view);
                }
            });

        } catch (Exception e) {
            Log.d(LOG_TAG, "gui setup failed in vote fragment");
        }
    }
}
