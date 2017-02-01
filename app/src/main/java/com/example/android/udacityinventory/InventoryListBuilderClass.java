package com.example.android.udacityinventory;

import android.net.Uri;
import android.util.Log;

/**
 * Created by fuguBook on 16/7/16.
 */
public class InventoryListBuilderClass {

    private String mID;
    private String mProductName = "";
    private String mPrice = "";
    private String mQty = "";
    private String mImg = "";

    public InventoryListBuilderClass (String id, String productName, String price, String qty, String img) {
        mID = id;
        mProductName = productName;
        mPrice = price;
        mQty = qty;
        mImg = img;
    }
    public String toString(String string) {
        if (string=="id") {
            return mID;
        } else if (string == "productName") {
            return mProductName;
        } else if (string == "price") {
            return mPrice;
        } else if (string == "qty") {
            return mQty;
        } else if (string.startsWith("android.resource")) {
           //mimap string
           return mImg;
        } else if (string != null) {
           return mImg;
        } else if (string == null) {
           Uri mimap = Uri.parse("android.resource://com.example.android.udacityinventory/" + R.mipmap.ic_launcher); //test
           mImg = mimap.toString();
           Log.i("ILBC", "arrived: " + mImg);
           return mImg;
        }
        return string;
    }
    public String getID() {
        return mID;
    }
    public String getImg() {
        return mImg;
    }

    public String setQty(String string) {
        mQty = string;
        return mQty;
    }
}
