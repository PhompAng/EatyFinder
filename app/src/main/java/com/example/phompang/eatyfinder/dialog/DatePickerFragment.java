package com.example.phompang.eatyfinder.dialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import com.example.phompang.eatyfinder.AddActivity;

import java.util.Calendar;

/**
 * Created by phompang on 10/25/2016 AD.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    private OnSetDateListener mListener;

    public interface OnSetDateListener {
        public void setDate(Bundle bundle);
    }

    public static DatePickerFragment newInstance() {
        return new DatePickerFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);
        mListener = (OnSetDateListener) getActivity();

        return new DatePickerDialog(getActivity(), this, year, month, date);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Bundle bundle = new Bundle();
        bundle.putInt("year", year);
        bundle.putInt("month", monthOfYear+1);
        bundle.putInt("date", dayOfMonth);
        if (mListener != null) {
            mListener.setDate(bundle);
        }
    }
}
