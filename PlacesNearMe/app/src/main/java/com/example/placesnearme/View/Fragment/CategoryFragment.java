package com.example.placesnearme.View.Fragment;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placesnearme.Adapter.ListDanhMucChaAdapter;
import com.example.placesnearme.Common;
import com.example.placesnearme.Model.Firebase.DanhMucCha;
import com.example.placesnearme.Model.Firebase.User;
import com.example.placesnearme.R;
import com.example.placesnearme.View.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class CategoryFragment extends Fragment implements View.OnClickListener{

    private TextView txtTenNguoiDung;
    private EditText txtSearch;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private RecyclerView listDanhMucCha;
    private ListDanhMucChaAdapter adapterDanhMucCha;

    private RecyclerView.LayoutManager layoutManagerDanhMuc;
    private List<DanhMucCha> danhMucChaList = new ArrayList<>();
    private List<User> users = new ArrayList<>();

    private LinearLayout linearTimKiem;

    private SharedPreferences preferences;

    private AlertDialog alertDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        preferences = getActivity().getSharedPreferences(Common.PREF_EDIT, 0);

        txtTenNguoiDung = view.findViewById(R.id.txttennguoidung);
        txtSearch = view.findViewById(R.id.txtSearch);

        alertDialog = new SpotsDialog(getContext());

        setupFirebaseAuth();

        listDanhMucCha = view.findViewById(R.id.recyclerDanhMuc);
        listDanhMucCha.setHasFixedSize(true);
        layoutManagerDanhMuc = new GridLayoutManager(getContext(), 3);
        listDanhMucCha.setLayoutManager(layoutManagerDanhMuc);

        linearTimKiem = view.findViewById(R.id.linearTimKiem);

        layDanhMuc();

        linearTimKiem.setOnClickListener(this);
        txtSearch.setOnClickListener(this);

        return view;
    }

    private void setupFirebaseAuth(){
        users.clear();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    MainActivity.db.collection(Common.USER).whereEqualTo(Common.mauser, user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                User userModel = doc.toObject(User.class);

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
        danhMucChaList.clear();

        alertDialog.show();

        MainActivity.db.collection(Common.DANHMUCCHA).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    DanhMucCha danhMucCha = doc.toObject(DanhMucCha.class);

                    danhMucChaList.add(danhMucCha);
                }

                adapterDanhMucCha = new ListDanhMucChaAdapter(danhMucChaList);
                adapterDanhMucCha.notifyDataSetChanged();
                listDanhMucCha.setAdapter(adapterDanhMucCha);

                alertDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage() + "", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
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

    @Override
    public void onResume() {
        super.onResume();

        setupFirebaseAuth();

        if (preferences.getBoolean(Common.edit, false)){
            click2();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.linearTimKiem:
                click();
                break;
            case R.id.txtSearch:
                click();
                break;
        }
    }

    private void click(){
        final FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.beginTransaction().hide(MainActivity.active).show(MainActivity.fragment2).commit();
        MainActivity.active = MainActivity.fragment2;

        MainActivity.bottomNavigationView.getMenu().findItem(R.id.action_search).setChecked(true);
    }

    private void click2(){
        final FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.beginTransaction().hide(MainActivity.active).show(MainActivity.fragment3).commit();
        MainActivity.active = MainActivity.fragment3;

        MainActivity.bottomNavigationView.getMenu().findItem(R.id.action_add_place).setChecked(true);
    }
}