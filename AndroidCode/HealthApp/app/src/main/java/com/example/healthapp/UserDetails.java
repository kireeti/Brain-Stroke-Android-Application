package com.example.healthapp;

public class UserDetails {
    static String username;
    static String date;
    static String prediction;
    static String suggestions;
public static void setDetails(String u, String d, String p, String s) {
    username = u;
    date = d;
    prediction = p;
    suggestions = s;
}
}
