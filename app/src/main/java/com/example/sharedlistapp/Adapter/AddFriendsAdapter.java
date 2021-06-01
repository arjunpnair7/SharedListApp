package com.example.sharedlistapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharedlistapp.Model.MyFriend;
import com.example.sharedlistapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class AddFriendsAdapter extends RecyclerView.Adapter<AddFriendsAdapter.AddFriendsViewHolder> {

    public interface FriendRequestCallback {
        public void sendFriendRequest(String receiverUsername, String userName);
        public void addToOutgoingRequests(String senderUID, String receiverUsername);
    }

    public static void disablefab(com.google.android.material.floatingactionbutton.FloatingActionButton fab) {
        fab.setEnabled(false);
    }


    private FirebaseUser fUser;
    private Context context;
    private List<MyFriend> friendRequests;
    private FriendRequestCallback callback;

    public AddFriendsAdapter(Context context, List<MyFriend> friendRequests, FriendRequestCallback callback) {
        this.context = context;
        this.friendRequests = friendRequests;
        this.callback = callback;
    }


    @NonNull
    @Override
    public AddFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.add_new_friend, parent, false);
        AddFriendsViewHolder holder = new AddFriendsViewHolder(v);
        return new AddFriendsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AddFriendsViewHolder holder, int position) {
        holder.friendUsernameTV.setText(friendRequests.get(position).getUsername());
        //disablefab(holder.newFriendFab);
        Log.i("test", friendRequests.get(position) + "");
    }

    @Override
    public int getItemCount() {
        return friendRequests.size();
    }

    public class AddFriendsViewHolder extends RecyclerView.ViewHolder {
        private com.google.android.material.floatingactionbutton.FloatingActionButton newFriendFab;
        private TextView friendUsernameTV;

        public AddFriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            newFriendFab = itemView.findViewById(R.id.addNewFriendFab);
            friendUsernameTV = itemView.findViewById(R.id.friendRequestTV);
            newFriendFab.setEnabled(true);

            newFriendFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fUser = FirebaseAuth.getInstance().getCurrentUser();
                    callback.sendFriendRequest(fUser.getEmail(), friendUsernameTV.getText().toString());
                    callback.addToOutgoingRequests(fUser.getUid(), friendUsernameTV.getText().toString());


                }
            });


        }


    }
}
