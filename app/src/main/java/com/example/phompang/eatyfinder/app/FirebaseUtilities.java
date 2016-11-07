package com.example.phompang.eatyfinder.app;

import android.os.Bundle;
import android.util.Log;

import com.example.phompang.eatyfinder.model.Party;
import com.example.phompang.eatyfinder.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    public void addParty(Party p, User u) {
        DatabaseReference reference = mDatabaseReference.child("parties").push();
        reference.setValue(p);
        reference.child("attendees").push().setValue(u);

    }

    public void addUser(User u) {
        mDatabaseReference.child("users").child(u.getUid()).setValue(u);
    }

    public void joinParty(final String key) {
        mDatabaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);

                mDatabaseReference.child("parties").child(key).child("attendees").push().setValue(u);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Util", "getUser:onCancelled", databaseError.toException());
            }
        });

        mDatabaseReference.child("parties").child(key).child("currentPeople").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long currentPeople = (long) dataSnapshot.getValue();
                currentPeople += 1;
                dataSnapshot.getRef().setValue(currentPeople);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
