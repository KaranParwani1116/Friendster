package com.example.friendster.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendster.R;
import com.example.friendster.activity.ProfileActivity;
import com.example.friendster.model.FriendModel;
import com.example.friendster.rest.ApiClient;
import com.example.friendster.rest.services.request;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Response;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.Viewholder> {
    Context context;
    List<FriendModel.Request> requests;

    public FriendRequestAdapter(Context context, List<FriendModel.Request> requests) {
        this.context = context;
        this.requests = requests;
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
        Picasso.get().load(requests.get(position).getProfileUrl()).placeholder(R.drawable.img_default_user).networkPolicy(NetworkPolicy.OFFLINE).into(holder.activityProfileSingle, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(requests.get(position).getProfileUrl()).placeholder(R.drawable.img_default_user).into(holder.activityProfileSingle);
            }
        });

        //loading name
        holder.activityTitleSingle.setText(requests.get(position).getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("uid", requests.get(position).getUid());
                context.startActivity(intent);
            }
        });

        //action button
        holder.actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.actionBtn.setText("Processing");
                holder.actionBtn.setEnabled(false);
                actionperform(requests.get(position).getUid());
            }
        });


    }

    private void actionperform(String uid) {
        request Request = ApiClient.getApiClient().create(request.class);
        Call<Integer> call = Request.performAction(new ProfileActivity.PerformAction(3 + "", FirebaseAuth.getInstance().getCurrentUser().getUid(), uid));

        call.enqueue(new retrofit2.Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                context.startActivity(new Intent(context, ProfileActivity.class).putExtra("uid", uid));
                Toast.makeText(context,"You both are friends on friendster now",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
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
