package com.example.friendster.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendster.R;
import com.example.friendster.adapter.FriendAdapter;
import com.example.friendster.adapter.FriendRequestAdapter;
import com.example.friendster.model.FriendModel;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    @BindView(R.id.request_title)
    TextView requestTitle;
    @BindView(R.id.friend_reqst_rcy)
    RecyclerView friendReqstRcy;
    @BindView(R.id.friend_title)
    TextView friendTitle;
    @BindView(R.id.friends_rcy)
    RecyclerView friendsRcy;
    @BindView(R.id.defaultTextView)
    TextView defaultTextView;
    private Context context;
    Unbinder unbinder;

    List<FriendModel.Request> requests = new ArrayList<>();
    List<FriendModel.Friend> friends = new ArrayList<>();

    FriendRequestAdapter friendRequestAdapter;
    FriendAdapter friendAdapter;

    public FriendsFragment() {
        // Required empty public constructor
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
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        unbinder = ButterKnife.bind(this, view);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(context);

        friendReqstRcy.setLayoutManager(linearLayoutManager1);
        friendsRcy.setLayoutManager(linearLayoutManager2);

        friendRequestAdapter = new FriendRequestAdapter(context, requests);
        friendAdapter = new FriendAdapter(context, friends);

        friendReqstRcy.setAdapter(friendRequestAdapter);
        friendsRcy.setAdapter(friendAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getListdata();
    }

    private void getListdata() {
        request Request = ApiClient.getApiClient().create(request.class);

        Map<String, String> params = new HashMap<>();
        params.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());

        Call<FriendModel> call = Request.loadfriends(params);

        call.enqueue(new Callback<FriendModel>() {
            @Override
            public void onResponse(Call<FriendModel> call, Response<FriendModel> response) {
                if (response.body() != null) {
                    if (response.body().getFriends().size() > 0) {
                        friends.clear();
                        friends.addAll(response.body().getFriends());
                        friendAdapter.notifyDataSetChanged();
                        friendTitle.setVisibility(View.VISIBLE);
                    } else {
                        friendTitle.setVisibility(View.GONE);
                    }

                    if (response.body().getRequests().size() > 0) {
                        requests.clear();
                        requests.addAll(response.body().getRequests());
                        friendRequestAdapter.notifyDataSetChanged();
                        requestTitle.setVisibility(View.VISIBLE);
                    } else {
                        requestTitle.setVisibility(View.GONE);
                    }

                    if (response.body().getRequests().size() == 0 && response.body().getFriends().size() == 0) {
                        defaultTextView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<FriendModel> call, Throwable t) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onPause() {
        super.onPause();

        requests.clear();
        friendRequestAdapter.notifyDataSetChanged();

        friends.clear();
        friendAdapter.notifyDataSetChanged();
    }
}
