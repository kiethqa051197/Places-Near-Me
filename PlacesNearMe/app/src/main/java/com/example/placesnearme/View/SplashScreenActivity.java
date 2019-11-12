package com.example.placesnearme.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.placesnearme.R;

public class SplashScreenActivity extends AppCompatActivity {

    ImageView imgGifSplashScreen;
    TextView txtVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        txtVersion = findViewById(R.id.txtVesion);

        imgGifSplashScreen = findViewById(R.id.imgSplashScreen);
        Glide.with(this).asGif().load(R.drawable.gif_splash).into(imgGifSplashScreen);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {

                } finally {
                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        thread.start();

        try{
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            txtVersion.setText("Version: " + packageInfo.versionName);
        }
        catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
    }
}
