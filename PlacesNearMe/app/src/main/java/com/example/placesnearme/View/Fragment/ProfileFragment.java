package com.example.placesnearme.View.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.placesnearme.Model.Firebase.User;
import com.example.placesnearme.R;
import com.example.placesnearme.View.LoginActivity;
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
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.text.TextUtils.isEmpty;
import static com.example.placesnearme.Remote.Check.doStringsMatch;

public class ProfileFragment extends Fragment implements View.OnClickListener{

    private CircleImageView imgAva, imgCamera;
    private EditText edTenHienThi, edEmail;
    private TextView txtSua, txtLuu;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    private List<User> users = new ArrayList<>();

    private SharedPreferences prefUser, prefFile;
    private SharedPreferences.Editor editorUser, editorFile;

    private static final int RESULT_LOAD_IMAGE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_profile, container, false);

            db = FirebaseFirestore.getInstance();
            storageReference = FirebaseStorage.getInstance().getReference();

            setupFirebaseAuth();

            prefUser = getActivity().getSharedPreferences("prefUser", 0);
            editorUser = prefUser.edit();

            prefFile = getActivity().getSharedPreferences("prefFile", 0);
            editorFile = prefFile.edit();

            imgAva = view.findViewById(R.id.imgAva);
            imgCamera = view.findViewById(R.id.imgCamera);

            edTenHienThi = view.findViewById(R.id.edTenHienThi);
            edEmail = view.findViewById(R.id.edEmail);

            txtLuu = view.findViewById(R.id.txtLuu);
            txtSua = view.findViewById(R.id.txtSua);

            txtSua.setOnClickListener(this);
            txtLuu.setOnClickListener(this);

            imgCamera.setOnClickListener(this);

            return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.txtSua:
                suaClick();
                break;
            case R.id.txtLuu:
                luuClick();
                break;
            case R.id.imgCamera:
                openGallary();
                break;
        }
    }

    private void openGallary(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK){
            if (data.getData() != null){
                Uri uri = data.getData();
                String fileName = getFileName(uri);

                imgAva.setImageURI(uri);

                upLoadAvatar(uri, fileName);

                editorFile.putString("filename", fileName);
                editorFile.commit();
            }
        }
    }

    private void upLoadAvatar(Uri uri, String fileName){
        StorageReference fileUpload = storageReference.child("Avatar").child(prefUser.getString("uid", "")).child(fileName);
        fileUpload.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getContext(), "DONE", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileName(Uri uri){
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

    private void setupFirebaseAuth(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    String idUser = user.getUid();
                    loadData(idUser);

                    editorUser.putString("uid", idUser);
                    editorUser.commit();
                }
            }
        };
    }

    private void loadData(final String userId){
        db.collection("User").whereEqualTo("mauser", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    User userModel = new User(doc.getString("mauser"),
                            doc.getString("email"),
                            doc.getString("avatar"),
                            doc.getString("username"));

                    users.add(userModel);
                }

                edTenHienThi.setText(users.get(0).getUsername());
                edEmail.setText(users.get(0).getEmail());
                loadAvatar(users.get(0).getAvatar());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage() + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateData(final String idUser, String fileName) {
        DocumentReference user = db.collection("User").document(idUser);
        user.update("username", edTenHienThi.getText().toString());
        user.update("email", edEmail.getText().toString());
        user.update("avatar", fileName)
                .addOnSuccessListener(new OnSuccessListener < Void > () {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Updated Successfully",
                                Toast.LENGTH_SHORT).show();
                    }
                });


        db.collection("User").document(idUser)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        loadData(idUser);
                    }
                });
    }

    private void loadAvatar(String ava){
        StorageReference storageImgProductType = FirebaseStorage.getInstance().getReference().child("Avatar").child(prefUser.getString("uid", ""))
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

    private void suaClick(){
        edTenHienThi.setEnabled(true);

        txtSua.setVisibility(View.GONE);
        txtLuu.setVisibility(View.VISIBLE);

        imgCamera.setVisibility(View.VISIBLE);
    }

    private void luuClick(){
        updateData(prefUser.getString("uid", ""), prefFile.getString("filename", ""));

        edTenHienThi.setEnabled(false);

        txtSua.setVisibility(View.VISIBLE);
        txtLuu.setVisibility(View.GONE);

        imgCamera.setVisibility(View.GONE);
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
}
