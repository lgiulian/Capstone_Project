package com.crilu.gothandroid;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.crilu.gothandroid.adapter.PlayerAdapter;
import com.crilu.gothandroid.databinding.ActivityPlayersManagerBinding;
import com.crilu.gothandroid.model.PlayersManagerViewModel;
import com.crilu.opengotha.Gotha;
import com.crilu.opengotha.Player;
import com.crilu.opengotha.RatedPlayer;
import com.crilu.opengotha.RatingList;
import com.crilu.opengotha.Tournament;
import com.crilu.opengotha.model.PlayersManager;

import java.util.Arrays;

import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import timber.log.Timber;

public class PlayersManagerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener,
        Tournament.OnTournamentChangeListener, PlayersManager.OnPlayerRegistrationListener {

    private ActivityPlayersManagerBinding mBinding;
    private PlayersManagerViewModel mPlayersManagerViewModel;

    private PlayersManager mPlayersManager;
    private ArrayAdapter<RatedPlayer> mAdapter;
    private PlayerAdapter<Player> mRegisteredPlayersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players_manager);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_players_manager);

        setSupportActionBar(mBinding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        init();
        initComponents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPlayersManager != null) {
            mPlayersManager.setTournamentChangeListener(this);
            mPlayersManager.setPlayerRegistrationListener(this);
        } else {
            Timber.e("PlayersManager instance is null");
            Snackbar.make(mBinding.coordinatorLayout, getString(R.string.all_something_went_wrong_initializing_model_data), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPlayersManager != null) {
            mPlayersManager.setTournamentChangeListener(null);
            mPlayersManager.setPlayerRegistrationListener(null);
        } else {
            Timber.e("PlayersManager instance is null");
            Snackbar.make(mBinding.coordinatorLayout, getString(R.string.all_something_went_wrong_initializing_model_data), Snackbar.LENGTH_LONG).show();
        }
    }

    private void init() {
        mPlayersManagerViewModel = ViewModelProviders.of(this).get(PlayersManagerViewModel.class);
        mPlayersManager = mPlayersManagerViewModel.getPlayersManager();
    }

    private void initComponents() {
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
        mRegisteredPlayersAdapter = new PlayerAdapter<>(this, mPlayersManagerViewModel.getPlayersList());

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
        Timber.d("position " + position + " selected");
        RatedPlayer selectedPlayer = mAdapter.getItem(position);
        //int nbRounds = mPlayersManagerViewModel.getNumberOfRounds();
        boolean[] participates = new boolean[Gotha.MAX_NUMBER_OF_ROUNDS];
        Arrays.fill(participates, Boolean.TRUE);
        if (selectedPlayer != null && mPlayersManager != null) {
            mPlayersManager.register(selectedPlayer.getName(),
                    selectedPlayer.getFirstName(),
                    selectedPlayer.getCountry(),
                    selectedPlayer.getClub(),
                    selectedPlayer.getEgfPin(),
                    selectedPlayer.getFfgLicence(),
                    selectedPlayer.getFfgLicenceStatus(),
                    selectedPlayer.getAgaId(),
                    selectedPlayer.getAgaExpirationDate(),
                    selectedPlayer.getStrGrade(),
                    "FIN",
                    selectedPlayer.getStrRawRating(),
                    selectedPlayer.getStrRawRating(),
                    "0",
                    participates);
        }

        mBinding.playerName.setText("");
    }

    @Override
    public void onTournamentChange() {
        mRegisteredPlayersAdapter = new PlayerAdapter<>(this, mPlayersManagerViewModel.getPlayersList());
        mBinding.playersTable.setDataAdapter(mRegisteredPlayersAdapter);
        mBinding.playersTable.getDataAdapter().notifyDataSetChanged();
    }

    @Override
    public void onTournamentErrorMessage(String message) {
        Snackbar.make(mBinding.coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onRegistrationError(String message) {
        Snackbar.make(mBinding.coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }
}
