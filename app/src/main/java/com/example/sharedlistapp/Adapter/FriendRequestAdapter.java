package com.example.sharedlistapp.Adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharedlistapp.Model.MyFriend;
import com.example.sharedlistapp.R;

import java.util.List;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.FriendRequestViewHolder> {

    private Context context;
    private List<MyFriend> myFriendList;

    public FriendRequestAdapter(Context context, List<MyFriend> myFriendList) {
        this.context = context;
        this.myFriendList = myFriendList;
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

        public FriendRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTV = itemView.findViewById(R.id.friendRequestTV);
            addButton = itemView.findViewById(R.id.addButton);
            declineButton = itemView.findViewById(R.id.declineButton);

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Add button clicked", Toast.LENGTH_SHORT).show();
                }
            });
            declineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Decline button clicked", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
