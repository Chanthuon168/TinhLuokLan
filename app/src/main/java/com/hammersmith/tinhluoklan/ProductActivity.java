package com.hammersmith.tinhluoklan;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.hammersmith.tinhluoklan.adapter.ProductAdapter;
import com.hammersmith.tinhluoklan.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private LinearLayoutManager layoutManager;
    private List<Product> products = new ArrayList<>();
    private Toolbar toolbar;
    private int id;
    private String name, image;
    private ImageView imageView;
    private ProgressDialog mProgressDialog;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        imageView = (ImageView) findViewById(R.id.imageView);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(ProductActivity.this);
        id = getIntent().getIntExtra("id", 0);
        name = getIntent().getStringExtra("name");
        image = getIntent().getStringExtra("image");

        Log.d("imageView", image);
        toolbar.setTitle(name);
        setSupportActionBar(toolbar);
        showProgressDialog();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ApiInterface serviceCar = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Product>> callCar = serviceCar.getCarByCategory(id);
        callCar.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                products = response.body();
                if (products.size() < 1) {
                    findViewById(R.id.lNoData).setVisibility(View.VISIBLE);
                    Uri uri = Uri.parse(image);
                    context = imageView.getContext();
                    Picasso.with(context).load(uri).into(imageView);
                } else {
                    productAdapter = new ProductAdapter(ProductActivity.this, products);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(productAdapter);
                }
                hideProgressDialog();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
