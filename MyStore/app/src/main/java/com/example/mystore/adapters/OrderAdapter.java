package com.example.mystore.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mystore.R;
import com.example.mystore.activities.UpdateOrderActivity;
import com.example.mystore.models.Address;
import com.example.mystore.models.Order;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private Context context;
    private List<Order> list;

    public OrderAdapter(Context context, List<Order> list) {
        this.context = context;
        this.list = list;
    }

    public void setList(List<Order> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = list.get(position);
        Date dateTime = order.getDateTime();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(dateTime);
        String time = new SimpleDateFormat("HH:mm:ss").format(dateTime);
        holder.date.setText(date);
        holder.time.setText(time);
        holder.totalPrice.setText(String.valueOf(order.getTotalPrice()));
        if (order.getStatus().equals("ordered"))
            holder.status.setTextColor(Color.parseColor("#00FF00"));
        else holder.status.setTextColor(Color.parseColor("#FF0000"));
        holder.status.setText(order.getStatus());
        Address address = order.getAddress();
        holder.address.setText(address.getName()+"\n"+address.getAddress()+"\n"+address.getPhone());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdateOrderActivity.class);
                intent.putExtra("order", order);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView date, time, totalPrice, address, status;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.order_date_item);
            time = itemView.findViewById(R.id.order_time_item);
            address = itemView.findViewById(R.id.order_address_item);
            totalPrice = itemView.findViewById(R.id.order_totalPrice_item);
            status = itemView.findViewById(R.id.order_status_item);
        }
    }
}
