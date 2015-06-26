package org.milderjoghurt.rlf.android;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import org.milderjoghurt.rlf.android.models.Session;
import org.milderjoghurt.rlf.android.models.VoteStats;
import org.milderjoghurt.rlf.android.net.ApiConnector;
import org.milderjoghurt.rlf.android.net.ApiResponseHandler;

import java.util.List;

public class ReaderLiveFeedbackDetailsFragment extends Fragment {
    /*
     * TODO: For demonstration
     */
    Handler demonstrationHandler = new Handler();

    /*
     * TODO: For demonstration
     */

    double avgspeed = 0;
    double avgunderstandable = 0;
    private String sessionId;
    private Session activeSession = null;
    private Runnable demonstrationRunnable = new Runnable() {

        @Override
        public void run() {
            sessionId = getActivity().getIntent().getStringExtra("SessionId");
            ApiConnector.getSession(sessionId, new ApiResponseHandler<Session>() {
                @Override
                public void onSuccess(Session model) {
                    activeSession = model;
                }

                @Override
                public void onFailure(Throwable e) {

                }
            });
            if (getView() != null && activeSession != null) {
                if (activeSession.open) {
                    ApiConnector.getVoteStats(sessionId, new ApiResponseHandler<List<VoteStats>>() {
                        @Override
                        public void onSuccess(List<VoteStats> model) {
                            for (VoteStats v : model) {
                                if (v.type == VoteStats.Type.SPEED)
                                    avgspeed = v.value;
                                if (v.type == VoteStats.Type.UNDERSTANDABILITY)
                                    avgunderstandable = v.value;
                            }
                        }

                        @Override
                        public void onFailure(Throwable e) {

                        }
                    });
                }
                SeekBar speedbar = (SeekBar) getView().findViewById(R.id.feedback_seekbar_speed);
                speedbar.setProgress((int) avgspeed);
                SeekBar understandbar = (SeekBar) getView().findViewById(R.id.feedback_seekbar_understandability);
                understandbar.setProgress((int) avgunderstandable);
            }

            demonstrationHandler.postDelayed(demonstrationRunnable, 5000); // every 5 seconds
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reader_livefeedback_details, container, false);

        view.findViewById(R.id.feedback_seekbar_speed).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        view.findViewById(R.id.feedback_seekbar_understandability).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        demonstrationHandler.post(demonstrationRunnable); // TODO: For demonstration
    }

    @Override
    public void onPause() {
        super.onPause();
        demonstrationHandler.removeCallbacks(demonstrationRunnable); // TODO: For demonstration
    }

}
