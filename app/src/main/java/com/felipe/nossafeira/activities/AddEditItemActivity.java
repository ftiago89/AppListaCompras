package com.felipe.nossafeira.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.felipe.nossafeira.R;
import com.felipe.nossafeira.database.SqlHelper;
import com.felipe.nossafeira.models.ListItem;

public class AddEditItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);

        Button btnAddItem = findViewById(R.id.btn_add_item);
        EditText editTextName = findViewById(R.id.editText_add_item_name);
        EditText editTextPrice = findViewById(R.id.editText_add_item_price);
        EditText editTextQuantity = findViewById(R.id.editText_add_item_quantity);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.get("type").equals("add")) {
                btnAddItem.setOnClickListener(view -> {
                    new Thread(() -> {

                        String name = editTextName.getText().toString();
                        String price = editTextPrice.getText().toString();
                        String quantity = editTextQuantity.getText().toString();

                        if (name.isEmpty()) {
                            runOnUiThread(() -> {
                                Toast.makeText(this, R.string.name_mandatory, Toast.LENGTH_SHORT).show();
                            });
                            return;
                        }

                        if (price.isEmpty()) {
                            price = "0";
                        }

                        if (quantity.isEmpty()) {
                            quantity = "1";
                        }

                        SqlHelper.getInstance(this).addItem(
                                (Integer) extras.get("listId"),
                                editTextName.getText().toString(),
                                Double.parseDouble(price),
                                Integer.parseInt(quantity)
                        );
                        runOnUiThread(() -> {
                            Intent intent = new Intent(this, ListItemsActivity.class);
                            intent.putExtra("listId", (Integer) extras.get("listId"));
                            startActivity(intent);
                            finish();
                        });
                    }).start();
                });
            } else if (extras.get("type").equals("edit")) {
                new Thread(() -> {
                    ListItem item = SqlHelper.getInstance(this).getListItem((int) extras.get("itemId"));
                    runOnUiThread(() -> {
                        if (item != null) {
                            editTextName.setText(item.getName());
                            editTextQuantity.setText(String.valueOf(item.getQuantity()));

                            btnAddItem.setOnClickListener(view -> {
                                item.setName(editTextName.getText().toString());

                                if (editTextPrice.getText() == null || editTextPrice.getText().toString().isEmpty()) {
                                    item.setPrice(0);
                                } else {
                                    item.setPrice(Double.parseDouble(editTextPrice.getText().toString()));
                                }
                                
                                item.setQuantity(Integer.parseInt(editTextQuantity.getText().toString()));

                                int calcId = SqlHelper.getInstance(this).updateListItem(item);

                                if (calcId > 0) {
                                    Toast.makeText(this, R.string.item_edited, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, R.string.item_edited_error, Toast.LENGTH_SHORT).show();
                                }

                                Intent intent = new Intent(this, ListItemsActivity.class);
                                intent.putExtra("listId", (int) extras.get("listId"));
                                startActivity(intent);
                                finish();
                            });
                        }
                    });
                }).start();
            }

        }
    }
}