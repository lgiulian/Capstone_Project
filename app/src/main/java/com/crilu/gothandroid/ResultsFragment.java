package com.crilu.gothandroid;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crilu.gothandroid.adapter.TableAdapter;
import com.crilu.gothandroid.model.ResultViewModel;
import com.crilu.opengotha.model.GamesResults;

import java.util.List;
import java.util.Vector;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import timber.log.Timber;

public class ResultsFragment extends Fragment {

    private ResultViewModel mResultViewModel;
    private List<Vector<String>> mGames;
    private TableView<Vector<String>> mGamesTable;
    private TableAdapter<Vector<String>> mGamesAdapter;

    public ResultsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_results, container, false);

        mResultViewModel = ViewModelProviders.of(getActivity()).get(ResultViewModel.class);
        mGames = mResultViewModel.getGames();

        String[] tableHeaders = { getString(R.string.games_header_table),
                getString(R.string.games_header_white),
                getString(R.string.games_header_black),
                getString(R.string.games_header_hd),
                getString(R.string.games_header_result)};
        mGamesTable = rootView.findViewById(R.id.results_table);
        mGamesTable.setHeaderAdapter(new SimpleTableHeaderAdapter(getContext(), tableHeaders));
        mGamesAdapter = new TableAdapter<>(getContext(), mGames);

        TableColumnWeightModel columnModel = new TableColumnWeightModel(5);
        columnModel.setColumnWeight(1, 2);
        columnModel.setColumnWeight(2, 2);
        mGamesTable.setColumnModel(columnModel);

        mGamesTable.setDataAdapter(mGamesAdapter);
        mGamesTable.addDataClickListener(new TableClickListener());

        return rootView;
    }

    private class TableClickListener implements TableDataClickListener<Vector<String>> {
        @Override
        public void onDataClicked(int rowIndex, Vector<String> clickedData) {
            Timber.d("game %s at position %s", clickedData.get(1), rowIndex);
            // mimic clicking on result column
            mResultViewModel.getGamesResults().tblGamesMousePressed(rowIndex, GamesResults.RESULT_COL);
        }
    }

    public void setTables(List<Vector<String>> games) {
        this.mGames = games;
    }

    public void updateGames() {
        mGamesAdapter.notifyDataSetChanged();
    }
}
