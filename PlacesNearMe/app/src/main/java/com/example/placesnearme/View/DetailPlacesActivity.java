package com.example.placesnearme.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.placesnearme.Adapter.ListDanhGiaDiaDiemAdapter;
import com.example.placesnearme.Adapter.ListHinhAnhDiaDiemAdapter;
import com.example.placesnearme.Common;
import com.example.placesnearme.Model.Firebase.DiaDiem;
import com.example.placesnearme.Model.Firebase.Review;
import com.example.placesnearme.Model.Firebase.User;
import com.example.placesnearme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class DetailPlacesActivity extends AppCompatActivity implements View.OnClickListener{
    private CircleImageView imgAvaDiaDiem, imgDienThoai, imgChrome, imgAva;
    private TextView txtTenDiaDiem, txtSdtDiaDiem, txtWebsite, txtDangHoatDong, txtDiaChi,
            txtThu2, txtThu3, txtThu4, txtThu5, txtThu6, txtThu7, txtChuNhat, txtDangBinhLuan;

    private EditText edBinhLuan;

    private Toolbar toolbar;

    private RecyclerView listHinhAnh, listReview;
    private ListHinhAnhDiaDiemAdapter adapterHinhAnh;
    private ListDanhGiaDiaDiemAdapter adapterDanhGia;
    private RecyclerView.LayoutManager layoutManager, layoutManager2;

    private RatingBar ratingBar;

    private FirebaseFirestore db;

    private SharedPreferences preferences, preferencesUser;
    private SharedPreferences.Editor editor;

    private List<String> hinhanhs = new ArrayList<>();
    private List<Review> reviews = new ArrayList<>();
    private List<User> users = new ArrayList<>();

    private FirebaseAuth.AuthStateListener mAuthListener;
    private StorageReference storageReference;
    private FirebaseUser user;

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_places);

        preferences = getSharedPreferences(Common.PREF_DIADIEM, 0);
        preferencesUser = getSharedPreferences(Common.PREF_USER, 0);
        editor = preferencesUser.edit();

        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        alertDialog = new SpotsDialog(this);

        imgAvaDiaDiem = findViewById(R.id.imgAvaDiaDiem);
        imgDienThoai = findViewById(R.id.imgDienThoai);
        imgChrome = findViewById(R.id.imgChrome);
        imgAva = findViewById(R.id.imgAva);

        txtTenDiaDiem = findViewById(R.id.txtTenDiaDiem);
        txtSdtDiaDiem = findViewById(R.id.txtSdtDiaDiem);
        txtWebsite = findViewById(R.id.txtWebsite);
        txtDangHoatDong = findViewById(R.id.txtDangHoatDong);
        txtDiaChi = findViewById(R.id.txtDiaChi);
        txtDangBinhLuan = findViewById(R.id.txtDangBinhLuan);

        edBinhLuan = findViewById(R.id.edBinhLuan);

        ratingBar = findViewById(R.id.rating);

        txtThu2 = findViewById(R.id.txtThu2);
        txtThu3 = findViewById(R.id.txtThu3);
        txtThu4 = findViewById(R.id.txtThu4);
        txtThu5 = findViewById(R.id.txtThu5);
        txtThu6 = findViewById(R.id.txtThu6);
        txtThu7 = findViewById(R.id.txtThu7);
        txtChuNhat = findViewById(R.id.txtChuNhat);

        toolbar = findViewById(R.id.toolbar);

        listHinhAnh = findViewById(R.id.recyclerHinhAnhDiaDiem);
        listHinhAnh.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 2);
        listHinhAnh.setLayoutManager(layoutManager);

        listReview = findViewById(R.id.recyclerReview);
        listReview.setHasFixedSize(true);
        layoutManager2 = new LinearLayoutManager(getApplicationContext());
        listReview.setLayoutManager(layoutManager2);

        layChiTietDiaDiem(preferences.getString(Common.madiadiem, ""));
        layReview(preferences.getString(Common.madiadiem, ""));

        setupFirebaseAuth();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setTitleTextColor(getColor(R.color.white));

        txtDangBinhLuan.setOnClickListener(this);
    }

    private void setBusinessTiming(TextView textView, String thu, String thoigianmocua, String thoigiandongcua){
        if (thoigianmocua.equals(getString(R.string.dongcua)))
            textView.setText(thu + " " + getString(R.string.dongcua));
        else if (thoigianmocua.equals(getString(R.string.mocua24h)))
            textView.setText(thu + " " + getString(R.string.mocua24h));
        else if (thoigianmocua.equals(getString(R.string.khongcothoigianhoatdong)))
            textView.setText(thu + " " + getString(R.string.khongcothoigianhoatdong));
        else
            textView.setText(thu + " " + thoigianmocua + " - " + thoigiandongcua);
    }

    private void layChiTietDiaDiem(final String madiadiem){
        alertDialog.show();

        hinhanhs.clear();

        db.collection(Common.DIADIEM).document(madiadiem).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        DiaDiem diaDiem = document.toObject(DiaDiem.class);

                        toolbar.setTitle(diaDiem.getTendiadiem());

                        txtTenDiaDiem.setText(diaDiem.getTendiadiem());
                        txtDiaChi.setText(diaDiem.getDiachi());

                        txtSdtDiaDiem.setText(diaDiem.getDienthoai());

                        if (diaDiem.getHinhanh().size() > 0){
                            hinhanhs.addAll(diaDiem.getHinhanh());

                            if (hinhanhs.get(0).substring(0, 5).equals("https")){
                                Picasso.get()
                                        .load(hinhanhs.get(0))
                                        .placeholder(R.drawable.img_loading)
                                        .into(imgAvaDiaDiem);
                            }else {
                                StorageReference storageImgProductType = FirebaseStorage.getInstance().getReference().child(Common.IMAGE)
                                        .child(madiadiem).child(hinhanhs.get(0));

                                long ONE_MEGABYTE = 1024 * 1024;
                                storageImgProductType.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        imgAvaDiaDiem.setImageBitmap(bitmap);
                                    }
                                });
                            }
                        }

                        adapterHinhAnh = new ListHinhAnhDiaDiemAdapter(hinhanhs, madiadiem);
                        adapterHinhAnh.notifyDataSetChanged();
                        listHinhAnh.setAdapter(adapterHinhAnh);

                        if (diaDiem.getThoigianhoatdong().size() > 0) {
                            for (int i = 0; i < diaDiem.getThoigianhoatdong().size(); i++) {
                                String thoigianmocua = diaDiem.getThoigianhoatdong().get(i).get(Common.mocua).toString();
                                String thoigiandongcua = diaDiem.getThoigianhoatdong().get(i).get(Common.dongcua).toString();

                                switch (i) {
                                    case 0:
                                        setBusinessTiming(txtThu2, getString(R.string.nhapthu2), thoigianmocua, thoigiandongcua);
                                        break;
                                    case 1:
                                        setBusinessTiming(txtThu3, getString(R.string.nhapthu3), thoigianmocua, thoigiandongcua);
                                        break;
                                    case 2:
                                        setBusinessTiming(txtThu4, getString(R.string.nhapthu4), thoigianmocua, thoigiandongcua);
                                        break;
                                    case 3:
                                        setBusinessTiming(txtThu5, getString(R.string.nhapthu5), thoigianmocua, thoigiandongcua);
                                        break;
                                    case 4:
                                        setBusinessTiming(txtThu6, getString(R.string.nhapthu6), thoigianmocua, thoigiandongcua);
                                        break;
                                    case 5:
                                        setBusinessTiming(txtThu7, getString(R.string.nhapthu7), thoigianmocua, thoigiandongcua);
                                        break;
                                    case 6:
                                        setBusinessTiming(txtChuNhat, getString(R.string.nhapchunhat), thoigianmocua, thoigiandongcua);
                                        break;
                                }
                            }
                        }

                        txtWebsite.setText(diaDiem.getWebsite());
                    } else {
                        Toast.makeText(DetailPlacesActivity.this, getString(R.string.khongtontaidiadiem), Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                } else {
                    Toast.makeText(DetailPlacesActivity.this, getString(R.string.coloitrongquatrinhlaydulieu), Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailPlacesActivity.this, getString(R.string.coloitrongquatrinhlaydulieu), Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
    }

    private void layReview(final String madiadiem){
        reviews.clear();

        db.collection(Common.DANHGIA).document(madiadiem).collection(Common.REVIEWS)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    Review review = doc.toObject(Review.class);

                    reviews.add(review);
                }

                adapterDanhGia = new ListDanhGiaDiaDiemAdapter(reviews);
                adapterDanhGia.notifyDataSetChanged();
                listReview.setAdapter(adapterDanhGia);

                alertDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.edit_menu_toolbar:
                SharedPreferences preferences = getSharedPreferences(Common.PREF_EDIT, 0);
                SharedPreferences.Editor editor = preferences.edit();

                editor.putBoolean(Common.edit, true);
                editor.commit();

                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
            default:break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadData(final String userId){
        users.clear();

        db.collection(Common.USER).whereEqualTo(Common.mauser, userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    User userModel = doc.toObject(User.class);

                    users.add(userModel);
                }

                editor.putString(Common.mauser, users.get(0).getMauser());
                editor.putString(Common.username, users.get(0).getUsername());
                editor.putString(Common.avatar, users.get(0).getAvatar());
                editor.commit();

                loadAvatar(users.get(0).getAvatar(), userId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage() + "", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadAvatar(String ava, String uid){
        if (ava.equals("ava_man.png")){
            StorageReference storageImgProductType = storageReference.child(Common.AVATAR).child(ava);

            long ONE_MEGABYTE = 1024 * 1024;
            storageImgProductType.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imgAva.setImageBitmap(bitmap);
                }
            });
        }else {
            StorageReference storageImgProductType = storageReference.child(Common.AVATAR)
                    .child(uid)
                    .child(ava);

            long ONE_MEGABYTE = 1024 * 1024;
            storageImgProductType.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imgAva.setImageBitmap(bitmap);
                }
            });
        }
    }

    private void setupFirebaseAuth(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null){
                    String idUser = user.getUid();
                    loadData(idUser);
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null)
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.txtDangBinhLuan:
                dangbinhluan(preferences.getString(Common.madiadiem, "")
                        , preferencesUser.getString(Common.mauser, "")
                        , preferencesUser.getString(Common.username, "")
                        , preferencesUser.getString(Common.avatar, ""));
                break;
        }
    }

    private void dangbinhluan(final String madiadiem, final String uid, final String ten, final String ava) {
        alertDialog.show();

        final DocumentReference docRef = db.collection(Common.DANHGIA).document(madiadiem)
                                            .collection(Common.REVIEWS).document();

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (!documentSnapshot.exists()){
                    final Review review = new Review();

                    if (ratingBar.getRating() != 0){
                        review.setDanhgia(String.valueOf(Math.round(ratingBar.getRating())));
                        review.setManguoireview(uid);
                        review.setNoidungreview(edBinhLuan.getText().toString());
                        review.setTennguoireview(ten);
                        review.setHinhanhnguoireview(ava);

                        docRef.set(review).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ratingBar.setRating(0);
                                edBinhLuan.setText("");

                                layReview(preferences.getString(Common.madiadiem, ""));
                                Toast.makeText(DetailPlacesActivity.this, getString(R.string.dabinhluan), Toast.LENGTH_SHORT).show();

                                alertDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DetailPlacesActivity.this, getString(R.string.coloitrongquatrinhlaydulieu), Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        });
                    } else{
                        Toast.makeText(DetailPlacesActivity.this, getString(R.string.loibinhluan), Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                }
            }
        });
    }
}
