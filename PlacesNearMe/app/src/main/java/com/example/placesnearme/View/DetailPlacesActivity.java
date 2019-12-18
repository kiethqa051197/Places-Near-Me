package com.example.placesnearme.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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

public class DetailPlacesActivity extends AppCompatActivity implements View.OnClickListener{
    private CircleImageView imgAvaDiaDiem, imgDienThoai, imgChrome, imgAva;
    private TextView txtTenDiaDiem, txtSdtDiaDiem, txtWebsite, txtDangHoatDong, txtDiaChi,
            txtThu2, txtThu3, txtThu4, txtThu5, txtThu6, txtThu7, txtChuNhat, txtSoHinh, txtDangBinhLuan;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_places);

        preferences = getSharedPreferences("prefMaDiaDiem", 0);
        preferencesUser = getSharedPreferences("prefUser", 0);
        editor = preferencesUser.edit();

        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        imgAvaDiaDiem = findViewById(R.id.imgAvaDiaDiem);
        imgDienThoai = findViewById(R.id.imgDienThoai);
        imgChrome = findViewById(R.id.imgChrome);
        imgAva = findViewById(R.id.imgAva);

        txtTenDiaDiem = findViewById(R.id.txtTenDiaDiem);
        txtSdtDiaDiem = findViewById(R.id.txtSdtDiaDiem);
        txtWebsite = findViewById(R.id.txtWebsite);
        txtDangHoatDong = findViewById(R.id.txtDangHoatDong);
        txtDiaChi = findViewById(R.id.txtDiaChi);
        txtSoHinh = findViewById(R.id.txtSoHinh);
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
        layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        listHinhAnh.setLayoutManager(layoutManager);

        listReview = findViewById(R.id.recyclerReview);
        listReview.setHasFixedSize(true);
        layoutManager2 = new LinearLayoutManager(getApplicationContext());
        listReview.setLayoutManager(layoutManager2);

        layChiTietDiaDiem(preferences.getString("maDiaDiem", ""));
        layReview(preferences.getString("maDiaDiem", ""));

        setupFirebaseAuth();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setTitleTextColor(getColor(R.color.white));

        txtDangBinhLuan.setOnClickListener(this);
    }

    private void layChiTietDiaDiem(final String madiadiem){
        db.collection("Dia Diem").document(madiadiem)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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

                            txtSoHinh.setText(diaDiem.getHinhanh().size() + "");

                            if (diaDiem.getHinhanh().size() > 3)
                                txtSoHinh.setVisibility(View.VISIBLE);
                            else
                                txtSoHinh.setVisibility(View.GONE);

                            for (int i = 0; i < diaDiem.getHinhanh().size(); i++)
                                hinhanhs.add(diaDiem.getHinhanh().get(i));

                            if (hinhanhs.get(0).substring(0, hinhanhs.get(0).indexOf(":")).equals("https")){
                                Picasso.get()
                                        .load(hinhanhs.get(0))
                                        .placeholder(R.drawable.img_loading)
                                        .into(imgAvaDiaDiem);
                            }else {
                                StorageReference storageImgProductType = FirebaseStorage.getInstance().getReference().child("Images")
                                        .child(diaDiem.getMadiadiem()).child(hinhanhs.get(0));

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
                        listHinhAnh.setAdapter(adapterHinhAnh);

                        if (diaDiem.getThoigianhoatdong().size() > 0){
                            if (diaDiem.getThoigianhoatdong().get(0).get("mocua").equals(getString(R.string.dongcua)))
                                txtThu2.setText(getString(R.string.nhapthu2) + " " + getString(R.string.dongcua));
                            else if (diaDiem.getThoigianhoatdong().get(0).get("mocua").equals(getString(R.string.mocua24h)))
                                txtThu2.setText(getString(R.string.nhapthu2) + " " + getString(R.string.mocua24h));
                            else if (diaDiem.getThoigianhoatdong().get(0).get("mocua").equals(getString(R.string.khongcothoigianhoatdong)))
                                txtThu2.setText(getString(R.string.nhapthu2) + " " + getString(R.string.khongcothoigianhoatdong));
                            else
                                txtThu2.setText(getString(R.string.nhapthu2) + " " + diaDiem.getThoigianhoatdong().get(0).get("mocua") + " - " + diaDiem.getThoigianhoatdong().get(0).get("dongcua"));

                            if (diaDiem.getThoigianhoatdong().get(1).get("mocua").equals(getString(R.string.dongcua)))
                                txtThu3.setText(getString(R.string.nhapthu3) + " " +getString(R.string.dongcua));
                            else if (diaDiem.getThoigianhoatdong().get(1).get("mocua").equals(getString(R.string.mocua24h)))
                                txtThu3.setText(getString(R.string.nhapthu3) + " " +getString(R.string.mocua24h));
                            else if (diaDiem.getThoigianhoatdong().get(1).get("mocua").equals(getString(R.string.khongcothoigianhoatdong)))
                                txtThu3.setText(getString(R.string.nhapthu3) + " " + getString(R.string.khongcothoigianhoatdong));
                            else
                                txtThu3.setText(getString(R.string.nhapthu3) + " " + diaDiem.getThoigianhoatdong().get(1).get("mocua") + " - " + diaDiem.getThoigianhoatdong().get(1).get("dongcua"));

                            if (diaDiem.getThoigianhoatdong().get(2).get("mocua").equals(getString(R.string.dongcua)))
                                txtThu4.setText(getString(R.string.nhapthu4) + " " + getString(R.string.dongcua));
                            else if (diaDiem.getThoigianhoatdong().get(2).get("mocua").equals(getString(R.string.mocua24h)))
                                txtThu4.setText(getString(R.string.nhapthu4) + " " + getString(R.string.mocua24h));
                            else if (diaDiem.getThoigianhoatdong().get(2).get("mocua").equals(getString(R.string.khongcothoigianhoatdong)))
                                txtThu4.setText(getString(R.string.nhapthu4) + " " + getString(R.string.khongcothoigianhoatdong));
                            else
                                txtThu4.setText(getString(R.string.nhapthu4) + " " + diaDiem.getThoigianhoatdong().get(2).get("mocua") + " - " + diaDiem.getThoigianhoatdong().get(2).get("dongcua"));

                            if (diaDiem.getThoigianhoatdong().get(3).get("mocua").equals(getString(R.string.dongcua)))
                                txtThu5.setText(getString(R.string.nhapthu5) + " " + getString(R.string.dongcua));
                            else if (diaDiem.getThoigianhoatdong().get(3).get("mocua").equals(getString(R.string.mocua24h)))
                                txtThu5.setText(getString(R.string.nhapthu5) + " " + getString(R.string.mocua24h));
                            else if (diaDiem.getThoigianhoatdong().get(3).get("mocua").equals(getString(R.string.khongcothoigianhoatdong)))
                                txtThu5.setText(getString(R.string.nhapthu5) + " " + getString(R.string.khongcothoigianhoatdong));
                            else
                                txtThu5.setText(getString(R.string.nhapthu5) + " " + diaDiem.getThoigianhoatdong().get(3).get("mocua") + " - " + diaDiem.getThoigianhoatdong().get(3).get("dongcua"));

                            if (diaDiem.getThoigianhoatdong().get(4).get("mocua").equals(getString(R.string.dongcua)))
                                txtThu6.setText(getString(R.string.nhapthu6) + " " + getString(R.string.dongcua));
                            else if (diaDiem.getThoigianhoatdong().get(4).get("mocua").equals(getString(R.string.mocua24h)))
                                txtThu6.setText(getString(R.string.nhapthu6) + " " + getString(R.string.mocua24h));
                            else if (diaDiem.getThoigianhoatdong().get(4).get("mocua").equals(getString(R.string.khongcothoigianhoatdong)))
                                txtThu6.setText(getString(R.string.nhapthu6) + " " + getString(R.string.khongcothoigianhoatdong));
                            else
                                txtThu6.setText(getString(R.string.nhapthu6) + " " + diaDiem.getThoigianhoatdong().get(4).get("mocua") + " - " + diaDiem.getThoigianhoatdong().get(4).get("dongcua"));

                            if (diaDiem.getThoigianhoatdong().get(5).get("mocua").equals(getString(R.string.dongcua)))
                                txtThu7.setText(getString(R.string.nhapthu7) + " " + getString(R.string.dongcua));
                            else if (diaDiem.getThoigianhoatdong().get(5).get("mocua").equals(getString(R.string.mocua24h)))
                                txtThu7.setText(getString(R.string.nhapthu7) + " " + getString(R.string.mocua24h));
                            else if (diaDiem.getThoigianhoatdong().get(5).get("mocua").equals(getString(R.string.khongcothoigianhoatdong)))
                                txtThu7.setText(getString(R.string.nhapthu7) + " " + getString(R.string.khongcothoigianhoatdong));
                            else
                                txtThu7.setText(getString(R.string.nhapthu7) + " " + diaDiem.getThoigianhoatdong().get(5).get("mocua") + " - " + diaDiem.getThoigianhoatdong().get(5).get("dongcua"));

                            if (diaDiem.getThoigianhoatdong().get(6).get("mocua").equals(getString(R.string.dongcua)))
                                txtChuNhat.setText(getString(R.string.nhapchunhat) + " " + getString(R.string.dongcua));
                            else if (diaDiem.getThoigianhoatdong().get(6).get("mocua").equals(getString(R.string.mocua24h)))
                                txtChuNhat.setText(getString(R.string.nhapchunhat) + " " + getString(R.string.mocua24h));
                            else if (diaDiem.getThoigianhoatdong().get(6).get("mocua").equals(getString(R.string.khongcothoigianhoatdong)))
                                txtChuNhat.setText(getString(R.string.nhapchunhat) + " " + getString(R.string.khongcothoigianhoatdong));
                            else
                                txtChuNhat.setText(getString(R.string.nhapchunhat) + " " + diaDiem.getThoigianhoatdong().get(6).get("mocua") + " - " + diaDiem.getThoigianhoatdong().get(6).get("dongcua"));
                            }

                        txtWebsite.setText(diaDiem.getWebsite());
                    } else {
                        Toast.makeText(DetailPlacesActivity.this, "Không tồn tại địa điểm này", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DetailPlacesActivity.this, "Lỗi! Không lấy dữ liệu được", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void layReview(final String madiadiem){
        reviews.clear();

        db.collection("Danh Gia").document(madiadiem).collection("Reviews")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    Review review = doc.toObject(Review.class);

                    reviews.add(review);
                }

                adapterDanhGia = new ListDanhGiaDiaDiemAdapter(reviews, madiadiem);
                adapterDanhGia.notifyDataSetChanged();
                listReview.setAdapter(adapterDanhGia);
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
                SharedPreferences preferences = getSharedPreferences("prefEdit", 0);
                SharedPreferences.Editor editor = preferences.edit();

                editor.putBoolean("edit", true);
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

        db.collection("User").whereEqualTo("mauser", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    User userModel = doc.toObject(User.class);

                    users.add(userModel);
                }

                editor.putString("uid", users.get(0).getMauser());
                editor.putString("ten", users.get(0).getUsername());
                editor.putString("ava", users.get(0).getAvatar());
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
        StorageReference storageImgProductType = storageReference.child("Avatar").child(uid).child(ava);

        long ONE_MEGABYTE = 1024 * 1024;
        storageImgProductType.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imgAva.setImageBitmap(bitmap);
            }
        });
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
                dangbinhluan(preferences.getString("maDiaDiem", "")
                        , preferencesUser.getString("uid", "")
                        , preferencesUser.getString("ten", "")
                        , preferencesUser.getString("ava", ""));
                break;
        }
    }

    private void dangbinhluan(String madiadiem, final String uid, final String ten, final String ava) {
        final DocumentReference docRef = db.collection("Danh Gia").document(madiadiem)
                                            .collection("Reviews").document();

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

                                layReview(preferences.getString("maDiaDiem", ""));
                                Toast.makeText(DetailPlacesActivity.this, "Thành công!!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else
                        Toast.makeText(DetailPlacesActivity.this, "Vui lòng đánh giá sao cho địa điểm! Bình luận có thể để trống!", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
