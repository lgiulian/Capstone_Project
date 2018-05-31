package com.crilu.gothandroid;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.crilu.gothandroid.databinding.ActivityPairBinding;
import com.crilu.opengotha.Tournament;
import com.crilu.opengotha.model.GamesPair;

import java.util.List;
import java.util.Vector;

public class PairActivity extends AppCompatActivity implements GamesPair.PairListener {

    private ActivityPairBinding mBinding;
    private GamesPair mGamesPairing;
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
        mGamesPairing.setPairListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGamesPairing.setPairListener(null);
    }

    private void init() {
        mGamesPairing = GothandroidApplication.getGamesPairInstance();
    }

    private void setupFragments() {
        List<Vector<String>> pairablePlayers = mGamesPairing.getTblPairablePlayers();
        mPairablePlayersFragment = new PairablePlayersFragment();
        mPairablePlayersFragment.setPairablePlayers(pairablePlayers);

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .add(R.id.pairable_players_container, mPairablePlayersFragment)
                .commit();

        List<Vector<String>> tables = mGamesPairing.getTblGames();
        mTablesFragment = new TablesFragment();
        mTablesFragment.setTables(tables);

        fragmentManager.beginTransaction()
                .add(R.id.tables_container, mTablesFragment)
                .commit();
    }

    public void onClickOk(View view) {
        finish();
    }

    public void onClickPair(View view) {
        mGamesPairing.pair(false, true);
    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onTableGamesUpdated() {
        List<Vector<String>> pairablePlayers = mGamesPairing.getTblPairablePlayers();
        mPairablePlayersFragment.setPairablePlayers(pairablePlayers);
        List<Vector<String>> tables = mGamesPairing.getTblGames();
        mTablesFragment.setTables(tables);
    }

    @Override
    public void onSearchResult(int row) {

    }

    @Override
    public void onShouldDisplayPanelInternal() {

    }
}
