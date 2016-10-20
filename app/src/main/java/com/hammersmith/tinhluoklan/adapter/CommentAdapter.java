package com.hammersmith.tinhluoklan.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.net.ParseException;
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
import com.hammersmith.tinhluoklan.PrefUtils;
import com.hammersmith.tinhluoklan.R;
import com.hammersmith.tinhluoklan.model.Comment;
import com.hammersmith.tinhluoklan.model.Reply;
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


/**
 * Created by Chan Thuon on 9/12/2016.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {
    private List<Comment> comments;
    private Context context;
    private Activity activity;
    private static String today;
    private PopupWindow popWindow;
    private ReplyAdapter replyAdapter;
    private List<Reply> replies = new ArrayList<>();
    private Reply reply;
    private Reply rep;
    private User user;

    public CommentAdapter(Activity activity, List<Comment> comments) {
        this.activity = activity;
        this.comments = comments;
        user = PrefUtils.getCurrentUser(activity);
        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public CommentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(CommentAdapter.MyViewHolder holder, final int position) {
        final Uri uri = Uri.parse(comments.get(position).getProfile());
        context = holder.profile.getContext();
        Picasso.with(context).load(uri).into(holder.profile);
        holder.name.setText(comments.get(position).getName());
        holder.comment.setText(comments.get(position).getComment());
        holder.createAt.setText(getTimeStamp(comments.get(position).getCreateAt()));
        if (comments.get(position).getLastMessage().equals("no_reply")) {
            holder.lReply.setVisibility(View.GONE);
        } else {
            final Uri uriReply = Uri.parse(comments.get(position).getProfileReply());
            context = holder.profileReply.getContext();
            Picasso.with(context).load(uriReply).into(holder.profileReply);
            holder.nameReply.setText(comments.get(position).getNameReply());
            holder.reply.setText(comments.get(position).getLastMessage());
        }
        holder.txtReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View inflatedView = layoutInflater.inflate(R.layout.popup_layout_reply, null, false);
                LinearLayout headerView = (LinearLayout) inflatedView.findViewById(R.id.headerLayout);
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

                ApiInterface serviceReply = ApiClient.getClient().create(ApiInterface.class);
                final Call<List<Reply>> callReply = serviceReply.getReply(comments.get(position).getId());
                callReply.enqueue(new Callback<List<Reply>>() {
                    @Override
                    public void onResponse(Call<List<Reply>> call, Response<List<Reply>> response) {
                        replies = response.body();
                        lDialog.setVisibility(View.GONE);
                        if (replies.size() < 1) {
                            lNoComment.setVisibility(View.VISIBLE);
                        } else {
                            lNoComment.setVisibility(View.GONE);
                            replyAdapter = new ReplyAdapter(activity, replies);
                            recyclerViewComment.setAdapter(replyAdapter);
                            replyAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Reply>> call, Throwable t) {

                    }
                });
                popWindow = new PopupWindow(inflatedView, width, height - 50, true);
                popWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.popup_bg));
                popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                popWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                popWindow.setAnimationStyle(R.style.PopupAnimationReply);
                popWindow.showAtLocation(inflatedView, Gravity.BOTTOM, 0, 100);

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
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String currentDateTime = dateFormat.format(new Date());
                        reply = new Reply(comments.get(position).getId(), user.getSocialLink(), writeComment.getText().toString(), currentDateTime);
                        ApiInterface serviceCreateReply = ApiClient.getClient().create(ApiInterface.class);
                        Call<Reply> callCreate = serviceCreateReply.createReply(reply);
                        callCreate.enqueue(new Callback<Reply>() {
                            @Override
                            public void onResponse(Call<Reply> call, Response<Reply> response) {
                                ApiInterface serviceReply = ApiClient.getClient().create(ApiInterface.class);
                                final Call<List<Reply>> callReply = serviceReply.getReply(comments.get(position).getId());
                                callReply.enqueue(new Callback<List<Reply>>() {
                                    @Override
                                    public void onResponse(Call<List<Reply>> call, Response<List<Reply>> response) {
                                        replies = response.body();
                                        lDialog.setVisibility(View.GONE);
                                        if (replies.size() < 1) {
                                            lNoComment.setVisibility(View.VISIBLE);
                                        } else {
                                            lNoComment.setVisibility(View.GONE);
                                            replyAdapter = new ReplyAdapter(activity, replies);
                                            recyclerViewComment.setAdapter(replyAdapter);
                                            replyAdapter.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<List<Reply>> call, Throwable t) {

                                    }
                                });
                            }

                            @Override
                            public void onFailure(Call<Reply> call, Throwable t) {

                            }
                        });

                        writeComment.setText("");
                    }
                });
            }
        });

        holder.lReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View inflatedView = layoutInflater.inflate(R.layout.popup_layout_reply, null, false);
                LinearLayout headerView = (LinearLayout) inflatedView.findViewById(R.id.headerLayout);
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

                ApiInterface serviceReply = ApiClient.getClient().create(ApiInterface.class);
                final Call<List<Reply>> callReply = serviceReply.getReply(comments.get(position).getId());
                callReply.enqueue(new Callback<List<Reply>>() {
                    @Override
                    public void onResponse(Call<List<Reply>> call, Response<List<Reply>> response) {
                        replies = response.body();
                        lDialog.setVisibility(View.GONE);
                        if (replies.size() < 1) {
                            lNoComment.setVisibility(View.VISIBLE);
                        } else {
                            lNoComment.setVisibility(View.GONE);
                            replyAdapter = new ReplyAdapter(activity, replies);
                            recyclerViewComment.setAdapter(replyAdapter);
                            replyAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Reply>> call, Throwable t) {

                    }
                });
                popWindow = new PopupWindow(inflatedView, width, height - 50, true);
                popWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.popup_bg));
                popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                popWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                popWindow.setAnimationStyle(R.style.PopupAnimationReply);
                popWindow.showAtLocation(inflatedView, Gravity.BOTTOM, 0, 100);

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
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String currentDateTime = dateFormat.format(new Date());
                        reply = new Reply(comments.get(position).getId(), user.getSocialLink(), writeComment.getText().toString(), currentDateTime);
                        ApiInterface serviceCreateReply = ApiClient.getClient().create(ApiInterface.class);
                        Call<Reply> callCreate = serviceCreateReply.createReply(reply);
                        callCreate.enqueue(new Callback<Reply>() {
                            @Override
                            public void onResponse(Call<Reply> call, Response<Reply> response) {
                                ApiInterface serviceReply = ApiClient.getClient().create(ApiInterface.class);
                                final Call<List<Reply>> callReply = serviceReply.getReply(comments.get(position).getId());
                                callReply.enqueue(new Callback<List<Reply>>() {
                                    @Override
                                    public void onResponse(Call<List<Reply>> call, Response<List<Reply>> response) {
                                        replies = response.body();
                                        lDialog.setVisibility(View.GONE);
                                        if (replies.size() < 1) {
                                            lNoComment.setVisibility(View.VISIBLE);
                                        } else {
                                            lNoComment.setVisibility(View.GONE);
                                            replyAdapter = new ReplyAdapter(activity, replies);
                                            recyclerViewComment.setAdapter(replyAdapter);
                                            replyAdapter.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<List<Reply>> call, Throwable t) {

                                    }
                                });
                            }

                            @Override
                            public void onFailure(Call<Reply> call, Throwable t) {

                            }
                        });

                        writeComment.setText("");
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView profile, profileReply;
        TextView name, comment, createAt, nameReply, reply, txtReply;
        LinearLayout lReply;

        public MyViewHolder(View itemView) {
            super(itemView);
            profile = (ImageView) itemView.findViewById(R.id.profile);
            profileReply = (ImageView) itemView.findViewById(R.id.profileReply);
            name = (TextView) itemView.findViewById(R.id.name);
            comment = (TextView) itemView.findViewById(R.id.comment);
            createAt = (TextView) itemView.findViewById(R.id.createdAt);
            nameReply = (TextView) itemView.findViewById(R.id.nameReply);
            reply = (TextView) itemView.findViewById(R.id.reply);
            lReply = (LinearLayout) itemView.findViewById(R.id.lReply);
            txtReply = (TextView) itemView.findViewById(R.id.txtReply);
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