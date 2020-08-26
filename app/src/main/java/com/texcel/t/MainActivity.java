package com.texcel.t;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DatabaseReference;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity {

    public  DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fabric.with(this, new Crashlytics());
        final SharedPreferences sharedPreferences = getSharedPreferences(""+R.string.app_name, MODE_PRIVATE);

         Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                if(sharedPreferences.contains(""+R.string.name))
                {
                    Intent intent=new Intent(MainActivity.this,Dashboard.class);
                    startActivity(intent);
                }
                else
                {
                    Intent intent=new Intent(MainActivity.this,Login.class);
                    startActivity(intent);

                }
            }
        }, 1000);





    }
}
