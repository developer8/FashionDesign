package com.fashion.binge.fashiondesign.classes;


public class JSONUrl {
    public static String BASE_URL = "https://binge.sg/index.php?";
    public static final String ACCESS_TOKEN_URL ="route=feed/rest_api/gettoken&grant_type=client_credentials";
    public static final String ACCESS_TOKEN_URL_WITH_IP ="route=feed/rest_api/gettoken&grant_type=client_credentials&ip=";
    public static final String SHOP_URL = "route=feed/rest_api/stores&group=featured";
    public static final String ALL_SHOP_URL ="route=feed/rest_api/stores";
    public static final String LOGIN_URL ="route=rest/login/login";
    public static final String LOG_OUT_URL ="route=rest/logout/logout";
    public static final String SIGN_UP_URL ="route=rest/register/register";
    public static final String FOLLOW_URL ="route=feed/rest_api/follow&id=";
    public static final String UNFOLLOW_URL ="route=feed/rest_api/unfollow&id=";
    public static String IMAGE_BASE_URL = "https://binge.sg/image/";
    public static final String FOLLOWING_URL ="route=feed/rest_api/followlist";
    public static final String GET_SHOP_BY_ID_URL ="route=feed/rest_api/stores&id=";
    public static final String GET_PRODUCT_BY_SHOP ="route=feed/rest_api/products&seller_id=";
    public static final String FORGOT_PASSWORD_URL ="route=rest/forgotten/forgotten";
    public static final String LEVEL_THREE_CATEGORY_URL ="route=feed/rest_api/categories&level=3";
    public static final String ADD_TO_WISHLIST_URL ="route=rest/wishlist/wishlist&id=";
    public static final String GET_WISHLIST_URL ="route=rest/wishlist/wishlist";
    public static final String DELETE_WISHLIST_URL ="route=rest/wishlist/wishlist&id=";
    public static final String PRODUCT_DETAIL_URL ="route=feed/rest_api/products&id=";
    public static final String PRODUCT_LIST_URL ="route=feed/rest_api/products&category=";
    public static final String ADD_TO_CART_URL ="route=rest/cart/cart";
    public static final String GET_CART_DATA ="route=rest/cart/cart";
    public static final String GET_USER_INFORMATION_URL ="route=rest/payment_address/paymentaddress";
    public static final String GET_SHIPPING_ADDRESS ="route=rest/shipping_address/shippingaddress";
    public static final String ADD_COUPON_URL ="route=rest/cart/coupon";
    public static final String GET_SHIPPING_METHOD_URL ="route=rest/shipping_method/shippingmethods";
    public static final String POST_SHIPPING_METHODS ="route=rest/shipping_method/shippingmethods";
    public static final String COMPLETE_PAYMENT_ADDRESS ="route=rest/payment_method/payments";
    public static final String CONFIRM_URL ="route=rest/confirm/confirm";
    public static final String EDIT_USER_PROFILE ="route=rest/account/account";
    public static final String EDIT_USER_ADDRESS ="route=rest/account/address&id=";
    public static final String CHANGE_PASSWORD_ACCOUNT ="route=rest/account/password";
    public static final String ADDRESS_LIST_URL = "route=rest/account/address";
    public static final String PRIVACY_POLICY_URL ="route=feed/rest_api/information&id=3";
    public static final String ABOUT_US_URL = "route=feed/rest_api/information&id=4";
    public static final String TERMS_AND_CONDITION_URL = "route=feed/rest_api/information&id=5";
    public static final String PRODUCT_SEARCH_URL = "route=feed/rest_api/products&search=";
    public static final String FACEBOOK_LOGIN_URL ="route=rest/login/sociallogin";
    public static final String ORDER_LIST_URL = "route=rest/order/orders";
    public static final String ORDER_DETAIL_URL = "route=rest/order/orders&id=";
    public static final String NOTIFICATION_URL ="route=feed/rest_api/notification";
    public static final String PAYPAL_GET_TOKEN_URL="route=rest/confirm/confirm";
    public static final String PAYPAL_POST_NONCE_URL="route=rest/confirm/confirm";
    public static String CATEGORY_SEARCH_URL="route=feed/rest_api/products";
}
