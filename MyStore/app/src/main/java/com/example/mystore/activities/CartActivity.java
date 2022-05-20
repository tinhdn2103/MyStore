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
import com.example.mystore.models.CartProduct;
import com.example.mystore.models.Category;
import com.example.mystore.models.Order;
import com.example.mystore.models.OrderDetail;
import com.example.mystore.models.Product;
import com.example.mystore.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity implements CartProductAdapter.CartProductListener {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private TextView totalPrice;
    private long total = 0;
    private ArrayList<CartProduct> listCartProduct;
    private CartProductAdapter adapter;
    private Button buyNow;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        buyNow = findViewById(R.id.cart_buyNow);
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
        recyclerView = findViewById(R.id.cart_rec);
        totalPrice = findViewById(R.id.cart_total_price);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        listCartProduct = new ArrayList<>();
        adapter = new CartProductAdapter(this, listCartProduct);
        adapter.setCartProductListener(this);
        recyclerView.setAdapter(adapter);
        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this, AddressActivity.class);
                Order order = new Order();
                order.setTotalPrice(total);
                User user = new User(auth.getUid());
                order.setUser(user);
                ArrayList<OrderDetail> orderDetails = new ArrayList<>();
                for(CartProduct cartProduct: listCartProduct){
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setPrice(cartProduct.getProduct().getPrice());
                    orderDetail.setProduct(cartProduct.getProduct());
                    orderDetail.setQuantity(cartProduct.getQuantity());
                    orderDetails.add(orderDetail);
                }
                order.setOrderDetails(orderDetails);
                intent.putExtra("cartOrder", order);
                startActivity(intent);
            }
        });

        db.collection("CartProduct").whereEqualTo("user", auth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(!task.getResult().isEmpty()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    CartProduct cartProduct = new CartProduct();
                                    cartProduct.setId(document.getId());
                                    User user = new User(auth.getUid());
                                    cartProduct.setUser(user);
                                    cartProduct.setQuantity((long) document.get("quantity"));
                                    Timestamp timestamp = (Timestamp) document.get("dateTime");
                                    cartProduct.setDateTime(timestamp.toDate());
                                    db.collection("Product").document((String) document.get("product"))
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()){
                                                        DocumentSnapshot document2 = task.getResult();
                                                        Product product = new Product();
                                                        product.setId(document2.getId());
                                                        product.setPrice((long) document2.get("price"));
                                                        product.setQuantity((long) document2.get("quantity"));
                                                        product.setName((String) document2.get("name"));
                                                        Timestamp timestamp = (Timestamp) document2.get("dateCreated");
                                                        product.setDateCreated(timestamp.toDate());
                                                        product.setDescription((String) document2.get("description"));
                                                        product.setImg_url((String) document2.get("img_url"));
                                                        product.setRate((String) document2.get("rate"));
                                                        total += cartProduct.getQuantity()*product.getPrice();
                                                        totalPrice.setText(String.valueOf(total)+"đ");
                                                        db.collection("Category").document((String) document2.get("category"))
                                                                .get()
                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                        if(task.isSuccessful()){
                                                                            DocumentSnapshot document3 = task.getResult();
                                                                            Category category = document3.toObject(Category.class);
                                                                            category.setId(document3.getId());
                                                                            product.setCategory(category);
                                                                            cartProduct.setProduct(product);
                                                                            listCartProduct.add(cartProduct);
                                                                            adapter.notifyDataSetChanged();
                                                                        }
                                                                        else {
                                                                            Toast.makeText(CartActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                    else {
                                                        Toast.makeText(CartActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        }else {
                            Toast.makeText(CartActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onAddItem(View view, int position) {
        CartProduct cartProduct = listCartProduct.get(position);
        if(cartProduct.getQuantity() < 10){
            db.runTransaction(new Transaction.Function<Void>() {
                @Nullable
                @Override
                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                    transaction.update(db.collection("CartProduct").document(cartProduct.getId()), "quantity", cartProduct.getQuantity()+1);
                    return null;
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    cartProduct.setQuantity(cartProduct.getQuantity()+1);
                    adapter.setList(listCartProduct);
                    total = totalPrice();
                    totalPrice.setText(String.valueOf(total)+"đ");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CartActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onMinItem(View view, int position) {
        CartProduct cartProduct = listCartProduct.get(position);
        if(cartProduct.getQuantity() >1){
            db.runTransaction(new Transaction.Function<Void>() {
                @Nullable
                @Override
                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                    transaction.update(db.collection("CartProduct").document(cartProduct.getId()), "quantity", cartProduct.getQuantity()-1);
                    return null;
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    cartProduct.setQuantity(cartProduct.getQuantity()-1);
                    adapter.setList(listCartProduct);
                    total = totalPrice();
                    totalPrice.setText(String.valueOf(total)+"đ");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CartActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onDeleteItem(View view, int position) {
        CartProduct cartProduct = listCartProduct.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có muốn xóa?");
        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                db.runTransaction(new Transaction.Function<Void>() {
                    @Nullable
                    @Override
                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        transaction.delete(db.collection("CartProduct").document(cartProduct.getId()));
                        return null;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        listCartProduct.remove(cartProduct);
                        adapter.setList(listCartProduct);
                        total = totalPrice();
                        totalPrice.setText(String.valueOf(total)+"đ");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CartActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    private long totalPrice(){
        long totalPrice = 0;
        for(CartProduct cartProduct : listCartProduct){
            totalPrice += cartProduct.getQuantity()*cartProduct.getProduct().getPrice();
        }
        return totalPrice;
    }
}