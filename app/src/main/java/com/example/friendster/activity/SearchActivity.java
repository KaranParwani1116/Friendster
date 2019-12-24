package com.example.friendster.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendster.R;
import com.example.friendster.adapter.searchAdapter;
import com.example.friendster.model.User;
import com.example.friendster.rest.ApiClient;
import com.example.friendster.rest.services.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.search_recy)
    RecyclerView searchRecy;

    searchAdapter adapter;

    List<User>users = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.arrow_back_white);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        searchRecy.setLayoutManager(layoutManager);

        adapter = new searchAdapter(this, users);

        searchRecy.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_view, menu);

        SearchView searchView =(SearchView)menu.findItem(R.id.search).getActionView();

        searchView.setIconified(false);
        ((EditText)searchView.findViewById(androidx.appcompat.R.id.search_src_text)).setTextColor(getResources().getColor(R.color.hint_color));
        ((EditText)searchView.findViewById(androidx.appcompat.R.id.search_src_text)).setHintTextColor(getResources().getColor(R.color.hint_color));
        ((ImageView)searchView.findViewById(androidx.appcompat.R.id.search_close_btn)).setImageResource(R.drawable.icon_clear);
        searchView.setQueryHint("Search People ");

        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchfromdb(query, true);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length()>2)
                {
                    searchfromdb(newText, false);
                }else {
                    users.clear();
                    adapter.notifyDataSetChanged();
                }

                return true;
            }
        });


        return true;
    }

    private void searchfromdb(String query, boolean b) {

        Map<String, String> params = new HashMap<>();
        params.put("keyword", query);

        request Request = ApiClient.getApiClient().create(request.class);

        Call<List<User>> call = Request.search(params);

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.isSuccessful())
                {
                    users.clear();
                    users.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });
    }
}
