package org.milderjoghurt.rlf.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
//import android.view.Menu;
//import android.view.MenuItem;


public class VoteActivity extends AppCompatActivity {

    // for logging
    private static final String LOG_TAG = "Voting";

    // vote symbols
    public static final int[] VOTES = { 0, 1, 2, 3};

    // determine which button maps to which vote symbol
    private Button[] voteMapping = { null, null, null, null };

    // last vote or -1 if not available
    private int lastVoteSymbol = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        // fill vote map
        voteMapping[0] = (Button) findViewById(R.id.btnVoteA);
        voteMapping[1] = (Button) findViewById(R.id.btnVoteB);
        voteMapping[2] = (Button) findViewById(R.id.btnVoteC);
        voteMapping[3] = (Button) findViewById(R.id.btnVoteD);
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
