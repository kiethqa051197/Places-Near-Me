package com.example.placesnearme.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.placesnearme.Adapter.ListItemDanhMucAdapter;
import com.example.placesnearme.Model.DanhMuc;
import com.example.placesnearme.Model.DanhMucCha;
import com.example.placesnearme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity{

    private ActionBar actionBar;
    public RecyclerView listItemDanhMuc;
    private ListItemDanhMucAdapter adapterDanhMuc;

    private FirebaseFirestore db;

    private RecyclerView.LayoutManager layoutManagerDanhMuc;
    private List<DanhMuc> danhMucList;

    SharedPreferences prefCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        db = FirebaseFirestore.getInstance();

        prefCategory = getSharedPreferences("shareDanhMucCha", 0); // 0 - for private mode

        listItemDanhMuc = findViewById(R.id.listDanhMuc);
        listItemDanhMuc.setHasFixedSize(true);
        layoutManagerDanhMuc = new GridLayoutManager(getApplicationContext(), 3);
        listItemDanhMuc.setLayoutManager(layoutManagerDanhMuc);

        actionBar = getSupportActionBar();
        actionBar.setTitle(prefCategory.getString("tendanhmuccha", "Error")); //Thiết lập tiêu đề nếu muốn
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadCategory();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadCategory() {
        danhMucList = new ArrayList<>();
        if (danhMucList.size() > 0)
            danhMucList.clear();

        db.collection("Danh Muc").whereEqualTo("Danh Muc Cha.madanhmuc", prefCategory.getString("madanhmuccha", "error")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    DanhMuc danhMuc = new DanhMuc(doc.getString("madanhmuc"),
                            doc.getString("tendanhmuc"), doc.getString("hinhanh"), doc.toObject(DanhMucCha.class));

                    danhMucList.add(danhMuc);
                }

                Log.d("ktra", danhMucList.size() + "");

                adapterDanhMuc = new ListItemDanhMucAdapter(CategoryActivity.this, danhMucList);
                listItemDanhMuc.setAdapter(adapterDanhMuc);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage() + "", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
