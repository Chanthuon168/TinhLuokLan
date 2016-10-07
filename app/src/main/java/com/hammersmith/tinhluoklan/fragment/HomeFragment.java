package com.hammersmith.tinhluoklan.fragment;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

/**
 * Created by Chan Thuon on 10/6/2016.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private RecyclerView recyclerView, recyclerViewPromotion;
    private ProductAdapter productAdapter;
    private LinearLayoutManager layoutManager, layoutPromotion;
    private List<Product> products = new ArrayList<>();
    private List<Promotion> promotions = new ArrayList<>();
    private PromotionAdapter promotionAdapter;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.content_main, container, false);
        root.findViewById(R.id.morePromotion).setOnClickListener(this);
        root.findViewById(R.id.moreRecently).setOnClickListener(this);
        final ViewBannerGallery viewBannerGallery = (ViewBannerGallery) root.findViewById(R.id.viewBannerGallery);
        final ArrayList<ViewBannerGallery.BannerItem> listData = new ArrayList<ViewBannerGallery.BannerItem>();
        listData.add(viewBannerGallery.new BannerItem("http://pictures.dealer.com/b/bmwofsudbury/0754/af9e2e8b523f1d7d8dca4cda26f3b055x.jpg", "http://www.bdonlinemart.com", "BMW CAMBODIA SHOP"));
        listData.add(viewBannerGallery.new BannerItem("http://dubbocitytoyota.com.au/img/placeholder/yaris-banner-ad.jpg", "http://www.sushmii.com", "TOYOYA CAMBODIA SHOP"));
        listData.add(viewBannerGallery.new BannerItem("http://www.mazdaofeverett.com/resrc/media/image/143580/mazda3banner.jpg", "http://www.gobeautyvoice.com", "MAZDA CAMBODIA SHOP"));
        listData.add(viewBannerGallery.new BannerItem("http://www.autosarena.com/wp-content/uploads/2014/11/Audi-A4-Premium-Sport-Banner.png", "https://s-media-cache-ak0.pinimg.com", "AUDI CAMBODIA SHOP"));

        recyclerViewPromotion = (RecyclerView) root.findViewById(R.id.recyclerViewPromotion);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);

        layoutPromotion = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        promotionAdapter = new PromotionAdapter(getActivity(), promotions);
        recyclerViewPromotion.setLayoutManager(layoutPromotion);
        recyclerViewPromotion.setAdapter(promotionAdapter);


        layoutManager = new LinearLayoutManager(getActivity());
        productAdapter = new ProductAdapter(getActivity(), products);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(productAdapter);

        viewBannerGallery.flip(listData, true);
        return root;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.morePromotion:
                Intent intent = new Intent(getActivity(), MoreProductActivity.class);
                intent.putExtra("key","promotion");
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
            case R.id.moreRecently:
                Intent intentRecently = new Intent(getActivity(), MoreProductActivity.class);
                intentRecently.putExtra("key","recently");
                intentRecently.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentRecently);
                break;
        }
    }
}
