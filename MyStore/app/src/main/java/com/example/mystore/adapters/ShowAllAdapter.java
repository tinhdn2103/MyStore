package com.example.mystore.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mystore.R;
import com.example.mystore.activities.DetailActivity;
import com.example.mystore.models.Product;

import java.util.ArrayList;

public class ShowAllAdapter extends RecyclerView.Adapter<ShowAllAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Product> list;

    public ShowAllAdapter(Context context, ArrayList<Product> list) {
        this.context = context;
        this.list = list;
    }

    public void setList(ArrayList<Product> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_all_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(list.get(position).getImg_url()).into(holder.showAllImg);
        holder.showAllName.setText(list.get(position).getName());
        holder.showAllPrice.setText(String.valueOf(list.get(position).getPrice())+"đ");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(context, DetailActivity.class);
                intent.putExtra("detail", list.get(holder.getAdapterPosition()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView showAllImg;
        TextView showAllName, showAllPrice;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            showAllImg = itemView.findViewById(R.id.showAllImg);
            showAllName = itemView.findViewById(R.id.showAllProductName);
            showAllPrice = itemView.findViewById(R.id.showAllProductPrice);
        }
    }
}
