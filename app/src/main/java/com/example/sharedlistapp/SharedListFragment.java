package com.example.sharedlistapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SharedListFragment extends Fragment {
    private RecyclerView sharedListRecyclerView;
    private com.google.android.material.floatingactionbutton.FloatingActionButton sharedListFab;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shared_list, container, false);

        sharedListRecyclerView = view.findViewById(R.id.shared_list_recyclerView);
        sharedListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        sharedListFab = view.findViewById(R.id.addNewSharedListfab);

        sharedListFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CreateNewSharedListActivity.class));
            }
        });



        return view;
    }
}