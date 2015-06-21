package org.milderjoghurt.rlf.android.net;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.milderjoghurt.rlf.android.dummy.Session;

public class ApiConnector {
    private static final class Constants {
        static final String BASE_URL = "http://krul.finf.uni-hannover.de/todo/"; // TODO
        static final String SESSIONS = "sessions";
        static final String SESSION = "sessions/";
        static final String SESSIONS_FROM = "sessions/from/";
        static final String VOTES = "/votes";
        static final String ANSWERS = "/answers";
        static final String RESET_ANSWERS = "/resetanswers/";
        static final String VOTE = "votes";
        static final String ANSWER = "answers";

        private Constants() {
            // Do not instantiate
        }
    }

    private ApiConnector() {
        // Do not instantiate
    }

    private static String getOwnerId(final Context context) {
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

    private static void put(final String url, final RequestParams params, final AsyncHttpResponseHandler responseHandler) {
        client.put(getAbsoluteUrl(url), params, responseHandler);
    }

    private static void delete(final String url, final AsyncHttpResponseHandler responseHandler) {
        client.delete(getAbsoluteUrl(url), responseHandler);
    }

    private static String getAbsoluteUrl(final String relativeUrl) {
        return Constants.BASE_URL + relativeUrl;
    }

    public static void getSessions(final AsyncHttpResponseHandler handler) {
        get(Constants.SESSIONS, null, handler);
    }

    public static void getSessionsByOwner(final String owner, final AsyncHttpResponseHandler handler) {
        get(Constants.SESSIONS_FROM + owner, null, handler);
    }

    public static void getSession(final String sessionId, final ApiResponseHandler<Session> handler) {
        get(Constants.SESSION + sessionId, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable e) {
                handler.onFailure(e);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                final Session session = new Session("foo", "bar", true); // TODO
                handler.onSuccess(session);
            }
        });
    }

    public static void createSession(final Session session, final AsyncHttpResponseHandler handler) {
        // TODO: transform session into POST params
        final RequestParams params = new RequestParams();
        params.put("key", "value");
        params.put("more", "data");

        post(Constants.SESSIONS, params, handler);
    }

    public static void updateSession(final Session session, final AsyncHttpResponseHandler handler) {
        // TODO: get ID from session object
        final String sessionId = "todo";

        // TODO: transform session into POST params
        final RequestParams params = new RequestParams();
        params.put("key", "value");
        params.put("more", "data");

        put(Constants.SESSION + sessionId, params, handler);
    }

    public static void deleteSession(final String sessionId, final String owner, final AsyncHttpResponseHandler handler) {
        delete(Constants.SESSION + sessionId + '/' + owner, handler);
    }

    public static void getVotes(final String sessionId, final AsyncHttpResponseHandler handler) {
        get(Constants.SESSION + sessionId + Constants.VOTES, null, handler);
    }

    public static void getAnswers(final String sessionId, final AsyncHttpResponseHandler handler) {
        get(Constants.SESSION + sessionId + Constants.ANSWERS, null, handler);
    }

    public static void resetAnswers(final String sessionId, final String owner, final AsyncHttpResponseHandler handler) {
        get(Constants.SESSION + sessionId + Constants.RESET_ANSWERS + owner, null, handler);
    }

    public static void createAnswer(final String sessionId, final AsyncHttpResponseHandler handler) {
        // TODO: transform (new parameter!) answer to POST params
        final RequestParams params = new RequestParams();
        params.put("key", "value");
        params.put("more", "data");

        post(Constants.ANSWER + sessionId, null, handler);
    }

    public static void createVote(final String sessionId, final AsyncHttpResponseHandler handler) {
        // TODO: transform (new parameter!) answer to POST params
        final RequestParams params = new RequestParams();
        params.put("key", "value");
        params.put("more", "data");

        post(Constants.VOTE + sessionId, null, handler);
    }
}
