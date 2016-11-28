package com.fashion.binge.fashiondesign.interfaces;

/**
 * Created by gokarna on 14/03/16.
 */
public interface ShopPageContentHolder {
    void setErrorShopPageContent(String statusCode, String statusText);
    void setSuccessShopPageContent(String success, String data);
}
