package com.crilu.gothandroid;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crilu.gothandroid.adapter.PairablePlayerAdapter;
import com.crilu.gothandroid.model.PairViewModel;

import java.util.List;
import java.util.Vector;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class PairablePlayersFragment extends Fragment {

    private PairablePlayerAdapter<Vector<String>> mPairablePlayersAdapter;

    public PairablePlayersFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pairable_players, container, false);

        if (getActivity() != null) {
            PairViewModel mPairViewModel = ViewModelProviders.of(getActivity()).get(PairViewModel.class);
            List<Vector<String>> mPairablePlayers = mPairViewModel.getPairablePlayers();

            String[] tableHeaders = {getString(R.string.pairable_players_header_name),
                    getString(R.string.pairable_players_header_rank),
                    getString(R.string.pairable_players_header_sco),
                    getString(R.string.pairable_players_header_co),
                    getString(R.string.pairable_players_header_club)};
            TableView<Vector<String>> mPlayersTable = rootView.findViewById(R.id.pairable_players_table);
            mPlayersTable.setHeaderAdapter(new SimpleTableHeaderAdapter(getContext(), tableHeaders));
            mPairablePlayersAdapter = new PairablePlayerAdapter<>(getContext(), mPairablePlayers);

            TableColumnWeightModel columnModel = new TableColumnWeightModel(5);
            columnModel.setColumnWeight(0, 2);
            mPlayersTable.setColumnModel(columnModel);

            mPlayersTable.setDataAdapter(mPairablePlayersAdapter);
        }
        return rootView;
    }

    public void updatePairablePlayers() {
        mPairablePlayersAdapter.notifyDataSetChanged();
    }
}
