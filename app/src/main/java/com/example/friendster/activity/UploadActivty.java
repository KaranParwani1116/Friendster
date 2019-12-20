package com.example.friendster.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.example.friendster.R;
import com.example.friendster.rest.ApiClient;
import com.example.friendster.rest.services.request;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

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

public class UploadActivty extends AppCompatActivity {

    @BindView(R.id.privacy_spinner)
    Spinner privacySpinner;
    @BindView(R.id.postBtnTxt)
    TextView postBtnTxt;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.dialogAvatar)
    CircleImageView dialogAvatar;
    @BindView(R.id.status_edit)
    EditText statusEdit;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.add_image)
    Button addImage;

    String imageUploadUrl="";
    boolean isimageSelected=false;
    File Compressedimagefile=null;
    int privacylevel=0;

    /*
     privacy level description

      0=>Friends
      1=>Onlyme
      2=>Public
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_activty);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.arrow_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UploadActivty.this,MainActivity.class));
            }
        });

        privacySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                privacylevel=i;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
               privacylevel=0;
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.create(UploadActivty.this)
                        .folderMode(true)
                        .single()
                        .start();
            }
        });

        postBtnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPost();
            }
        });
    }

    private void uploadPost() {
        String status=statusEdit.getText().toString();
        String userid= FirebaseAuth.getInstance().getCurrentUser().getUid();

        if(status.trim().length()>0 || isimageSelected)
        {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);

            builder.addFormDataPart("post",status);
            builder.addFormDataPart("postUserId",FirebaseAuth.getInstance().getCurrentUser().getUid());
            builder.addFormDataPart("privacy",privacylevel+"");


            if(isimageSelected){
                builder.addFormDataPart("isimageSelected","1");
                builder.addFormDataPart("file",Compressedimagefile.getName(), RequestBody.create(MediaType.parse("multipart/form-data"),Compressedimagefile));
            }else{
                builder.addFormDataPart("isimageSelected","0");
            }

            MultipartBody multipartBody=builder.build();

            request Request= ApiClient.getApiClient().create(request.class);
            Call<Integer>req=Request.uploadstatus(multipartBody);
            req.enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {

                    if(response.body()!=null && response.body()==1)
                    {
                        Toast.makeText(UploadActivty.this,"Post is Successfull",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(UploadActivty.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(UploadActivty.this,"Something Went Wrong",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    Toast.makeText(UploadActivty.this,"Somethhing went wrong",Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(this,"Please write your post first",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(ImagePicker.shouldHandle(requestCode,resultCode,data)){
            Image selectedimage=ImagePicker.getFirstImageOrNull(data);

            try {
                Compressedimagefile=new Compressor(this)
                        .setQuality(75)
                        .compressToFile(new File(selectedimage.getPath()));

                isimageSelected=true;

                Picasso.get().load(new File(selectedimage.getPath())).placeholder(R.drawable.default_image_placeholder).into(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }
}
