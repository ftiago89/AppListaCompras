package com.felipe.nossafeira.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.felipe.nossafeira.R;
import com.felipe.nossafeira.database.SqlHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnNewList;
        Button btnManageLists;

        btnNewList = findViewById(R.id.btn_main_newList);
        btnManageLists = findViewById(R.id.btn_main_manageLists);

        btnNewList.setOnClickListener(view -> {
            View dialogView = getLayoutInflater().inflate(R.layout.textview_list_name, null);
            EditText nameEditText = dialogView.findViewById(R.id.editText_add_list_name);

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.txt_add_list)
                    .setView(dialogView)
                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                        if (!validate(nameEditText.getText().toString())) {
                            Toast.makeText(this, R.string.name_validation_error, Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                            return;
                        }

                        new Thread(() -> {
                            int calcId = SqlHelper.getInstance(this).addShoppingList(nameEditText.getText().toString());
                            runOnUiThread(() -> {
                                if (calcId > 0) {
                                    Toast.makeText(this, R.string.list_saved, Toast.LENGTH_SHORT).show();
                                    openListManagingActivity();
                                }
                            });
                        }).start();

                    })
                    .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel())
                    .create();

            dialog.show();
        });

        btnManageLists.setOnClickListener(view -> {
            openListManagingActivity();
        });
    }

    private void openListManagingActivity() {
        Intent intent = new Intent(this, ListManagingActivity.class);
        startActivity(intent);
    }

    private boolean validate(String input) {
        return !input.isEmpty();
    }
}