package com.example.phompang.eatyfinder;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.phompang.eatyfinder.app.FirebaseUtilities;
import com.example.phompang.eatyfinder.app.FoursquareUtils;
import com.example.phompang.eatyfinder.dialog.DatePickerFragment;
import com.example.phompang.eatyfinder.dialog.TimePickerFragment;
import com.example.phompang.eatyfinder.model.Datetime;
import com.example.phompang.eatyfinder.model.MyCompactVenue;
import com.example.phompang.eatyfinder.model.Party;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddActivity extends AppCompatActivity implements DatePickerFragment.OnSetDateListener, TimePickerFragment.OnSetTimeListener, GoogleApiClient.OnConnectionFailedListener, ConnectionCallbacks, LocationListener {

    public static final int RESULT_LOAD_IMAGE = 3;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 8000;

    @BindView(R.id.addTitle)
    EditText mTitle;
    @BindView(R.id.addImg)
    ImageView mImg;
    @BindView(R.id.addImgFab)
    FloatingActionButton mImgFab;
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
    @BindView(R.id.addPricePerPerson)
    EditText mPricePerPerson;
    @BindView(R.id.addLocation)
    AutoCompleteTextView mLocation;

    private ArrayAdapter<String> dateAdapter;
    private ArrayAdapter<String> timeAdapter;
    private ArrayAdapter<MyCompactVenue> mVenuesAdapter;
    private Datetime datetime;
    private Uri selectedImage;
    private FirebaseUtilities mFirebaseUtilities;
    private StorageReference folderRef;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    private List<MyCompactVenue> mVenues;
    private MyCompactVenue mCompactVenue;

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
        mVenues = new ArrayList<>();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

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

        mImgFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        mRequiredPeople.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(mPrice.getText().toString()) && !TextUtils.isEmpty(mRequiredPeople.getText().toString())) {
                    Double price = Double.parseDouble(mPrice.getText().toString());
                    int people = Integer.parseInt(mRequiredPeople.getText().toString());
                    mPricePerPerson.setText(Double.toString(price / people));
                }
            }
        });

        mPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(mPrice.getText().toString()) && !TextUtils.isEmpty(mRequiredPeople.getText().toString())) {
                    Double price = Double.parseDouble(mPrice.getText().toString());
                    int people = Integer.parseInt(mRequiredPeople.getText().toString());
                    mPricePerPerson.setText(Double.toString(price / people));
                }
            }
        });

        mVenuesAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, mVenues);
        mLocation.setThreshold(1);
        mVenuesAdapter.setNotifyOnChange(true);
        mLocation.setAdapter(mVenuesAdapter);

        mLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mCompactVenue = (MyCompactVenue) adapterView.getItemAtPosition(i);
            }
        });
    }

    @OnClick(R.id.addGetLocation)
    public void getLocation() {
        if (mLastLocation != null) {
            handleNewLocation(mLastLocation);
        }
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
            //TODO check if required less than current
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
        if (selectedImage == null) {
            Toast.makeText(getApplicationContext(), "Please select image", Toast.LENGTH_SHORT).show();
            focusView = mTitle;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            addParty();
        }
    }

    private void addParty() {
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
        p.setOwner(FirebaseAuth.getInstance().getCurrentUser().getUid());
        p.setTitle(title);
        p.setDesc(desc);
        p.setDate(date);
        p.setTime(time);
        p.setCurrentPeople(currentPeople);
        p.setRequiredPeople(requiredPeople);
        p.setPrice(price);
        p.setPricePerPerson(price/requiredPeople);
        p.setLocation(location);
        p.setPhoto(uid);
        p.setCategory(mCompactVenue.getCategories()[0]);
        mFirebaseUtilities.addParty(p);
        finish();
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

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                Log.d("permission", grantResults[0] + "");
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    onConnected(null);
                } else {
                    Toast.makeText(this, "Need Location Permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("fail", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(mLastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void handleNewLocation(Location location) {
        Log.d("newLocation", location.toString());
        String ll = String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude());
        try {
            mVenues = FoursquareUtils.venuesSearch(ll);
            mVenuesAdapter.clear();
            mVenuesAdapter.addAll(mVenues);
            mVenuesAdapter.notifyDataSetChanged();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        handleNewLocation(location);
        Log.d("currentLocation", location.toString());
    }
}
