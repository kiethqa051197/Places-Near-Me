package com.example.placesnearme.View;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.placesnearme.R;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    ImageView imgBackgroundRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        imgBackgroundRegister = findViewById(R.id.imgBackgroundLogin);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        List<Integer> givenList = Arrays.asList(R.drawable.background_1, R.drawable.background_2,R.drawable.background_3,
                R.drawable.background_4, R.drawable.background_5, R.drawable.background_6, R.drawable.background_7,
                R.drawable.background_8, R.drawable.background_9, R.drawable.background_10,
                R.drawable.background_11, R.drawable.background_12, R.drawable.background_13);

        Random rand = new Random();
        int randomElement = givenList.get(rand.nextInt(givenList.size()));

        imgBackgroundRegister.setImageResource(randomElement);
    }
}
