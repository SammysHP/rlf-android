package org.milderjoghurt.rlf.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class ReaderActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        Intent intent = getIntent();
        setTitle(intent.getStringExtra("Titel"));

        // Enable "keep screen on" programmatically (independently of portrait/landscape layout)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPagerAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);
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
                    view = getLayoutInflater().inflate(R.layout.layout_reader_livefeedback, null);
                    break;
                case 1:
                    view = new FrameLayout(container.getContext());
                    //noinspection ResourceType
                    view.setId(12345);
                    getFragmentManager().beginTransaction().add(view.getId(), new ReaderPollFragment(), null).commit();
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
                    return "Feedback";
                case 1:
                    return "Umfrage";
                default:
                    return "";
            }
        }
    }
}
