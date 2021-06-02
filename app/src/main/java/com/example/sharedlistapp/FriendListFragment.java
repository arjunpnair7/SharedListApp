package com.example.sharedlistapp;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
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
    private ValueEventListener listener;
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
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(friendListRecyclerView);
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

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

        private Drawable icon;
        private ColorDrawable background;


        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX,
                    dY, actionState, isCurrentlyActive);
            icon = ContextCompat.getDrawable(getContext(),
                    R.drawable.ic_list_trash);
            background = new ColorDrawable(Color.RED);
            View itemView = viewHolder.itemView;
            int backgroundCornerOffset = 20;
            int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + icon.getIntrinsicHeight();

            if (dX > 0) { // Swiping to the right
                int iconLeft = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
                int iconRight = itemView.getLeft() + iconMargin + 50;
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                background.setBounds(itemView.getLeft(), itemView.getTop(),
                        itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                        itemView.getBottom());
            } else if (dX < 0) { // Swiping to the left
                int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                        itemView.getTop(), itemView.getRight(), itemView.getBottom());
            } else { // view is unSwiped
                background.setBounds(0, 0, 0, 0);
            }


            background.draw(c);
            icon.draw(c);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("MyFriends");
            reference.addValueEventListener(listener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot element: snapshot.getChildren()) {
                        MyFriend myFriend = element.getValue(MyFriend.class);
                      //  Log.i("tester", myFriend.getUsername() + ":" + viewHolder.getAdapterPosition();
                        if (myFriend.getUsername().equals(myFriendList.get(viewHolder.getAdapterPosition()).getUsername())) {
                            String key = element.getKey();
                            reference.child(key).removeValue();
                            reference.removeEventListener(listener);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    };
}