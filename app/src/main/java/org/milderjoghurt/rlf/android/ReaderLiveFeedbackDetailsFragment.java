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
            if(msg.getData().getInt("Status") !=0) {
                int avgspeed = msg.getData().getInt("Speed");
                int avgunderstandable = msg.getData().getInt("Understandability");
                SeekBar speedbar = (SeekBar) fragment.getView().findViewById(R.id.feedback_seekbar_speed);
                speedbar.setProgress(avgspeed);
                SeekBar understandbar = (SeekBar) fragment.getView().findViewById(R.id.feedback_seekbar_understandability);
                understandbar.setProgress(avgunderstandable);
            }
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
        Intent serviceIntent = new Intent(getActivity(),ReaderUpdateService.class);
        serviceIntent.putExtra("sessionId",getActivity().getIntent().getStringExtra("sessionId"));
        getActivity().bindService(serviceIntent, updConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unbindService(updConnection);
    }
}
