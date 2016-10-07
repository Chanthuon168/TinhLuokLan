package com.hammersmith.tinhluoklan.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hammersmith.tinhluoklan.R;
import com.hammersmith.tinhluoklan.adapter.CategoryAdapter;
import com.hammersmith.tinhluoklan.model.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chan Thuon on 10/7/2016.
 */
public class CategoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private GridLayoutManager layoutManager;
    private List<Category> categories = new ArrayList<>();

    public CategoryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_category, container, false);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(getActivity(), 3);
        categoryAdapter = new CategoryAdapter(getActivity(), categories);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(categoryAdapter);

        return root;
    }
}
