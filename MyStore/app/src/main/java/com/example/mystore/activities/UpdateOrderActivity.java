package com.example.mystore.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mystore.R;
import com.example.mystore.adapters.CartProductAdapter;
import com.example.mystore.adapters.OrderDetailAdapter;
import com.example.mystore.models.CartProduct;
import com.example.mystore.models.Order;
import com.example.mystore.models.OrderDetail;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;

public class UpdateOrderActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private TextView totalPrice;
    private long total = 0;
    private ArrayList<OrderDetail> orderDetails;
    private OrderDetailAdapter adapter;
    private Button cancel, updateAddress;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_order);

        toolbar = findViewById(R.id.cart_toolbar);
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
        orderDetails = order.getOrderDetails();

        totalPrice = findViewById(R.id.total_price);
        total = totalPrice();
        totalPrice.setText(String.valueOf(total));

        recyclerView = findViewById(R.id.order_detail_rec);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new OrderDetailAdapter(this, orderDetails);
        recyclerView.setAdapter(adapter);

        cancel = findViewById(R.id.cancel_order);
        updateAddress = findViewById(R.id.edit_address);
        if(order.getStatus().equals("cancelled")){
            cancel.setEnabled(false);
            updateAddress.setEnabled(false);
        }
        else {
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UpdateOrderActivity.this);
                    builder.setTitle("Xác nhận hủy");
                    builder.setMessage("Bạn có muốn hủy đơn hàng?");
                    builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            db.runTransaction(new Transaction.Function<Void>() {
                                @Nullable
                                @Override
                                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                    transaction.update(db.collection("Order").document(order.getId()), "status", "cancelled");
                                    return null;
                                }
                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    order.setStatus("cancelled");
                                    Intent intent = new Intent();
                                    intent.putExtra("order", order);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UpdateOrderActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    builder.setNegativeButton("Bỏ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.show();
                }
            });
            updateAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(UpdateOrderActivity.this, UpdateAddressActivity.class);
                    intent.putExtra("order", order);
                    startActivityForResult(intent, 1);
                }
            });
        }
    }
    private long totalPrice(){
        long totalPrice = 0;
        for(OrderDetail orderDetail : orderDetails){
            totalPrice += orderDetail.getQuantity()*orderDetail.getPrice();
        }
        return totalPrice;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1)
            if(resultCode == RESULT_OK){
                order = (Order) data.getSerializableExtra("order");
            }
    }
}