package com.example.mystore.activities;

import androidx.annotation.NonNull;
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
import com.example.mystore.models.CartProduct;
import com.example.mystore.models.Order;
import com.example.mystore.models.Product;
import com.example.mystore.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AddressActivity extends AppCompatActivity implements AddressAdapter.SelectAddress{

    private Button addBtn, orderBtn;
    private RecyclerView recyclerView;
    private List<Address> addressList;
    private AddressAdapter adapter;
    private Toolbar toolbar;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private Address selectedAddress;
    private Order cartOrder, productOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

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

        recyclerView = findViewById(R.id.address_rec);
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        addressList = new ArrayList<>();
        adapter = new AddressAdapter(this, addressList, this);
        recyclerView.setAdapter(adapter);
        getAddress();

        cartOrder = (Order) getIntent().getSerializableExtra("cartOrder");
        productOrder = (Order) getIntent().getSerializableExtra("productOrder");

        orderBtn = findViewById(R.id.order_btn);
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddressActivity.this, OrderActivity.class);
                if(selectedAddress==null){
                    Toast.makeText(AddressActivity.this, "Vui lòng chọn địa chỉ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(cartOrder!=null)
                    cartOrder.setAddress(selectedAddress);
                else
                    productOrder.setAddress(selectedAddress);
                intent.putExtra("cartOrder", cartOrder);
                intent.putExtra("productOrder", productOrder);
                startActivity(intent);
            }
        });
        addBtn = findViewById(R.id.add_address_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddressActivity.this, AddAddressActivity.class);
                intent.putExtra("cartOrder", cartOrder);
                intent.putExtra("productOrder", productOrder);
                intent.putExtra("activity", "AddressActivity");
                startActivity(intent);
            }
        });
    }

    private void getAddress() {
        List<Address> list = new ArrayList<>();
        adapter.setList(list);
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
                                address.setSelected(false);
                                list.add(address);
                                adapter.notifyDataSetChanged();

                            }
                        } else {
                            Toast.makeText(AddressActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void setAddress(Address address) {
        selectedAddress = address;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAddress();
    }
}