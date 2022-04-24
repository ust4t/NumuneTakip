package com.mericoztiryaki.yasin.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.mericoztiryaki.yasin.R;

public class ImageActivity extends AppCompatActivity {

    private static final String PATH_KEY = "image_path";

    private PhotoView photoView;

    public static Intent newInstance(Context from, String url){
        Intent intent = new Intent(from, ImageActivity.class);
        intent.putExtra(PATH_KEY, url);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        this.getSupportActionBar().hide();

        String url = getIntent().getStringExtra(PATH_KEY);

        photoView = findViewById(R.id.photo_view);
        Glide.with(this).load(url).into(photoView);
    }
}
