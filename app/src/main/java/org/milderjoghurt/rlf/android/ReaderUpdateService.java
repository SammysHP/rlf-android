package org.milderjoghurt.rlf.android;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import org.milderjoghurt.rlf.android.models.Session;
import org.milderjoghurt.rlf.android.models.VoteStats;
import org.milderjoghurt.rlf.android.net.ApiConnector;
import org.milderjoghurt.rlf.android.net.ApiResponseHandler;

import java.util.ArrayList;
import java.util.List;

public class ReaderUpdateService extends Service {
    private String sessionId = "";
    private Session activeSession = null;
    static Handler ThreadHandler = new Handler();
    private Runnable updateThread = new Runnable() {

        @Override
        public void run() {
            if (updHandlers.size() < 1)
                stopSelf();
            if(sessionId.length() == 6) {
                ApiConnector.getSession(sessionId, new ApiResponseHandler<Session>() {
                    @Override
                    public void onSuccess(Session model) {
                        activeSession = model;
                    }

                    @Override
                    public void onFailure(Throwable e) {
                    }
                });
            }
            if(activeSession != null) {
                final Bundle bundle = new Bundle();
                if (!activeSession.open) {
                    bundle.putInt("Open", 0);
                    for (Handler upd : updHandlers) {
                        Message msg = new Message();
                        msg.setData(bundle);
                        upd.sendMessage(msg);
                    }
                }
                if (activeSession.open) {
                    ApiConnector.getVoteStats(sessionId, new ApiResponseHandler<List<VoteStats>>() {
                        @Override
                        public void onSuccess(List<VoteStats> model) {
                            bundle.putInt("Open", 1);
                            for (VoteStats v : model) {
                                if (v.type == VoteStats.Type.ALL)
                                    bundle.putInt("All", v.value);
                                if (v.type == VoteStats.Type.CURRENTUSERS)
                                    bundle.putInt("Count", v.value);
                                if (v.type == VoteStats.Type.REQUEST)
                                    bundle.putInt("Request", v.value);
                                if (v.type == VoteStats.Type.SPEED)
                                    bundle.putInt("Speed", v.value);
                                if (v.type == VoteStats.Type.UNDERSTANDABILITY)
                                    bundle.putInt("Understandability", v.value);
                            }
                            for (Handler upd : updHandlers) {
                                Message msg = new Message();
                                msg.setData(bundle);
                                upd.sendMessage(msg);
                            }
                        }

                        @Override
                        public void onFailure(Throwable e) {
                            Toast.makeText(getApplicationContext(), "Fehler: " + e.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            if(updHandlers.size() > 0)
                ThreadHandler.postDelayed(updateThread, 5000); // every 5 seconds
        }
    };

    private final IBinder updBinder = new ReaderBinder();
    private static ArrayList<Handler> updHandlers = new ArrayList<Handler>();
    public ReaderUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        sessionId = intent.getStringExtra("sessionId");
        return updBinder;
    }

    @Override
    public void onCreate(){
        ThreadHandler.removeCallbacks(updateThread);
        ThreadHandler.postDelayed(updateThread,1000);
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }
    public class ReaderBinder extends Binder{
        public ReaderUpdateService getService(){
            return ReaderUpdateService.this;
        }
        public void setCallback(Handler ReaderUpdateHandler){
        }
        public void addCallback(Handler ReaderUpdateHandler){
            updHandlers.add(ReaderUpdateHandler);
        }
        public void removeCallbackHandler(Handler ReaderUpdateHandler){
            updHandlers.remove(ReaderUpdateHandler);
        }
    }
}
