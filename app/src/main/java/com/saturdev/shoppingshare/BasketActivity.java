package com.saturdev.shoppingshare;

import static com.saturdev.shoppingshare.Models.Constants.BUSINESS_SHORT_CODE;
import static com.saturdev.shoppingshare.Models.Constants.CALLBACKURL;
import static com.saturdev.shoppingshare.Models.Constants.PARTYB;
import static com.saturdev.shoppingshare.Models.Constants.PASSKEY;
import static com.saturdev.shoppingshare.Models.Constants.PUSH_NOTIFICATION;
import static com.saturdev.shoppingshare.Models.Constants.REGISTRATION_COMPLETE;
import static com.saturdev.shoppingshare.Models.Constants.TOPIC_GLOBAL;
import static com.saturdev.shoppingshare.Models.Constants.TRANSACTION_TYPE;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.saturdev.shoppingshare.Models.Basket;
import com.saturdev.shoppingshare.ViewHolder.CartViewHolder;
import com.saturdev.shoppingshare.api.ApiClient;
import com.saturdev.shoppingshare.api.model.AccessToken;
import com.saturdev.shoppingshare.api.model.STKPush;
import com.saturdev.shoppingshare.util.NotificationUtils;
import com.saturdev.shoppingshare.util.SharedPrefsUtil;
import com.saturdev.shoppingshare.util.Utils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

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
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private RecyclerView recyclerView;
    private ProgressDialog mProgressDialog;
    private String mFireBaseRegId;
    private ApiClient mApiClient;
    private SharedPrefsUtil mSharedPrefsUtil;

    private int totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);
        ButterKnife.bind(this);

        mProgressDialog = new ProgressDialog(this);
        mSharedPrefsUtil = new SharedPrefsUtil(this);
        firebaseAuth = FirebaseAuth.getInstance();
        mApiClient = new ApiClient();
        mApiClient.setIsDebug(true);

        recyclerView = findViewById(R.id.basketItems);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAccessToken();
                showCheckoutDialog();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BasketActivity.this, HomeActivity.class));
            }
        });

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_GLOBAL);
                    getFirebaseRegId();

                } else if (intent.getAction().equals(PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    NotificationUtils.createNotification(getApplicationContext(), message);
                    showResultDialog(message);
                }
            }
        };
        getFirebaseRegId();
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
                    @SuppressLint("SetTextI18n")
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Basket model) {
                        holder.txtProductQuantity.setText("QTY: " + model.getQuantity());
                        holder.txtProductName.setText(model.getName());
                        holder.txtProductPrice.setText("KSH " + model.getPrice());

                        int oneProductTPrice = ((Integer.parseInt(model.getPrice()))) * Integer.parseInt(model.getQuantity());
                        totalPrice += oneProductTPrice;

                        totalAmountTV.setText("KSH " + String.valueOf(totalPrice));

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    Boolean isReady = false;

    public void getAccessToken() {
        mApiClient.setGetAccessToken(true);
        mApiClient.mpesaService().getAccessToken().enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(@NonNull Call<AccessToken> call, @NonNull Response<AccessToken> response) {

                if (response.isSuccessful()) {
                    mApiClient.setAuthToken(response.body().accessToken);
//                    performSTKPush("0712000928");
                    isReady = true;
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessToken> call, @NonNull Throwable t) {

            }
        });
    }

    public void showCheckoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.checkout_dialog_title, totalPrice));

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        input.setHint("07xxxxxxxx");
        builder.setView(input);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            String phone_number = input.getText().toString();
            performSTKPush(phone_number);
        });

        builder.show();
    }

    public void performSTKPush(String phone_number) {
        mProgressDialog.setMessage(getString(R.string.dialog_message_processing));
        mProgressDialog.setTitle(getString(R.string.title_wait));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
        String timestamp = Utils.getTimestamp();
        STKPush stkPush = new STKPush(
                BUSINESS_SHORT_CODE,
                Utils.getPassword(BUSINESS_SHORT_CODE, PASSKEY, timestamp),
                timestamp,
                TRANSACTION_TYPE,
                String.valueOf(totalPrice),
                Utils.sanitizePhoneNumber(phone_number),
                PARTYB,
                Utils.sanitizePhoneNumber(phone_number),
                CALLBACKURL,
                "test", //The account reference
                "test"  //The transaction description
        );

        mApiClient.setGetAccessToken(false);

        mApiClient.mpesaService().sendPush(stkPush).enqueue(new Callback<STKPush>() {
            @Override
            public void onResponse(@NonNull Call<STKPush> call, @NonNull Response<STKPush> response) {
                mProgressDialog.dismiss();
                try {
                    if (response.isSuccessful()) {
                        Timber.d("post submitted to API. %s", response.body());
                    } else {
                        Timber.e("Response %s", response.errorBody().string());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<STKPush> call, @NonNull Throwable t) {
                mProgressDialog.dismiss();
                Timber.e(t);
            }
        });
    }

    private void getFirebaseRegId() {
        mFireBaseRegId = mSharedPrefsUtil.getFirebaseRegistrationID();

        if (!TextUtils.isEmpty(mFireBaseRegId)) {
            mSharedPrefsUtil.saveFirebaseRegistrationID(mFireBaseRegId);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


    public void showResultDialog(String result) {
        Timber.d(result);
        if (!mSharedPrefsUtil.getIsFirstTime()) {
            // run your one time code
            mSharedPrefsUtil.saveIsFirstTime(true);

            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText(getString(R.string.title_success))
                    .setContentText(getString(R.string.dialog_message_success))
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                        mSharedPrefsUtil.saveIsFirstTime(false);
                    })
                    .show();
        }
    }
}