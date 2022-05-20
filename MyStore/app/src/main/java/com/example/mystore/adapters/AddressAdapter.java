package com.example.mystore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mystore.R;
import com.example.mystore.models.Address;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
    private Context context;
    private List<Address> list;
    private SelectAddress selectAddress;
    private RadioButton selectedRadioButton;

    public AddressAdapter(Context context, List<Address> list, SelectAddress selectAddress) {
        this.context = context;
        this.list = list;
        this.selectAddress = selectAddress;
    }

    public void setList(List<Address> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Address address = list.get(position);
        holder.address.setText(address.getName()+"\n"+address.getAddress()+"\n"+address.getPhone());
        if(address.isSelected()){
            holder.radioButton.setChecked(address.isSelected());
            selectedRadioButton = holder.radioButton;
        }
        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(Address address1:list){
                    address1.setSelected(false);
                }
                if(selectedRadioButton!=null){
                    selectedRadioButton.setChecked(false);
                }
                selectedRadioButton = (RadioButton) view;
                selectedRadioButton.setChecked(true);
                selectAddress.setAddress(address);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView address;
        private RadioButton radioButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address_item);
            radioButton = itemView.findViewById(R.id.select_address);
        }
    }

    public interface SelectAddress{
        void setAddress(Address address);
    }
}
