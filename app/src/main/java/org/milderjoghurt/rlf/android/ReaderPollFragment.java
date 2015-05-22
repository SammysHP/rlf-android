package org.milderjoghurt.rlf.android;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ReaderPollFragment extends Fragment {
    private Bar pollbarA;
    private Bar pollbarB;
    private Bar pollbarC;
    private Bar pollbarD;

    private static class Bar {
        private final LinearLayout bar;
        private final String label;

        public Bar(final LinearLayout bar, final String label) {
            this.bar = bar;
            this.label = label;

            setCount(0, 0);
        }

        public void setCount(final int count, final int total) {
            TextView text = (TextView) bar.findViewById(R.id.label);
            text.setText(label + " (" + count + ")");

            ProgressBar progress = (ProgressBar) bar.findViewById(R.id.progressbar);
            if (total < 0 || count <= 0) {
                progress.setProgress(0);
            } else if (count > total) {
                progress.setProgress(100);
            } else {
                progress.setProgress(count * 100 / total);
            }
        }
    }

    /*
     * TODO: For demonstration
     */
    Handler demonstrationHandler = new Handler();

    /*
     * TODO: For demonstration
     */
    private Runnable demonstrationRunnable = new Runnable() {
        private final Random RANDOM = new Random();

        public int multiMax(int... n) {
            int i = 0;
            int max = n[i];

            while (++i < n.length)
                if (n[i] > max)
                    max = n[i];

            return max;
        }

        @Override
        public void run() {
            int a = RANDOM.nextInt(15);
            int b = RANDOM.nextInt(15);
            int c = RANDOM.nextInt(15);
            int d = RANDOM.nextInt(15);
            int max = multiMax(a, b, c, d);

            pollbarA.setCount(a, max);
            pollbarB.setCount(b, max);
            pollbarC.setCount(c, max);
            pollbarD.setCount(d, max);

            demonstrationHandler.postDelayed(demonstrationRunnable, 10000); // every 10 seconds
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reader_poll, container, false);

        pollbarA = new Bar((LinearLayout) view.findViewById(R.id.pollbarA), "A");
        pollbarB = new Bar((LinearLayout) view.findViewById(R.id.pollbarB), "B");
        pollbarC = new Bar((LinearLayout) view.findViewById(R.id.pollbarC), "C");
        pollbarD = new Bar((LinearLayout) view.findViewById(R.id.pollbarD), "D");

        View dismissButton = view.findViewById(R.id.poll_reset);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pollbarA.setCount(0,0);
                pollbarB.setCount(0,0);
                pollbarC.setCount(0,0);
                pollbarD.setCount(0,0);
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
