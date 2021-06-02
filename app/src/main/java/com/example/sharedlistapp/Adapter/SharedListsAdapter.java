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

public class SharedListsAdapter extends RecyclerView.Adapter<SharedListsAdapter.SharedListItemsViewHolder> {

    public interface SharedListsCallbacks {
         void sharedListItemClicked(String sharedListId, String sharedListTitle);
    }


    private Context context;
    private List<MyLists> userLists;

    public SharedListsAdapter(Context context, List<MyLists> userLists) {
        this.context = context;
        this.userLists = userLists;
    }

    @NonNull
    @Override
    public SharedListItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_layout, parent, false);
        return new SharedListItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SharedListItemsViewHolder holder, int position) {
        holder.listCategory.setText(userLists.get(position).getListCategory());
        holder.listTitle.setText(userLists.get(position).getListName());

    }

    @Override
    public int getItemCount() {
        return userLists.size();
    }


    public class SharedListItemsViewHolder extends RecyclerView.ViewHolder {

        public TextView listTitle;
        public TextView listCategory;

        public SharedListItemsViewHolder(@NonNull View itemView) {
            super(itemView);
            listTitle = itemView.findViewById(R.id.listTitle);
            listCategory = itemView.findViewById(R.id.listCategory);

        }
    }


}
