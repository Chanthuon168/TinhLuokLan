package com.hammersmith.tinhluoklan;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.hammersmith.tinhluoklan.fragment.HomeFragment;
import com.hammersmith.tinhluoklan.model.DeviceToken;
import com.hammersmith.tinhluoklan.model.User;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.widget.IconTextView;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private HomeFragment homeFragment;
    private TextView name, email;
    private RoundedImageView profile;
    private Context context = MainActivity.this;
    private Boolean helper_home = false;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private View mHeaderView;
    private User user, userSocial;
    private DeviceToken deviceToken;
    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = PrefUtils.getCurrentUser(MainActivity.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            initScreen();
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Check type of intent filter
                if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)) {
                    //Registration success
                    String token = intent.getStringExtra("token");
                    deviceToken = new DeviceToken(user.getSocialLink(), token);
                    ApiInterface serviceDeviceToken = ApiClient.getClient().create(ApiInterface.class);
                    Call<DeviceToken> callDeviceToken = serviceDeviceToken.createDeviceToken(deviceToken);
                    callDeviceToken.enqueue(new Callback<DeviceToken>() {
                        @Override
                        public void onResponse(Call<DeviceToken> call, Response<DeviceToken> response) {

                        }

                        @Override
                        public void onFailure(Call<DeviceToken> call, Throwable t) {

                        }
                    });
//                    Toast.makeText(getApplicationContext(), "GCM token:" + token, Toast.LENGTH_LONG).show();
                } else if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)) {
                    //Registration error
                    Toast.makeText(getApplicationContext(), "GCM registration error!!!", Toast.LENGTH_LONG).show();
                } else {
                    //Tobe define
                }
            }
        };

        //Check status of Google play service in device
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (ConnectionResult.SUCCESS != resultCode) {
            //Check type of error
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                //So notification
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());
            } else {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }
        } else {
            //Start service
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mHeaderView = navigationView.getHeaderView(0);
        name = (TextView) mHeaderView.findViewById(R.id.name);
        email = (TextView) mHeaderView.findViewById(R.id.email);
        profile = (RoundedImageView) mHeaderView.findViewById(R.id.profile);
        userSocial = new User(user.getSocialLink());
        ApiInterface serviceUser = ApiClient.getClient().create(ApiInterface.class);
        Call<User> callUser = serviceUser.getUser(userSocial);
        callUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                user = response.body();
                name.setText(user.getName());
                email.setText(user.getEmail());
                Uri uri = Uri.parse(user.getPhoto());
                context = profile.getContext();
                Picasso.with(context).load(uri).into(profile);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

        Menu m = navigationView.getMenu();
        m.findItem(R.id.nav_home).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_home));
        m.findItem(R.id.nav_products).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_list));
        m.findItem(R.id.nav_sell).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_camera));
        m.findItem(R.id.nav_account).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_user));
        m.findItem(R.id.nav_my_product).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_car));
        m.findItem(R.id.nav_favorite).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_star));
        m.findItem(R.id.nav_logout).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_sign_out));

        verifyStoragePermissions(MainActivity.this);
    }

    private void initScreen() {
        homeFragment = new HomeFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container_framelayout, homeFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        helper_home = true;
    }

    @Override
    public void onBackPressed() {
        dialogExit("Are you sure want to exit the App?");
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            if (helper_home == false) {
                initScreen();
            }
        } else if (id == R.id.nav_products) {
            Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);

        } else if (id == R.id.nav_sell) {
            Intent intent = new Intent(MainActivity.this, SellActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        } else if (id == R.id.nav_account) {
            Intent intent = new Intent(MainActivity.this, AccountActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        } else if (id == R.id.nav_my_product) {
            Intent intent = new Intent(MainActivity.this, MyCarActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        } else if (id == R.id.nav_favorite) {
            Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {
            FacebookSdk.sdkInitialize(getApplicationContext());
            PrefUtils.clearCurrentUser(MainActivity.this);
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void dialogExit(String strMessage) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View viewDialog = factory.inflate(R.layout.layout_dialog, null);
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(viewDialog);
        TextView message = (TextView) viewDialog.findViewById(R.id.message);
        message.setText(strMessage);
        IconTextView icon = (IconTextView) viewDialog.findViewById(R.id.icon);
        icon.setText("{fa-times-circle}");
        TextView activate = (TextView) viewDialog.findViewById(R.id.ok);
        activate.setText("Exit");
        activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });
        viewDialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.w("MainActivity", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w("MainActivity", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }
}
