package com.example.mystore.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.mystore.R;
import com.example.mystore.activities.CartActivity;
import com.example.mystore.activities.RegistrationActivity;
import com.example.mystore.activities.ShowAllActivity;
import com.example.mystore.adapters.CategoryAdapter;
import com.example.mystore.adapters.NewProductsAdapter;
import com.example.mystore.adapters.PopularProductsAdapter;

import com.example.mystore.models.Category;
import com.example.mystore.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import java.util.List;

public class HomeFragment extends Fragment implements CategoryAdapter.CategoryItemLister {
    private Toolbar toolbar;
    private TextView catShowAll, popShowAll, newShowAll;

    private LinearLayout home;

    private ImageSlider imageSlider;

    private RecyclerView catRecyclerView, newRecyclerView, popRecyclerView;
    //Category
    private CategoryAdapter categoryAdapter;
    private ArrayList<Category> categoryList;
    //New Products
    private NewProductsAdapter newProductsAdapter;
    private ArrayList<Product> newProductsList;
    //Popular Products
    private PopularProductsAdapter popProductsAdapter;
    private ArrayList<Product> popProductsList;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ArrayList<Product> productList;

    public HomeFragment() {
    }

    public ArrayList<Product> getProductList() {
        return productList;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_logout){
            auth.signOut();
            Intent intent = new Intent(getContext(), RegistrationActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
        else if(id == R.id.menu_cart){
            Intent intent = new Intent(getContext(), CartActivity.class);
            startActivity(intent);
        }
        return true;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        toolbar = view.findViewById(R.id.home_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        //activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        productList = new ArrayList<>();
        home = view.findViewById(R.id.home);
        imageSlider = view.findViewById(R.id.image_slider);
        catRecyclerView = view.findViewById(R.id.recCategory);
        newRecyclerView = view.findViewById(R.id.recNew);
        catShowAll = view.findViewById(R.id.categorySeeAll);
        newShowAll = view.findViewById(R.id.newSeeAll);
        popShowAll = view.findViewById(R.id.popSeeAll);

        catShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ShowAllActivity.class);
                intent.putExtra("productList", productList);
                startActivity(intent);
            }
        });
        newShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ShowAllActivity.class);
                intent.putExtra("productList", productList);
                startActivity(intent);
            }
        });
        popShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ShowAllActivity.class);
                intent.putExtra("productList", productList);
                startActivity(intent);
            }
        });

        home.setVisibility(View.GONE);


        //Image slider

        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.banner1, "Giảm giá đối với những mặt hàng giày", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.banner2, "Giảm giá đối với những mặt hàng nước hoa", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.banner3, "Giảm giá lên đến 70%", ScaleTypes.CENTER_CROP));
        imageSlider.setImageList(slideModels);

        //Category

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(), categoryList, this);
        catRecyclerView.setLayoutManager(manager);
        catRecyclerView.setAdapter(categoryAdapter);
        popRecyclerView = view.findViewById(R.id.recPop);

        //New products

        LinearLayoutManager manager1 = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        newProductsList = productList;
        newProductsAdapter = new NewProductsAdapter(getContext(), newProductsList);
        newRecyclerView.setLayoutManager(manager1);
        newRecyclerView.setAdapter(newProductsAdapter);

        //Popular products

        LinearLayoutManager manager2 = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        popProductsList = productList;
        popProductsAdapter = new PopularProductsAdapter(getContext(), popProductsList);
        popRecyclerView.setLayoutManager(manager2);
        popRecyclerView.setAdapter(popProductsAdapter);

        db.collection("Category")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Category category = document.toObject(Category.class);
                                category.setId(document.getId());
                                categoryList.add(category);
                            }
                            db.collection("Product")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                for (QueryDocumentSnapshot document2 : task.getResult()) {
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
                                                    for(Category category: categoryList){
                                                        if(category.getId().equals((String) document2.get("category"))){
                                                            product.setCategory(category);
                                                            break;
                                                        }
                                                    }
                                                    productList.add(product);
                                                    categoryAdapter.notifyDataSetChanged();
                                                    newProductsAdapter.notifyDataSetChanged();
                                                    popProductsAdapter.notifyDataSetChanged();
                                                    home.setVisibility(View.VISIBLE);
                                                }
                                            }else {
                                                Toast.makeText(getContext(), ""+task.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(getContext(), ""+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return view;
    }

    @Override
    public void onClickItem(Category category) {
        Intent intent = new Intent(getContext(), ShowAllActivity.class);
        intent.putExtra("category", category);
        intent.putExtra("productList", productList);
        getContext().startActivity(intent);
    }
}