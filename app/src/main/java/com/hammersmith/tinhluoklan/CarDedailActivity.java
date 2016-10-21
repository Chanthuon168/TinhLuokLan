package com.hammersmith.tinhluoklan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hammersmith.tinhluoklan.adapter.CommentAdapter;
import com.hammersmith.tinhluoklan.model.Comment;
import com.hammersmith.tinhluoklan.model.Favorite;
import com.hammersmith.tinhluoklan.model.Image;
import com.hammersmith.tinhluoklan.model.Product;
import com.hammersmith.tinhluoklan.model.User;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarDedailActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private static String today;
    private int id;
    private List<Image> images = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private FloatingActionButton fab;
    private LinearLayoutManager layoutManager;
    private List<Comment> comments = new ArrayList<>();
    private CommentAdapter commentAdapter;
    private RecyclerView recyclerView;
    private Product product;
    private TextView name, year, catName, type, transmition, using, mater, color, address, price, createAt, licence, description, sellerName, phone, email;
    private String strPhone, strEmail, strNumber, strTitle, strOwner, strDate;
    private RoundedImageView profile;
    private User user;
    private Context context;
    private ImageView iconSend;
    private EditText writeComment;
    private Comment comment;
    private Comment comm;
    private Favorite favorite;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_dedail);
        iconSend = (ImageView) findViewById(R.id.iconSend);
        writeComment = (EditText) findViewById(R.id.writeComment);
        user = PrefUtils.getCurrentUser(CarDedailActivity.this);
        profile = (RoundedImageView) findViewById(R.id.profile);
        name = (TextView) findViewById(R.id.name);
        year = (TextView) findViewById(R.id.year);
        catName = (TextView) findViewById(R.id.catName);
        type = (TextView) findViewById(R.id.carType);
        transmition = (TextView) findViewById(R.id.transmition);
        using = (TextView) findViewById(R.id.carUsing);
        mater = (TextView) findViewById(R.id.carMater);
        color = (TextView) findViewById(R.id.color);
        address = (TextView) findViewById(R.id.address);
        createAt = (TextView) findViewById(R.id.createdAt);
        licence = (TextView) findViewById(R.id.licence);
        description = (TextView) findViewById(R.id.description);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        price = (TextView) findViewById(R.id.price);
        sellerName = (TextView) findViewById(R.id.sellerName);
        phone = (TextView) findViewById(R.id.phone);
        email = (TextView) findViewById(R.id.email);
        fab.setOnClickListener(this);
        findViewById(R.id.report).setOnClickListener(this);
        findViewById(R.id.contactSeller).setOnClickListener(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout_view_profile);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        id = getIntent().getIntExtra("id", 0);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setNestedScrollingEnabled(false);
        final Uri uri = Uri.parse(user.getPhoto());
        context = profile.getContext();
        Picasso.with(context).load(uri).into(profile);

        ApiInterface serviceFavoriteStatus = ApiClient.getClient().create(ApiInterface.class);
        Call<Favorite> callStatusFavorite = serviceFavoriteStatus.getFavoriteStatus(id, user.getSocialLink());
        callStatusFavorite.enqueue(new Callback<Favorite>() {
            @Override
            public void onResponse(Call<Favorite> call, Response<Favorite> response) {
                favorite = response.body();
                if (favorite.getMsg().equals("Added to favorite")) {
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.star));
                } else {
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.star_outline));
                }
            }

            @Override
            public void onFailure(Call<Favorite> call, Throwable t) {

            }
        });

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        showProgressDialog();
        final Window window = getWindow();

        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(getResources().getColor(R.color.transparent));
                    }
                    isShow = true;
                } else if (isShow) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        window.setStatusBarColor(getResources().getColor(R.color.transparent));
                    }
                    isShow = false;
                }
            }
        });

        ApiInterface serviceImage = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Image>> callImage = serviceImage.getImage(id);
        callImage.enqueue(new Callback<List<Image>>() {
            @Override
            public void onResponse(Call<List<Image>> call, Response<List<Image>> response) {
                images = response.body();
                final ViewBannerGalleryDetail viewBannerGallery = (ViewBannerGalleryDetail) findViewById(R.id.viewBannerGallery);
                final ArrayList<ViewBannerGalleryDetail.BannerItem> listData = new ArrayList<ViewBannerGalleryDetail.BannerItem>();
                for (int i = 0; i < images.size(); i++) {
                    Log.d("imageList", ApiClient.BASE_URL + images.get(i).getImage());
                    listData.add(viewBannerGallery.new BannerItem(ApiClient.BASE_URL + images.get(i).getImage()));
                }
                viewBannerGallery.flip(listData, true);
            }

            @Override
            public void onFailure(Call<List<Image>> call, Throwable t) {

            }
        });

        ApiInterface serviceDetail = ApiClient.getClient().create(ApiInterface.class);
        Call<Product> callDetail = serviceDetail.getCarDetail(id);
        callDetail.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                product = response.body();
                name.setText(product.getName());
                year.setText(product.getCarYear());
                catName.setText(product.getCatName());
                type.setText(product.getType());
                transmition.setText(product.getTransmition());
                using.setText(product.getCarUsing());
                mater.setText(product.getCarMater() + "km");
                color.setText(product.getColor());
                address.setText(product.getAddress());
                price.setText("USD " + product.getPrice() + ".00");
                createAt.setText("Added date " + getTimeStamp(product.getCreatedAt()));
                licence.setText(product.getLicence());
                description.setText(product.getDescription());
                sellerName.setText(product.getSellerName());
                strPhone = product.getPhone();
                strEmail = product.getEmail();
                strNumber = String.valueOf(product.getId());
                strOwner = product.getSellerName();
                strTitle = product.getName();
                strDate = getTimeStamp(product.getCreatedAt());

                if (product.getPhone2().equals("")) {
                    phone.setText(strPhone);
                } else {
                    phone.setText(strPhone + "/" + product.getPhone2());
                }
                email.setText(strEmail);
                hideProgressDialog();
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {

            }
        });

        ApiInterface serviceComment = ApiClient.getClient().create(ApiInterface.class);
        final Call<List<Comment>> callComment = serviceComment.getComment(id);
        callComment.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                comments = response.body();
                commentAdapter = new CommentAdapter(CarDedailActivity.this, comments);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(commentAdapter);
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {

            }
        });

        iconSend.setEnabled(false);
        writeComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() == 0) {
                    iconSend.setEnabled(false);
                    iconSend.setImageResource(R.drawable.ic_content_unsend);
                } else {
                    iconSend.setEnabled(true);
                    iconSend.setImageResource(R.drawable.ic_content_send);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        iconSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (comments.size() < 1) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentDateTime = dateFormat.format(new Date());
                    comment = new Comment(id, user.getSocialLink(), writeComment.getText().toString(), currentDateTime);
                    ApiInterface serviceCreateComment = ApiClient.getClient().create(ApiInterface.class);
                    Call<Comment> callCreate = serviceCreateComment.createComment(comment);
                    callCreate.enqueue(new Callback<Comment>() {
                        @Override
                        public void onResponse(Call<Comment> call, Response<Comment> response) {

                            ApiInterface serviceComment = ApiClient.getClient().create(ApiInterface.class);
                            final Call<List<Comment>> callComment = serviceComment.getComment(id);
                            callComment.enqueue(new Callback<List<Comment>>() {
                                @Override
                                public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                                    comments = response.body();
                                    commentAdapter = new CommentAdapter(CarDedailActivity.this, comments);
                                    recyclerView.setLayoutManager(layoutManager);
                                    recyclerView.setAdapter(commentAdapter);
                                    commentAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onFailure(Call<List<Comment>> call, Throwable t) {

                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<Comment> call, Throwable t) {

                        }
                    });
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentDateTime = dateFormat.format(new Date());
                    comment = new Comment(id, user.getSocialLink(), writeComment.getText().toString(), currentDateTime);
                    ApiInterface serviceCreateComment = ApiClient.getClient().create(ApiInterface.class);
                    Call<Comment> callCreate = serviceCreateComment.createComment(comment);
                    callCreate.enqueue(new Callback<Comment>() {
                        @Override
                        public void onResponse(Call<Comment> call, Response<Comment> response) {
                            comment = response.body();
                            comm = new Comment();
                            comm.setComment(comment.getComment());
                            comm.setProfile(comment.getProfile());
                            comm.setCreateAt(comment.getCreateAt());
                            comm.setName(comment.getName());
                            comm.setLastMessage(comment.getLastMessage());
                            comments.add(comm);
                            commentAdapter.notifyDataSetChanged();
                            if (commentAdapter.getItemCount() > 1) {
                                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, commentAdapter.getItemCount() - 1);
                            }
                        }

                        @Override
                        public void onFailure(Call<Comment> call, Throwable t) {

                        }
                    });
                }
                writeComment.setText("");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                createFavorite(v);
                break;
            case R.id.contactSeller:
                dialogContact();
                break;
            case R.id.report:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", strEmail, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Report The Content");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear Team TinhLuokLan\n\n\tI want to report the centent\n\n\tNumber " + strNumber + "\n\n\tTitle " + strTitle + "\n\n\tAdded by " + strOwner + "\n\n\tAdded date " + strDate + "\n\nThe reason ");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
                break;
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
            format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("yyyy/dd/LLLL, hh:mm a");
            String date1 = format.format(date);
            timestamp = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }

    private void dialogContact() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View viewDialog = factory.inflate(R.layout.layout_dialog_contact, null);
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
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
                startActivity(intent);
            }
        });
        viewDialog.findViewById(R.id.l_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", strPhone, null)));
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
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                }
            });
        }
        dialog.show();
    }

    private void createFavorite(final View v) {
        favorite = new Favorite(id, user.getSocialLink());
        ApiInterface serviceCreateFavorite = ApiClient.getClient().create(ApiInterface.class);
        Call<Favorite> callFavorite = serviceCreateFavorite.createFavorite(favorite);
        callFavorite.enqueue(new Callback<Favorite>() {
            @Override
            public void onResponse(Call<Favorite> call, Response<Favorite> response) {
                favorite = response.body();
                Log.d("favorite", favorite.getMsg());
                if (favorite.getMsg().equals("Added to favorite")) {
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.star));
                    Snackbar snackbar = Snackbar.make(v, "Car added to favorite", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.star_outline));
                }
            }

            @Override
            public void onFailure(Call<Favorite> call, Throwable t) {

            }
        });
    }
}
