package com.example.mystore.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mystore.R;
import com.example.mystore.adapters.ShowAllAdapter;
import com.example.mystore.models.Category;
import com.example.mystore.models.Product;

import java.util.ArrayList;

public class SearchFragment extends Fragment {
    private SearchView searchView;
    private RecyclerView recyclerView;
    private ArrayList<Product> productList;
    private ShowAllAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        productList = (ArrayList<Product>) getArguments().getSerializable("productList");
        searchView = view.findViewById(R.id.search_view);
        recyclerView = view.findViewById(R.id.search_list);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(manager);
        adapter = new ShowAllAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ArrayList<Product> list = new ArrayList();
                for(Product p: productList){
                    if(p.getName().toLowerCase().contains(s.toLowerCase()))
                        list.add(p);
                }
                if(list.isEmpty())
                    Toast.makeText(getContext(), "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
                else
                    adapter.setList(list);
                return false;
            }
        });
        return view;
    }
}
