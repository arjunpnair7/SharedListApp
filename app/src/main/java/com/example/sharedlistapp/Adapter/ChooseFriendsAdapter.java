package com.example.sharedlistapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharedlistapp.Model.MyFriend;
import com.example.sharedlistapp.R;


import java.util.List;

public class ChooseFriendsAdapter extends RecyclerView.Adapter<ChooseFriendsAdapter.ChooseFriendsViewHolder> {
    private List<MyFriend> friendList;
    private List<Boolean> friendCheckedList;
    private Context context;

    public ChooseFriendsAdapter(Context context, List<MyFriend> friendList, List<Boolean> friendCheckedList) {
        this.friendList = friendList;
        this.context = context;
        this.friendCheckedList = friendCheckedList;
        Toast.makeText(context, friendList.size() + ":" + friendCheckedList.size(), Toast.LENGTH_SHORT).show();
    }


    @NonNull
    @Override
    public ChooseFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.choose_friends_item, parent, false);
        return new ChooseFriendsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChooseFriendsViewHolder holder, int position) {
        holder.friendEmailTV.setText(friendList.get(position).getUsername());
        holder.userSelectedFriend.setChecked(false);
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public class ChooseFriendsViewHolder extends RecyclerView.ViewHolder {

        private CheckBox userSelectedFriend;
        private TextView friendEmailTV;

        public ChooseFriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            userSelectedFriend = itemView.findViewById(R.id.chooseFriendBox);
            friendEmailTV = itemView.findViewById(R.id.chooseFriendEmailTV);

            userSelectedFriend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    friendCheckedList.set(getAdapterPosition(), isChecked);
                    Toast.makeText(context, friendList.get(getAdapterPosition()).getUsername() + ":" + friendCheckedList.get(getAdapterPosition()), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
}
