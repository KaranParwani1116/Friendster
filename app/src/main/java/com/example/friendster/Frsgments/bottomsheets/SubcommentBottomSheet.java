package com.example.friendster.Frsgments.bottomsheets;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import com.example.friendster.R;
import com.example.friendster.adapter.SubCommentAdapter;
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
public class SubcommentBottomSheet extends BottomSheetDialogFragment {

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
    boolean iskeypadOpened = false;
    private String cid;
    private String commentby;
    private Context context;
    private PostModel postModel;

    private SubCommentAdapter subcommentAdapter;
    private List<CommentModel.Comment>comments=new ArrayList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = View.inflate(context, R.layout.fragment_comment_bottom_sheet, null);

        postModel = Parcels.unwrap(getFragmentManager().findFragmentByTag("commentFragment").getArguments().getParcelable("postModel"));
        cid = getFragmentManager().findFragmentByTag("commentFragment").getArguments().getString("cid");
        commentby = getFragmentManager().findFragmentByTag("commentFragment").getArguments().getString("commentby");
        iskeypadOpened = getFragmentManager().findFragmentByTag("commentFragment").getArguments().getBoolean("openkeyboard");


       Log.d("cid",cid);
       Log.d("commentby",commentby);

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
                Call<CommentModel> call = Request.poatcomment(new CommentBottomSheet.Addcomment(comment, FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        postModel.getPostid(), cid, "1", postModel.getPostUserId(), "1", commentby));

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
                                commentsTxt.setText(commentcount + " Comments");

                                comments.add(response.body().getResult().get(0).getComment());
                                int position = comments.indexOf(response.body().getResult().get(0).getComment());
                                subcommentAdapter.notifyItemInserted(position);
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

        subcommentAdapter = new SubCommentAdapter(context,comments);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        commentRecy.setLayoutManager(linearLayoutManager);
        commentRecy.setAdapter(subcommentAdapter);
        loadpreviouscomments();
    }

    private void loadpreviouscomments() {
        Map<String,String>params = new HashMap<>();
        params.put("postId",postModel.getPostid());
        params.put("commentId",cid);

        request Request = ApiClient.getApiClient().create(request.class);
        Call<List<CommentModel.Comment>>call = Request.subcomment(params);
        call.enqueue(new Callback<List<CommentModel.Comment>>() {
            @Override
            public void onResponse(Call<List<CommentModel.Comment>> call, Response<List<CommentModel.Comment>> response) {
                if (response.body()!=null) {
                    if (response.body().size() > 0) {
                        comments.addAll(response.body());
                        subcommentAdapter.notifyDataSetChanged();
                    } else {

                    }
                }
            }

            @Override
            public void onFailure(Call<List<CommentModel.Comment>> call, Throwable t) {

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

}
