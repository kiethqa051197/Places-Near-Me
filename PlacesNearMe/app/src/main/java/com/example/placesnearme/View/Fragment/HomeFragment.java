package com.example.placesnearme.View.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placesnearme.Adapter.ListItemDanhMucChaAdapter;
import com.example.placesnearme.Model.DanhMucCha;
import com.example.placesnearme.R;
import com.example.placesnearme.View.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public RecyclerView listItemDanhMuc;
    private ListItemDanhMucChaAdapter adapterDanhMucCha;

    private FirebaseFirestore db;

    private RecyclerView.LayoutManager layoutManagerDanhMuc;
    private List<DanhMucCha> danhMucChaList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Home");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        db = FirebaseFirestore.getInstance();

        listItemDanhMuc = view.findViewById(R.id.listDanhMucCha);
        listItemDanhMuc.setHasFixedSize(true);
        layoutManagerDanhMuc = new GridLayoutManager(getContext(), 3);
        listItemDanhMuc.setLayoutManager(layoutManagerDanhMuc);

        loadCategory();
        return view;
    }

    private void loadCategory() {
        danhMucChaList = new ArrayList<>();
        if (danhMucChaList.size() > 0)
            danhMucChaList.clear();

        db.collection("Danh Muc Cha").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    DanhMucCha danhMucCha = new DanhMucCha(doc.getString("madanhmuc"),
                            doc.getString("tendanhmuc"));

                    danhMucChaList.add(danhMucCha);
                }

                adapterDanhMucCha = new ListItemDanhMucChaAdapter(((MainActivity)getActivity()),danhMucChaList);
                listItemDanhMuc.setAdapter(adapterDanhMucCha);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage() + "", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
