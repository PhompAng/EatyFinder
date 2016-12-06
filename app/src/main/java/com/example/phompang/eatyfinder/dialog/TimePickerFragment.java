package com.example.phompang.eatyfinder.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

import com.example.phompang.eatyfinder.AddActivity;

import java.util.Calendar;

/**
 * Created by phompang on 10/25/2016 AD.
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private OnSetTimeListener mListener;

    public interface OnSetTimeListener {
        public void setTime(Bundle bundle);
    }

    public static TimePickerFragment newInstance() {
        return new TimePickerFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        mListener = (OnSetTimeListener) getActivity();

        return new TimePickerDialog(getContext(), this, hour, min, true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Bundle bundle = new Bundle();
        bundle.putInt("hour", hourOfDay);
        bundle.putInt("minute", minute);
        if (mListener != null) {
            mListener.setTime(bundle);
        }
    }
}
