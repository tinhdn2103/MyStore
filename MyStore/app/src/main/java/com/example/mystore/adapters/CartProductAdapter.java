package com.example.mystore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mystore.R;
import com.example.mystore.models.CartProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CartProductAdapter extends RecyclerView.Adapter<CartProductAdapter.ViewHolder> {
    private Context context;
    private List<CartProduct> list;
    private FirebaseFirestore db;
    private CartProductListener cartProductListener;

    public CartProductAdapter(Context context, List<CartProduct> list) {
        this.context = context;
        this.list = list;
        this.db = FirebaseFirestore.getInstance();
    }

    public void setCartProductListener(CartProductListener cartProductListener) {
        this.cartProductListener = cartProductListener;
    }

    public void setList(List<CartProduct> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartProduct cartProduct = list.get(position);
        Date dateTime = cartProduct.getDateTime();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(dateTime);
        String time = new SimpleDateFormat("HH:mm:ss").format(dateTime);
        holder.date.setText(date);
        holder.time.setText(time);
        holder.quantity.setText(String.valueOf(cartProduct.getQuantity()));
        holder.addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartProductListener.onAddItem(view, holder.getAdapterPosition());
            }
        });

        holder.minItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartProductListener.onMinItem(view, holder.getAdapterPosition());
            }
        });
        holder.price.setText(String.valueOf(cartProduct.getProduct().getPrice()));
        holder.totalPrice.setText(String.valueOf(cartProduct.getQuantity()*cartProduct.getProduct().getPrice()));
        holder.name.setText(cartProduct.getProduct().getName());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartProductListener.onDeleteItem(view, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, price, date, time, totalPrice, quantity;
        private ImageView minItem, addItem, delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cart_name_item);
            price = itemView.findViewById(R.id.cart_price_item);
            date = itemView.findViewById(R.id.cart_date_item);
            time = itemView.findViewById(R.id.cart_time_item);
            totalPrice = itemView.findViewById(R.id.cart_total_price_item);
            quantity = itemView.findViewById(R.id.cart_quantity_item);
            minItem = itemView.findViewById(R.id.minItem);
            addItem = itemView.findViewById(R.id.addItem);
            delete = itemView.findViewById(R.id.delete);
        }
    }

    public interface CartProductListener{
        void onAddItem(View view, int position);
        void onMinItem(View view, int position);
        void onDeleteItem(View view, int position);
    }
}
