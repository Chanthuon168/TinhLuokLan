package com.hammersmith.tinhluoklan;

import android.app.ProgressDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hammersmith.tinhluoklan.adapter.CategoryAdapter;
import com.hammersmith.tinhluoklan.adapter.FavoriteAdapter;
import com.hammersmith.tinhluoklan.model.Category;
import com.hammersmith.tinhluoklan.model.Favorite;
import com.hammersmith.tinhluoklan.model.User;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FavoriteAdapter favoriteAdapter;
    private LinearLayoutManager layoutManager;
    private List<Favorite> favorites = new ArrayList<>();
    private Toolbar toolbar;
    private ProgressDialog mProgressDialog;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        user = PrefUtils.getCurrentUser(FavoriteActivity.this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My Favorites");
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
        showProgressDialog();
        getCategory();
    }

    private void getCategory() {
        ApiInterface serviceCategory = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Favorite>> callCategory = serviceCategory.getFavorite(user.getSocialLink());
        callCategory.enqueue(new Callback<List<Favorite>>() {
            @Override
            public void onResponse(Call<List<Favorite>> call, Response<List<Favorite>> response) {
                favorites = response.body();
                favoriteAdapter = new FavoriteAdapter(FavoriteActivity.this, favorites);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(favoriteAdapter);
                hideProgressDialog();
            }

            @Override
            public void onFailure(Call<List<Favorite>> call, Throwable t) {
                hideProgressDialog();
                dialogTryAgain("An occur while loading data");
            }
        });
    }

    private void dialogTryAgain(String strMessage) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View viewDialog = factory.inflate(R.layout.layout_dialog_error, null);
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(viewDialog);
        TextView message = (TextView) viewDialog.findViewById(R.id.message);
        message.setText(strMessage);
        IconTextView icon = (IconTextView) viewDialog.findViewById(R.id.icon);
        icon.setText("{fa-times-circle}");
        TextView activate = (TextView) viewDialog.findViewById(R.id.ok);
        activate.setText("Try Again");
        viewDialog.findViewById(R.id.l_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCategory();
                dialog.dismiss();
                showProgressDialog();
            }
        });

        dialog.show();
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
