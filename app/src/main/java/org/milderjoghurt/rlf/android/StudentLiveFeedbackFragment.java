package org.milderjoghurt.rlf.android;


import android.app.Fragment;
import android.graphics.Color;
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
import org.milderjoghurt.rlf.android.ui.VerticalSeekBar;

import org.milderjoghurt.rlf.android.net.exceptions.SessionNotOpenException;

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
    private Timer signal_timer = new Timer();
    private Button feedback_btn;
    private VerticalSeekBar speedBar;
    private VerticalSeekBar understandBar;
    private TextView lastFeedbackView;
    private static final int unselectedColor = R.color.button_material_light;


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
            //(getResources().getDrawable(R.drawable.roundedbutton));
            signalBtn.setBackgroundColor((getResources().getColor(R.color.vote_button_selected)));
            signalBtn.setText("Hand senken");
        } else {
            // signal_btn.setBackground(getResources().getDrawable(R.drawable.roundedbutton));
            signalBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.roundedbutton));
            signalBtn.setBackgroundColor(Color.LTGRAY);
            signalBtn.setText("Hand heben");
            //signal_btn.setBackgroundResource(android.R.drawable.btn_default);
        }
    }

    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);


        //Sliders und textView

        speedBar =(VerticalSeekBar) getView().findViewById(R.id.speedBar);
        understandBar = (VerticalSeekBar) getView().findViewById(R.id.understandBar);
        lastFeedbackView = (TextView) getView().findViewById(R.id.textView2);



        //Buttons

        signal_btn = (Button) getView().findViewById(R.id.signal);
        signal_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPressed = !isPressed;

                // configure a timer which automatically deactives the "melden" feature after a given interval
                if(isPressed) {
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



               // Runnable mRunnable;
               // Handler mHandler=new Handler();

         /*       mRunnable=new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        // yourLayoutObject.setVisibility(View.INVISIBLE); //If you want just hide the View. But it will retain space occupied by the View.
                        // yourLayoutObject.setVisibility(View.GONE); //This will remove the View. and free s the space occupied by the View
                    }
                };*/




                Vote vote = new Vote(Vote.Type.REQUEST, 0);

                executeSignallingButtonUILogic(isPressed, signal_btn);
                vote.value = isPressed ? 1 : -1;


                if(currentSession == null)
                {
                    Log.e("rlf-android", "session for anwering invalid");
                    return;
                }

                ApiConnector.createVote(currentSession, vote, ApiConnector.getOwnerId(getActivity().getApplicationContext()), new ApiResponseHandler<Vote>() {

                    @Override
                    public void onFailure(Throwable e) {
                        //Log.e("rlf-android", e.toString());
                        if(e instanceof SessionNotOpenException) {
                            Toast.makeText(getActivity().getApplicationContext(), "Die Sitzung wurde bereits geschlossen!", Toast.LENGTH_LONG).show();
                            getActivity().finish();
                        }else{

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

        feedback_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String currentTimeString = new SimpleDateFormat("HH:mm:ss").format(new Date());
                lastFeedbackView.setText("Letztes Feedback:" + System.getProperty("line.separator") + currentTimeString);

                int speed = speedBar.getProgress();
                int understandabillity = understandBar.getProgress();
                Vote voteSpeed = new Vote(Vote.Type.SPEED, speed);
                Vote voteUnderstandabillity = new Vote(Vote.Type.UNDERSTANDABILITY, (understandabillity));


                if(currentSession == null)
                {
                    Log.e("rlf-android", "session for anwering invalid");
                    return;
                }

                ApiConnector.createVote(currentSession, voteSpeed, ApiConnector.getOwnerId(getActivity().getApplicationContext()), new ApiResponseHandler<Vote>() {

                    @Override
                    public void onFailure(Throwable e) {
                        //Log.e("rlf-android", e.toString());
                        if(e instanceof SessionNotOpenException) {
                            Toast.makeText(getActivity().getApplicationContext(), "Die Sitzung wurde bereits geschlossen!", Toast.LENGTH_LONG).show();
                            getActivity().finish();
                        }else {

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

                        if(e instanceof SessionNotOpenException) {
                            Toast.makeText(getActivity().getApplicationContext(), "Die Sitzung wurde bereits geschlossen!", Toast.LENGTH_LONG).show();
                            getActivity().finish();
                        }else {
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
