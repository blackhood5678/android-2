package com.pu.facebook.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pu.facebook.Post;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "facebookdb";
    private static final String TABLE_USERS = "users";
    private static final String KEY_USER_ID = "userid";
    private static final String KEY_FIRSTNAME = "firstname";
    private static final String KEY_LASTNAME = "lastname";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_GENDER = "gender";


    private static final String TABLE_POST = "posts";
    private static final String COLUMN_POST_ID = "postid";
    private static final String COLUMN_USER_ID = "userid";
    private static final String COLUMN_POST_DESCRIPTION = "post_desc";
    private static final String COLUMN_POST_IMAGE = "post_image";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_FIRSTNAME + " TEXT,"
                + KEY_LASTNAME + " TEXT,"
                + KEY_EMAIL + " TEXT,"
                + KEY_PASSWORD + " TEXT,"
                + KEY_GENDER + " TEXT"
                + ")";
        db.execSQL(CREATE_USER_TABLE);

        String sql = "CREATE TABLE " + TABLE_POST
                + "(" + COLUMN_POST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_ID + " VARCHAR, "
                + COLUMN_POST_IMAGE + " VARCHAR, "
                + COLUMN_POST_DESCRIPTION + " VARCHAR);";
        db.execSQL(sql);


    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        // Create tables again
        onCreate(db);
    }

    public void addUser(String first, String last, String email, String password, String gender) {


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_FIRSTNAME, first);
        contentValues.put(KEY_LASTNAME, last);
        contentValues.put(KEY_EMAIL, email);
        contentValues.put(KEY_PASSWORD, password);
        contentValues.put(KEY_GENDER, gender);


        db.insert(TABLE_USERS, null, contentValues);

        db.close();

    }

    public void addPost(String desc, String image, String userid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_POST_DESCRIPTION, desc);
        contentValues.put(COLUMN_POST_IMAGE, image);
        contentValues.put(COLUMN_USER_ID, userid);


        db.insert(TABLE_POST, null, contentValues);
        db.close();
    }

    public void editPost(String desc, String image, int postid) {


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_POST_DESCRIPTION, desc);
        contentValues.put(COLUMN_POST_IMAGE, image);
        db.update(TABLE_POST, contentValues, "postid=" + postid, null);

        db.close();

    }

    public List<Post> getPOSTs() {
        List<Post> postList = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  a.postid,a.post_desc,a.post_image,b.firstname,b.lastname FROM " + TABLE_POST + " a, "
                + TABLE_USERS + " b where a.userid=b.userid" +
                " order by a.postid desc";
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                // Log.e("News Title", cursor.getString(cursor.getColumnIndex("news_heading")));
                Post post = new Post(cursor.getString(cursor.getColumnIndex("postid")), cursor.getString(cursor.getColumnIndex("firstname")) + " " + cursor.getString(cursor.getColumnIndex("lastname")),
                        cursor.getString(cursor.getColumnIndex("post_desc")), cursor.getString(cursor.getColumnIndex("post_image")));


                postList.add(post);

            } while (cursor.moveToNext());
        }
        db.close();
        return postList;

    }

    public List<Post> getPOSTsById(int userid) {
        List<Post> postList = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  a.postid,a.post_desc,a.post_image,b.firstname,b.lastname FROM " + TABLE_POST + " a, "
                + TABLE_USERS + " b where a.userid=b.userid and a.userid=" + userid +
                " order by a.postid desc";
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                // Log.e("News Title", cursor.getString(cursor.getColumnIndex("news_heading")));
                Post post = new Post(cursor.getString(cursor.getColumnIndex("postid")), cursor.getString(cursor.getColumnIndex("firstname")) + " " + cursor.getString(cursor.getColumnIndex("lastname")),
                        cursor.getString(cursor.getColumnIndex("post_desc")), cursor.getString(cursor.getColumnIndex("post_image")));


                postList.add(post);

            } while (cursor.moveToNext());
        }
        db.close();
        return postList;

    }

    public int getPostCount() {
        int pCount = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select * from " + TABLE_POST;
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() > 0) {
            pCount = cursor.getCount();
        }
        return pCount;

    }


    public int getLogin(String email, String Pass) {
        int Active_From = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select userid from " + TABLE_USERS + " Where email='" + email + "' and password='" + Pass + "'";
        Cursor cursor = db.rawQuery(Query, null);


        if (cursor.getCount() > 0) {

            if (cursor.moveToFirst()) {

                Active_From = cursor.getInt(0);

            }

        }
        return Active_From;
    }

    public String getName(int userid) {
        String username = "";
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select firstname,lastname from " + TABLE_USERS + " Where userid=" + userid;
        Cursor cursor = db.rawQuery(Query, null);


        if (cursor.getCount() > 0) {

            if (cursor.moveToFirst()) {

                username = cursor.getString(0) + " " + cursor.getString(1);
                Log.e("USERNAME", username);

            }

        }
        return username;
    }

    public boolean delPost(int postid) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_POST, "postid=" + postid, null) > 0;
    }

    public enum DatabaseManager {
        INSTANCE;
        private SQLiteDatabase db;
        private boolean isDbClosed = true;
        DatabaseHandler dbHelper;

        public void init(Context context) {
            dbHelper = new DatabaseHandler(context);
            if (isDbClosed) {
                isDbClosed = false;
                this.db = dbHelper.getWritableDatabase();
            }

        }


        public boolean isDatabaseClosed() {
            return isDbClosed;
        }

        public void closeDatabase() {
            if (!isDbClosed && db != null) {
                isDbClosed = true;
                db.close();
                dbHelper.close();
            }
        }
    }
}
