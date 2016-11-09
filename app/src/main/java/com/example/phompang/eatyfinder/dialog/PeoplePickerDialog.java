package com.example.phompang.eatyfinder.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.example.phompang.eatyfinder.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by phompang on 11/8/2016 AD.
 */

public class PeoplePickerDialog extends DialogFragment {
    public static final String ARG_PEOPLE = "maxPeople";

    private int maxPeople;

    public static DialogFragment newInstance(int maxPeople) {
        PeoplePickerDialog dialog = new PeoplePickerDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_PEOPLE, maxPeople);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            maxPeople = getArguments().getInt(ARG_PEOPLE, 0);
        }
    }

    @BindView(R.id.dialogPeople)
    NumberPicker numberPicker;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_pick_people, null);
        ButterKnife.bind(this, v);

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(maxPeople);
        numberPicker.setWrapSelectorWheel(true);

        builder.setTitle("Select Number of people")
//            .setMessage("testtest")
            .setView(v)
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            })
            .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    PeoplePickerDialog.this.getDialog().cancel();
                }
            });

        return builder.create();
    }
}
