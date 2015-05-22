package org.milderjoghurt.rlf.android;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ReaderLiveFeedbackDetailsFragment extends Fragment {
    /*
     * TODO: For demonstration
     */
    Handler demonstrationHandler = new Handler();

    /*
     * TODO: For demonstration
     */
    private Runnable demonstrationRunnable = new Runnable() {
        private final Random RANDOM = new Random();

        @Override
        public void run() {
            if (getView() != null) {
                // Change speed with probability of 70%
                if (RANDOM.nextInt(100) < 70) {
                    SeekBar bar = (SeekBar) getView().findViewById(R.id.feedback_seekbar_speed);
                    final int progress = Math.max(0, Math.min(255, bar.getProgress() + (RANDOM.nextInt(20) - 10)));
                    bar.setProgress(progress);
                }

                // Change understandability with probability of 70%
                if (RANDOM.nextInt(100) < 70) {
                    SeekBar bar = (SeekBar) getView().findViewById(R.id.feedback_seekbar_understandability);
                    final int progress = Math.max(0, Math.min(255, bar.getProgress() + (RANDOM.nextInt(20) - 10)));
                    bar.setProgress(progress);
                }
            }

            demonstrationHandler.postDelayed(demonstrationRunnable, 2000); // every 5 seconds
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
