package com.fashion.binge.fashiondesign.models;

/**
 * Created by gokarna on 23/02/16.
 */
public class ShoppageInfo {
    private String sellerId;
    private String sellerName;
    private String sellerNickName;
    private String sellerBanner;
    private String sellerGroup;
    private String userFollow;
    private String sellerCountryFlag;
    private String sellerAvater;
    private String sellerCountryName;
    private boolean isClicked=false;

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    public String getSellerAvater() {
        return sellerAvater;
    }

    public void setSellerAvater(String sellerAvater) {
        this.sellerAvater = sellerAvater;
    }

    public String getSellerCountryFlag() {
        return sellerCountryFlag;
    }

    public void setSellerCountryFlag(String sellerCountryFlag) {
        this.sellerCountryFlag = sellerCountryFlag;
    }

    public String getSellerCountryName() {
        return sellerCountryName;
    }

    public void setSellerCountryName(String sellerCountryName) {
        this.sellerCountryName = sellerCountryName;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerNickName() {
        return sellerNickName;
    }

    public void setSellerNickName(String sellerNickName) {
        this.sellerNickName = sellerNickName;
    }

    public String getSellerBanner() {
        return sellerBanner;
    }

    public void setSellerBanner(String sellerBanner) {
        this.sellerBanner = sellerBanner;
    }

    public String getSellerGroup() {
        return sellerGroup;
    }

    public void setSellerGroup(String sellerGroup) {
        this.sellerGroup = sellerGroup;
    }

    public String getUserFollow() {
        return userFollow;
    }

    public void setUserFollow(String userFollow) {
        this.userFollow = userFollow;
    }
}
