package com.example.database;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    //Variables storing data to display for this example
    private ArrayList<String> prices;
    private ArrayList<String> dates;
    private ArrayList<String> names;
    private ArrayList<String> id;

    private MyDBHandler dbHandler;

    private MainActivity mainActivity;

    public RecyclerAdapter(AppCompatActivity activity) {
        prices = new ArrayList<>();
        dates = new ArrayList<>();
        names = new ArrayList<>();
        id = new ArrayList<>();
        mainActivity = (MainActivity) activity;

        dbHandler = new MyDBHandler(activity, null, null, 1);

        ArrayList<Receipt> toAdd = dbHandler.fetchNReceipts(40, 0);

        if (toAdd != null) {
            if (!toAdd.isEmpty()) {
                for (Receipt selectedReceipt : toAdd) {
                    prices.add(String.valueOf(selectedReceipt.get_cost()));
                    dates.add(String.valueOf(selectedReceipt.get_date()));
                    names.add(String.valueOf(selectedReceipt.get_companyName()));
                    id.add(String.valueOf(selectedReceipt.get_ID()));
                }
            }
        }
        mainActivity.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                MyDBHandler handler = new MyDBHandler(activity, null, null, 1);

                ArrayList<Receipt> toAdd = dbHandler.fetchNReceipts(40, layoutManager.findLastVisibleItemPosition()+1);
                if (toAdd != null)
                for (Receipt selectedReceipt : toAdd) {
                    prices.add(String.valueOf(selectedReceipt.get_cost()));
                    dates.add(String.valueOf(selectedReceipt.get_date()));
                    names.add(String.valueOf(selectedReceipt.get_companyName()));
                    id.add(String.valueOf(selectedReceipt.get_ID()));
                }
            }
        });

    }

    HashSet<Integer> selectedCardViews;
    RecyclerAdapter.ViewHolder holder2;

    //Class that holds the items to be displayed (Views in card_layout)
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemPrice;
        TextView itemDate;
        TextView itemName;
        String itemID;
        CheckBox checkBox;
        private int tempCounter = 1;

        public ViewHolder(View itemView) {
            super(itemView);
            itemPrice = itemView.findViewById(R.id.item_price);
            itemDate = itemView.findViewById(R.id.item_date);
            itemName = itemView.findViewById(R.id.item_name);
            checkBox = itemView.findViewById(R.id.checkBox);
            selectedCardViews = new HashSet<>();

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    // Inflate the menu resource file
                    PopupMenu popup = new PopupMenu(view.getContext(), view);
                    popup.getMenuInflater().inflate(R.menu.card_menu, popup.getMenu());

                    // Handle menu item clicks
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit_card:
                                    // Handle Edit from LongClick menu
                                    Toast.makeText(view.getContext(), "edit clicked", Toast.LENGTH_SHORT).show();
                                    startMyActivity(view.getContext(), true, itemID);
                                    return true;
                                case R.id.delete_card:
                                    // Handle Delete from LongClick menu
                                    Toast.makeText(view.getContext(), "delete clicked " + tempCounter, Toast.LENGTH_SHORT).show();
                                    tempCounter++;
//                                    MyDBHandler handler = new MyDBHandler(mainActivity, null, null, 1);
                                    dbHandler.deleteReceipt(itemID);
                                    mainActivity.refreshAdapter();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });

                    // Show the popup menu
                    popup.show();
                    return true;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startMyActivity(v.getContext(), false, itemID);
                }
            });

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Get the position of the checkbox from its tag
                    int position = (int) buttonView.getTag();
                    //If button is checked,
                    if (isChecked) {
                        if (selectedCardViews.isEmpty()) {
                            Toast.makeText(itemView.getContext(), "empty", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(itemView.getContext(), "checked card " + id.get(position) + " at " + position, Toast.LENGTH_SHORT).show();
                        selectedCardViews.add(position);
                        enableTrash();
                    } else {
                        Toast.makeText(itemView.getContext(), "unchecked" + position, Toast.LENGTH_SHORT).show();
                        selectedCardViews.remove(position);
                        if (selectedCardViews.isEmpty()) {
                            Toast.makeText(itemView.getContext(), "empty", Toast.LENGTH_SHORT).show();
                            disableTrash();
                        }
                    }
                }
            });
        }
    }

    //RecyclerView calls this method whenever it needs to create a new ViewHolder.
    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new ViewHolder(v);
    }

    //ViewHolder calls this method when a CheckBox is selected.
    public void enableTrash() {
        Menu menu = mainActivity.getActivityBarMenu();
        MenuItem myMenuItem = menu.findItem(R.id.action_delete);
        myMenuItem.setVisible(true);
    }

    //ViewHolder calls this method when all CheckBoxes are unselected.
    public void disableTrash() {
        Menu menu = mainActivity.getActivityBarMenu();
        MenuItem myMenuItem = menu.findItem(R.id.action_delete);
        myMenuItem.setVisible(false);
    }

    public ArrayList<String> findIDsOfItemsForDeletion() {
        HashSet<Integer> selectedCardViews = this.getSelectedCardViews();
        ArrayList<String> idsToDelete = new ArrayList<>();
        for (Integer selectedCardView : selectedCardViews) {
            idsToDelete.add(this.holder2.itemID);
        }
        Toast.makeText(mainActivity, "delete from RecyclerAdapter", Toast.LENGTH_SHORT).show();
        Log.i("LAZAROS TAGS", idsToDelete.toArray().toString());
        return idsToDelete;
    }

    //ViewHolder calls this method when a CardView is clicked.
    public void startMyActivity(Context context, Boolean edit, String id) {
        Intent intent = new Intent(context, receiptScreen.class);
        if (edit) {
            intent.putExtra("editBoolean", true);
            intent.putExtra("newReceipt", false);
        }
        intent.putExtra("id", id);

        context.startActivity(intent);
    }

    //RecyclerView calls this method to associate a ViewHolder with data.
    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
        holder.itemPrice.setText(prices.get(position));
        holder.itemDate.setText(dates.get(position));
        holder.itemName.setText(names.get(position));
        holder.itemID = id.get(position);
        // Set the tag for the checkbox to its position in the adapter
        holder.checkBox.setTag(position);
        holder2 = holder;
    }

    //RecyclerView calls this method to get the size of the dataset.
    @Override
    public int getItemCount() {
        return names.size();
    }

    public HashSet<Integer> getSelectedCardViews() {
        return selectedCardViews;
    }
}

