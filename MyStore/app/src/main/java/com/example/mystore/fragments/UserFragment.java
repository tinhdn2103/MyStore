package com.example.mystore.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.mystore.R;
import com.google.firebase.auth.FirebaseAuth;

public class UserFragment extends Fragment {
    private Toolbar toolbar;
    private FirebaseAuth auth;
    private TextView email;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmet_user_page, container, false);
        toolbar = view.findViewById(R.id.order_list_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();
        email = view.findViewById(R.id.email);
        email.setText("Email: "+auth.getCurrentUser().getEmail());
        return view;
    }
}
