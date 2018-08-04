package com.crilu.gothandroid;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crilu.gothandroid.adapter.TableAdapter;
import com.crilu.gothandroid.model.PairViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.TableDataLongClickListener;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import timber.log.Timber;

public class TablesFragment extends Fragment {

    private PairViewModel mPairViewModel;
    private List<Vector<String>> mTables;
    private TableAdapter<Vector<String>> mTablesAdapter;

    public TablesFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tables, container, false);

        if (getActivity() != null) {
            mPairViewModel = ViewModelProviders.of(getActivity()).get(PairViewModel.class);
            mTables = mPairViewModel.getTables();

            String[] tableHeaders = {getString(R.string.tables_header_table),
                    getString(R.string.tables_header_white),
                    getString(R.string.tables_header_black),
                    getString(R.string.tables_header_hd)};
            TableView<Vector<String>> mTablesTable = rootView.findViewById(R.id.tables_table);
            mTablesTable.setHeaderAdapter(new SimpleTableHeaderAdapter(getContext(), tableHeaders));
            mTablesAdapter = new TableAdapter<>(getContext(), mTables);

            TableColumnWeightModel columnModel = new TableColumnWeightModel(4);
            columnModel.setColumnWeight(1, 2);
            columnModel.setColumnWeight(2, 2);
            mTablesTable.setColumnModel(columnModel);

            mTablesTable.setDataAdapter(mTablesAdapter);
            mTablesTable.addDataLongClickListener(new TableLongClickListener());
        }
        return rootView;
    }

    private class TableLongClickListener implements TableDataLongClickListener<Vector<String>> {
        @Override
        public boolean onDataLongClicked(int rowIndex, Vector<String> clickedTable) {
            // unpaired selected game
            Timber.d("unpaired game %s at position %s", clickedTable.get(1), rowIndex);
            ArrayList<Integer> selectedTables = new ArrayList<>();
            int selectedTable = Integer.parseInt(clickedTable.get(0));
            selectedTables.add(selectedTable);
            mPairViewModel.getGamesPair().unpair(selectedTables);
            return true;
        }
    }

    public void updateTables() {
        mTablesAdapter.notifyDataSetChanged();
    }
}
