package com.hammersmith.tinhluoklan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.hammersmith.tinhluoklan.fragment.HomeFragment;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.widget.IconTextView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private HomeFragment homeFragment;
    private Boolean helper_home = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            initScreen();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu m = navigationView.getMenu();
        m.findItem(R.id.nav_home).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_home));
        m.findItem(R.id.nav_products).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_list));
        m.findItem(R.id.nav_sell).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_camera));
        m.findItem(R.id.nav_account).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_user));
        m.findItem(R.id.nav_my_product).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_tags));
        m.findItem(R.id.nav_favorite).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_star_half_o));
        m.findItem(R.id.nav_logout).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_sign_out));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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


        } else if (id == R.id.nav_account) {

        } else if (id == R.id.nav_my_product) {

        } else if (id == R.id.nav_favorite) {


        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                finish();
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
}
