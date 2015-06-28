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

    // vote symbols
    public static final String[] VOTES_LBL = { "A", "B", "C", "D" };
    public static final String VOTE_NONE = "NONE";

    // UI
    private Button[] voteMapping = { null, null, null, null }; // determine which button maps to which vote symbol
    private Button sendButton = null;
    private AnimationDrawable sendBtnUIFeedback = null;
    private static final int unselectedColor = R.color.button_material_light;

    // model
    private boolean[] selectionStates = {false, false, false, false };
    private static final long LEAST_WAIT_TIME = 300; // ms
    private long lastVoteTime = 0; // timestamp --> prevent clientside spamming
    private Session currentSession = null;

    private QuestionAnswer q;
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

         if (btnA.isSelected()){
            btnA.setActivated(false);
            btnA.setSelected(false);
            btnA.setBackgroundColor(unselectedColor);
        }if (btnB.isSelected()){
            btnA.setActivated(false);
            btnB.setSelected(false);
            btnA.setBackgroundColor(unselectedColor);
        }if (btnC.isSelected()){
            btnC.setActivated(false);
            btnC.setSelected(false);
            btnA.setBackgroundColor(unselectedColor);
        }if (btnD.isSelected()){
            btnD.setActivated(false);
            btnD.setSelected(false);
            btnA.setBackgroundColor(unselectedColor);
        }
        v.setSelected(true);
        v.setActivated(true);
        v.setBackgroundColor(Color.LTGRAY);
        sendButton.setEnabled(true);
    }



    /**
     * Should be called when user clicks on a vote button
     */  //testweise umschreiben
    /*public void onVoteClick(View src) {

        Button btnSource = (Button) src;
        if(btnSource == null)
            return;

        // search for possible vote source to map to a symbol
        for(int i = 0; i < voteMapping.length; ++i) {
            if(voteMapping[i] == btnSource) {

                // set model state
                selectionStates[i] = !selectionStates[i];
            }
        }*/

        // ui feedback
       // updateUI();

        // TODO:
        // the model state might also be sended here, but this might contradict
        // the "send info vs. revert choice" idea issued in gitlab

        // debug
        //Log.d(LOG_TAG, "voted " + getLastVoteSymbolStr());
    //}

    /**
     * Should be called when vote was chosen by user and is about to be sended to host
     */
    public void onVoteSend(View src) {

        // ui feedback
        sendButton.setBackground(sendBtnUIFeedback);
        sendBtnUIFeedback.start(); // FIXME: sometimes it seems not to run ..

        // prevent spamming
        final long curTime = System.currentTimeMillis();
        if(curTime - lastVoteTime < LEAST_WAIT_TIME) {

            // abort because min. idle time is not passed
            Toast.makeText(getActivity().getApplicationContext(), MSG_VOTE_BLOCKED, Toast.LENGTH_SHORT).show();
            return;
        }
//
//        // query vote for sending to server
//        boolean querySuccess = true; // TODO logic? interface?
//
//        // handle success:
//        if(querySuccess) {
//
//            // update timestamp
//            lastVoteTime = curTime;
//
//            // debug
//            Log.d(LOG_TAG, "voted " + getLastVoteSymbolStr());
//
//            // give more ui feedback:
//            Toast.makeText(getActivity().getApplicationContext(), MSG_VOTE_SENDED, Toast.LENGTH_SHORT).show();
//
//            // reset model
//            for(int i=0;i<selectionStates.length;++i) {
//                selectionStates[i] = false;
//            }
//
//            updateUI();
//        }


        sendButton.setEnabled(false); // disable as long as server is working on answer


        if(currentSession == null)
        {
            Log.e("rlf-android", "session for anwering invalid");
            return;
        }

        //
        if (btnA.isActivated()){
            q.answer = QuestionAnswer.Answer.A;
        }else if(btnB.isActivated()){
            q.answer = QuestionAnswer.Answer.B;
        }else if(btnC.isActivated()){
            q.answer = QuestionAnswer.Answer.C;
        }else if(btnD.isActivated()){
            q.answer = QuestionAnswer.Answer.D;
        }
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

                // previous code:

                // update timestamp
                lastVoteTime = curTime;

                // debug
                //  Log.d(LOG_TAG, "voted " + getLastVoteSymbolStr());

                // give more ui feedback:
                Toast.makeText(getActivity().getApplicationContext(), MSG_VOTE_SENDED, Toast.LENGTH_SHORT).show();

                // reset model
//                for(int i=0;i<selectionStates.length;++i) {
//                    selectionStates[i] = false;
                }

               // updateUI();

        });

    }

   /* private boolean isSelected(int index) {
        return (index >= 0 && index < selectionStates.length) ? selectionStates[index] : false;
    }*/

/*    private boolean isAtLeastOneSelected() {
        boolean result = false;
        for(boolean b : selectionStates)
            result |= b;
        return result;
    }*/

    /**
     * Updates UI state, especially element colors
     */
/*    private void updateUI() {
        for(int i = 0; i < voteMapping.length; ++i) {
            final int btnColor = selectionStates[i] ? getResources().getColor(R.color.vote_button_selected) : getResources().getColor(unselectedColor);
            voteMapping[i].setBackgroundColor(btnColor);
        }

        // there was (at least) one selection, so "sending" is enabled
        sendButton.setEnabled(isAtLeastOneSelected());
    }*/

    /**
     * Returns the current model state. Can be "VOTE_NONE" if no selection was done
     * @return Selection choices as string or VOTE_NONE
     */
/*    public String getLastVoteSymbolStr() {

        // TODO: details about how to communicate with server necessary

        if(!isAtLeastOneSelected())
            return VOTE_NONE;

        String result = "";
        for(int i=0;i<selectionStates.length;++i)
            if(isSelected(i))
                result += VOTES_LBL[i];

        return result;
    }*/

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




         /*   sendBtnUIFeedback = new AnimationDrawable();
            sendBtnUIFeedback.addFrame(new ColorDrawable(getResources().getColor(R.color.vote_button_selected)), 110);
            sendBtnUIFeedback.addFrame(new ColorDrawable(getResources().getColor(unselectedColor)), 10); // back to unselected
            sendBtnUIFeedback.setOneShot(true);*/

            //updateUI();

        } catch (Exception e) {
            Log.d(LOG_TAG, "gui setup failed in vote fragment");
        }
    }
}
