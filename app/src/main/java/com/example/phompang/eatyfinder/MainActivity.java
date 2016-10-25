package com.example.phompang.eatyfinder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.phompang.eatyfinder.model.Party;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.twitter.sdk.android.Twitter;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SearchFragment.OnFragmentInteractionListener, AllFragment.OnAddClickedListener, MeFragment.OnLogoutListener, BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.bottom_navigation) BottomNavigationView mBottomNavigationView;

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mBottomNavigationView.setOnNavigationItemSelectedListener(this);

        SearchFragment searchFragment = SearchFragment.newInstance("test", "test");
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, searchFragment).commit();
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        Twitter.logOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (item.getItemId()) {
            case R.id.action_search:
                SearchFragment searchFragment = SearchFragment.newInstance("test", "test");
                fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.flContent, searchFragment).commit();
                break;
            case R.id.action_all:
                AllFragment allFragment = AllFragment.newInstance("test", "test");
                fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.flContent, allFragment).commit();
                break;
            case R.id.action_me:
                MeFragment meFragment = MeFragment.newInstance("test", "test");
                fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.flContent, meFragment).commit();
                break;
        }
        return false;
    }

    @Override
    public void onLogout() {
        signOut();
    }

    @Override
    public void onClicked() {
        Intent intent = new Intent(MainActivity.this, AddActivity.class);
        startActivity(intent);
    }
}
