package com.example.friendster.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.friendster.Frsgments.FriendsFragment;
import com.example.friendster.Frsgments.NewsFeedFragment;
import com.example.friendster.Frsgments.NotificationFragment;
import com.example.friendster.Interface.CommentInterface;
import com.example.friendster.R;
import com.example.friendster.utils.BottomNavigationViewHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements CommentInterface {
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.search)
    ImageView search;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.framelayout)
    FrameLayout framelayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;
    //Google Sign In Client
    private GoogleSignInClient googleSignInClient;

    //firebase auth
    private FirebaseAuth mAuth;

    //fragment objects

    private FriendsFragment friendsFragment;
    private NewsFeedFragment newsFeedFragment;
    private NotificationFragment notificationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        //setting up toolbar

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //intitalizing bottomnavigation bar
        intitalizebottomNavigation();

        //intitalize fragment
        initializefragment();

        //intitalfragment

        setFragment(newsFeedFragment);

        onclicklistenerbottomnavigation();

        onclicklistenerfab();

        //setting click listener for searchview

        searchclicklistener();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.signout) {
            mAuth.signOut();
            googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Signed Out Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            Intent intent = new Intent(MainActivity.this, Loginactivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

    private void initializefragment() {
        friendsFragment=new FriendsFragment();
        newsFeedFragment=new NewsFeedFragment();
        notificationFragment=new NotificationFragment();
    }

    private void intitalizebottomNavigation() {

        bottomNavigation.inflateMenu(R.menu.bottom_navigation_main);
        bottomNavigation.setItemBackgroundResource(R.color.colorPrimary);
        bottomNavigation.setItemTextColor(ContextCompat.getColorStateList(bottomNavigation.getContext(), R.color.nav_item_colors));
        bottomNavigation.setItemIconTintList(ContextCompat.getColorStateList(bottomNavigation.getContext(), R.color.nav_item_colors));
        BottomNavigationViewHelper.removeShiftMode(bottomNavigation);
    }

    private void setFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.framelayout,fragment);
        fragmentTransaction.commit();
    }

    private void onclicklistenerbottomnavigation() {
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.newsfeed_fragment:
                        setFragment(newsFeedFragment);
                        break;

                    case R.id.profile_fragment:
                        startActivity(new Intent(MainActivity.this,ProfileActivity.class).putExtra("uid",FirebaseAuth.getInstance().getCurrentUser().getUid()));
                        break;

                    case R.id.profile_friends:
                        setFragment(friendsFragment);
                        break;

                    case R.id.profile_notification:
                        setFragment(notificationFragment);
                        break;
                }

                return true;
            }
        });
    }

    private void onclicklistenerfab() {
     fab.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             startActivity(new Intent(MainActivity.this,UploadActivty.class));
         }
     });
    }

    private void searchclicklistener() {
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void callbackMethod(int CommentCount) {
      //sending the comment count rom bottom sheet

        Log.d("NewsFeed Fragment",CommentCount+"");
      newsFeedFragment.UpdateCommentCount(CommentCount);
    }
}
