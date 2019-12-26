package com.example.friendster.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendster.R;
import com.example.friendster.activity.ProfileActivity;
import com.example.friendster.model.FriendModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.Viewholder> {
    Context context;
    List<FriendModel.Friend>friends;

    public FriendAdapter(Context context, List<FriendModel.Friend>friends)
    {
        this.context = context;
        this.friends = friends;
    }
    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friends, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        //loading profile image
        Picasso.get().load(friends.get(position).getProfileUrl()).placeholder(R.drawable.img_default_user).networkPolicy(NetworkPolicy.OFFLINE).into(holder.activityProfileSingle, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(friends.get(position).getProfileUrl()).placeholder(R.drawable.img_default_user).into(holder.activityProfileSingle);
            }
        });

        //loading name
        holder.activityTitleSingle.setText(friends.get(position).getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("uid", friends.get(position).getUid());
                context.startActivity(intent);
            }
        });

        //hiding visibility of accept button

        holder.actionBtn.setVisibility(View.INVISIBLE);


    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        @BindView(R.id.activity_profile_single)
        CircleImageView activityProfileSingle;
        @BindView(R.id.activity_title_single)
        TextView activityTitleSingle;
        @BindView(R.id.action_btn)
        Button actionBtn;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
