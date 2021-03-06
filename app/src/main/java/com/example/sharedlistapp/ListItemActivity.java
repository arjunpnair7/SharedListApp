package com.example.sharedlistapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharedlistapp.Adapter.MyListsItemsAdapter;
import com.example.sharedlistapp.Model.MyListItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListItemActivity extends AppCompatActivity {

    private TextView listTitleTv;
    private RecyclerView listItemsRecyclerView;
    private RecyclerView completedRecyclerView;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private String listID;
    private List<MyListItem> listItems;
    private List<MyListItem> completedItems;
    private List<MyListItem> incompleteItems;
    private List<String> itemKeys;
    private List<String> completedItemKeys;
    private List<String> incompleteItemKeys;
    private com.google.android.material.floatingactionbutton.FloatingActionButton listItemFAB;

    //To-Do
    //Create a completed and incompleted itemKeys array


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);

        completedItems = new ArrayList<>();
        incompleteItems = new ArrayList<>();

        listID = getIntent().getStringExtra(MyListsFragment.LIST_ID);
        listItems = new ArrayList<>();
        itemKeys = new ArrayList<>();
        completedItemKeys = new ArrayList<>();
        incompleteItemKeys = new ArrayList<>();

        listTitleTv = findViewById(R.id.SharedListTitleTV);
        listItemsRecyclerView = findViewById(R.id.sharedListItemRecyclerView);
        completedRecyclerView = findViewById(R.id.completedRecyclerView);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(listItemsRecyclerView);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        listItemFAB = findViewById(R.id.sharedAddNewItemFab);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid())
                            .child("MyLists").child(listID).child("ListItems");



        listTitleTv.setText(getIntent().getStringExtra(MyListsFragment.LIST_TITLE));


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listItems.clear();
                itemKeys.clear();
                for (DataSnapshot element: snapshot.getChildren()) {
                    MyListItem item = element.getValue(MyListItem.class);
                    listItems.add(item);
                    itemKeys.add(element.getKey());
                    Log.i("listitemactivity", element.getKey());
                }
                sortLists(listItems);

                MyListsItemsAdapter listsItemsAdapter = new MyListsItemsAdapter(ListItemActivity.this, incompleteItems);
                MyListsItemsAdapter completeAdapter = new MyListsItemsAdapter(ListItemActivity.this, completedItems);

                Log.i("count", "length: " + listItems.size() + " vs " + completedItems.size());
                listsItemsAdapter.itemIDs = incompleteItemKeys;
                listsItemsAdapter.listID = listID;

                completeAdapter.itemIDs = completedItemKeys;
                completeAdapter.listID = listID;


                listItemsRecyclerView.setLayoutManager(new LinearLayoutManager(ListItemActivity.this));
                listItemsRecyclerView.setAdapter(listsItemsAdapter);

                completedRecyclerView.setLayoutManager(new LinearLayoutManager(ListItemActivity.this));
                completedRecyclerView.setAdapter(completeAdapter);






            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listItemFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(ListItemActivity.this);

                View listDialog = LayoutInflater.from(ListItemActivity.this).inflate(R.layout.new_list_alert_dialog, null);
                EditText itemTitle = listDialog.findViewById(R.id.listNameET);
                EditText itemGenre = listDialog.findViewById(R.id.listCategoryET);

                itemTitle.setHint("Enter item name");
                itemGenre.setHint("Enter genre");

                builder.setPositiveButton("Add item", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Map<String, Object> newListItem = new HashMap<>();
                        newListItem.put("Genre", itemGenre.getText().toString());
                        newListItem.put("Title", itemTitle.getText().toString());
                        newListItem.put("Date", new Date().toString());
                        newListItem.put("Completed", "False");

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid())
                                    .child("MyLists").child(listID).child("ListItems");
                        reference.push().setValue(newListItem);

                        Toast.makeText(ListItemActivity.this, itemTitle.getText().toString() + itemGenre.getText().toString(), Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        Toast.makeText(ListItemActivity.this, "cancel clicked", Toast.LENGTH_SHORT).show();


                    }
                });
                builder.setView(listDialog);
                builder.show();


            }
        });

    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

        private Drawable icon;
        private ColorDrawable background;


        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX,
                    dY, actionState, isCurrentlyActive);
            icon = ContextCompat.getDrawable(ListItemActivity.this,
                    R.drawable.ic_list_trash);
            background = new ColorDrawable(Color.RED);
            View itemView = viewHolder.itemView;
            int backgroundCornerOffset = 20;
            int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + icon.getIntrinsicHeight();

            if (dX > 0) { // Swiping to the right
                int iconLeft = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
                int iconRight = itemView.getLeft() + iconMargin + 50;
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                background.setBounds(itemView.getLeft(), itemView.getTop(),
                        itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                        itemView.getBottom());
            } else if (dX < 0) { // Swiping to the left
                int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                        itemView.getTop(), itemView.getRight(), itemView.getBottom());
            } else { // view is unSwiped
                background.setBounds(0, 0, 0, 0);
            }


            background.draw(c);
            icon.draw(c);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid())
                    .child("MyLists").child(listID).child("ListItems");
            reference.child(itemKeys.get(viewHolder.getAdapterPosition())).removeValue();

        }
    };

    private void sortLists(List<MyListItem> baseList) {
        //Method takes in a list of all items and will then populate/update
        //the incomplete and complete arraylists
        completedItems.clear();
        incompleteItems.clear();
        incompleteItemKeys.clear();
        completedItemKeys.clear();

        for (int i = 0; i < baseList.size(); i++) {
            if (baseList.get(i).getCompleted().equals("False")) {
                incompleteItems.add(baseList.get(i));
                incompleteItemKeys.add(itemKeys.get(i));
            } else {
                completedItems.add(baseList.get(i));
                completedItemKeys.add(itemKeys.get(i));
            }
        }
    }








}