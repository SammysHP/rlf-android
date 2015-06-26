package org.milderjoghurt.rlf.android;

import android.app.Fragment;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.milderjoghurt.rlf.android.models.Session;
import org.milderjoghurt.rlf.android.models.VoteStats;
import org.milderjoghurt.rlf.android.net.ApiConnector;
import org.milderjoghurt.rlf.android.net.ApiResponseHandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Feedback indicator.
 * <p/>
 * This will show a square area which indicates the current feedback and request state. Feedback will be displayed in different colors as smileys, request state by flashing colors.
 */
public class ReaderLiveFeedbackFragment extends Fragment {

    public static final int FLASH_DURATION = 500; // ms
    /*
     * TODO: For demonstration
     */
    Handler demonstrationHandler = new Handler();
    private FeedbackState activeFeedbackState = FeedbackState.INACTIVE;
    private boolean requestActive = false;
    private boolean isOpen = false;
    private String sessionId;
    /*
     * TODO: For demonstration
     */
    private Runnable demonstrationRunnable = new Runnable() {
        private final List<FeedbackState> VALUES = Collections.unmodifiableList(Arrays.asList(FeedbackState.values()));
        private final int SIZE = VALUES.size();
        private final Random RANDOM = new Random();

        double sumspeed = 0;
        double sumunderstandable = 0;

        double avgspeed = 0;
        double avgunderstandable = 0;

        @Override
        public void run() {
            if (isOpen) {
                ApiConnector.getVoteStats(sessionId, new ApiResponseHandler<List<VoteStats>>() {
                    @Override
                    public void onSuccess(List<VoteStats> model) {
                        for (VoteStats v : model) {
                            if (v.type == VoteStats.Type.SPEED)
                                sumspeed += v.value;
                            if (v.type == VoteStats.Type.UNDERSTANDABILITY)
                                sumunderstandable += v.value;
                        }
                        avgspeed = sumspeed / model.size();
                        avgunderstandable = sumunderstandable / model.size();
                        setUserCount(model.size());

                    }

                    @Override
                    public void onFailure(Throwable e) {

                    }
                });
                if (avgspeed > 40 && avgspeed < 60)
                    setFeedbackState(FeedbackState.INACTIVE.POSITIVE);
                else if (avgspeed > 20 && avgspeed < 80)
                    setFeedbackState(FeedbackState.INACTIVE.NEUTRAL);
                else
                    setFeedbackState(FeedbackState.INACTIVE.NEGATIVE);
                updateView();
                demonstrationHandler.postDelayed(demonstrationRunnable, 5000); // every 5 seconds
            }
        }
    };
    private Session activeSession;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_reader_livefeedback, container, false);

        View dismissButton = view.findViewById(R.id.reader_feedback_dismiss);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestActive = false;
                updateView();
            }
        });
        Switch OpenSwitch = (Switch) view.findViewById(R.id.swtSessionOpen);
        OpenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isOpen = isChecked;
                if (isOpen && !activeSession.open) {
                    activeSession.open = true;
                    ApiConnector.updateSession(activeSession, ApiConnector.getOwnerId(view.getContext()), new ApiResponseHandler<Session>() {
                        @Override
                        public void onSuccess(Session model) {

                        }

                        @Override
                        public void onFailure(Throwable e) {

                        }
                    });
                }
                if (!isOpen) {
                    setFeedbackState(FeedbackState.INACTIVE);
                    if (activeSession.open) {
                        activeSession.open = false;
                        ApiConnector.updateSession(activeSession, ApiConnector.getOwnerId(view.getContext()), new ApiResponseHandler<Session>() {
                            @Override
                            public void onSuccess(Session model) {

                            }

                            @Override
                            public void onFailure(Throwable e) {

                            }
                        });
                    }
                }
            }
        });
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
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
        demonstrationHandler.post(demonstrationRunnable);// TODO: For demonstration
    }

    @Override
    public void onPause() {
        super.onPause();
        demonstrationHandler.removeCallbacks(demonstrationRunnable); // TODO: For demonstration
    }

    /**
     * Set feedback state and update view.
     *
     * @param state The new feedback state
     */
    private void setFeedbackState(final FeedbackState state) {
        activeFeedbackState = state;
        updateView();
    }

    /**
     * Set request state.
     * <p/>
     * Setting this to true will show a message that someone requests to say something.
     *
     * @param enabled Whether someone requests to say something
     */
    private void setRequestState(final boolean enabled) {
        requestActive = enabled;
        updateView();
    }

    /**
     * Update the view with current feedback and request states.
     */
    private void updateView() {
        if (getView() == null) {
            return;
        }

        View background = getView(); // R.id.reader_feedback_background
        ImageView icon = (ImageView) getView().findViewById(R.id.reader_feedback_icon);
        View dismissButton = getView().findViewById(R.id.reader_feedback_dismiss);

        if (requestActive) {
            AnimationDrawable animator = new AnimationDrawable();
            animator.addFrame(new ColorDrawable(getResources().getColor(R.color.reader_livefeedback_request)), FLASH_DURATION);
            animator.addFrame(new ColorDrawable(getResources().getColor(activeFeedbackState.color)), FLASH_DURATION);
            animator.setOneShot(false);
            background.setBackground(animator);
            animator.start();
            dismissButton.setVisibility(View.VISIBLE);
        } else {
            dismissButton.setVisibility(View.GONE);
            background.setBackground(null);
            background.setBackgroundResource(activeFeedbackState.color);
        }

        icon.setImageResource(activeFeedbackState.icon);
    }

    /**
     * Update the number of active users.
     *
     * @param count Number of active users used for the calculation
     */
    private void setUserCount(final int count) {
        if (getView() == null) {
            return;
        }

        TextView v = (TextView) getView().findViewById(R.id.reader_feedback_usercount);
        v.setText(Integer.toString(count));
    }

    /**
     * States for the feedback view.
     * <p/>
     * POSITIVE represents an all-good situation (usually green with happy smiley), NEGATIVE a bad situation (red with sad smiley) and NEUTRAL something inbetween.
     */
    public enum FeedbackState {
        POSITIVE(R.color.reader_livefeedback_positive, R.drawable.feedback_smiley_happy),
        NEUTRAL(R.color.reader_livefeedback_neutral, R.drawable.feedback_smiley_neutral),
        NEGATIVE(R.color.reader_livefeedback_negative, R.drawable.feedback_smiley_sad),
        INACTIVE(R.color.reader_livefeedback_inactiv, R.drawable.feedback_smiley_neutral);

        public final int color;
        public final int icon;

        FeedbackState(final int color, final int icon) {
            this.color = color;
            this.icon = icon;
        }
    }
}
