package com.example.sharedlistapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sharedlistapp.Adapter.SharedListsAdapter;
import com.example.sharedlistapp.Model.MyFriend;
import com.example.sharedlistapp.Model.MyLists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class SharedListFragment extends Fragment implements SharedListsAdapter.SharedListsCallbacks {
    private RecyclerView sharedListRecyclerView;
    private com.google.android.material.floatingactionbutton.FloatingActionButton sharedListFab;
    private ValueEventListener listener1;
    private ValueEventListener listener2;
    private FirebaseUser firebaseUser;
    private List<MyLists> userSharedLists;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shared_list, container, false);

        userSharedLists = new ArrayList<>();

        sharedListRecyclerView = view.findViewById(R.id.shared_list_recyclerView);
        sharedListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        sharedListFab = view.findViewById(R.id.addNewSharedListfab);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        sharedListFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CreateNewSharedListActivity.class));
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("SharedLists");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot element: snapshot.getChildren()) {
                    String key = element.getKey();
                    checkIfUserIsPartOfList(firebaseUser.getEmail(), key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        SharedListsAdapter sharedListsAdapter = new SharedListsAdapter(getContext(), userSharedLists);
        sharedListRecyclerView.setAdapter(sharedListsAdapter);
        return view;
    }

    public void checkIfUserIsPartOfList(String userEmail, String listKey) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("SharedLists").child(listKey).child("Users");
        reference.addValueEventListener(listener1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MyFriend myFriend = dataSnapshot.getValue(MyFriend.class);
                    if (myFriend.getUsername().equals(userEmail)) {
                        DatabaseReference listReference = FirebaseDatabase.getInstance().getReference().child("SharedLists").child(listKey).child("ListDetails");
                        listReference.addValueEventListener(listener2 = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                MyLists myLists = snapshot.getValue(MyLists.class);
                                Log.i("toaster", myLists.getListName());
                                userSharedLists.add(myLists);
                                Log.i("toastersize", userSharedLists.size() + "");
                                SharedListsAdapter sharedListsAdapter = new SharedListsAdapter(getContext(), userSharedLists);
                                sharedListRecyclerView.setAdapter(sharedListsAdapter);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void sharedListItemClicked(String sharedListId, String sharedListTitle) {

    }
}