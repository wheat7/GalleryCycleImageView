package com.wheat7.gallerycycleimageview;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.wheat7.library.GalleryCycleImageView;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private GalleryCycleImageView mGalleryCycleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mGalleryCycleImageView = (GalleryCycleImageView) findViewById(R.id.gallery_cycle_image);
        mGalleryCycleImageView.setCount(6);
        mGalleryCycleImageView.setGalleyCycleImageListener(new GalleryCycleImageView.GalleyCycleImageListener() {
            @Override
            public View setItem(View imageView, final int position) {
                imageView.setBackgroundResource(R.drawable.dali);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext,"Position=" + position,Toast.LENGTH_SHORT).show();
                    }
                });
                return imageView;
            }
        });
        mGalleryCycleImageView.startAutoPlay();
    }
}
