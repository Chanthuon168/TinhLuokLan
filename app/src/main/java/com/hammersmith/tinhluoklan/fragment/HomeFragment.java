package com.hammersmith.tinhluoklan.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hammersmith.tinhluoklan.ApiClient;
import com.hammersmith.tinhluoklan.ApiInterface;
import com.hammersmith.tinhluoklan.MoreProductActivity;
import com.hammersmith.tinhluoklan.ProductActivity;
import com.hammersmith.tinhluoklan.R;
import com.hammersmith.tinhluoklan.ViewBannerGallery;
import com.hammersmith.tinhluoklan.adapter.ProductAdapter;
import com.hammersmith.tinhluoklan.adapter.PromotionAdapter;
import com.hammersmith.tinhluoklan.model.Product;
import com.hammersmith.tinhluoklan.model.Promotion;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Chan Thuon on 10/6/2016.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private LinearLayoutManager layoutManager;
    private List<Product> products = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private SwipeRefreshLayout swipeRefresh;
    private int sizePromotion, sizeRecently;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.content_main, container, false);
        root.findViewById(R.id.moreRecently).setOnClickListener(this);
        final ViewBannerGallery viewBannerGallery = (ViewBannerGallery) root.findViewById(R.id.viewBannerGallery);
        final ArrayList<ViewBannerGallery.BannerItem> listData = new ArrayList<ViewBannerGallery.BannerItem>();
        listData.add(viewBannerGallery.new BannerItem("http://pictures.dealer.com/b/bmwofsudbury/0754/af9e2e8b523f1d7d8dca4cda26f3b055x.jpg", "http://www.bdonlinemart.com", "BMW CAMBODIA SHOP"));
        listData.add(viewBannerGallery.new BannerItem("http://dubbocitytoyota.com.au/img/placeholder/yaris-banner-ad.jpg", "http://www.sushmii.com", "TOYOYA CAMBODIA SHOP"));
        listData.add(viewBannerGallery.new BannerItem("http://www.mazdaofeverett.com/resrc/media/image/143580/mazda3banner.jpg", "http://www.gobeautyvoice.com", "MAZDA CAMBODIA SHOP"));
        listData.add(viewBannerGallery.new BannerItem("http://www.autosarena.com/wp-content/uploads/2014/11/Audi-A4-Premium-Sport-Banner.png", "https://s-media-cache-ak0.pinimg.com", "AUDI CAMBODIA SHOP"));

        showProgressDialog();
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        swipeRefresh = (SwipeRefreshLayout) root.findViewById(R.id.swiperefresh);
        recyclerView.setNestedScrollingEnabled(false);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRecently();
//                if (sizeBanner < 1) {
//                    refreshBanner();
//                }
            }
        });

        ApiInterface serviceRecently = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Product>> callRecently = serviceRecently.getRecentlyAdded();
        callRecently.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                products = response.body();
                sizeRecently = products.size();
                layoutManager = new LinearLayoutManager(getActivity());
                productAdapter = new ProductAdapter(getActivity(), products);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(productAdapter);
                hideProgressDialog();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {

            }
        });

        viewBannerGallery.flip(listData, true);
        return root;
    }

    private void refreshRecently() {
        ApiInterface serviceRecently = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Product>> callRecently = serviceRecently.getRecentlyAdded();
        callRecently.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                products = response.body();
                if (products.size() != sizeRecently) {
                    layoutManager = new LinearLayoutManager(getActivity());
                    productAdapter = new ProductAdapter(getActivity(), products);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(productAdapter);
                    sizeRecently = products.size();
                }
                swipeRefresh.setRefreshing(false);
                hideProgressDialog();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.moreRecently:
                Intent intentRecently = new Intent(getActivity(), MoreProductActivity.class);
                intentRecently.putExtra("key", "recently");
                intentRecently.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentRecently);
                break;
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
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
