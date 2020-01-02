package com.example.friendster.Frsgments;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendster.R;
import com.example.friendster.adapter.NewsFeedAdapter;
import com.example.friendster.model.PostModel;
import com.example.friendster.rest.ApiClient;
import com.example.friendster.rest.services.request;
import com.google.firebase.auth.FirebaseAuth;

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

import static java.lang.Math.abs;


public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    @BindView(R.id.newsfeed)
    RecyclerView newsfeed;
    private Context context;
    @BindView(R.id.newsfeedProgressBar)
    ProgressBar newsfeedProgressBar;
    Unbinder unbinder;

    int limit = 3;
    int offset = 0;
    boolean isfromstart = true;

    List<PostModel> posts = new ArrayList<>();

    NewsFeedAdapter newsFeedAdapter;

    String uid = "0";
    String current_state = "0";

    public ProfileFragment() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this,view);


        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        newsfeed.setLayoutManager(linearLayoutManager);

        newsFeedAdapter = new NewsFeedAdapter(context, posts);
        newsfeed.setAdapter(newsFeedAdapter);

        uid = getArguments().getString("uid");
        current_state = getArguments().getString("current_state");

        newsfeed.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                int visibleitemcount = linearLayoutManager.getChildCount();
                int totalitemcount = linearLayoutManager.getItemCount();
                int passvisibleitems = linearLayoutManager.findFirstCompletelyVisibleItemPosition();


                if( abs(passvisibleitems)  + visibleitemcount == (totalitemcount))
                {
                    isfromstart = false;
                    newsfeedProgressBar.setVisibility(View.VISIBLE);
                    offset = offset + limit;
                    loadprofilepost();
                }
            }
        });

        return view;
    }

    public void UpdateCommentCount(int CommentCount){
        isfromstart=false;
        offset=0;
        posts.clear();

        loadprofilepost();
    }

    @Override
    public void onStart() {
        super.onStart();
        isfromstart = true;
        offset = 0;
        loadprofilepost();
    }

    private void loadprofilepost() {
        request Request = ApiClient.getApiClient().create(request.class);

        Map<String, String> map = new HashMap<>();
        map.put("onlineid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        map.put("uid",uid);
        map.put("limit",limit+"");
        map.put("offset",offset+"");
        map.put("current_state",current_state);

        Call<List<PostModel>> call = Request.profileTimeline(map);

        call.enqueue(new Callback<List<PostModel>>() {
            @Override
            public void onResponse(Call<List<PostModel>> call, Response<List<PostModel>> response) {
                newsfeedProgressBar.setVisibility(View.GONE);
                if(response.body()!=null)
                {
                    posts.addAll(response.body());

                    if(isfromstart)
                    {
                        newsfeed.setAdapter(newsFeedAdapter);
                    }
                    else
                    {
                        newsFeedAdapter.notifyItemRangeInserted(posts.size(),response.body().size());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<PostModel>> call, Throwable t) {
                newsfeedProgressBar.setVisibility(View.GONE);
                Toast.makeText(context, t.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
        posts.clear();
        offset=0;
        newsFeedAdapter.notifyDataSetChanged();
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
