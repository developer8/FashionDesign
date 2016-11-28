package com.fashion.binge.fashiondesign.classes;

import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class FormValidation {
    public boolean loginFormValidation(CoordinatorLayout coordinatorLayout,EditText email, EditText password) {
        if (email.getText().toString().length() == 0) {
            prepareCustomSnackbar(coordinatorLayout,"Username field required");
            return false;
        }
        if (password.getText().toString().length() == 0) {
            prepareCustomSnackbar(coordinatorLayout,"Password field required");
            return false;
        }
        return true;
    }

    public boolean signUpValidation(CoordinatorLayout coordinatorLayout, EditText firstName, EditText lastName, EditText signupEmail, EditText signUpPassword, EditText retypePassword, EditText telephone, EditText postCode, EditText city,TextView country) {
        if (firstName.getText().toString().length() == 0) {
            prepareCustomSnackbar(coordinatorLayout,"First Name field required");
            return false;
        }
        if (lastName.getText().toString().length() == 0) {
            prepareCustomSnackbar(coordinatorLayout,"Last Name field required");
            return false;
        }
        if (signupEmail.getText().toString().length() == 0) {
            prepareCustomSnackbar(coordinatorLayout,"Email field required");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(signupEmail.getText().toString()).matches()) {
            prepareCustomSnackbar(coordinatorLayout,"Invalid Email Address");
            return false;
        }
        if (signUpPassword.getText().toString().length() == 0) {
            prepareCustomSnackbar(coordinatorLayout,"Password field required");
            return false;
        }
        if (retypePassword.getText().toString().length() == 0) {
            prepareCustomSnackbar(coordinatorLayout,"Retype password to confirm");
            return false;
        }
        if (!signUpPassword.getText().toString().equals(retypePassword.getText().toString())) {
            prepareCustomSnackbar(coordinatorLayout,"Password mismatch");
            return false;
        }
       /* if (telephone.getText().toString().length() == 0) {
            prepareCustomSnackbar(coordinatorLayout,"Telephone field required");
            return false;
        }*/
       /* if (postCode.getText().toString().length() == 0) {
            prepareCustomSnackbar(coordinatorLayout,"Postcode field required");
            return false;
        }*/
        if (country.getText().toString().length() == 0) {
            prepareCustomSnackbar(coordinatorLayout,"City field required");
            return false;
        }
        return true;
    }

    private void prepareCustomSnackbar(CoordinatorLayout coordinatorLayout, String text) {
        Snackbar snackbar=Snackbar.make(coordinatorLayout,text, Snackbar.LENGTH_SHORT);
        View view = snackbar.getView();
        view.setBackgroundColor(Color.parseColor("#f66d63"));
        snackbar.show();
    }
}
