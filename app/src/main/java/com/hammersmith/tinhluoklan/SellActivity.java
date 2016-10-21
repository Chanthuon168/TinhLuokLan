package com.hammersmith.tinhluoklan;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.hammersmith.tinhluoklan.adapter.FavoriteAdapter;
import com.hammersmith.tinhluoklan.model.Favorite;
import com.hammersmith.tinhluoklan.model.User;

import java.util.ArrayList;
import java.util.List;

public class SellActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FavoriteAdapter favoriteAdapter;
    private LinearLayoutManager layoutManager;
    private List<Favorite> favorites = new ArrayList<>();
    private Toolbar toolbar;
    private ProgressDialog mProgressDialog;
    private User user;
    private LinearLayout lNoFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);
        user = PrefUtils.getCurrentUser(SellActivity.this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Selling Car");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
