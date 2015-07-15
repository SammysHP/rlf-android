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
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import java.lang.ref.WeakReference;

public class ReaderLiveFeedbackDetailsFragment extends Fragment {

    private static class MyHandler extends Handler {
        private final WeakReference<ReaderLiveFeedbackDetailsFragment> mFragment;

        public MyHandler(ReaderLiveFeedbackDetailsFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            ReaderLiveFeedbackDetailsFragment fragment = mFragment.get();
            if (fragment != null && msg.getData().getInt("Open") != 0) {
                int avgspeed = msg.getData().getInt("Speed");
                int avgunderstandable = msg.getData().getInt("Understandability");
                fragment.updateView(avgspeed, avgunderstandable);
            }
        }
    }

    public void updateView(int speed, int understandability) {
        if (getView() == null) {
            return;
        }
        SeekBar speedbar = (SeekBar) getView().findViewById(R.id.feedback_seekbar_speed);
        speedbar.setProgress(speed);
        SeekBar understandbar = (SeekBar) getView().findViewById(R.id.feedback_seekbar_understandability);
        understandbar.setProgress(understandability);
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
        Intent serviceIntent = new Intent(getActivity(), ReaderUpdateService.class);
        serviceIntent.putExtra("sessionId", getActivity().getIntent().getStringExtra("sessionId"));
        getActivity().bindService(serviceIntent, updConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        m_Binder.removeCallbackHandler(CallbackHandler);
        getActivity().unbindService(updConnection);
        super.onPause();
    }
}
