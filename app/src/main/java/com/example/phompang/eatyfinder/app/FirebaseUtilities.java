package com.example.phompang.eatyfinder.app;

import android.util.Log;

import com.example.phompang.eatyfinder.model.Party;
import com.example.phompang.eatyfinder.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    public void addUser(User u) {
        mDatabaseReference.child("users").child(u.getUid()).setValue(u);
    }

    public void joinParty(final String key, final int people) {
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseReference.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference attendees = mDatabaseReference.child("parties").child(key).child("attendees").child(uid);
                User u = dataSnapshot.getValue(User.class);

                attendees.setValue(u);
                attendees.child("people").setValue(people);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Util", "getUser:onCancelled", databaseError.toException());
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
