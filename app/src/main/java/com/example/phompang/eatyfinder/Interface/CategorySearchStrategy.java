package com.example.phompang.eatyfinder.Interface;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by phompang on 12/4/2016 AD.
 */

public class CategorySearchStrategy implements SearchStrategy {
    @Override
    public Query search(DatabaseReference databaseReference, String text) {
        return databaseReference.child("parties").orderByChild("category/name").equalTo(text).limitToLast(20);
    }
}
