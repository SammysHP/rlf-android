package org.milderjoghurt.rlf.android;

import android.app.Fragment;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

    /**
     * States for the feedback view.
     * <p/>
     * POSITIVE represents an all-good situation (usually green with happy smiley), NEGATIVE a bad situation (red with sad smiley) and NEUTRAL something inbetween.
     */
    public enum FeedbackState {
        POSITIVE(R.color.reader_livefeedback_positive, R.drawable.feedback_smiley_happy),
        NEUTRAL(R.color.reader_livefeedback_neutral, R.drawable.feedback_smiley_neutral),
        NEGATIVE(R.color.reader_livefeedback_negative, R.drawable.feedback_smiley_sad);

        public final int color;
        public final int icon;

        FeedbackState(final int color, final int icon) {
            this.color = color;
            this.icon = icon;
        }
    }

    public static final int FLASH_DURATION = 500; // ms

    /*
     * TODO: For demonstration
     */
    Handler demonstrationHandler = new Handler();

    /*
     * TODO: For demonstration
     */
    private Runnable demonstrationRunnable = new Runnable() {
        private final List<FeedbackState> VALUES = Collections.unmodifiableList(Arrays.asList(FeedbackState.values()));
        private final int SIZE = VALUES.size();
        private final Random RANDOM = new Random();

        @Override
        public void run() {
            // Change feedback state with probability of 80%
            if (RANDOM.nextInt(100) < 80) {
                setFeedbackState(VALUES.get(RANDOM.nextInt(SIZE)));
            }

            // Raise request with probability of 15%
            if (RANDOM.nextInt(100) < 15) {
                setRequestState(true);
            }

            // Dismiss request with probability of 60%
            if (RANDOM.nextInt(100) < 60) {
                setRequestState(false);
            }

            // Update user count with probability of 50%
            if (RANDOM.nextInt(100) < 50) {
                setUserCount(RANDOM.nextInt(15+5));
            }

            demonstrationHandler.postDelayed(demonstrationRunnable, 5000); // every 5 seconds
        }
    };

    private FeedbackState activeFeedbackState = FeedbackState.POSITIVE;
    private boolean requestActive = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reader_livefeedback, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
        demonstrationHandler.post(demonstrationRunnable); // TODO: For demonstration
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

        if (requestActive) {
            AnimationDrawable animator = new AnimationDrawable();
            animator.addFrame(new ColorDrawable(getResources().getColor(activeFeedbackState.color)), FLASH_DURATION);
            animator.addFrame(new ColorDrawable(getResources().getColor(R.color.reader_livefeedback_request)), FLASH_DURATION);
            animator.setOneShot(false);
            background.setBackground(animator);
            animator.start();
        } else {
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
}
