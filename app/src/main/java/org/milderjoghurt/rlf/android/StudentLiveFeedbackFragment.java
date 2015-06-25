package org.milderjoghurt.rlf.android;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import org.milderjoghurt.rlf.android.R;

/**
 * A simple {@link Fragment} subclass.
 */




public class StudentLiveFeedbackFragment extends Fragment {

    private boolean isPressed = false;
    private Button signal_btn;
    private static final int unselectedColor = R.color.button_material_light;


    public StudentLiveFeedbackFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_live_feedback, container, false);
    }



    public void onActivityCreated(Bundle savedInstance) {
    super.onActivityCreated(savedInstance);

    //   Button break1_btn = (Button) getView().findViewById(R.id.break1);
    signal_btn = (Button) getView().findViewById(R.id.signal);
    signal_btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isPressed = !isPressed;

            if (isPressed) {
                signal_btn.setBackgroundColor(Color.parseColor("#FF9900"));
                signal_btn.setText("Hand senken");
            } else {
                signal_btn.setBackgroundColor(Color.LTGRAY);
                signal_btn.setText("Hand heben");
                //signal_btn.setBackgroundResource(android.R.drawable.btn_default);
            }
        }
    });
    //return view;
}

}
