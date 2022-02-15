package com.felipe.nossafeira.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.felipe.nossafeira.R;
import com.felipe.nossafeira.adapters.ShoppingListAdapter;
import com.felipe.nossafeira.database.SqlHelper;
import com.felipe.nossafeira.models.ShoppingList;

import java.util.ArrayList;
import java.util.List;

public class ListManagingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_managing);

        RecyclerView rvShoppingList = findViewById(R.id.rv_shopping_list);

        new Thread(() -> {
            List<ShoppingList> shoppingLists = SqlHelper.getInstance(this).getShoppingLists();
            runOnUiThread(() -> {
                ShoppingListAdapter adapter = new ShoppingListAdapter(shoppingLists, this);
                rvShoppingList.setLayoutManager(new LinearLayoutManager(this));
                rvShoppingList.setAdapter(adapter);
            });
        }).start();
    }
}