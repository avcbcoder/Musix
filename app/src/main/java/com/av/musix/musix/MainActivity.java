package com.av.musix.musix;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private pl.droidsonroids.gif.GifTextView gif;
public static final int time=1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
getWindow().setStatusBarColor(Color.WHITE);

        int perm = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (perm == PackageManager.PERMISSION_GRANTED) {
            Intent i = new Intent(MainActivity.this, MusicPlayer.class);
            startActivity(i);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    45);
        }

        getWindow().setStatusBarColor(Color.BLACK);
//        Handler h=new Handler(new Runnable() {
//            @Override
//            public void run() {
//                Intent i = new Intent(MainActivity.this, MusicPlayer.class);
//                startActivity(i);
//            }
//        },time);
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                long curr = System.currentTimeMillis();
//                while (System.currentTimeMillis() < 3000 + curr) ;
//                Intent i = new Intent(MainActivity.this, MusicPlayer.class);
//                startActivity(i);
//            }
//        });


//        t.run();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 45) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent i = new Intent(MainActivity.this, MusicPlayer.class);
                startActivity(i);
            } else {
                //toas
                Toast.makeText(this, "PERMISSION DENIED", Toast.LENGTH_SHORT).show();
                reqAgain();
            }
        }
    }

    public void reqAgain() {
        new AlertDialog.Builder(this)
                .setMessage("We need this permission to read files of sdCard.\n" +
                        "Please allow this permission")
                .setPositiveButton("GIVE PERMISSION", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(
                                MainActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                45
                        );
                    }
                })
                .setNegativeButton("NO THANKS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "okay", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create()
                .show();
    }



}
