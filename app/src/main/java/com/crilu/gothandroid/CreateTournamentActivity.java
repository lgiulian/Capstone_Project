package com.crilu.gothandroid;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;

import com.crilu.gothandroid.databinding.ActivityCreateTournamentBinding;
import com.crilu.opengotha.model.GothaModel;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CreateTournamentActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private int mCurrentDatePickerFieldId;

    private ActivityCreateTournamentBinding mBinding;
    private final static int DEFAULT_NUMBER_OF_ROUNDS_SPINNER_INDEX = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_tournament);

        //setContentView(R.layout.activity_create_tournament);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mBinding.toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initComponents();

        GothaModel gothaModel = new GothaModel();
        //gothaModel.startTournament();
    }

    private void initComponents() {
        mBinding.form.numberOfRoundsSpinner.setSelection(DEFAULT_NUMBER_OF_ROUNDS_SPINNER_INDEX);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar cal = new GregorianCalendar(year, month, dayOfMonth);
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        String dateStr = dateFormat.format(cal.getTime());
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

    public static class DatePickerFragment extends DialogFragment {

        private DatePickerDialog.OnDateSetListener mCallback;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getContext(), mCallback, year, month, day);
        }

        public void setCallback(DatePickerDialog.OnDateSetListener mCallback) {
            this.mCallback = mCallback;
        }
    }
}

