package com.example.mystore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mystore.R;
import com.example.mystore.models.Order;
import com.example.mystore.models.OrderDetail;
import com.example.mystore.models.Product;
import com.example.mystore.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    private ImageView detailImg;
    private TextView name, price, desc, rate, quantity;
    private RatingBar ratingBar;
    private ImageView addItems, minItems;
    private Button addToCart, buyNow;
    private FirebaseFirestore db;
    private Product product = null;
    private int totalQuantity;
    private FirebaseAuth auth;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        detailImg = findViewById(R.id.detailImg);
        name = findViewById(R.id.detailName);
        price = findViewById(R.id.detailPrice);
        desc = findViewById(R.id.detailDesc);
        rate = findViewById(R.id.detailRate);
        ratingBar = findViewById(R.id.ratingBar);
        quantity = findViewById(R.id.quantity);
        addItems = findViewById(R.id.addItem);
        minItems = findViewById(R.id.minItem);
        addToCart = findViewById(R.id.addToCart);
        buyNow = findViewById(R.id.buyNow);
        toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        totalQuantity = 1;

        getIntent().getSerializableExtra("");
        product = (Product) getIntent().getSerializableExtra("detail");

        //Products

        Glide.with(getApplicationContext()).load(product.getImg_url()).into(detailImg);
        name.setText(product.getName());
        rate.setText(product.getRate());
        desc.setText(product.getDescription());
        price.setText(String.valueOf(product.getPrice()+"đ"));
        try{
            ratingBar.setRating(Float.parseFloat(product.getRate()));
        }catch (Exception e) {
        }

        addItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(totalQuantity < 10){
                    totalQuantity++;
                    quantity.setText(String.valueOf(totalQuantity));
                }
            }
        });

        minItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(totalQuantity >1){
                    totalQuantity--;
                    quantity.setText(String.valueOf(totalQuantity));
                }
            }
        });

        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, AddressActivity.class);
                Order order = new Order();
                order.setTotalPrice(product.getPrice()*totalQuantity);
                User user = new User(auth.getUid());
                order.setUser(user);
                ArrayList<OrderDetail> orderDetails = new ArrayList<>();
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setPrice(product.getPrice());
                orderDetail.setProduct(product);
                orderDetail.setQuantity(totalQuantity);
                orderDetails.add(orderDetail);
                order.setOrderDetails(orderDetails);
                intent.putExtra("productOrder", order);
                startActivity(intent);
            }
        });


        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                Date dateTime = calendar.getTime();
                Map<String, Object> cartProductMap = new HashMap<>();

                cartProductMap.put("product", product.getId());
                cartProductMap.put("dateTime", dateTime);
                cartProductMap.put("user", auth.getUid());
                cartProductMap.put("quantity", totalQuantity);

                db.collection("CartProduct")
                        .add(cartProductMap)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(DetailActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DetailActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}