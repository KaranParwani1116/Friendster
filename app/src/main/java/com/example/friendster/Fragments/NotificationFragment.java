package com.example.friendster.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendster.R;
import com.example.friendster.adapter.NotificationAdapter;
import com.example.friendster.model.NotificationModel;
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


public class NotificationFragment extends Fragment {

    @BindView(R.id.notification_recy)
    RecyclerView notificationRecy;
    @BindView(R.id.defaultTextView)
    TextView defaultTextView;

    Unbinder unbinder;
    private Context context;
    private NotificationAdapter notificationAdapter;
    private List<NotificationModel>notificationModels = new ArrayList<>();

    public NotificationFragment() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        unbinder = ButterKnife.bind(this,view);

        notificationAdapter = new NotificationAdapter(context, notificationModels);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        notificationRecy.setLayoutManager(linearLayoutManager);
        notificationRecy.setAdapter(notificationAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getNotificationFragment();
    }

    private void getNotificationFragment() {

        Map<String,String>params = new HashMap<>();
        params.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());

        request Request = ApiClient.getApiClient().create(request.class);

        Call<List<NotificationModel>> call = Request.getnotification(params);

        call.enqueue(new Callback<List<NotificationModel>>() {
            @Override
            public void onResponse(Call<List<NotificationModel>> call, Response<List<NotificationModel>> response) {

                if(response.body()!=null)
                {
                    if(response.body().size()>0)
                    {
                        notificationModels.addAll(response.body());
                        notificationAdapter.notifyDataSetChanged();
                        defaultTextView.setVisibility(View.GONE);
                    }
                    else
                    {
                        defaultTextView.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    Toast.makeText(context, "Something went wrong...",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<NotificationModel>> call, Throwable t) {

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
        notificationModels.clear();
        notificationAdapter.notifyDataSetChanged();
    }
}
