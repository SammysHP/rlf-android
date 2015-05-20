package org.milderjoghurt.rlf.android;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reader_poll, container, false);

        pollbarA = new Bar((LinearLayout) view.findViewById(R.id.pollbarA), "A");
        pollbarB = new Bar((LinearLayout) view.findViewById(R.id.pollbarB), "B");
        pollbarC = new Bar((LinearLayout) view.findViewById(R.id.pollbarC), "C");
        pollbarD = new Bar((LinearLayout) view.findViewById(R.id.pollbarD), "D");

        return view;
    }
}
