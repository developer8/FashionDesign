package com.fashion.binge.fashiondesign.models;

/**
 * Created by gokarna on 01/05/16.
 */
public class CategoryModel {
    private String categoryId;
    private String parentCategoryName;
    private String size;
    private String color;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getParentCategoryName() {
        return parentCategoryName;
    }

    public void setParentCategoryName(String parentCategoryName) {
        this.parentCategoryName = parentCategoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    private String categoryName;
    private String categoryImage;
    private boolean isBackgroundEnabled;

    public boolean isBackgroundEnabled() {
        return isBackgroundEnabled;
    }

    public void setBackgroundEnabled(boolean backgroundEnabled) {
        isBackgroundEnabled = backgroundEnabled;
    }
}
