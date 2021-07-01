package com.example.sharedlistapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;


public class SharedListFragment extends Fragment implements SharedListsAdapter.SharedListsCallbacks {
    private RecyclerView sharedListRecyclerView;
    private com.google.android.material.floatingactionbutton.FloatingActionButton sharedListFab;
    private ValueEventListener listener1;
    private ValueEventListener listener2;
    private ValueEventListener listener3;
    private ChildEventListener childEventListener;
    private FirebaseUser firebaseUser;
    public static int counter = 0;
    private List<MyLists> userSharedLists;
    private SharedListsAdapter.SharedListsCallbacks callbacks;
    public static final String SHARED_TITLE = "sharedTitle";
    public static final String SHARED_LISTS = "sharedLists";
    public static final String SHARED_CATEGORY = "sharedCategory";
    List<String> listName;
    List<String> listType;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shared_list, container, false);
        callbacks = this;
        counter = 0;

         listName = new ArrayList<>();
         listType = new ArrayList<>();

        userSharedLists = new ArrayList<>();

        sharedListRecyclerView = view.findViewById(R.id.shared_list_recyclerView);
        sharedListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        sharedListFab = view.findViewById(R.id.addNewSharedListfab);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        sharedListFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreateNewSharedListActivity.class);
                String[] listTitles = new String[userSharedLists.size()];
                String[] listCategories = new String[userSharedLists.size()];
                for (int i = 0; i < userSharedLists.size(); i++) {
                    listTitles[i] = userSharedLists.get(i).getListName();
                    listCategories[i] = userSharedLists.get(i).getListCategory();
                }
                intent.putExtra(SHARED_LISTS, listTitles);
                intent.putExtra(SHARED_CATEGORY, listCategories);
                startActivity(intent);
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("SharedLists");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for (DataSnapshot dataSnapshot : snapshot.child("Users").getChildren()) {
                    MyFriend myFriend = dataSnapshot.getValue(MyFriend.class);
                    if (myFriend.getUsername().equals(firebaseUser.getEmail())) {
                        MyLists myLists = snapshot.getValue(MyLists.class);
                        userSharedLists.add(myLists);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };



        reference.addListenerForSingleValueEvent(listener3 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userSharedLists.clear();
                int i = 0;
                for (DataSnapshot element: snapshot.getChildren()) {
                    Log.i("abc", i + "");
                    i++;
                    String key = element.getKey();
                    checkIfUserIsPartOfList(firebaseUser.getEmail(), key);
                }
                reference.removeEventListener(listener3);
                reference.addChildEventListener(childEventListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }

    public void checkIfUserIsPartOfList(String userEmail, String listKey) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("SharedLists").child(listKey).child("Users");
        userSharedLists.clear();



        reference.addListenerForSingleValueEvent(listener1 = new ValueEventListener() {
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

                                userSharedLists.add(myLists);
                                Log.i("toastersize", userSharedLists.size() + "");

                                SharedListsAdapter sharedListsAdapter = new SharedListsAdapter(getContext(), userSharedLists, callbacks);
                                sharedListRecyclerView.setAdapter(sharedListsAdapter);

                                listReference.removeEventListener(listener2);
                                reference.removeEventListener(listener1);

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
        //userSharedLists.clear();
    }

    @Override
    public void sharedListItemClicked(String sharedListTitle) {
        Intent i = new Intent(getContext(), SharedListItemActivity.class);
        i.putExtra(SHARED_TITLE, sharedListTitle);
        startActivity(i);
    }
}