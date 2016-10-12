package com.hammersmith.tinhluoklan;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.hammersmith.tinhluoklan.model.Image;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarDedailActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private int id;
    private List<Image> images = new ArrayList<>();
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_dedail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("2017 AUDI A5");
        id = getIntent().getIntExtra("id", 0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        showProgressDialog();

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
}
