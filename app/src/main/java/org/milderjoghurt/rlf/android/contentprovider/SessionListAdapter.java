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

package org.milderjoghurt.rlf.android.contentprovider;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.malinskiy.superrecyclerview.swipe.BaseSwipeAdapter;

import org.milderjoghurt.rlf.android.R;
import org.milderjoghurt.rlf.android.ReaderActivity;
import org.milderjoghurt.rlf.android.models.Session;
import org.milderjoghurt.rlf.android.net.ApiConnector;
import org.milderjoghurt.rlf.android.net.ApiResponseHandler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Dummy Content Adapter for Session List
 */
public class SessionListAdapter extends BaseSwipeAdapter<SessionListAdapter.ViewHolder> {
    List<Session> sessions;

    public SessionListAdapter(List<Session> sessions) {
        this.sessions = sessions;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_session_entry, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.cv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_UP) {
                    Intent intent = new Intent(v.getContext(), ReaderActivity.class);
                    intent.putExtra("Titel", sessions.get(holder.getPosition()).name.toString());
                    intent.putExtra("SessionId", sessions.get(holder.getPosition()).id);
                    v.getContext().startActivity(intent);
                }
                return true;
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // tell deletetion to network API

                final Session toDeleteSessionObj = sessions.get(holder.getPosition());

                if (toDeleteSessionObj == null)
                    return; // nothing to do ..

                final View sourceView = v; // necessary to get context within asynch handler

                ApiConnector.deleteSession(toDeleteSessionObj, ApiConnector.getOwnerId(v.getContext()), new ApiResponseHandler<Session>() {
                    @Override
                    public void onSuccess(Session model) {
                        remove(holder.getPosition());
                        Toast.makeText(sourceView.getContext(), model.name + " entfernt", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Toast.makeText(sourceView.getContext(), "Fehler: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        return holder;
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
            holder.sessionOpen.setBackgroundResource(R.color.sessionlist_open);
        } else {
            holder.sessionOpen.setImageResource(R.drawable.ic_action_pause);
            holder.sessionOpen.setBackgroundResource(R.color.sessionlist_closed);
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

    public void remove(int position) {
        sessions.remove(position);

        closeItem(position);

        notifyItemRemoved(position);
    }

    // Provide a reference to the views for each data item
    public static class ViewHolder extends BaseSwipeAdapter.BaseSwipeableViewHolder {
        CardView cv;
        TextView sessionName;
        TextView sessionId;
        ImageView sessionOpen;
        TextView sessionDate;
        Button deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.session_list_item);
            sessionName = (TextView) itemView.findViewById(R.id.session_name);
            sessionId = (TextView) itemView.findViewById(R.id.session_id);
            sessionOpen = (ImageView) itemView.findViewById(R.id.session_open);
            sessionDate = (TextView) itemView.findViewById(R.id.session_date);
            deleteButton = (Button) itemView.findViewById(R.id.session_list_item_delete);
        }
    }
}
