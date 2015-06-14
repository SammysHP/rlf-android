package org.milderjoghurt.rlf.android;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class StudentLiveFragment extends Fragment {

    private boolean isPressed = false;
    private Button signal_btn;
    private static final int unselectedColor = R.color.button_material_light;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_student_live, container, false);


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
                } else {
                    signal_btn.setBackgroundColor(Color.LTGRAY);
                    //signal_btn.setBackgroundResource(android.R.drawable.btn_default);
                }
            }
        });
        //return view;
    }



}
