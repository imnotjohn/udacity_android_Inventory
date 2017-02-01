package com.example.android.udacityinventory;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.udacityinventory.db.AddInventoryDbContractClass;
import com.example.android.udacityinventory.db.AddInventoryDbHelperClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fuguBook on 16/7/16.
 */
public class InventoryArrayAdapterClass extends ArrayAdapter<InventoryListBuilderClass> {

    private int tempQtyHolder;
    private String tempQtyString;
    boolean qtyModified;

    private String mID;
    private String mProductName;
    private String mPrice;
    private String mQty;
    private String mImg;

    private String mTempPathString;

    private AddInventoryDbHelperClass mHelper;

    public InventoryArrayAdapterClass(Activity context, int position, ArrayList<InventoryListBuilderClass> inventoryListBuilderClassArrayList) {
        super(context,0,inventoryListBuilderClassArrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context = this.getContext();
        mHelper = new AddInventoryDbHelperClass(context);

        View listItemView = convertView;

        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_list_item, parent, false);
        }

        final InventoryListBuilderClass currentInventoryListItem = getItem(position);

        final TextView idTextView = (TextView) listItemView.findViewById(R.id.list_item_id);
        idTextView.setText(currentInventoryListItem.toString("id"));
        mID = idTextView.getText().toString();

        final TextView productNameTextView = (TextView) listItemView.findViewById(R.id.list_item_productName);
        productNameTextView.setText(currentInventoryListItem.toString("productName"));
        mProductName = productNameTextView.getText().toString();

        final TextView priceTextView = (TextView) listItemView.findViewById(R.id.list_item_price);
        priceTextView.setText(currentInventoryListItem.toString("price"));
        mPrice = priceTextView.getText().toString();

        final TextView qtyTextView = (TextView) listItemView.findViewById(R.id.list_item_qty);
        qtyTextView.setText(currentInventoryListItem.toString("qty"));
        mQty = qtyTextView.getText().toString();

        Button btn_plus = (Button) listItemView.findViewById(R.id.btn_plus);
        Button btn_minus = (Button) listItemView.findViewById(R.id.btn_minus);

        btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempQtyHolder = Integer.parseInt(qtyTextView.getText().toString());
                tempQtyHolder += 1;
                tempQtyString = String.valueOf(tempQtyHolder);
                qtyTextView.setText(tempQtyString);
                qtyModified = true;
            }
        });

        btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempQtyHolder = Integer.parseInt(qtyTextView.getText().toString());
                if (tempQtyHolder - 1 < 0) {
                    toastMakerInvalidAmt();
                } else {
                    tempQtyHolder -= 1;
                    tempQtyString = String.valueOf(tempQtyHolder);
                    qtyTextView.setText(String.valueOf(tempQtyHolder));
                    qtyModified = true;
                }
            }
        });

        ImageView listItemImage = (ImageView) listItemView.findViewById(R.id.list_item_image);
        String testPath = currentInventoryListItem.getImg();
        Bitmap imageBitmap = BitmapFactory.decodeFile(testPath);
        listItemImage.setImageBitmap(imageBitmap);
        mImg = testPath.toString();

        LinearLayout listViewItem = (LinearLayout) listItemView.findViewById(R.id.list_item);
        listViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Context context = view.getContext();

                    Intent toDetaiLActivity = new Intent (context, DetailActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    toDetaiLActivity.putExtra("id", currentInventoryListItem.toString("id"));
                    toDetaiLActivity.putExtra("productName", currentInventoryListItem.toString("productName"));
                    toDetaiLActivity.putExtra("price", currentInventoryListItem.toString("price"));

                    if (!qtyModified){
                        toDetaiLActivity.putExtra("qty", currentInventoryListItem.toString("qty"));
                    } else {
                        toDetaiLActivity.putExtra("qty", currentInventoryListItem.setQty(tempQtyString));
                    }
                    toDetaiLActivity.putExtra("path", currentInventoryListItem.getImg());

                    context.startActivity(toDetaiLActivity);
                } catch (Exception ex) {
                    Log.d("TAG", "onItemClick Failed: " + ex);
                    ex.printStackTrace();
                }
            }
        });
        return listItemView;
    }
    private void toastMakerInvalidAmt() {
        Context context = this.getContext();
        CharSequence toastText = "Number values must be greater than zero.";
        int toastDuration = Toast.LENGTH_LONG;

        Toast toastReady = Toast.makeText(context, toastText, toastDuration);
        toastReady.show();
    }
}
