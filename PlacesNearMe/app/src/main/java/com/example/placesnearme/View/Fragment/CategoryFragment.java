package com.example.placesnearme.View.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placesnearme.Adapter.ListDanhMucChaAdapter;
import com.example.placesnearme.Model.Firebase.DanhMucCha;
import com.example.placesnearme.Model.Firebase.User;
import com.example.placesnearme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment{

    private TextView txtTenNguoiDung;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private RecyclerView listDanhMucCha;
    private ListDanhMucChaAdapter adapterDanhMucCha;

    private FirebaseFirestore db;

    private RecyclerView.LayoutManager layoutManagerDanhMuc;
    private List<DanhMucCha> danhMucChaList = new ArrayList<>();

    private List<User> users = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        txtTenNguoiDung = view.findViewById(R.id.txttennguoidung);

        db = FirebaseFirestore.getInstance();

        setupFirebaseAuth();

        listDanhMucCha = view.findViewById(R.id.recyclerDanhMuc);
        listDanhMucCha.setHasFixedSize(true);
        layoutManagerDanhMuc = new GridLayoutManager(getContext(), 3);
        listDanhMucCha.setLayoutManager(layoutManagerDanhMuc);

        layDanhMuc();

        return view;
    }

    private void setupFirebaseAuth(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    db.collection("User").whereEqualTo("mauser", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                User userModel = new User(doc.getString("mauser"),
                                        doc.getString("email"),
                                        doc.getString("avatar"),
                                        doc.getString("username"));

                                users.add(userModel);
                            }

                            txtTenNguoiDung.setText(users.get(0).getUsername());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage() + "", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    txtTenNguoiDung.setText("");
                }
            }
        };
    }

    private void layDanhMuc(){
        db.collection("Danh Muc Cha").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    DanhMucCha danhMucCha = new DanhMucCha(doc.getString("madanhmuc"),
                            doc.getString("tendanhmuc"), doc.getString("hinhanh"));

                    danhMucChaList.add(danhMucCha);
                }

                adapterDanhMucCha = new ListDanhMucChaAdapter(danhMucChaList);
                listDanhMucCha.setAdapter(adapterDanhMucCha);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage() + "", Toast.LENGTH_SHORT).show();
            }
        });
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