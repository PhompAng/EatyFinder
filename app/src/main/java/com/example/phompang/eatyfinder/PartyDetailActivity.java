package com.example.phompang.eatyfinder;

import android.content.Context;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.phompang.eatyfinder.app.FirebaseUtilities;
import com.example.phompang.eatyfinder.model.Party;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PartyDetailActivity extends AppCompatActivity {

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
    @BindView(R.id.toolbarImg)
    ImageView mImg;
    @BindView(R.id.join)
    FloatingActionButton mJoin;

    private Party mParty;
    private StorageReference mStorageReference;
    private FirebaseUtilities mFirebaseUtilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_detail);
        ButterKnife.bind(this);

        mParty = (Party) getIntent().getSerializableExtra("party");
        Log.d("key", getIntent().getStringExtra("key"));

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setTitle(mParty.getTitle());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mFirebaseUtilities = FirebaseUtilities.newInstance();

        setData();

        if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(mParty.getOwner())) {
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
        mFirebaseUtilities.joinParty(getIntent().getStringExtra("key"));
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
        Log.d("attendees", mParty.getAttendees().size() + "");
    }
}
