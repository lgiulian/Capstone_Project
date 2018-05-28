package com.crilu.gothandroid;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.crilu.gothandroid.adapter.PlayerTableAdapter;
import com.crilu.gothandroid.databinding.ActivityPlayersManagerBinding;
import com.crilu.opengotha.RatedPlayer;
import com.crilu.opengotha.RatingList;

import java.util.ArrayList;
import java.util.List;

import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;

public class PlayersManagerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    private static final String TAG = PlayersManagerActivity.class.getSimpleName();

    private ActivityPlayersManagerBinding mBinding;

    private ArrayAdapter<RatedPlayer> mAdapter;
    private PlayerTableAdapter<RatedPlayer> mRegisteredPlayersAdapter;
    private List<RatedPlayer> mRegisteredPlayers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players_manager);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_players_manager);

        setSupportActionBar(mBinding.toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RatingList ratingList = GothandroidApplication.getRatingList();
        mAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, ratingList.getALRatedPlayers());
        mBinding.playerName.setAdapter(mAdapter);
        mBinding.playerName.setOnItemClickListener(this);

        String[] tableHeaders = { getString(R.string.players_manager_header_last_name),
                getString(R.string.players_manager_header_first_name),
                getString(R.string.players_manager_header_country),
                getString(R.string.players_manager_header_club),
                getString(R.string.players_manager_header_rank),
                getString(R.string.players_manager_header_rating),
                getString(R.string.players_manager_header_grade)};
        mBinding.playersTable.setHeaderAdapter(new SimpleTableHeaderAdapter(this, tableHeaders));
        mRegisteredPlayers = new ArrayList<>();
        mRegisteredPlayersAdapter = new PlayerTableAdapter<RatedPlayer>(this, mRegisteredPlayers);

        TableColumnWeightModel columnModel = new TableColumnWeightModel(7);
        columnModel.setColumnWeight(0, 2);
        columnModel.setColumnWeight(1, 2);
        mBinding.playersTable.setColumnModel(columnModel);

        mBinding.playersTable.setDataAdapter(mRegisteredPlayersAdapter);

        //final int rowColorEven = ContextCompat.getColor(this, R.color.colorAccent);
        //final int rowColorOdd = ContextCompat.getColor(this, R.color.colorPrimary);
        //mBinding.playersTable.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(rowColorEven, rowColorOdd));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "position " + position + " selected");
        RatedPlayer selectedPlayer = mAdapter.getItem(position);
        mBinding.playersTable.getDataAdapter().getData().add(selectedPlayer);
        mBinding.playersTable.getDataAdapter().notifyDataSetChanged();
        mBinding.playerName.setText("");
    }
}
