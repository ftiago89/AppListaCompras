package com.felipe.nossafeira.adapters;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.felipe.nossafeira.R;
import com.felipe.nossafeira.activities.AddEditItemActivity;
import com.felipe.nossafeira.activities.ListItemsActivity;
import com.felipe.nossafeira.database.SqlHelper;
import com.felipe.nossafeira.models.ListItem;

import java.util.List;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.ListItemViewHolder> implements AdapterClickListener{

    private final ListItemsActivity listItemsActivity;
    private final List<ListItem> items;

    public ListItemAdapter(ListItemsActivity listItemsActivity, List<ListItem> items) {
        this.listItemsActivity = listItemsActivity;
        this.items = items;
    }

    @NonNull
    @Override
    public ListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ListItemViewHolder(listItemsActivity.getLayoutInflater().inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemViewHolder holder, int position) {
        holder.bind(items.get(position), position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onLongClick(int position) {
        AlertDialog dialog = new AlertDialog.Builder(listItemsActivity)
                .setTitle(R.string.should_delete_item)
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                    new Thread(() -> {
                        boolean isDeleted = SqlHelper.getInstance(listItemsActivity).deleteItem("items", items.get(position).getId());
                        listItemsActivity.runOnUiThread(() -> {
                            if (isDeleted) {
                                items.remove(position);
                                notifyDataSetChanged();
                            }
                        });
                    }).start();
                })
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel())
                .create();
        dialog.show();
    }

    @Override
    public void onClick(int position) {
        ListItem item = items.get(position);
        Intent intent = new Intent(listItemsActivity, AddEditItemActivity.class);
        intent.putExtra("type", "edit");
        intent.putExtra("itemId", item.getId());
        intent.putExtra("listId", item.getListId());
        listItemsActivity.startActivity(intent);
        listItemsActivity.finish();
    }

    protected class ListItemViewHolder extends RecyclerView.ViewHolder {

        public ListItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(ListItem item, int position) {
            TextView txtItem = itemView.findViewById(R.id.txt_list_item);
            CheckBox checkBox = itemView.findViewById(R.id.checkbox_list_item);
            LinearLayout layout = itemView.findViewById(R.id.layout_item);

            txtItem.setText(listItemsActivity.getString(R.string.itemList_description, item.getName(), item.getPrice(), item.getQuantity()));

            itemView.setOnLongClickListener(view -> {
                onLongClick(position);
                return false;
            });

            itemView.setOnClickListener(view -> {
                onClick(position);
            });

            if (item.getIsChecked() == 1) {
                layout.setBackgroundColor(itemView.getResources().getColor(R.color.secondaryDarkColor));
                checkBox.setChecked(true);
            } else {
                layout.setBackgroundColor(itemView.getResources().getColor(android.R.color.white));
                checkBox.setChecked(false);
            }

            checkBox.setOnClickListener(view -> {
                if (checkBox.isChecked()) {
                    new Thread(() -> {
                        item.setIsChecked(1);
                        int calcId = SqlHelper.getInstance(listItemsActivity).updateListItem(item);
                        listItemsActivity.runOnUiThread(() -> {
                            if (calcId > 0) {
                                layout.setBackgroundColor(itemView.getResources().getColor(R.color.secondaryDarkColor));
                            }
                        });
                    }).start();
                } else {
                    new Thread(() -> {
                        item.setIsChecked(0);
                        int calcId = SqlHelper.getInstance(listItemsActivity).updateListItem(item);
                        listItemsActivity.runOnUiThread(() -> {
                            if (calcId > 0) {
                                layout.setBackgroundColor(itemView.getResources().getColor(android.R.color.white));
                            }
                        });
                    }).start();
                }
            });
        }
    }

    public List<ListItem> getItems() {
        return items;
    }
}
