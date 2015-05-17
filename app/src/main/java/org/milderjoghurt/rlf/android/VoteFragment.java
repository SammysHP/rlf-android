package org.milderjoghurt.rlf.android;

import android.app.Activity;
import android.net.Uri;
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

    // auto-generated
    private OnFragmentInteractionListener mListener;

    public VoteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vote, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    // FIXME auto-generated
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        // fill vote map
        voteMapping[0] = (Button) getView().findViewById(R.id.btnVoteA);
        voteMapping[1] = (Button) getView().findViewById(R.id.btnVoteB);
        voteMapping[2] = (Button) getView().findViewById(R.id.btnVoteC);
        voteMapping[3] = (Button) getView().findViewById(R.id.btnVoteD);
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     * FIXME auto-generated
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        // --> auto-generated
        public void onFragmentInteraction(Uri uri);
    }

}
