package edisonchallenge.agaaz;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {
    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_USER = "user";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_TEMP = "temp";
    private static final String KEY_MOIST = "moisture";
    private static final String KEY_HUMID = "humidity";
    private static final String KEY_STYPE = "soiltype";
    private static final String KEY_FERT = "fertilizer";
    private static final String KEY_WATER = "water";
    private static final String KEY_NSTYPE = "nsoiltype";
    private static final String KEY_CROP = "crop";
    private static final String KEY_FSIZE = "fieldsize";
    private static final String KEY_DOS = "dateofsowing";


    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_LOCATION + " TEXT,"
                + KEY_TEMP + " TEXT,"+ KEY_MOIST + " TEXT,"+ KEY_HUMID + " TEXT,"+ KEY_STYPE + " TEXT,"
                + KEY_FERT + " TEXT,"+ KEY_WATER + " TEXT," + KEY_NSTYPE + " TEXT," + KEY_CROP + " TEXT,"
                + KEY_FSIZE + " TEXT," + KEY_DOS + " TEXT," + KEY_UID + " TEXT," + KEY_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String name, String email, String location, String temp, String moist, String humid,
                        String stype, String fert, String water, String uid, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_LOCATION, location);
        values.put(KEY_TEMP,temp);
        values.put(KEY_MOIST,moist);
        values.put(KEY_HUMID,humid);
        values.put(KEY_STYPE,stype);
        values.put(KEY_FERT,fert);
        values.put(KEY_WATER,water);
        values.put(KEY_UID, uid); // Email
        values.put(KEY_CREATED_AT, created_at); // Created At

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }


    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("location", cursor.getString(3));
            user.put("temp",cursor.getString(4));
            user.put("moist",cursor.getString(5));
            user.put("humid",cursor.getString(6));
            user.put("soiltype",cursor.getString(7));
            user.put("fertilizer",cursor.getString(8));
            user.put("water",cursor.getString(9));
            user.put("uid", cursor.getString(14));
            user.put("created_at", cursor.getString(15));

        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    //Update details of new harvest
    public void updateDetails(String nsoiltype, String crop, String fieldsize, String dateos){
        SQLiteDatabase db =this.getWritableDatabase();
        HashMap<String, String> user =getUserDetails();
        ContentValues values = new ContentValues();
        values.put(KEY_NSTYPE, nsoiltype);
        values.put(KEY_CROP, crop);
        values.put(KEY_FSIZE, fieldsize);
        values.put(KEY_DOS, dateos);
        db.update(TABLE_USER, values, KEY_EMAIL+ "=" + new String[]{String.valueOf(user.get("email"))}, null);
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}
