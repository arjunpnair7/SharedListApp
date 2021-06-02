package com.example.sharedlistapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sharedlistapp.Adapter.ChooseFriendsAdapter;
import com.example.sharedlistapp.Model.MyFriend;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateNewSharedListActivity extends AppCompatActivity {

    private RecyclerView chooseFriendsRecyclerView;
    private DatabaseReference reference;
    private FirebaseUser fUser;
    private ValueEventListener listener;
    private List<MyFriend> friendList;
    private List<Boolean> friendCheckedList;
    private ChooseFriendsAdapter chooseFriendsAdapter;
    private Button createSharedListButton;
    private FirebaseUser firebaseUser;
    private String[] listTitles;
    private String[] listCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_shared_list);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        createSharedListButton = findViewById(R.id.createSharedListButton);
        chooseFriendsRecyclerView = findViewById(R.id.chooseFriendsRecyclerView);
        chooseFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        listTitles = getIntent().getStringArrayExtra(SharedListFragment.SHARED_LISTS);
        listCategories = getIntent().getStringArrayExtra(SharedListFragment.SHARED_CATEGORY);
        friendList = new ArrayList<>();

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        createSharedListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(CreateNewSharedListActivity.this);

                View listDialog = LayoutInflater.from(CreateNewSharedListActivity.this).inflate(R.layout.new_list_alert_dialog, null);
                EditText listTitle = listDialog.findViewById(R.id.listNameET);
                EditText listCategory = listDialog.findViewById(R.id.listCategoryET);

                builder.setPositiveButton("Add list", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        for (int i = 0; i < listCategories.length; i++) {
                            if (listCategory.getText().toString().equals(listCategories[i]) && listTitle.getText().toString().equals(listTitles[i])) {
                                Toast.makeText(CreateNewSharedListActivity.this, "Please create a unique list", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }


                        Map<String, Object> newListItem = new HashMap<>();
                        newListItem.put("listCategory", listCategory.getText().toString());
                        newListItem.put("listName", listTitle.getText().toString());

                        List<MyFriend> finalList = findUsersForNewSharedList(friendList, friendCheckedList);
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("SharedLists").child(listTitle.getText().toString()).child("Users");

                        for (int i = 0; i < finalList.size(); i++) {
                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("username", finalList.get(i).getUsername());
                            reference.push().setValue(userInfo);
                        }
                        Map<String, Object> user = new HashMap<>();
                        user.put("username", firebaseUser.getEmail());
                        reference.push().setValue(user);
                        reference = FirebaseDatabase.getInstance().getReference("SharedLists").child(listTitle.getText().toString());
                        reference.child("ListDetails").setValue(newListItem);
                        startActivity(new Intent(CreateNewSharedListActivity.this, MainActivity.class));


                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(CreateNewSharedListActivity.this, "cancel clicked", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CreateNewSharedListActivity.this, MainActivity.class));
                    }
                });
                builder.setView(listDialog);
                builder.show();

            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid()).child("MyFriends");
        reference.addValueEventListener(listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendList.clear();
                for (DataSnapshot element: snapshot.getChildren()) {
                    MyFriend myFriend = element.getValue(MyFriend.class);
                    friendList.add(myFriend);
                }
                friendCheckedList = new ArrayList<>();
                for (int i = 0; i < friendList.size(); i++) {
                    friendCheckedList.add(false);
                }
                chooseFriendsAdapter = new ChooseFriendsAdapter(CreateNewSharedListActivity.this, friendList, friendCheckedList);
                chooseFriendsRecyclerView.setAdapter(chooseFriendsAdapter);
                reference.removeEventListener(listener);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public List<MyFriend> findUsersForNewSharedList(List<MyFriend> friendList, List<Boolean> friendBoolean) {
        List<MyFriend> usersToBeAdded = new ArrayList<>();
        for (int i = 0; i < friendList.size(); i++) {
            if (friendBoolean.get(i)) {
                usersToBeAdded.add(friendList.get(i));
            }
        }
        return usersToBeAdded;
    }
}