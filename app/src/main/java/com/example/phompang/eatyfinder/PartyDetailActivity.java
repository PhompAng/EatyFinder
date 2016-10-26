package com.example.phompang.eatyfinder;

import android.content.Context;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.phompang.eatyfinder.model.Party;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    @BindView(R.id.detailToolbarImg)
    ImageView mImg;

    private Party mParty;
    private StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_detail);
        ButterKnife.bind(this);

        mParty = (Party) getIntent().getSerializableExtra("party");

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setTitle(mParty.getTitle());

        Toolbar toolbar = (Toolbar) findViewById(R.id.detailToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStorageReference = FirebaseStorage.getInstance().getReference();

        setData();
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
    }
}
