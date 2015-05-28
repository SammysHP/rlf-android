package org.milderjoghurt.rlf.android;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.milderjoghurt.rlf.android.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentLiveFeedbackFragment extends Fragment {


    public StudentLiveFeedbackFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_live_feedback, container, false);
    }


}
