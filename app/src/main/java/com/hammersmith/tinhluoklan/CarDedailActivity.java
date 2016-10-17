package com.hammersmith.tinhluoklan;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hammersmith.tinhluoklan.adapter.CommentAdapter;
import com.hammersmith.tinhluoklan.model.Comment;
import com.hammersmith.tinhluoklan.model.Image;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarDedailActivity extends AppCompatActivity implements View.OnClickListener{
    private Toolbar toolbar;
    private int id;
    private String name;
    private List<Image> images = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private FloatingActionButton fab;
    private TextView price;
    private LinearLayoutManager layoutManager;
    private List<Comment> comments = new ArrayList<>();
    private CommentAdapter commentAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_dedail);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        price = (TextView) findViewById(R.id.price);
        price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        fab.setOnClickListener(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout_view_profile);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        name = getIntent().getStringExtra("name");
        id = getIntent().getIntExtra("id", 0);
        recyclerView.setNestedScrollingEnabled(false);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        showProgressDialog();
        final Window window = getWindow();

        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(getResources().getColor(R.color.transparent));
                    }
                    isShow = true;
                } else if (isShow) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        window.setStatusBarColor(getResources().getColor(R.color.transparent));
                    }
                    isShow = false;
                }
            }
        });

        layoutManager = new LinearLayoutManager(this);
        commentAdapter = new CommentAdapter(this, comments);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(commentAdapter);

        ApiInterface serviceImage = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Image>> callImage = serviceImage.getImage(id);
        callImage.enqueue(new Callback<List<Image>>() {
            @Override
            public void onResponse(Call<List<Image>> call, Response<List<Image>> response) {
                images = response.body();
                final ViewBannerGalleryDetail viewBannerGallery = (ViewBannerGalleryDetail) findViewById(R.id.viewBannerGallery);
                final ArrayList<ViewBannerGalleryDetail.BannerItem> listData = new ArrayList<ViewBannerGalleryDetail.BannerItem>();
                for (int i = 0; i < images.size(); i++) {
                    Log.d("imageList", ApiClient.BASE_URL + images.get(i).getImage());
                    listData.add(viewBannerGallery.new BannerItem(ApiClient.BASE_URL + images.get(i).getImage()));
                }
                viewBannerGallery.flip(listData, true);
                hideProgressDialog();
            }

            @Override
            public void onFailure(Call<List<Image>> call, Throwable t) {

            }
        });
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.heart));
                break;
        }
    }
}
