package com.felipe.nossafeira.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.felipe.nossafeira.R;
import com.felipe.nossafeira.adapters.ListItemAdapter;
import com.felipe.nossafeira.database.SqlHelper;
import com.felipe.nossafeira.models.ListItem;

import java.util.List;

public class ListItemsActivity extends AppCompatActivity {

    private Bundle testeExtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_items);

        Bundle extras = getIntent().getExtras();
        testeExtras = extras;

        RecyclerView rvItemsList = findViewById(R.id.rv_items_list);
        Button btnNewItem = findViewById(R.id.btn_add_item);

        if (extras != null) {
            final int listId = (int) extras.get("listId");

            btnNewItem.setOnClickListener(view -> {
                Intent intent = new Intent(this, AddEditItemActivity.class);
                intent.putExtra("type", "add");
                intent.putExtra("listId", listId);
                startActivity(intent);
                finish();
            });

            new Thread(() -> {
                List<ListItem> items = SqlHelper.getInstance(this).getListItemsByListId(listId);
                runOnUiThread(() -> {
                    ListItemAdapter adapter = new ListItemAdapter(this, items);
                    rvItemsList.setLayoutManager(new LinearLayoutManager(this));
                    rvItemsList.setAdapter(adapter);
                });
            }).start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_calc) {
            double total = calcTotal();
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.total, total))
                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss())
                    .create();
            dialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private double calcTotal() {
        double total = 0;
        List<ListItem> items = SqlHelper.getInstance(this).getListItemsByListId((int) testeExtras.get("listId"));

        for (ListItem item : items) {
            total += item.getPrice() * item.getQuantity();
        }

        return total;
    }
}