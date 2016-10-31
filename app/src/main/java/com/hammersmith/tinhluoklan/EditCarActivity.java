package com.hammersmith.tinhluoklan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.hammersmith.tinhluoklan.adapter.PhotoEditAdapter;
import com.hammersmith.tinhluoklan.adapter.ViewPagerAdapter;
import com.hammersmith.tinhluoklan.model.Image;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditCarActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private PhotoEditAdapter photoEditAdapter;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private List<Image> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_car);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewPhoto);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        toolbar.setTitle("Edit your car");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ApiInterface serviceImage = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Image>> callImage = serviceImage.getImage(24);
        callImage.enqueue(new Callback<List<Image>>() {
            @Override
            public void onResponse(Call<List<Image>> call, Response<List<Image>> response) {
                images = response.body();
                photoEditAdapter = new PhotoEditAdapter(EditCarActivity.this, images);
                recyclerView.setAdapter(photoEditAdapter);
                photoEditAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Image>> call, Throwable t) {

            }
        });
    }
}
