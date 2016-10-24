package com.hammersmith.tinhluoklan;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.hammersmith.tinhluoklan.adapter.CarMakeAdapter;
import com.hammersmith.tinhluoklan.adapter.CategoryAdapter;
import com.hammersmith.tinhluoklan.adapter.FavoriteAdapter;
import com.hammersmith.tinhluoklan.adapter.PhotoAdapter;
import com.hammersmith.tinhluoklan.model.Category;
import com.hammersmith.tinhluoklan.model.Favorite;
import com.hammersmith.tinhluoklan.model.User;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.List;

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
    private EditText transmission, condition, using, licence;
    private String strTransmission, strCondition, strUsing, strLicence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);
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
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
            }
        });

    }

    @Override
    public void itemClicked(View view, int position) {
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
                    }
                }
                break;
            case R.id.lImportPhoto:
                Intent intent = new Intent(this, AlbumSelectActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 6);
                startActivityForResult(intent, Constants.REQUEST_CODE);
                break;
        }
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
                                    transmission.clearFocus();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    transmission.clearFocus();
                                }
                            });

                    builder.show();
                }
                break;
            case R.id.condition:
                if (hasFocus) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    final CharSequence[] array = {"New", "Used"};
                    builder.setTitle("Condition")
                            .setSingleChoiceItems(array, 2, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int position) {
                                    strCondition = (String) array[position];
                                }
                            })

                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    condition.setText(strCondition);
                                    condition.clearFocus();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    condition.clearFocus();
                                }
                            });

                    builder.show();
                }
                break;
            case R.id.using:
                if (hasFocus) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    final CharSequence[] array = {"Gasoline", "Petrol", "Gas", "Petrol & Gas", "Gasoline & Gas"};
                    builder.setTitle("Using Power")
                            .setSingleChoiceItems(array, 5, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int position) {
                                    strUsing = (String) array[position];
                                }
                            })

                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    using.setText(strUsing);
                                    using.clearFocus();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    using.clearFocus();
                                }
                            });

                    builder.show();
                }
                break;
            case R.id.licence:
                if (hasFocus) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    final CharSequence[] array = {"With licence", "Without licence"};
                    builder.setTitle("Licence")
                            .setSingleChoiceItems(array, 2, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int position) {
                                    strLicence = (String) array[position];
                                }
                            })

                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    licence.setText(strLicence);
                                    licence.clearFocus();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    licence.clearFocus();
                                }
                            });

                    builder.show();
                }
                break;
        }
    }
}
