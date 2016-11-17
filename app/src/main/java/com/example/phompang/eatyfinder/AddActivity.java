package com.example.phompang.eatyfinder;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.example.phompang.eatyfinder.adapter.VenueAdapter;
import com.example.phompang.eatyfinder.app.FirebaseUtilities;
import com.example.phompang.eatyfinder.app.FoursquareUtils;
import com.example.phompang.eatyfinder.dialog.DatePickerFragment;
import com.example.phompang.eatyfinder.dialog.TimePickerFragment;
import com.example.phompang.eatyfinder.model.Datetime;
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

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.VenuesSearchResult;

public class AddActivity extends AppCompatActivity implements DatePickerFragment.OnSetDateListener, TimePickerFragment.OnSetTimeListener, GoogleApiClient.OnConnectionFailedListener, ConnectionCallbacks, LocationListener {

    public static final int RESULT_LOAD_IMAGE = 3;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

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
    private Datetime datetime;
    private Uri selectedImage;
    private FirebaseUtilities mFirebaseUtilities;
    private StorageReference folderRef;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

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

        try {
            CompactVenue[] venues = FoursquareUtils.venuesSearch("13.7294079,100.7830827");
            VenueAdapter venueAdapter = new VenueAdapter(this, R.layout.support_simple_spinner_dropdown_item, venues);
            mLocation.setThreshold(1);
            mLocation.setAdapter(venueAdapter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }

    @OnClick(R.id.addGetLocation)
    public void getLocation() {
        if (mLastLocation != null) {
            Log.d("lattt", String.valueOf(mLastLocation.getLatitude()));
            Log.d("Longgg", String.valueOf(mLastLocation.getLongitude()));
        } else {
            Log.d("aaa", "aaa");
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

            final Party p = new Party();
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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
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
        Log.d("location", location.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        Log.d("currentLocation", location.toString());
    }
}
