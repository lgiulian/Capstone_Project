package com.crilu.gothandroid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crilu.gothandroid.adapter.TableAdapter;

import java.util.List;
import java.util.Vector;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class TablesFragment extends Fragment {

    private List<Vector<String>> mTables;
    private TableView<Vector<String>> mTablesTable;
    private TableAdapter<Vector<String>> mTablesAdapter;

    public TablesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tables, container, false);

        String[] tableHeaders = { getString(R.string.tables_header_table),
                getString(R.string.tables_header_white),
                getString(R.string.tables_header_black),
                getString(R.string.tables_header_hd)};
        mTablesTable = rootView.findViewById(R.id.tables_table);
        mTablesTable.setHeaderAdapter(new SimpleTableHeaderAdapter(getContext(), tableHeaders));
        mTablesAdapter = new TableAdapter<>(getContext(), mTables);

        TableColumnWeightModel columnModel = new TableColumnWeightModel(4);
        columnModel.setColumnWeight(1, 2);
        columnModel.setColumnWeight(2, 2);
        mTablesTable.setColumnModel(columnModel);

        mTablesTable.setDataAdapter(mTablesAdapter);

        return rootView;
    }

    public void setTables(List<Vector<String>> tables) {
        this.mTables = tables;
        if (mTablesTable != null && mTablesAdapter != null) {
            mTablesAdapter = new TableAdapter<>(getContext(), mTables);
            mTablesTable.setDataAdapter(mTablesAdapter);
            mTablesAdapter.notifyDataSetChanged();
        }
    }

}
