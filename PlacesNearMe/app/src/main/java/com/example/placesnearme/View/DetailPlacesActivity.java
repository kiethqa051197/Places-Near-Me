package com.example.placesnearme.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.placesnearme.Adapter.ListDanhGiaDiaDiemAdapter;
import com.example.placesnearme.Adapter.ListHinhAnhDiaDiemAdapter;
import com.example.placesnearme.Model.Firebase.DiaDiem;
import com.example.placesnearme.Model.Firebase.Review;
import com.example.placesnearme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailPlacesActivity extends AppCompatActivity implements View.OnClickListener{
    private CircleImageView imgAvaDiaDiem, imgDienThoai, imgChrome;
    private TextView txtTenDiaDiem, txtSdtDiaDiem, txtWebsite, txtDangHoatDong, txtDiaChi,
            txtThu2, txtThu3, txtThu4, txtThu5, txtThu6, txtThu7, txtChuNhat, txtSoHinh;

    private RecyclerView listHinhAnh, listReview;
    private ListHinhAnhDiaDiemAdapter adapterHinhAnh;
    private ListDanhGiaDiaDiemAdapter adapterDanhGia;
    private RecyclerView.LayoutManager layoutManager, layoutManager2;

    private FirebaseFirestore db;

    private SharedPreferences preferences;

    private List<String> hinhanhs = new ArrayList<>();
    private List<Review> reviews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_places);

        preferences = getSharedPreferences("prefMaDiaDiem", 0);

        db = FirebaseFirestore.getInstance();

        imgAvaDiaDiem = findViewById(R.id.imgAvaDiaDiem);
        imgDienThoai = findViewById(R.id.imgDienThoai);
        imgChrome = findViewById(R.id.imgChrome);

        txtTenDiaDiem = findViewById(R.id.txtTenDiaDiem);
        txtSdtDiaDiem = findViewById(R.id.txtSdtDiaDiem);
        txtWebsite = findViewById(R.id.txtWebsite);
        txtDangHoatDong = findViewById(R.id.txtDangHoatDong);
        txtDiaChi = findViewById(R.id.txtDiaChi);
        txtSoHinh = findViewById(R.id.txtSoHinh);

        txtThu2 = findViewById(R.id.txtThu2);
        txtThu3 = findViewById(R.id.txtThu3);
        txtThu4 = findViewById(R.id.txtThu4);
        txtThu5 = findViewById(R.id.txtThu5);
        txtThu6 = findViewById(R.id.txtThu6);
        txtThu7 = findViewById(R.id.txtThu7);
        txtChuNhat = findViewById(R.id.txtChuNhat);

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
    }

    private void layChiTietDiaDiem(final String madiadiem){
        db.collection("Dia Diem").document(madiadiem)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        DiaDiem diaDiem = document.toObject(DiaDiem.class);

                        txtTenDiaDiem.setText(diaDiem.getTendiadiem());
                        txtDiaChi.setText(diaDiem.getDiachi());

                        txtSdtDiaDiem.setText(diaDiem.getDienthoai());

                        txtSoHinh.setText(diaDiem.getHinhanh().size() + "");

                        if (diaDiem.getHinhanh().size() > 0){
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
                            txtThu2.setText(diaDiem.getThoigianhoatdong().get(0));
                            txtThu3.setText(diaDiem.getThoigianhoatdong().get(1));
                            txtThu4.setText(diaDiem.getThoigianhoatdong().get(2));
                            txtThu5.setText(diaDiem.getThoigianhoatdong().get(3));
                            txtThu6.setText(diaDiem.getThoigianhoatdong().get(4));
                            txtThu7.setText(diaDiem.getThoigianhoatdong().get(5));
                            txtChuNhat.setText(diaDiem.getThoigianhoatdong().get(6));
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
        db.collection("Danh Gia").document(madiadiem).collection("Reviews")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    Review review = doc.toObject(Review.class);

                    reviews.add(review);
                }

                adapterDanhGia = new ListDanhGiaDiaDiemAdapter(reviews, madiadiem);
                listReview.setAdapter(adapterDanhGia);
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
