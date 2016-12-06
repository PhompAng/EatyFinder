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
import com.example.phompang.eatyfinder.app.FirebaseUtilities;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by phompang on 11/8/2016 AD.
 */

public class PeoplePickerDialog extends DialogFragment {
    public static final String ARG_KEY = "key";
    public static final String ARG_PEOPLE = "maxPeople";

    private String key;
    private int maxPeople;
    private FirebaseUtilities mFirebaseUtilities;

    private OnJoinListener listener;

    public interface OnJoinListener {
        void onJoin(String key, int people);
    }

    public static DialogFragment newInstance(String key, int maxPeople) {
        PeoplePickerDialog dialog = new PeoplePickerDialog();
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);
        args.putInt(ARG_PEOPLE, maxPeople);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            key = getArguments().getString(ARG_KEY);
            maxPeople = getArguments().getInt(ARG_PEOPLE, 0);
        }

        mFirebaseUtilities = FirebaseUtilities.newInstance();
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
        listener = (OnJoinListener) getActivity();

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(maxPeople);
        numberPicker.setWrapSelectorWheel(true);

        builder.setTitle("Select Number of people")
            .setView(v)
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    PeoplePickerDialog.this.getDialog().cancel();
                }
            })
            .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (listener != null) {
                        listener.onJoin(key, numberPicker.getValue());
                    }
                }
            });

        return builder.create();
    }
}
