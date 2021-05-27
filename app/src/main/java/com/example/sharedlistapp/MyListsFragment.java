package com.example.sharedlistapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sharedlistapp.Adapter.MyListsAdapter;
import com.example.sharedlistapp.Model.MyLists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyListsFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private List<MyLists> myLists;
    private com.google.android.material.floatingactionbutton.FloatingActionButton newListfab;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_lists, container, false);
        recyclerView = view.findViewById(R.id.my_lists_recyclerview);
        newListfab = view.findViewById(R.id.addNewListfab);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.HORIZONTAL));


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myLists = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("MyLists");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myLists.clear();
                for (DataSnapshot element: snapshot.getChildren()) {
                    MyLists list = element.getValue(MyLists.class);
                    Log.i("mylists", list.getListCategory() + list.getListName());
                    myLists.add(list);
                }
                MyListsAdapter myListsAdapter = new MyListsAdapter(myLists, getContext());
                recyclerView.setAdapter(myListsAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        newListfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                View listDialog = LayoutInflater.from(getContext()).inflate(R.layout.new_list_alert_dialog, null);
                EditText listTitle = listDialog.findViewById(R.id.listNameET);
                EditText listCategory = listDialog.findViewById(R.id.listCategoryET);

                builder.setPositiveButton("Add list", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Map<String, Object> newListItem = new HashMap<>();
                        newListItem.put("listCategory", listCategory.getText().toString());
                        newListItem.put("listName", listTitle.getText().toString());

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid())
                                .child("MyLists");
                        databaseReference.push().setValue(newListItem);
                        Toast.makeText(getContext(), listTitle.getText().toString() + listCategory.getText().toString(), Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        Toast.makeText(getContext(), "cancel clicked", Toast.LENGTH_SHORT).show();


                    }
                });
                builder.setView(listDialog);
                builder.show();



            }
        });

        Log.i("mylists", String.valueOf(myLists.size()));

        return view;
    }
}