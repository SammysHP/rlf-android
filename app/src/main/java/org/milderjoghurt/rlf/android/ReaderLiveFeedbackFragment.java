package org.milderjoghurt.rlf.android;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.milderjoghurt.rlf.android.models.Session;
import org.milderjoghurt.rlf.android.models.Vote;
import org.milderjoghurt.rlf.android.net.ApiConnector;
import org.milderjoghurt.rlf.android.net.ApiResponseHandler;

import java.lang.ref.WeakReference;

/**
 * Feedback indicator.
 * <p/>
 * This will show a square area which indicates the current feedback and request state. Feedback will be displayed in different colors as smileys, request state by flashing colors.
 */
public class ReaderLiveFeedbackFragment extends Fragment {

    private static class MyHandler extends Handler {
        private final WeakReference<ReaderLiveFeedbackFragment> mFragment;

        public MyHandler(ReaderLiveFeedbackFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }
        @Override
        public void handleMessage(Message msg) {
            ReaderLiveFeedbackFragment fragment = mFragment.get();
            if(fragment.activeSession.open) {
                int curStatus = msg.getData().getInt("All");
                if (curStatus > 66)
                    fragment.setFeedbackState(FeedbackState.POSITIVE);
                else if (curStatus > 33)
                    fragment.setFeedbackState(FeedbackState.NEUTRAL);
                else
                    fragment.setFeedbackState(FeedbackState.NEGATIVE);
                if (msg.getData().getInt("Request") > 0)
                    fragment.setRequestState(true);
                fragment.setUserCount(msg.getData().getInt("Count"));
            }else{
                fragment.setFeedbackState(FeedbackState.INACTIVE);
            }
            fragment.updateView();
        }
    }

    private final MyHandler CallbackHandler = new MyHandler(this);
    private static ReaderUpdateService.ReaderBinder m_Binder;
    private static ReaderUpdateService m_Service;
    private ServiceConnection updConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder binder) {
            m_Binder = (ReaderUpdateService.ReaderBinder) binder;
            m_Service = m_Binder.getService();
            m_Binder.addCallback(CallbackHandler);
        }

        public void onServiceDisconnected(ComponentName name) {
            m_Service = null;
            m_Binder = null;
        }
    };

    public static final int FLASH_DURATION = 500; // ms

    private FeedbackState activeFeedbackState = FeedbackState.INACTIVE;
    private boolean requestActive = false;
    private String sessionId;
    private Session activeSession;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_reader_livefeedback, container, false);

        View dismissButton = view.findViewById(R.id.reader_feedback_dismiss);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vote vote = new Vote(Vote.Type.REQUEST, -1);
                ApiConnector.createVote(activeSession, vote, ApiConnector.getOwnerId(view.getContext()), new ApiResponseHandler<Vote>() {
                    @Override
                    public void onSuccess(Vote model) {
                        requestActive = false;
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Toast.makeText(getActivity(), "Fehler: " + e.toString(), Toast.LENGTH_LONG).show();
                    }
                });
                updateView();
            }
        });
        final Switch OpenSwitch = (Switch) view.findViewById(R.id.swtSessionOpen);
        OpenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                if (!isChecked) {
                    setFeedbackState(FeedbackState.INACTIVE);
                } else {
                    setFeedbackState(FeedbackState.NEUTRAL);
                }
                if (activeSession.open != isChecked) {
                    activeSession.open = isChecked;
                    ApiConnector.updateSession(activeSession, ApiConnector.getOwnerId(view.getContext()), new ApiResponseHandler<Session>() {
                        @Override
                        public void onSuccess(Session model) {
                            activeSession = model;
                        }

                        @Override
                        public void onFailure(Throwable e) {
                            activeSession.open = !isChecked;
                            OpenSwitch.setChecked(!isChecked);
                        }
                    });
                }
            }
        });
        sessionId = getActivity().getIntent().getStringExtra("SessionId");
        ApiConnector.getSession(sessionId, new ApiResponseHandler<Session>() {
            @Override
            public void onSuccess(Session model) {
                activeSession = model;
                OpenSwitch.setChecked(activeSession.open);
            }

            @Override
            public void onFailure(Throwable e) {
                Toast.makeText(getActivity(), "Fehler: " + e.toString(), Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
        Intent serviceIntent = new Intent(getActivity(),ReaderUpdateService.class);
        serviceIntent.putExtra("sessionId",sessionId);
        getActivity().bindService(serviceIntent, updConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    public void onPause() {
        super.onPause();
        getActivity().unbindService(updConnection);
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
        INACTIVE(R.color.reader_livefeedback_inactiv, R.drawable.feedback_smiley_happy);

        public final int color;
        public final int icon;

        FeedbackState(final int color, final int icon) {
            this.color = color;
            this.icon = icon;
        }
    }
}
