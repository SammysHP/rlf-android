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
import android.widget.Toast;

import org.milderjoghurt.rlf.android.models.QuestionAnswer;
import org.milderjoghurt.rlf.android.models.Session;
import org.milderjoghurt.rlf.android.net.ApiConnector;
import org.milderjoghurt.rlf.android.net.ApiResponseHandler;

import java.util.List;

public class ReaderPollFragment extends Fragment {

    Handler demonstrationHandler = new Handler();
    private Bar pollbarA;
    private Bar pollbarB;
    private Bar pollbarC;
    private Bar pollbarD;
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
                    Toast.makeText(getActivity(), "Fehler: " + e.toString(), Toast.LENGTH_LONG).show();
                }
            });
            if (activeSession != null) {
                if (activeSession.open) {
                    ApiConnector.getAnswers(sessionId, new ApiResponseHandler<List<QuestionAnswer>>() {
                        @Override
                        public void onSuccess(List<QuestionAnswer> model) {
                            int acount = 0;
                            int bcount = 0;
                            int ccount = 0;
                            int dcount = 0;
                            for (QuestionAnswer q : model) {
                                if (q.answer == QuestionAnswer.Answer.A)
                                    acount++;
                                if (q.answer == QuestionAnswer.Answer.B)
                                    bcount++;
                                if (q.answer == QuestionAnswer.Answer.C)
                                    ccount++;
                                if (q.answer == QuestionAnswer.Answer.D)
                                    dcount++;
                            }
                            pollbarA.setCount(acount, model.size());
                            pollbarB.setCount(bcount, model.size());
                            pollbarC.setCount(ccount, model.size());
                            pollbarD.setCount(dcount, model.size());
                        }

                        @Override
                        public void onFailure(Throwable e) {
                        }
                    });
                }
            }


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
                ApiConnector.resetAnswers(sessionId, ApiConnector.getOwnerId(v.getContext()), new ApiResponseHandler<Session>() {
                    @Override
                    public void onSuccess(Session model) {
                        resetCallback();
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Toast.makeText(getActivity(), "Fehler: " + e.toString(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        return view;
    }

    public void resetCallback() {
        pollbarA.setCount(0, 0);
        pollbarB.setCount(0, 0);
        pollbarC.setCount(0, 0);
        pollbarD.setCount(0, 0);
        Toast.makeText(this.getActivity(), "Neue Fragerunde gestartet", Toast.LENGTH_LONG).show();
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
            if (total <= 0 || count <= 0) {
                progress.setProgress(0);
            } else if (count > total) {
                progress.setProgress(100);
            } else {
                progress.setProgress(count * 100 / total);
            }
        }
    }
}
