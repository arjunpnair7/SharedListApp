package com.example.sharedlistapp.Adapter;

import android.content.Context;
import android.media.JetPlayer;
import android.provider.ContactsContract;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharedlistapp.Model.MyFriend;
import com.example.sharedlistapp.NewFriendActivity;
import com.example.sharedlistapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.FriendRequestViewHolder> {

    private Context context;
    private List<MyFriend> myFriendList;
    private FirebaseUser firebaseUser;

    public FriendRequestAdapter(Context context, List<MyFriend> myFriendList) {
        this.context = context;
        this.myFriendList = myFriendList;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }


    @NonNull
    @Override
    public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.pending_request_item, parent, false);
        return new FriendRequestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestViewHolder holder, int position) {
        holder.usernameTV.setText(myFriendList.get(position).getUsername());
    }

    @Override
    public int getItemCount() {
        return myFriendList.size();
    }

    public class FriendRequestViewHolder extends RecyclerView.ViewHolder {
        private TextView usernameTV;
        private Button addButton;
        private Button declineButton;
        private String key;
        private String receiverEmail;
        private ValueEventListener listener;
        private ValueEventListener listener2;



        public FriendRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTV = itemView.findViewById(R.id.friendRequestTV);
            addButton = itemView.findViewById(R.id.addButton);
            declineButton = itemView.findViewById(R.id.declineButton);

            declineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("FriendRequests");

                    reference.addValueEventListener(listener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String formattedUsername = "";
                            for (DataSnapshot element: snapshot.getChildren()) {
                                MyFriend myFriend = element.getValue(MyFriend.class);
                                if (myFriend.getUsername().equals(usernameTV.getText().toString())) {
                                    receiverEmail = usernameTV.getText().toString();
                                    formattedUsername = usernameTV.getText().toString().substring(0, usernameTV.getText().toString().indexOf("@"));
                                    formattedUsername = formattedUsername + "Requests";
                                    reference.child(formattedUsername).removeValue();
                                    reference.removeEventListener(listener);

                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });


            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("FriendRequests");

                    reference.addValueEventListener(listener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String formattedUsername = "";
                            for (DataSnapshot element: snapshot.getChildren()) {
                                MyFriend myFriend = element.getValue(MyFriend.class);
                                if (myFriend.getUsername().equals(usernameTV.getText().toString())) {
                                    receiverEmail = usernameTV.getText().toString();
                                     formattedUsername = usernameTV.getText().toString().substring(0, usernameTV.getText().toString().indexOf("@"));
                                    formattedUsername = formattedUsername + "Requests";
                                    reference.child(formattedUsername).removeValue();
                                    reference.removeEventListener(listener);

                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });




                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users");
                    databaseRef.addListenerForSingleValueEvent(listener2 = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot element: snapshot.getChildren()) {
                                MyFriend myFriend = element.getValue(MyFriend.class);
                                if (receiverEmail.equals(myFriend.getUsername())) {
                                    key = element.getKey();
                                    String baseString = firebaseUser.getEmail();
                                    baseString = baseString.substring(0, baseString.indexOf("@"));
                                    databaseRef.child(key).child("OutgoingRequests").child(baseString).removeValue();
                                    databaseRef.removeEventListener(listener2);
                                } else {
                                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                            Map<String, Object> friendUpdate = new HashMap<>();
                            friendUpdate.put("username", receiverEmail);
                            Map<String, Object> friendUpdate2 = new HashMap<>();
                            friendUpdate2.put("username", firebaseUser.getEmail());

                        DatabaseReference friendReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid())
                                                            .child("MyFriends");
                        friendReference.push().setValue(friendUpdate);
                        DatabaseReference friendReference2 = FirebaseDatabase.getInstance().getReference("Users").child(key).child("MyFriends");
                        friendReference2.push().setValue(friendUpdate2);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }


                
            });

           
        }
    }
}
