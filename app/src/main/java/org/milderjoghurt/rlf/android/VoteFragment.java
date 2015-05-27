package org.milderjoghurt.rlf.android;

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



/**
 * Fragment for voting functionality
 */
public class VoteFragment extends Fragment {

    // local res / logging
    private static final String LOG_TAG = "Voting";
    private static final String SELECTED_CHOICE_PREFIX = "Auswahl:";
    private static final String MSG_VOTE_SENDED = "Auswahl gesendet";
    private static final String MSG_VOTE_BLOCKED = "Du hast eben erst abgestimmt!";

    // vote symbols
    public static final int[] VOTES = { 0, 1, 2, 3 };
    public static final String[] VOTES_LBL = { "A", "B", "C", "D" };

    // UI
    private Button[] voteMapping = { null, null, null, null }; // determine which button maps to which vote symbol
    private Button sendButton = null;
    private AnimationDrawable sendBtnUIFeedback = null;

    // model
    private static final long LEAST_WAIT_TIME = 30000; // ms
    private int lastVoteSymbol = -1; // last vote or -1 if not available
    private String lastVoteSymbolStr = VOTES_LBL[0]; // vote str
    private long lastVoteTime = 0; // timestamp - prevent clientside spamming


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vote, container, false);
    }

    /**
     * Should be called when user clicks on a vote button
     */
    public void onVoteClick(View src) {

        Button btnSource = (Button) src;
        if(btnSource == null)
            return;

        // search for possible vote source to map to a symbol
        for(int i = 0; i < voteMapping.length; ++i) {
            if(voteMapping[i] == btnSource) {

                // set model state
                setVoteSymbol(i);

                // visual feedback on ui
                src.setBackgroundColor(getResources().getColor(R.color.vote_button_selected));

                // there was (at least) one selection, so "sending" is enabled
                sendButton.setEnabled(true);

            } else {

                // no match --> set to unselected color
                voteMapping[i].setBackgroundColor(getResources().getColor(R.color.vote_button_unselected));
            }
        }

        // debug
        //Log.d(LOG_TAG, "vote " + getLastVoteSymbol() + " selected");
    }

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

        // query vote for sending to server
        boolean querySuccess = true; // TODO logic? interface?

        // handle success:
        if(querySuccess) {

            // update timestamp
            lastVoteTime = curTime;

            // give more ui feedback:
            Toast.makeText(getActivity().getApplicationContext(), MSG_VOTE_SENDED, Toast.LENGTH_SHORT).show();

            for(int i=0;i<voteMapping.length;++i) {
                voteMapping[i].setBackgroundColor(getResources().getColor(R.color.vote_button_unselected));
            }
            sendButton.setEnabled(false);
        }
    }

    private void setVoteSymbol(int index) {
        lastVoteSymbol = VOTES[index];
        lastVoteSymbolStr = VOTES_LBL[index];
    }

    public int getLastVoteSymbol() {
        return lastVoteSymbol;
    }

    public String getLastVoteSymbolStr() {
        return lastVoteSymbolStr;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {

        super.onActivityCreated(savedInstance);

        // fill vote map
        try {
            Button btnA = (Button) getView().findViewById(R.id.btnVoteA);
            Button btnB = (Button) getView().findViewById(R.id.btnVoteB);
            Button btnC = (Button) getView().findViewById(R.id.btnVoteC);
            Button btnD = (Button) getView().findViewById(R.id.btnVoteD);
            sendButton = (Button) getView().findViewById(R.id.btnVoteSend);
            sendButton.setEnabled(false); // there needs to be a selection for enabling

            View.OnClickListener voteClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VoteFragment.this.onVoteClick(view);
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
                    VoteFragment.this.onVoteSend(view);
                }
            });

            voteMapping[0] = btnA;
            voteMapping[1] = btnB;
            voteMapping[2] = btnC;
            voteMapping[3] = btnD;


            sendBtnUIFeedback = new AnimationDrawable();
            sendBtnUIFeedback.addFrame(new ColorDrawable(getResources().getColor(R.color.vote_button_selected)), 110);
            sendBtnUIFeedback.addFrame(new ColorDrawable(getResources().getColor(R.color.vote_button_unselected)), 10);
            sendBtnUIFeedback.setOneShot(true);


        } catch (Exception e) {
            Log.d(LOG_TAG, "gui setup failed");
        }
    }
}
