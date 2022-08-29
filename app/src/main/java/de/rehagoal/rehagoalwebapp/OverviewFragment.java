package de.rehagoal.rehagoalwebapp;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.wearable.view.WearableRecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.rehagoal.rehagoalwebapp.R;

import de.rehagoal.rehagoalwebapp.model.API.ListEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Creates a UI View for entries <workflow>
 */

public class OverviewFragment extends Fragment {

    /**
     * Default onCreate method, calls only super to create Fragment
     *
     * @param savedInstanceState could contain an saved state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Layout inflater method to create a view and bind it to a given listAdapter
     *
     * @param inflater           contains the layout assets
     * @param container          contains the view group, but could be empty / null
     * @param savedInstanceState could contain an saved state
     * @return a fully inflated recyclerView with a custom
     * OverviewListAdapter containing ListEntries
     */
    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_overview, container, false);
        WearableRecyclerView mWearableRecyclerView = (WearableRecyclerView) view.findViewById(R.id.rvOverview);

        List<ListEntry> list = new ArrayList<>();
        if (getArguments().getSerializable("list") instanceof ListEntry[]) {
            ListEntry[] entries = (ListEntry[]) getArguments().getSerializable("list");
            if (entries != null) {
                Collections.addAll(list, entries);
            }
        }

        OverviewListAdapter mOverviewListAdapter = new OverviewListAdapter(getContext(), list);
        mWearableRecyclerView.setAdapter(mOverviewListAdapter);
        mWearableRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        return view;
    }
}
