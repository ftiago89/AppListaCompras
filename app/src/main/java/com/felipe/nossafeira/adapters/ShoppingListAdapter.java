package com.felipe.nossafeira.adapters;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.felipe.nossafeira.R;
import com.felipe.nossafeira.activities.ListItemsActivity;
import com.felipe.nossafeira.activities.ListManagingActivity;
import com.felipe.nossafeira.database.SqlHelper;
import com.felipe.nossafeira.models.ShoppingList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder> implements AdapterClickListener{

    private final ListManagingActivity listManagingActivity;
    private final List<ShoppingList> lists;

    public ShoppingListAdapter(List<ShoppingList> lists, ListManagingActivity listManagingActivity) {
        this.lists = lists;
        this.listManagingActivity = listManagingActivity;
    }

    @NonNull
    @Override
    public ShoppingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShoppingListViewHolder(listManagingActivity.getLayoutInflater().inflate(R.layout.shopping_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingListViewHolder holder, int position) {
        holder.bind(this.lists.get(position));
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    @Override
    public void onLongClick(int position) {
        AlertDialog dialog = new AlertDialog.Builder(listManagingActivity)
                .setTitle(R.string.should_delete_list)
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                    new Thread(() -> {
                        boolean isListDeleted = SqlHelper.getInstance(listManagingActivity)
                                .deleteItem("lists", lists.get(position).getId());
                        listManagingActivity.runOnUiThread(() -> {
                            if (isListDeleted) {
                                new Thread(() -> {
                                    boolean isItemsDeleted = SqlHelper.getInstance(listManagingActivity).deleteListItemsByListId(lists.get(position).getId());
                                    listManagingActivity.runOnUiThread(() -> {
                                        if (isItemsDeleted) {
                                            lists.remove(position);
                                            Toast.makeText(listManagingActivity, R.string.shoppintList_deleted, Toast.LENGTH_SHORT).show();
                                            notifyDataSetChanged();
                                        }
                                    });
                                }).start();
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
        ShoppingList list = lists.get(position);

        Intent intent = new Intent(listManagingActivity, ListItemsActivity.class);
        intent.putExtra("name", list.getName());
        intent.putExtra("listId", list.getId());
        listManagingActivity.startActivity(intent);
    }

    protected class ShoppingListViewHolder extends RecyclerView.ViewHolder {

        public ShoppingListViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(ShoppingList list) {
            TextView txtName = itemView.findViewById(R.id.item_txt_name);

            String formattedDate = "";
            try {
                SimpleDateFormat fromStringDate = new SimpleDateFormat("yyyy-MM-dd", new Locale("pt", "BR"));
                Date date = fromStringDate.parse(list.getCreatedDate());
                SimpleDateFormat toStringDate = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));
                formattedDate = toStringDate.format(date);
            } catch (ParseException ignored) {
            }

            txtName.setText(listManagingActivity.getString(R.string.shoppingLists_item, list.getName(), formattedDate));

            itemView.setOnLongClickListener(view -> {
                onLongClick(getAdapterPosition());
                return false;
            });

            itemView.setOnClickListener(view -> {
                onClick(getAdapterPosition());
            });
        }
    }
}


