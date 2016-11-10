package com.example.phompang.eatyfinder;

import android.content.Context;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.phompang.eatyfinder.app.DpiUtils;
import com.example.phompang.eatyfinder.app.FirebaseUtilities;
import com.example.phompang.eatyfinder.dialog.PeoplePickerDialog;
import com.example.phompang.eatyfinder.model.Party;
import com.example.phompang.eatyfinder.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PartyDetailActivity extends AppCompatActivity implements PeoplePickerDialog.OnJoinListener {

    private CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.detailDate)
    TextView mDate;
    @BindView(R.id.detailTime)
    TextView mTime;
    @BindView(R.id.detailLocation)
    TextView mLocation;
    @BindView(R.id.detailPrice)
    TextView mPrice;
    @BindView(R.id.detailPricePerPerson)
    TextView mPricePerPerson;
    @BindView(R.id.detailPeople)
    TextView mPeople;
    @BindView(R.id.detailDesc)
    TextView mDesc;
    @BindView(R.id.detailAttendeeContainer)
    LinearLayout mDetailAttendeeContainer;
    @BindView(R.id.toolbarImg)
    ImageView mImg;
    @BindView(R.id.join)
    FloatingActionButton mJoin;

    private Party mParty;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private FirebaseUtilities mFirebaseUtilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_detail);
        ButterKnife.bind(this);

        mParty = (Party) getIntent().getSerializableExtra("party");

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setTitle(mParty.getTitle());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mFirebaseUtilities = FirebaseUtilities.newInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mDatabaseReference.child("parties").child(getIntent().getStringExtra("key")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mParty = dataSnapshot.getValue(Party.class);
                setData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        setData();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (mParty.getAttendees().containsKey(uid)) {
            mJoin.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.join)
    public void join() {
        int maxPeople = mParty.getRequiredPeople() - mParty.getCurrentPeople();
        DialogFragment dialogFragment = PeoplePickerDialog.newInstance(getIntent().getStringExtra("key"), maxPeople);
        dialogFragment.show(getSupportFragmentManager(), "people");
    }

    private void setData() {
        //Glide.with(this).load(mParty.getPhoto()).centerCrop().into(mImg);
        mStorageReference.child("photos/" + mParty.getPhoto()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Context ctx = getApplicationContext();
                if (ctx != null) {
                    Glide.with(ctx).load(uri).centerCrop().into(mImg);
                }
            }
        });
        mDate.setText(mParty.getDate());
        mTime.setText(mParty.getTime());
        mLocation.setText(mParty.getLocation());
        mPrice.setText(Double.toString(mParty.getPrice()));
        mPricePerPerson.setText(Double.toString(mParty.getPricePerPerson()));
        mPeople.setText(mParty.getCurrentPeople() + "/" + mParty.getRequiredPeople() + " คน");
        mDesc.setText(mParty.getDesc());
        if (null != mDetailAttendeeContainer && mDetailAttendeeContainer.getChildCount() > 0) {
            try {
                mDetailAttendeeContainer.removeViews (0, mDetailAttendeeContainer.getChildCount());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (User u: mParty.getAttendees().values()) {
            int width = DpiUtils.toPixels(40, getResources().getDisplayMetrics());
            int height = DpiUtils.toPixels(40, getResources().getDisplayMetrics());
            CircularImageView cv = new CircularImageView(this);
            cv.setLayoutParams(new LinearLayout.LayoutParams(width, height));
            Glide.with(this).load(u.getPhoto()).into(cv);

            TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            if (u.getPeople() > 1) {
                textView.setText(String.format("+%d", u.getPeople()-1));
            } else {
                textView.setText("");
            }
            textView.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
            mDetailAttendeeContainer.addView(cv);
            mDetailAttendeeContainer.addView(textView);
        }
    }

    @Override
    public void onJoin(String key, int people) {
        mFirebaseUtilities.joinParty(key, people);
        mFirebaseUtilities.updateCurrentPeople(key, people);
    }
}
