package com.hammersmith.tinhluoklan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

public class CarDedailActivity extends AppCompatActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_dedail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("2017 AUDI A5");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        final ViewBannerGalleryDetail viewBannerGallery = (ViewBannerGalleryDetail) findViewById(R.id.viewBannerGallery);
        final ArrayList<ViewBannerGalleryDetail.BannerItem> listData = new ArrayList<ViewBannerGalleryDetail.BannerItem>();
        listData.add(viewBannerGallery.new BannerItem("http://www.blogcdn.com/slideshows/images/slides/391/253/2/S3912532/slug/l/2017-audi-a5-01-1.jpg"));
        listData.add(viewBannerGallery.new BannerItem("http://topgearrussia.ru/data/topgear/preview/2016-09/07/image-48c04c4a1473230387-980x550.jpg"));
        listData.add(viewBannerGallery.new BannerItem("http://cdn.slashgear.com/wp-content/uploads/2016/06/2017-audi-a5-04-1.jpg"));
        viewBannerGallery.flip(listData, true);
    }
}
