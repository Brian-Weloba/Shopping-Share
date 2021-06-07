package com.saturdev.shoppingshare.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saturdev.shoppingshare.Interfaces.ItemClickListener;
import com.saturdev.shoppingshare.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtProductName, txtProductPrice, txtProductQuantity;
    public ImageView removeButton;
    private ItemClickListener itemClickListener;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        txtProductName = itemView.findViewById(R.id.prodName);
        txtProductPrice = itemView.findViewById(R.id.prodPrice);
        txtProductQuantity = itemView.findViewById(R.id.prodQuantity);
        removeButton = itemView.findViewById(R.id.removeFromBasket);

    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAbsoluteAdapterPosition(), false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
