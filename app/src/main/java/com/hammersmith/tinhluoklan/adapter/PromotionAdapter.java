package com.hammersmith.tinhluoklan.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hammersmith.tinhluoklan.CarDedailActivity;
import com.hammersmith.tinhluoklan.ProductActivity;
import com.hammersmith.tinhluoklan.R;
import com.hammersmith.tinhluoklan.model.Product;
import com.hammersmith.tinhluoklan.model.Promotion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chan Thuon on 10/6/2016.
 */
public class PromotionAdapter extends RecyclerView.Adapter<PromotionAdapter.MyViewHolder> {
    private Activity activity;
    private Promotion promotion;
    private List<Promotion> promotions = new ArrayList<>();

    public PromotionAdapter(Activity activity, List<Promotion> promotions) {
        this.activity = activity;
        this.promotions = promotions;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_promotion, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, CarDedailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView price, title;
        ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            price = (TextView) itemView.findViewById(R.id.price);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
