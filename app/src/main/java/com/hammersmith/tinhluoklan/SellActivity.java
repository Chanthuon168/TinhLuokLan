package com.hammersmith.tinhluoklan;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.google.android.gms.common.api.Api;
import com.hammersmith.tinhluoklan.adapter.CarMakeAdapter;
import com.hammersmith.tinhluoklan.adapter.CategoryAdapter;
import com.hammersmith.tinhluoklan.adapter.FavoriteAdapter;
import com.hammersmith.tinhluoklan.adapter.PhotoAdapter;
import com.hammersmith.tinhluoklan.model.Category;
import com.hammersmith.tinhluoklan.model.Favorite;
import com.hammersmith.tinhluoklan.model.MyCar;
import com.hammersmith.tinhluoklan.model.Sell;
import com.hammersmith.tinhluoklan.model.User;
import com.joanzapata.iconify.widget.IconTextView;
import com.kosalgeek.android.photoutil.PhotoLoader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellActivity extends AppCompatActivity implements CarMakeAdapter.ClickListener, View.OnClickListener, View.OnFocusChangeListener {
    private RecyclerView recyclerView, recyclerViewPhoto;
    private CarMakeAdapter adapter;
    private List<Category> categories = new ArrayList<>();
    private Toolbar toolbar;
    private LinearLayoutManager layoutManager;
    private LinearLayout lCategory, lPhoto, lInformation, l_next, l_back, lDefault;
    private static String strProcess = "category";
    private TextView txtProcess;
    private TextView next;
    private IconTextView iconNext;
    private PhotoAdapter photoAdapter;
    private GridLayoutManager layoutManagerPhoto;
    private List<String> imgpath = new ArrayList<>();
    private ArrayList<String> imageList = new ArrayList<>();
    private List<String> fileName = new ArrayList<>();
    private List<String> images = new ArrayList<>();
    private List<String> temList = new ArrayList<>();
    private EditText transmission, condition, using, licence, model, year, color, mileage, city, price, description, name, address, email, phone, phone2;
    private String currentDateTime, strSocialLink, strUsername, strTransmission, strCondition, strUsing, strLicence, strModel, strYear, strColor, strMileage, strCity, strPrice, strDescription, strName, strAddress, strPhone, strPhone2;
    private ProgressDialog mProgressDialog, dialog;
    private User user, mUser, userSocial;
    private CheckBox checkBox;
    private MyCar myCar;
    private Sell sell, mSell;
    private int catId, numImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);
        user = PrefUtils.getCurrentUser(SellActivity.this);
        getUser();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        currentDateTime = dateFormat.format(new Date());
        strSocialLink = user.getSocialLink();
        strUsername = user.getName();
        checkBox = (CheckBox) findViewById(R.id.checkbox);
        city = (EditText) findViewById(R.id.city);
        model = (EditText) findViewById(R.id.model);
        year = (EditText) findViewById(R.id.year);
        color = (EditText) findViewById(R.id.color);
        mileage = (EditText) findViewById(R.id.mileage);
        price = (EditText) findViewById(R.id.price);
        description = (EditText) findViewById(R.id.description);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewCarMake);
        lCategory = (LinearLayout) findViewById(R.id.l_category);
        lPhoto = (LinearLayout) findViewById(R.id.l_photo);
        txtProcess = (TextView) findViewById(R.id.txtProcessing);
        l_next = (LinearLayout) findViewById(R.id.l_next);
        l_back = (LinearLayout) findViewById(R.id.l_back);
        next = (TextView) findViewById(R.id.next);
        iconNext = (IconTextView) findViewById(R.id.iconNext);
        lInformation = (LinearLayout) findViewById(R.id.l_information);
        recyclerViewPhoto = (RecyclerView) findViewById(R.id.recyclerViewPhoto);
        transmission = (EditText) findViewById(R.id.transmission);
        condition = (EditText) findViewById(R.id.condition);
        using = (EditText) findViewById(R.id.using);
        licence = (EditText) findViewById(R.id.licence);
        lDefault = (LinearLayout) findViewById(R.id.lDefault);
        name = (EditText) findViewById(R.id.name);
        address = (EditText) findViewById(R.id.address);
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phone);
        phone2 = (EditText) findViewById(R.id.phone2);
        licence.setOnFocusChangeListener(this);
        using.setOnFocusChangeListener(this);
        condition.setOnFocusChangeListener(this);
        transmission.setOnFocusChangeListener(this);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerViewPhoto.setNestedScrollingEnabled(false);
        l_back.setOnClickListener(this);
        l_next.setOnClickListener(this);
        findViewById(R.id.lImportPhoto).setOnClickListener(this);
        toolbar.setTitle("Sell your car");
        toolbar.setNavigationIcon(R.drawable.ic_content_clear);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogExit("Are you sure want to cancel your selling?");
            }
        });
        layoutManagerPhoto = new GridLayoutManager(this, 2);
        photoAdapter = new PhotoAdapter(this, imgpath);
        recyclerViewPhoto.setLayoutManager(layoutManagerPhoto);
        recyclerViewPhoto.setAdapter(photoAdapter);
        layoutManager = new LinearLayoutManager(this);
        showDialog();
        ApiInterface serviceCategory = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Category>> callCategory = serviceCategory.getCategory();
        callCategory.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                categories = response.body();
                adapter = new CarMakeAdapter(SellActivity.this, categories);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
                adapter.setClickListener(SellActivity.this);
                hideDialog();
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {

            }
        });

        condition.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("New")) {
                    mileage.setText("0");
                }
            }
        });

    }

    @Override
    public void itemClicked(View view, int position) {
        catId = categories.get(position).getId();
        lCategory.setVisibility(View.GONE);
        lPhoto.setVisibility(View.VISIBLE);
        strProcess = "photo";
        txtProcess.setText("2/3 Import your photo");
        l_next.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.l_back:
                if (strProcess.equals("category")) {
                    dialogExit("Are you sure want to cancel your selling?");
                } else if (strProcess.equals("photo")) {
                    lDefault.setVisibility(View.VISIBLE);
                    lInformation.setVisibility(View.GONE);
                    lPhoto.setVisibility(View.GONE);
                    lCategory.setVisibility(View.VISIBLE);
                    strProcess = "category";
                    txtProcess.setText("1/3 Select car make");
                    next.setText("Next ");
                    iconNext.setText("{fa-arrow-circle-right}");
                    l_next.setVisibility(View.GONE);
                    imgpath.clear();
                    photoAdapter.notifyDataSetChanged();
                } else if (strProcess.equals("info")) {
                    lCategory.setVisibility(View.GONE);
                    lPhoto.setVisibility(View.VISIBLE);
                    lInformation.setVisibility(View.GONE);
                    strProcess = "photo";
                    txtProcess.setText("2/3 Import your photo");
                    next.setText("Next ");
                    iconNext.setText("{fa-arrow-circle-right}");
                    l_next.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.l_next:
                if (strProcess.equals("category")) {
                    lPhoto.setVisibility(View.VISIBLE);
                    lCategory.setVisibility(View.GONE);
                    lInformation.setVisibility(View.GONE);
                    strProcess = "photo";
                    txtProcess.setText("2/3 Import your photo");
                    next.setText("Next ");
                    iconNext.setText("{fa-arrow-circle-right}");
                    l_next.setVisibility(View.VISIBLE);
                } else if (strProcess.equals("photo")) {
                    if (imgpath.size() < 1) {
                        dialogImport("Import your photos before continue!");
                    } else {
                        lCategory.setVisibility(View.GONE);
                        lPhoto.setVisibility(View.GONE);
                        lInformation.setVisibility(View.VISIBLE);
                        strProcess = "info";
                        txtProcess.setText("3/3 Enter information");
                        next.setText("Sell ");
                        iconNext.setText("{fa-check-circle}");
                        l_next.setVisibility(View.VISIBLE);
                        getUser();
                    }
                } else if (strProcess.equals("info")) {
                    if (name.getText().toString().equals("")) {
                        Snackbar snack = Snackbar.make(view, "Seller's name are required", Snackbar.LENGTH_LONG);
                        View v = snack.getView();
                        TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.RED);
                        snack.show();
                    } else if (address.getText().toString().equals("")) {
                        Snackbar snack = Snackbar.make(view, "Seller's address are required", Snackbar.LENGTH_LONG);
                        View v = snack.getView();
                        TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.RED);
                        snack.show();
                    } else if (email.getText().toString().equals("")) {
                        Snackbar snack = Snackbar.make(view, "Seller's email are required", Snackbar.LENGTH_LONG);
                        View v = snack.getView();
                        TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.RED);
                        snack.show();
                    } else if (phone.getText().toString().equals("")) {
                        Snackbar snack = Snackbar.make(view, "Seller's phone are required", Snackbar.LENGTH_LONG);
                        View v = snack.getView();
                        TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.RED);
                        snack.show();
                    } else if (model.getText().toString().equals("")) {
                        Snackbar snack = Snackbar.make(view, "Model are required", Snackbar.LENGTH_LONG);
                        View v = snack.getView();
                        TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.RED);
                        snack.show();
                    } else if (year.getText().toString().equals("")) {
                        Snackbar snack = Snackbar.make(view, "Year are required", Snackbar.LENGTH_LONG);
                        View v = snack.getView();
                        TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.RED);
                        snack.show();
                    } else if (color.getText().toString().equals("")) {
                        Snackbar snack = Snackbar.make(view, "Color are required", Snackbar.LENGTH_LONG);
                        View v = snack.getView();
                        TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.RED);
                        snack.show();
                    } else if (transmission.getText().toString().equals("")) {
                        Snackbar snack = Snackbar.make(view, "Transmission are required", Snackbar.LENGTH_LONG);
                        View v = snack.getView();
                        TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.RED);
                        snack.show();
                    } else if (condition.getText().toString().equals("")) {
                        Snackbar snack = Snackbar.make(view, "Condition are required", Snackbar.LENGTH_LONG);
                        View v = snack.getView();
                        TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.RED);
                        snack.show();
                    } else if (city.getText().toString().equals("")) {
                        Snackbar snack = Snackbar.make(view, "City are required", Snackbar.LENGTH_LONG);
                        View v = snack.getView();
                        TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.RED);
                        snack.show();
                    } else if (using.getText().toString().equals("")) {
                        Snackbar snack = Snackbar.make(view, "Using power are required", Snackbar.LENGTH_LONG);
                        View v = snack.getView();
                        TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.RED);
                        snack.show();
                    } else if (price.getText().toString().equals("")) {
                        Snackbar snack = Snackbar.make(view, "Price are required", Snackbar.LENGTH_LONG);
                        View v = snack.getView();
                        TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.RED);
                        snack.show();
                    } else if (licence.getText().toString().equals("")) {
                        Snackbar snack = Snackbar.make(view, "Licence are required", Snackbar.LENGTH_LONG);
                        View v = snack.getView();
                        TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.RED);
                        snack.show();
                    } else {
                        if (checkBox.isChecked()) {
                            uploadFile();
                        } else {
                            Snackbar snack = Snackbar.make(view, "Please agree term and condition.", Snackbar.LENGTH_LONG);
                            View v = snack.getView();
                            TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                            tv.setTextColor(Color.RED);
                            snack.show();
                        }
                    }
                }
                break;
            case R.id.lImportPhoto:
                Intent intent = new Intent(this, AlbumSelectActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 5);
                startActivityForResult(intent, Constants.REQUEST_CODE);
                break;
        }
    }

    private void uploadFile() {
        showProgressDialog();
        for (String imagePath : imageList) {
            try {
                Bitmap bitmap = PhotoLoader.init().from(imagePath).requestSize(512, 512).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                byte[] byteFormat = stream.toByteArray();
                final String encoded = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
                sell = new Sell(encoded);
                ApiInterface serviceUploadFile = ApiClient.getClient().create(ApiInterface.class);
                Call<Sell> callUploadFile = serviceUploadFile.uploadFile(sell);
                callUploadFile.enqueue(new Callback<Sell>() {
                    @Override
                    public void onResponse(Call<Sell> call, Response<Sell> response) {
                        mSell = response.body();
                        fileName.add(mSell.getMsg());
                        images.add(mSell.getMsg());
                        if (fileName.size() == imageList.size()) {
                            saveProduct();
                        }
                    }

                    @Override
                    public void onFailure(Call<Sell> call, Throwable t) {
                        hideProgressDialog();
                        dialogError("Error while uploading product");
                    }
                });
            } catch (FileNotFoundException e) {
            }
        }
    }

    private void saveProduct() {
        strModel = model.getText().toString().trim();
        sell = new Sell(catId, strSocialLink, strModel);
        ApiInterface serviceSaveProduct = ApiClient.getClient().create(ApiInterface.class);
        Call<Sell> callSaveProduct = serviceSaveProduct.saveProduct(sell);
        callSaveProduct.enqueue(new Callback<Sell>() {
            @Override
            public void onResponse(Call<Sell> call, Response<Sell> response) {
                mSell = response.body();
                saveImage(mSell.getCarId());
            }

            @Override
            public void onFailure(Call<Sell> call, Throwable t) {
                hideProgressDialog();
                dialogError("Error while uploading product");
            }
        });

    }

    private void saveImage(final int carId) {
        for (final String nameList : fileName) {
            sell = new Sell(carId, nameList);
            ApiInterface serviceSaveImage = ApiClient.getClient().create(ApiInterface.class);
            Call<Sell> callSaveImage = serviceSaveImage.saveImage(sell);
            callSaveImage.enqueue(new Callback<Sell>() {
                @Override
                public void onResponse(Call<Sell> call, Response<Sell> response) {
                    mSell = response.body();
                    temList.add(String.valueOf(mSell.getImgId()));
                    if (temList.size() == imageList.size()) {
                        saveProductDetail(carId, mSell.getImgId());
                    }
                }

                @Override
                public void onFailure(Call<Sell> call, Throwable t) {
                    hideProgressDialog();
                    dialogError("Error while uploading product");
                }
            });
        }
    }

    private void saveProductDetail(int carId, int imgId) {
        numImage = imageList.size();
        strYear = year.getText().toString().trim();
        strColor = color.getText().toString().trim();
        strMileage = mileage.getText().toString().trim();
        strCity = city.getText().toString().trim();
        strPrice = price.getText().toString().trim();
        strDescription = description.getText().toString().trim();
        strName = name.getText().toString().trim();
        strAddress = address.getText().toString().trim();
        strPhone = phone.getText().toString().trim();
        strPhone2 = phone2.getText().toString().trim();
        sell = new Sell(carId, numImage, imgId, strPrice, strYear, strCondition, strTransmission, strUsing, strMileage, strColor, strCity, strLicence, strDescription, strName, strAddress, strPhone, strPhone2, strSocialLink, currentDateTime);
        ApiInterface serviceSaveProductDetail = ApiClient.getClient().create(ApiInterface.class);
        Call<Sell> callSaveProductDetail = serviceSaveProductDetail.saveProductDetail(sell);
        callSaveProductDetail.enqueue(new Callback<Sell>() {
            @Override
            public void onResponse(Call<Sell> call, Response<Sell> response) {
                mSell = response.body();
                if (mSell.getMsg().equals("success")) {
                    imageList.clear();
                    fileName.clear();
                    strProcess = "category";
                    hideProgressDialog();
                    dialogSuccess("Your car uploaded successfully");
                }
            }

            @Override
            public void onFailure(Call<Sell> call, Throwable t) {
                hideProgressDialog();
                dialogError("Error while uploading product");
            }
        });
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
                pushNotification();
                startActivity(new Intent(SellActivity.this, MainActivity.class));
                finish();
            }
        });

        dialog.show();
    }

    private void pushNotification() {
        String strMessage = strUsername + " recently added new product";
        String strImage = ApiClient.BASE_URL + "images/" + images.get(0);
        sell = new Sell(strMessage, strImage);
        ApiInterface servicePushNotification = ApiClient.getClient().create(ApiInterface.class);
        Call<Sell> callPushNotification = servicePushNotification.pushNotification(sell);
        callPushNotification.enqueue(new Callback<Sell>() {
            @Override
            public void onResponse(Call<Sell> call, Response<Sell> response) {

            }

            @Override
            public void onFailure(Call<Sell> call, Throwable t) {

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
                dialog.dismiss();
                uploadFile();
            }
        });

        dialog.show();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE && resultCode == resultCode && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
            imgpath.clear();
            imageList.clear();
            for (int i = 0; i < images.size(); i++) {
                imgpath.add(images.get(i).path);
                imageList.add(images.get(i).path);
            }
            photoAdapter.notifyDataSetChanged();
            Log.d("size", imgpath.size() + "");
            if (imgpath.size() > 0) {
                lDefault.setVisibility(View.GONE);
            }
        }
    }

    private void dialogImport(String strMessage) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View viewDialog = factory.inflate(R.layout.layout_dialog_new, null);
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(viewDialog);
        TextView message = (TextView) viewDialog.findViewById(R.id.message);
        message.setText(strMessage);
        IconTextView icon = (IconTextView) viewDialog.findViewById(R.id.icon);
        icon.setText("{fa-picture-o}");
        TextView cancel = (TextView) viewDialog.findViewById(R.id.cancel);
        cancel.setText("Yes");
        viewDialog.findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void dialogExit(String strMessage) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View viewDialog = factory.inflate(R.layout.layout_dialog, null);
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(viewDialog);
        TextView message = (TextView) viewDialog.findViewById(R.id.message);
        message.setText(strMessage);
        IconTextView icon = (IconTextView) viewDialog.findViewById(R.id.icon);
        icon.setText("{fa-times-circle-o}");
        TextView activate = (TextView) viewDialog.findViewById(R.id.ok);
        activate.setText("Yes");
        activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strProcess = "category";
                startActivity(new Intent(SellActivity.this, MainActivity.class));
                finish();
            }
        });
        TextView cancel = (TextView) viewDialog.findViewById(R.id.cancel);
        cancel.setText("No");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.transmission:
                if (hasFocus) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    final CharSequence[] array = {"Manual", "Automatic"};
                    builder.setTitle("Transmission")
                            .setSingleChoiceItems(array, 2, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int position) {
                                    strTransmission = (String) array[position];
                                }
                            })

                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    transmission.setText(strTransmission);
                                    transmission.setCursorVisible(false);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    transmission.setCursorVisible(false);
                                }
                            });

                    builder.show();
                }
                break;
            case R.id.condition:
                if (hasFocus) {
                    AlertDialog.Builder builderCondition = new AlertDialog.Builder(this);
                    final CharSequence[] arrayCondition = {"New", "Used"};
                    builderCondition.setTitle("Condition")
                            .setSingleChoiceItems(arrayCondition, 2, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int position) {
                                    strCondition = (String) arrayCondition[position];
                                }
                            })

                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    condition.setText(strCondition);
                                    condition.setCursorVisible(false);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    condition.setCursorVisible(false);
                                }
                            });

                    builderCondition.show();
                    break;
                }
            case R.id.using:
                if (hasFocus) {
                    AlertDialog.Builder builderUsing = new AlertDialog.Builder(this);
                    final CharSequence[] arrayUsing = {"Gasoline", "Petrol", "Gas", "Petrol & Gas", "Gasoline & Gas"};
                    builderUsing.setTitle("Using Power")
                            .setSingleChoiceItems(arrayUsing, 5, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int position) {
                                    strUsing = (String) arrayUsing[position];
                                }
                            })

                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    using.setText(strUsing);
                                    using.setCursorVisible(false);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    using.setCursorVisible(false);
                                }
                            });

                    builderUsing.show();
                }
                break;
            case R.id.licence:
                if (hasFocus) {
                    AlertDialog.Builder builderLicence = new AlertDialog.Builder(this);
                    final CharSequence[] arrayLicence = {"With licence", "Without licence"};
                    builderLicence.setTitle("Licence")
                            .setSingleChoiceItems(arrayLicence, 2, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int position) {
                                    strLicence = (String) arrayLicence[position];
                                }
                            })

                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    licence.setText(strLicence);
                                    licence.setCursorVisible(false);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    licence.setCursorVisible(false);
                                }
                            });

                    builderLicence.show();
                }
                break;
        }
    }

    private void showDialog() {
        if (dialog == null) {
            dialog = new ProgressDialog(this);
            dialog.setMessage("Loading...");
            dialog.setIndeterminate(true);
        }

        dialog.show();
    }

    private void hideDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.hide();
        }
    }


    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Uploading...");
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
        hideDialog();
    }

    @Override
    public void onBackPressed() {
        dialogExit("Are you sure want to cancel your selling?");
    }
}
