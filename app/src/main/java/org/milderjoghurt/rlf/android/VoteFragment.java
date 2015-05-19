package org.milderjoghurt.rlf.android;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log; // for demonstration/testing
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * Fragment for voting functionality
 */
public class VoteFragment extends Fragment {

    // for logging
    private static final String LOG_TAG = "Voting";
    private static final String SELECTED_CHOICE_PREFIX = "Auswahl:";

    // vote symbols
    public static final int[] VOTES = { 0, 1, 2, 3 };
    public static final String[] VOTES_LBL = { "A", "B", "C", "D" };

    // determine which button maps to which vote symbol
    private Button[] voteMapping = { null, null, null, null };

    // last vote or -1 if not available
    private int lastVoteSymbol = -1;
    private String lasteVoteSymbolStr = VOTES_LBL[0];


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {




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
            if(voteMapping[i] == btnSource) {
                setVoteSymbol(i);
            }
        }

        // present current state
        TextView textSelectedChoice = (TextView) getView().findViewById(R.id.txtVoteChoice);
        if(textSelectedChoice != null) {
            textSelectedChoice.setText(SELECTED_CHOICE_PREFIX + getLastVoteSymbolStr());
        }

        // debug
        Log.d(LOG_TAG, "vote " + getLastVoteSymbol() + " selected");
    }

    /**
     * Should be called when vote was chosen by user and is about to be sended to host
     */
    public void onVoteSend(View src) {
        Log.d(LOG_TAG, "vote " + getLastVoteSymbolStr() + " is queried for sending");
    }

    private void setVoteSymbol(int index) {
        lastVoteSymbol = VOTES[index];
        lasteVoteSymbolStr = VOTES_LBL[index];
    }

    public int getLastVoteSymbol() {
        return lastVoteSymbol;
    }

    public String getLastVoteSymbolStr() {
        return lasteVoteSymbolStr;
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
    }
}
