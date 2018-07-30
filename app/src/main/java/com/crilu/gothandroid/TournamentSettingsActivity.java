package com.crilu.gothandroid;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RadioGroup;

import com.crilu.gothandroid.data.TournamentDao;
import com.crilu.gothandroid.databinding.ActivityTournamentSettingsBinding;
import com.crilu.gothandroid.databinding.FragmentTournamentSettingsGeneralBinding;
import com.crilu.gothandroid.databinding.FragmentTournamentSettingsHandicapBinding;
import com.crilu.gothandroid.databinding.FragmentTournamentSettingsPairingBinding;
import com.crilu.gothandroid.databinding.FragmentTournamentSettingsPlacementBinding;
import com.crilu.gothandroid.model.TournamentOptionViewModel;
import com.crilu.gothandroid.model.firestore.Tournament;
import com.crilu.opengotha.GeneralParameterSet;
import com.crilu.opengotha.HandicapParameterSet;
import com.crilu.opengotha.PairingParameterSet;
import com.crilu.opengotha.PlacementParameterSet;
import com.crilu.opengotha.Player;
import com.crilu.opengotha.TournamentInterface;
import com.crilu.opengotha.components.JComboBox;
import com.crilu.opengotha.model.TournamentOptions;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import timber.log.Timber;

public class TournamentSettingsActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private ActivityTournamentSettingsBinding mBinding;
    private TournamentOptionViewModel mTournamentOptionViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tournament_settings);
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        init();
    }

    private void init() {
        mTournamentOptionViewModel = ViewModelProviders.of(this).get(TournamentOptionViewModel.class);
    }

    private String checkUserLoggedIn() {
        String UID = GothandroidApplication.getCurrentUser();
        if (TextUtils.isEmpty(UID)) {
            Snackbar.make(mBinding.mainContent, getString(R.string.you_are_not_logged_in), Snackbar.LENGTH_LONG).show();
            return null;
        }
        return UID;
    }

    private void saveAndUploadOnFirestore() {
        TournamentOptions tournamentOptions = mTournamentOptionViewModel.getTournamentOptions();
        TournamentInterface tournamentGotha = tournamentOptions.getTournament();
        if (tournamentGotha == null) {
            Snackbar.make(mBinding.mainContent, getString(R.string.no_tournament_selected), Snackbar.LENGTH_LONG).show();
            return;
        }
        String UID = checkUserLoggedIn();
        if (TextUtils.isEmpty(UID)) return;

        if (!TextUtils.isEmpty(tournamentGotha.getTournamentIdentity())) {
            Timber.d("saving tournament %s", tournamentGotha.getTournamentIdentity());
            Tournament tournament = TournamentDao.getTournamentByIdentity(this, tournamentGotha.getTournamentIdentity());
            tournament.setFullName(tournamentGotha.getFullName());
            tournament.setShortName(tournamentGotha.getShortName());
            tournament.setLocation(tournamentOptions.txfLocation);
            tournament.setDirector(tournamentOptions.txfDirector);
            try {
                tournament.setBeginDate(GothandroidApplication.dateFormat.parse(tournamentOptions.txfBeginDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            TournamentDao.saveCurrentTournamentAndUploadOnFirestore(this, tournament, UID, true, mBinding.mainContent);
        }
    }

    private void exportTournament() {

    }

    public static class GeneralFragment extends Fragment implements DatePickerDialog.OnDateSetListener, View.OnClickListener, TournamentOptions.OnTournamentOptionsListener, RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener, View.OnFocusChangeListener {

        private int mCurrentDatePickerFieldId;
        private FragmentTournamentSettingsGeneralBinding mBinding;
        private TournamentOptionViewModel mTournamentOptionViewModel;

        public GeneralFragment() {
        }

        public static GeneralFragment newInstance() {
            GeneralFragment fragment = new GeneralFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            init();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tournament_settings_general, container, false);

            setUiListeners();

            View rootView = mBinding.getRoot();
            return rootView;
        }

        public void setUiListeners() {
            customInitComponents();
            final TournamentOptions tournamentOptions = mTournamentOptionViewModel.getTournamentOptions();
            mBinding.tournamentName.setOnFocusChangeListener(this);
            mBinding.tournamentShortName.setOnFocusChangeListener(this);
            mBinding.tournamentLocation.setOnFocusChangeListener(this);
            mBinding.tournamentDirector.setOnFocusChangeListener(this);
            mBinding.tournamentBeginDate.setOnFocusChangeListener(this);
            mBinding.tournamentEndDate.setOnFocusChangeListener(this);
            mBinding.numberOfRoundsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedCrit = mBinding.numberOfRoundsSpinner.getItemAtPosition(position).toString();
                    Timber.d("selected value %s", selectedCrit);
                    tournamentOptions.txfNumberOfRounds = selectedCrit;
                    tournamentOptions.txfNumberOfRoundsFocusLost();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });

            mBinding.beginDatePickerBtn.setOnClickListener(this);
            mBinding.endDatePickerBtn.setOnClickListener(this);
            mBinding.exportBtn.setOnClickListener(this);
            mBinding.saveBtn.setOnClickListener(this);
            mBinding.radioGroupMmsAbsent.setOnCheckedChangeListener(this);
            mBinding.radioGroupMmsBye.setOnCheckedChangeListener(this);
            mBinding.radioGroupNbwAbsent.setOnCheckedChangeListener(this);
            mBinding.radioGroupNbwBye.setOnCheckedChangeListener(this);
            mBinding.roundDownChkBox.setOnCheckedChangeListener(this);
        }

        private void customInitComponents() {
            TournamentOptions tournamentOptions = mTournamentOptionViewModel.getTournamentOptions();
            GeneralParameterSet gps = tournamentOptions.getTournament().getTournamentParameterSet().getGeneralParameterSet();
            mBinding.tournamentName.setText(tournamentOptions.txfName);
            mBinding.tournamentShortName.setText(tournamentOptions.txfShortName);
            mBinding.tournamentLocation.setText(tournamentOptions.txfLocation);
            mBinding.tournamentDirector.setText(tournamentOptions.txfDirector);
            mBinding.tournamentBeginDate.setText(tournamentOptions.txfBeginDate);
            mBinding.tournamentEndDate.setText(tournamentOptions.txfEndDate);
            int roundNumber = 1;
            try {
                roundNumber = Integer.valueOf(tournamentOptions.txfNumberOfRounds);
            } catch (NumberFormatException ex) {}
            mBinding.numberOfRoundsSpinner.setSelection(roundNumber - 1);

            switch(gps.getGenMMS2ValueAbsent()){
                case 0 : mBinding.mmsAbsentButton1.setChecked(true); break;
                case 1 : mBinding.mmsAbsentButton2.setChecked(true); break;
                case 2 : mBinding.mmsAbsentButton3.setChecked(true); break;
            }
            switch(gps.getGenNBW2ValueAbsent()){
                case 0 : mBinding.nbwAbsentButton1.setChecked(true); break;
                case 1 : mBinding.nbwAbsentButton2.setChecked(true); break;
                case 2 : mBinding.nbwAbsentButton3.setChecked(true); break;
            }
            switch(gps.getGenMMS2ValueBye()){
                case 0 : mBinding.mmsByeButton1.setChecked(true); break;
                case 1 : mBinding.mmsByeButton2.setChecked(true); break;
                case 2 : mBinding.mmsByeButton3.setChecked(true); break;
            }
            switch(gps.getGenNBW2ValueBye()){
                case 0 : mBinding.nbwByeButton1.setChecked(true); break;
                case 1 : mBinding.nbwByeButton2.setChecked(true); break;
                case 2 : mBinding.nbwByeButton3.setChecked(true); break;
            }

            mBinding.roundDownChkBox.setChecked(gps.isGenRoundDownNBWMMS());

            if (tournamentOptions.pnlMcMahonVisible){
                mBinding.mcmahonBar.setText(Player.convertIntToKD(gps.getGenMMBar()));
                mBinding.mcmahonFloor.setText(Player.convertIntToKD(gps.getGenMMFloor()));
                mBinding.mcmahonZero.setText(Player.convertIntToKD(gps.getGenMMZero()));
            }
        }

        private void init() {
            mTournamentOptionViewModel = ViewModelProviders.of(getActivity()).get(TournamentOptionViewModel.class);
        }

        @Override
        public void onResume() {
            super.onResume();
            mTournamentOptionViewModel.getTournamentOptions().addTournamentOptionsListener(this);
        }

        @Override
        public void onStop() {
            super.onStop();
            mTournamentOptionViewModel.getTournamentOptions().removeTournamentOptionsListener(this);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar cal = new GregorianCalendar(year, month, dayOfMonth);
            String dateStr = GothandroidApplication.dateFormat.format(cal.getTime());
            if (mCurrentDatePickerFieldId == R.id.begin_date_picker_btn) {
                mBinding.tournamentBeginDate.setText(dateStr);
                TournamentOptions tournamentOptions = mTournamentOptionViewModel.getTournamentOptions();
                tournamentOptions.txfBeginDate = dateStr;
                tournamentOptions.txfBeginDateFocusLost();
            } else if (mCurrentDatePickerFieldId == R.id.end_date_picker_btn) {
                mBinding.tournamentEndDate.setText(dateStr);
                TournamentOptions tournamentOptions = mTournamentOptionViewModel.getTournamentOptions();
                tournamentOptions.txfEndDate = dateStr;
                tournamentOptions.txfEndDateFocusLost();
            }

        }

        public void showDatePickerDialog(View view) {
            mCurrentDatePickerFieldId = view.getId();
            CreateTournamentActivity.DatePickerFragment newFragment = new CreateTournamentActivity.DatePickerFragment();
            newFragment.setCallback(this);
            newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.begin_date_picker_btn:
                    showDatePickerDialog(v);
                    break;
                case R.id.end_date_picker_btn:
                    showDatePickerDialog(v);
                    break;
                case R.id.save_btn:
                    if (getActivity() != null) {
                        ((TournamentSettingsActivity) getActivity()).saveAndUploadOnFirestore();
                    }
                    break;
                case R.id.export_btn:
                    if (getActivity() != null) {
                        ((TournamentSettingsActivity) getActivity()).exportTournament();
                    }
                    break;
            }
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int id = group.getId();
            TournamentOptions tournamentOptions = mTournamentOptionViewModel.getTournamentOptions();
            switch (id) {
                case R.id.radioGroup_mms_absent:
                    tournamentOptions.rdbAbsentMMS0 = mBinding.mmsAbsentButton1.isChecked();
                    tournamentOptions.rdbAbsentMMS1 = mBinding.mmsAbsentButton2.isChecked();
                    tournamentOptions.rdbAbsentMMS2 = mBinding.mmsAbsentButton3.isChecked();
                    tournamentOptions.rdbAbsentOrByeFocusLost();
                    break;
                case R.id.radioGroup_mms_bye:
                    tournamentOptions.rdbByeMMS0 = mBinding.mmsByeButton1.isChecked();
                    tournamentOptions.rdbByeMMS1 = mBinding.mmsByeButton2.isChecked();
                    tournamentOptions.rdbByeMMS2 = mBinding.mmsByeButton3.isChecked();
                    tournamentOptions.rdbAbsentOrByeFocusLost();
                    break;
                case R.id.radioGroup_nbw_absent:
                    tournamentOptions.rdbAbsentNBW0 = mBinding.nbwAbsentButton1.isChecked();
                    tournamentOptions.rdbAbsentNBW1 = mBinding.nbwAbsentButton2.isChecked();
                    tournamentOptions.rdbAbsentNBW2 = mBinding.nbwAbsentButton3.isChecked();
                    tournamentOptions.rdbAbsentOrByeFocusLost();
                    break;
                case R.id.radioGroup_nbw_bye:
                    tournamentOptions.rdbByeNBW0 = mBinding.nbwByeButton1.isChecked();
                    tournamentOptions.rdbByeNBW1 = mBinding.nbwByeButton2.isChecked();
                    tournamentOptions.rdbByeNBW2 = mBinding.nbwByeButton3.isChecked();
                    tournamentOptions.rdbAbsentOrByeFocusLost();
                    break;
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int id = buttonView.getId();
            TournamentOptions tournamentOptions = mTournamentOptionViewModel.getTournamentOptions();
            switch (id) {
                case R.id.roundDownChkBox:
                    tournamentOptions.ckbRoundDown = isChecked;
                    tournamentOptions.ckbRoundDownFocusLost();
                    break;
            }
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            int id = v.getId();
            TournamentOptions tournamentOptions = mTournamentOptionViewModel.getTournamentOptions();
            switch (id) {
                case R.id.tournament_name:
                    tournamentOptions.txfName = mBinding.tournamentName.getText().toString();
                    tournamentOptions.txfNameFocusLost();
                    break;
                case R.id.tournament_short_name:
                    tournamentOptions.txfShortName = mBinding.tournamentShortName.getText().toString();
                    tournamentOptions.txfShortNameFocusLost();
                    break;
                case R.id.tournament_location:
                    tournamentOptions.txfLocation = mBinding.tournamentLocation.getText().toString();
                    tournamentOptions.txfLocationFocusLost();
                    break;
                case R.id.tournament_director:
                    tournamentOptions.txfDirector = mBinding.tournamentDirector.getText().toString();
                    tournamentOptions.txfDirectorFocusLost();
                    break;
            }
        }

        @Override
        public void onDisplayChangeSystem() {
            Timber.d("onDisplayChangeSystem NOT implemented yet");
        }

        @Override
        public void onTitleUpdate(String title) {
            Timber.d("onTitleUpdate NOT implemented yet");
        }

        @Override
        public void onMessage(String message) {
            Timber.d("onMessage NOT implemented yet");
        }

    }

    public static class HandicapFragment extends Fragment implements TournamentOptions.OnTournamentOptionsListener, View.OnFocusChangeListener, RadioGroup.OnCheckedChangeListener, View.OnClickListener {

        private FragmentTournamentSettingsHandicapBinding mBinding;
        private TournamentOptionViewModel mTournamentOptionViewModel;

        public HandicapFragment() {
        }

        public static HandicapFragment newInstance() {
            HandicapFragment fragment = new HandicapFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            init();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tournament_settings_handicap, container, false);

            setUiListeners();

            View rootView = mBinding.getRoot();
            return rootView;
        }

        public void setUiListeners() {
            customInitComponents();
            mBinding.noHandicap.setOnFocusChangeListener(this);
            mBinding.handicapCeiling.setOnFocusChangeListener(this);
            mBinding.radioGroupHandicapBasedOn.setOnCheckedChangeListener(this);
            mBinding.radioGroupHandicapCorrection.setOnCheckedChangeListener(this);
            mBinding.exportBtn.setOnClickListener(this);
            mBinding.saveBtn.setOnClickListener(this);
        }

        private void customInitComponents() {
            TournamentOptions tournamentOptions = mTournamentOptionViewModel.getTournamentOptions();
            HandicapParameterSet hps = tournamentOptions.getTournament().getTournamentParameterSet().getHandicapParameterSet();
            mBinding.noHandicap.setText(Player.convertIntToKD(hps.getHdNoHdRankThreshold()));
            mBinding.handicapCeiling.setText("" + hps.getHdCeiling());
            if(hps.isHdBasedOnMMS()) {
                mBinding.mcmahonRb.setChecked(true);
            } else {
                mBinding.rankRb.setChecked(true);
            }
            switch(hps.getHdCorrection()){
                case 0 : mBinding.hndCorrectionRb.setChecked(true); break;
                case 1 : mBinding.hndCorrection1Rb.setChecked(true); break;
                case 2 : mBinding.hndCorrection2Rb.setChecked(true); break;
                case 3 : mBinding.hndCorrection3Rb.setChecked(true); break;
                case -1: mBinding.hndCorrectionPlus1Rb.setChecked(true); break;
            }
        }

        private void init() {
            mTournamentOptionViewModel = ViewModelProviders.of(getActivity()).get(TournamentOptionViewModel.class);
        }

        @Override
        public void onResume() {
            super.onResume();
            mTournamentOptionViewModel.getTournamentOptions().addTournamentOptionsListener(this);
        }

        @Override
        public void onStop() {
            super.onStop();
            mTournamentOptionViewModel.getTournamentOptions().removeTournamentOptionsListener(this);
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            int id = v.getId();
            if (hasFocus) return;
            TournamentOptions tournamentOptions = mTournamentOptionViewModel.getTournamentOptions();
            switch (id) {
                case R.id.no_handicap:
                    tournamentOptions.txfNoHdRankThreshold = mBinding.noHandicap.getText().toString();
                    tournamentOptions.txfNoHdRankThresholdFocusLost();
                    break;
                case R.id.handicap_ceiling:
                    tournamentOptions.txfHdCeiling = mBinding.handicapCeiling.getText().toString();
                    tournamentOptions.txfHdCeilingFocusLost();
                    break;
            }
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int id = group.getId();
            TournamentOptions tournamentOptions = mTournamentOptionViewModel.getTournamentOptions();
            switch (id) {
                case R.id.radioGroup_handicap_based_on:
                    tournamentOptions.rdbHdBaseMMS = mBinding.mcmahonRb.isChecked();
                    tournamentOptions.rdbHdBaseRank = mBinding.rankRb.isChecked();
                    tournamentOptions.rdbHdBaseMMSActionPerformed();
                    tournamentOptions.rdbHdBaseRankActionPerformed();
                    break;
                case R.id.radioGroup_handicap_correction:
                    tournamentOptions.rdbHdCorrection0 = mBinding.hndCorrectionRb.isChecked();
                    tournamentOptions.rdbHdCorrection1 = mBinding.hndCorrection1Rb.isChecked();
                    tournamentOptions.rdbHdCorrection2 = mBinding.hndCorrection2Rb.isChecked();
                    tournamentOptions.rdbHdCorrection3 = mBinding.hndCorrection3Rb.isChecked();
                    tournamentOptions.rdbHdCorrectionPlus1 = mBinding.hndCorrectionPlus1Rb.isChecked();
                    tournamentOptions.rdbHdCorrectionActionPerformed();
                    break;
            }
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.save_btn:
                    if (getActivity() != null) {
                        ((TournamentSettingsActivity) getActivity()).saveAndUploadOnFirestore();
                    }
                    break;
                case R.id.export_btn:
                    if (getActivity() != null) {
                        ((TournamentSettingsActivity) getActivity()).exportTournament();
                    }
                    break;
            }
        }

        @Override
        public void onDisplayChangeSystem() {
            Timber.d("onDisplayChangeSystem NOT implemented yet");
        }

        @Override
        public void onTitleUpdate(String title) {
            Timber.d("onTitleUpdate NOT implemented yet");
        }

        @Override
        public void onMessage(String message) {
            Timber.d("onMessage NOT implemented yet");
        }
    }

    public static class PlacementFragment extends Fragment implements TournamentOptions.OnTournamentOptionsListener, View.OnClickListener {

        private FragmentTournamentSettingsPlacementBinding mBinding;
        private TournamentOptionViewModel mTournamentOptionViewModel;

        public PlacementFragment() {
        }

        public static PlacementFragment newInstance() {
            PlacementFragment fragment = new PlacementFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            init();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tournament_settings_placement, container, false);

            setUiListeners();

            View rootView = mBinding.getRoot();
            return rootView;
        }

        public void setUiListeners() {
            customInitComponents();
            mBinding.exportBtn.setOnClickListener(this);
            mBinding.saveBtn.setOnClickListener(this);
        }

        private void customInitComponents() {
            final TournamentOptions tournamentOptions = mTournamentOptionViewModel.getTournamentOptions();
            PlacementParameterSet pps = tournamentOptions.getTournament().getTournamentParameterSet().getPlacementParameterSet();

            ArrayAdapter<String> spinnerAdapter = getStringArrayAdapter(tournamentOptions.cbxCrit1);
            mBinding.criterion1Spinner.setAdapter(spinnerAdapter);
            mBinding.criterion1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedCrit = mBinding.criterion1Spinner.getItemAtPosition(position).toString();
                    Timber.d("selected value %s", selectedCrit);
                    tournamentOptions.cbxCrit1.getModel().setSelectedItem(selectedCrit);
                    tournamentOptions.cbxCritFocusLost();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });

            spinnerAdapter = getStringArrayAdapter(tournamentOptions.cbxCrit2);
            mBinding.criterion2Spinner.setAdapter(spinnerAdapter);
            mBinding.criterion2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedCrit = mBinding.criterion2Spinner.getItemAtPosition(position).toString();
                    Timber.d("selected value %s", selectedCrit);
                    tournamentOptions.cbxCrit2.getModel().setSelectedItem(selectedCrit);
                    tournamentOptions.cbxCritFocusLost();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });
            spinnerAdapter = getStringArrayAdapter(tournamentOptions.cbxCrit3);
            mBinding.criterion3Spinner.setAdapter(spinnerAdapter);
            mBinding.criterion3Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedCrit = mBinding.criterion3Spinner.getItemAtPosition(position).toString();
                    Timber.d("selected value %s", selectedCrit);
                    tournamentOptions.cbxCrit3.getModel().setSelectedItem(selectedCrit);
                    tournamentOptions.cbxCritFocusLost();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });
            spinnerAdapter = getStringArrayAdapter(tournamentOptions.cbxCrit4);
            mBinding.criterion4Spinner.setAdapter(spinnerAdapter);
            mBinding.criterion4Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedCrit = mBinding.criterion4Spinner.getItemAtPosition(position).toString();
                    Timber.d("selected value %s", selectedCrit);
                    tournamentOptions.cbxCrit4.getModel().setSelectedItem(selectedCrit);
                    tournamentOptions.cbxCritFocusLost();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });

            int[] displayedCriteria = pps.getPlaCriteria();
            int posToSelect = ((ArrayAdapter)mBinding.criterion1Spinner.getAdapter()).getPosition(PlacementParameterSet.criterionLongName(displayedCriteria[0]));
            mBinding.criterion1Spinner.setSelection(posToSelect);
            posToSelect = ((ArrayAdapter)mBinding.criterion2Spinner.getAdapter()).getPosition(PlacementParameterSet.criterionLongName(displayedCriteria[1]));
            mBinding.criterion2Spinner.setSelection(posToSelect);
            posToSelect = ((ArrayAdapter)mBinding.criterion3Spinner.getAdapter()).getPosition(PlacementParameterSet.criterionLongName(displayedCriteria[2]));
            mBinding.criterion3Spinner.setSelection(posToSelect);
            posToSelect = ((ArrayAdapter)mBinding.criterion4Spinner.getAdapter()).getPosition(PlacementParameterSet.criterionLongName(displayedCriteria[3]));
            mBinding.criterion4Spinner.setSelection(posToSelect);
        }

        @NonNull
        private ArrayAdapter<String> getStringArrayAdapter(JComboBox crit1) {
            List<Vector<String>> dataModel = crit1.getModel().getDataModel();
            List<String> values = new ArrayList<>();
            for (Vector<String> row: dataModel) {
                values.add(row.get(0));
            }
            return new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, values);
        }

        private void init() {
            mTournamentOptionViewModel = ViewModelProviders.of(getActivity()).get(TournamentOptionViewModel.class);
        }

        @Override
        public void onResume() {
            super.onResume();
            mTournamentOptionViewModel.getTournamentOptions().addTournamentOptionsListener(this);
        }

        @Override
        public void onStop() {
            super.onStop();
            mTournamentOptionViewModel.getTournamentOptions().removeTournamentOptionsListener(this);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.save_btn:
                    if (getActivity() != null) {
                        ((TournamentSettingsActivity) getActivity()).saveAndUploadOnFirestore();
                    }
                    break;
                case R.id.export_btn:
                    if (getActivity() != null) {
                        ((TournamentSettingsActivity) getActivity()).exportTournament();
                    }
                    break;
            }
        }

        @Override
        public void onDisplayChangeSystem() {
            Timber.d("onDisplayChangeSystem NOT implemented yet");
        }

        @Override
        public void onTitleUpdate(String title) {
            Timber.d("onTitleUpdate NOT implemented yet");
        }

        @Override
        public void onMessage(String message) {
            Timber.d("onMessage NOT implemented yet");
        }
    }


    public static class PairingFragment extends Fragment implements TournamentOptions.OnTournamentOptionsListener, CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener, View.OnFocusChangeListener, View.OnClickListener {

        private FragmentTournamentSettingsPairingBinding mBinding;
        private TournamentOptionViewModel mTournamentOptionViewModel;

        public PairingFragment() {
        }

        public static PairingFragment newInstance() {
            PairingFragment fragment = new PairingFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            init();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tournament_settings_pairing, container, false);

            setUiListeners();

            View rootView = mBinding.getRoot();
            return rootView;
        }

        public void setUiListeners() {
            customInitComponents();
            mBinding.earlierRoundsUpTo.setOnFocusChangeListener(this);
            mBinding.addSortingRatingCkb.setOnCheckedChangeListener(this);
            mBinding.radioGroupEarlierRounds.setOnCheckedChangeListener(this);
            mBinding.radioGroupLaterRounds.setOnCheckedChangeListener(this);
            mBinding.compensateDrawCkb.setOnCheckedChangeListener(this);
            mBinding.avoidPairingSameCkb.setOnCheckedChangeListener(this);
            mBinding.radioGroupPlayerUpperGroup.setOnCheckedChangeListener(this);
            mBinding.radioGroupPlayerLowerGroup.setOnCheckedChangeListener(this);
            mBinding.radioGroupBaseCriteriaPair.setOnCheckedChangeListener(this);
            mBinding.balanceWhiteBlackCkb.setOnCheckedChangeListener(this);
            mBinding.doNotApply.setOnFocusChangeListener(this);
            mBinding.playersAtLeastCkb.setOnCheckedChangeListener(this);
            mBinding.playersAboveMcmahonCkb.setOnCheckedChangeListener(this);
            mBinding.exportBtn.setOnClickListener(this);
            mBinding.saveBtn.setOnClickListener(this);
        }

        private void customInitComponents() {
            TournamentOptions tournamentOptions = mTournamentOptionViewModel.getTournamentOptions();
            TournamentInterface tournament = tournamentOptions.getTournament();
            PairingParameterSet paiPS = tournament.getTournamentParameterSet().getPairingParameterSet();

            mBinding.earlierRoundsUpTo.setText("" + (paiPS.getPaiMaLastRoundForSeedSystem1() + 1));
            mBinding.addSortingRatingCkb.setChecked(
                    paiPS.getPaiMaAdditionalPlacementCritSystem1() == PlacementParameterSet.PLA_CRIT_RATING);

            if (paiPS.getPaiMaSeedSystem1() == PairingParameterSet.PAIMA_SEED_SPLITANDRANDOM)
                mBinding.earlierRoundsRb1.setChecked(true);
            if (paiPS.getPaiMaSeedSystem1() == PairingParameterSet.PAIMA_SEED_SPLITANDFOLD)
                mBinding.earlierRoundsRb2.setChecked(true);
            if (paiPS.getPaiMaSeedSystem1() == PairingParameterSet.PAIMA_SEED_SPLITANDSLIP)
                mBinding.earlierRoundsRb3.setChecked(true);
            if (paiPS.getPaiMaSeedSystem2() == PairingParameterSet.PAIMA_SEED_SPLITANDRANDOM)
                mBinding.laterRoundsRb1.setChecked(true);
            if (paiPS.getPaiMaSeedSystem2() == PairingParameterSet.PAIMA_SEED_SPLITANDFOLD)
                mBinding.laterRoundsRb2.setChecked(true);
            if (paiPS.getPaiMaSeedSystem2() == PairingParameterSet.PAIMA_SEED_SPLITANDSLIP)
                mBinding.laterRoundsRb3.setChecked(true);

            mBinding.compensateDrawCkb.setChecked(paiPS.isPaiMaCompensateDUDD());
            if (paiPS.getPaiMaDUDDUpperMode() == PairingParameterSet.PAIMA_DUDD_TOP)
                mBinding.playerUpperGroupRb1.setChecked(true);
            if (paiPS.getPaiMaDUDDUpperMode() == PairingParameterSet.PAIMA_DUDD_MID)
                mBinding.playerUpperGroupRb2.setChecked(true);
            if (paiPS.getPaiMaDUDDUpperMode() == PairingParameterSet.PAIMA_DUDD_BOT)
                mBinding.playerUpperGroupRb3.setChecked(true);
            if (paiPS.getPaiMaDUDDLowerMode() == PairingParameterSet.PAIMA_DUDD_TOP)
                mBinding.playerLowerGroupRb1.setChecked(true);
            if (paiPS.getPaiMaDUDDLowerMode() == PairingParameterSet.PAIMA_DUDD_MID)
                mBinding.playerLowerGroupRb2.setChecked(true);
            if (paiPS.getPaiMaDUDDLowerMode() == PairingParameterSet.PAIMA_DUDD_BOT)
                mBinding.playerLowerGroupRb3.setChecked(true);

            mBinding.avoidPairingSameCkb.setChecked(paiPS.getPaiBaAvoidDuplGame() != 0);

            if (paiPS.getPaiBaRandom() == 0){
                mBinding.baseCriteriaPairRb1.setChecked(true);
            }
            else{
                mBinding.baseCriteriaPairRb2.setChecked(true);
            }

            mBinding.balanceWhiteBlackCkb.setChecked(paiPS.getPaiBaBalanceWB() != 0);

            mBinding.doNotApply.setText(Player.convertIntToKD(paiPS.getPaiSeRankThreshold()));
            mBinding.playersAtLeastCkb.setChecked(paiPS.isPaiSeBarThresholdActive());
            mBinding.playersAboveMcmahonCkb.setChecked(paiPS.isPaiSeNbWinsThresholdActive());
        }

        private void init() {
            mTournamentOptionViewModel = ViewModelProviders.of(getActivity()).get(TournamentOptionViewModel.class);
        }

        @Override
        public void onResume() {
            super.onResume();
            mTournamentOptionViewModel.getTournamentOptions().addTournamentOptionsListener(this);
        }

        @Override
        public void onStop() {
            super.onStop();
            mTournamentOptionViewModel.getTournamentOptions().removeTournamentOptionsListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int id = buttonView.getId();
            TournamentOptions tournamentOptions = mTournamentOptionViewModel.getTournamentOptions();
            switch (id) {
                case R.id.add_sorting_rating_ckb:
                    tournamentOptions.ckbAddSortOnRating = isChecked;
                    tournamentOptions.ckbAddSortOnRatingFocusLost();
                    break;
                case R.id.avoid_drawing_ckb:
                    tournamentOptions.ckbAvoid2DUDD = isChecked;
                    break;
                case R.id.compensate_draw_ckb:
                    tournamentOptions.ckbCompensate = isChecked;
                    tournamentOptions.ckbCompensateFocusLost();
                    break;
                case R.id.balance_white_black_ckb:
                    tournamentOptions.ckbBalanceWB = isChecked;
                    tournamentOptions.ckbBalanceWBFocusLost();
                    break;
                case R.id.players_at_least_ckb:
                    tournamentOptions.ckbSeNbWinsThresholdActive = isChecked;
                    tournamentOptions.ckbSecCritFocusLost();
                    break;
                case R.id.players_above_mcmahon_ckb:
                    tournamentOptions.ckbSeBarThresholdActive = isChecked;
                    tournamentOptions.ckbSeBarThresholdActiveFocusLost();
                    break;
            }
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int id = group.getId();
            TournamentOptions tournamentOptions = mTournamentOptionViewModel.getTournamentOptions();
            switch (id) {
                case R.id.radioGroup_earlier_rounds:
                    tournamentOptions.rdbFormerSplitAndRandom = mBinding.earlierRoundsRb1.isChecked();
                    tournamentOptions.rdbFormerSplitAndFold = mBinding.earlierRoundsRb2.isChecked();
                    tournamentOptions.rdbFormerSplitAndSlip = mBinding.earlierRoundsRb3.isChecked();
                    tournamentOptions.rdbSeedSystemFocusLost();
                    break;
                case R.id.radioGroup_later_rounds:
                    tournamentOptions.rdbLatterSplitAndRandom = mBinding.laterRoundsRb1.isChecked();
                    tournamentOptions.rdbLatterSplitAndFold = mBinding.laterRoundsRb2.isChecked();
                    tournamentOptions.rdbLatterSplitAndSlip = mBinding.laterRoundsRb3.isChecked();
                    tournamentOptions.rdbSeedSystemFocusLost();
                    break;
                case R.id.radioGroup_player_upper_group:
                    tournamentOptions.rdbDUDDUGTop = mBinding.playerUpperGroupRb1.isChecked();
                    tournamentOptions.rdbDUDDUGMid = mBinding.playerUpperGroupRb2.isChecked();
                    tournamentOptions.rdbDUDDUGBot = mBinding.playerUpperGroupRb3.isChecked();
                    tournamentOptions.rdbDUDDFocusLost();
                    break;
                case R.id.radioGroup_player_lower_group:
                    tournamentOptions.rdbDUDDLGTop = mBinding.playerLowerGroupRb1.isChecked();
                    tournamentOptions.rdbDUDDLGMid = mBinding.playerLowerGroupRb2.isChecked();
                    tournamentOptions.rdbDUDDLGBot = mBinding.playerLowerGroupRb3.isChecked();
                    tournamentOptions.rdbDUDDFocusLost();
                    break;
                case R.id.radioGroup_base_criteria_pair:
                    tournamentOptions.rdbNoRandom = mBinding.baseCriteriaPairRb1.isChecked();
                    tournamentOptions.rdbAcceptRandom = mBinding.baseCriteriaPairRb2.isChecked();
                    tournamentOptions.rdbNoRandomActionPerformed();
                    tournamentOptions.rdbAcceptRandomActionPerformed();
                    break;
            }
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            int id = v.getId();
            if (hasFocus) return;
            TournamentOptions tournamentOptions = mTournamentOptionViewModel.getTournamentOptions();
            switch (id) {
                case R.id.earlier_rounds_up_to:
                    tournamentOptions.txfLastRoundForSeedSystem1 = mBinding.earlierRoundsUpTo.getText().toString();
                    tournamentOptions.txfLastRoundForSeedSystem1FocusLost();
                    break;
                case R.id.do_not_apply:
                    tournamentOptions.txfSeRankThreshold = mBinding.doNotApply.getText().toString();
                    tournamentOptions.txfSecCritFocusLost();
                    break;
            }
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.save_btn:
                    if (getActivity() != null) {
                        ((TournamentSettingsActivity) getActivity()).saveAndUploadOnFirestore();
                    }
                    break;
                case R.id.export_btn:
                    if (getActivity() != null) {
                        ((TournamentSettingsActivity) getActivity()).exportTournament();
                    }
                    break;
            }
        }

        @Override
        public void onDisplayChangeSystem() {
            Timber.d("onDisplayChangeSystem NOT implemented yet");
        }

        @Override
        public void onTitleUpdate(String title) {
            Timber.d("onTitleUpdate NOT implemented yet");
        }

        @Override
        public void onMessage(String message) {
            Timber.d("onMessage NOT implemented yet");
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return GeneralFragment.newInstance();
                case 1:
                    return HandicapFragment.newInstance();
                case 2:
                    return PlacementFragment.newInstance();
                case 3:
                    return PairingFragment.newInstance();
            }

            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

}
