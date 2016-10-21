package com.hammersmith.tinhluoklan.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.ParseException;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
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
import com.hammersmith.tinhluoklan.CategoryActivity;
import com.hammersmith.tinhluoklan.EditCarActivity;
import com.hammersmith.tinhluoklan.PrefUtils;
import com.hammersmith.tinhluoklan.R;
import com.hammersmith.tinhluoklan.model.Comment;
import com.hammersmith.tinhluoklan.model.Favorite;
import com.hammersmith.tinhluoklan.model.Love;
import com.hammersmith.tinhluoklan.model.MyCar;
import com.hammersmith.tinhluoklan.model.User;
import com.joanzapata.iconify.widget.IconTextView;
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
public class MyCarAdapter extends RecyclerView.Adapter<MyCarAdapter.MyViewHolder> {
    private List<MyCar> myCars;
    private Context context;
    private Activity activity;
    private User user;
    private static String today;
    private Love love;
    private String numLove, numComment;
    private Comment comment;
    private Comment comm;
    private PopupWindow popWindow;
    private CommentAdapter commentAdapter;
    private List<Comment> comments = new ArrayList<>();

    public MyCarAdapter(Activity activity, List<MyCar> myCars) {
        this.activity = activity;
        this.myCars = myCars;
        user = PrefUtils.getCurrentUser(activity);
        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public MyCarAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_my_car, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyCarAdapter.MyViewHolder holder, final int position) {
        final Uri uri = Uri.parse(ApiClient.BASE_URL + myCars.get(position).getImage());
        context = holder.imageView.getContext();
        Picasso.with(context).load(uri).into(holder.imageView);
        holder.title.setText(myCars.get(position).getTitle());
        holder.price.setText("USD " + myCars.get(position).getPrice() + ".00");
        holder.createAt.setText(getTimeStamp(myCars.get(position).getCreateAt()));
        holder.using.setText(myCars.get(position).getCarUsing());
        holder.type.setText(myCars.get(position).getType());
        holder.transmition.setText(myCars.get(position).getTransmition());
        holder.mater.setText(myCars.get(position).getCarMater() + "km");
        holder.lContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogContact(myCars.get(position).getPhone(), myCars.get(position).getEmail());
            }
        });
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentNew = new Intent(activity, CarDedailActivity.class);
                intentNew.putExtra("id", myCars.get(position).getId());
                intentNew.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(intentNew);
            }
        });
        holder.lFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentNew = new Intent(activity, CarDedailActivity.class);
                intentNew.putExtra("id", myCars.get(position).getId());
                intentNew.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(intentNew);
            }
        });
        holder.iconDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEdit(myCars.get(position).getId());
            }
        });

        ApiInterface serviceLoveStatus = ApiClient.getClient().create(ApiInterface.class);
        Call<Love> callStatus = serviceLoveStatus.getLoveStatus(myCars.get(position).getId(), user.getSocialLink());
        callStatus.enqueue(new Callback<Love>() {
            @Override
            public void onResponse(Call<Love> call, Response<Love> response) {
                love = response.body();
                numLove = love.getCount();
                if (numLove.equals("no_love")) {
                    numLove = "";
                }
                if (love.getStatus().equals("checked")) {
                    holder.iconLove.setImageResource(R.drawable.heart);
                } else {
                    holder.iconLove.setImageResource(R.drawable.heart_outline);
                }
                holder.txtNumLove.setText(numLove);
            }

            @Override
            public void onFailure(Call<Love> call, Throwable t) {

            }
        });
        holder.lLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                love = new Love(myCars.get(position).getId(), user.getSocialLink());
                ApiInterface serviceCreateLove = ApiClient.getClient().create(ApiInterface.class);
                Call<Love> callCreate = serviceCreateLove.createLove(love);
                callCreate.enqueue(new Callback<Love>() {
                    @Override
                    public void onResponse(Call<Love> call, Response<Love> response) {
                        love = response.body();
                        if (love.getStatus().equals("checked")) {
                            holder.iconLove.setImageResource(R.drawable.heart);
                        } else {
                            holder.iconLove.setImageResource(R.drawable.heart_outline);
                        }
                        if (love.getCount().equals("0")) {
                            holder.txtNumLove.setText("");
                        } else {
                            holder.txtNumLove.setText(love.getCount());
                        }
                    }

                    @Override
                    public void onFailure(Call<Love> call, Throwable t) {

                    }
                });
            }
        });
        ApiInterface serviceCountComment = ApiClient.getClient().create(ApiInterface.class);
        Call<Comment> callCountComment = serviceCountComment.getCountComment(myCars.get(position).getId());
        callCountComment.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                comment = response.body();
                numComment = comment.getCount();
                if (numComment.equals("no_comment")) {
                    numComment = "";
                }
                holder.txtNumComment.setText(numComment);
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {

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
                final Call<List<Comment>> callComment = serviceComment.getComment(myCars.get(position).getId());
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
                            comment = new Comment(myCars.get(position).getId(), user.getSocialLink(), writeComment.getText().toString(), currentDateTime);
                            ApiInterface serviceCreateComment = ApiClient.getClient().create(ApiInterface.class);
                            Call<Comment> callCreate = serviceCreateComment.createComment(comment);
                            callCreate.enqueue(new Callback<Comment>() {
                                @Override
                                public void onResponse(Call<Comment> call, Response<Comment> response) {
                                    ApiInterface serviceComment = ApiClient.getClient().create(ApiInterface.class);
                                    final Call<List<Comment>> callComment = serviceComment.getComment(myCars.get(position).getId());
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

                                    ApiInterface serviceCountComment = ApiClient.getClient().create(ApiInterface.class);
                                    Call<Comment> callCountComment = serviceCountComment.getCountComment(myCars.get(position).getId());
                                    callCountComment.enqueue(new Callback<Comment>() {
                                        @Override
                                        public void onResponse(Call<Comment> call, Response<Comment> response) {
                                            comment = response.body();
                                            numComment = comment.getCount();
                                            if (numComment.equals("no_comment")) {
                                                numComment = "";
                                            }
                                            holder.txtNumComment.setText(numComment);
                                        }

                                        @Override
                                        public void onFailure(Call<Comment> call, Throwable t) {

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
                            comment = new Comment(myCars.get(position).getId(), user.getSocialLink(), writeComment.getText().toString(), currentDateTime);
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
                                    ApiInterface serviceCountComment = ApiClient.getClient().create(ApiInterface.class);
                                    Call<Comment> callCountComment = serviceCountComment.getCountComment(myCars.get(position).getId());
                                    callCountComment.enqueue(new Callback<Comment>() {
                                        @Override
                                        public void onResponse(Call<Comment> call, Response<Comment> response) {
                                            comment = response.body();
                                            numComment = comment.getCount();
                                            if (numComment.equals("no_comment")) {
                                                numComment = "";
                                            }
                                            holder.txtNumComment.setText(numComment);
                                        }

                                        @Override
                                        public void onFailure(Call<Comment> call, Throwable t) {

                                        }
                                    });
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

    private void dialogEdit(final int proId) {
        LayoutInflater factory = LayoutInflater.from(activity);
        final View viewDialog = factory.inflate(R.layout.layout_dialog_mycar, null);
        final AlertDialog dialog = new AlertDialog.Builder(activity).create();
        dialog.setView(viewDialog);
        final LinearLayout activate = (LinearLayout) viewDialog.findViewById(R.id.ok);
        activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        viewDialog.findViewById(R.id.l_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(activity, EditCarActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(intent);
            }
        });
        viewDialog.findViewById(R.id.l_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialogConfirm(proId);
            }
        });

        dialog.show();
    }

    private void dialogConfirm(final int proID) {
        LayoutInflater factory = LayoutInflater.from(activity);
        final View viewDialog = factory.inflate(R.layout.layout_dialog, null);
        final AlertDialog dialog = new AlertDialog.Builder(activity).create();
        dialog.setView(viewDialog);
        IconTextView icon = (IconTextView) viewDialog.findViewById(R.id.icon);
        icon.setText("{fa-trash-o}");
        TextView message = (TextView) viewDialog.findViewById(R.id.message);
        message.setText("Are you sure want to delete this car from Buy and Sell Car?");
        TextView cancel = (TextView) viewDialog.findViewById(R.id.cancel);
        final TextView delete = (TextView) viewDialog.findViewById(R.id.ok);
        delete.setText("Delete");
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ApiInterface serviceDeleteMyFavorite = ApiClient.getClient().create(ApiInterface.class);
                Call<List<MyCar>> callDelete = serviceDeleteMyFavorite.deleteMyProduct(proID, user.getSocialLink());
                callDelete.enqueue(new Callback<List<MyCar>>() {
                    @Override
                    public void onResponse(Call<List<MyCar>> call, Response<List<MyCar>> response) {
                        myCars = response.body();
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<List<MyCar>> call, Throwable t) {

                    }
                });
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void deleteCar() {

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
        return myCars.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, iconLove;
        TextView title, price, using, type, transmition, mater, createAt, txtNumComment, txtNumLove;
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
            txtNumComment = (TextView) itemView.findViewById(R.id.txtNumComment);
            txtNumLove = (TextView) itemView.findViewById(R.id.txtNumLove);
            iconLove = (ImageView) itemView.findViewById(R.id.iconLove);
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
