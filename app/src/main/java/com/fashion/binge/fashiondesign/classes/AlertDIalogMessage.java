package com.fashion.binge.fashiondesign.classes;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.fashion.binge.fashiondesign.Homepage;
import com.fashion.binge.fashiondesign.sweetalertdialog.SweetAlertDialog;


public class AlertDIalogMessage {
    public SweetAlertDialog showProgressDialog(Context context, String message) {
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#F6705E"));
        pDialog.setTitleText(message);
        pDialog.setCancelable(false);
        pDialog.show();
        return pDialog;
    }

    public SweetAlertDialog showErrorDialog(final Context context, String message) {
        final SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        pDialog.setTitleText(message);
        pDialog.show();
        return pDialog;
    }

    public void showErrorMessage(VolleyError volleyError, Context context) {
        final Activity activity = (Activity) context;
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
        if (!activity.isFinishing()) {
            if (volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError) {
                sweetAlertDialog.setTitleText("Oops");
                sweetAlertDialog.setContentText("Error in connection...");
                sweetAlertDialog.show();
            } else if (volleyError instanceof AuthFailureError) {
                sweetAlertDialog.setTitleText("Oops");
                sweetAlertDialog.setContentText("Error in Authorization...");
                sweetAlertDialog.show();
            } else if (volleyError instanceof ServerError) {
                sweetAlertDialog.setTitleText("Oops");
                sweetAlertDialog.setContentText("Error in Server...");
                sweetAlertDialog.show();
            } else if (volleyError instanceof ParseError) {
                sweetAlertDialog.setTitleText("Oops");
                sweetAlertDialog.setContentText("Error in Parsing...");
                sweetAlertDialog.show();
            }
            if (!(activity instanceof Homepage)) {
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        sweetAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                activity.finish();
                                activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

                            }
                        });
                    }
                });
            }
        }
    }
}