package com.example.mystore.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mystore.R;
import com.example.mystore.models.Address;
import com.example.mystore.models.CartProduct;
import com.example.mystore.models.Order;
import com.example.mystore.models.OrderDetail;
import com.example.mystore.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button btnOrder;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Order cartOrder, productOrder;
    private TextView totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        toolbar = findViewById(R.id.order_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        totalPrice  = findViewById(R.id.order_total_price);
        btnOrder = findViewById(R.id.order);

        cartOrder = (Order) getIntent().getSerializableExtra("cartOrder");
        productOrder = (Order) getIntent().getSerializableExtra("productOrder");

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        if (productOrder!=null){
            totalPrice.setText(String.valueOf(productOrder.getTotalPrice()));
            btnOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.runTransaction(new Transaction.Function<Void>() {
                        @Nullable
                        @Override
                        public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                            DocumentReference docRef = db.collection("Order").document();
                            Calendar calendar = Calendar.getInstance();
                            Date dateTime = calendar.getTime();
                            Map<String, Object> orderMap = new HashMap<>();
                            orderMap.put("dateTime", dateTime);
                            orderMap.put("user", auth.getUid());
                            orderMap.put("totalPrice", productOrder.getTotalPrice());
                            orderMap.put("address", productOrder.getAddress().getId());
                            orderMap.put("status", "ordered");
                            transaction.set(docRef, orderMap);
                            Map<String, Object> orderDetailMap = new HashMap<>();
                            OrderDetail orderDetail = productOrder.getOrderDetails().get(0);
                            orderDetailMap.put("product", orderDetail.getProduct().getId());
                            orderDetailMap.put("price", orderDetail.getPrice());
                            orderDetailMap.put("order", docRef.getId());
                            orderDetailMap.put("quantity", orderDetail.getQuantity());
                            transaction.set(db.collection("OrderDetail").document(), orderDetailMap);
                            return null;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(OrderActivity.this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(OrderActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
        }
        if(cartOrder!=null){
            totalPrice.setText(String.valueOf(cartOrder.getTotalPrice()));
            btnOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<CartProduct> listCartProduct = new ArrayList<>();
                    db.collection("CartProduct").whereEqualTo("user", auth.getUid())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (!task.getResult().isEmpty()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                CartProduct cartProduct = new CartProduct();
                                                cartProduct.setId(document.getId());
                                                listCartProduct.add(cartProduct);
                                            }
                                            db.runTransaction(new Transaction.Function<Void>() {
                                                @Nullable
                                                @Override
                                                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                                                    DocumentReference docRef = db.collection("Order").document();
                                                    for(CartProduct cartProduct: listCartProduct){
                                                        transaction.delete(db.collection("CartProduct").document(cartProduct.getId()));
                                                    }
                                                    Calendar calendar = Calendar.getInstance();
                                                    Date dateTime = calendar.getTime();
                                                    Map<String, Object> orderMap = new HashMap<>();
                                                    orderMap.put("dateTime", dateTime);
                                                    orderMap.put("user", auth.getUid());
                                                    orderMap.put("totalPrice", cartOrder.getTotalPrice());
                                                    orderMap.put("address", cartOrder.getAddress().getId());
                                                    orderMap.put("status", "ordered");
                                                    transaction.set(docRef, orderMap);
                                                    ArrayList<OrderDetail> orderDetails = cartOrder.getOrderDetails();
                                                    for(OrderDetail orderDetail:orderDetails){
                                                        Map<String, Object> orderDetailMap = new HashMap<>();
                                                        orderDetailMap.put("product", orderDetail.getProduct().getId());
                                                        orderDetailMap.put("price", orderDetail.getPrice());
                                                        orderDetailMap.put("order", docRef.getId());
                                                        orderDetailMap.put("quantity", orderDetail.getQuantity());
                                                        transaction.set(db.collection("OrderDetail").document(), orderDetailMap);
                                                    }

                                                    return null;
                                                }
                                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(OrderActivity.this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(OrderActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }else {
                                        Toast.makeText(OrderActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            });
        }
    }
}