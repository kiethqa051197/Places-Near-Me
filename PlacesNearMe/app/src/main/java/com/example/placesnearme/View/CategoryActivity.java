package com.example.placesnearme.View;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placesnearme.Adapter.ListDanhMucAdapter;
import com.example.placesnearme.Common;
import com.example.placesnearme.Model.Firebase.DanhMuc;
import com.example.placesnearme.Model.Firebase.DanhMucCha;
import com.example.placesnearme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class CategoryActivity extends AppCompatActivity implements View.OnClickListener{

    public RecyclerView listDanhMuc;
    private ListDanhMucAdapter adapterDanhMuc;

    private FirebaseFirestore db;

    private RecyclerView.LayoutManager layoutManager;
    private List<DanhMuc> danhMucList = new ArrayList<>();

    private ImageView imgBack;
    private TextView txtTenDanhMuc;

    private SharedPreferences prefCategory;

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoty_child);

        db = FirebaseFirestore.getInstance();

        prefCategory = getSharedPreferences(Common.PREF_DANHMUCCHA, 0);

        alertDialog = new SpotsDialog(this, R.style.Custom);

        imgBack = findViewById(R.id.imgBack);

        txtTenDanhMuc = findViewById(R.id.txtTenDanhMuc);

        listDanhMuc = findViewById(R.id.recyclerDanhMucCon);
        listDanhMuc.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        listDanhMuc.setLayoutManager(layoutManager);

        txtTenDanhMuc.setText(prefCategory.getString(Common.tendanhmuc, ""));

        loadCategory();

        imgBack.setOnClickListener(this);
    }

    private void loadCategory() {
        alertDialog.show();

        if (danhMucList.size() > 0)
            danhMucList.clear();

        db.collection(Common.DANHMUC).whereEqualTo(Common.DANHMUCCHA + "." + Common.madanhmuc, prefCategory.getString(Common.madanhmuc, ""))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    DanhMuc danhMuc = doc.toObject(DanhMuc.class);

                    danhMucList.add(danhMuc);
                }

                adapterDanhMuc = new ListDanhMucAdapter(danhMucList);
                adapterDanhMuc.notifyDataSetChanged();
                listDanhMuc.setAdapter(adapterDanhMuc);

                alertDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage() + "", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.imgBack:
                onBackPressed();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (adapterDanhMuc != null)
            adapterDanhMuc.notifyDataSetChanged();
    }
}