package com.crilu.gothandroid;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.crilu.gothandroid.databinding.ActivityResultBinding;
import com.crilu.gothandroid.model.PairViewModel;
import com.crilu.gothandroid.model.ResultViewModel;
import com.crilu.opengotha.model.GamesPair;
import com.crilu.opengotha.model.GamesResults;

import timber.log.Timber;

public class ResultActivity extends AppCompatActivity implements GamesResults.GamesResultsListener {

    private ActivityResultBinding mBinding;
    private ResultViewModel mResultViewModel;
    private ResultsFragment mResultsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_result);

        setSupportActionBar(mBinding.toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

        if(savedInstanceState == null) {
            setupFragments();
        }

    }

    private void init() {
        mResultViewModel = ViewModelProviders.of(this).get(ResultViewModel.class);

        mBinding.roundNoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String roundSelectedStr = mBinding.roundNoSpinner.getItemAtPosition(position).toString();
                Timber.d("selected round %s", roundSelectedStr);
                mResultViewModel.getGamesResults().setSpnRoundNumber(Integer.valueOf(roundSelectedStr));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mResultViewModel.getGamesResults().addGamesResultsListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mResultViewModel.getGamesResults().removeGamesResultsListener(this);
    }

    private void setupFragments() {
        mResultsFragment = new ResultsFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .add(R.id.results_container, mResultsFragment)
                .commit();
    }

    @Override
    public void updateTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onMessage(String message) {
        Snackbar.make(mBinding.coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onUpdateTableGames() {
        mResultsFragment.updateGames();
    }
}
