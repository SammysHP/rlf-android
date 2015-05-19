package org.milderjoghurt.rlf.android.dummy;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.milderjoghurt.rlf.android.R;

import java.util.List;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import com.malinskiy.superrecyclerview.swipe.BaseSwipeAdapter;
import com.malinskiy.superrecyclerview.swipe.SwipeLayout;

/**
 * Dummy Content Adapter for Session List
 */
public class SessionListAdapter extends BaseSwipeAdapter<SessionListAdapter.ViewHolder> {
    List<Session> sessions;

    public SessionListAdapter(List<Session> sessions) {
        this.sessions = sessions;
    }

    // Provide a reference to the views for each data item
    public static class ViewHolder extends BaseSwipeAdapter.BaseSwipeableViewHolder {
        CardView cv;
        TextView sessionName;
        TextView sessionId;
        ImageView sessionOpen;
        TextView sessionDate;

        ViewHolder(View itemView) {
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
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_session_entry, viewGroup, false);
        ViewHolder svh = new ViewHolder(v);
        return svh;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return sessions.size();
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        super.onBindViewHolder(holder, pos);
        Calendar c1 = Calendar.getInstance(); // today
        Calendar c2 = Calendar.getInstance();
        c2.setTime(sessions.get(pos).date); // your date
        SimpleDateFormat sdf;

        if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)) {
            sdf = new SimpleDateFormat("HH:mm");
        } else {
            sdf = new SimpleDateFormat("d. MMM");
        }

        holder.sessionName.setText(sessions.get(pos).name);
        holder.sessionId.setText(sessions.get(pos).id);
        holder.sessionDate.setText(sdf.format(sessions.get(pos).date));
        if (sessions.get(pos).open) {
            holder.sessionOpen.setImageResource(R.drawable.ic_action_play);
            holder.sessionOpen.setBackgroundResource(android.R.color.holo_green_light);
        } else {
            holder.sessionOpen.setImageResource(R.drawable.ic_action_pause);
            holder.sessionOpen.setBackgroundResource(android.R.color.holo_red_light);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void insert(Session session, int position) {
        closeAllExcept(null);

        sessions.add(position, session);

        notifyItemInserted(position);
    }
}
