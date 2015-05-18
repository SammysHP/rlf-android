package org.milderjoghurt.rlf.android;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateSessionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateSessionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateSessionFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_create_session, container, false);
        return view;
    }

}
