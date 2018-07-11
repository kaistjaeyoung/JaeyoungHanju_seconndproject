package com.example.q.myapplication;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.q.myapplication.R;

import java.util.ArrayList;

public class Imageupg extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageupg);

        Button closebutton = (Button) findViewById(R.id.btnClose);
        closebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ArrayList<String> pathList = getPathList(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] {MediaStore.Images.Media.DATA});
        final ImageSlideAdapter imageSlideAdapter = new ImageSlideAdapter(this, pathList );
        final ViewPager pager = findViewById(R.id.pager);
        pager.setAdapter(imageSlideAdapter);

        Intent i = getIntent();
        int position = i.getExtras().getInt("index");
        pager.setCurrentItem(position);
    }

    public ArrayList<String> getPathList( Uri uri, String[] projection){
        ArrayList<String> pathList = new ArrayList<>();
        Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
        if(cursor.moveToFirst()){
            do{
                pathList.add(cursor.getString(0));
            }while(cursor.moveToNext());
        }
        return pathList;
    }
}