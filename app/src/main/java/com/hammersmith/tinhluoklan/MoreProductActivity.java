package com.hammersmith.tinhluoklan;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.hammersmith.tinhluoklan.adapter.ProductAdapter;
import com.hammersmith.tinhluoklan.model.Product;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoreProductActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private LinearLayoutManager layoutManager;
    private List<Product> products = new ArrayList<>();
    private Toolbar toolbar;
    private String key;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_product);
        showProgressDialog();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        key = getIntent().getStringExtra("key");
        if (key.equals("promotion")) {
            toolbar.setTitle("Special Promotion");
        } else {
            toolbar.setTitle("Recently Added");
        }
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
        layoutManager = new LinearLayoutManager(this);

        ApiInterface serviceMore = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Product>> callMore = serviceMore.getMoreCar();
        callMore.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                products = response.body();
                productAdapter = new ProductAdapter(MoreProductActivity.this, products);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(productAdapter);
                hideProgressDialog();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                hideProgressDialog();
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
