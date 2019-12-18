package com.example.friendster.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.friendster.R;
import com.example.friendster.adapter.ProfileViewPagerAdapter;
import com.example.friendster.model.User;
import com.example.friendster.rest.ApiClient;
import com.example.friendster.rest.services.request;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";


    @BindView(R.id.profile_cover)
    ImageView profileCover;
    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.profile_option_btn)
    Button profileOptionBtn;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.ViewPager_profile)
    ViewPager ViewPagerProfile;
    @BindView(R.id.loading_profile_bar)
    ProgressBar loadingProfileBar;
    @BindView(R.id.main_bar_layout)
    AppBarLayout mainBarLayout;
    @BindView(R.id.view_pager_layout)
    RelativeLayout viewPagerLayout;

    ProfileViewPagerAdapter profileViewPagerAdapter;
    String uid = "0";

    /*
    Desctiption of currentstate variable

    0=profile is still loading
    1=two people are friends
    2=this person has sent friend request to another person
    3=this person has received friend request from somenone
    4=two person are unknowns
    5=viewing our own profile
     */
    int curentstate = 0;

    /*
    two variables ccver url and profileurl to fetch image from url using using picasso
     */

    String coverurl = "", profileurl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        //fetching uid to display profile activity
        uid = getIntent().getStringExtra("uid");

        profileViewPagerAdapter = new ProfileViewPagerAdapter(getSupportFragmentManager(), 1);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back_white);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });

        ViewPagerProfile.setAdapter(profileViewPagerAdapter);

        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(uid)) {
            //we have opened our profile
            curentstate = 5;
            profileOptionBtn.setText("Edit Profile");

            loadprofile();
        } else {
            //we have opened our another person profile
        }
    }

    private void loadprofile() {
        request Request = ApiClient.getApiClient().create(request.class);
        Map<String, String> param = new HashMap<>();
        param.put("userid", uid);

        Call<User> call = Request.loadownProfile(param);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                loadingProfileBar.setVisibility(View.INVISIBLE);
                mainBarLayout.setVisibility(View.VISIBLE);
                viewPagerLayout.setVisibility(View.VISIBLE);

                if (response.body() != null) {
                    profileurl = response.body().getProfileUrl();
                    coverurl = response.body().getCoverUrl();
                    collapsingToolbar.setTitle(response.body().getName());

                    if (!profileurl.isEmpty()) {
                        Picasso.get().load(profileurl).networkPolicy(NetworkPolicy.OFFLINE).into(profileImage, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(profileurl).into(profileImage);
                            }
                        });
                    }

                    if (!coverurl.isEmpty()) {
                        Picasso.get().load(coverurl).networkPolicy(NetworkPolicy.OFFLINE).into(profileCover, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(coverurl).into(profileCover);
                            }
                        });
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Something Went Wrong........Please try again later", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
