package com.hammersmith.tinhluoklan;

import android.accounts.Account;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hammersmith.tinhluoklan.model.Sell;
import com.hammersmith.tinhluoklan.model.User;
import com.joanzapata.iconify.widget.IconTextView;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.PhotoLoader;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private ImageView profile;
    private EditText name, email, address, phone, phone2;
    private LinearLayout lSave;
    private User user, userSocial, mUser, userPref;
    private Context context;
    private static final int SELECT_PHOTO = 100;
    ArrayList<String> imageList = new ArrayList<>();
    private static String encoded;
    private GalleryPhoto galleryPhoto;
    private static String photoPath;
    private ProgressDialog mProgressDialog;
    private Sell sell, mSell;
    private static String photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acount);
        user = PrefUtils.getCurrentUser(AccountActivity.this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        profile = (ImageView) findViewById(R.id.profile);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        address = (EditText) findViewById(R.id.address);
        phone = (EditText) findViewById(R.id.phone);
        phone2 = (EditText) findViewById(R.id.phone2);
        profile.setOnClickListener(this);
        lSave = (LinearLayout) findViewById(R.id.lSave);
        lSave.setOnClickListener(this);
        toolbar.setTitle("My Account");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        galleryPhoto = new GalleryPhoto(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getUser();
    }

    private void getUser() {
        userSocial = new User(user.getSocialLink());
        ApiInterface serviceUser = ApiClient.getClient().create(ApiInterface.class);
        Call<User> callUser = serviceUser.getUser(userSocial);
        callUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                mUser = response.body();
                name.setText(mUser.getName());
                email.setText(mUser.getEmail());
                Uri uri = Uri.parse(mUser.getPhoto());
                context = profile.getContext();
                Picasso.with(context).load(uri).into(profile);
                address.setText(mUser.getAddress());
                phone.setText(mUser.getPhone());
                phone2.setText(mUser.getPhone2());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                break;
            case R.id.lSave:
                showProgressDialog();
                if (imageList.size() < 1) {
                    updateUser();
                } else {
                    uploadFile();
                }
                break;
        }
    }

    private void uploadFile() {
        final String strName = name.getText().toString();
        final String strAddress = address.getText().toString();
        final String strPhone = phone.getText().toString();
        final String strPhone2 = phone2.getText().toString();
        final String strEmail = email.getText().toString();
        sell = new Sell(encoded);
        ApiInterface serviceUploadFile = ApiClient.getClient().create(ApiInterface.class);
        Call<Sell> callUploadFile = serviceUploadFile.uploadFile(sell);
        callUploadFile.enqueue(new Callback<Sell>() {
            @Override
            public void onResponse(Call<Sell> call, Response<Sell> response) {
                mSell = response.body();
                photo = ApiClient.BASE_URL + "images/" + mSell.getMsg();
                user = new User(user.getSocialLink(), strName, strEmail, strAddress, strPhone, strPhone2, photo);
                ApiInterface serviceUpdateAccount = ApiClient.getClient().create(ApiInterface.class);
                Call<User> callUpdateAccount = serviceUpdateAccount.updateAccount(user);
                callUpdateAccount.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        imageList.clear();
                        mUser = response.body();
                        userPref = new User();
                        userPref.setName(mUser.getName());
                        userPref.setEmail(mUser.getEmail());
                        userPref.setSocialLink(mUser.getSocialLink());
                        userPref.setPhoto(mUser.getPhoto());
                        PrefUtils.setCurrentUser(userPref, AccountActivity.this);
                        hideProgressDialog();
                        dialogSuccess("Account was updated successfully");
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                    }
                });
            }

            @Override
            public void onFailure(Call<Sell> call, Throwable t) {
                dialogError("Error while updating account");
            }
        });
    }

    private void updateUser() {
        final String strName = name.getText().toString();
        final String strAddress = address.getText().toString();
        final String strPhone = phone.getText().toString();
        final String strPhone2 = phone2.getText().toString();
        final String strEmail = email.getText().toString();
        user = new User(user.getSocialLink(), strName, strEmail, strAddress, strPhone, strPhone2, user.getPhoto());
        ApiInterface serviceUpdateAccount = ApiClient.getClient().create(ApiInterface.class);
        Call<User> callUpdateAccount = serviceUpdateAccount.updateAccount(user);
        callUpdateAccount.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                imageList.clear();
                mUser = response.body();
                userPref = new User();
                userPref.setName(mUser.getName());
                userPref.setEmail(mUser.getEmail());
                userPref.setSocialLink(mUser.getSocialLink());
                userPref.setPhoto(mUser.getPhoto());
                PrefUtils.setCurrentUser(userPref, AccountActivity.this);
                hideProgressDialog();
                dialogSuccess("Account was updated successfully");
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
            }
        });
    }

    private void dialogError(String strMessage) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View viewDialog = factory.inflate(R.layout.layout_dialog_new, null);
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(viewDialog);
        TextView message = (TextView) viewDialog.findViewById(R.id.message);
        message.setText(strMessage);
        IconTextView icon = (IconTextView) viewDialog.findViewById(R.id.icon);
        icon.setText("{fa-exclamation-circle}");
        TextView cancel = (TextView) viewDialog.findViewById(R.id.cancel);
        cancel.setText("Try again");
        viewDialog.findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void dialogSuccess(String strMessage) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View viewDialog = factory.inflate(R.layout.layout_dialog_new, null);
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(viewDialog);
        TextView message = (TextView) viewDialog.findViewById(R.id.message);
        message.setText(strMessage);
        IconTextView icon = (IconTextView) viewDialog.findViewById(R.id.icon);
        icon.setText("{fa-check-circle-o}");
        TextView cancel = (TextView) viewDialog.findViewById(R.id.cancel);
        cancel.setText("SUCCESS");
        viewDialog.findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });

        dialog.show();
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Account updating...");
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageList.clear();
        if (requestCode == SELECT_PHOTO && resultCode == this.RESULT_OK) {
            galleryPhoto.setPhotoUri(data.getData());
            photoPath = galleryPhoto.getPath();
            imageList.add(photoPath);
            Log.d("path", photoPath);
            try {
                Bitmap bitmap = PhotoLoader.init().from(photoPath).requestSize(512, 512).getBitmap();
                profile.setImageBitmap(bitmap);
                encoded = getEncoded64ImageStringFromBitmap(bitmap);

            } catch (FileNotFoundException e) {
                Toast.makeText(getApplicationContext(), "Error while loading image", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imgString;
    }
}
