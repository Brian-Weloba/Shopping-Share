package com.saturdev.shoppingshare.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saturdev.shoppingshare.Models.Carts;
import com.saturdev.shoppingshare.R;
import com.saturdev.shoppingshare.ViewItemActivity;

import java.util.ArrayList;
import java.util.List;

public class GridElementAdapter extends RecyclerView.Adapter<GridElementAdapter.SimpleViewHolder> {


    private final Context context;
    private final List<Carts> elements;

    public GridElementAdapter(Context context, ArrayList<Carts> cartsArrayList) {
        this.context = context;
        this.elements = cartsArrayList;
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(this.context).inflate(R.layout.grid_element, parent, false);
        return new SimpleViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SimpleViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        Carts cartItem = elements.get(position);
        holder.cartNameTV.setText(cartItem.getCart_name());
        holder.priceTV.setText("KES " + cartItem.getCart_price());

        holder.addIV.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(context, "Added to basket", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Removed from basket", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.favouriteIV.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Removed from favourites", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.itemIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewItemActivity.class);
                intent.putExtra("name", elements.get(position).getCart_name());
                intent.putExtra("price", elements.get(position).getCart_price());
                intent.putExtra("description", elements.get(position).getCart_description());
                intent.putExtra("items", elements.get(position).getCart_items());
                context.startActivity(intent);
            }
        });

        holder.cartNameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewItemActivity.class);
                intent.putExtra("name", elements.get(position).getCart_name());
                intent.putExtra("price", elements.get(position).getCart_price());
                intent.putExtra("description", elements.get(position).getCart_description());
                intent.putExtra("items", elements.get(position).getCart_items());
                context.startActivity(intent);
            }
        });

        holder.priceTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewItemActivity.class);
                intent.putExtra("name", elements.get(position).getCart_name());
                intent.putExtra("price", elements.get(position).getCart_price());
                intent.putExtra("description", elements.get(position).getCart_description());
                intent.putExtra("items", elements.get(position).getCart_items());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (elements != null) {
            return this.elements.size();
        } else {
            return 0;
        }

    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public final TextView cartNameTV;
        public final TextView priceTV;
        public final RelativeLayout cardRL;
        public final ImageView itemIV;
        public final ToggleButton favouriteIV;
        public final ToggleButton addIV;

        public SimpleViewHolder(View view) {
            super(view);
            cartNameTV = view.findViewById(R.id.cartName);
            priceTV = view.findViewById(R.id.cartPrice);
            cardRL = view.findViewById(R.id.wholeCard);
            addIV = view.findViewById(R.id.buttonAdd);
            favouriteIV = view.findViewById(R.id.buttonLike);
            itemIV = view.findViewById(R.id.itemPicture);
        }
    }
}
