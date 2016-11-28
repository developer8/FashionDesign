package com.fashion.binge.fashiondesign.models;


import java.io.Serializable;
import java.util.List;

public class GetProductByShopModel implements Serializable {
    private String id;
    private String name;
    private String image;
    private String price;
    private String internationShipping;
    private List<String> colorList;
    private List<String> sizeList;
    private String jsonId;
    private String wishlist;
    private String banner;
    private String sellerId;
    private String sellerNickName;
    private String sellerAvater;


    public String getInternationShipping() {
        return internationShipping;
    }

    public void setInternationShipping(String internationShipping) {
        this.internationShipping = internationShipping;
    }

    public String getSellerAvater() {
        return sellerAvater;
    }

    public void setSellerAvater(String sellerAvater) {
        this.sellerAvater = sellerAvater;
    }

    public String getSellerNickName() {
        return sellerNickName;
    }

    public void setSellerNickName(String sellerNickName) {
        this.sellerNickName = sellerNickName;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getWishlist() {
        return wishlist;
    }

    public void setWishlist(String wishlist) {
        this.wishlist = wishlist;
    }

    public String getJsonId() {
        return jsonId;
    }

    public void setJsonId(String jsonId) {
        this.jsonId = jsonId;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    private List<String> categoryList;
    public List<String> getCategoryList() {
        return category;
    }

    public void setCategoryList(List<String> categoryList) {
        this.category = categoryList;
    }

    private List<String> category;
    public List<String> getColorList() {
        return colorList;
    }

    public void setColorList(List<String> colorList) {
        this.colorList = colorList;
    }

    public List<String> getSizeList() {
        return sizeList;
    }

    public void setSizeList(List<String> sizeList) {
        this.sizeList = sizeList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
