package com.example.placesnearme.View;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.placesnearme.Common;
import com.example.placesnearme.Model.Firebase.User;
import com.example.placesnearme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import dmax.dialog.SpotsDialog;

import static android.text.TextUtils.isEmpty;
import static com.example.placesnearme.Remote.Check.doStringsMatch;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText mEmail, mPassword, mConfirmPassword;
    private Button btnRegister;
    private TextView txtSignIn;
    private ImageView imgBack;

    private FirebaseFirestore mDb;

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDb = FirebaseFirestore.getInstance();
        mEmail = findViewById(R.id.edEmail);
        mPassword = findViewById(R.id.edPassword);
        mConfirmPassword = findViewById(R.id.edConfirmPassword);

        txtSignIn = findViewById(R.id.txtSignIn);
        btnRegister = findViewById(R.id.btnRegister);
        imgBack = findViewById(R.id.imgBack);

        alertDialog = new SpotsDialog(this, R.style.Custom);

        hideSoftKeyboard();

        btnRegister.setOnClickListener(this);
        txtSignIn.setOnClickListener(this);
        imgBack.setOnClickListener(this);
    }

    public void registerNewEmail(final String email, String password){
        alertDialog.show();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //insert some default data
                            User user = new User();
                            user.setEmail(email);
                            user.setUsername(email.substring(0, email.indexOf("@")));
                            user.setMauser(FirebaseAuth.getInstance().getUid());
                            user.setAvatar("ava_man.png");

                            DocumentReference newUserRef = mDb
                                    .collection(Common.USER).document(FirebaseAuth.getInstance().getUid());

                            newUserRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    alertDialog.dismiss();

                                    if(task.isSuccessful()){
                                        redirectLoginScreen();
                                    }else{
                                        View parentLayout = findViewById(android.R.id.content);
                                        Snackbar.make(parentLayout, getString(R.string.coloitrongquatrinhlaydulieu), Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else {
                            View parentLayout = findViewById(android.R.id.content);
                            Snackbar.make(parentLayout, getString(R.string.coloidangky), Snackbar.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }
                    }
                });
    }

    private void redirectLoginScreen(){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnRegister:
                //check for null valued EditText fields
                if(!isEmpty(mEmail.getText().toString())
                        && !isEmpty(mPassword.getText().toString())
                        && !isEmpty(mConfirmPassword.getText().toString())){

                    //check if passwords match
                    if(doStringsMatch(mPassword.getText().toString(), mConfirmPassword.getText().toString())){
                        //Initiate registration task
                        registerNewEmail(mEmail.getText().toString(), mPassword.getText().toString());
                    }else
                        Toast.makeText(RegisterActivity.this, getString(R.string.matkhaukhongkhop), Toast.LENGTH_SHORT).show();

                }else
                    Toast.makeText(RegisterActivity.this, getString(R.string.khongduocdetrong), Toast.LENGTH_SHORT).show();
                break;
            case R.id.txtSignIn:
                redirectLoginScreen();
                break;
            case R.id.imgBack:
                onBackPressed();
                break;
        }
    }
}