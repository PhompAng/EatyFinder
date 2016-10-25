package com.example.phompang.eatyfinder.app;

import android.os.Bundle;

import com.example.phompang.eatyfinder.model.Party;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by phompang on 10/25/2016 AD.
 */

public class FirebaseUtilities {

    private static FirebaseUtilities mFirebaseUtilities;
    private DatabaseReference mDatabaseReference;

    private FirebaseUtilities() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public static FirebaseUtilities newInstance() {
        if (mFirebaseUtilities != null) {
            return mFirebaseUtilities;
        }
        mFirebaseUtilities = new FirebaseUtilities();
        return mFirebaseUtilities;
    }

    public void addParty(Party p) {
        mDatabaseReference.child("parties").push().setValue(p);
    }
}
