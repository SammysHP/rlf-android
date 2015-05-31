package org.milderjoghurt.rlf.android;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class StudentLiveFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_student_live, container, false);


    }
    public void onActivityCreated(Bundle savedInstance) {

        super.onActivityCreated(savedInstance);

     //   Button break1_btn = (Button) getView().findViewById(R.id.break1);
        Button signal_btn = (Button) getView().findViewById(R.id.signal);


    }

}
