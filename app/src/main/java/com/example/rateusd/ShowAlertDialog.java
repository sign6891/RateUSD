package com.example.rateusd;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class ShowAlertDialog {

    private boolean isUpdate = false;
    Context context;
    String price;

    public ShowAlertDialog(Context context) {
        this.context = context;
    }

    public String addAndEditPrice(String priceTrackingTextView) {


        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        View view = layoutInflaterAndroid.inflate(R.layout.add_price, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(context);
        alertDialogBuilderUserInput.setView(view);

        TextView newPriceTitle = view.findViewById(R.id.newPriceTitle);
        EditText newPriceEditText = view.findViewById(R.id.newPriceEditText);

        newPriceTitle.setText(!isUpdate ? "Add Price" : "Edit Price");

        if (isUpdate && !TextUtils.isEmpty(priceTrackingTextView)) {
            newPriceEditText.setText(priceTrackingTextView);
        }

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(isUpdate ? "Update" : "Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).setNegativeButton(isUpdate ? "Delete" : "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isUpdate) {
                            //priceTrackingTextView.setText("");
                            price = "";
                            isUpdate = false;
                        } else {
                            dialog.cancel();
                        }
                    }
                });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(newPriceEditText.getText().toString())) {
                    Toast.makeText(context, "Enter car name!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }
               // if (!TextUtils.isEmpty(newPriceEditText.getText())) {
                    price = newPriceEditText.getText().toString();
                    isUpdate = true;
                //} else {
                 //  isUpdate = false;
                //}
            }
        });
        return price;
    }
}
