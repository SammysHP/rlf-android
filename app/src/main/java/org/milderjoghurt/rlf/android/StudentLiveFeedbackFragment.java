package org.milderjoghurt.rlf.android;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.Toast;

import org.milderjoghurt.rlf.android.R;
import org.milderjoghurt.rlf.android.models.Session;
import org.milderjoghurt.rlf.android.models.Vote;
import org.milderjoghurt.rlf.android.net.ApiConnector;
import org.milderjoghurt.rlf.android.net.ApiResponseHandler;

/**
 * A simple {@link Fragment} subclass.
 */




public class StudentLiveFeedbackFragment extends Fragment {

    private Session currentSession = null;
    private String sessionId;
    private boolean isPressed = false;
    private Button signal_btn;
    private static final int unselectedColor = R.color.button_material_light;


    public StudentLiveFeedbackFragment() {
        // Required empty public constructor
    }

/*    public void setSessionID(String pSessionID) {

        ApiConnector.getSession(pSessionID, new ApiResponseHandler<Session>() {
            @Override
            public void onSuccess(Session session) {
                StudentLiveFeedbackFragment.this.currentSession = session;
            }

            @Override
            public void onFailure(Throwable e) {
                StudentLiveFeedbackFragment.this.currentSession = null; // TODO network error ..
            }
        });
    }*/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        sessionId = getActivity().getIntent().getStringExtra("SessionId");
        ApiConnector.getSession(sessionId, new ApiResponseHandler<Session>() {
            @Override
            public void onFailure(Throwable e) {
                Log.e("rlf-android", e.toString());
                // session id invalid or network issue
                // report error (TODO)
                // and exit.
                Toast.makeText(getActivity().getApplicationContext(), "Fehler", Toast.LENGTH_SHORT).show();
                //finish();
            }

            @Override
            public void onSuccess(Session session) {
                currentSession = session;
            }
        });

        return inflater.inflate(R.layout.fragment_student_live_feedback, container, false);
    }



    public void onActivityCreated(Bundle savedInstance) {
    super.onActivityCreated(savedInstance);





    //   Button break1_btn = (Button) getView().findViewById(R.id.break1)
    signal_btn = (Button) getView().findViewById(R.id.signal);

        signal_btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isPressed = !isPressed;

            Vote vote = new Vote(Vote.Type.REQUEST, 0);

            if (isPressed) {

                 vote.value = -1;

                        //(getResources().getDrawable(R.drawable.roundedbutton));
                signal_btn.setBackgroundColor((getResources().getColor(R.color.vote_button_selected)));
                signal_btn.setText("Hand senken");
            } else {

                vote.value = 0;
               // signal_btn.setBackground(getResources().getDrawable(R.drawable.roundedbutton));
                signal_btn.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.roundedbutton));
                signal_btn.setBackgroundColor(Color.LTGRAY);
                signal_btn.setText("Hand heben");
                //signal_btn.setBackgroundResource(android.R.drawable.btn_default);
            }

            ApiConnector.createVote(currentSession, vote, ApiConnector.getOwnerId(getActivity().getApplicationContext()), new ApiResponseHandler<Vote>(){

                @Override
                public void onFailure(Throwable e){
                    //Log.e("rlf-android", e.toString());
                    Toast.makeText(getActivity().getApplicationContext(), "Fehler, Auswahl wurde nicht akzeptiert!", Toast.LENGTH_SHORT).show();
                    Log.e("rlf-android", e.toString());
                }
                @Override
                public void onSuccess(Vote v){
                    Toast.makeText(getActivity().getApplicationContext(), "Feedback gesendet!", Toast.LENGTH_SHORT).show();
                }
            });

        }
    });
    //return view;
}

}
