package com.example.android.udacityinventory;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.udacityinventory.db.AddInventoryDbContractClass;
import com.example.android.udacityinventory.db.AddInventoryDbHelperClass;


/**
 * Created by fuguBook on 17/7/16.
 */
public class DetailActivity extends AppCompatActivity {
    private String mID;
    private String mProductName;
    private String mPrice;
    private String mQty;
    private String mImg;

    private String mTempPathString;
    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 50;

    private String mEditedDetailProductName;
    private String mEditedDetailPrice;
    private String mEditedDetailQty;
    private String mEditedDetailImg;

    private AddInventoryDbHelperClass mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detail);
        mHelper = new AddInventoryDbHelperClass(this);

        Uri mimap = Uri.parse("android.resource://com.example.android.udacityinventory/" + R.mipmap.ic_launcher);
        mTempPathString = mimap.toString();

        //dynamically setTextViews
        Intent toDetailActivity = getIntent();
        Bundle extras = toDetailActivity.getExtras();

        if (savedInstanceState == null) {
            if(extras == null) {
                mID = null;
                mProductName = null;
                mPrice = null;
                mQty = null;
                mImg = null;
            } else {
                mID = extras.getString("id");
                mProductName = extras.getString("productName");
                mPrice = extras.getString("price");
                mQty = extras.getString("qty");
                mImg = extras.getString("path");
            }
        } else {
            mID = (String) savedInstanceState.getSerializable("id");
            mProductName = (String) savedInstanceState.getSerializable("productName");
            mPrice = (String) savedInstanceState.getSerializable("price");
            mQty = (String) savedInstanceState.getSerializable("qty");
            mImg = (String) savedInstanceState.getSerializable("path");
        }

        //instantiate layout items
        final TextView list_item_productName = (TextView) findViewById(R.id.list_item_productName);
        final TextView list_item_price = (TextView) findViewById(R.id.list_item_price);
        final TextView list_item_qty = (TextView) findViewById(R.id.list_item_qty);
        final ImageView list_item_imageDetail = (ImageView) findViewById(R.id.list_item_imageDetail);

        //declare values for layout items
        list_item_productName.setText("\n" + mProductName);
        list_item_price.setText("\nPrice: $" + mPrice);
        list_item_qty.setText("\nQuantity: " + mQty);
        mTempPathString = mImg;
        final Bitmap imageBitmap = BitmapFactory.decodeFile(mTempPathString);
        list_item_imageDetail.setImageBitmap(imageBitmap);

        //set Listeners for layout items
        list_item_productName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout editDetailLayout = new LinearLayout(view.getContext());
                editDetailLayout.setOrientation(LinearLayout.VERTICAL);

                final EditText editDetailProductName = new EditText(view.getContext());
                editDetailProductName.setHint(list_item_productName.getText());
                editDetailLayout.addView(editDetailProductName);

                AlertDialog editDialog = new AlertDialog.Builder(view.getContext())
                        .setTitle("Edit Product Name:")
                        .setView(editDetailLayout)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface editDialog, int which) {

                                mProductName = editDetailProductName.getText().toString();
                                mEditedDetailProductName = mProductName;
                                list_item_productName.setText("\n" + mProductName);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                editDialog.show();
            }
        });

        list_item_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout editDetailLayout = new LinearLayout(view.getContext());
                editDetailLayout.setOrientation(LinearLayout.VERTICAL);

                final EditText editDetailPrice = new EditText(view.getContext());
                editDetailPrice.setHint(list_item_price.getText());
                editDetailLayout.addView(editDetailPrice);

                AlertDialog editDialog = new AlertDialog.Builder(view.getContext())
                        .setTitle("Edit Price Details:")
                        .setView(editDetailLayout)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface editDialog, int which) {

                                mPrice = editDetailPrice.getText().toString();
                                mEditedDetailPrice = mPrice;
                                list_item_price.setText("\nPrice: $" + mPrice);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                editDialog.show();
            }
        });

        list_item_qty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout editDetailLayout = new LinearLayout(view.getContext());
                editDetailLayout.setOrientation(LinearLayout.VERTICAL);

                final EditText editDetailQty = new EditText(view.getContext());
                editDetailQty.setHint(list_item_qty.getText());
                editDetailLayout.addView(editDetailQty);

                AlertDialog editDialog = new AlertDialog.Builder(view.getContext())
                        .setTitle("Edit Quantity Details:")
                        .setView(editDetailLayout)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface editDialog, int which) {
                                mQty = editDetailQty.getText().toString();
                                mEditedDetailQty = mQty;
                                list_item_qty.setText("\nQuantity: " + mQty);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                editDialog.show();
            }
        });

        list_item_imageDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activeGallery();
            }
        });

        //instantiate button objects
        Button btn_deleteInventoryItem = (Button) findViewById(R.id.btn_deleteIventoryItem); //delete item button
        Button btn_resupplyInventoryItem = (Button) findViewById(R.id.btn_resupplyIventoryItem); //send email to supplier button
        Button btn_sellInventoryItem = (Button) findViewById(R.id.btn_sellIventoryItem); //reduce qty by 1 button
        Button btn_receivedInventoryItem = (Button) findViewById(R.id.btn_receivedIventoryItem); //increase qty by 1 button -- added
        Button saveEditedDetails = (Button) findViewById(R.id.btn_save_edited_details); //commit edits button

        btn_deleteInventoryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog editDialog = new AlertDialog.Builder(view.getContext())
                        .setTitle("Are you sure you want to Delete?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface editDialog, int which) {
                            InventoryListBuilderClass inventoryListBuilderClass = new InventoryListBuilderClass(mID, mProductName, mPrice, mQty, mImg);
                            SQLiteDatabase db = mHelper.getWritableDatabase();
                            db.delete(AddInventoryDbContractClass.inventoryEntry.TABLE_NAME, AddInventoryDbContractClass.inventoryEntry._ID + " = ?", new String[]{inventoryListBuilderClass.getID()});
                            db.close();
                            returnToMainActivity();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                editDialog.show();
            }
        });

        btn_resupplyInventoryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSendEmail = new Intent(Intent.ACTION_SENDTO);
                intentSendEmail.setType("*/*");
                intentSendEmail.setData(Uri.parse("mailto:"));

                intentSendEmail.putExtra(Intent.EXTRA_EMAIL, "");
                intentSendEmail.putExtra(Intent.EXTRA_SUBJECT, "resupply of: " + mProductName);
                intentSendEmail.putExtra(Intent.EXTRA_TEXT, "I am in need of: \n" +
                        "\t" + mProductName + "\n" +
                        "\tat " + mPrice + " per unit\n" +
                        "\t" + mQty + "\n\n" +
                        "Thanks, \n" +
                        "\t\tToni Montaño");
                if (intentSendEmail.resolveActivity(getPackageManager()) != null) {
                    startActivity(intentSendEmail);
                }
            }
        });

        btn_sellInventoryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGreaterThanZero(mQty) && mQty != null) {
                    Log.d("isGreaterThanZero", "True");
                    int newQty = Integer.parseInt(mQty) - 1;
                    mQty = String.valueOf(newQty);
                    mEditedDetailQty = mQty;
                    list_item_qty.setText("\nQuantity: " + mQty);
                } else {
                    toastMaker();
                }
            }
        });

        btn_receivedInventoryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((Integer.parseInt(mQty) + 1) >= 0 && mQty != null) {
                    Log.d("isGreaterThanZero", "True");
                    int newQty = Integer.parseInt(mQty) + 1;
                    mQty = String.valueOf(newQty);
                    mEditedDetailQty = mQty;
                    list_item_qty.setText("\nQuantity: " + mQty);
                } else {
                    toastMaker();
                }
            }
        });

        saveEditedDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveClick();
            }
        });
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
            mImg = mTempPathString;
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

    private boolean isGreaterThanZero(String string) {
        if (Integer.parseInt(string) - 1 >= 0) {
            return true;
        } else {
            return false;
        }
    }

    private void toastMaker() {
        Context context = getApplicationContext();
        CharSequence toastText = "I'm sorry, that would result in negative inventory.";
        int toastDuration = Toast.LENGTH_SHORT;

        Toast toastReady = Toast.makeText(context, toastText, toastDuration);
        toastReady.show();
    }

    private void onSaveClick() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        InventoryListBuilderClass inventoryListBuilderClass = new InventoryListBuilderClass(mID, mProductName, mPrice, mQty, mImg);

        mEditedDetailProductName = mProductName;
        mEditedDetailPrice = mPrice;
        mEditedDetailQty = mQty;
        mEditedDetailImg = mImg;

        if (mEditedDetailProductName.equals("") || mEditedDetailPrice.equals("") || mEditedDetailQty.equals("") || mEditedDetailImg.equals("")) {
            toastMakerNull();
        } else if (Integer.parseInt(mEditedDetailQty) < 0 || Integer.parseInt(mEditedDetailPrice) < 0) {
            toastMakerInvalidAmt();
        } else {
            contentValues.put(AddInventoryDbContractClass.inventoryEntry.COL_INVENTORY_PRODNAME, mEditedDetailProductName);
            contentValues.put(AddInventoryDbContractClass.inventoryEntry.COL_INVENTORY_PRICE, mEditedDetailPrice);
            contentValues.put(AddInventoryDbContractClass.inventoryEntry.COL_INVENTORY_QTY, mEditedDetailQty);
            contentValues.put(AddInventoryDbContractClass.inventoryEntry.COL_INVENTORY_IMG, mEditedDetailImg);
            Log.d("onSaveClick()", "contentValues " + contentValues);

            db.update(AddInventoryDbContractClass.inventoryEntry.TABLE_NAME, contentValues, AddInventoryDbContractClass.inventoryEntry._ID + " = ?", new String[]{inventoryListBuilderClass.getID()});
            db.close();
            returnToMainActivity();
        }
    }

    private void toastMakerNull() {
        Context context = getApplicationContext();
        CharSequence toastText = "I'm sorry, one of your fields == your amount of friends. :(";
        int toastDuration = Toast.LENGTH_LONG;

        Toast toastReady = Toast.makeText(context, toastText, toastDuration);
        toastReady.show();
    }

    private void toastMakerInvalidAmt() {
        Context context = getApplicationContext();
        CharSequence toastText = "Number values must be greater than zero.";
        int toastDuration = Toast.LENGTH_LONG;

        Toast toastReady = Toast.makeText(context, toastText, toastDuration);
        toastReady.show();
    }

    //escape!
    private void returnToMainActivity() {
        Intent returnToInventoryActivity = new Intent(DetailActivity.this, InventoryActivity.class);
        startActivity(returnToInventoryActivity);
    }
}