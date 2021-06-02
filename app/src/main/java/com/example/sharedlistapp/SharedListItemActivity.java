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

public class SharedListItemActivity extends AppCompatActivity {

    private TextView listTitleTv;
    private RecyclerView sharedListItemsRecyclerView;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private String listID;
    private List<MyListItem> listItems;
    private com.google.android.material.floatingactionbutton.FloatingActionButton listItemFAB;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_list_item);

        listID = getIntent().getStringExtra(SharedListFragment.SHARED_TITLE);
        listItems = new ArrayList<>();

        listTitleTv = findViewById(R.id.SharedListTitleTV);
        sharedListItemsRecyclerView = findViewById(R.id.sharedListItemRecyclerView);

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(sharedListItemsRecyclerView);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        listItemFAB = findViewById(R.id.sharedAddNewItemFab);

        databaseReference = FirebaseDatabase.getInstance().getReference("SharedLists").child(listID)
                .child("ListDetails").child("ListItems");



        listTitleTv.setText(getIntent().getStringExtra(SharedListFragment.SHARED_TITLE));


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listItems.clear();
                for (DataSnapshot element: snapshot.getChildren()) {
                    MyListItem item = element.getValue(MyListItem.class);
                    listItems.add(item);
                    Log.i("listitemactivity", element.getKey());
                }

                MyListsItemsAdapter listsItemsAdapter = new MyListsItemsAdapter(SharedListItemActivity.this, listItems);
                listsItemsAdapter.listID = listID;
                sharedListItemsRecyclerView.setLayoutManager(new LinearLayoutManager(SharedListItemActivity.this));
                sharedListItemsRecyclerView.setAdapter(listsItemsAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listItemFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(SharedListItemActivity.this);

                View listDialog = LayoutInflater.from(SharedListItemActivity.this).inflate(R.layout.new_list_alert_dialog, null);
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

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("SharedLists").child(listID)
                                .child("ListDetails").child("ListItems");
                        reference.push().setValue(newListItem);

                        Toast.makeText(SharedListItemActivity.this, itemTitle.getText().toString() + itemGenre.getText().toString(), Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        Toast.makeText(SharedListItemActivity.this, "cancel clicked", Toast.LENGTH_SHORT).show();


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
            icon = ContextCompat.getDrawable(SharedListItemActivity.this,
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
           // DatabaseReference reference = FirebaseDatabase.getInstance().getReference("SharedLists").child(f)
                //    .child("MyLists").child(listID).child("ListItems");

        }
    };








}