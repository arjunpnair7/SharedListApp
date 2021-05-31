package com.example.sharedlistapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharedlistapp.Model.MyFriend;
import com.example.sharedlistapp.R;

import java.util.List;

public class MyFriendsAdapter extends RecyclerView.Adapter<MyFriendsAdapter.MyFriendViewHolder> {
    private List<MyFriend> myFriendList;
    private Context context;

    public MyFriendsAdapter(Context context, List<MyFriend> myFriendList) {
        this.context = context;
        this.myFriendList = myFriendList;
    }

    @NonNull
    @Override
    public MyFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_item_layout, parent, false);
        return new MyFriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyFriendViewHolder holder, int position) {
        holder.friendUserNameTV.setText(myFriendList.get(position).getUsername());
    }

    @Override
    public int getItemCount() {
        return myFriendList.size();
    }


    public class MyFriendViewHolder extends RecyclerView.ViewHolder {
        private TextView friendUserNameTV;
        public MyFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            friendUserNameTV = itemView.findViewById(R.id.friendUsernameTV);
        }
    }

}
