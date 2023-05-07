package com.example.database;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    //Variables storing data to display for this example
    private final String[] prices = {"50€", "50€", "65€", "2€", "50€", "65€", "2€", "50€", "65€", "2€", "50€", "65€", "2€"};
    private final String[] dates = {"5/5/2025", "6/8/2020", "4/6/2012", "6/6/2022", "6/8/2020", "4/6/2012", "6/6/2022", "6/8/2020", "4/6/2012", "6/6/2022"};
    private final String[] names = {"ego", "esy", "aytos", "emeis", "esy", "aytos", "emeis", "esy", "aytos", "emeis"};

    //Class that holds the items to be displayed (Views in card_layout)
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemPrice;
        TextView itemDate;
        TextView itemName;

        public ViewHolder(View itemView) {
            super(itemView);
            itemPrice = itemView.findViewById(R.id.item_price);
            itemDate = itemView.findViewById(R.id.item_date);
            itemName = itemView.findViewById(R.id.item_name);

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
        }
    }

    public static void startMyActivity(Context context, Boolean edit) {
        Intent intent = new Intent(context, ViewReceiptScreen.class);
        if (edit) {
            intent.putExtra("editBoolean", true);
        }
        context.startActivity(intent);
    }

    //Methods that must be implemented for a RecyclerView.Adapter
    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
        holder.itemPrice.setText(prices[position]);
        holder.itemDate.setText(dates[position]);
        holder.itemName.setText(names[position]);
    }

    @Override
    public int getItemCount() {
        return names.length;
    }
}

