package org.milderjoghurt.rlf.android;

//import android.content.Intent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import org.milderjoghurt.rlf.android.models.Session;
import org.milderjoghurt.rlf.android.net.ApiConnector;
import org.milderjoghurt.rlf.android.net.ApiResponseHandler;


public class StudentLiveActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_student_live);

        Intent intent = getIntent();
        setTitle(intent.getStringExtra("Titel"));

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPagerAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);

        ApiConnector.getSession(intent.getStringExtra("Titel"), new ApiResponseHandler<Session>() {
            @Override
            public void onFailure(Throwable e) {
                Log.e("rlf-android", e.toString());
            }

            @Override
            public void onSuccess(Session session) {
                Log.e("rlf-android", session.name);
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
                    getFragmentManager().beginTransaction().add(view.getId(), new VoteFragment(), null).commit();
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




