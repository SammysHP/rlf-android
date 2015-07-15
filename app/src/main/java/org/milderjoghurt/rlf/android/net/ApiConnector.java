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

package org.milderjoghurt.rlf.android.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.milderjoghurt.rlf.android.models.QuestionAnswer;
import org.milderjoghurt.rlf.android.models.Session;
import org.milderjoghurt.rlf.android.models.Vote;
import org.milderjoghurt.rlf.android.models.VoteStats;
import org.milderjoghurt.rlf.android.net.exceptions.BadRequestException;
import org.milderjoghurt.rlf.android.net.exceptions.NoSuchSessionException;
import org.milderjoghurt.rlf.android.net.exceptions.PermissionDeniedException;
import org.milderjoghurt.rlf.android.net.exceptions.SessionNotOpenException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

public class ApiConnector {
    private static final class Constants {
        static final String BASE_URL = "http://krul.finf.uni-hannover.de:9000";
        static final String SESSIONS = "/sessions";
        static final String SESSIONID = "/sessions/";
        static final String SESSIONS_FROM = "/sessions/from/";
        static final String VOTES = "/votes";
        static final String VOTESTATS = "/votestats";
        static final String ANSWERS = "/answers";
        static final String RESET_ANSWERS = "/resetanswers/";

        private Constants() {
            // Do not instantiate
        }
    }

    // ueber diese zeit lang ist eine "meldung" (hand heben im feedback) auf dem server gueltig
    public static final int VALID_DURATION_OF_SIGNALLING_MILLIS = 30 * 1000;
    public static final int VALID_DURATION_OF_BREAK_SIGNALLING_MILLIS = 5 * 60 * 1000;// 5 minuten

    private ApiConnector() {
        // Do not instantiate
    }

    public static String getOwnerId(final Context context) {
        final SharedPreferences sharedPrefs = context.getSharedPreferences("default", Context.MODE_PRIVATE);

        if (sharedPrefs.contains("owner_id")) {
            return sharedPrefs.getString("owner_id", "");
        }

        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final String deviceId = tm.getDeviceId();

        final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        final WifiManager m_wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        final String wlanMac = m_wm.getConnectionInfo().getMacAddress();

        final String ownerId = deviceId + '-' + androidId + '-' + wlanMac;

        final SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("owner_id", ownerId);
        editor.commit();

        return ownerId;
    }

    private static AsyncHttpClient client = new AsyncHttpClient();

    private static void get(final String url, final RequestParams params, final AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    private static void post(final String url, final RequestParams params, final AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static void post(final String url, final String json, final AsyncHttpResponseHandler responseHandler) {
        try {
            final StringEntity entity = new StringEntity(json);
            client.post(null, getAbsoluteUrl(url), entity, "application/json", responseHandler);
        } catch (UnsupportedEncodingException e) {
            // ignore this shit
        }
    }

    private static void put(final String url, final RequestParams params, final AsyncHttpResponseHandler responseHandler) {
        client.put(getAbsoluteUrl(url), params, responseHandler);
    }

    private static void put(final String url, final String json, final AsyncHttpResponseHandler responseHandler) {
        try {
            final StringEntity entity = new StringEntity(json);
            client.put(null, getAbsoluteUrl(url), entity, "application/json", responseHandler);
        } catch (UnsupportedEncodingException e) {
            // ignore this shit
        }
    }

    private static void delete(final String url, final AsyncHttpResponseHandler responseHandler) {
        client.delete(getAbsoluteUrl(url), responseHandler);
    }

    private static String getAbsoluteUrl(final String relativeUrl) {
        return Constants.BASE_URL + relativeUrl;
    }

    @Deprecated
    public static void getSessions(final ApiResponseHandler<List<Session>> handler) {
        get(Constants.SESSIONS, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable e) {
                handler.onFailure(e);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    final List<Session> sessionList = Arrays.asList(mapper.readValue(responseString, Session[].class));
                    handler.onSuccess(sessionList);
                } catch (IOException e) {
                    handler.onFailure(e);
                }
            }
        });
    }

    public static void getSessionsByOwner(final String owner, final ApiResponseHandler<List<Session>> handler) {
        get(Constants.SESSIONS_FROM + owner, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable e) {
                handler.onFailure(e);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    final List<Session> sessionList = Arrays.asList(mapper.readValue(responseString, Session[].class));
                    handler.onSuccess(sessionList);
                } catch (IOException e) {
                    handler.onFailure(e);
                }
            }
        });
    }

    public static void getSession(final String sessionId, final ApiResponseHandler<Session> handler) {
        get(Constants.SESSIONID + sessionId, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable e) {
                if (statusCode == 404) {
                    handler.onFailure(new NoSuchSessionException(sessionId));
                    return;
                }

                handler.onFailure(e);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    final Session session = mapper.readValue(responseString, Session.class);
                    handler.onSuccess(session);
                } catch (IOException e) {
                    handler.onFailure(e);
                }
            }
        });
    }

    public static void createSession(final Session session, final String owner, final ApiResponseHandler<Session> handler) {
        session.owner = owner;
        try {
            ObjectMapper mapper = new ObjectMapper();
            final String json = mapper.writeValueAsString(session);

            post(Constants.SESSIONS, json, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable e) {
                    if (statusCode == 400) {
                        handler.onFailure(new BadRequestException(responseString));
                        return;
                    }

                    handler.onFailure(e);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        final Session session = mapper.readValue(responseString, Session.class);
                        handler.onSuccess(session);
                    } catch (IOException e) {
                        handler.onFailure(e);
                    }
                }
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public static void updateSession(final Session session, final String owner, final ApiResponseHandler<Session> handler) {
        session.owner = owner;
        try {
            ObjectMapper mapper = new ObjectMapper();
            final String json = mapper.writeValueAsString(session);

            put(Constants.SESSIONID + session.id, json, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable e) {
                    if (statusCode == 401) {
                        handler.onFailure(new PermissionDeniedException(responseString));
                        return;
                    } else if (statusCode == 404) {
                        handler.onFailure(new NoSuchSessionException(session.id));
                        return;
                    }

                    handler.onFailure(e);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        final Session session = mapper.readValue(responseString, Session.class);
                        handler.onSuccess(session);
                    } catch (IOException e) {
                        handler.onFailure(e);
                    }
                }
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public static void deleteSession(final Session session, final String owner, final ApiResponseHandler<Session> handler) {
        delete(Constants.SESSIONID + session.id + '/' + owner, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable e) {
                if (statusCode == 401) {
                    handler.onFailure(new PermissionDeniedException(responseString));
                    return;
                } else if (statusCode == 404) {
                    handler.onFailure(new NoSuchSessionException(session.id));
                    return;
                }

                handler.onFailure(e);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                handler.onSuccess(session);
            }
        });
    }

    public static void getVotes(final String sessionId, final ApiResponseHandler<List<Vote>> handler) {
        get(Constants.SESSIONID + sessionId + Constants.VOTES, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable e) {
                if (statusCode == 404) {
                    handler.onFailure(new NoSuchSessionException(sessionId));
                    return;
                }

                handler.onFailure(e);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    final List<Vote> voteList = Arrays.asList(mapper.readValue(responseString, Vote[].class));
                    handler.onSuccess(voteList);
                } catch (IOException e) {
                    handler.onFailure(e);
                }
            }
        });
    }

    public static void getVoteStats(final String sessionId, final ApiResponseHandler<List<VoteStats>> handler) {
        get(Constants.SESSIONID + sessionId + Constants.VOTESTATS, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable e) {
                if (statusCode == 404) {
                    handler.onFailure(new NoSuchSessionException(sessionId));
                    return;
                }

                handler.onFailure(e);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    final List<VoteStats> vsList = Arrays.asList(mapper.readValue(responseString, VoteStats[].class));
                    handler.onSuccess(vsList);
                } catch (IOException e) {
                    handler.onFailure(e);
                }
            }
        });
    }

    public static void getAnswers(final String sessionId, final ApiResponseHandler<List<QuestionAnswer>> handler) {
        get(Constants.SESSIONID + sessionId + Constants.ANSWERS, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable e) {
                if (statusCode == 404) {
                    handler.onFailure(new NoSuchSessionException(sessionId));
                    return;
                }

                handler.onFailure(e);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    final List<QuestionAnswer> answerList = Arrays.asList(mapper.readValue(responseString, QuestionAnswer[].class));
                    handler.onSuccess(answerList);
                } catch (IOException e) {
                    handler.onFailure(e);
                }
            }
        });
    }

    public static void resetAnswers(final String sessionId, final String owner, final ApiResponseHandler<Session> handler) {
        post(Constants.SESSIONID + sessionId + Constants.RESET_ANSWERS + owner, "[]", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable e) {
                if (statusCode == 404) {
                    handler.onFailure(new NoSuchSessionException(sessionId));
                    return;
                }

                if (statusCode == 401) {
                    handler.onFailure(new PermissionDeniedException(responseString));
                    return;
                }

                handler.onFailure(e);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                handler.onSuccess(null);
            }
        });
    }

    public static void createAnswer(final Session session, final QuestionAnswer answer, final String owner, final ApiResponseHandler<QuestionAnswer> handler) {
        answer.owner = owner;
        try {
            ObjectMapper mapper = new ObjectMapper();
            final String json = mapper.writeValueAsString(answer);

            post(Constants.SESSIONID + session.id + Constants.ANSWERS, json, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable e) {
                    if (statusCode == 404) {
                        handler.onFailure(new NoSuchSessionException(session.id));
                        return;
                    }

                    if (statusCode == 403) {
                        handler.onFailure(new SessionNotOpenException(session.id));
                        return;
                    }

                    if (statusCode == 400) {
                        handler.onFailure(new BadRequestException(responseString));
                        return;
                    }

                    handler.onFailure(e);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        final QuestionAnswer answer = mapper.readValue(responseString, QuestionAnswer.class);
                        handler.onSuccess(answer);
                    } catch (IOException e) {
                        handler.onFailure(e);
                    }
                }
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public static void createVote(final Session session, final Vote vote, final String owner, final ApiResponseHandler<Vote> handler) {
        vote.owner = owner;
        try {
            ObjectMapper mapper = new ObjectMapper();
            final String json = mapper.writeValueAsString(vote);

            post(Constants.SESSIONID + session.id + Constants.VOTES, json, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable e) {
                    if (statusCode == 404) {
                        handler.onFailure(new NoSuchSessionException(session.id));
                        return;
                    }

                    if (statusCode == 403) {
                        handler.onFailure(new SessionNotOpenException(session.id));
                        return;
                    }

                    if (statusCode == 400) {
                        handler.onFailure(new BadRequestException(responseString));
                        return;
                    }

                    handler.onFailure(e);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        final Vote vote = mapper.readValue(responseString, Vote.class);
                        handler.onSuccess(vote);
                    } catch (IOException e) {
                        handler.onFailure(e);
                    }
                }
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
