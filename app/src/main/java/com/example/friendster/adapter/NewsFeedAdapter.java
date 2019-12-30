package com.example.friendster.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendster.R;
import com.example.friendster.activity.FullPostActivity;
import com.example.friendster.model.PostModel;
import com.example.friendster.rest.ApiClient;
import com.example.friendster.rest.services.request;
import com.example.friendster.utils.AgoDateParse;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.text.ParseException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.Viewholder> {
    Context context;
    List<PostModel> posts;


    public NewsFeedAdapter(Context context, List<PostModel> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        PostModel postModel = posts.get(position);

        holder.peopleName.setText(postModel.getName());

        if(postModel.getPost()!=null && postModel.getPost().length()>1)
        {
            holder.post.setText(postModel.getPost());
        }

        Picasso.get().load(postModel.getProfileUrl()).placeholder(R.drawable.img_default_user).networkPolicy(NetworkPolicy.OFFLINE).
                into(holder.peopleImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                      Picasso.get().load(postModel.getProfileUrl()).placeholder(R.drawable.img_default_user).into(holder.peopleImage);
                    }
                });


        if(postModel.getPrivacy().equals("0"))
        {
            holder.privacyIcon.setImageResource(R.drawable.icon_friends);
        }
        else if(postModel.getPrivacy().equals("1"))
        {
            holder.privacyIcon.setImageResource(R.drawable.icon_onlyme);
        }
        else
        {
            holder.privacyIcon.setImageResource(R.drawable.icon_public);
        }

        if(!postModel.getStatusImage().isEmpty()){
            Picasso.get().load(ApiClient.Base_Url_1 + postModel.getStatusImage()).placeholder(R.drawable.default_image_placeholder).networkPolicy(NetworkPolicy.OFFLINE).
                    into(holder.statusImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(ApiClient.Base_Url_1 + postModel.getStatusImage()).placeholder(R.drawable.default_image_placeholder).into(holder.statusImage);
                        }
                    });
        }
        else
        {
            holder.statusImage.setVisibility(View.GONE);
        }

        try {
            holder.date.setText(AgoDateParse.getTimeAgo(AgoDateParse.getTimeInMillsecond(postModel.getStatusTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(postModel.isLiked())
        {
            holder.likeImg.setImageResource(R.drawable.icon_like_selected);
        }
        else
        {
            holder.likeImg.setImageResource(R.drawable.icon_like);
        }

        //taking likes count

        if(postModel.getLikeCount().equals("0") || postModel.getLikeCount().equals("1") )
        {
            holder.likeTxt.setText(postModel.getLikeCount() + " Like");
        }
        else{
            holder.likeTxt.setText(postModel.getLikeCount() + " Likes");
        }

        //handling like and unlike feature

        holder.likeSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.likeSection.setEnabled(false);
                if(!postModel.isLiked())
                {
                    //handling like operation
                    operationlike(holder,postModel);

                    request Request = ApiClient.getApiClient().create(request.class);
                    Call<Integer>call = Request.likeunlike(new Addlike(FirebaseAuth.getInstance().getCurrentUser().getUid(),postModel.getPostid(),postModel.getPostUserId(),1));

                    call.enqueue(new retrofit2.Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            holder.likeSection.setEnabled(true);
                            if(response.isSuccessful())
                            {
                                if (response.body()!=null)
                                {
                                    if(response.body().equals(0))
                                    {
                                        operationunlike(holder,postModel);
                                        Toast.makeText(context,"Something went wrong...",Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else
                                {
                                    operationunlike(holder,postModel);
                                    Toast.makeText(context,"Something went wrong...",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            operationunlike(holder,postModel);
                            Toast.makeText(context,"Something went wrong...",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else
                {
                    //handling unlike operation
                    operationunlike(holder,postModel);
                    request Request = ApiClient.getApiClient().create(request.class);
                    Call<Integer>call = Request.likeunlike(new Addlike(FirebaseAuth.getInstance().getCurrentUser().getUid(),postModel.getPostid(),postModel.getPostUserId(),0));

                    call.enqueue(new retrofit2.Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            holder.likeSection.setEnabled(true);
                            if(response.isSuccessful())
                            {
                                if (response.body()!=null)
                                {

                                }
                                else
                                {
                                    operationlike(holder,postModel);
                                    Toast.makeText(context,"Something went wrong...",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            operationlike(holder,postModel);
                            Toast.makeText(context,"Something went wrong...",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FullPostActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("postModel", Parcels.wrap(postModel));
                intent.putExtra("postBundle",bundle);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    private void operationlike(Viewholder holder,PostModel postModel)
    {
        holder.likeImg.setImageResource(R.drawable.icon_like_selected);
        int count = Integer.parseInt(postModel.getLikeCount());
        count++;

        if(count==0 || count==1)
        {
            holder.likeTxt.setText(count + " Like");
        }
        else{
            holder.likeTxt.setText(count + " Likes");
        }

    }

    private void operationunlike(Viewholder holder,PostModel postModel)
    {
        holder.likeImg.setImageResource(R.drawable.icon_like);
        int count = Integer.parseInt(postModel.getLikeCount());
        count--;

        if(count==0 || count==1)
        {
            holder.likeTxt.setText(count + " Like");
        }
        else{
            holder.likeTxt.setText(count + " Likes");
        }

        posts.get(holder.getAdapterPosition()).setLikeCount(count+"");
        posts.get(holder.getAdapterPosition()).setLiked(false);
    }



    public class Viewholder extends RecyclerView.ViewHolder {

        @BindView(R.id.people_image)
        ImageView peopleImage;
        @BindView(R.id.people_name)
        TextView peopleName;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.privacy_icon)
        ImageView privacyIcon;
        @BindView(R.id.memory_meta_rel)
        RelativeLayout memoryMetaRel;
        @BindView(R.id.post)
        TextView post;
        @BindView(R.id.status_image)
        ImageView statusImage;
        @BindView(R.id.like_img)
        ImageView likeImg;
        @BindView(R.id.like_txt)
        TextView likeTxt;
        @BindView(R.id.likeSection)
        LinearLayout likeSection;
        @BindView(R.id.comment_img)
        ImageView commentImg;
        @BindView(R.id.comment_txt)
        TextView commentTxt;
        @BindView(R.id.commentSection)
        LinearLayout commentSection;


        public Viewholder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class Addlike{
        String userId,postId,contentOwnerId;
        int operationType;

        public Addlike(String userId, String postId, String contentOwnerId, int operationType) {
            this.userId = userId;
            this.postId = postId;
            this.contentOwnerId = contentOwnerId;
            this.operationType = operationType;
        }
    }
}
