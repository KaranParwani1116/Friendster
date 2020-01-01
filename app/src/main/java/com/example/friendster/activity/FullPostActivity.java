package com.example.friendster.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;

import com.example.friendster.Frsgments.bottomsheets.CommentBottomSheet;
import com.example.friendster.R;
import com.example.friendster.adapter.NewsFeedAdapter;
import com.example.friendster.model.PostModel;
import com.example.friendster.rest.ApiClient;
import com.example.friendster.rest.services.request;
import com.example.friendster.utils.AgoDateParse;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.text.ParseException;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class FullPostActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.post_user_image)
    ImageView postUserImage;
    @BindView(R.id.post_user_name)
    TextView postUserName;
    @BindView(R.id.privacy)
    ImageView privacy;
    @BindView(R.id.post_date)
    TextView postDate;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.post_image)
    ImageView postImage;
    @BindView(R.id.top_rel)
    RelativeLayout topRel;
    @BindView(R.id.like_img)
    ImageView likeImg;
    @BindView(R.id.like_txt)
    TextView likeTxt;
    @BindView(R.id.like_section)
    LinearLayout likeSection;
    @BindView(R.id.comment_txt)
    TextView commentTxt;
    @BindView(R.id.comment_section)
    LinearLayout commentSection;
    @BindView(R.id.reaction_card)
    CardView reactionCard;
    @BindView(R.id.comment)
    EditText comment;
    @BindView(R.id.comment_send)
    ImageView commentSend;
    @BindView(R.id.comment_send_wrapper)
    RelativeLayout commentSendWrapper;
    @BindView(R.id.comment_bottom_part)
    LinearLayout commentBottomPart;
    @BindView(R.id.top_hide_show)
    RelativeLayout topHideShow;

    PostModel postModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_post);
        ButterKnife.bind(this);

        postModel = Parcels.unwrap(getIntent().getBundleExtra("postBundle").getParcelable("postModel"));

        if (postModel == null) {
            Toast.makeText(this, "Something Went Wrong...", Toast.LENGTH_SHORT).show();
            onBackPressed();
            finish();
        }

        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.arrow_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        setData(postModel);
    }

    private void setData(PostModel postModel) {
        postUserName.setText(postModel.getName());
        status.setText(postModel.getPost());

        Picasso.get().load(postModel.getProfileUrl()).placeholder(R.drawable.img_default_user).networkPolicy(NetworkPolicy.OFFLINE)
                .into(postUserImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(postModel.getProfileUrl()).placeholder(R.drawable.img_default_user).into(postUserImage);
                    }
                });

        if (!postModel.getStatusImage().isEmpty()) {
            Picasso.get().load(ApiClient.Base_Url_1 + postModel.getStatusImage()).placeholder(R.drawable.default_image_placeholder).networkPolicy(NetworkPolicy.OFFLINE)
                    .into(postImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(ApiClient.Base_Url_1 + postModel.getStatusImage()).placeholder(R.drawable.default_image_placeholder).into(postImage);
                        }
                    });
        } else {
            postImage.setVisibility(View.GONE);
        }

        try {
            postDate.setText(AgoDateParse.getTimeAgo(AgoDateParse.getTimeInMillsecond(postModel.getStatusTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (postModel.getPrivacy().equals("0")) {
            privacy.setImageResource(R.drawable.icon_friends);
        } else if (postModel.getPrivacy().equals("1")) {
            privacy.setImageResource(R.drawable.icon_onlyme);
        } else {
            privacy.setImageResource(R.drawable.icon_public);
        }

        if (postModel.isLiked()) {
            likeImg.setImageResource(R.drawable.icon_like_selected);
        } else {
            likeImg.setImageResource(R.drawable.icon_like);
        }
        int count = Integer.parseInt(postModel.getLikeCount());

        if (count == 0 || count == 1) {
            likeTxt.setText(count + " Like");
        } else {
            likeTxt.setText(count + " Likes");
        }

        likeSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!postModel.isLiked())
                {
                    //handling like operation
                    operationlike(postModel);

                    request Request = ApiClient.getApiClient().create(request.class);
                    Call<Integer>call = Request.likeunlike(new NewsFeedAdapter.Addlike(FirebaseAuth.getInstance().getCurrentUser().getUid(),postModel.getPostid(),postModel.getPostUserId(),1));

                    call.enqueue(new retrofit2.Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            likeSection.setEnabled(true);
                            if(response.isSuccessful())
                            {
                                if (response.body()!=null)
                                {
                                    if(response.body().equals(0))
                                    {
                                        operationunlike(postModel);
                                        Toast.makeText(FullPostActivity.this,"Something went wrong...",Toast.LENGTH_SHORT).show();
                                    }
                                    else {

                                    }
                                }
                                else
                                {
                                    operationunlike(postModel);
                                    Toast.makeText(FullPostActivity.this,"Something went wrong...",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            operationunlike(postModel);
                            Toast.makeText(FullPostActivity.this,"Something went wrong...",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else
                {
                    //handling unlike operation
                    operationunlike(postModel);
                    request Request = ApiClient.getApiClient().create(request.class);
                    Call<Integer>call = Request.likeunlike(new NewsFeedAdapter.Addlike(FirebaseAuth.getInstance().getCurrentUser().getUid(),postModel.getPostid(),postModel.getPostUserId(),0));

                    call.enqueue(new retrofit2.Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            likeSection.setEnabled(true);
                            if(response.isSuccessful())
                            {
                                if (response.body()!=null)
                                {

                                }
                                else
                                {
                                    operationlike(postModel);
                                    Toast.makeText(FullPostActivity.this,"Something went wrong...",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            operationlike(postModel);
                            Toast.makeText(FullPostActivity.this,"Something went wrong...",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        if(postModel.getHasComment().equals("1"))
        {
            if(postModel.getCommentCount().equals("1"))
            {
                commentTxt.setText("1 Comment");
            }
            else
            {
                int commentcount = Integer.parseInt(postModel.getCommentCount());

                commentTxt.setText(commentcount + " Comments");
            }
        }
        else
        {
            commentTxt.setText("0 Comment");
        }

       commentSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialogFragment bottomSheetDialogFragment=new CommentBottomSheet();
                Bundle bundle = new Bundle();
                bundle.putParcelable("postModel",Parcels.wrap(postModel));
                bottomSheetDialogFragment.setArguments(bundle);
                FragmentActivity fragmentActivity = (FragmentActivity)FullPostActivity.this;
                bottomSheetDialogFragment.show(fragmentActivity.getSupportFragmentManager(),"commentFragment");
            }
        });
    }

    private void operationlike(PostModel postModel)
    {
        likeImg.setImageResource(R.drawable.icon_like_selected);
        int count = Integer.parseInt(postModel.getLikeCount());
        count++;
        postModel.setLiked(true);
        postModel.setLikeCount(count+"");

        if(count==0 || count==1)
        {
            likeTxt.setText(count + " Like");
        }
        else{
            likeTxt.setText(count + " Likes");
        }

    }
    private void operationunlike(PostModel postModel)
    {
        likeImg.setImageResource(R.drawable.icon_like);
        int count = Integer.parseInt(postModel.getLikeCount());
        count--;

        if(count==0 || count==1)
        {
            likeTxt.setText(count + " Like");
        }
        else{
            likeTxt.setText(count + " Likes");
        }

        postModel.setLikeCount(count+"");
        postModel.setLiked(true);
    }
}
