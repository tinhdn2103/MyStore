package com.example.mystore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mystore.R;
import com.example.mystore.models.OrderDetail;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {
    private Context context;
    private List<OrderDetail> list;
    private FirebaseFirestore db;

    public OrderDetailAdapter(Context context, List<OrderDetail> list) {
        this.context = context;
        this.list = list;
        this.db = FirebaseFirestore.getInstance();
    }

    public void setList(List<OrderDetail> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_detail_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderDetail orderDetail = list.get(position);
        holder.quantity.setText(String.valueOf(orderDetail.getQuantity()));
        holder.price.setText(String.valueOf(orderDetail.getProduct().getPrice()));
        holder.totalPrice.setText(String.valueOf(orderDetail.getQuantity()*orderDetail.getPrice()));
        holder.name.setText(orderDetail.getProduct().getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, price, totalPrice, quantity;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cart_name_item);
            price = itemView.findViewById(R.id.cart_price_item);
            totalPrice = itemView.findViewById(R.id.cart_total_price_item);
            quantity = itemView.findViewById(R.id.cart_quantity_item);
        }
    }

}
