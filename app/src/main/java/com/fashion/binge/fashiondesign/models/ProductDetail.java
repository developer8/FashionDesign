package com.fashion.binge.fashiondesign.models;

import java.util.List;

/**
 * Created by gokarna on 26/04/16.
 */
public class ProductDetail {
    private String id;
    private String productImage;
    private List<String> productSubImage;
    private String price;
    private String description;
    private String name;
    private List<String> size;
    private List<String> optionValueId;
    private List<String> productOptionValueId;
    private List<String> quantity;
    private String wishList;

    public String getWishList() {
        return wishList;
    }

    public void setWishList(String wishList) {
        this.wishList = wishList;
    }

    public List<String> getQuantity() {
        return quantity;
    }

    public void setQuantity(List<String> quantity) {
        this.quantity = quantity;
    }

    private List<String> productOptionId;
    public List<String> getProductOptionValueId() {
        return productOptionValueId;
    }

    public void setProductOptionValueId(List<String> productOptionValueId) {
        this.productOptionValueId = productOptionValueId;
    }

    public List<String> getProductOptionId() {
        return productOptionId;
    }

    public void setProductOptionId(List<String> productOptionId) {
        this.productOptionId = productOptionId;
    }


    public List<String> getSize() {
        return size;
    }

    public void setSize(List<String> size) {
        this.size = size;
    }

    public List<String> getOptionValueId() {
        return optionValueId;
    }

    public void setOptionValueId(List<String> optionValueId) {
        this.optionValueId = optionValueId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public List<String> getProductSubImage() {
        return productSubImage;
    }

    public void setProductSubImage(List<String> productSubImage) {
        this.productSubImage = productSubImage;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
