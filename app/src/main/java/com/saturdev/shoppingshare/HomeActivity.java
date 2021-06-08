package com.saturdev.shoppingshare;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.leanback.widget.HorizontalGridView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saturdev.shoppingshare.Adapters.GridElementAdapter;
import com.saturdev.shoppingshare.Models.Carts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.helloUser)
    TextView mHelloUser;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.buttonCart)
    ImageView mCart;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar_main)
    Toolbar toolbar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.drawerLayout)
    DrawerLayout drawer;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    ArrayList<Carts> cartsArrayList = new ArrayList<>();
    DatabaseReference dbCart;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        navigationView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        dbCart = FirebaseDatabase.getInstance().getReference("Carts");

        HorizontalGridView horizontalGridView = (HorizontalGridView) findViewById(R.id.cartsView);
        HorizontalGridView horizontalGridView2 = (HorizontalGridView) findViewById(R.id.cartsView2);
        HorizontalGridView horizontalGridView3 = (HorizontalGridView) findViewById(R.id.cartsView3);


        GridElementAdapter adapter = new GridElementAdapter(HomeActivity.this, cartsArrayList);

        horizontalGridView.setAdapter(adapter);
        horizontalGridView2.setAdapter(adapter);
        horizontalGridView3.setAdapter(adapter);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        dbCart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fetchData(snapshot);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mCart.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, BasketActivity.class);
            startActivity(intent);
        });

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_basket:
                new Intent(HomeActivity.this,BasketActivity.class);
                startActivity(new Intent(HomeActivity.this,BasketActivity.class));
                break;
            case R.id.nav_favourites:
                Toast.makeText(this, "Clicked favorites", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(HomeActivity.this,FavouritesActivity.class));
                break;
            case R.id.nav_logout:
                SharedPreferences sharedPref = getSharedPreferences("data", MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = sharedPref.edit();
                prefEditor.putInt("isLogged", 0);
                prefEditor.apply();

                firebaseAuth.signOut();
                startActivity(new Intent(HomeActivity.this,MainActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    @SuppressLint("SetTextI18n")
    private void checkUserStatus() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
//            String phone = firebaseUser.getPhoneNumber();
            String username = firebaseUser.getDisplayName();
//            String email = firebaseUser.getEmail();
            mHelloUser.setText("Hello " + username + ",");
        } else {
            finish();
        }
    }

    private void fetchData(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            cartsArrayList.clear();

            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                Map<String, String> td = (HashMap<String, String>) ds.getValue();
                assert td != null;
                Carts cart = new Carts(td.get("cart_name"), td.get("cart_items"), td.get("cart_description"), td.get("cart_price"));
                cartsArrayList.add(cart);
            }
        }
    }
}