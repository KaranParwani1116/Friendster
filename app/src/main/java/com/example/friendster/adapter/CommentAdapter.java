package com.example.friendster.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendster.Frsgments.bottomsheets.SubcommentBottomSheet;
import com.example.friendster.R;
import com.example.friendster.model.CommentModel;
import com.example.friendster.model.PostModel;
import com.example.friendster.utils.AgoDateParse;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.text.ParseException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context context;
    private List<CommentModel.Result> result;
    private PostModel postModel;

    public CommentAdapter(Context context, List<CommentModel.Result> result,PostModel postModel) {
        this.context = context;
        this.result = result;
        this.postModel = postModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_single_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final CommentModel.Result model= result.get(position);
        holder.commentPerson.setText(model.getComment().getName());
        holder.commentBody.setText(model.getComment().getComment());

        Picasso.get().load(model.getComment().getProfileUrl()).placeholder(R.drawable.img_default_user).networkPolicy(NetworkPolicy.OFFLINE).into(holder.commentProfile, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(model.getComment().getProfileUrl()).placeholder(R.drawable.img_default_user).into(holder.commentProfile);
            }
        });

        try {
            holder.commentDate.setText(AgoDateParse.getTimeAgo(AgoDateParse.getTimeInMillsecond(model.getComment().getCommentDate())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(model.getComment().getHasSubComment().equals("1"))
        {
            holder.subCommentSection.setVisibility(View.VISIBLE);
            int commenttotal = model.getSubComments().getTotal();

            if(commenttotal==1)
            {
                holder.moreComments.setVisibility(View.GONE);
            }
            else
            {
                holder.moreComments.setVisibility(View.VISIBLE);

                commenttotal--;
                holder.moreComments.setText("View "+commenttotal+" more comments");
            }
            holder.subCommentBody.setText(model.getSubComments().getLastComment().get(0).getComment());
            holder.subCommentPerson.setText(model.getSubComments().getLastComment().get(0).getName());

            Picasso.get().load(model.getSubComments().getLastComment().get(0).getProfileUrl()).placeholder(R.drawable.img_default_user).networkPolicy(NetworkPolicy.OFFLINE).into(holder.subCommentProfile, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(model.getSubComments().getLastComment().get(0).getProfileUrl()).placeholder(R.drawable.img_default_user).into(holder.subCommentProfile);
                }
            });

            try {
                holder.subCommentDate.setText(AgoDateParse.getTimeAgo(AgoDateParse.getTimeInMillsecond(model.getSubComments().getLastComment().get(0).getCommentDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else
        {
            holder.subCommentSection.setVisibility(View.GONE);
        }
        Log.d("CommentAdapter",result.get(position).getComment().getCid());

        holder.replyTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialogFragment bottomSheetDialogFragment = new SubcommentBottomSheet();
                Bundle args = new Bundle();
                args.putParcelable("postModel", Parcels.wrap(postModel));
                args.putString("cid",result.get(position).getComment().getCid());
                args.putString("commentby",result.get(position).getComment().getCommentBy());
                args.putBoolean("openkeyboard",true);

                bottomSheetDialogFragment.setArguments(args);
                FragmentActivity fragmentActivity = (FragmentActivity)context;
                bottomSheetDialogFragment.show(fragmentActivity.getSupportFragmentManager(),"commentFragment");
            }
        });

        holder.moreComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialogFragment bottomSheetDialogFragment = new SubcommentBottomSheet();
                Bundle args = new Bundle();
                args.putParcelable("postModel", Parcels.wrap(postModel));
                args.putString("cid",result.get(position).getComment().getCid());
                args.putString("commentby",result.get(position).getComment().getCommentBy());
                args.putBoolean("openkeyBoard",true);
                bottomSheetDialogFragment.setArguments(args);
                FragmentActivity fragmentActivity = (FragmentActivity) context;
                bottomSheetDialogFragment.show(fragmentActivity.getSupportFragmentManager(), "commentFragment");
            }
        });
    }

    @Override
    public int getItemCount() {
        return result.size();
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
