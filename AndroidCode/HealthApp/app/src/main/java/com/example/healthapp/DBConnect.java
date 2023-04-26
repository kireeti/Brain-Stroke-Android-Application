package com.example.healthapp;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
public class DBConnect extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "healthapp.db";
    private HashMap hp;

    public DBConnect(Context context) {
        super(context, DATABASE_NAME, null, 1);
        //context.deleteDatabase(DATABASE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table register(username text primary key,password text,address text,email text,contactno text)");
        db.execSQL("create table activitities(username text,previous_activity text,suggestion text, previous_date text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS register");
        db.execSQL("DROP TABLE IF EXISTS activitities");
        onCreate(db);
    }

    public void saveActivity(String user, String predict, String suggestion, String date) {
        UserDetails.setDetails(user, date, predict, suggestion);
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Delete from activitities WHERE username = '"+user+"'";
        db.execSQL(query);
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", user);
        contentValues.put("previous_activity", predict);
        contentValues.put("suggestion", suggestion);
        contentValues.put("previous_date", date);
        db.insert("activitities", null, contentValues);
    }

    public String register(String user, String pass, String address, String email, String contact) {
        boolean flag = isUserExists(user);
        String error = "none";
        if (!flag) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("username", user);
            contentValues.put("password", pass);
            contentValues.put("address", address);
            contentValues.put("email", email);
            contentValues.put("contactno", contact);
            db.insert("register", null, contentValues);
            error = "success";
        } else {
            error = "Username already exists";
        }
        return error;
    }

    public void setDetails(String user) {
        UserDetails.setDetails(user, "No Data Available", "No Data Available", "No Data Available");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select username,previous_activity,suggestion, previous_date from activitities", null);
        while (res.moveToNext()) {
            @SuppressLint("Range") String id = res.getString(res.getColumnIndex("username"));
            if (id.equals(user)) {
                @SuppressLint("Range") String activities = res.getString(res.getColumnIndex("previous_activity"));
                @SuppressLint("Range") String suggestion = res.getString(res.getColumnIndex("suggestion"));
                @SuppressLint("Range") String date = res.getString(res.getColumnIndex("previous_date"));
                UserDetails.setDetails(user, date, activities, suggestion);
            }
        }
    }

    public boolean login(String user, String pass) {
        boolean flag = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select username,password from register", null);
        while (res.moveToNext()) {
            @SuppressLint("Range") String id = res.getString(res.getColumnIndex("username"));
            @SuppressLint("Range") String password = res.getString(res.getColumnIndex("password"));
            if (id.equals(user) && pass.equals(password)) {
                flag = true;
                setDetails(user);
                break;
            }
        }
        return flag;
    }

    public boolean isUserExists(String user) {
        boolean flag = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select username from register", null);
        while (res.moveToNext()) {
            @SuppressLint("Range") String id = res.getString(res.getColumnIndex("username"));
            if (id.equals(user)) {
                flag = true;
                break;
            }
        }
        return flag;
    }
}

