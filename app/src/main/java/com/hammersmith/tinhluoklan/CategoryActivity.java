package com.hammersmith.tinhluoklan;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hammersmith.tinhluoklan.adapter.CategoryAdapter;
import com.hammersmith.tinhluoklan.model.Category;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Chan Thuon on 10/7/2016.
 */
public class CategoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private GridLayoutManager layoutManager;
    private List<Category> categories = new ArrayList<>();
    private Toolbar toolbar;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("All Categories");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(this, 3);
        progressDialog = ProgressDialog.show(this, null,"Loading...", true);
        ApiInterface serviceCategory = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Category>> callCategory = serviceCategory.getCategory();
        callCategory.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                categories = response.body();
                categoryAdapter = new CategoryAdapter(CategoryActivity.this, categories);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(categoryAdapter);
                hideDialog();
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                hideDialog();
            }
        });
    }
    private void hideDialog(){

        new Thread(new Runnable() {
            @Override
            public void run()
            {
                // do the thing that takes a long time

                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        progressDialog.dismiss();
                    }
                });
            }
        }).start();
    }
}
