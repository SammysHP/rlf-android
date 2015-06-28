package org.milderjoghurt.rlf.android;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log; // for demonstration/testing
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
    private static final String MSG_VOTE_BLOCKED = "Du hast eben erst abgestimmt!";


    // UI
    private Button sendButton = null;
    private static final int unselectedColor = R.color.button_material_light;

    // model
    private boolean[] selectionStates = {false, false, false, false };
    private static final long LEAST_WAIT_TIME = 300; // ms
    private long lastVoteTime = 0; // timestamp --> prevent clientside spamming
    private Session currentSession = null;

    //private QuestionAnswer q;
    private Button btnA;
    private Button btnB;
    private Button btnC;
    private Button btnD;;

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

    public void onVoteClick(View v){


      btnA.setBackgroundColor(unselectedColor);
      btnA.setPressed(false);


      btnB.setPressed(false);
      btnB.setBackgroundColor(unselectedColor);

      btnC.setPressed(false);
      btnC.setBackgroundColor(unselectedColor);

      btnD.setPressed(false);
      btnD.setBackgroundColor(unselectedColor);

      v.setPressed(true);
      v.setBackgroundColor((getResources().getColor(R.color.vote_button_selected)));
      sendButton.setEnabled(true);

/*        if (btnA.isPressed()){
            q.answer = QuestionAnswer.Answer.A;
        }else if(btnB.isPressed()){
            q.answer = QuestionAnswer.Answer.B;
        }else if(btnC.isPressed()){
            q.answer = QuestionAnswer.Answer.C;
        }else if(btnD.isPressed()){
            q.answer = QuestionAnswer.Answer.D;
        }else {*/

 //       }
    //  Toast.makeText(getActivity().getApplicationContext(), "pressed"+ q.answer, Toast.LENGTH_SHORT).show();

    }

    public void onVoteSend(View src) {

        // ui feedback
        sendButton.setBackgroundColor(unselectedColor);
        //sendBtnUIFeedback.start(); // FIXME: sometimes it seems not to run ..

        // prevent spamming
        final long curTime = System.currentTimeMillis();
        if(curTime - lastVoteTime < LEAST_WAIT_TIME) {

            // abort because min. idle time is not passed
            Toast.makeText(getActivity().getApplicationContext(), MSG_VOTE_BLOCKED, Toast.LENGTH_SHORT).show();
            return;
        }

        if(currentSession == null)
        {
            Log.e("rlf-android", "session for anwering invalid");
            return;
        }

        QuestionAnswer q = new QuestionAnswer(QuestionAnswer.Answer.A);

        if (btnA.isPressed()){
            q.answer= QuestionAnswer.Answer.A;
        }else if(btnB.isPressed()){
            q.answer= QuestionAnswer.Answer.B;
        }else if(btnC.isPressed()){
            q.answer= QuestionAnswer.Answer.C;
        }else {
            q.answer= QuestionAnswer.Answer.D;
        }

        //QuestionAnswer q = new QuestionAnswer(QuestionAnswer.Answer.D);
        //Toast.makeText(getActivity().getApplicationContext(), "Answer" + q.answer, Toast.LENGTH_SHORT).show();
        //
        ApiConnector.createAnswer(currentSession, q, ApiConnector.getOwnerId(getActivity().getApplicationContext()), new ApiResponseHandler<QuestionAnswer>() {
            @Override
            public void onFailure(Throwable e) {
                // sending answer to server was not successful, give feedback
                Toast.makeText(getActivity().getApplicationContext(), "Fehler, Auswahl wurde nicht akzeptiert!", Toast.LENGTH_SHORT).show();
                Log.e("rlf-android", e.toString());
            }

            @Override
            public void onSuccess(QuestionAnswer answer) {
                // debug
                Log.d("rlf-android", "auswahl " + answer.toString() + " gesendet");

                lastVoteTime = curTime;
                Toast.makeText(getActivity().getApplicationContext(), "Abgestimmt", Toast.LENGTH_SHORT).show();
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
