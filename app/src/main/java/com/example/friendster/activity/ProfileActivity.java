package com.example.friendster.activity;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {
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
    File Compressedimagefile;


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

    int imageuploadtype=0;
    /*
    0 for profile image
    1 for cover image
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

        //edit profile button
        profileOptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(curentstate == 5)
                {
                    CharSequence options[] = new CharSequence[]{"Change Cover Profile", "Change Profile Picture"};
                    AlertDialog.Builder builder = CreateAlertdialog(options);
                    AlertDialog alertDialog=builder.create();
                    alertDialog.show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(ImagePicker.shouldHandle(requestCode,resultCode,data)){
            Image selectedimage=ImagePicker.getFirstImageOrNull(data);

            try {
                Compressedimagefile=new Compressor(this)
                        .setQuality(75)
                        .compressToFile(new File(selectedimage.getPath()));

                uploadfile(Compressedimagefile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {

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
                        Picasso.get().load(profileurl).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.progress).into(profileImage, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(profileurl).placeholder(R.drawable.progress).into(profileImage);
                            }
                        });
                    }

                    if (!coverurl.isEmpty()) {
                        Picasso.get().load(coverurl).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.progress).into(profileCover, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(coverurl).placeholder(R.drawable.progress).into(profileCover);
                            }
                        });
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Something Went Wrong........Please try again later", Toast.LENGTH_SHORT).show();
                }
                addImageCoverClick();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addImageCoverClick() {
        profileCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFullImage(profileCover,coverurl);
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFullImage(profileImage,profileurl);
            }
        });
    }


    private AlertDialog.Builder CreateAlertdialog(CharSequence[] options) {
        AlertDialog.Builder builder=new AlertDialog.Builder(ProfileActivity.this);
        builder.setOnDismissListener(ProfileActivity.this);
        builder.setTitle("Choose Options");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i)
                {
                    case 0:
                        imageuploadtype=1;
                        ImagePicker.create(ProfileActivity.this)
                                .folderMode(true)
                                .toolbarFolderTitle("Choose a folder")
                                .toolbarImageTitle("Select a image")
                                .start();
                        //load cover profile
                        break;

                    case 1:
                        imageuploadtype=0;
                        ImagePicker.create(ProfileActivity.this)
                                .folderMode(true)
                                .toolbarFolderTitle("Choose a folder")
                                .toolbarImageTitle("Select a image")
                                .start();
                        //load profile picture
                        break;
                }
            }
        });
        return builder;
    }

    private void uploadfile(File compressedimagefile) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        builder.addFormDataPart("postUserId",FirebaseAuth.getInstance().getCurrentUser().getUid());
        builder.addFormDataPart("imageuploadtype",imageuploadtype+"");
        builder.addFormDataPart("file",Compressedimagefile.getName(), RequestBody.create(MediaType.parse("multipart/form-data"),Compressedimagefile));


        MultipartBody multipartBody=builder.build();

        request Request= ApiClient.getApiClient().create(request.class);
        Call<Integer>req=Request.uploadImage(multipartBody);
        req.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {

                if(response.body()!=null && response.body()==1)
                {
                   if(imageuploadtype==0)
                   {
                       Picasso.get().load(Compressedimagefile).placeholder(R.drawable.progress).networkPolicy(NetworkPolicy.OFFLINE).into(profileImage, new com.squareup.picasso.Callback() {
                           @Override
                           public void onSuccess() {

                           }

                           @Override
                           public void onError(Exception e) {
                               Picasso.get().load(Compressedimagefile).placeholder(R.drawable.progress).into(profileImage);
                           }
                       });
                       Toast.makeText(ProfileActivity.this,"Profile picture changed successfully",Toast.LENGTH_SHORT).show();
                   }
                   else if(imageuploadtype==1) {
                       Picasso.get().load(Compressedimagefile).placeholder(R.drawable.progress).networkPolicy(NetworkPolicy.OFFLINE).into(profileCover, new com.squareup.picasso.Callback() {
                           @Override
                           public void onSuccess() {

                           }

                           @Override
                           public void onError(Exception e) {
                               Picasso.get().load(Compressedimagefile).placeholder(R.drawable.progress).into(profileCover);
                           }
                       });
                       Toast.makeText(ProfileActivity.this,"Profile Cover changed successfully",Toast.LENGTH_SHORT).show();
                   }
                }
                else
                {
                    Toast.makeText(ProfileActivity.this,"Something Went Wrong",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(ProfileActivity.this,"Somethhing went wrong",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void viewFullImage(View view, String url) {

       Intent intent = new Intent(ProfileActivity.this, FullImageActivity.class);
       intent.putExtra("imageurl", url);

       Pair[] pairs = new Pair[1];
       pairs[0] = new Pair<View,String>(view, "shared");

        ActivityOptions options = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            options = ActivityOptions.makeSceneTransitionAnimation(ProfileActivity.this,pairs);
            startActivity(intent, options.toBundle());
        }
        else {
            startActivity(intent);
        }
    }
}
