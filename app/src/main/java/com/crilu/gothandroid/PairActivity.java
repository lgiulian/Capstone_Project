package com.crilu.gothandroid;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.crilu.gothandroid.databinding.ActivityPairBinding;
import com.crilu.gothandroid.model.PairViewModel;
import com.crilu.opengotha.model.GamesPair;

public class PairActivity extends AppCompatActivity implements GamesPair.OnPairListener {

    private ActivityPairBinding mBinding;
    private PairViewModel mPairViewModel;
    private GamesPair mGamesPair;
    private PairablePlayersFragment mPairablePlayersFragment;
    private TablesFragment mTablesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_pair);

        setSupportActionBar(mBinding.toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        mPairViewModel = ViewModelProviders.of(this).get(PairViewModel.class);
        mGamesPair = mPairViewModel.getGamesPair();
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
