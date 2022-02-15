package com.felipe.nossafeira.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.felipe.nossafeira.models.ListItem;
import com.felipe.nossafeira.models.ShoppingList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SqlHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "nossa_feira.db";
    private static final int DB_VERSION = 1;
    private static SqlHelper INSTANCE;


    public static SqlHelper getInstance(Context context) {
        if (INSTANCE == null)
            return new SqlHelper(context);
        return INSTANCE;
    }


    private SqlHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE lists (id INTEGER primary key, name TEXT, created_date DATETIME)"
        );
        sqLiteDatabase.execSQL(
                "CREATE TABLE items (id INTEGER primary key, listId INTEGER, name TEXT, price DECIMAL," +
                        " quantity INTEGER, created_date DATETIME, isChecked INTEGER)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d("SQLite", "onUpgrade disparado");
    }

    //SHOPPING LISTS
    public List<ShoppingList> getShoppingLists() {
        List<ShoppingList> registers = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM lists", null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String stringDate = cursor.getString(cursor.getColumnIndexOrThrow("created_date"));

                    registers.add(new ShoppingList(id, name, stringDate));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("SQLite", e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        return registers;
    }

    public int addShoppingList(String name) {
        SQLiteDatabase db = getWritableDatabase();

        int calcId = 0;

        try {
            db.beginTransaction();

            ContentValues values = new ContentValues();
            values.put("name", name);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", new Locale("pt", "BR"));

            String now = sdf.format(new Date());
            values.put("created_date", now);

            calcId = (int) db.insertOrThrow("lists", null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("SQLite", e.getMessage(), e);
        } finally {
            if (db.isOpen())
                db.endTransaction();
        }

        return calcId;
    }

    public boolean deleteItem(String table, int id) {
        SQLiteDatabase db = getWritableDatabase();
        boolean result = false;

        try {
            db.beginTransaction();

            db.delete(table, "id = ?", new String[] {String.valueOf(id)});
            db.setTransactionSuccessful();

            result = true;
        } catch (Exception e) {
            Log.e("SQLite", e.getMessage(), e);
        } finally {
            if (db.isOpen())
                db.endTransaction();
        }

        return result;
    }

    //ITEMS LISTS
    public List<ListItem> getListItemsByListId(int inputListId) {
        List<ListItem> registers = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM items WHERE listId=?", new String[]{String.valueOf(inputListId)});

        try {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    int listId = cursor.getInt(cursor.getColumnIndexOrThrow("listId"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                    int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                    String stringDate = cursor.getString(cursor.getColumnIndexOrThrow("created_date"));
                    int isChecked = cursor.getInt(cursor.getColumnIndexOrThrow("isChecked"));

                    registers.add(new ListItem(id, listId, name, price, quantity, stringDate, isChecked));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("SQLite", e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        return registers;
    }

    public ListItem getListItem(int itemId) {
        SQLiteDatabase db = getReadableDatabase();
        ListItem item = null;

        Cursor cursor = db.rawQuery("SELECT * from items WHERE id=?", new String[]{String.valueOf(itemId)});

        try {
            cursor.moveToNext();
            int listId = cursor.getInt(cursor.getColumnIndexOrThrow("listId"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
            String createdDate = cursor.getString(cursor.getColumnIndexOrThrow("created_date"));
            int isChecked = cursor.getInt(cursor.getColumnIndexOrThrow("isChecked"));

            item = new ListItem(itemId, listId, name, price, quantity, createdDate, isChecked);
        } catch (Exception e) {
            Log.e("SQLite", e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        return item;
    }

    public int addItem(int listId, String name, double price, int quantity) {
        int itemId = 0;

        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            ContentValues values = new ContentValues();
            values.put("listId", listId);
            values.put("name", name);
            values.put("price", price);
            values.put("quantity", quantity);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", new Locale("pt", "BR"));

            String now = sdf.format(new Date());
            values.put("created_date", now);

            itemId = (int) db.insertOrThrow("items", null, values);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("SQLite", e.getMessage(), e);
        } finally {
            if (db.isOpen())
                db.endTransaction();
        }

        return itemId;
    }

    public int updateListItem(ListItem item) {
        SQLiteDatabase db = getWritableDatabase();

        int calcId = 0;

        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();

            values.put("name", item.getName());
            values.put("price", item.getPrice());
            values.put("quantity", item.getQuantity());
            values.put("isChecked", item.getIsChecked());

            calcId = db.update("items", values, "id=?", new String[]{String.valueOf(item.getId())});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("SQLite", e.getMessage(), e);
        } finally {
            if (db.isOpen())
                db.endTransaction();
        }

        return calcId;
    }

    public boolean deleteListItemsByListId(int listId) {
        SQLiteDatabase db = getWritableDatabase();
        boolean result = false;

        try {
            db.beginTransaction();

            db.delete("items", "listId = ?", new String[] {String.valueOf(listId)});
            db.setTransactionSuccessful();

            result = true;
        } catch (Exception e) {
            Log.e("SQLite", e.getMessage(), e);
        } finally {
            if (db.isOpen())
                db.endTransaction();
        }

        return result;
    }
}
