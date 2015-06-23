package org.milderjoghurt.rlf.android;

//import android.content.Intent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.milderjoghurt.rlf.android.models.Session;
import org.milderjoghurt.rlf.android.models.Vote;
import org.milderjoghurt.rlf.android.net.ApiConnector;
import org.milderjoghurt.rlf.android.net.ApiResponseHandler;


public class StudentLiveActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "SESSION_ID";

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    private String sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_student_live);

        final Intent intent = getIntent();
        final Uri uri = intent.getData();
        final String idExtra = intent.getStringExtra(EXTRA_ID);

        if (uri != null) {
            // called from implicit intent, parse URI
            sessionId = uri.getLastPathSegment();
        } else if (idExtra != null) {
            // called from explicit intent with extra
            sessionId = idExtra;
        }

        if (sessionId == null) {
            // no session id found
            // report error (TODO)
            // and exit.
            Toast.makeText(StudentLiveActivity.this, "Fehler", Toast.LENGTH_SHORT).show();
            finish();
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPagerAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);

        ApiConnector.getSession(sessionId, new ApiResponseHandler<Session>() {
            @Override
            public void onFailure(Throwable e) {
                Log.e("rlf-android", e.toString());
                // session id invalid or network issue
                // report error (TODO)
                // and exit.
                Toast.makeText(StudentLiveActivity.this, "Fehler", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onSuccess(Session session) {
                setTitle(session.name);
            }
        });
    }

    private class ViewPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = null;

            switch (position) {
                case 0:
                    view = getLayoutInflater().inflate(R.layout.layout_student_live, null);
                    break;
                case 1:
                    view = new FrameLayout(container.getContext());
                    //noinspection ResourceType
                    view.setId(12345);
                    VoteFragment voteFrag = new VoteFragment();
                    voteFrag.setSessionID(sessionId); // session has to be set in any way
                    getFragmentManager().beginTransaction().add(view.getId(), voteFrag, null).commit();
            }

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(final ViewGroup container, final int position, final Object object) {
            container.removeView((View) object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.title_feedback);
                case 1:
                    return getResources().getString(R.string.title_poll);
                default:
                    return "";
            }
        }
    }
}




