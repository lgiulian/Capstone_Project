package com.crilu.gothandroid;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.crilu.gothandroid.databinding.ActivityPairBinding;
import com.crilu.gothandroid.model.PairViewModel;
import com.crilu.opengotha.Gotha;
import com.crilu.opengotha.TournamentInterface;
import com.crilu.opengotha.model.GamesPair;

import java.util.Arrays;

import timber.log.Timber;

public class PairActivity extends AppCompatActivity implements GamesPair.OnPairListener {

    private ActivityPairBinding mBinding;
    private GamesPair mGamesPair;
    private PairablePlayersFragment mPairablePlayersFragment;
    private TablesFragment mTablesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_pair);

        setSupportActionBar(mBinding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        init();

        if(savedInstanceState == null) {
            setupFragments();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mGamesPair.setPairListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGamesPair.setPairListener(null);
    }

    private void init() {
        PairViewModel mPairViewModel = ViewModelProviders.of(this).get(PairViewModel.class);
        mGamesPair = mPairViewModel.getGamesPair();

        TournamentInterface tournament = GothandroidApplication.getGothaModelInstance().getTournament();
        int rounds = Gotha.MAX_NUMBER_OF_ROUNDS;
        if (tournament != null) {
            rounds = tournament.getTournamentParameterSet().getGeneralParameterSet().getNumberOfRounds();
        }
        ArrayAdapter<String> spinnerRoundsArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Arrays.copyOfRange(getResources().getStringArray(R.array.round_number_array), 0, rounds));
        mBinding.form.pairRoundNoSpinner.setAdapter(spinnerRoundsArrayAdapter);

        mBinding.form.pairRoundNoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String roundSelectedStr = mBinding.form.pairRoundNoSpinner.getItemAtPosition(position).toString();
                Timber.d("selected round %s", roundSelectedStr);
                mGamesPair.setSpnRoundNumber(Integer.valueOf(roundSelectedStr));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

    }

    private void setupFragments() {
        mPairablePlayersFragment = new PairablePlayersFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .add(R.id.pairable_players_container, mPairablePlayersFragment)
                .commit();

        mTablesFragment = new TablesFragment();

        fragmentManager.beginTransaction()
                .add(R.id.tables_container, mTablesFragment)
                .commit();
    }

    public void onClickOk(View view) {
        finish();
    }

    public void onClickPair(View view) {
        mGamesPair.pair(false, true);
    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onTableGamesUpdated() {
        mPairablePlayersFragment.updatePairablePlayers();
        mTablesFragment.updateTables();
    }

    @Override
    public void onSearchResult(int row) {

    }

    @Override
    public void onShouldDisplayPanelInternal() {

    }
}
