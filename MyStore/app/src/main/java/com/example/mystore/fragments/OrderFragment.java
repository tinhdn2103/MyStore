package com.example.mystore.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mystore.R;
import com.example.mystore.activities.CartActivity;
import com.example.mystore.adapters.OrderAdapter;
import com.example.mystore.models.Address;
import com.example.mystore.models.CartProduct;
import com.example.mystore.models.Category;
import com.example.mystore.models.Order;
import com.example.mystore.models.OrderDetail;
import com.example.mystore.models.Product;
import com.example.mystore.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderFragment extends Fragment {
    private TextView totalOrder;
    private RecyclerView recyclerView;
    private List<Order> list;
    private Toolbar toolbar;
    private OrderAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public OrderFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);
        toolbar = view.findViewById(R.id.order_list_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        totalOrder = view.findViewById(R.id.order_total);
        recyclerView = view.findViewById(R.id.order_list_rec);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        list = new ArrayList<>();
        adapter = new OrderAdapter(getContext(), list);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        getOrders();
        return view;
    }
    private void getOrders(){
        List<Order> orderList = new ArrayList<>();
        adapter.setList(orderList);
        db.collection("Order").whereEqualTo("user", auth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            totalOrder.setText("Tổng: "+String.valueOf(task.getResult().size())+" đơn");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Order order = new Order();
                                User user = new User(auth.getUid());
                                order.setUser(user);
                                order.setTotalPrice((long) document.get("totalPrice"));
                                Timestamp timestamp = (Timestamp) document.get("dateTime");
                                order.setDateTime(timestamp.toDate());
                                order.setStatus((String) document.get("status"));
                                order.setId(document.getId());
                                db.collection("Address").document((String) document.get("address"))
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document2 = task.getResult();
                                                    Address address = new Address();
                                                    address.setName((String) document2.get("name"));
                                                    address.setPhone((String) document2.get("phone"));
                                                    address.setAddress((String) document2.get("address"));
                                                    User user = new User(auth.getUid());
                                                    address.setUser(user);
                                                    address.setId(document2.getId());
                                                    order.setAddress(address);
                                                    orderList.add(order);
                                                    adapter.notifyDataSetChanged();

                                                } else {
                                                    Toast.makeText(getContext(), ""+task.getException(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                ArrayList<OrderDetail> orderDetails = new ArrayList<>();
                                order.setOrderDetails(orderDetails);
                                db.collection("OrderDetail").whereEqualTo("order", order.getId())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    if(!task.getResult().isEmpty()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            OrderDetail orderDetail = new OrderDetail();
                                                            orderDetail.setId(document.getId());
                                                            orderDetail.setQuantity((long) document.get("quantity"));
                                                            orderDetail.setPrice((long) document.get("price"));
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
                                                                                                    orderDetail.setProduct(product);
                                                                                                    orderDetails.add(orderDetail);
                                                                                                }
                                                                                                else {
                                                                                                    Toast.makeText(getContext(), ""+task.getException(), Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                            }
                                                                            else {
                                                                                Toast.makeText(getContext(), ""+task.getException(), Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                }else {
                                                    Toast.makeText(getContext(), ""+task.getException(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }

                        } else {
                            Toast.makeText(getContext(), ""+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        getOrders();
    }
}
