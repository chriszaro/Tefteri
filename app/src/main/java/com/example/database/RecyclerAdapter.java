package com.example.database;

import android.app.Activity;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    //Variables storing data to display for this example
    private final ArrayList<String> prices;
    private final ArrayList<String> dates;
    private final ArrayList<String> names;
    private final ArrayList<String> id;

    private final MyDBHandler dbHandler;

    private final Activity activity;

    boolean monthly;

    /**
     *
     * @param activity the activity from which we are creating the adapter, either MainActivity or ViewByMonthScreen
     * @param monthly boolean
     */
    public RecyclerAdapter(AppCompatActivity activity, boolean monthly, String month, String year) {
        prices = new ArrayList<>();
        dates = new ArrayList<>();
        names = new ArrayList<>();
        id = new ArrayList<>();
        this.monthly = monthly;

        ArrayList<Receipt> toAdd;

        dbHandler = new MyDBHandler(activity, null, null, 1);
        this.activity = activity;

        if(monthly){
            toAdd = dbHandler.fetchReceiptsBasedOnMonthAndYear(month, year);
        }
        else {
            toAdd = dbHandler.fetchAllReceipts();
        }

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
    }

    HashSet<String> selectedCardViews;
    RecyclerAdapter.ViewHolder holder2;

    //Class that holds the items to be displayed (Views in card_layout)
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemPrice;
        TextView itemDate;
        TextView itemName;
        String itemID;
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
                public boolean onLongClick(View view) { //view is the card
                    // Inflate the menu resource file
                    PopupMenu popup = new PopupMenu(view.getContext(), view); //view.getContext() returns the activity that the card is contained within
                    popup.getMenuInflater().inflate(R.menu.card_menu, popup.getMenu());

                    // Handle menu item clicks
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit_card:
                                    // Handle Edit from LongClick menu
                                    startMyActivity(view.getContext(), true, itemID);
                                    return true;
                                case R.id.delete_card:
                                    // Handle Delete from LongClick menu
                                    dbHandler.deleteReceipt(itemID);
                                    if(!monthly){
                                        MainActivity mainActivity = (MainActivity) activity;
                                        mainActivity.refreshAdapter();
                                    }
                                    else{
                                        ViewByMonthScreen viewByMonthScreen = (ViewByMonthScreen) activity;
                                        viewByMonthScreen.refreshAdapter();
                                    }
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
                        selectedCardViews.add(id.get(position));
                        enableTrash();
                    } else {
                        selectedCardViews.remove(id.get(position));
                        if (selectedCardViews.isEmpty()) {
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
        if(!monthly){
            MainActivity temp = (MainActivity) activity;
            Menu menu = temp.getActivityBarMenu();
            MenuItem myMenuItem = menu.findItem(R.id.action_delete);
            myMenuItem.setVisible(true);
        }
        else {
            ViewByMonthScreen temp = (ViewByMonthScreen) activity;
            Menu menu = temp.getActivityBarMenu();
            MenuItem myMenuItem = menu.findItem(R.id.action_delete);
            myMenuItem.setVisible(true);
        }

    }

    //ViewHolder calls this method when all CheckBoxes are unselected.
    public void disableTrash() {
        if(!monthly){
            MainActivity temp = (MainActivity) activity;
            Menu menu = temp.getActivityBarMenu();
            MenuItem myMenuItem = menu.findItem(R.id.action_delete);
            myMenuItem.setVisible(false);
        }
        else {
            ViewByMonthScreen temp = (ViewByMonthScreen) activity;
            Menu menu = temp.getActivityBarMenu();
            MenuItem myMenuItem = menu.findItem(R.id.action_delete);
            myMenuItem.setVisible(false);
        }

    }

    public ArrayList<String> findIDsOfItemsForDeletion() {
        HashSet<String> selectedCardViews = this.getSelectedCardViews();
        ArrayList<String> idsToDelete = new ArrayList<>();
        idsToDelete.addAll(selectedCardViews);
        return idsToDelete;
    }

    //ViewHolder calls this method when a CardView is clicked.
    public void startMyActivity(Context context, Boolean edit, String id) {
        Intent intent = new Intent(context, ReceiptScreen.class);
        if (edit) {
            intent.putExtra("editBoolean", true);
            intent.putExtra("newReceipt", false);
        }
        intent.putExtra("id", id);

        Activity a = (Activity) context;
        a.startActivity(intent);
    }

    //RecyclerView calls this method to associate a ViewHolder with data.
    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
        holder.itemPrice.setText(prices.get(position).replace('.', ',') + '€');
        holder.itemDate.setText(dates.get(position));

        holder.itemName.setText(names.get(position));

        int maxCharacters = 20; // Set the maximum number of characters you want to display

        if (names.get(position).length() > maxCharacters) {
            holder.itemName.setText(names.get(position).subSequence(0, maxCharacters));
        }

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

    public HashSet<String> getSelectedCardViews() {
        return selectedCardViews;
    }
}

