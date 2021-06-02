package com.example.sharedlistapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sharedlistapp.Adapter.MyListsAdapter;
import com.example.sharedlistapp.Model.MyLists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyListsFragment extends Fragment implements MyListsAdapter.ListItemCallbacks {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private List<MyLists> myLists;
    private List<String> uniqueIDs;
    private com.google.android.material.floatingactionbutton.FloatingActionButton newListfab;
    private MyListsAdapter.ListItemCallbacks callbacks;
    public final static String LIST_ID = "listID";
    public final static String LIST_TITLE = "listTitle";




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        callbacks = this;
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_lists, container, false);
        recyclerView = view.findViewById(R.id.shared_list_recyclerView);
        newListfab = view.findViewById(R.id.addNewSharedListfab);




        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.HORIZONTAL));
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myLists = new ArrayList<>();
        uniqueIDs = new ArrayList<>();


        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("MyLists");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myLists.clear();
                uniqueIDs.clear();
                for (DataSnapshot element: snapshot.getChildren()) {
                    MyLists list = element.getValue(MyLists.class);
                    Log.i("mylists", list.getListCategory() + list.getListName());
                    uniqueIDs.add(element.getKey());
                    myLists.add(list);
                }
//                Log.i("lists", uniqueIDs.get(0) + "");
                //Log.i("lists", myLists.get(0).getListName() + "");

                MyListsAdapter myListsAdapter = new MyListsAdapter(myLists, getContext(), uniqueIDs, callbacks);
                recyclerView.setAdapter(myListsAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        newListfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                View listDialog = LayoutInflater.from(getContext()).inflate(R.layout.new_list_alert_dialog, null);
                EditText listTitle = listDialog.findViewById(R.id.listNameET);
                EditText listCategory = listDialog.findViewById(R.id.listCategoryET);

                builder.setPositiveButton("Add list", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Map<String, Object> newListItem = new HashMap<>();
                        newListItem.put("listCategory", listCategory.getText().toString());
                        newListItem.put("listName", listTitle.getText().toString());

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid())
                                .child("MyLists");
                        databaseReference.push().setValue(newListItem);
                        Toast.makeText(getContext(), listTitle.getText().toString() + listCategory.getText().toString(), Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        Toast.makeText(getContext(), "cancel clicked", Toast.LENGTH_SHORT).show();


                    }
                });
                builder.setView(listDialog);
                builder.show();





            }
        });

        Log.i("mylists", String.valueOf(myLists.size()));

        return view;
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

        private Drawable icon;
        private ColorDrawable background;


        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX,
                    dY, actionState, isCurrentlyActive);
            icon = ContextCompat.getDrawable(getContext(),
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
                    .child("MyLists");
            reference.child(uniqueIDs.get(viewHolder.getAdapterPosition())).removeValue();

        }
    };


    @Override
    public void ListItemClicked(String listID, String listTitle) {
        Intent i = new Intent(getContext(), ListItemActivity.class);
        i.putExtra(LIST_ID, listID);
        i.putExtra(LIST_TITLE, listTitle);
        startActivity(i);

    }
}