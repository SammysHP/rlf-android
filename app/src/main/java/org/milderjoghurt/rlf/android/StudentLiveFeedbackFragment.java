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
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.milderjoghurt.rlf.android.models.Session;
import org.milderjoghurt.rlf.android.models.Vote;
import org.milderjoghurt.rlf.android.net.ApiConnector;
import org.milderjoghurt.rlf.android.net.ApiResponseHandler;
import org.milderjoghurt.rlf.android.net.exceptions.SessionNotOpenException;
import org.milderjoghurt.rlf.android.ui.VerticalSeekBar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentLiveFeedbackFragment extends Fragment {

    private Session currentSession;
    private boolean isPressed = false;
    private Button signal_btn;
    private Button break_btn;
    private boolean breakIsPressed = false;
    private Timer signal_timer = new Timer();
    private Timer break_timer = new Timer();
    private Button feedback_btn;
    private VerticalSeekBar speedBar;
    private VerticalSeekBar understandBar;
    private TextView lastFeedbackView;


    public StudentLiveFeedbackFragment() {
        // Required empty public constructor
    }

    public void setSession(String sessionID) {
        ApiConnector.getSession(sessionID, new ApiResponseHandler<Session>() {
            @Override
            public void onSuccess(Session session) {
                StudentLiveFeedbackFragment.this.currentSession = session;
            }

            @Override
            public void onFailure(Throwable e) {
                StudentLiveFeedbackFragment.this.currentSession = null; // TODO network error ..
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_student_live_feedback, container, false);
    }

    private void executeSignallingButtonUILogic(boolean isBtnPressed, Button signalBtn) {
        if (isBtnPressed) {
            signalBtn.setActivated(true);
            signalBtn.setText("Hand senken");
        } else {
            signalBtn.setActivated(false);
            signalBtn.setText("Hand heben");
        }
    }

    private void executeBreakSignallingButtonUILogic(boolean isBtnPressed, Button breakBtn) {
        if (isBtnPressed) {
            breakBtn.setActivated(true);
        } else {
            breakBtn.setActivated(false);
        }
    }

    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);


        //Sliders und textView

        speedBar = (VerticalSeekBar) getView().findViewById(R.id.speedBar);
        understandBar = (VerticalSeekBar) getView().findViewById(R.id.understandBar);
        lastFeedbackView = (TextView) getView().findViewById(R.id.textView2);

        break_btn = (Button) getView().findViewById(R.id.btnBreak);
        break_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentSession == null) {
                    Log.e("rlf-android", "session for anwering invalid");
                    return;
                }

                breakIsPressed = !breakIsPressed;

                break_timer.cancel(); // abort previous tasks (if any)
                break_timer.purge();

                // when switching to pressed state ...
                if (breakIsPressed) {

                    final Button currentBtnTarget = break_btn;
                    final Handler hl = new Handler();

                    break_timer = new Timer();
                    // do automatically deactivate this request to prevent raising numbers of
                    // break requests due to inactive students
                    break_timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // thread-safety for gui elements ...
                            hl.post(new Runnable() {
                                @Override
                                public void run() {
                                    currentBtnTarget.callOnClick(); // execute complete logic for model consistency
                                }
                            });
                        }
                    }, ApiConnector.VALID_DURATION_OF_BREAK_SIGNALLING_MILLIS);
                }

                // send positive/negative numbers as state information to the server
                // first press will always send a positive number, so sending a number on every call should be valid
                // and result in a "0" when deactivating the request button
                Vote vote = new Vote(Vote.Type.BREAK, breakIsPressed ? 1 : -1);
                ApiConnector.createVote(currentSession, vote, ApiConnector.getOwnerId(getActivity().getApplicationContext()), new ApiResponseHandler<Vote>() {

                    @Override
                    public void onFailure(Throwable e) {
                        //Log.e("rlf-android", e.toString());
                        if (e instanceof SessionNotOpenException) {
                            Toast.makeText(getActivity().getApplicationContext(), "Die Sitzung wurde bereits geschlossen!", Toast.LENGTH_LONG).show();
                            getActivity().finish();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "Fehler, Auswahl wurde nicht akzeptiert!", Toast.LENGTH_SHORT).show();
                            Log.e("rlf-android", "########### " + e.toString());
                        }
                    }

                    @Override
                    public void onSuccess(Vote v) {
                        //Toast.makeText(getActivity().getApplicationContext(), "Feedback gesendet!", Toast.LENGTH_SHORT).show();
                    }
                });


                executeBreakSignallingButtonUILogic(breakIsPressed, break_btn);
            }
        });

        //Buttons
        signal_btn = (Button) getView().findViewById(R.id.signal);

        signal_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPressed = !isPressed;

                // configure a timer which automatically deactives the "melden" feature after a given interval
                if (isPressed) {
                    signal_timer.cancel(); // abort previous tasks (if any)
                    signal_timer.purge();
                    signal_timer = new Timer();
                    final Button currentSignalBtnTarget = signal_btn;
                    final Handler hl = new Handler();
                    signal_timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            //StudentLiveFeedbackFragment.this.executeSignallingButtonUILogic(isPressed, currentSignalBtnTarget);

                            // thread-safety for gui elements ...
                            hl.post(new Runnable() {
                                @Override
                                public void run() {
                                    currentSignalBtnTarget.callOnClick(); // execute complete logic for model consistency
                                }
                            });

                        }
                    }, ApiConnector.VALID_DURATION_OF_SIGNALLING_MILLIS);

                } else {
                    // btn was manually deactivated
                    signal_timer.cancel();
                    signal_timer.purge();
                }

                Vote vote = new Vote(Vote.Type.REQUEST, 0);

                executeSignallingButtonUILogic(isPressed, signal_btn);
                vote.value = isPressed ? 1 : -1;


                if (currentSession == null) {
                    Log.e("rlf-android", "session for anwering invalid");
                    return;
                }

                ApiConnector.createVote(currentSession, vote, ApiConnector.getOwnerId(getActivity().getApplicationContext()), new ApiResponseHandler<Vote>() {

                    @Override
                    public void onFailure(Throwable e) {
                        //Log.e("rlf-android", e.toString());
                        if (e instanceof SessionNotOpenException) {
                            Toast.makeText(getActivity().getApplicationContext(), "Die Sitzung wurde bereits geschlossen!", Toast.LENGTH_LONG).show();
                            getActivity().finish();
                        } else {

                            Toast.makeText(getActivity().getApplicationContext(), "Fehler, Auswahl wurde nicht akzeptiert!", Toast.LENGTH_SHORT).show();
                            Log.e("rlf-android", e.toString());
                        }
                    }

                    @Override
                    public void onSuccess(Vote v) {
                        //Toast.makeText(getActivity().getApplicationContext(), "Feedback gesendet!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


        feedback_btn = (Button) getView().findViewById(R.id.sendFeedback);
        feedback_btn.setText("Feedback" + System.getProperty("line.separator") + "senden");

        feedback_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String currentTimeString = new SimpleDateFormat("HH:mm:ss").format(new Date());
                lastFeedbackView.setText("Letztes Feedback:" + System.getProperty("line.separator") + currentTimeString);

                int speed = speedBar.getProgress();
                int understandabillity = understandBar.getProgress();
                Vote voteSpeed = new Vote(Vote.Type.SPEED, speed);
                Vote voteUnderstandabillity = new Vote(Vote.Type.UNDERSTANDABILITY, (understandabillity));


                if (currentSession == null) {
                    Log.e("rlf-android", "session for anwering invalid");
                    return;
                }

                ApiConnector.createVote(currentSession, voteSpeed, ApiConnector.getOwnerId(getActivity().getApplicationContext()), new ApiResponseHandler<Vote>() {

                    @Override
                    public void onFailure(Throwable e) {
                        //Log.e("rlf-android", e.toString());
                        if (e instanceof SessionNotOpenException) {
                            Toast.makeText(getActivity().getApplicationContext(), "Die Sitzung wurde bereits geschlossen!", Toast.LENGTH_LONG).show();
                            getActivity().finish();
                        } else {

                            Toast.makeText(getActivity().getApplicationContext(), "Fehler, Auswahl wurde nicht akzeptiert!", Toast.LENGTH_SHORT).show();
                            Log.e("rlf-android", e.toString());
                        }

                    }

                    @Override
                    public void onSuccess(Vote v) {
                        //Toast.makeText(getActivity().getApplicationContext(), "Feedback gesendet!", Toast.LENGTH_SHORT).show();
                    }
                });

                ApiConnector.createVote(currentSession, voteUnderstandabillity, ApiConnector.getOwnerId(getActivity().getApplicationContext()), new ApiResponseHandler<Vote>() {

                    @Override
                    public void onFailure(Throwable e) {
                        //Log.e("rlf-android", e.toString());

                        if (e instanceof SessionNotOpenException) {
                            Toast.makeText(getActivity().getApplicationContext(), "Die Sitzung wurde bereits geschlossen!", Toast.LENGTH_LONG).show();
                            getActivity().finish();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "Fehler, Auswahl wurde nicht akzeptiert!", Toast.LENGTH_SHORT).show();
                            Log.e("rlf-android", e.toString());
                        }
                    }

                    @Override
                    public void onSuccess(Vote v) {
                        //Toast.makeText(getActivity().getApplicationContext(), "Feedback gesendet!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


    }

}
