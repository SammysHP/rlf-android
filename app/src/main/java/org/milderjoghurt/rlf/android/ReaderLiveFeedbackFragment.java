/*
 * Copyright (C) 2015 MilderJoghurt
 *
 * This file is part of Realtime Lecture Feedback for Android.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See COPYING, CONTRIBUTORS for more details.
 */

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
import android.util.Log;
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
 * <p>
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
            if (fragment != null && fragment.activeSession != null && fragment.activeSession.open) {
                int curStatus = msg.getData().getInt("All");
                if (curStatus > 66) {
                    fragment.setFeedbackState(FeedbackState.POSITIVE);
                } else if (curStatus > 33) {
                    fragment.setFeedbackState(FeedbackState.NEUTRAL);
                } else {
                    fragment.setFeedbackState(FeedbackState.NEGATIVE);
                }
                fragment.setRequestState(msg.getData().getInt("Request") > 0);
                fragment.setRequestBreakState(msg.getData().getInt("Break"));
                fragment.setUserCount(msg.getData().getInt("Count"));
            } else {
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
    private int breakRequests = 0;
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

        // deactivation of request for a break (quite equal to behavior of "request")
        View dismissBreakButton = view.findViewById(R.id.reader_feedback_dismiss_break);
        dismissBreakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vote vote = new Vote(Vote.Type.BREAK, -1);
                ApiConnector.createVote(activeSession, vote, ApiConnector.getOwnerId(view.getContext()), new ApiResponseHandler<Vote>() {
                    @Override
                    public void onSuccess(Vote model) {
                        breakRequests = 0;
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Toast.makeText(getActivity(), "Fehler: " + e.toString(), Toast.LENGTH_LONG).show();
                        Log.e("dismissbreak", "TESTDISMISSBREAK: " + e.toString());
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

                if (activeSession != null && activeSession.open != isChecked) {
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
        Intent serviceIntent = new Intent(getActivity(), ReaderUpdateService.class);
        serviceIntent.putExtra("sessionId", sessionId);
        getActivity().bindService(serviceIntent, updConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    public void onPause() {
        m_Binder.removeCallbackHandler(CallbackHandler);
        getActivity().unbindService(updConnection);
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
     * <p>
     * Setting this to true will show a message that someone requests to say something.
     *
     * @param enabled Whether someone requests to say something
     */
    private void setRequestState(final boolean enabled) {
        requestActive = enabled;
        updateView();
    }

    /**
     * Set request state.
     * <p>
     * Setting this to true will show a message that someone requests a break.
     *
     * @param count Amount of requests for a break
     */
    private void setRequestBreakState(final int count) {
        breakRequests = count;
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
//            AnimationDrawable animator = new AnimationDrawable();
//            animator.addFrame(new ColorDrawable(getResources().getColor(R.color.reader_livefeedback_request)), FLASH_DURATION);
//            animator.addFrame(new ColorDrawable(getResources().getColor(activeFeedbackState.color)), FLASH_DURATION);
//            animator.setOneShot(false);
//            background.setBackground(animator);
//            animator.start();
            dismissButton.setVisibility(View.VISIBLE);
        } else {
            dismissButton.setVisibility(View.GONE);
//            background.setBackground(null);
//            background.setBackgroundResource(activeFeedbackState.color);
        }

        // if the amount of requests for a break is greater than a threshold
        // then raise some GUI feedback (amount might also be displayed)
        View dismissBreak = getView().findViewById(R.id.reader_feedback_dismiss_break);
        Log.d(getClass().getSimpleName(), "break count: " + breakRequests);
        if (breakRequests > 4) {
            dismissBreak.setVisibility(View.VISIBLE);

            if (dismissBreak instanceof TextView) {
                TextView breakText = (TextView) dismissBreak;
                breakText.setText("Pause gewuenscht:" + breakRequests);
            }
        } else {
            dismissBreak.setVisibility(View.GONE);
        }

        if (requestActive) {
            AnimationDrawable animator = new AnimationDrawable();
            animator.addFrame(new ColorDrawable(getResources().getColor(R.color.reader_livefeedback_request)), FLASH_DURATION);
            animator.addFrame(new ColorDrawable(getResources().getColor(activeFeedbackState.color)), FLASH_DURATION);
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

    /**
     * States for the feedback view.
     * <p>
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
