package com.example.sharedlistapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharedlistapp.Model.MyLists;
import com.example.sharedlistapp.R;

import java.util.List;

public class MyListsAdapter extends RecyclerView.Adapter<MyListsAdapter.ListViewHolder> {

    private List<MyLists> userLists;
    private Context context;


    public MyListsAdapter(List<MyLists> userLists, Context context) {
        this.userLists = userLists;
        this.context = context;
    }


    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        MyLists myLists = userLists.get(position);

        holder.listCategory.setText(myLists.getListCategory());
        holder.listTitle.setText(myLists.getListName());


    }



    @Override
    public int getItemCount() {
        return userLists.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        public TextView listTitle;
        public TextView listCategory;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            listTitle = itemView.findViewById(R.id.listTitle);
            listCategory = itemView.findViewById(R.id.listCategory);
        }
    }
}
