package com.fashion.binge.fashiondesign;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fashion.binge.fashiondesign.classes.ConnectionManager;
import com.fashion.binge.fashiondesign.classes.FormValidation;
import com.fashion.binge.fashiondesign.classes.OnSwipeTouchListener;
import com.fashion.binge.fashiondesign.classes.SetStatusBarColor;
import com.fashion.binge.fashiondesign.classes.Utils;
import com.fashion.binge.fashiondesign.helper.FacebookConfiguration;
import com.fashion.binge.fashiondesign.interfaces.AccessTokenInfoHolder;
import com.fashion.binge.fashiondesign.interfaces.JsonResponseHolder;
import com.fashion.binge.fashiondesign.interfaces.LoginInfoHolder;
import com.fashion.binge.fashiondesign.json.CreateUser;
import com.fashion.binge.fashiondesign.json.FacebookLogin;
import com.fashion.binge.fashiondesign.json.ForgotPassword;
import com.fashion.binge.fashiondesign.json.GetCartData;
import com.fashion.binge.fashiondesign.json.Login;
import com.fashion.binge.fashiondesign.shared_preference.SharedPrefrenceInfo;
import com.fashion.binge.fashiondesign.sweetalertdialog.SweetAlertDialog;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;
import com.sromku.simple.fb.utils.PictureAttributes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout signUpLayout;
    private LinearLayout signInLayout;
    private LinearLayout topParent;
    private TextView createAccount;
    private EditText firstName, lastName, signupEmail, signupPassword, retypePassword, telephone, postCode;
    private EditText email, password, city;
    private TextView country;
    private Button login, signUp, facebookButton;
    private TextView forgotPassword;
    private SimpleFacebook mSimpleFacebook;
    private CoordinatorLayout coordinatorLayout;
    private String accessToken;
    private String ipAddress;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        if (!Homepage.isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        SetStatusBarColor.setStausBarColor(this);
        initialiseLayout();
        initialiseListeners();
    }

    private void initialiseLayout() {
        signUpLayout = (LinearLayout) findViewById(R.id.sign_up_layout);
        signInLayout = (LinearLayout) findViewById(R.id.sign_in_layout);
        createAccount = (TextView) findViewById(R.id.create_account);
        topParent = (LinearLayout) findViewById(R.id.top_parent);
       /* male = (FrameLayout) findViewById(R.id.male);
        female = (FrameLayout) findViewById(R.id.female);*/
        login = (Button) findViewById(R.id.login);
        signUp = (Button) findViewById(R.id.signUp);
        facebookButton = (Button) findViewById(R.id.facebook_button);
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        signupEmail = (EditText) findViewById(R.id.signup_email);
        signupPassword = (EditText) findViewById(R.id.signup_password);
        retypePassword = (EditText) findViewById(R.id.retype_password);
        telephone = (EditText) findViewById(R.id.telephone);
        postCode = (EditText) findViewById(R.id.post_code);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        city = (EditText) findViewById(R.id.city);
        country = (TextView) findViewById(R.id.country);
        WifiManager wifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
        ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        Log.v("User ip",ipAddress);
        //String locale = this.getResources().getConfiguration().locale.getCountry();
        String localename = this.getResources().getConfiguration().locale.getDisplayCountry();
        if (localename != null){
            country.setText(localename);
        }
        //Log.v("locale",locale);
        //Log.v("localeName",localename);
        forgotPassword = (TextView) findViewById(R.id.forgot_password);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
    }

    private void initialiseListeners() {
        createAccount.setOnClickListener(this);
        topParent.setOnClickListener(this);
       /* male.setOnClickListener(this);
        female.setOnClickListener(this);*/
        country.setOnClickListener(this);
        login.setOnClickListener(this);
        signUp.setOnClickListener(this);
        facebookButton.setOnClickListener(this);
        signUpLayout.setOnClickListener(null);
        forgotPassword.setOnClickListener(this);
        signUpLayout.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeDown() {
                if (signUpLayout.getVisibility() == View.VISIBLE) {
                    hideSignUpAnimationView(signUpLayout);
                }
            }

            @Override
            public void onSwipeLeft() {
            }

            @Override
            public void onSwipeUp() {
            }

            @Override
            public void onSwipeRight() {
            }
        });
    }

    private void setFacebookProfile() {
        SimpleFacebook.setConfiguration(new FacebookConfiguration().getConfiguration());
        mSimpleFacebook = SimpleFacebook.getInstance(this);
        login();
    }

    private void login() {
        mSimpleFacebook.login(new OnLoginListener() {
            @Override
            public void onLogin(String accessToken, List<Permission> list, List<Permission> list1) {
                PictureAttributes attributes = PictureAttributes.createPictureAttributes();
                attributes.setType(PictureAttributes.PictureType.SQUARE);
                attributes.setHeight(100);
                attributes.setHeight(100);
                Profile.Properties properties = new Profile.Properties.Builder()
                        .add(Profile.Properties.NAME)
                        .add(Profile.Properties.EMAIL)
                        .add(Profile.Properties.BIRTHDAY)
                        .add(Profile.Properties.PICTURE, attributes)
                        .build();
                MainActivity.this.accessToken = accessToken;
                mSimpleFacebook.getProfile(properties, mProfileListener);
            }

            @Override
            public void onCancel() {
                Log.e("Facebook:: ", "User Cancelled");
            }

            @Override
            public void onException(Throwable throwable) {
                Log.e("Facebook:: ", throwable.toString());
            }

            @Override
            public void onFail(String s) {
                Log.e("Facebook:: ", s);
            }
        });
    }

    OnProfileListener mProfileListener = new OnProfileListener() {
        @Override
        public void onComplete(Profile response) {
            super.onComplete(response);
            sendFacebookDataToserver(response);
        }

        private void sendFacebookDataToserver(final Profile response) {
            String facebookEmail = response.getEmail();
            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#F6705E"));
            sweetAlertDialog.setTitleText("Signing in with Facebook");
            sweetAlertDialog.show();
            new FacebookLogin(MainActivity.this).requestToken(sweetAlertDialog, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), facebookEmail, MainActivity.this.accessToken, new LoginInfoHolder() {
                @Override
                public void loginInfo(String success, final String data) {
                    if (success.equals("401")) {
                        Utils.setTokenInfo(MainActivity.this, sweetAlertDialog, new AccessTokenInfoHolder() {
                            @Override
                            public void setAcessTokenInfo(String accessToken, String expires_in, String token_type) {
                                sweetAlertDialog.dismiss();
                                sendFacebookDataToserver(response);
                            }
                        });
                    } else {
                        if (success.equals("true")) {
                            ///////////////////////////////////////////////////////////////
                            /*call the cart api here to make cart count up to date for the particular user*/
                            new GetCartData(MainActivity.this).getCartDatas(sweetAlertDialog, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), new JsonResponseHolder() {
                                @Override
                                public void onResponse(String status, String message) {
                                    if (status.equals("true")) {
                                        sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                        sweetAlertDialog.setTitleText("Login successful");
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        try {
                                            //parse the get cart api to check if cart count is available
                                            JSONObject cartJsonObject = new JSONObject(message);
                                            if (cartJsonObject.has("total_product_count")) {
                                                String totalProductCount = cartJsonObject.getString("total_product_count");
                                                editor.putString(SharedPrefrenceInfo.CART_COUNT, totalProductCount);
                                                editor.apply();
                                            }
                                            //parse the json response and save the data in shared preference for the future use
                                            JSONObject jsonObject = new JSONObject(data);
                                            String firstName = jsonObject.getString("firstname");
                                            String lastname = jsonObject.getString("lastname");
                                            String email = jsonObject.getString("email");
                                            String telephone = jsonObject.getString("telephone");
                                            editor.putString(SharedPrefrenceInfo.IS_USER_LOGGED_IN, "true");
                                            editor.putString(SharedPrefrenceInfo.FIRST_NAME, firstName);
                                            editor.putString(SharedPrefrenceInfo.LAST_NAME, lastname);
                                            editor.putString(SharedPrefrenceInfo.EMAIL, email);
                                            editor.putString(SharedPrefrenceInfo.TELEPHONE, telephone);
                                            editor.apply();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                sweetAlertDialog.dismissWithAnimation();
                                                Intent homepageIntent = new Intent(MainActivity.this, Homepage.class);
                                                startActivity(homepageIntent);
                                                finish();
                                            }
                                        }, 1000);
                                    }
                                }
                            });
                        } else {
                            sweetAlertDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                            sweetAlertDialog.setTitleText(data);
                        }
                    }

                }
            });
        }

        @Override
        public void onThinking() {
            super.onThinking();

        }

        @Override
        public void onException(Throwable throwable) {
            super.onException(throwable);
            Log.i("Facebook:: ", "Profile exception " + throwable.toString());
        }

        @Override
        public void onFail(String reason) {
            super.onFail(reason);
            Log.i("Facebook:: ", "Profile fail " + reason);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSimpleFacebook.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.create_account:
                translateSignUpAnimationView(signUpLayout);
                break;
            case R.id.top_parent:
                if (signUpLayout.getVisibility() == View.VISIBLE) {
                    hideSignUpAnimationView(signUpLayout);
                }
                break;
            case R.id.male:
               /* gender = "male";
                male.setBackground(ContextCompat.getDrawable(this, R.drawable.fillcircletextview));
                female.setBackground(ContextCompat.getDrawable(this, R.drawable.circletextview));*/
                break;
            case R.id.female:
              /*  gender = "female";
                female.setBackground(ContextCompat.getDrawable(this, R.drawable.fillcircletextview));
                male.setBackground(ContextCompat.getDrawable(this, R.drawable.circletextview));*/
                break;
            case R.id.country:
                new MaterialDialog.Builder(this)
                        .title(R.string.country)
                        .items(R.array.country_array)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                country.setText(text);
                                return true;
                            }
                        }).show();
                break;
            case R.id.login:
                onLogin();
                break;
            case R.id.signUp:
                signUp();
                break;
            case R.id.facebook_button:
                setFacebookProfile();
                break;
            case R.id.forgot_password:
                forgotPassword();
                break;
        }
    }

    private void onLogin() {
        if (new FormValidation().loginFormValidation(coordinatorLayout, email, password)) {
            if (new ConnectionManager(this).isConnectedToInternet()) {
                final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                if (sharedPreferences.getString(SharedPrefrenceInfo.IS_USER_LOGGED_IN, "false").equals("false")) {
                    final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#F6705E"));
                    pDialog.setTitleText("Signing in");
                    pDialog.setCancelable(true);
                    pDialog.show();
                    new Login(this).requestToken(pDialog,sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), email.getText().toString(), password.getText().toString(), new LoginInfoHolder() {
                        @Override
                        public void loginInfo(String success, final String data) {
                            if (success.equals("401")) {
                                pDialog.dismiss();
                                Utils.setTokenInfo(MainActivity.this, pDialog,ipAddress, new AccessTokenInfoHolder() {
                                    @Override
                                    public void setAcessTokenInfo(String accessToken, String expires_in, String token_type) {
                                        onLogin();
                                    }
                                });
                            } else {
                                if (success.equals("true")) {
                                    ///////////////////////////////////////////////////////////////
                                    /*call the cart api here to make cart count up to date for the particular user*/
                                    new GetCartData(MainActivity.this).getCartDatas(pDialog, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), new JsonResponseHolder() {
                                        @Override
                                        public void onResponse(String status, String message) {
                                            if (status.equals("true")) {
                                                pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                                pDialog.setTitleText("Login successful");
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                try {
                                                    //parse the get cart api to check if cart count is available
                                                    JSONObject cartJsonObject = new JSONObject(message);
                                                    if (cartJsonObject.has("total_product_count")) {
                                                        String totalProductCount = cartJsonObject.getString("total_product_count");
                                                        editor.putString(SharedPrefrenceInfo.CART_COUNT, totalProductCount);
                                                        editor.apply();
                                                    }
                                                    //parse the json response and save the data in shared preference for the future use
                                                    JSONObject jsonObject = new JSONObject(data);
                                                    String firstName = jsonObject.getString("firstname");
                                                    String lastname = jsonObject.getString("lastname");
                                                    String email = jsonObject.getString("email");
                                                    String telephone = jsonObject.getString("telephone");
                                                    editor.putString(SharedPrefrenceInfo.IS_USER_LOGGED_IN, "true");
                                                    editor.putString(SharedPrefrenceInfo.FIRST_NAME, firstName);
                                                    editor.putString(SharedPrefrenceInfo.LAST_NAME, lastname);
                                                    editor.putString(SharedPrefrenceInfo.EMAIL, email);
                                                    editor.putString(SharedPrefrenceInfo.TELEPHONE, telephone);
                                                    editor.apply();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        pDialog.dismissWithAnimation();
                                                        Intent homepageIntent = new Intent(MainActivity.this, Homepage.class);
                                                        startActivity(homepageIntent);
                                                        finish();
                                                    }
                                                }, 1000);
                                            }
                                        }
                                    });
                                    //////////////////////////////////////////////////////////////
                                } else {
                                    pDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                                    pDialog.setTitleText("Invalid username and password");
                                }
                            }
                        }
                    });
                }

            } else {
                new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("No internet connection!")
                        .show();
            }
        }
    }

    private void signUp() {
        if (new FormValidation().signUpValidation(coordinatorLayout, firstName, lastName, signupEmail, signupPassword, retypePassword, telephone, postCode, city, country)) {
            if (new ConnectionManager(this).isConnectedToInternet()) {
                final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#F6705E"));
                pDialog.setTitleText("Signing up");
                pDialog.setCancelable(true);
                pDialog.show();
                new CreateUser(this).requestSignUp(pDialog, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), firstName.getText().toString(), lastName.getText().toString(), signupEmail.getText().toString(), signupPassword.getText().toString(), retypePassword.getText().toString(), telephone.getText().toString(), postCode.getText().toString(), city.getText().toString(), country.getText().toString(), new JsonResponseHolder() {
                    @Override
                    public void onResponse(String status, String message) {
                        switch (status) {
                            case "true":
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(SharedPrefrenceInfo.IS_USER_LOGGED_IN, "true");
                                editor.apply();
                                pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                signUpLayout.setVisibility(View.GONE);
                                signInLayout.setVisibility(View.VISIBLE);
                                break;
                            case "false":
                                pDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                                pDialog.setTitleText(message);
                                break;
                            case "401":
                                pDialog.dismiss();
                                Utils.setTokenInfo(MainActivity.this, pDialog, ipAddress, new AccessTokenInfoHolder() {
                                    @Override
                                    public void setAcessTokenInfo(String accessToken, String expires_in, String token_type) {
                                        signUp();
                                    }
                                });
                                break;
                        }
                    }
                });
            } else {
                new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("No internet connection!")
                        .show();
            }
        }
    }

    private void forgotPassword() {
        final Dialog alertDialog = new Dialog(this);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.forgot_password_dialog);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.getWindow().setLayout(550, WindowManager.LayoutParams.WRAP_CONTENT);
        TextView cancel = (TextView) alertDialog.findViewById(R.id.cancel);
        TextView reset = (TextView) alertDialog.findViewById(R.id.reset);
        final EditText email = (EditText) alertDialog.findViewById(R.id.email);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                final SweetAlertDialog pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#F6705E"));
                pDialog.setTitleText("Loading....");
                pDialog.setCancelable(true);
                pDialog.show();
                new ForgotPassword(MainActivity.this).requestForgotPassword(pDialog, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), email.getText().toString(), new JsonResponseHolder() {
                    @Override
                    public void onResponse(String status, String message) {
                        if (status.equals("401")) {
                            pDialog.dismiss();
                            alertDialog.dismiss();
                            Utils.setTokenInfo(MainActivity.this, pDialog, new AccessTokenInfoHolder() {
                                @Override
                                public void setAcessTokenInfo(String accessToken, String expires_in, String token_type) {
                                    forgotPassword();
                                }
                            });
                        } else {
                            if (status.equals("true")) {
                                pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                pDialog.setTitleText("Please check your email");
                                alertDialog.dismiss();
                            } else {
                                pDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                                pDialog.setTitleText(message.substring(1, message.length() - 1));
                            }
                        }
                    }
                });
            }
        });
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (signUpLayout.getVisibility() == View.VISIBLE) {
            hideSignUpAnimationView(signUpLayout);
        } else {
            super.onBackPressed();
        }
    }

    private void translateSignUpAnimationView(final LinearLayout linearLayout) {
        TranslateAnimation anim = new TranslateAnimation(0, 0, 700, 0);
        anim.setDuration(300);
        anim.setFillAfter(true);
        linearLayout.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                signInLayout.setVisibility(View.INVISIBLE);
                signUpLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                linearLayout.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void hideSignUpAnimationView(final LinearLayout linearLayout) {
        TranslateAnimation anim = new TranslateAnimation(0, 0, 0, 1900);
        anim.setDuration(300);
        anim.setFillAfter(true);
        linearLayout.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                signInLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                linearLayout.clearAnimation();
                if (signUpLayout.getVisibility() == View.VISIBLE) {
                    signUpLayout.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
