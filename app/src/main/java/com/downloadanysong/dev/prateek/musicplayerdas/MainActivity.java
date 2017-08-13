package com.downloadanysong.dev.prateek.musicplayerdas;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.downloadanysong.dev.prateek.musicplayerdas.Helper.LastSharedPrefrence;
import com.downloadanysong.dev.prateek.musicplayerdas.NavBar.AboutActivity;
import com.downloadanysong.dev.prateek.musicplayerdas.NavBar.PlayerActivity;
import com.downloadanysong.dev.prateek.musicplayerdas.NavBar.PlayerService;

import java.util.HashMap;

import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.PlayerService.currentSongIndex;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.PlayerService.mHandler;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    FragmentTransaction transaction;

    public static boolean INITSTATE=false;
    public static LastSharedPrefrence lsp;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        checkUserPermission();
        verifyStoragePermissions(this);
        transaction = getSupportFragmentManager().beginTransaction();
        setTitle("Download Any Song");
        try {
            transaction.replace(R.id.ccontent, new PlayerActivity());
            transaction.commit();
        }catch (Exception e)
        {
            Log.d("ERROR","thread ki koi dikkat");
        }

       registerReceiver(mMessageReceiver, new IntentFilter("destroyall"));
        lsp = new LastSharedPrefrence(getApplicationContext());
        if (lsp.checklastplayed()==true) {
            HashMap<String, Integer> user = lsp.fetchlastsong();
            Log.d("ERROR2", String.valueOf(user.get("songpos"))+"ACT FETCH");
            PlayerService.setCurrentSongIndex(user.get("songpos")-1);
            PlayerActivity.currentSongIndex=user.get("songpos")-1;
            PlayerActivity.progress=user.get("currentpos");
        }

    }

    private void checkUserPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
                return;
            }
        }


    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            PlayerService.mHandler.removeCallbacks(PlayerActivity.mUpdateTimeTask);
            finish();

        }

    };
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 123:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //   /loadSongs();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    checkUserPermission();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
            //SETTING MENU
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        String title = "Home";
        setTitle(title);
        Fragment selectedFragment = PlayerActivity.newInstance();
        if (id == R.id.nav_search) {
            selectedFragment = PlayerActivity.newInstance();
            title = "Home";
        } else if (id == R.id.nav_lib) {

            selectedFragment = PlayerActivity.newInstance();
            title = "Music Library";

            // Handle the camera action
        } else if (id == R.id.nav_fav) {
            //FAV LIST NOT IN THIS BUILD

        } else if (id == R.id.nav_share) {
            // this runs, for example, after a button click
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, "Hey I just found this App with which you can Download Any Song : playsore/AppUrl");
            startActivity(intent);

        } else if (id == R.id.nav_abtus) {
            selectedFragment = AboutActivity.newInstance();
            title = "About Us";

        }
        try{
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.ccontent, selectedFragment);
            transaction.commit();
            if (title != null && findViewById(R.id.toolbar) != null) setTitle(title);


            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }catch (Exception e){}
        Log.d("ERROR","PTA NHI AB");

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMessageReceiver);
        PlayerService.mHandler.removeCallbacks(PlayerActivity.mUpdateTimeTask);
        lsp.lastPlayed();

        lsp.storelastsong(PlayerService.currentSongIndex,PlayerActivity.progress);
       /* if (lsp.checklastplayed()==false)
        {

        }
        else {
            lsp.lastPlayed();
            lsp.storelastsong(PlayerService.currentSongIndex,PlayerActivity.progress);


        }*/


    }
}
