package org.milderjoghurt.rlf.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.malinskiy.superrecyclerview.swipe.SparseItemRemoveAnimator;
import com.malinskiy.superrecyclerview.swipe.SwipeItemManagerInterface;

import org.milderjoghurt.rlf.android.contentprovider.SessionListAdapter;
import org.milderjoghurt.rlf.android.models.Session;
import org.milderjoghurt.rlf.android.net.ApiConnector;
import org.milderjoghurt.rlf.android.net.ApiResponseHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SessionListFragment extends Fragment implements OnMoreListener, SwipeRefreshLayout.OnRefreshListener {

    List<Session> sessions = null;
    private SuperRecyclerView mRecyclerView;
    private SessionListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SparseItemRemoveAnimator mSparseAnimator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_session_list, container, false);

        initRecyclerView(view);

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

    // Provide Dummy content
    private void initializeData() {
        if(sessions == null)
            sessions = new ArrayList<>();

        // get all available data from server
        final String ownerID = ApiConnector.getOwnerId(getActivity().getApplicationContext());
        ApiConnector.getSessionsByOwner(ownerID, new ApiResponseHandler<List<Session>>() {
            @Override
            public void onSuccess(List<Session> model) {
                // refresh data
                sessions.clear();
                sessions.addAll(model);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable e) {

                if(e instanceof IOException) {
                    Toast.makeText(getActivity().getApplicationContext(), "Fehler bei der Netzwerkkommunikation!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Fehler: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                // this is just a convention for handling errors. we only want to show
                // data when query was successfull
                sessions.clear();
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    private void initRecyclerView(View view) {
        mRecyclerView = (SuperRecyclerView) view.findViewById(R.id.session_list);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        initializeData();
        mAdapter = new SessionListAdapter(sessions);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setMode(SwipeItemManagerInterface.Mode.Single);

        // add refresh listener
        mRecyclerView.setRefreshListener(this);
        mRecyclerView.setRefreshingColorResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);
        //mRecyclerView.setupMoreListener(this, 1);
    }

    @Override
    public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
        Toast.makeText(this.getActivity(), "More", Toast.LENGTH_LONG).show();
        //mAdapter.add("More asked, more served");
    }

    @Override
    public void onRefresh() {
        initializeData();
    }
}
