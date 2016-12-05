package com.example.phompang.eatyfinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.phompang.eatyfinder.app.DpiUtils;
import com.example.phompang.eatyfinder.app.FirebaseUtilities;
import com.example.phompang.eatyfinder.dialog.PeoplePickerDialog;
import com.example.phompang.eatyfinder.model.Comment;
import com.example.phompang.eatyfinder.model.Party;
import com.example.phompang.eatyfinder.model.User;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PartyDetailActivity extends AppCompatActivity implements PeoplePickerDialog.OnJoinListener {

    public static final int PARTY_EDIT = 1000;

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
    @BindView(R.id.commentContainer)
    LinearLayout mCommentContainer;
    @BindView(R.id.commentEditText)
    EditText mCommentEditText;
    @BindView(R.id.toolbarImg)
    ImageView mImg;
    @BindView(R.id.join)
    FloatingActionButton mJoin;

    private Party mParty;
    private List<Comment> mComments;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private FirebaseUtilities mFirebaseUtilities;

    private DatabaseReference commentRef;
    private ValueEventListener commentListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mComments.clear();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                mComments.add(snapshot.getValue(Comment.class));
            }
            setComment();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    private DatabaseReference partiesRef;
    private ValueEventListener partiesListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mParty = dataSnapshot.getValue(Party.class);
            setData();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private String uid;
    private String key;
    private boolean seeMoreState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_detail);
        ButterKnife.bind(this);

        mParty = (Party) getIntent().getSerializableExtra("party");
        key = getIntent().getStringExtra("key");
        mComments = new ArrayList<>();

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mFirebaseUtilities = FirebaseUtilities.newInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        partiesRef = mDatabaseReference.child("parties").child(key);
        partiesRef.addValueEventListener(partiesListener);
        commentRef = mDatabaseReference.child("comments").child(key);
        commentRef.addValueEventListener(commentListener);

        setData();

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (mParty.getOwner().equals(uid)) {
            mJoin.setImageResource(R.drawable.ic_mode_edit_white_24dp);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PARTY_EDIT && resultCode == Activity.RESULT_OK) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        partiesRef.removeEventListener(partiesListener);
        commentRef.removeEventListener(commentListener);
    }

    @OnClick(R.id.join)
    public void join() {
        if (mParty.getOwner().equals(uid)) {
            Intent intent = new Intent(PartyDetailActivity.this, AddActivity.class);
            intent.putExtra("party", mParty);
            intent.putExtra("key", key);
            startActivityForResult(intent, PARTY_EDIT);
        } else {
            int maxPeople = mParty.getRequiredPeople() - mParty.getCurrentPeople();
            DialogFragment dialogFragment = PeoplePickerDialog.newInstance(key, maxPeople);
            dialogFragment.show(getSupportFragmentManager(), "people");
        }
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

    @OnClick(R.id.commentAdd)
    public void addComment() {
        String comment = mCommentEditText.getText().toString();
        if (!TextUtils.isEmpty(comment)) {
            Comment c = new Comment();
            c.setComment(comment);
            mFirebaseUtilities.addComment(key, c);
            mCommentEditText.setText("");
        }
    }

    private void setData() {
        collapsingToolbarLayout.setTitle(mParty.getTitle());
        Glide.with(this).using(new FirebaseImageLoader()).load(mStorageReference.child("photos/" + mParty.getPhoto())).centerCrop().into(mImg);
        mDate.setText(mParty.getDate());
        mTime.setText(mParty.getTime());
        mLocation.setText(mParty.getLocation());
        mPrice.setText(Double.toString(mParty.getPrice()));
        mPricePerPerson.setText(Double.toString(mParty.getPricePerPerson()));
        mPeople.setText(mParty.getCurrentPeople() + "/" + mParty.getRequiredPeople() + " คน");
        mDesc.setText(mParty.getDesc());

        setAttendee(seeMoreState);
        setComment();
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
            for (final User u : mParty.getAttendees().values()) {
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(PartyDetailActivity.this, ProfileActivity.class);
                        intent.putExtra("uid", u.getUid());
                        startActivity(intent);
                    }
                };
                CircularImageView cv = new CircularImageView(this);
                cv.setLayoutParams(new LinearLayout.LayoutParams(width, height));
                cv.setOnClickListener(clickListener);
                Glide.with(this).load(u.getPhoto()).into(cv);

                TextView textView = new TextView(this);
                textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                if (u.getPeople() > 1) {
                    textView.setText(String.format("+%d", u.getPeople() - 1));
                } else {
                    textView.setText("");
                }
                textView.setOnClickListener(clickListener);
                textView.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                mAttendeeContainer.addView(cv);
                mAttendeeContainer.addView(textView);
            }
        } else {
            mAttendeeContainer.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER_VERTICAL;
            for (final User u: mParty.getAttendees().values()) {
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(PartyDetailActivity.this, ProfileActivity.class);
                        intent.putExtra("uid", u.getUid());
                        startActivity(intent);
                    }
                };
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setOnClickListener(clickListener);

                CircularImageView cv = new CircularImageView(this);
                cv.setLayoutParams(new LinearLayout.LayoutParams(width, height));
                Glide.with(this).load(u.getPhoto()).into(cv);


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

    public void setComment() {
        if (null != mCommentContainer && mCommentContainer.getChildCount() > 0) {
            try {
                mCommentContainer.removeViews (0, mCommentContainer.getChildCount());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int width = DpiUtils.toPixels(40, getResources().getDisplayMetrics());
        int height = DpiUtils.toPixels(40, getResources().getDisplayMetrics());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;

        LinearLayout.LayoutParams commentLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        commentLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        for (final Comment c: mComments) {
            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PartyDetailActivity.this, ProfileActivity.class);
                    intent.putExtra("uid", c.getUser().getUid());
                    startActivity(intent);
                }
            };

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setOnClickListener(clickListener);

            CircularImageView cv = new CircularImageView(this);
            cv.setLayoutParams(new LinearLayout.LayoutParams(width, height));
            Glide.with(this).load(c.getUser().getPhoto()).into(cv);

            TextView textView = new TextView(this);
            textView.setLayoutParams(layoutParams);
            textView.setText(c.getUser().getDisplayName());
            textView.setTextColor(ContextCompat.getColor(this, R.color.textPrimary));

            LinearLayout commentLinearLayout = new LinearLayout(this);
            commentLinearLayout.setOrientation(LinearLayout.VERTICAL);
            TextView comment = new TextView(this);
            commentLayoutParams.setMarginStart(width);
            comment.setLayoutParams(commentLayoutParams);
            comment.setText(c.getComment());
            commentLinearLayout.addView(comment);

            linearLayout.addView(cv);
            linearLayout.addView(textView);

            mCommentContainer.addView(linearLayout);
            mCommentContainer.addView(commentLinearLayout);
        }
    }

    @Override
    public void onJoin(String key, int people) {
        mFirebaseUtilities.joinParty(key, people);
        mFirebaseUtilities.updateCurrentPeople(key, people);
    }
}
