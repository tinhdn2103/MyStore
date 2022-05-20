package com.example.mystore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mystore.R;
import com.example.mystore.models.CartProduct;
import com.example.mystore.models.Order;
import com.example.mystore.models.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddAddressActivity extends AppCompatActivity {

    private EditText ename, eaddress, ephone;
    private Toolbar toolbar;
    private Button addBtn;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private Order cartOrder, productOrder, order;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        String activity = getIntent().getStringExtra("activity");
        if(activity.equals("AddressActivity")) {
            cartOrder = (Order) getIntent().getSerializableExtra("cartOrder");
            productOrder = (Order) getIntent().getSerializableExtra("productOrder");
        }else{
            order = (Order) getIntent().getSerializableExtra("order");
        }

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        toolbar = findViewById(R.id.add_address_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ename = findViewById(R.id.add_address_name);
        eaddress = findViewById(R.id.add_address_address);
        ephone = findViewById(R.id.add_address_phone);
        addBtn = findViewById(R.id.add_address);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = ename.getText().toString();
                String address = eaddress.getText().toString();
                String phone = ephone.getText().toString();
                if(!name.isEmpty() && !address.isEmpty() && !phone.isEmpty()){
                    Map<String, String> map = new HashMap<>();
                    map.put("name", name);
                    map.put("address", address);
                    map.put("phone", phone);
                    map.put("user", auth.getCurrentUser().getUid());
                    db.collection("Address")
                            .add(map)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(AddAddressActivity.this, "Đã thêm địa chỉ", Toast.LENGTH_SHORT).show();
                                    if(activity.equals("AddressActivity")) {
                                        Intent intent = new Intent(AddAddressActivity.this, AddressActivity.class);
                                        intent.putExtra("cartOrder", cartOrder);
                                        intent.putExtra("productOrder", productOrder);
                                        startActivity(intent);
                                    }
                                    else{
                                        Intent intent = new Intent(AddAddressActivity.this, UpdateAddressActivity.class);
                                        intent.putExtra("order", order);
                                        startActivity(intent);
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddAddressActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            }
        });

    }
}