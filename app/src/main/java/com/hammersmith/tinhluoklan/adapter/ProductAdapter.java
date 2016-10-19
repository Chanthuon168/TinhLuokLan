package com.hammersmith.tinhluoklan.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hammersmith.tinhluoklan.ApiClient;
import com.hammersmith.tinhluoklan.ApiInterface;
import com.hammersmith.tinhluoklan.CarDedailActivity;
import com.hammersmith.tinhluoklan.PrefUtils;
import com.hammersmith.tinhluoklan.R;
import com.hammersmith.tinhluoklan.model.Comment;
import com.hammersmith.tinhluoklan.model.Product;
import com.hammersmith.tinhluoklan.model.Reply;
import com.hammersmith.tinhluoklan.model.User;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Chan Thuon on 10/6/2016.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
    private User user;
    private Context context;
    private Activity activity;
    private List<Product> products;
    private Product product;
    private PopupWindow popWindow;
    private CommentAdapter commentAdapter;
    private List<Comment> comments = new ArrayList<>();
    private Comment comment;
    private Comment comm;
    private String numLove, numComment;
    private Reply reply;

    public ProductAdapter(Activity activity, List<Product> products) {
        this.activity = activity;
        this.products = products;
        user = PrefUtils.getCurrentUser(activity);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_product, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Uri uri = Uri.parse(ApiClient.BASE_URL + products.get(position).getImage());
        context = holder.image.getContext();
        Picasso.with(context).load(uri).into(holder.image);
        holder.title.setText(products.get(position).getName());
        holder.price.setText("USD " + products.get(position).getPrice() + ".00");
        holder.carType.setText(products.get(position).getType());
        holder.transmition.setText(products.get(position).getTransmition());
        holder.carUsing.setText(products.get(position).getCarUsing());
        holder.numImage.setText(products.get(position).getNumImage());
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, CarDedailActivity.class);
                intent.putExtra("id", products.get(position).getId());
                intent.putExtra("name", products.get(position).getName());
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(intent);
            }
        });
        holder.lComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View inflatedView = layoutInflater.inflate(R.layout.popup_layout, null, false);
                final RecyclerView recyclerViewComment = (RecyclerView) inflatedView.findViewById(R.id.commentsListView);
                final EditText writeComment = (EditText) inflatedView.findViewById(R.id.writeComment);
                final ImageView iconSend = (ImageView) inflatedView.findViewById(R.id.iconSend);
                final LinearLayout lNoComment = (LinearLayout) inflatedView.findViewById(R.id.lNoComment);
                final LinearLayout lDialog = (LinearLayout) inflatedView.findViewById(R.id.lDialog);
                Display display = activity.getWindowManager().getDefaultDisplay();
                final Point size = new Point();
                display.getSize(size);
                DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
                int width = displayMetrics.widthPixels;
                int height = displayMetrics.heightPixels;
                LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
                recyclerViewComment.setLayoutManager(layoutManager);
                ApiInterface serviceComment = ApiClient.getClient().create(ApiInterface.class);
                final Call<List<Comment>> callComment = serviceComment.getComment(products.get(position).getId());
                callComment.enqueue(new Callback<List<Comment>>() {
                    @Override
                    public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                        comments = response.body();
                        lDialog.setVisibility(View.GONE);
                        if (comments.size() < 1) {
                            lNoComment.setVisibility(View.VISIBLE);
                        } else {
                            lNoComment.setVisibility(View.GONE);
                            commentAdapter = new CommentAdapter(activity, comments);
                            recyclerViewComment.setAdapter(commentAdapter);
                            commentAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Comment>> call, Throwable t) {

                    }
                });
                popWindow = new PopupWindow(inflatedView, width, height - 50, true);
                popWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.popup_bg));
                popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                popWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                popWindow.setAnimationStyle(R.style.PopupAnimation);
                popWindow.showAtLocation(v, Gravity.BOTTOM, 0, 100);
                iconSend.setEnabled(false);
                writeComment.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (charSequence.toString().trim().length() == 0) {
                            iconSend.setEnabled(false);
                            iconSend.setImageResource(R.drawable.ic_content_send);
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
                            comment = new Comment(products.get(position).getId(), user.getSocialLink(), writeComment.getText().toString(), currentDateTime);
                            ApiInterface serviceCreateComment = ApiClient.getClient().create(ApiInterface.class);
                            Call<Comment> callCreate = serviceCreateComment.createComment(comment);
                            callCreate.enqueue(new Callback<Comment>() {
                                @Override
                                public void onResponse(Call<Comment> call, Response<Comment> response) {
                                    ApiInterface serviceComment = ApiClient.getClient().create(ApiInterface.class);
                                    final Call<List<Comment>> callComment = serviceComment.getComment(products.get(position).getId());
                                    callComment.enqueue(new Callback<List<Comment>>() {
                                        @Override
                                        public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                                            comments = response.body();
                                            lDialog.setVisibility(View.GONE);
                                            if (comments.size() < 1) {
                                                lNoComment.setVisibility(View.VISIBLE);
                                            } else {
                                                lNoComment.setVisibility(View.GONE);
                                                commentAdapter = new CommentAdapter(activity, comments);
                                                recyclerViewComment.setAdapter(commentAdapter);
                                                commentAdapter.notifyDataSetChanged();
                                            }
                                            lNoComment.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onFailure(Call<List<Comment>> call, Throwable t) {

                                        }
                                    });

//                                    ApiInterface serviceCountComment = ApiClient.getClient().create(ApiInterface.class);
//                                    Call<Comment> callCountComment = serviceCountComment.getCountComment(products.get(position).getId());
//                                    callCountComment.enqueue(new Callback<Comment>() {
//                                        @Override
//                                        public void onResponse(Call<Comment> call, Response<Comment> response) {
//                                            comment = response.body();
//                                            numComment = comment.getCount();
//                                            if (numComment.equals("no_comment")) {
//                                                numComment = "";
//                                            }
//                                            holder.txtNumComment.setText(numComment);
//                                        }
//
//                                        @Override
//                                        public void onFailure(Call<Comment> call, Throwable t) {
//
//                                        }
//                                    });
                                }

                                @Override
                                public void onFailure(Call<Comment> call, Throwable t) {

                                }
                            });
                        } else {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String currentDateTime = dateFormat.format(new Date());
                            comment = new Comment(products.get(position).getId(), user.getSocialLink(), writeComment.getText().toString(), currentDateTime);
                            ApiInterface serviceCreateComment = ApiClient.getClient().create(ApiInterface.class);
                            Call<Comment> callCreate = serviceCreateComment.createComment(comment);
                            callCreate.enqueue(new Callback<Comment>() {
                                @Override
                                public void onResponse(Call<Comment> call, Response<Comment> response) {
                                    lNoComment.setVisibility(View.GONE);
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
                                        recyclerViewComment.getLayoutManager().smoothScrollToPosition(recyclerViewComment, null, commentAdapter.getItemCount() - 1);
                                    }
//                                    ApiInterface serviceCountComment = ApiClient.getClient().create(ApiInterface.class);
//                                    Call<Comment> callCountComment = serviceCountComment.getCountComment(products.get(position).getId());
//                                    callCountComment.enqueue(new Callback<Comment>() {
//                                        @Override
//                                        public void onResponse(Call<Comment> call, Response<Comment> response) {
//                                            comment = response.body();
//                                            numComment = comment.getCount();
//                                            if (numComment.equals("no_comment")) {
//                                                numComment = "";
//                                            }
//                                            holder.txtNumComment.setText(numComment);
//                                        }
//
//                                        @Override
//                                        public void onFailure(Call<Comment> call, Throwable t) {
//
//                                        }
//                                    });
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
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, price, carType, transmition, carUsing, numImage;
        LinearLayout lComment;

        public MyViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            price = (TextView) itemView.findViewById(R.id.price);
            carType = (TextView) itemView.findViewById(R.id.carType);
            transmition = (TextView) itemView.findViewById(R.id.transmition);
            carUsing = (TextView) itemView.findViewById(R.id.carUsing);
            numImage = (TextView) itemView.findViewById(R.id.numImage);
            lComment = (LinearLayout) itemView.findViewById(R.id.lComment);
        }
    }
}
