package com.crilu.gothandroid;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crilu.gothandroid.adapter.PairablePlayerAdapter;

import java.util.List;
import java.util.Vector;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class PairablePlayersFragment extends Fragment {

    private List<Vector<String>> mPairablePlayers;
    private TableView<Vector<String>> mPlayersTable;
    private PairablePlayerAdapter<Vector<String>> mPairablePlayersAdapter;

    public PairablePlayersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pairable_players, container, false);

        String[] tableHeaders = { getString(R.string.pairable_players_header_name),
                getString(R.string.pairable_players_header_rank),
                getString(R.string.pairable_players_header_sco),
                getString(R.string.pairable_players_header_co),
                getString(R.string.pairable_players_header_club)};
        mPlayersTable = rootView.findViewById(R.id.pairable_players_table);
        mPlayersTable.setHeaderAdapter(new SimpleTableHeaderAdapter(getContext(), tableHeaders));
        mPairablePlayersAdapter = new PairablePlayerAdapter<>(getContext(), mPairablePlayers);

        TableColumnWeightModel columnModel = new TableColumnWeightModel(5);
        columnModel.setColumnWeight(0, 2);
        mPlayersTable.setColumnModel(columnModel);

        mPlayersTable.setDataAdapter(mPairablePlayersAdapter);

        return rootView;
    }

    public void setPairablePlayers(List<Vector<String>> pairablePlayers) {
        this.mPairablePlayers = pairablePlayers;
        if (mPlayersTable != null && mPairablePlayersAdapter != null) {
            mPairablePlayersAdapter = new PairablePlayerAdapter<>(getContext(), mPairablePlayers);
            mPlayersTable.setDataAdapter(mPairablePlayersAdapter);
            mPairablePlayersAdapter.notifyDataSetChanged();
        }
    }
}
