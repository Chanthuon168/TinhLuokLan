package com.hammersmith.tinhluoklan.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hammersmith.tinhluoklan.ApiClient;
import com.hammersmith.tinhluoklan.R;
import com.hammersmith.tinhluoklan.model.Image;
import com.hammersmith.tinhluoklan.model.Photo;
import com.joanzapata.iconify.widget.IconTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chan Thuon on 9/18/2016.
 */
public class PhotoEditAdapter extends RecyclerView.Adapter<PhotoEditAdapter.MyViewHolder> {
    private ClickListener clickListener;
    private Context context;
    private Activity activity;
    private Photo photo;
    private List<Image> images = new ArrayList<>();

    public PhotoEditAdapter(Activity activity, List<Image> images) {
        this.activity = activity;
        this.images = images;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        IconTextView icon;
        TextView txtDefaultPhoto;
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            txtDefaultPhoto = (TextView) itemView.findViewById(R.id.txtDefaultPhoto);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.itemClicked(view, getLayoutPosition());
            }
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_photo, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Uri uri = Uri.parse(ApiClient.BASE_URL + images.get(position).getImage());
        context = holder.imageView.getContext();
        Picasso.with(context).load(uri).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public interface ClickListener {
        public void itemClicked(View view, int position);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
