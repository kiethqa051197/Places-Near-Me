package com.example.placesnearme.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.placesnearme.Adapter.ListAddDanhMucAdapter;
import com.example.placesnearme.Adapter.ListDanhMucAdapter;
import com.example.placesnearme.Common;
import com.example.placesnearme.Model.Firebase.DanhMuc;
import com.example.placesnearme.Model.Firebase.DanhMucCha;
import com.example.placesnearme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.text.TextUtils.isEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddCategoryActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{
    private static final int RESULT_LOAD_IMAGE = 1;

    private ImageView imgChonHinh;
    public EditText edNhapTuKhoa, edTenDanhMuc;
    private Button btnThemDanhMuc;

    private String fileName = "";

    private FirebaseFirestore db;
    private StorageReference storageReference;

    private Toolbar toolbar;

    private Uri uri;

    private List<DanhMuc> danhMucList = new ArrayList<>();
    private List<DanhMucCha> danhMucChaList = new ArrayList<>();

    private RecyclerView listItem;
    private RecyclerView.LayoutManager layoutManager;

    private Spinner spinDanhMucCha;

    private ArrayAdapter<DanhMucCha> arrayAdapterDanhMuc;
    private ListAddDanhMucAdapter adapter;

    public boolean isUpdate = false;
    public String idUpdate = "";
    public String danhMucDuocChon = "";
    public String tenDanhMucDuocChon = "";
    public String hinhanhdanhMucDuocChon = "";

    public String hinhanh = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setTitleTextColor(getColor(R.color.white));
        toolbar.setTitle(getString(R.string.capnhatthemdanhmuc));

        layDanhMucCha();

        spinDanhMucCha = findViewById(R.id.spinDanhMucCha);
        arrayAdapterDanhMuc = new ArrayAdapter<DanhMucCha>(this, android.R.layout.simple_list_item_1, danhMucChaList);

        spinDanhMucCha.setAdapter(arrayAdapterDanhMuc);
        arrayAdapterDanhMuc.notifyDataSetChanged();

        listItem = findViewById(R.id.recyclerDanhMuc);
        listItem.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 3);
        listItem.setLayoutManager(layoutManager);

        imgChonHinh = findViewById(R.id.imgChonHinh);
        edNhapTuKhoa = findViewById(R.id.edNhapTuKhoa);
        edTenDanhMuc = findViewById(R.id.edTenDanhMuc);
        btnThemDanhMuc = findViewById(R.id.btnThemDanhMuc);

        if (isUpdate){
            btnThemDanhMuc.setText(getString(R.string.suadanhmuc));
        }else
            btnThemDanhMuc.setText(getString(R.string.themdanhmuc2));

        imgChonHinh.setOnClickListener(this);
        btnThemDanhMuc.setOnClickListener(this);
        spinDanhMucCha.setOnItemSelectedListener(this);
    }

    private void layDanhMucCha() {
        if (danhMucChaList.size() > 0)
            danhMucChaList.clear();

        db.collection(Common.DANHMUCCHA).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    DanhMucCha danhMucCha = doc.toObject(DanhMucCha.class);

                    danhMucChaList.add(danhMucCha);
                }
                arrayAdapterDanhMuc.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddCategoryActivity.this, e.getMessage() + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallary(){
        Intent intent = new Intent();
        intent.setType(Common.setStype);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, Common.titleChooseImage), RESULT_LOAD_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK){
            if (data.getData() != null){
                uri = data.getData();
                fileName = getFileName(uri);

                imgChonHinh.setImageResource(android.R.color.transparent);
                imgChonHinh.setImageURI(uri);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals("Delete"))
            deleteItem(item.getOrder());

        return super.onContextItemSelected(item);
    }

    private void deleteItem(int order) {
        db.collection(Common.DANHMUC).document(danhMucList.get(order).getMadanhmuc())
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        layDanhMuc(danhMucDuocChon);
                    }
                });
    }

    private void upLoadImage(String madanhmuc, Uri uri){
        StorageReference fileUpload = storageReference.child(Common.DANHMUC).child(madanhmuc);
        fileUpload.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddCategoryActivity.this, getString(R.string.themhinhthanhcong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileName(Uri uri){
        String result = null;

        if (uri.getScheme().equals(Common.content)){
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }finally {
                cursor.close();
            }
        }

        if (result == null){
            result = uri.getPath();

            int cut = result.lastIndexOf('/');

            if (cut != -1){
                result = result.substring(cut + 1);
            }
        }

        return result;
    }

    private void themDanhMuc(){
        String input = edNhapTuKhoa.getText().toString();
        final List<String> inputArray = Arrays.asList(input.split("\\s*, \\s*"));

        final DocumentReference docRef = db.collection(Common.DANHMUC).document();

        if (!isEmpty(edTenDanhMuc.getText().toString()) && !fileName.equals("")){
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (!documentSnapshot.exists()){
                        DanhMucCha danhMucCha = new DanhMucCha();
                        danhMucCha.setMadanhmuc(danhMucDuocChon);
                        danhMucCha.setTendanhmuc(tenDanhMucDuocChon);
                        danhMucCha.setHinhanh(hinhanhdanhMucDuocChon);

                        Map<String, Object> danhmuc = new HashMap<>();
                        danhmuc.put(Common.madanhmuc, docRef.getId());
                        danhmuc.put(Common.tendanhmuc, edTenDanhMuc.getText().toString());
                        danhmuc.put(Common.DANHMUCCHA, danhMucCha);
                        danhmuc.put(Common.hinhanh, docRef.getId());
                        danhmuc.put(Common.tukhoa, inputArray);

                        docRef.set(danhmuc).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                upLoadImage(docRef.getId(), uri);
                                Toast.makeText(AddCategoryActivity.this, getString(R.string.themthanhcong), Toast.LENGTH_SHORT).show();

                                layDanhMuc(danhMucDuocChon);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddCategoryActivity.this, getString(R.string.themloi), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }else
            Toast.makeText(this, getString(R.string.tendanhmuctrong), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.imgChonHinh:
                openGallary();
                break;
            case R.id.btnThemDanhMuc:
                if (!isUpdate)
                    themDanhMuc();
                else{
                    String input = edNhapTuKhoa.getText().toString();
                    final List<String> inputArray = Arrays.asList(input.split("\\s*, \\s*"));

                    if (fileName.equals("")){
                        updateData(edTenDanhMuc.getText().toString(), hinhanh, inputArray);
                    }else{
                        upLoadImage(idUpdate, uri);
                        updateData(edTenDanhMuc.getText().toString(), fileName, inputArray);
                    }
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinDanhMucCha:
                danhMucDuocChon = danhMucChaList.get(position).getMadanhmuc();
                tenDanhMucDuocChon = danhMucChaList.get(position).getTendanhmuc();
                hinhanhdanhMucDuocChon = danhMucChaList.get(position).getHinhanh();

                layDanhMuc(danhMucDuocChon);
                break;
        }
    }

    private void layDanhMuc(String madanhmuc) {
        if (danhMucList.size() > 0){
            danhMucList.clear();
            adapter.notifyDataSetChanged();
        }

        db.collection(Common.DANHMUC).whereEqualTo(Common.DANHMUCCHA + "." + Common.madanhmuc, madanhmuc)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    DanhMuc danhMuc = doc.toObject(DanhMuc.class);
                    danhMucList.add(danhMuc);
                }

                adapter = new ListAddDanhMucAdapter(danhMucList, AddCategoryActivity.this);
                adapter.notifyDataSetChanged();
                listItem.setAdapter(adapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage() + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void updateData(String tendanhmuc, String fileName, List<String> tukhoas) {
        DocumentReference docref = db.collection(Common.DANHMUC).document(idUpdate);

        docref.update(Common.tendanhmuc, tendanhmuc);
        docref.update(Common.hinhanh, fileName);

        docref.update(Common.tukhoa, FieldValue.delete());

        Map<String, Object> tukhoaMap = new HashMap<>();
        tukhoaMap.put(Common.tukhoa, tukhoas);
        docref.set(tukhoaMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                layDanhMuc(danhMucDuocChon);
                Toast.makeText(AddCategoryActivity.this, getString(R.string.themthanhcong), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
