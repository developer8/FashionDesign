package com.fashion.binge.fashiondesign;import android.content.SharedPreferences;import android.content.pm.ActivityInfo;import android.graphics.Color;import android.os.Bundle;import android.preference.PreferenceManager;import android.support.design.widget.CoordinatorLayout;import android.support.design.widget.Snackbar;import android.support.v7.app.AppCompatActivity;import android.support.v7.widget.Toolbar;import android.view.MenuItem;import android.view.View;import android.widget.EditText;import android.widget.ImageView;import android.widget.TextView;import com.fashion.binge.fashiondesign.classes.SetStatusBarColor;import com.fashion.binge.fashiondesign.classes.Utils;import com.fashion.binge.fashiondesign.interfaces.AccessTokenInfoHolder;import com.fashion.binge.fashiondesign.interfaces.ResponseInfoHolder;import com.fashion.binge.fashiondesign.json.ChangePasswordAccount;import com.fashion.binge.fashiondesign.shared_preference.SharedPrefrenceInfo;import com.fashion.binge.fashiondesign.sweetalertdialog.SweetAlertDialog;public class ChangePassword extends AppCompatActivity {    private Toolbar toolbar;    private TextView toolbarTitle;    private SharedPreferences sharedPreferences;    private EditText newPassword, confirmNewPassword;    private ImageView cart,notification;    private CoordinatorLayout coordinatorLayout;    private TextView cartCount,notificationCount;    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_change_password);        if (!Homepage.isTablet(this)) {            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);        }        initialiseLayout();        setSupportActionBar(toolbar);        //noinspection ConstantConditions        getSupportActionBar().setDisplayHomeAsUpEnabled(true);        SetStatusBarColor.setStausBarColor(this);        toolbarTitle.setText(R.string.change_password);        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);        cartCount.setVisibility(View.GONE);        notification.setVisibility(View.GONE);        notificationCount.setVisibility(View.GONE);        cart.setImageResource(R.mipmap.tick);        cart.setColorFilter(Color.argb(255, 255, 255, 255));        cart.setAdjustViewBounds(true);        cart.setMaxHeight(55);        cart.setPadding(0, 15, 0, 0);        cart.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                changePassword();            }        });    }    private void initialiseLayout() {        toolbar = (Toolbar) findViewById(R.id.toolbar);        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);        newPassword = (EditText) findViewById(R.id.new_password);        confirmNewPassword = (EditText) findViewById(R.id.confirm_password);        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);        cart = (ImageView) findViewById(R.id.cart);        cartCount=(TextView)findViewById(R.id.cart_number);        notification=(ImageView)findViewById(R.id.notification);        notificationCount=(TextView)findViewById(R.id.notification_number);    }    private void changePassword() {        if (newPassword.getText().toString().equals("") || confirmNewPassword.getText().toString().equals("")) {            Snackbar snackbar = Snackbar.make(coordinatorLayout, "All field are mandatory", Snackbar.LENGTH_SHORT);            View view = snackbar.getView();            view.setBackgroundColor(Color.parseColor("#f66d63"));            snackbar.show();        } else if (!newPassword.getText().toString().equals(confirmNewPassword.getText().toString())) {            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Password do not match", Snackbar.LENGTH_SHORT);            View view = snackbar.getView();            view.setBackgroundColor(Color.parseColor("#f66d63"));            snackbar.show();        } else {            final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(ChangePassword.this, SweetAlertDialog.PROGRESS_TYPE);            sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#F6705E"));            sweetAlertDialog.setTitleText("Changing your password");            sweetAlertDialog.show();            new ChangePasswordAccount(ChangePassword.this).changeAccountPassword(sweetAlertDialog, confirmNewPassword.getText().toString(), sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), new ResponseInfoHolder() {                @Override                public void setFollowingInfo(String success, String data) {                    if (success.equals("401")) {                        Utils.setTokenInfo(ChangePassword.this, sweetAlertDialog, new AccessTokenInfoHolder() {                            @Override                            public void setAcessTokenInfo(String accessToken, String expires_in, String token_type) {                                sweetAlertDialog.dismissWithAnimation();                                changePassword();                            }                        });                    } else {                        if (success.equals("true")) {                            sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);                            sweetAlertDialog.setTitleText("Successfully changed password");                        } else {                            sweetAlertDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);                            sweetAlertDialog.setTitleText(data);                        }                    }                }            });        }    }    @Override    public boolean onOptionsItemSelected(MenuItem item) {        int id = item.getItemId();        if (id == android.R.id.home) {            finish();            return true;        }        return super.onOptionsItemSelected(item);    }}