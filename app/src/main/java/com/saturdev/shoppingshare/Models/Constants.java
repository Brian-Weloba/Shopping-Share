package com.saturdev.shoppingshare.Models;

import com.saturdev.shoppingshare.BuildConfig;

public class Constants {
    public static final String FIREBASE_CHILD_USERS = "users";
    public static final String PREFERENCES_USERNAME_KEY = "username" ;
    public static final String EMAIL_KEY = "email" ;
    public static final String PHONE_NO = "phone";
    public static final  String CONSUMER_KEY= BuildConfig.CONSUMER_KEY;
    public static final  String CONSUMER_SECRET= BuildConfig.CONSUMER_SECRET;
    public static final int CONNECT_TIMEOUT = 60 * 1000;
    /**
     * Connection Read timeout duration
     */
    public static final int READ_TIMEOUT = 60 * 1000;
    /**
     * Connection write timeout duration
     */
    public static final int WRITE_TIMEOUT = 60 * 1000;
    /**
     * Base URL
     */
    public static final String BASE_URL = "https://sandbox.safaricom.co.ke/";
    /**
     * global topic to receive app wide push notifications
     */
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
    public static final String SHARED_PREF = "ah_firebase";

    //STKPush Properties
    public static final String BUSINESS_SHORT_CODE = "174379";
    public static final String PASSKEY = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919";
    public static final String TRANSACTION_TYPE = "CustomerPayBillOnline";
    public static final String PARTYB = "174379";
    public static final String CALLBACKURL = "https://saturday.co.ke/";
}
