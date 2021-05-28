package com.example.sharedlistapp.Adapter;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharedlistapp.Model.MyListItem;
import com.example.sharedlistapp.R;

import java.util.List;

public class MyListsItemsAdapter extends RecyclerView.Adapter<MyListsItemsAdapter.ListItemViewHolder> {





    private Context context;
    private List<MyListItem> listItems;
    private MyListsAdapter.ListItemCallbacks callbacks;

    public MyListsItemsAdapter(Context context, List<MyListItem> listItems, MyListsAdapter.ListItemCallbacks callbacks) {

        this.context = context;
        this.listItems = listItems;
        this.callbacks = callbacks;

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
        holder.itemDate.setText(listItems.get(position).getDate().toString());

    }



    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ListItemViewHolder extends RecyclerView.ViewHolder {
        public TextView itemTitle;
        public TextView itemGenre;
        public TextView itemDate;

        public ListItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemTitle = itemView.findViewById(R.id.listItemName);
            itemGenre = itemView.findViewById(R.id.listItemGenre);
            itemDate = itemView.findViewById(R.id.listItemDate);
        }
    }
}
