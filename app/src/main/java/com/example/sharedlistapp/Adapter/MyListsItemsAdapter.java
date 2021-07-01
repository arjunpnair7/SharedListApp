package com.example.sharedlistapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharedlistapp.Model.MyListItem;
import com.example.sharedlistapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyListsItemsAdapter extends RecyclerView.Adapter<MyListsItemsAdapter.ListItemViewHolder> {





    private Context context;
    private List<MyListItem> listItems;
    public List<String> itemIDs = new ArrayList<>();
    public String listID = "";



    public MyListsItemsAdapter(Context context, List<MyListItem> listItems) {

        this.context = context;
        this.listItems = listItems;

    }


    @NonNull
    @Override
    public ListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.listitem_layout, parent, false);
        return new ListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemViewHolder holder, int position) {

        Log.i("Adapter", listItems.get(position).getTitle());
        holder.itemGenre.setText(listItems.get(position).getGenre());
        holder.itemTitle.setText(listItems.get(position).getTitle());
        if (listItems.get(position).getCompleted() != null) {
            if (listItems.get(position).getCompleted().equals("False")) {
                holder.itemCheckbox.setChecked(false);
            } else {
                holder.itemCheckbox.setChecked(true);
            }
        }


    }


    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ListItemViewHolder extends RecyclerView.ViewHolder {
        public TextView itemTitle;
        public TextView itemGenre;
        private CheckBox itemCheckbox;

        public FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        public ListItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemTitle = itemView.findViewById(R.id.chooseFriendEmailTV);
            itemGenre = itemView.findViewById(R.id.listItemGenre);
            itemCheckbox = itemView.findViewById(R.id.chooseFriendBox);

            itemCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Map<String, Object> updateInfo = new HashMap<>();
                    updateInfo.put("Genre", itemGenre.getText().toString());
                    updateInfo.put("Title", itemTitle.getText().toString());
                    updateInfo.put("Date", new Date().toString());
                    if (isChecked) {
                        updateInfo.put("Completed", "True");
                    } else {
                        updateInfo.put("Completed", "False");
                    }
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("MyLists")
                                                                                .child(listID).child("ListItems").child(itemIDs.get(getAdapterPosition()));
                    reference.updateChildren(updateInfo);
                }
            });


        }
    }
}
