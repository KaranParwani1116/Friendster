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


public class NewsFeedFragment extends Fragment {

    private static final String TAG = "NewsFeedFragment";

    @BindView(R.id.newsfeed)
    RecyclerView newsfeed;
    @BindView(R.id.newsfeedProgressBar)
    ProgressBar newsfeedProgressBar;

    private Context context;

    Unbinder unbinder;

    int limit = 3;
    int offset = 0;
    boolean isfromstart = true;

    List<PostModel> posts = new ArrayList<>();

    NewsFeedAdapter newsFeedAdapter;
    int commentcount=-1;

    public NewsFeedFragment() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news_feed, container, false);

        unbinder = ButterKnife.bind(this,view);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        newsfeed.setLayoutManager(linearLayoutManager);

        newsFeedAdapter = new NewsFeedAdapter(context, posts);
        newsfeed.setAdapter(newsFeedAdapter);

        newsfeed.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                int visibleitemcount = linearLayoutManager.getChildCount();
                int totalitemcount = linearLayoutManager.getItemCount();
                int passvisibleitems = linearLayoutManager.findFirstCompletelyVisibleItemPosition();

                Log.d(TAG,posts.size()+"");
                Log.d(TAG+"p",passvisibleitems+"");
                Log.d(TAG + "v",visibleitemcount+"");
                Log.d(TAG+"t",totalitemcount+"");

                if( abs(passvisibleitems) + visibleitemcount == (totalitemcount))
                {
                    Log.d(TAG,"scrolled");
                    isfromstart = false;
                    newsfeedProgressBar.setVisibility(View.VISIBLE);
                    offset = offset + limit;
                    loadtimeline();
                }
            }
        });

        return view;
    }

    public void UpdateCommentCount(int CommentCount)
    {
        Log.d("News Feed","Update comment count called");

        posts.clear();
        isfromstart=true;
        offset=0;
        loadtimeline();
    }

    @Override
    public void onStart() {
        super.onStart();
        isfromstart = true;
        offset = 0;
        loadtimeline();
        Log.d(TAG,"onStart");
    }

    private void loadtimeline() {
        Log.d("loadtimeline","invoked");
        request Request = ApiClient.getApiClient().create(request.class);

        Map<String, String> map = new HashMap<>();
        map.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        map.put("limit",limit+"");
        map.put("offset",offset+"");

        Call<List<PostModel>> call = Request.gettimeline(map);

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
