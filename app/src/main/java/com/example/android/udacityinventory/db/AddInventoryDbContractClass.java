package com.example.android.udacityinventory.db;

/**
 * Created by fuguBook on 16/7/16.
 */

import android.provider.BaseColumns;

public class AddInventoryDbContractClass {
    public static final String DB_NAME = "com.example.android.udacityinventory.db";
    public static final int DB_VERSION = 1;

    public class inventoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "inventory";

        public static final String COL_INVENTORY_PRODNAME = "productName";

        public static final String COL_INVENTORY_PRICE = "price";

        public static final String COL_INVENTORY_QTY = "quantity";

        public static final String COL_INVENTORY_IMG = "path";
    }
}
