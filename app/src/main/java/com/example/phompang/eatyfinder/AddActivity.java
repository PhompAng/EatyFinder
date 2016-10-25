package com.example.phompang.eatyfinder;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.phompang.eatyfinder.dialog.DatePickerFragment;
import com.example.phompang.eatyfinder.dialog.TimePickerFragment;
import com.example.phompang.eatyfinder.model.Datetime;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddActivity extends AppCompatActivity implements DatePickerFragment.OnSetDateListener, TimePickerFragment.OnSetTimeListener {

    @BindView(R.id.addTitle)
    EditText mTitle;
    @BindView(R.id.addImg)
    ImageView mImg;
    @BindView(R.id.addDesc)
    EditText mDesc;
    @BindView(R.id.addDate)
    Spinner mDate;
    @BindView(R.id.addTime)
    Spinner mTime;
    @BindView(R.id.currentPeople)
    EditText mCurrentPeople;
    @BindView(R.id.requiredPeople)
    EditText mRequiredPeople;
    @BindView(R.id.addPrice)
    EditText mPrice;
    @BindView(R.id.addLocation)
    EditText mLocation;

    private ArrayAdapter<String> dateAdapter;
    private ArrayAdapter<String> timeAdapter;
    private Datetime datetime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add New Party");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        datetime = new Datetime();

        dateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, datetime.date_data);
        mDate.setAdapter(dateAdapter);
        timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, datetime.time_data);
        mTime.setAdapter(timeAdapter);

        mDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == datetime.date_data.size() - 1) {
                    DialogFragment dateFragment = DatePickerFragment.newInstance();
                    dateFragment.show(getSupportFragmentManager(), "date");
                } else {
                    datetime.pickDate(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == datetime.time_data.size() - 1) {
                    DialogFragment timeFragment = TimePickerFragment.newInstance();
                    timeFragment.show(getSupportFragmentManager(), "timepicker");
                } else {
                    datetime.pickTime(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_add_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_add:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addParty() {
        String title = mTitle.getText().toString();
        String desc = mDesc.getText().toString();
    }

    @Override
    public void setDate(Bundle bundle) {
        String year = Integer.toString(bundle.getInt("year", 0));
        String month = String.format("%02d", bundle.getInt("month", 0));
        String date = String.format("%02d", bundle.getInt("date", 0));
        datetime.date_data.set(datetime.date_data.size() - 1, year + "-" + month + "-" + date);
        dateAdapter.notifyDataSetChanged();
        datetime.setDate(year + "-" + month + "-" + date);
    }

    @Override
    public void setTime(Bundle bundle) {
        String hour = String.format("%02d", bundle.getInt("hour", 0));
        String minute = String.format("%02d", bundle.getInt("minute", 0));
        datetime.time_data.set(datetime.time_data.size() - 1, hour + ":" + minute);
        timeAdapter.notifyDataSetChanged();
        datetime.setTime(hour + ":" + minute + ":00");
    }
}
