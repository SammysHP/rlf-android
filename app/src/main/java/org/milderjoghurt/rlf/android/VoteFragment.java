package org.milderjoghurt.rlf.android;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log; // for demonstration/testing
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * Fragment for voting functionality
 */
public class VoteFragment extends Fragment {

    // for logging
    private static final String LOG_TAG = "Voting";

    // vote symbols
    public static final int[] VOTES = { 0, 1, 2, 3};

    // determine which button maps to which vote symbol
    private Button[] voteMapping = { null, null, null, null };

    // last vote or -1 if not available
    private int lastVoteSymbol = -1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // fill vote map
        try {
            Button btnA = (Button) getView().findViewById(R.id.btnVoteA);
            Button btnB = (Button) getView().findViewById(R.id.btnVoteB);
            Button btnC = (Button) getView().findViewById(R.id.btnVoteC);
            Button btnD = (Button) getView().findViewById(R.id.btnVoteD);
            Button btnSend = (Button) getView().findViewById(R.id.btnVoteSend);

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
            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VoteFragment.this.onVoteSend(view);
                }
            });

            voteMapping[0] = btnA;
            voteMapping[1] = btnB;
            voteMapping[2] = btnC;
            voteMapping[3] = btnD;
        } catch (Exception e) {
            Log.d(LOG_TAG, "gui setup failed");
        }


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
        for(int i=0;i<voteMapping.length;++i) {
            if(voteMapping[i] == btnSource)
                setVoteSymbol(VOTES[i]);
        }

        Log.d(LOG_TAG, "vote " + getLastVoteSymbol() + " selected");
    }

    /**
     * Should be called when vote was chosen by user and is about to be sended to host
     */
    public void onVoteSend(View src) {
        Log.d(LOG_TAG, "vote is queried for sending");
    }

    private void setVoteSymbol(int i) {
        lastVoteSymbol = i;
    }

    public int getLastVoteSymbol() {
        return lastVoteSymbol;
    }
}
