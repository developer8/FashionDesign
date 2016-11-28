package com.fashion.binge.fashiondesign.interfaces;

import org.json.JSONArray;

import java.util.List;


public interface GetProductByShopInterface {
    void setErrorInfo(String message);
    void setSuccessInfo(JSONArray jsonArray,List<String> id,List<String> name,List<String> image,List<String> price,List<String> wishlist, List<String> internationalShipping,List<String> category,List<List<String>> colorList,List<List<String>> sizeList,List<List<String>> categoryList,List<JSONArray> jsonArrayList,String totalProduct);
}