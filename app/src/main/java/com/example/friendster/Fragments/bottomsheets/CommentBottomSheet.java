package com.example.friendster.Fragments.bottomsheets;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendster.Interface.CommentInterface;
import com.example.friendster.Interface.ProfileCommentInterface;
import com.example.friendster.R;
import com.example.friendster.adapter.CommentAdapter;
import com.example.friendster.model.CommentModel;
import com.example.friendster.model.PostModel;
import com.example.friendster.rest.ApiClient;
import com.example.friendster.rest.services.request;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentBottomSheet extends BottomSheetDialogFragment {

    @BindView(R.id.comments_txt)
    TextView commentsTxt;
    @BindView(R.id.top_section)
    LinearLayout topSection;
    @BindView(R.id.comment_recy)
    RecyclerView commentRecy;
    @BindView(R.id.comment_edittext)
    EditText commentEdittext;
    @BindView(R.id.comment_send)
    ImageView commentSend;
    @BindView(R.id.comment_send_wrapper)
    RelativeLayout commentSendWrapper;
    @BindView(R.id.comment_top_wrapper)
    LinearLayout commentTopWrapper;

    Unbinder unbinder;
    Boolean isFlagZero=true;

    private Context context;
    private PostModel postModel;
    private CommentInterface commentInterface=null;
    private ProfileCommentInterface profileCommentInterface=null;

    private CommentAdapter commentAdapter;

    private List<CommentModel.Result>results=new ArrayList<>();
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;

        if(context instanceof CommentInterface)
        {
            commentInterface = (CommentInterface)context;
        }else if(context instanceof ProfileCommentInterface)
        {
            profileCommentInterface=(ProfileCommentInterface)context;
        }
        else{
            throw new RuntimeException(context.toString() + "must implement comment interface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        commentInterface=null;
        profileCommentInterface=null;
    }

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = View.inflate(context, R.layout.fragment_comment_bottom_sheet, null);
        postModel = Parcels.unwrap(getFragmentManager().findFragmentByTag("commentFragment").getArguments().getParcelable("postModel"));
        unbinder = ButterKnife.bind(this,view);
        dialog.setContentView(view);

        View view1 = (View)view.getParent();
        view1.setBackgroundColor(Color.TRANSPARENT);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog dialog1 = (BottomSheetDialog)dialog;
                FrameLayout bottomsheet = dialog1.findViewById(R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomsheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        commentEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                Drawable img1 = getResources().getDrawable(R.drawable.icon_before_comment_send);
                Drawable img2 = getResources().getDrawable(R.drawable.icon_after_comment_send);

                if(charSequence.toString().trim().length()==0){
                    isFlagZero=true;
                    commentSendWrapper.setBackgroundResource(R.drawable.icon_background_before_comment);
                    loadimagewithanimation(context,img1);
                }
                if (charSequence.toString().trim().length()!=0 && isFlagZero)
                {
                    isFlagZero=false;
                    commentSendWrapper.setBackgroundResource(R.drawable.icon_background_after_comment);
                    loadimagewithanimation(context,img2);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        commentSendWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //fetching comment on button click
                if(isFlagZero)
                    return;

                String comment = commentEdittext.getText().toString();
                commentEdittext.setText("");
                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);
                request Request = ApiClient.getApiClient().create(request.class);
                Call<CommentModel> call = Request.poatcomment(new Addcomment(comment, FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        "0",postModel.getPostid(),
                       "0",postModel.getPostUserId(),"0",""));

                call.enqueue(new Callback<CommentModel>() {
                    @Override
                    public void onResponse(Call<CommentModel> call, Response<CommentModel> response) {
                        if(response.body()!=null)
                        {
                            if(response.body().getResult().size()>0)
                            {
                                Toast.makeText(context,"Comment Successful",Toast.LENGTH_SHORT).show();
                                int commentcount = Integer.parseInt(postModel.getCommentCount());
                                commentcount++;

                                //updating the comment count in news feed fragment
                                if(commentInterface!=null) {
                                    commentInterface.callbackMethod(commentcount);
                                }else if(profileCommentInterface!=null)
                                {
                                    profileCommentInterface.call(commentcount);
                                }

                                commentsTxt.setText(commentcount + " Comments");
                                results.add(response.body().getResult().get(0));
                                int position = results.indexOf(response.body().getResult().get(0));
                                commentAdapter.notifyItemInserted(position);
                                commentRecy.scrollToPosition(position);
                            }
                            else {
                                Toast.makeText(context,"Something Went Wrong.....",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(context,"Something Went Wrong.....",Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<CommentModel> call, Throwable t) {
                        Toast.makeText(context,"Something Went Wrong.....",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        if(postModel.getHasComment().equals("1"))
        {
            if(postModel.getCommentCount().equals("1"))
            {
                commentsTxt.setText("1 Comment");
            }
            else
            {
                int commentcount = Integer.parseInt(postModel.getCommentCount());

                commentsTxt.setText(commentcount + " Comments");
            }
        }
        else
        {
            commentsTxt.setText("0 Comment");
        }


        commentAdapter = new CommentAdapter(context,results,postModel);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        commentRecy.setLayoutManager(linearLayoutManager);
        commentRecy.setAdapter(commentAdapter);
        loadpreviouscomments();
    }

    private void loadpreviouscomments() {
        Map<String,String>params = new HashMap<>();
        params.put("postId",postModel.getPostid());

        request Request = ApiClient.getApiClient().create(request.class);
        Call<CommentModel>call = Request.retrievetoplevelcomment(params);
        call.enqueue(new Callback<CommentModel>() {
            @Override
            public void onResponse(Call<CommentModel> call, Response<CommentModel> response) {
                if (response.body().getResult()!=null) {
                    if (response.body().getResult().size() > 0) {
                        results.addAll(response.body().getResult());
                        commentAdapter.notifyDataSetChanged();
                    } else {

                    }
                }
            }

            @Override
            public void onFailure(Call<CommentModel> call, Throwable t) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void loadimagewithanimation(Context c,final Drawable img1)
    {
        final Animation anim_in= AnimationUtils.loadAnimation(c,R.anim.zoom_in);
        final Animation anim_out= AnimationUtils.loadAnimation(c,R.anim.zoom_out);

        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
               commentSend.setImageDrawable(img1);

               anim_in.setAnimationListener(new Animation.AnimationListener() {
                   @Override
                   public void onAnimationStart(Animation animation) {

                   }

                   @Override
                   public void onAnimationEnd(Animation animation) {

                   }

                   @Override
                   public void onAnimationRepeat(Animation animation) {

                   }
               });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });
        commentSend.startAnimation(anim_out);
    }

    public static class Addcomment{
        String comment,commentBy,superParentId,parentId,hasSubComment,postUserId,level,commentUserId;

        public Addcomment(String comment, String commentBy, String superParentId, String parentId, String hasSubCommment, String postUserId, String level, String commentUserId) {
            this.comment = comment;
            this.commentBy = commentBy;
            this.superParentId = superParentId;
            this.parentId = parentId;
            this.hasSubComment = hasSubCommment;
            this.postUserId = postUserId;
            this.level = level;
            this.commentUserId = commentUserId;
        }
    }
}
