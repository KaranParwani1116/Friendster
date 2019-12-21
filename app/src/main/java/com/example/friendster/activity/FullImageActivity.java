package com.example.friendster.activity;

import android.os.Bundle;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.example.friendster.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FullImageActivity extends AppCompatActivity {

    @BindView(R.id.full_image_id)
    PhotoView fullImageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        ButterKnife.bind(this);

        final String imageurl = getIntent().getStringExtra("imageurl");

        if(!imageurl.isEmpty())
        {
            Picasso.get().load(imageurl).networkPolicy(NetworkPolicy.OFFLINE).into(fullImageId, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                   Picasso.get().load(imageurl).into(fullImageId);
                }
            });
        }
    }
}
