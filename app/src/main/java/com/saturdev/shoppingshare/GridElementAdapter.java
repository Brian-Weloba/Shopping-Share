package com.saturdev.shoppingshare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GridElementAdapter extends RecyclerView.Adapter<GridElementAdapter.SimpleViewHolder> {


    private Context context;
    private List<Carts> elements;

    public GridElementAdapter(Context context, ArrayList<Carts> cartsArrayList) {
        this.context = context;
        this.elements = cartsArrayList;
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public final TextView textView;
        public final TextView priceTV;

        public SimpleViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.cartName);
            priceTV = view.findViewById(R.id.cartPrice);
        }
    }


    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(this.context).inflate(R.layout.grid_element, parent, false);
        return new SimpleViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull SimpleViewHolder holder, final int position) {
        Carts cartItem = elements.get(position);
        holder.textView.setText(cartItem.getCart_name());
        holder.priceTV.setText(cartItem.getCart_price());
//        holder.textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(context, "Position =" + position, Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if(elements!=null) {
            return this.elements.size();
        }else {
            return 0;
        }

    }
}
