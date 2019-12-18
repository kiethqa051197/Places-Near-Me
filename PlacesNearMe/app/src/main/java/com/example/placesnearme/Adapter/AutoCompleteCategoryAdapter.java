package com.example.placesnearme.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.placesnearme.R;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteCategoryAdapter extends ArrayAdapter {
    private List<String> tukhoaListFull;

    public AutoCompleteCategoryAdapter(@NonNull Context context, @NonNull List<String> tukhoaList) {
        super(context, 0, tukhoaList);

        tukhoaListFull = new ArrayList<>(tukhoaList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return danhmucFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_auofill, parent, false);
        }

        TextView txtTuKhoa = convertView.findViewById(R.id.txtTuKhoa);

        String tukhoa = (String) getItem(position);

        if (convertView != null){
            txtTuKhoa.setText(tukhoa);
        }

        return convertView;
    }

    private Filter danhmucFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<String> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                suggestions.addAll(tukhoaListFull);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (String item : tukhoaListFull){
                    if (item.toLowerCase().contains(filterPattern)){
                        suggestions.add(item);
                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return (CharSequence) resultValue;
        }
    };
}
