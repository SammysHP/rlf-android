package org.milderjoghurt.rlf.android.dummy;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.milderjoghurt.rlf.android.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.text.SimpleDateFormat;

/**
 * Dummy Content Adapter for Session List
 */
public class SessionListAdapter extends RecyclerView.Adapter<SessionListAdapter.SessionViewHolder> {
    List<Session> sessions;

    public SessionListAdapter() {
        initializeData();
    }

    // Provide Dummy content
    private void initializeData() {
        sessions = new ArrayList<>();
        sessions.add(new Session("ABCDEF", "Software Qualitaet", true));
        sessions.add(new Session("GHIJKL", "Software Technik", true));
        sessions.add(new Session("MNOPQR", "Software Projekt 1", false));
        sessions.add(new Session("MNOPQR", "Software Projekt 2", false));
        sessions.add(new Session("MNOPQR", "Software Projekt 3", false));
        sessions.add(new Session("MNOPQR", "Software Projekt 4", false));
        sessions.add(new Session("MNOPQR", "Software Projekt 5", false));
        sessions.add(new Session("MNOPQR", "Software Projekt 6", false));
    }

    // Provide a reference to the views for each data item
    public static class SessionViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView sessionName;
        TextView sessionId;
        ImageView sessionOpen;
        TextView sessionDate;

        SessionViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.session_list_item);
            sessionName = (TextView) itemView.findViewById(R.id.session_name);
            sessionId = (TextView) itemView.findViewById(R.id.session_id);
            sessionOpen = (ImageView) itemView.findViewById(R.id.session_open);
            sessionDate = (TextView) itemView.findViewById(R.id.session_date);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SessionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_session_entry, viewGroup, false);
        SessionViewHolder svh = new SessionViewHolder(v);
        return svh;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return sessions.size();
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(SessionViewHolder sessionViewHolder, int i) {
        Calendar c1 = Calendar.getInstance(); // today
        Calendar c2 = Calendar.getInstance();
        c2.setTime(sessions.get(i).date); // your date
        SimpleDateFormat sdf;

        if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)) {
            sdf = new SimpleDateFormat("HH:mm");
        } else {
            sdf = new SimpleDateFormat("d. MMM");
        }

        sessionViewHolder.sessionName.setText(sessions.get(i).name);
        sessionViewHolder.sessionId.setText(sessions.get(i).id);
        sessionViewHolder.sessionDate.setText(sdf.format(sessions.get(i).date));
        if (sessions.get(i).open) {
            sessionViewHolder.sessionOpen.setImageResource(R.drawable.ic_action_play);
            sessionViewHolder.sessionOpen.setBackgroundResource(android.R.color.holo_green_light);
        } else {
            sessionViewHolder.sessionOpen.setImageResource(R.drawable.ic_action_pause);
            sessionViewHolder.sessionOpen.setBackgroundResource(android.R.color.holo_red_light);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
