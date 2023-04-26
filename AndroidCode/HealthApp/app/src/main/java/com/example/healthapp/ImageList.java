package com.example.healthapp;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageList extends ArrayAdapter<String> {
    private final Activity context;
    String names[];
    int images[];
    public ImageList(Activity context,String names[],int images[]) {
        super(context, R.layout.exercise_list_layout,names);
        //local to global value initialization
        this.context = context;
        this.names=names;
        this.images = images;
    }
    //automatically call this method to show entire list of array in list view
    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.exercise_list_layout, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        ImageView img = (ImageView) rowView.findViewById(R.id.myimage);
        txtTitle.setText(names[position]);
        Bitmap photo = BitmapFactory.decodeResource(context.getResources(), images[position]);
        img.setImageBitmap(photo);
        return rowView;
    }
}

