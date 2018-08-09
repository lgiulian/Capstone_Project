package com.crilu.gothandroid;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;

import com.crilu.gothandroid.data.TournamentDao;
import com.crilu.gothandroid.databinding.ActivityGamesOptionsBinding;
import com.crilu.gothandroid.model.GameOptionViewModel;
import com.crilu.gothandroid.model.firestore.Tournament;
import com.crilu.opengotha.GeneralParameterSet;
import com.crilu.opengotha.TournamentInterface;
import com.crilu.opengotha.model.GamesOptions;

import timber.log.Timber;

public class GamesOptionsActivity extends AppCompatActivity implements GamesOptions.OnGamesOptionsListener, View.OnFocusChangeListener, View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private ActivityGamesOptionsBinding mBinding;
    private GameOptionViewModel mGameOptionViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_games_options);

        setSupportActionBar(mBinding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        init();
        setUiListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGameOptionViewModel.getGameOptions() != null) {
            mGameOptionViewModel.getGameOptions().addOnGamesOptionsListener(this);
        } else {
            Timber.e("GamesOptions instance is null");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGameOptionViewModel.getGameOptions() != null) {
            mGameOptionViewModel.getGameOptions().removeOnGamesOptionsListener(this);
        } else {
            Timber.e("GamesOptions instance is null");
        }
    }

    private void init() {
        mGameOptionViewModel = ViewModelProviders.of(this).get(GameOptionViewModel.class);
    }

    private void setUiListeners() {
        customInitComponents();
        mBinding.exportBtn.setOnClickListener(this);
        mBinding.saveBtn.setOnClickListener(this);
        mBinding.gobanSize.setOnFocusChangeListener(this);
        mBinding.komi.setOnFocusChangeListener(this);
        mBinding.basicTime.setOnFocusChangeListener(this);
        mBinding.standardTimeSeconds.setOnFocusChangeListener(this);
        mBinding.canadianMoves.setOnFocusChangeListener(this);
        mBinding.canadianTimeSeconds.setOnFocusChangeListener(this);
        mBinding.fisherBonusTime.setOnFocusChangeListener(this);
        mBinding.radioGroupTimeSystem.setOnCheckedChangeListener(this);
    }

    @SuppressLint("SetTextI18n")
    private void customInitComponents() {
        if (mGameOptionViewModel.getGameOptions() != null) {
            TournamentInterface tournament = mGameOptionViewModel.getGameOptions().getTournament();
            GeneralParameterSet gps = tournament.getTournamentParameterSet().getGeneralParameterSet();
            mBinding.gobanSize.setText(gps.getStrSize());
            mBinding.komi.setText(gps.getStrKomi());

            mBinding.basicTime.setText("" + gps.getBasicTime());

            int complTimeSystem = gps.getComplementaryTimeSystem();

            switch (complTimeSystem) {
                case GeneralParameterSet.GEN_GP_CTS_SUDDENDEATH:
                    mBinding.rbSuddenDeath.setChecked(true);
                    break;
                case GeneralParameterSet.GEN_GP_CTS_STDBYOYOMI:
                    mBinding.rbStandard.setChecked(true);
                    break;
                case GeneralParameterSet.GEN_GP_CTS_CANBYOYOMI:
                    mBinding.rbCanadian.setChecked(true);
                    break;
                case GeneralParameterSet.GEN_GP_CTS_FISCHER:
                    mBinding.rbFischer.setChecked(true);
                    break;
            }

            mBinding.standardTimeSeconds.setText("" + gps.getStdByoYomiTime());
            mBinding.canadianMoves.setText("" + gps.getNbMovesCanTime());
            mBinding.canadianTimeSeconds.setText("" + gps.getCanByoYomiTime());
            mBinding.fisherBonusTime.setText("" + gps.getFischerTime());
        } else {
            Timber.e("GamesOptions instance is null");
        }
    }

    private String checkUserLoggedIn() {
        String UID = GothandroidApplication.getCurrentUser();
        if (TextUtils.isEmpty(UID)) {
            Snackbar.make(mBinding.coordinatorLayout, getString(R.string.you_are_not_logged_in), Snackbar.LENGTH_LONG).show();
            return null;
        }
        return UID;
    }

    private void saveAndUploadOnFirestore() {
        GamesOptions gamesOptions = mGameOptionViewModel.getGameOptions();
        if (gamesOptions != null) {
            TournamentInterface tournamentGotha = gamesOptions.getTournament();
            if (tournamentGotha == null) {
                Snackbar.make(mBinding.coordinatorLayout, getString(R.string.no_tournament_selected), Snackbar.LENGTH_LONG).show();
                return;
            }
            String UID = checkUserLoggedIn();
            if (TextUtils.isEmpty(UID)) return;

            if (!TextUtils.isEmpty(tournamentGotha.getTournamentIdentity())) {
                Timber.d("saving tournament %s", tournamentGotha.getTournamentIdentity());
                Tournament tournament = TournamentDao.getTournamentByIdentity(this, tournamentGotha.getTournamentIdentity());
                TournamentDao.saveCurrentTournamentAndUploadOnFirestore(this, tournament, UID, true, mBinding.coordinatorLayout);
            }
        } else {
            Timber.e("GamesOptions instance is null");
        }
    }

    private void exportTournament() {

    }

    @Override
    public void onTitleUpdate(String title) {

    }

    @Override
    public void onUpdateEgfClass(String egfClass) {
        mBinding.egfClassTv.setText(egfClass);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();
        GamesOptions gamesOptions = mGameOptionViewModel.getGameOptions();
        if (gamesOptions != null) {
            switch (id) {
                case R.id.goban_size:
                    gamesOptions.txfSize = mBinding.gobanSize.getText().toString();
                    gamesOptions.txfSizeFocusLost();
                    break;
                case R.id.komi:
                    gamesOptions.txfKomi = mBinding.komi.getText().toString();
                    gamesOptions.txfKomiFocusLost();
                    break;
                case R.id.basic_time:
                    gamesOptions.txfBasicTime = mBinding.basicTime.getText().toString();
                    gamesOptions.txfBasicTimeFocusLost();
                    break;
                case R.id.standard_time_seconds:
                    gamesOptions.txfStdTime = mBinding.standardTimeSeconds.getText().toString();
                    gamesOptions.txfStdTimeFocusLost();
                    break;
                case R.id.canadian_moves:
                    gamesOptions.txfCanNbMoves = mBinding.canadianMoves.getText().toString();
                    gamesOptions.txfCanNbMovesFocusLost();
                    break;
                case R.id.canadian_time_seconds:
                    gamesOptions.txfCanTime = mBinding.canadianTimeSeconds.getText().toString();
                    gamesOptions.txfCanTimeFocusLost();
                    break;
                case R.id.fisher_bonus_time:
                    gamesOptions.txfFischerTime = mBinding.fisherBonusTime.getText().toString();
                    gamesOptions.txfFischerTimeFocusLost();
                    break;
            }
        } else {
            Timber.e("GamesOptions instance is null");
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.save_btn:
                saveAndUploadOnFirestore();
                break;
            case R.id.export_btn:
                exportTournament();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int id = group.getId();
        GamesOptions gamesOptions = mGameOptionViewModel.getGameOptions();
        if (gamesOptions != null) {
            switch (id) {
                case R.id.radioGroup_time_system:
                    gamesOptions.rdbSuddenDeath = mBinding.rbSuddenDeath.isChecked();
                    gamesOptions.rdbStdByoYomi = mBinding.rbStandard.isChecked();
                    gamesOptions.rdbCanByoYomi = mBinding.rbCanadian.isChecked();
                    gamesOptions.rdbFischer = mBinding.rbFischer.isChecked();
                    gamesOptions.rdbComplTimeSystemActionPerformed();
                    break;
            }
        } else {
            Timber.e("GamesOptions instance is null");
        }
    }
}
