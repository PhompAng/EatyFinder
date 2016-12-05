package com.example.phompang.eatyfinder.app;

import android.util.Log;

import com.example.phompang.eatyfinder.Interface.FCMInteface;
import com.example.phompang.eatyfinder.model.Comment;
import com.example.phompang.eatyfinder.model.Notification;
import com.example.phompang.eatyfinder.model.NotificationBody;
import com.example.phompang.eatyfinder.model.Party;
import com.example.phompang.eatyfinder.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by phompang on 10/25/2016 AD.
 */

public class FirebaseUtilities {

    private static FirebaseUtilities sFirebaseUtilities;
    private DatabaseReference mDatabaseReference;

    private FirebaseUtilities() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public static FirebaseUtilities newInstance() {
        if (sFirebaseUtilities != null) {
            return sFirebaseUtilities;
        }
        sFirebaseUtilities = new FirebaseUtilities();
        return sFirebaseUtilities;
    }

    public void addParty(Party p) {
        DatabaseReference reference = mDatabaseReference.child("parties").push();
        reference.setValue(p);
        joinParty(reference.getKey(), p.getCurrentPeople());
    }

    public void updateParty(String key, Party p, int currentPeople) {
        DatabaseReference reference = mDatabaseReference.child("parties").child(key);
        reference.setValue(p);
        joinParty(key, currentPeople);
    }

    public void addUser(User u) {
        mDatabaseReference.child("users").child(u.getUid()).setValue(u);
    }

    public void addComment(final String key, final Comment comment) {
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference reference = mDatabaseReference.child("comments").child(key).push();
        mDatabaseReference.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                comment.setUser(u);
                reference.setValue(comment);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void joinParty(final String key, final int people) {
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseReference.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference attendees = mDatabaseReference.child("parties").child(key).child("attendees").child(uid);
                User u = dataSnapshot.getValue(User.class);
                u.setToken(FirebaseInstanceId.getInstance().getToken());

                attendees.setValue(u);
                attendees.child("people").setValue(people);

                sendNoti(key, u);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Util", "getUser:onCancelled", databaseError.toException());
            }
        });

    }

    private void sendNoti(String key, final User u) {
        mDatabaseReference.child("parties").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Party p = dataSnapshot.getValue(Party.class);
                String uid = p.getOwner();

                mDatabaseReference.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("https://fcm.googleapis.com")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();;
                        FCMInteface fcmInteface = retrofit.create(FCMInteface.class);

                        Notification notification = new Notification();
                        NotificationBody body = new NotificationBody();
                        body.setBody(u.getDisplayName() + " join your party");
                        body.setTitle("Eaty Finder");
                        notification.setTo(user.getToken());
                        notification.setNotification(body);
                        Call<ResponseBody> call = fcmInteface.sendNoti(notification);
                        Log.d("noti", notification.getTo());
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void success(Result<ResponseBody> result) {
                                Log.d("sendNoti", "success");
                            }

                            @Override
                            public void failure(TwitterException exception) {
                                Log.e("sendNoti", "fail");
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateCurrentPeople(String key, final int people) {
        mDatabaseReference.child("parties").child(key).child("currentPeople").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long currentPeople = (long) dataSnapshot.getValue();
                currentPeople += people;
                dataSnapshot.getRef().setValue(currentPeople);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
