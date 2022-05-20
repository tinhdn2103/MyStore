package com.example.mystore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.mystore.R;
import com.example.mystore.fragments.HomeFragment;
import com.example.mystore.fragments.OrderFragment;
import com.example.mystore.fragments.SearchFragment;
import com.example.mystore.fragments.UserFragment;
import com.example.mystore.models.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private HomeFragment homeFragment;
    private UserFragment userFragment;
    private SearchFragment searchFragment;
    private OrderFragment orderFragment;
    private FirebaseAuth auth;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        homeFragment = new HomeFragment();
        userFragment = new UserFragment();
        searchFragment = new SearchFragment();
        orderFragment = new OrderFragment();
        bottomNavigationView = findViewById(R.id.bnv);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.mHome:
                        loadFragment(homeFragment);
                        break;
                    case R.id.mSearch:
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("productList", homeFragment.getProductList());
                        searchFragment.setArguments(bundle);
                        loadFragment(searchFragment);
                        break;
                    case R.id.mOrder:
                        loadFragment(orderFragment);
                        break;
                    case R.id.mUser:
                        loadFragment(userFragment);
                        break;
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.mHome);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_container, fragment);
        transaction.commit();
    }

}