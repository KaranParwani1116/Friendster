package com.example.friendster.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendster.R;
import com.example.friendster.model.CommentModel;
import com.example.friendster.utils.AgoDateParse;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubCommentAdapter extends RecyclerView.Adapter<SubCommentAdapter.ViewHolder> {
    private Context context;
    private List<CommentModel.Comment> comments;


    public SubCommentAdapter(Context context, List<CommentModel.Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_single_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final CommentModel.Comment model = comments.get(position);
        holder.commentPerson.setText(model.getName());
        holder.commentBody.setText(model.getComment());

        Picasso.get().load(model.getProfileUrl()).placeholder(R.drawable.img_default_user).networkPolicy(NetworkPolicy.OFFLINE).into(holder.commentProfile, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(model.getProfileUrl()).placeholder(R.drawable.img_default_user).into(holder.commentProfile);
            }
        });

        try {
            holder.commentDate.setText(AgoDateParse.getTimeAgo(AgoDateParse.getTimeInMillsecond(model.getCommentDate())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.replyTxt.setVisibility(View.INVISIBLE);


    }

    @Override
    public int getItemCount() {
        return comments.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.comment_profile)
        ImageView commentProfile;
        @BindView(R.id.comment_person)
        TextView commentPerson;
        @BindView(R.id.option_id)
        ImageView optionId;
        @BindView(R.id.comment_body)
        TextView commentBody;
        @BindView(R.id.comment_date)
        TextView commentDate;
        @BindView(R.id.reply_txt)
        TextView replyTxt;
        @BindView(R.id.more_comments)
        TextView moreComments;
        @BindView(R.id.sub_comment_profile)
        ImageView subCommentProfile;
        @BindView(R.id.sub_comment_person)
        TextView subCommentPerson;
        @BindView(R.id.sub_comment_body)
        TextView subCommentBody;
        @BindView(R.id.sub_comment_date)
        TextView subCommentDate;
        @BindView(R.id.sub_comment_section)
        LinearLayout subCommentSection;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
