package org.milderjoghurt.rlf.android;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateChangedListener;

import org.milderjoghurt.rlf.android.models.Session;
import org.milderjoghurt.rlf.android.net.ApiConnector;
import org.milderjoghurt.rlf.android.net.ApiResponseHandler;

import java.util.Date;

public class CreateSessionFragment extends Fragment {
    Date d = new Date();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_create_session, container, false);
        setHasOptionsMenu(true);
        final FloatingActionButton btnCreateSession = (FloatingActionButton) view.findViewById(R.id.session_create_fb);
        btnCreateSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatingButtonClick(v);
            }
        });

        MaterialCalendarView calendar = (MaterialCalendarView) view.findViewById(R.id.calendar);
        calendar.setFirstDayOfWeek(2);
        calendar.setSelectedDate(d);

        calendar.setOnDateChangedListener(new OnDateChangedListener() {
            @Override
            public void onDateChanged(MaterialCalendarView materialCalendarView, CalendarDay calendarDay) {
                d = calendarDay.getDate();
            }
        });

        return view;
    }

    public void FloatingButtonClick(View v) {
        EditText et = (EditText) getView().findViewById(R.id.etSessionName);

        Session session = new Session(ApiConnector.getOwnerId(getActivity().getApplicationContext()), et.getText().toString(), false, d);
        ApiConnector.createSession(session, ApiConnector.getOwnerId(getActivity().getApplicationContext()), new ApiResponseHandler<Session>() {
            @Override
            public void onSuccess(Session model) {
                Toast.makeText(getActivity(), model.name + " Erstellt", Toast.LENGTH_SHORT).show();
                NavUtils.navigateUpFromSameTask(getActivity());
            }

            @Override
            public void onFailure(Throwable e) {
                Toast.makeText(getActivity(), "Fehler: Sitzung nicht Erstellt", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_create_session, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
