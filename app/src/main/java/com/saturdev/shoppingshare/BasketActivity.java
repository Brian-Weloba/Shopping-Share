package com.saturdev.shoppingshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.saturdev.shoppingshare.ViewHolder.CartViewHolder;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BasketActivity extends AppCompatActivity {
    @BindView(R.id.backButton)
    ImageView back;
    @BindView(R.id.totalAmount)
    TextView totalAmountTV;
    @BindView(R.id.buttonCheckout)
    Button checkout;
//    @BindView(R.id.removeFromBasket)
//    ImageView remove;
//    @BindView(R.id.basketItems)
//    RecyclerView recyclerView;

    FirebaseAuth firebaseAuth;
    RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;

    private int totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);
        ButterKnife.bind(this);


        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.basketItems);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BasketActivity.this, HomeActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("cart List");
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        assert firebaseUser != null;
        FirebaseRecyclerOptions<Basket> options =
                new FirebaseRecyclerOptions.Builder<Basket>()
                        .setQuery(cartListRef.child("User View")
                                        .child(Objects.requireNonNull(firebaseUser.getPhoneNumber()))
                                        .child("Products")
                                , Basket.class)
                        .build();

        FirebaseRecyclerAdapter<Basket, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Basket, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Basket model) {
                        holder.txtProductQuantity.setText("QTY: " + model.getQuantity());
                        holder.txtProductName.setText(model.getName());
                        holder.txtProductPrice.setText("KSH " + model.getPrice());

                        int oneProductTPrice = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());
                        totalPrice += oneProductTPrice;

                        totalAmountTV.setText("KSH "+String.valueOf(totalPrice));

                        holder.removeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cartListRef.child("User View")
                                        .child(firebaseUser.getPhoneNumber())
                                        .child("Products")
                                        .child(model.getName())
                                        .removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(BasketActivity.this, "Item Removed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(BasketActivity.this, ViewItemActivity.class);
                                intent.putExtra("name", model.getName());
                                intent.putExtra("price", model.getPrice());
                                intent.putExtra("description", model.getDescription());
                                intent.putExtra("items", model.getItems());
                                startActivity(intent);
                            }
                        });

//                        holder.itemView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                CharSequence options[] = new CharSequence[]{
//                                        "Edit",
//                                        "Remove from basket"
//                                };
//                                AlertDialog.Builder builder = new AlertDialog.Builder(BasketActivity.this);
//                                builder.setTitle("Options");
//
//                                builder.setItems(options, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        if (which == 0) {
//                                            Intent intent = new Intent(BasketActivity.this, ViewItemActivity.class);
//                                            intent.putExtra("name", model.getName());
//                                            startActivity(intent);
//                                        }
//                                        if (which == 1) {
//                                            cartListRef.child("User View")
//                                                    .child(firebaseUser.getPhoneNumber())
//                                                    .child("Products")
//                                                    .child(model.getName())
//                                                    .removeValue()
//                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                        @Override
//                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                            if (task.isSuccessful()) {
//                                                                Toast.makeText(BasketActivity.this, "Item Removed", Toast.LENGTH_SHORT).show();
//                                                            }
//                                                        }
//                                                    });
//                                        }
//                                    }
//                                });
//                                builder.show();
//                            }
//                        });
                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.basket_items_layout, parent, false);
                        return new CartViewHolder(view);
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}