package com.example.gavin.databindingtest;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.gavin.databindingtest.databinding.ActivityMainBinding;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static String mName = "MM";
    private ActivityMainBinding binding;
    private User user;
    private User2 mUser2;
    List<User2> data = new ArrayList<>();
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        user = new User("Micheal", "Jack");
        binding.setUser(user);
        binding.setHandle(new MyHandler());
        mUser2 = new User2();
        mUser2.firstName.set("周");
        mUser2.lastName.set("先森");
        mUser2.age.set(5);
        mUser2.isStudent.set(false);
        binding.setUser2(mUser2);
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);
        binding.setImageUrl("http://115.159.198.162:3000/posts/57355a92d9ca741017a28375/1467250338739.jpg");


        delay();

        for (int i = 0; i < 5; i++) {
            User2 user2 = new User2();
            user2.age.set(30);
            user2.firstName.set("Micheal " + i);
            user2.lastName.set("Jack " + i);
            data.add(user2);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false);
        binding.recyclerView.setLayoutManager(layoutManager);
        adapter = new MyAdapter(data);
        binding.recyclerView.setAdapter(adapter);

    }
    /**
     * 两秒后改变firstName
     */
    private void delay() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                user.setFirstName("Com");
                mUser2.age.set(33);

                for (int i = 0; i < 20; i++) {
                    User2 user2 = new User2();
                    user2.age.set(30);
                    user2.firstName.set("Micheal " + i);
                    user2.lastName.set("Jack " + i);
                    data.add(user2);
                }
                adapter.notifyDataSetChanged();
            }
        }, 2000);

    }
}
