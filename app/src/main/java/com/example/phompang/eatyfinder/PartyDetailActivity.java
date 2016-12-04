package com.example.phompang.eatyfinder;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
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

    @BindView(R.id.date)
    TextView mDate;
    @BindView(R.id.time)
    TextView mTime;
    @BindView(R.id.location)
    TextView mLocation;
    @BindView(R.id.price)
    TextView mPrice;
    @BindView(R.id.pricePerPerson)
    TextView mPricePerPerson;
    @BindView(R.id.people)
    TextView mPeople;
    @BindView(R.id.desc)
    TextView mDesc;
    @BindView(R.id.seeMoreText)
    TextView mSeeMoreText;
    @BindView(R.id.seeMore)
    ImageView mSeeMore;
    @BindView(R.id.attendeeContainer)
    LinearLayout mAttendeeContainer;
    @BindView(R.id.toolbarImg)
    ImageView mImg;
    @BindView(R.id.join)
    FloatingActionButton mJoin;

    private Party mParty;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private FirebaseUtilities mFirebaseUtilities;

    private boolean seeMoreState = false;

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

    @OnClick(R.id.seeMore)
    public void seeMoreClick() {
        if (!seeMoreState) {
            seeMoreState = true;
            mSeeMoreText.setText("See Less");
            mSeeMore.setImageResource(R.drawable.ic_expand_less_black_24dp);
            setAttendee(seeMoreState);
        } else {
            seeMoreState = false;
            mSeeMoreText.setText("See More");
            mSeeMore.setImageResource(R.drawable.ic_expand_more_black_24dp);
            setAttendee(seeMoreState);
        }
    }

    @OnClick(R.id.seeMoreText)
    public void seeMoreTextClick() {
        seeMoreClick();
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

        setAttendee(seeMoreState);
    }

    public void setAttendee(boolean state) {
        if (null != mAttendeeContainer && mAttendeeContainer.getChildCount() > 0) {
            try {
                mAttendeeContainer.removeViews (0, mAttendeeContainer.getChildCount());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int width = DpiUtils.toPixels(40, getResources().getDisplayMetrics());
        int height = DpiUtils.toPixels(40, getResources().getDisplayMetrics());
        if (!state) {
            mAttendeeContainer.setOrientation(LinearLayout.HORIZONTAL);
            for (User u : mParty.getAttendees().values()) {
                CircularImageView cv = new CircularImageView(this);
                cv.setLayoutParams(new LinearLayout.LayoutParams(width, height));
                Glide.with(this).load(u.getPhoto()).into(cv);

                TextView textView = new TextView(this);
                textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                if (u.getPeople() > 1) {
                    textView.setText(String.format("+%d", u.getPeople() - 1));
                } else {
                    textView.setText("");
                }
                textView.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                mAttendeeContainer.addView(cv);
                mAttendeeContainer.addView(textView);
            }
        } else {
            mAttendeeContainer.setOrientation(LinearLayout.VERTICAL);
            for (User u: mParty.getAttendees().values()) {
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                CircularImageView cv = new CircularImageView(this);
                cv.setLayoutParams(new LinearLayout.LayoutParams(width, height));
                Glide.with(this).load(u.getPhoto()).into(cv);


                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER_VERTICAL;

                TextView textView = new TextView(this);
                textView.setLayoutParams(layoutParams);
                textView.setText(u.getDisplayName());
                textView.setTextColor(ContextCompat.getColor(this, R.color.textPrimary));

                TextView textView1 = new TextView(this);
                textView1.setLayoutParams(layoutParams);
                if (u.getPeople() > 1) {
                    textView1.setText(String.format(" with %d friends", u.getPeople() - 1));
                } else {
                    textView1.setText("");
                }

                linearLayout.addView(cv);
                linearLayout.addView(textView);
                linearLayout.addView(textView1);

                mAttendeeContainer.addView(linearLayout);
            }
        }
    }

    @Override
    public void onJoin(String key, int people) {
        mFirebaseUtilities.joinParty(key, people);
        mFirebaseUtilities.updateCurrentPeople(key, people);
    }
}
