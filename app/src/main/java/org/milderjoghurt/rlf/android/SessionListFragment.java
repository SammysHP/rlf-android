package org.milderjoghurt.rlf.android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.malinskiy.superrecyclerview.swipe.SparseItemRemoveAnimator;
import com.malinskiy.superrecyclerview.swipe.SwipeItemManagerInterface;

import org.milderjoghurt.rlf.android.dummy.Session;
import org.milderjoghurt.rlf.android.dummy.SessionListAdapter;
import org.milderjoghurt.rlf.android.ui.RecyclerUtils;

import java.util.ArrayList;
import java.util.List;

public class SessionListFragment extends Fragment implements OnMoreListener, SwipeRefreshLayout.OnRefreshListener {

    List<Session> sessions;
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
        mRecyclerView.addOnItemTouchListener(new RecyclerUtils.RecyclerItemClickListener(getActivity(), new RecyclerUtils.RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getActivity(), "Clicked " + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getView().getContext(), ReaderActivity.class);

                intent.putExtra("Titel", sessions.get(position).toString());
                getView().getContext().startActivity(intent);
            }
        }));

        // add refresh listener
        //mRecyclerView.setRefreshListener(this);
        //mRecyclerView.setRefreshingColorResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);
        //mRecyclerView.setupMoreListener(this, 1);
    }


    @Override
    public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
        Toast.makeText(this.getActivity(), "More", Toast.LENGTH_LONG).show();
        //mAdapter.add("More asked, more served");
    }

    @Override
    public void onRefresh() {
        Toast.makeText(this.getActivity(), "Refresh", Toast.LENGTH_LONG).show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mAdapter.insert(new Session("AAAAAA", "new", false), 0);
            }
        }, 2000);
    }
}
