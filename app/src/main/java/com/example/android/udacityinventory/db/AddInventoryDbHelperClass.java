package com.example.android.udacityinventory.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by fuguBook on 16/7/16.
 */
public class AddInventoryDbHelperClass extends SQLiteOpenHelper {
    public AddInventoryDbHelperClass (Context context) {
        super(context, AddInventoryDbContractClass.DB_NAME, null, AddInventoryDbContractClass.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableString = "CREATE TABLE " + AddInventoryDbContractClass.inventoryEntry.TABLE_NAME + " ( " +
                AddInventoryDbContractClass.inventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                AddInventoryDbContractClass.inventoryEntry.COL_INVENTORY_PRODNAME + " TEXT NOT NULL, " +
                AddInventoryDbContractClass.inventoryEntry.COL_INVENTORY_PRICE + " TEXT NOT NULL, " +
                AddInventoryDbContractClass.inventoryEntry.COL_INVENTORY_QTY + " TEXT NOT NULL, " +
                AddInventoryDbContractClass.inventoryEntry.COL_INVENTORY_IMG + " TEXT NOT NULL);";
        db.execSQL(createTableString);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF IT EXISTS " + AddInventoryDbContractClass.inventoryEntry.TABLE_NAME);
        onCreate(db);
    }
}
