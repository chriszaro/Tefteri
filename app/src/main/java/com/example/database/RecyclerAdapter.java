package com.example.database;

import android.content.Context;
import android.content.Intent;
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
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    //Variables storing data to display for this example
    private final String[] prices = {"50€", "50€", "65€", "2€", "50€", "65€", "2€", "50€", "65€", "2€", "50€", "65€", "2€"};
    private final String[] dates = {"5/5/2025", "6/8/2020", "4/6/2012", "6/6/2022", "6/8/2020", "4/6/2012", "6/6/2022", "6/8/2020", "4/6/2012", "6/6/2022"};
    private final String[] names = {"ego", "esy", "aytos", "emeis", "esy", "aytos", "emeis", "esy", "aytos", "emeis"};

    static HashSet<Integer> selectedCardViews;

    //Class that holds the items to be displayed (Views in card_layout)
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemPrice;
        TextView itemDate;
        TextView itemName;
        CheckBox checkBox;

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
                                    // Handle Edit menu item click
                                    Toast.makeText(view.getContext(), "edit clicked", Toast.LENGTH_SHORT).show();
                                    startMyActivity(view.getContext(), true);
                                    return true;
                                case R.id.delete_card:
                                    // Handle Delete menu item click
                                    Toast.makeText(view.getContext(), "delete clicked", Toast.LENGTH_SHORT).show();
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
                    startMyActivity(v.getContext(), false);
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
                        Toast.makeText(itemView.getContext(), "checked"+position, Toast.LENGTH_SHORT).show();
                        selectedCardViews.add(position);
                        enableTrash();
                    } else {
                        Toast.makeText(itemView.getContext(), "unchecked"+position, Toast.LENGTH_SHORT).show();
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
    public static void enableTrash(){
        Menu menu = MainActivity.getActivityBarMenu();
        MenuItem myMenuItem = menu.findItem(R.id.action_delete);
        myMenuItem.setEnabled(true);
    }

    //ViewHolder calls this method when all CheckBoxes are unselected.
    public static void disableTrash(){
        Menu menu = MainActivity.getActivityBarMenu();
        MenuItem myMenuItem = menu.findItem(R.id.action_delete);
        myMenuItem.setEnabled(false);
    }

    //ViewHolder calls this method when a CardView is clicked.
    public static void startMyActivity(Context context, Boolean edit) {
        Intent intent = new Intent(context, ViewReceiptScreen.class);
        if (edit) {
            intent.putExtra("editBoolean", true);
        }
        context.startActivity(intent);
    }

    //RecyclerView calls this method to associate a ViewHolder with data.
    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
        holder.itemPrice.setText(prices[position]);
        holder.itemDate.setText(dates[position]);
        holder.itemName.setText(names[position]);
        // Set the tag for the checkbox to its position in the adapter
        holder.checkBox.setTag(position);

    }

    //RecyclerView calls this method to get the size of the dataset.
    @Override
    public int getItemCount() {
        return names.length;
    }
}

