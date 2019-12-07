package com.example.placesnearme.View.Fragment;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placesnearme.Adapter.ListHinhDaChonAdapter;
import com.example.placesnearme.Model.Firebase.DanhMuc;
import com.example.placesnearme.Model.Firebase.DanhMucCha;
import com.example.placesnearme.Model.Firebase.DiaDiem;
import com.example.placesnearme.R;
import com.example.placesnearme.View.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Nullable;

public class AddPlaceFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener,
        RadioGroup.OnCheckedChangeListener{
    private static final int RESULT_LOAD_IMAGE = 1;

    private Button btnThemDiaDiem, btnGioMoCuaThu2Thu6, btnGioDongCuaThu2Thu6, btnGioMoCuaThu7, btnGioDongCuaThu7,
            btnGioMoCuaChuNhat, btnGioDongCuaChuNhat;
    private RecyclerView recyclerChonHinh;
    private EditText edTenDiaDiem, edDiaChi, edSoDienThoai, edWebsite;
    private Spinner spinDanhMucCha;
    private RadioGroup rdGroupGioHoatDong, rdGroupGioMoCuaThu2Thu6, rdGroupGioMoCuaThu7, rdGroupGioMoCuaChuNhat;
    private LinearLayout linearChonGio, linearChonGioThu2Thu6, linearChonGioThu7, linearChonGioChuNhat;
    private GridLayout khungDanhMuc;
    private ImageView btnChonAnh;

    private FirebaseFirestore db;
    private StorageReference storageReference;

    private ListHinhDaChonAdapter adapterHinhDaChon;
    private RecyclerView.LayoutManager layoutManager;

    private List<Uri> uriList = new ArrayList<>();
    private List<String> fileNameList = new ArrayList<>();
    private List<DanhMucCha> danhMucChaList = new ArrayList<>();
    private List<String> danhMucDuocChonList = new ArrayList<>();
    private List<String> thoigianhoatdong = new ArrayList<>();

    private ArrayAdapter<DanhMucCha> arrayAdapterDanhMuc;
    private List<DanhMuc> danhMucList = new ArrayList<>();

    private String danhMucDuocChon, gioMoCuaThu7, gioDongCuaThu7, gioMoCuaChuNhat, gioDongCuaChuNhat,
            gioMoCuaThu2Thu6, gioDongCuaThu2Thu6;

    private boolean hoatdong24h = false, khongcothoigianhoatdong = true, hoatdong24hthu2thu6, khonghoatdongthu2thu6,
            hoatdong24hthu7, khonghoatdongthu7, hoatdong24hchunhat, khonghoatdongchunhat;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new_places, container, false);

        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        btnThemDiaDiem = view.findViewById(R.id.btnThemDiaDiem);
        btnChonAnh = view.findViewById(R.id.btnChonAnh);
        btnGioMoCuaThu2Thu6 = view.findViewById(R.id.btnGioMoCuaThu2Thu6);
        btnGioDongCuaThu2Thu6 = view.findViewById(R.id.btnGioDongCuaThu2Thu6);
        btnGioMoCuaThu7 = view.findViewById(R.id.btnGioMoCuaThu7);
        btnGioDongCuaThu7 = view.findViewById(R.id.btnGioDongCuaThu7);
        btnGioMoCuaChuNhat = view.findViewById(R.id.btnGioMoCuaChuNhat);
        btnGioDongCuaChuNhat = view.findViewById(R.id.btnGioDongCuaChuNhat);

        recyclerChonHinh = view.findViewById(R.id.recyclerChonHinh);

        edTenDiaDiem = view.findViewById(R.id.edTenDiaDiem);
        edDiaChi = view.findViewById(R.id.edDiaChi);
        edSoDienThoai = view.findViewById(R.id.edSoDienThoai);
        edWebsite = view.findViewById(R.id.edWebsite);

        spinDanhMucCha = view.findViewById(R.id.spinDanhMucCha);

        rdGroupGioHoatDong = view.findViewById(R.id.rdGroupGioHoatDong);
        rdGroupGioMoCuaThu2Thu6 = view.findViewById(R.id.rdGroupGioMoCuaThu2Thu6);
        rdGroupGioMoCuaThu7 = view.findViewById(R.id.rdGroupGioMoCuaThu7);
        rdGroupGioMoCuaChuNhat = view.findViewById(R.id.rdGroupGioMoCuaChuNhat);

        linearChonGio = view.findViewById(R.id.linearChonGio);
        linearChonGioThu2Thu6 = view.findViewById(R.id.linearChonGioThu2Thu6);
        linearChonGioThu7 = view.findViewById(R.id.linearChonGioThu7);
        linearChonGioChuNhat = view.findViewById(R.id.linearChonGioChuNhat);

        khungDanhMuc = view.findViewById(R.id.khungDanhMuc);

        recyclerChonHinh = view.findViewById(R.id.recyclerChonHinh);
        recyclerChonHinh.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getContext(), 4);
        recyclerChonHinh.setLayoutManager(layoutManager);

        linearChonGio.setVisibility(View.GONE);

        layDanhMucCha();
        arrayAdapterDanhMuc = new ArrayAdapter<DanhMucCha>(getContext(), android.R.layout.simple_list_item_1, danhMucChaList);

        spinDanhMucCha.setAdapter(arrayAdapterDanhMuc);
        arrayAdapterDanhMuc.notifyDataSetChanged();

        btnThemDiaDiem.setOnClickListener(this);
        btnChonAnh.setOnClickListener(this);
        btnGioMoCuaThu2Thu6.setOnClickListener(this);
        btnGioDongCuaThu2Thu6.setOnClickListener(this);
        btnGioMoCuaThu7.setOnClickListener(this);
        btnGioDongCuaThu7.setOnClickListener(this);
        btnGioMoCuaChuNhat.setOnClickListener(this);
        btnGioDongCuaChuNhat.setOnClickListener(this);

        spinDanhMucCha.setOnItemSelectedListener(this);

        rdGroupGioHoatDong.setOnCheckedChangeListener(this);
        rdGroupGioMoCuaThu2Thu6.setOnCheckedChangeListener(this);
        rdGroupGioMoCuaThu7.setOnCheckedChangeListener(this);
        rdGroupGioMoCuaChuNhat.setOnCheckedChangeListener(this);

        return view;
    }

    private void openGallary(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK){
            if(data.getClipData() != null){
                int total = data.getClipData().getItemCount();

                for (int i = 0; i < total; i++){
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    String fileName = getFileName(uri);

                    uriList.add(uri);
                    fileNameList.add(fileName);
                }

                adapterHinhDaChon = new ListHinhDaChonAdapter(uriList);
                recyclerChonHinh.setAdapter(adapterHinhDaChon);
            }else if (data.getData() != null){
                Uri uri = data.getData();
                String fileName = getFileName(uri);

                uriList.add(uri);
                fileNameList.add(fileName);

                adapterHinhDaChon = new ListHinhDaChonAdapter(uriList);
                recyclerChonHinh.setAdapter(adapterHinhDaChon);
            }
        }
    }

    public String getFileName(Uri uri){
        String result = null;

        if (uri.getScheme().equals("content")){
            Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
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

    private void layDanhMucCha(){
        db.collection("Danh Muc Cha").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                Toast.makeText(getContext(), e.getMessage() + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void layDanhMuc(String madanhmuc) {
        danhMucList.clear();
        khungDanhMuc.removeAllViews();

        db.collection("Danh Muc").whereEqualTo("Danh Muc Cha.madanhmuc", madanhmuc)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    DanhMuc danhMuc = doc.toObject(DanhMuc.class);
                    danhMucList.add(danhMuc);
                }

                int total = danhMucList.size();
                int column = 3;
                int row = total / column;
                khungDanhMuc.setColumnCount(column);
                khungDanhMuc.setRowCount(row + 1);

                for (int i = 0, c = 0, r = 0; i < danhMucList.size(); i++, c++){
                    String maDanhMuc = danhMucList.get(i).getMadanhmuc();

                    if(c == column)
                    {
                        c = 0;
                        r++;
                    }

                    CheckBox checkBox = new CheckBox(getContext());

                    GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                    param.height = GridLayout.LayoutParams.WRAP_CONTENT;
                    param.width = GridLayout.LayoutParams.WRAP_CONTENT;
                    param.rightMargin = 10;
                    param.topMargin = 10;
                    param.setGravity(Gravity.CENTER);
                    param.columnSpec = GridLayout.spec(c);
                    param.rowSpec = GridLayout.spec(r);

                    checkBox.setLayoutParams(param);

                    checkBox.setText(danhMucList.get(i).getTendanhmuc());
                    checkBox.setTag(maDanhMuc);
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            String maDanhMuc = buttonView.getTag().toString();
                            if(isChecked){
                                danhMucDuocChonList.add(maDanhMuc);
                            } else {
                                danhMucDuocChonList.remove(maDanhMuc);
                            }
                        }
                    });
                    khungDanhMuc.addView(checkBox);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage() + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ThemDiaDiem(){
        final DocumentReference docRef = db.collection("Dia Diem").document();

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable DocumentSnapshot documentSnapshot, @androidx.annotation.Nullable FirebaseFirestoreException e) {
                if (!documentSnapshot.exists()){
                    final DiaDiem diaDiem = new DiaDiem();

                    GeoPoint location = new GeoPoint(MainActivity.latitude, MainActivity.longtitude);

                    if (hoatdong24h){
                        thoigianhoatdong.clear();

                        thoigianhoatdong.add(getString(R.string.nhapthu2) + " " + getString(R.string.mocua24h));
                        thoigianhoatdong.add(getString(R.string.nhapthu3) + " " + getString(R.string.mocua24h));
                        thoigianhoatdong.add(getString(R.string.nhapthu4) + " " + getString(R.string.mocua24h));
                        thoigianhoatdong.add(getString(R.string.nhapthu5) + " " + getString(R.string.mocua24h));
                        thoigianhoatdong.add(getString(R.string.nhapthu6) + " " + getString(R.string.mocua24h));
                        thoigianhoatdong.add(getString(R.string.nhapthu7) + " " + getString(R.string.mocua24h));
                        thoigianhoatdong.add(getString(R.string.nhapchunhat) + " " + getString(R.string.mocua24h));
                    }else if(khongcothoigianhoatdong){
                        thoigianhoatdong.clear();

                        thoigianhoatdong.add(getString(R.string.nhapthu2) + " " + getString(R.string.khongcothoigianhoatdong));
                        thoigianhoatdong.add(getString(R.string.nhapthu3) + " " + getString(R.string.khongcothoigianhoatdong));
                        thoigianhoatdong.add(getString(R.string.nhapthu4) + " " + getString(R.string.khongcothoigianhoatdong));
                        thoigianhoatdong.add(getString(R.string.nhapthu5) + " " + getString(R.string.khongcothoigianhoatdong));
                        thoigianhoatdong.add(getString(R.string.nhapthu6) + " " + getString(R.string.khongcothoigianhoatdong));
                        thoigianhoatdong.add(getString(R.string.nhapthu7) + " " + getString(R.string.khongcothoigianhoatdong));
                        thoigianhoatdong.add(getString(R.string.nhapchunhat) + " " + getString(R.string.khongcothoigianhoatdong));
                    }else{
                        thoigianhoatdong.clear();

                        if (hoatdong24hthu2thu6){
                            thoigianhoatdong.add(getString(R.string.nhapthu2) + " " + getString(R.string.mocua24h));
                            thoigianhoatdong.add(getString(R.string.nhapthu3) + " " + getString(R.string.mocua24h));
                            thoigianhoatdong.add(getString(R.string.nhapthu4) + " " + getString(R.string.mocua24h));
                            thoigianhoatdong.add(getString(R.string.nhapthu5) + " " + getString(R.string.mocua24h));
                            thoigianhoatdong.add(getString(R.string.nhapthu6) + " " + getString(R.string.mocua24h));
                        }else if (khonghoatdongthu2thu6){
                            thoigianhoatdong.add(getString(R.string.nhapthu2) + " " + getString(R.string.khongmocua));
                            thoigianhoatdong.add(getString(R.string.nhapthu3) + " " + getString(R.string.khongmocua));
                            thoigianhoatdong.add(getString(R.string.nhapthu4) + " " + getString(R.string.khongmocua));
                            thoigianhoatdong.add(getString(R.string.nhapthu5) + " " + getString(R.string.khongmocua));
                            thoigianhoatdong.add(getString(R.string.nhapthu6) + " " + getString(R.string.khongmocua));
                        }else{
                            thoigianhoatdong.add(getString(R.string.nhapthu2) + " " + gioMoCuaThu2Thu6 + " - " + gioDongCuaThu2Thu6);
                            thoigianhoatdong.add(getString(R.string.nhapthu3) + " " + gioMoCuaThu2Thu6 + " - " + gioDongCuaThu2Thu6);
                            thoigianhoatdong.add(getString(R.string.nhapthu4) + " " + gioMoCuaThu2Thu6 + " - " + gioDongCuaThu2Thu6);
                            thoigianhoatdong.add(getString(R.string.nhapthu5) + " " + gioMoCuaThu2Thu6 + " - " + gioDongCuaThu2Thu6);
                            thoigianhoatdong.add(getString(R.string.nhapthu6) + " " + gioMoCuaThu2Thu6 + " - " + gioDongCuaThu2Thu6);
                        }

                        if (hoatdong24hthu7)
                            thoigianhoatdong.add(getString(R.string.nhapthu7) + " " + getString(R.string.mocua24h));
                        else if(khonghoatdongthu7)
                            thoigianhoatdong.add(getString(R.string.nhapthu7) + " " + getString(R.string.khongmocua));
                        else
                            thoigianhoatdong.add(getString(R.string.nhapthu7) + " " + gioMoCuaThu7 + " - " + gioDongCuaThu7);

                        if (hoatdong24hchunhat)
                            thoigianhoatdong.add(getString(R.string.nhapchunhat) + " " + getString(R.string.mocua24h));
                        else if(khonghoatdongchunhat)
                            thoigianhoatdong.add(getString(R.string.nhapchunhat) + " " + getString(R.string.khongmocua));
                        else
                            thoigianhoatdong.add(getString(R.string.nhapchunhat) + " " + gioMoCuaChuNhat + " - " + gioDongCuaChuNhat);
                    }

                    if(!edTenDiaDiem.getText().toString().equals("")){
                        if (!edDiaChi.getText().toString().equals("")){
                            if (danhMucDuocChonList.size() > 0){
                                diaDiem.setMadiadiem(docRef.getId());
                                diaDiem.setTendiadiem(edTenDiaDiem.getText().toString());
                                diaDiem.setWebsite(edWebsite.getText().toString());
                                diaDiem.setLocation(location);
                                diaDiem.setDanhmuc(danhMucDuocChonList);
                                diaDiem.setDienthoai(edSoDienThoai.getText().toString());
                                diaDiem.setHinhanh(fileNameList);
                                diaDiem.setThoigianhoatdong(thoigianhoatdong);
                                diaDiem.setDiachi(edDiaChi.getText().toString());

                                docRef.set(diaDiem).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        btnThemDiaDiem.setEnabled(false);

                                        Toast.makeText(getContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();

                                        for (int j = 0; j < uriList.size(); j++){
                                            StorageReference fileUpload = storageReference.child("Images").child(diaDiem.getMadiadiem()).child(getFileName(uriList.get(j)));
                                            fileUpload.putFile(uriList.get(j)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    Toast.makeText(getContext(), "Thêm hình ảnh thành công", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), "Thêm hình ảnh thất bại", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Thêm thất bại!!!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else {
                                Toast.makeText(getContext(), "Địa điểm phải có ít nhất một danh mục!!!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(getContext(), "Không được để trống địa chỉ!!!", Toast.LENGTH_SHORT).show();
                        }
                    }else
                        Toast.makeText(getContext(), "Không được để trống tên địa điểm!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        if (adapterHinhDaChon != null)
            adapterHinhDaChon.notifyDataSetChanged();
    }

    @Override
    public void onClick(final View v) {
        Calendar calendar = Calendar.getInstance();
        int gio = calendar.get(Calendar.HOUR_OF_DAY);
        int phut = calendar.get(Calendar.MINUTE);

        switch (v.getId()){
            case R.id.btnChonAnh:
                openGallary();
                break;

            //Chọn giờ mở cửa thứ 2 đến thứ 6
            case R.id.btnGioMoCuaThu2Thu6:
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        gioMoCuaThu2Thu6 = hourOfDay + ":" + minute;
                        ((Button)v).setText(gioMoCuaThu2Thu6);
                    }
                }, gio, phut, true);

                timePickerDialog.show();
                break;
            case R.id.btnGioDongCuaThu2Thu6:
                TimePickerDialog timePickerDialog2 = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        gioDongCuaThu2Thu6 = hourOfDay + ":" + minute;
                        ((Button)v).setText(gioDongCuaThu2Thu6);
                    }
                }, gio, phut, true);

                timePickerDialog2.show();
                break;

            // chọn giờ mở cửa thứ 7
            case R.id.btnGioMoCuaThu7:
                TimePickerDialog timePickerDialog3 = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        gioMoCuaThu7 = hourOfDay + ":" + minute;
                        ((Button)v).setText(gioMoCuaThu7);
                    }
                }, gio, phut, true);

                timePickerDialog3.show();
                break;
            case R.id.btnGioDongCuaThu7:
                TimePickerDialog timePickerDialog4 = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        gioDongCuaThu7 = hourOfDay + ":" + minute;
                        ((Button)v).setText(gioDongCuaThu7);
                    }
                }, gio, phut, true);

                timePickerDialog4.show();
                break;

            // chọn giờ mở cửa chủ nhật
            case R.id.btnGioMoCuaChuNhat:
                TimePickerDialog timePickerDialog5 = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        gioMoCuaChuNhat = hourOfDay + ":" + minute;
                        ((Button)v).setText(gioMoCuaChuNhat);
                    }
                }, gio, phut, true);

                timePickerDialog5.show();
                break;
            case R.id.btnGioDongCuaChuNhat:
                TimePickerDialog timePickerDialog6 = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        gioDongCuaChuNhat = hourOfDay + ":" + minute;
                        ((Button)v).setText(gioDongCuaChuNhat);
                    }
                }, gio, phut, true);

                timePickerDialog6.show();
                break;

            // nút thêm địa điểm
            case R.id.btnThemDiaDiem:
                ThemDiaDiem();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.spinDanhMucCha:
                danhMucDuocChon = danhMucChaList.get(position).getMadanhmuc();

                layDanhMuc(danhMucDuocChon);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rdMoCua:
                hoatdong24h = false;
                khongcothoigianhoatdong = false;
                linearChonGio.setVisibility(View.VISIBLE);
                break;
            case R.id.rdMoCua24h:
                hoatdong24h = true;
                khongcothoigianhoatdong = false;
                linearChonGio.setVisibility(View.GONE);
                break;
            case R.id.rdKhongCoThoiGianHoatDong:
                hoatdong24h = false;
                khongcothoigianhoatdong = true;
                linearChonGio.setVisibility(View.GONE);
                break;

            /// khung thứ 2 đến thứ 6
            case R.id.rdDatGioThu2Thu6:
                hoatdong24hthu2thu6 = false;
                khonghoatdongthu2thu6 = false;
                linearChonGioThu2Thu6.setVisibility(View.VISIBLE);
                break;
            case R.id.rdMoCua24hThu2Thu6:
                hoatdong24hthu2thu6 = true;
                khonghoatdongthu2thu6 = false;
                linearChonGioThu2Thu6.setVisibility(View.GONE);
                break;
            case R.id.rdKhongMoCuaThu2Thu6:
                hoatdong24hthu2thu6 = false;
                khonghoatdongthu2thu6 = true;
                linearChonGioThu2Thu6.setVisibility(View.GONE);
                break;

            /// khung thứ 7
            case R.id.rdDatGioThu7:
                hoatdong24hthu7 = false;
                khonghoatdongthu7 = false;
                linearChonGioThu7.setVisibility(View.VISIBLE);
                break;
            case R.id.rdMoCua24hThu7:
                hoatdong24hthu7 = true;
                khonghoatdongthu7 = false;
                linearChonGioThu7.setVisibility(View.GONE);
                break;
            case R.id.rdKhongMoCuaThu7:
                hoatdong24hthu7 = false;
                khonghoatdongthu7 = true;
                linearChonGioThu7.setVisibility(View.GONE);
                break;

            /// khung chủ nhật
            case R.id.rdDatGioChuNhat:
                hoatdong24hchunhat = false;
                khonghoatdongthu2thu6 = false;
                linearChonGioChuNhat.setVisibility(View.VISIBLE);
                break;
            case R.id.rdMoCua24hChuNhat:
                hoatdong24hchunhat = true;
                khonghoatdongchunhat = false;
                linearChonGioChuNhat.setVisibility(View.GONE);
                break;
            case R.id.rdKhongMoCuaChuNhat:
                hoatdong24hthu2thu6 = false;
                khonghoatdongthu2thu6 = true;
                linearChonGioChuNhat.setVisibility(View.GONE);
                break;
        }
    }
}
