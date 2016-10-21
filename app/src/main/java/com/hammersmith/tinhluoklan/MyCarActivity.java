package com.hammersmith.tinhluoklan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hammersmith.tinhluoklan.adapter.FavoriteAdapter;
import com.hammersmith.tinhluoklan.adapter.MyCarAdapter;
import com.hammersmith.tinhluoklan.model.Favorite;
import com.hammersmith.tinhluoklan.model.MyCar;
import com.hammersmith.tinhluoklan.model.User;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCarActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private MyCarAdapter myCarAdapter;
    private LinearLayoutManager layoutManager;
    private List<MyCar> myCars = new ArrayList<>();
    private Toolbar toolbar;
    private ProgressDialog mProgressDialog;
    private User user;
    private LinearLayout lNoFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_car);
        user = PrefUtils.getCurrentUser(MyCarActivity.this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My Cars");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        findViewById(R.id.lUpload).setOnClickListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        lNoFavorite = (LinearLayout) findViewById(R.id.lNoFavorite);
        layoutManager = new LinearLayoutManager(this);
        showProgressDialog();
        getMyCar();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lUpload:
                Intent intent = new Intent(MyCarActivity.this, SellActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
        }
    }

    private void getMyCar() {
        ApiInterface serviceCategory = ApiClient.getClient().create(ApiInterface.class);
        Call<List<MyCar>> callCategory = serviceCategory.getMyProduct(user.getSocialLink());
        callCategory.enqueue(new Callback<List<MyCar>>() {
            @Override
            public void onResponse(Call<List<MyCar>> call, Response<List<MyCar>> response) {
                myCars = response.body();
                if (myCars.size() < 1) {
                    lNoFavorite.setVisibility(View.VISIBLE);
                } else {
                    myCarAdapter = new MyCarAdapter(MyCarActivity.this, myCars);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(myCarAdapter);
                }
                hideProgressDialog();
            }

            @Override
            public void onFailure(Call<List<MyCar>> call, Throwable t) {
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
                getMyCar();
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
