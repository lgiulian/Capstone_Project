package com.crilu.gothandroid;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CreateTournamentActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText mBeginDate;
    private EditText mEndDate;
    private int mCurrentDatePickerFieldId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tournament);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBeginDate = findViewById(R.id.tournament_begin_date);
        mEndDate = findViewById(R.id.tournament_end_date);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar cal = new GregorianCalendar(year, month, dayOfMonth);
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        String dateStr = dateFormat.format(cal.getTime());
        if (mCurrentDatePickerFieldId == R.id.begin_date_picker_btn) {
            mBeginDate.setText(dateStr);
        } else if (mCurrentDatePickerFieldId == R.id.end_date_picker_btn) {
            mEndDate.setText(dateStr);
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

