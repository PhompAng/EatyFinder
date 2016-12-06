package com.example.phompang.eatyfinder;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by phompang on 12/5/2016 AD.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("UpdateToken", token);
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        if (u != null) {
            FirebaseDatabase.getInstance().getReference().child("users").child(u.getUid()).child("token").setValue(token);
        }
    }
}
