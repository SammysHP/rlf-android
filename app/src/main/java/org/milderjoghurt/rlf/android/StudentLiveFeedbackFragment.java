package org.milderjoghurt.rlf.android;


import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.milderjoghurt.rlf.android.models.Session;
import org.milderjoghurt.rlf.android.models.Vote;
import org.milderjoghurt.rlf.android.net.ApiConnector;
import org.milderjoghurt.rlf.android.net.ApiResponseHandler;
import org.milderjoghurt.rlf.android.ui.VerticalSeekBar;


/**
 * A simple {@link Fragment} subclass.
 */


public class StudentLiveFeedbackFragment extends Fragment {

    private Session currentSession;
    public String sessionId;
    private boolean isPressed = false;
    private Button signal_btn;
    private Button feedback_btn;
    private VerticalSeekBar speedBar;
    private VerticalSeekBar understandBar;
    private static final int unselectedColor = R.color.button_material_light;


    public StudentLiveFeedbackFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        sessionId = getActivity().getIntent().getStringExtra(StudentLiveActivity.EXTRA_ID);
        ApiConnector.getSession(sessionId, new ApiResponseHandler<Session>() {
            @Override
            public void onSuccess(Session model) {
                currentSession = model;

            }

            @Override
            public void onFailure(Throwable e) {
                Toast.makeText(getActivity(), "Fehler: " + e.toString(), Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
        });

        Toast.makeText(getActivity().getApplicationContext(), sessionId, Toast.LENGTH_SHORT).show();

        return inflater.inflate(R.layout.fragment_student_live_feedback, container, false);
    }


    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);


        //Sliders

        speedBar =(VerticalSeekBar) getView().findViewById(R.id.speedBar);
        understandBar = (VerticalSeekBar) getView().findViewById(R.id.understandBar);




        //Buttons

        signal_btn = (Button) getView().findViewById(R.id.signal);
        signal_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPressed = !isPressed;


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

                if (isPressed) {

                    vote.value = 1;

                    //(getResources().getDrawable(R.drawable.roundedbutton));
                    signal_btn.setBackgroundColor((getResources().getColor(R.color.vote_button_selected)));
                    signal_btn.setText("Hand senken");
                } else {

                    vote.value = -1;
                    // signal_btn.setBackground(getResources().getDrawable(R.drawable.roundedbutton));
                    signal_btn.setBackgroundDrawable(getResources().getDrawable(
                            R.drawable.roundedbutton));
                    signal_btn.setBackgroundColor(Color.LTGRAY);
                    signal_btn.setText("Hand heben");
                    //signal_btn.setBackgroundResource(android.R.drawable.btn_default);
                }

                if(currentSession == null)
                {
                    Log.e("rlf-android", "session for anwering invalid");
                    return;
                }

                ApiConnector.createVote(currentSession, vote, ApiConnector.getOwnerId(getActivity().getApplicationContext()), new ApiResponseHandler<Vote>() {

                    @Override
                    public void onFailure(Throwable e) {
                        //Log.e("rlf-android", e.toString());
                        Toast.makeText(getActivity().getApplicationContext(), "Fehler, Auswahl wurde nicht akzeptiert!", Toast.LENGTH_SHORT).show();
                        Log.e("rlf-android", e.toString());
                    }

                    @Override
                    public void onSuccess(Vote v) {
                        Toast.makeText(getActivity().getApplicationContext(), "Feedback gesendet!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        //return view;


        feedback_btn = (Button) getView().findViewById(R.id.sendFeedback);

        feedback_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int speed = speedBar.getProgress();
                int understandabillity = understandBar.getProgress();
                Vote voteSpeed = new Vote(Vote.Type.SPEED, speed);
                Vote voteUnderstandabillity = new Vote(Vote.Type.UNDERSTANDABILITY, understandabillity);


                //(getResources().getDrawable(R.drawable.roundedbutton));
                signal_btn.setBackgroundColor((getResources().getColor(R.color.vote_button_selected)));

                if(currentSession == null)
                {
                    Log.e("rlf-android", "session for anwering invalid");
                    return;
                }

                ApiConnector.createVote(currentSession, voteSpeed, ApiConnector.getOwnerId(getActivity().getApplicationContext()), new ApiResponseHandler<Vote>() {

                    @Override
                    public void onFailure(Throwable e) {
                        //Log.e("rlf-android", e.toString());
                        Toast.makeText(getActivity().getApplicationContext(), "Fehler, Auswahl wurde nicht akzeptiert!", Toast.LENGTH_SHORT).show();
                        Log.e("rlf-android", e.toString());
                    }

                    @Override
                    public void onSuccess(Vote v) {
                        Toast.makeText(getActivity().getApplicationContext(), "Feedback gesendet!", Toast.LENGTH_SHORT).show();
                    }
                });

                ApiConnector.createVote(currentSession, voteUnderstandabillity, ApiConnector.getOwnerId(getActivity().getApplicationContext()), new ApiResponseHandler<Vote>() {

                    @Override
                    public void onFailure(Throwable e) {
                        //Log.e("rlf-android", e.toString());
                        Toast.makeText(getActivity().getApplicationContext(), "Fehler, Auswahl wurde nicht akzeptiert!", Toast.LENGTH_SHORT).show();
                        Log.e("rlf-android", e.toString());
                    }

                    @Override
                    public void onSuccess(Vote v) {
                        Toast.makeText(getActivity().getApplicationContext(), "Feedback gesendet!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


    }

}
