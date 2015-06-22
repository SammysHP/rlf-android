package org.milderjoghurt.rlf.android.net;

import android.content.Context;
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
import org.milderjoghurt.rlf.android.net.exceptions.NoSuchSessionException;

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
        static final String ANSWERS = "/answers";
        static final String RESET_ANSWERS = "/resetanswers/";

        private Constants() {
            // Do not instantiate
        }
    }

    private ApiConnector() {
        // Do not instantiate
    }

    public static String getOwnerId(final Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
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

    public static void getSessionsByOwner(final String owner, final ApiResponseHandler<Session> handler) {
        get(Constants.SESSIONS_FROM + owner, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable e) {
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

    public static void createSession(final Session session, final ApiResponseHandler<Session> handler) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            final String json = mapper.writeValueAsString(session);

            post(Constants.SESSIONS, json, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable e) {
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

    public static void updateSession(final Session session, final ApiResponseHandler<Session> handler) {
        String sessionId = session.id;
        try {
            ObjectMapper mapper = new ObjectMapper();
            final String json = mapper.writeValueAsString(session);

            put(Constants.SESSIONID + sessionId, json, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable e) {
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

    public static void deleteSession(final String sessionId, final String owner, final ApiResponseHandler<Session> handler) {
        delete(Constants.SESSIONID + sessionId + '/' + owner, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable e) {
                handler.onFailure(e);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                handler.onSuccess(null);
            }
        });
    }

    public static void getVotes(final String sessionId, final ApiResponseHandler<List<Vote>> handler) {
        get(Constants.SESSIONID + sessionId + Constants.VOTES, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable e) {
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

    public static void getAnswers(final String sessionId, final ApiResponseHandler<List<QuestionAnswer>> handler) {
        get(Constants.SESSIONID + sessionId + Constants.ANSWERS, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable e) {
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
        get(Constants.SESSIONID + sessionId + Constants.RESET_ANSWERS + owner, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable e) {
                handler.onFailure(e);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                handler.onSuccess(null);
            }
        });
    }

    public static void createAnswer(final Session session, final QuestionAnswer answer, final ApiResponseHandler<QuestionAnswer> handler) {
        String sessionId = session.id;
        try {
            ObjectMapper mapper = new ObjectMapper();
            final String json = mapper.writeValueAsString(answer);

            post(Constants.SESSIONID + sessionId + Constants.ANSWERS, json, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable e) {
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

    public static void createVote(final Session session, final Vote vote, final ApiResponseHandler<Vote> handler) {
        String sessionId = session.id;
        try {
            ObjectMapper mapper = new ObjectMapper();
            final String json = mapper.writeValueAsString(vote);

            post(Constants.SESSIONID + sessionId + Constants.VOTES, json, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable e) {
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
