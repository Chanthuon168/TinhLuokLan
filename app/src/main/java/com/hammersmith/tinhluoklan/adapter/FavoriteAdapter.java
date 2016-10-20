package com.hammersmith.tinhluoklan.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ParseException;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hammersmith.tinhluoklan.ApiClient;
import com.hammersmith.tinhluoklan.ApiInterface;
import com.hammersmith.tinhluoklan.CarDedailActivity;
import com.hammersmith.tinhluoklan.PrefUtils;
import com.hammersmith.tinhluoklan.R;
import com.hammersmith.tinhluoklan.model.Favorite;
import com.hammersmith.tinhluoklan.model.User;
import com.joanzapata.iconify.widget.IconTextView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Chan Thuon on 9/12/2016.
 */
public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.MyViewHolder> {
    private Favorite favorite;
    private List<Favorite> favorites;
    private Context context;
    private Activity activity;
    private User user;
    private static String today;

    public FavoriteAdapter(Activity activity, List<Favorite> favorites) {
        this.activity = activity;
        this.favorites = favorites;
        user = PrefUtils.getCurrentUser(activity);
        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public FavoriteAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_favorite, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(FavoriteAdapter.MyViewHolder holder, final int position) {
        final Uri uri = Uri.parse(ApiClient.BASE_URL + favorites.get(position).getImage());
        context = holder.imageView.getContext();
        Picasso.with(context).load(uri).into(holder.imageView);
        holder.title.setText(favorites.get(position).getTitle());
        holder.price.setText("USD " + favorites.get(position).getPrice() + ".00");
        holder.createAt.setText(getTimeStamp(favorites.get(position).getCreateAt()));
        holder.using.setText(favorites.get(position).getCarUsing());
        holder.type.setText(favorites.get(position).getType());
        holder.transmition.setText(favorites.get(position).getTransmition());
        holder.mater.setText(favorites.get(position).getCarMater() + "km");

        holder.lContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogContact(favorites.get(position).getPhone(), favorites.get(position).getEmail());
            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentNew = new Intent(activity, CarDedailActivity.class);
                intentNew.putExtra("id", favorites.get(position).getProId());
                intentNew.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(intentNew);
            }
        });

        holder.lFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentNew = new Intent(activity, CarDedailActivity.class);
                intentNew.putExtra("id", favorites.get(position).getProId());
                intentNew.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(intentNew);
            }
        });

        holder.iconDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiInterface serviceDeleteFavorite = ApiClient.getClient().create(ApiInterface.class);
                Call<List<Favorite>> callDelete = serviceDeleteFavorite.deleteFavorite(favorites.get(position).getId(), user.getSocialLink());
                callDelete.enqueue(new Callback<List<Favorite>>() {
                    @Override
                    public void onResponse(Call<List<Favorite>> call, Response<List<Favorite>> response) {
                        favorites = response.body();
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<List<Favorite>> call, Throwable t) {

                    }
                });
            }
        });

    }

    private void dialogContact(final String strPhone, final String strEmail) {
        LayoutInflater factory = LayoutInflater.from(activity);
        final View viewDialog = factory.inflate(R.layout.layout_dialog_contact, null);
        final AlertDialog dialog = new AlertDialog.Builder(activity).create();
        dialog.setView(viewDialog);
        LinearLayout activate = (LinearLayout) viewDialog.findViewById(R.id.ok);
        LinearLayout layoutEmail = (LinearLayout) viewDialog.findViewById(R.id.l_email);
        activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        viewDialog.findViewById(R.id.l_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", strPhone, null));
                activity.startActivity(intent);
            }
        });
        viewDialog.findViewById(R.id.l_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", strPhone, null)));
            }
        });
        if (strEmail.equals("")) {
            layoutEmail.setVisibility(View.GONE);
        } else {
            layoutEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", strEmail, null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                    activity.startActivity(Intent.createChooser(emailIntent, "Send email..."));
                }
            });
        }
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, price, using, type, transmition, mater, createAt;
        LinearLayout lFavorite;
        IconTextView iconDelete;
        LinearLayout lComment, lLove, lContact;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            price = (TextView) itemView.findViewById(R.id.price);
            createAt = (TextView) itemView.findViewById(R.id.createdAt);
            lFavorite = (LinearLayout) itemView.findViewById(R.id.l_favorite);
            iconDelete = (IconTextView) itemView.findViewById(R.id.iconDelete);
            using = (TextView) itemView.findViewById(R.id.using);
            type = (TextView) itemView.findViewById(R.id.type);
            transmition = (TextView) itemView.findViewById(R.id.transmition);
            mater = (TextView) itemView.findViewById(R.id.mater);
            lComment = (LinearLayout) itemView.findViewById(R.id.lComment);
            lLove = (LinearLayout) itemView.findViewById(R.id.lLove);
            lContact = (LinearLayout) itemView.findViewById(R.id.lContact);
        }
    }

    public static String getTimeStamp(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = "";

        today = today.length() < 2 ? "0" + today : today;

        try {
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            String dateToday = todayFormat.format(date);
            format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("dd LLL, hh:mm a");
            String date1 = format.format(date);
            timestamp = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }
}
