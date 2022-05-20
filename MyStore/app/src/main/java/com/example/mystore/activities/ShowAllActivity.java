package com.example.mystore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mystore.R;
import com.example.mystore.adapters.ShowAllAdapter;
import com.example.mystore.models.Category;
import com.example.mystore.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShowAllActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ShowAllAdapter showAllAdapter;
    private ArrayList<Product> showAllList;
    private ArrayList<Product> productList;
    private Category category;
    private FirebaseFirestore db;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);


        category = (Category) getIntent().getSerializableExtra("category");
        productList = (ArrayList<Product>) getIntent().getSerializableExtra("productList");

        db = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.show_all_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        recyclerView = findViewById(R.id.showAllRec);
        GridLayoutManager manager = new GridLayoutManager(this, 2);


        if (category == null){
            showAllList = productList;
            showAllAdapter = new ShowAllAdapter(this, showAllList);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(showAllAdapter);
        }

        if (category != null){
            showAllList = new ArrayList<>();
            for(Product product:productList){
                if(product.getCategory().getId().equals(category.getId()))
                    showAllList.add(product);
            }
            showAllAdapter = new ShowAllAdapter(this, showAllList);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(showAllAdapter);
        }
    }
}