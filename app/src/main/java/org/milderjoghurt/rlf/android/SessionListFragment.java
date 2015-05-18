package org.milderjoghurt.rlf.android;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;

import com.getbase.floatingactionbutton.FloatingActionButton;

import org.milderjoghurt.rlf.android.dummy.SessionListAdapter;

public class SessionListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_session_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.session_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        mAdapter = new SessionListAdapter();
        mRecyclerView.setAdapter(mAdapter);

        // Floating Button Action
        final FloatingActionButton addSession = (FloatingActionButton) view.findViewById(R.id.session_list_fab);
        addSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CreateSessionActivity.class);
                view.getContext().startActivity(intent);
            }
        });

        return view;
    }
}
