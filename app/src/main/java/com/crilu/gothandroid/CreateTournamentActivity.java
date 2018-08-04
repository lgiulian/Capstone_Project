package com.crilu.gothandroid;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;

import com.crilu.gothandroid.data.GothaContract;
import com.crilu.gothandroid.databinding.ActivityCreateTournamentBinding;
import com.crilu.gothandroid.sync.GothaSyncUtils;
import com.crilu.gothandroid.utils.FileUtils;
import com.crilu.opengotha.ExternalDocument;
import com.crilu.opengotha.TournamentInterface;
import com.crilu.opengotha.TournamentParameterSet;
import com.crilu.opengotha.model.GothaModel;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import timber.log.Timber;

public class CreateTournamentActivity extends AppCompatAdActivity implements DatePickerDialog.OnDateSetListener {

    private int mCurrentDatePickerFieldId;

    private ActivityCreateTournamentBinding mBinding;
    private final static int DEFAULT_NUMBER_OF_ROUNDS_SPINNER_INDEX = 4;
    private int mSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_tournament);

        setSupportActionBar(mBinding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initComponents();

    }

    private void initComponents() {
        mBinding.form.numberOfRoundsSpinner.setSelection(DEFAULT_NUMBER_OF_ROUNDS_SPINNER_INDEX);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar cal = new GregorianCalendar(year, month, dayOfMonth);
        String dateStr = GothandroidApplication.dateFormatPretty.format(cal.getTime());
        if (mCurrentDatePickerFieldId == R.id.begin_date_picker_btn) {
            mBinding.form.tournamentBeginDate.setText(dateStr);
        } else if (mCurrentDatePickerFieldId == R.id.end_date_picker_btn) {
            mBinding.form.tournamentEndDate.setText(dateStr);
        }

    }

    public void showDatePickerDialog(View view) {
        mCurrentDatePickerFieldId = view.getId();
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setCallback(this);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void createTournament(View view) {
        GothaModel gothaModel = GothandroidApplication.getGothaModelInstance();
        Date beginDate = null;
        Date endDate = null;
        try {
            beginDate = GothandroidApplication.dateFormatPretty.parse(mBinding.form.tournamentBeginDate.getText().toString());
        } catch (ParseException e) {
            Timber.d(e);
        }
        try {
            endDate = GothandroidApplication.dateFormatPretty.parse(mBinding.form.tournamentEndDate.getText().toString());
        } catch (ParseException e) {
            Timber.d(e);
        }
        int noRounds = Integer.valueOf(mBinding.form.numberOfRoundsSpinner.getSelectedItem().toString());
        gothaModel.startTournament(mBinding.form.tournamentName.getText().toString(),
                mBinding.form.tournamentShortName.getText().toString(),
                mBinding.form.tournamentLocation.getText().toString(),
                mBinding.form.tournamentDirector.getText().toString(),
                beginDate,
                endDate,
                noRounds,
                1,
                mSystem);

        TournamentInterface tournament = GothandroidApplication.getGothaModelInstance().getTournament();
        saveTournament(tournament);

        displayAds();
        finish();
    }

    private void saveTournament(TournamentInterface tournament) {
        String filename = tournament.getFullName() + ".xml";
        File file = new File(getFilesDir(), filename);
        ExternalDocument.generateXMLFile(tournament, file);
        try {
            String tournamentContent = FileUtils.getFileContents(file);
            ContentValues cv = GothaSyncUtils.getGothaTournamentContentValues(tournament, tournamentContent);
            ContentResolver gothaContentResolver = getContentResolver();
            gothaContentResolver.insert(
                    GothaContract.TournamentEntry.CONTENT_URI,
                    cv);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onSystemButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.radio_mcmahon:
                mSystem = TournamentParameterSet.TYPE_MCMAHON;
                break;
            case R.id.radio_swiss:
                mSystem = TournamentParameterSet.TYPE_SWISS;
                break;
            case R.id.radio_swiss_categories:
                mSystem = TournamentParameterSet.TYPE_SWISSCAT;
                break;

        }
    }

    public static class DatePickerFragment extends DialogFragment {

        private DatePickerDialog.OnDateSetListener mCallback;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(requireContext(), mCallback, year, month, day);
        }

        public void setCallback(DatePickerDialog.OnDateSetListener mCallback) {
            this.mCallback = mCallback;
        }
    }
}

