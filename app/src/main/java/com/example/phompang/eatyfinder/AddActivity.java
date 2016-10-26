package com.example.phompang.eatyfinder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.phompang.eatyfinder.app.FirebaseUtilities;
import com.example.phompang.eatyfinder.dialog.DatePickerFragment;
import com.example.phompang.eatyfinder.dialog.TimePickerFragment;
import com.example.phompang.eatyfinder.model.Datetime;
import com.example.phompang.eatyfinder.model.Party;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddActivity extends AppCompatActivity implements DatePickerFragment.OnSetDateListener, TimePickerFragment.OnSetTimeListener {

    public static final int RESULT_LOAD_IMAGE = 3;

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
    private Uri selectedImage;
    private FirebaseUtilities mFirebaseUtilities;
    private StorageReference folderRef;

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
        mFirebaseUtilities = FirebaseUtilities.newInstance();
        folderRef = FirebaseStorage.getInstance().getReference();

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

        mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
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
                validate();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case RESULT_LOAD_IMAGE:
                if (resultCode == Activity.RESULT_OK && null != intent) {
                    selectedImage = intent.getData();
                    mImg.setPadding(0,0,0,0);
                    Glide.with(this).loadFromMediaStore(selectedImage).centerCrop().into(mImg);
                }
        }
    }

    private void uploadFromFile(Uri file, String uid) {
        StorageReference imageRef = folderRef.child("photos/" + uid);
        UploadTask mUploadTask = imageRef.putFile(file);

        mUploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), String.format("Failure: %s", exception.getMessage()), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                //Toast.makeText(getApplicationContext(), taskSnapshot.getDownloadUrl().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validate() {
        mTitle.setError(null);
        mDesc.setError(null);
        mPrice.setError(null);
        mCurrentPeople.setError(null);
        mRequiredPeople.setError(null);
        mLocation.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mLocation.getText().toString())) {
            mLocation.setError(getString(R.string.error_field_required));
            focusView = mLocation;
            cancel = true;
        }
        if (TextUtils.isEmpty((mPrice.getText().toString()))) {
            mPrice.setError(getString(R.string.error_field_required));
            focusView = mPrice;
            cancel = true;
        }
        if (TextUtils.isEmpty(mRequiredPeople.getText().toString())) {
            mRequiredPeople.setError(getString(R.string.error_field_required));
            focusView = mRequiredPeople;
            cancel = true;
        }
        if (TextUtils.isEmpty(mCurrentPeople.getText().toString())) {
            mCurrentPeople.setError(getString(R.string.error_field_required));
            focusView = mCurrentPeople;
            cancel = true;
        }
        if (TextUtils.isEmpty(mDesc.getText().toString())) {
            mDesc.setError(getString(R.string.error_field_required));
            focusView = mDesc;
            cancel = true;
        }
        if (TextUtils.isEmpty(mTitle.getText().toString())) {
            mTitle.setError(getString(R.string.error_field_required));
            focusView = mTitle;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
//            showProgress(true);
            String title = mTitle.getText().toString();
            String desc = mDesc.getText().toString();
            String date = datetime.getDate();
            String time = datetime.getTime();
            int currentPeople = Integer.parseInt(mCurrentPeople.getText().toString());
            int requiredPeople = Integer.parseInt(mRequiredPeople.getText().toString());
            double price = Double.parseDouble(mPrice.getText().toString());
            String location = mLocation.getText().toString();

            String uid = UUID.randomUUID().toString();
            uploadFromFile(selectedImage, uid);

            Party p = new Party();
            p.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
            p.setTitle(title);
            p.setDesc(desc);
            p.setDate(date);
            p.setTime(time);
            p.setCurrentPeople(currentPeople);
            p.setRequiredPeople(requiredPeople);
            p.setPrice(price);
            p.setLocation(location);
            p.setPhoto(uid);

            mFirebaseUtilities.addParty(p);
            finish();
        }
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
