package com.example.sharedlistapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sharedlistapp.Adapter.FriendRequestAdapter;
import com.example.sharedlistapp.Adapter.MyFriendsAdapter;
import com.example.sharedlistapp.Model.MyFriend;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FriendListFragment extends Fragment {

    private RecyclerView friendListRecyclerView;
    private RecyclerView friendRequestRecyclerView;
    private List<MyFriend> myFriendList;
    private List<MyFriend> pendingFriendRequestList;
    private FirebaseUser firebaseUser;
    private com.google.android.material.floatingactionbutton.FloatingActionButton newFriendFab;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friend_list, container, false);
        newFriendFab = view.findViewById(R.id.addNewFriendfab);
        friendListRecyclerView = view.findViewById(R.id.friend_list_recyclerView);
        friendRequestRecyclerView = view.findViewById(R.id.pendingRequestsRecyclerView);
        friendRequestRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        friendListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myFriendList = new ArrayList<>();
        pendingFriendRequestList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("MyFriends");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myFriendList.clear();
                for (DataSnapshot element: snapshot.getChildren()) {
                    MyFriend friend = element.getValue(MyFriend.class);
                    myFriendList.add(friend);
                }
                MyFriendsAdapter friendsAdapter = new MyFriendsAdapter(getContext(), myFriendList);
                friendListRecyclerView.setAdapter(friendsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        newFriendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), NewFriendActivity.class);
                startActivity(i);

            }
        });
        DatabaseReference newReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("FriendRequests");
        newReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pendingFriendRequestList.clear();
                for (DataSnapshot element: snapshot.getChildren()) {
                    MyFriend myFriend = element.getValue(MyFriend.class);
                    pendingFriendRequestList.add(myFriend);
                }
                FriendRequestAdapter friendRequestAdapter = new FriendRequestAdapter(getContext(), pendingFriendRequestList);
                friendRequestRecyclerView.setAdapter(friendRequestAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return view;

    }
}