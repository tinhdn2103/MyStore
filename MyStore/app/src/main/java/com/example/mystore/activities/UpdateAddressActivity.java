package com.example.mystore.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mystore.R;
import com.example.mystore.adapters.AddressAdapter;
import com.example.mystore.models.Address;
import com.example.mystore.models.Order;
import com.example.mystore.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;

public class UpdateAddressActivity extends AppCompatActivity implements AddressAdapter.SelectAddress{
    private Button addBtn, updateBtn;
    private RecyclerView recyclerView;
    private List<Address> addressList;
    private AddressAdapter adapter;
    private Toolbar toolbar;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private Address selectedAddress;
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_address);

        toolbar = findViewById(R.id.address_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        order = (Order) getIntent().getSerializableExtra("order");

        selectedAddress = order.getAddress();

        recyclerView = findViewById(R.id.address_rec);
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        addressList = new ArrayList<>();
        adapter = new AddressAdapter(this, addressList, this);
        recyclerView.setAdapter(adapter);
        db.collection("Address").whereEqualTo("user", auth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Address address = new Address();
                                address.setName((String) document.get("name"));
                                address.setPhone((String) document.get("phone"));
                                address.setAddress((String) document.get("address"));
                                User user = new User(auth.getUid());
                                address.setUser(user);
                                address.setId(document.getId());
                                if(address.getId().equals(order.getAddress().getId()))
                                    address.setSelected(true);
                                else address.setSelected(false);
                                addressList.add(address);
                                adapter.notifyDataSetChanged();

                            }
                        } else {
                            Toast.makeText(UpdateAddressActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        updateBtn = findViewById(R.id.update_btn);
        addBtn = findViewById(R.id.add_address_btn);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.runTransaction(new Transaction.Function<Void>() {
                    @Nullable
                    @Override
                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        transaction.update(db.collection("Order").document(order.getId()), "address", selectedAddress.getId());
                        return null;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        order.setAddress(selectedAddress);
                        Intent intent = new Intent();
                        intent.putExtra("order", order);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateAddressActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateAddressActivity.this, AddAddressActivity.class);
                intent.putExtra("activity", "UpdateAddressActivity");
                intent.putExtra("order", order);
                startActivity(intent);
            }
        });

    }

    @Override
    public void setAddress(Address address) {
        selectedAddress = address;
    }

}