package com.example.sharedlistapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.sharedlistapp.Adapter.AddFriendsAdapter;
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

public class NewFriendActivity extends AppCompatActivity {
    private SearchView friendSearchView;
    private List<MyFriend> friendRequests;
    private RecyclerView addFriendsRecyclerView;
    private FirebaseUser firebaseUser;
    private AddFriendsAdapter addFriendsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        friendRequests = new ArrayList<>();
        setContentView(R.layout.activity_new_friend);
        addFriendsRecyclerView = findViewById(R.id.findFriendsRecyclerView);
        addFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        friendSearchView = findViewById(R.id.friendSearchView);
        friendSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("search", query);
                friendRequests.clear();
                databaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot element: snapshot.getChildren()) {
                            MyFriend myFriend = element.getValue(MyFriend.class);
                            if (query.equals(myFriend.getUsername())) {
                                friendRequests.add(myFriend);
                            } else {
                                Toast.makeText(NewFriendActivity.this, "No matching user", Toast.LENGTH_SHORT).show();

                            }
                        }
                        addFriendsAdapter = new AddFriendsAdapter(NewFriendActivity.this, friendRequests);
                        addFriendsRecyclerView.setAdapter(addFriendsAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });





                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                friendRequests.clear();
                addFriendsRecyclerView.setAdapter(new AddFriendsAdapter(NewFriendActivity.this, friendRequests));
                return true;
            }
        });



    }
}