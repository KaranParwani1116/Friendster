package com.example.friendster.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendster.R;
import com.example.friendster.activity.FullPostActivity;
import com.example.friendster.activity.ProfileActivity;
import com.example.friendster.model.NotificationModel;
import com.example.friendster.utils.AgoDateParse;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.viewholder> {

    private static final String TAG = "NotificationAdapter";
    private Context context;
    private List<NotificationModel>notificationModels;

    public NotificationAdapter(Context context, List<NotificationModel>notificationModels)
    {
        this.context = context;
        this.notificationModels = notificationModels;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        Log.i(TAG, "onBindViewHolder: ");

        NotificationModel notificationModel = notificationModels.get(position);

        Picasso.get().load(notificationModel.getProfileUrl()).placeholder(R.drawable.img_default_user).networkPolicy(NetworkPolicy.OFFLINE).
                into(holder.notficationSenderProfile, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(notificationModel.getProfileUrl()).placeholder(R.drawable.img_default_user).into(holder.notficationSenderProfile);
                    }
                });

        /*
        type => [1.Liked your post
                 2.Commented on your post
                 3.replied to your comment
                 4.send you friend request
                 5.Accepted your friend request]
         */

        switch (notificationModel.getType()) {
            case "1":
                holder.notificationTitle.setText(notificationModel.getName() + " liked your post");
                holder.notificationBody.setText(notificationModel.getPost() + "");
                break;
            case "2":
                holder.notificationTitle.setText(notificationModel.getName() + " commented your post");
                holder.notificationBody.setText(notificationModel.getPost() + "");
                break;
            case "3":
                holder.notificationTitle.setText(notificationModel.getName() + " replied on your comment");
                holder.notificationBody.setText(notificationModel.getPost() + "");
                break;
            case "4":
                holder.notificationTitle.setText(notificationModel.getName() + " send you friend request");
                holder.notificationBody.setVisibility(View.GONE);
                break;
            case "5":
                holder.notificationTitle.setText(notificationModel.getName() + " accepted your friend request");
                holder.notificationBody.setVisibility(View.GONE);
                break;
        }

        try {
            holder.notificationDate.setText(AgoDateParse.getTimeAgo(AgoDateParse.getTimeInMillsecond(notificationModel.getNotificationTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

       if(notificationModel.getType().equals("1") || notificationModel.getType().equals("2") || notificationModel.getType().equals("3"))
       {
           holder.itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Intent intent = new Intent(context, FullPostActivity.class);
                   Bundle bundle = new Bundle();
                   bundle.putBoolean("isLoadFromNetwork",true);
                   bundle.putString("postid",notificationModel.getPostId());
                   intent.putExtra("postBundle",bundle);
                   context.startActivity(intent);
               }
           });
       }
       else{
           holder.itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Intent intent = new Intent(context, ProfileActivity.class);
                   intent.putExtra("uid",notificationModel.getNotificationFrom());
                   context.startActivity(intent);
               }
           });
       }
    }

    @Override
    public int getItemCount() {
        return notificationModels.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {

        @BindView(R.id.notfication_sender_profile)
        CircleImageView notficationSenderProfile;
        @BindView(R.id.notification_title)
        TextView notificationTitle;
        @BindView(R.id.notification_body)
        TextView notificationBody;
        @BindView(R.id.notification_date)
        TextView notificationDate;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
