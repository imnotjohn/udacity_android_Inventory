package com.example.android.udacityinventory;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.udacityinventory.db.AddInventoryDbContractClass;
import com.example.android.udacityinventory.db.AddInventoryDbHelperClass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class InventoryActivity extends AppCompatActivity {
    private static final String TAG = "InventoryActivity";
    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 50;
    private AddInventoryDbHelperClass mHelper;

    private String mID;
    private String mProductName;
    private String mPrice;
    private String mQty;
    private String mImg;
    private String mTempPathString;

//    private ImageView addImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        mHelper = new AddInventoryDbHelperClass(this);

        Uri mimap = Uri.parse("android.mimap.resource://com.example.android.udacityinventory/" + R.mipmap.ic_launcher);
        mTempPathString = mimap.toString();

        updateUI();
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(InventoryActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(InventoryActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                toastMakerDenied();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(InventoryActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
                return true;
            }
        }
        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("REQ", "approved");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Log.i("REQ", "revoked");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
        }
    }

    private void updateUI() {
        ArrayList<InventoryListBuilderClass> inventoryListBuilderArrayList = new ArrayList<>();
        InventoryArrayAdapterClass inventoryArrayAdapterClass = new InventoryArrayAdapterClass(InventoryActivity.this, 0, inventoryListBuilderArrayList);
        ListView listView = (ListView) findViewById(R.id.list_container);
//        listView.setAdapter(inventoryArrayAdapterClass); //original -- works

        SQLiteDatabase db = mHelper.getReadableDatabase();
        dbIsPopulated(db); //check to see if db is populated

        //update from adjusted value intent
        Intent adjustValue = getIntent();
        Bundle adjustedExtras = adjustValue.getExtras();

        if (adjustedExtras == null) {
            Cursor cursor = db.query(AddInventoryDbContractClass.inventoryEntry.TABLE_NAME,
                    new String[]{
                            AddInventoryDbContractClass.inventoryEntry._ID,
                            AddInventoryDbContractClass.inventoryEntry.COL_INVENTORY_PRODNAME,
                            AddInventoryDbContractClass.inventoryEntry.COL_INVENTORY_PRICE,
                            AddInventoryDbContractClass.inventoryEntry.COL_INVENTORY_QTY,
                            AddInventoryDbContractClass.inventoryEntry.COL_INVENTORY_IMG},
                    null, null, null, null, null);
            while (cursor.moveToNext()) {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    switch (i) {
                        case 0:
                            mID = cursor.getString(i);
                        case 1:
                            mProductName = cursor.getString(i);
                        case 2:
                            mPrice = cursor.getString(i);
                        case 3:
                            mQty = cursor.getString(i);
                        case 4:
                            mImg = cursor.getString(i);
                    }
                }
     inventoryListBuilderArrayList.add(new InventoryListBuilderClass(mID, mProductName, mPrice, mQty, mImg));

            }
            cursor.close();
            db.close();
        } else if (adjustedExtras != null) {
            Log.d(TAG, "Adjusted!");
            SQLiteDatabase dbAdjusted = mHelper.getReadableDatabase();
            ContentValues adjustContentValues = new ContentValues();

            mProductName = adjustedExtras.getString("productName");
            mPrice = adjustedExtras.getString("price");
            mQty = adjustedExtras.getString("qty");
            mImg = adjustedExtras.getString("path");

            adjustContentValues.put(AddInventoryDbContractClass.inventoryEntry.COL_INVENTORY_PRODNAME, mProductName);
            adjustContentValues.put(AddInventoryDbContractClass.inventoryEntry.COL_INVENTORY_PRICE, mPrice);
            adjustContentValues.put(AddInventoryDbContractClass.inventoryEntry.COL_INVENTORY_QTY, mQty);
            adjustContentValues.put(AddInventoryDbContractClass.inventoryEntry.COL_INVENTORY_IMG, mImg);
            dbAdjusted.insert(AddInventoryDbContractClass.DB_NAME, null, adjustContentValues);
        }
        listView.setAdapter(inventoryArrayAdapterClass);
    }

    private void activeGallery() {
        Intent activeGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(activeGalleryIntent, MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE && resultCode == RESULT_OK && null != data) {
                Uri selectedImage = data.getData();
                String picturePath = getRealPathFromURI(selectedImage);
                mTempPathString = picturePath;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_inventory_item, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_inv_item:

                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                final EditText addInventoryFromEditText = new EditText(this);
                addInventoryFromEditText.setHint("Enter Product Name");
                linearLayout.addView(addInventoryFromEditText);

                final EditText addPriceFromEditText = new EditText(this);
                addPriceFromEditText.setHint("Enter Price (ex. 4.50)");
                linearLayout.addView(addPriceFromEditText);

                final EditText addQtyFromEditText = new EditText(this);
                addQtyFromEditText.setHint("Enter Quantity (ex. 5)");
                linearLayout.addView(addQtyFromEditText);

                Button btn_addImage = new Button(this);
                btn_addImage.setText("add an image");
                btn_addImage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                btn_addImage.setOnClickListener(new View.OnClickListener() { //works
                    @Override
                    public void onClick(View view) {
                        activeGallery();
                    }
                });
                linearLayout.addView(btn_addImage);

                final AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Add a new product to your inventory:")
                        .setView(linearLayout)
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Done", null)
                        .create();

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String addedInventoryItem = String.valueOf(addInventoryFromEditText.getText());
                                String addedInventoryPrice = String.valueOf(addPriceFromEditText.getText());
                                String addedInventoryQty = String.valueOf(addQtyFromEditText.getText());

                                if (addedInventoryItem.equals("") || addedInventoryPrice.equals("") || addedInventoryQty.equals("")) {
                                    toastMakerNull();
                                } else {
                                    SQLiteDatabase db = mHelper.getWritableDatabase();
                                    ContentValues contentValues = new ContentValues();

                                    //put to contentValues == assigns values to db
                                    contentValues.put(AddInventoryDbContractClass.inventoryEntry.COL_INVENTORY_PRODNAME, addedInventoryItem);
                                    contentValues.put(AddInventoryDbContractClass.inventoryEntry.COL_INVENTORY_PRICE, addedInventoryPrice);
                                    contentValues.put(AddInventoryDbContractClass.inventoryEntry.COL_INVENTORY_QTY, addedInventoryQty);
                                    //ultraman image: http://vignette1.wikia.nocookie.net/ultra/images/f/f6/UA-Ultraman-v2-11.png/revision/latest?cb=20130920164317
                                    contentValues.put(AddInventoryDbContractClass.inventoryEntry.COL_INVENTORY_IMG, mTempPathString);

                                    Log.d(TAG, "mTempPathString: " + mTempPathString);
                                    //insert to db
                                    db.insertWithOnConflict(AddInventoryDbContractClass.inventoryEntry.TABLE_NAME,
                                            null,
                                            contentValues,
                                            SQLiteDatabase.CONFLICT_REPLACE);
                                    db.close();
                                    updateUI();
                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                });
                dialog.show();
                //check for User Permissions to access gallery
                if (checkPermissions()) {
                    Log.i("checkPermissions", "true");
                } else {
                    Log.i("checkPermissions", "false");
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private void toastMakerDenied() {
        Context context = getApplicationContext();
        CharSequence toastText = "I'm sorry, we're done. You can't give me what I need. I need permissions.";
        int toastDuration = Toast.LENGTH_SHORT;

        Toast toastReady = Toast.makeText(context, toastText, toastDuration);
        toastReady.show();
    }

    private void toastMakerNull() {
        Context context = getApplicationContext();
        CharSequence toastText = "I'm sorry, one of your fields == your amount of friends. :(";
        int toastDuration = Toast.LENGTH_LONG;

        Toast toastReady = Toast.makeText(context, toastText, toastDuration);
        toastReady.show();
    }

    private boolean dbIsPopulated(SQLiteDatabase db) {
        //Checks to see whether or not db is populated
        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM " + AddInventoryDbContractClass.inventoryEntry.TABLE_NAME, null);
        if (cur != null) {
            cur.moveToFirst();                       // Always one row returned.
            if (cur.getInt(0) == 0) {               // Zero count means empty table.
                setContentView(R.layout.activity_empty_list);
                return false;
            }
        }
        cur.close();
        return true;
    }

}