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

import java.util.List;

public class AddFriendsAdapter extends RecyclerView.Adapter<AddFriendsAdapter.AddFriendsViewHolder> {

    private Context context;
    private List<MyFriend> friendRequests;

    public AddFriendsAdapter(Context context, List<MyFriend> friendRequests) {
        this.context = context;
        this.friendRequests = friendRequests;
    }


    @NonNull
    @Override
    public AddFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.add_new_friend, parent, false);
        return new AddFriendsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AddFriendsViewHolder holder, int position) {
        holder.friendUsernameTV.setText(friendRequests.get(position).getUsername());
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
            newFriendFab = itemView.findViewById(R.id.addNewFriendfab);
            friendUsernameTV = itemView.findViewById(R.id.friendUsernameTV);
        }
    }
}
