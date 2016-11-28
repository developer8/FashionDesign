package com.fashion.binge.fashiondesign.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fashion.binge.fashiondesign.ProductListPage;
import com.fashion.binge.fashiondesign.R;
import com.fashion.binge.fashiondesign.adapters.CategoryListAdapter;
import com.fashion.binge.fashiondesign.adapters.CategoryRecyclerAdapter;
import com.fashion.binge.fashiondesign.classes.OnSwipeTouchListener;
import com.fashion.binge.fashiondesign.interfaces.CategoryParentOnClickInteface;
import com.fashion.binge.fashiondesign.models.CategoryModel;
import com.fashion.binge.fashiondesign.shared_preference.SharedPrefrenceInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CategoryFragment extends Fragment implements ListView.OnItemClickListener {
    private String type;
    private RecyclerView categoryView;
    private ListView categoryRecyclerViewChild;
    private JSONArray categoriesJsonArray;
    private int maxHeight;
    private TextView lastSelectedTextView;
    private ImageView selectedView;
    private CoordinatorLayout coordinatorLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getString("type");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.category_fragment, container, false);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinator_layout);
        categoryView = (RecyclerView) view.findViewById(R.id.category_view);
        RelativeLayout categoryChildParent = (RelativeLayout) view.findViewById(R.id.category_child_parent);
        categoryView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        categoryView.setHasFixedSize(true);
        categoryRecyclerViewChild = (ListView) view.findViewById(R.id.category_view_child);
        categoryRecyclerViewChild.setOnItemClickListener(this);
        categoryChildParent.post(new Runnable() {
            @Override
            public void run() {
                maxHeight = (categoryView.getHeight() / 2);
            }
        });
        categoryChildParent.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeDown() {
                if (categoryRecyclerViewChild.getVisibility() == View.VISIBLE) {
                    hideSignUpAnimationView(categoryRecyclerViewChild);
                    selectedView.setColorFilter(Color.argb(0, 255, 0, 0));
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
        categoryView.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeDown() {
                if (categoryRecyclerViewChild.getVisibility() == View.VISIBLE) {
                    hideSignUpAnimationView(categoryRecyclerViewChild);
                    selectedView.setColorFilter(Color.argb(0, 255, 0, 0));
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String categoryJson = sharedPreferences.getString(SharedPrefrenceInfo.LEVEL_THREE_CATEGORY, "");
        prepareRecyclerViewAdapter(categoryJson);

        return view;
    }

    private void prepareRecyclerViewAdapter(String data) {
        try {
            final JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                final JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                if (name.toUpperCase().equals(type)) {
                    final List<CategoryModel> categoryModelList = new ArrayList<>();
                    String categories = jsonObject.getString("categories");
                    categoriesJsonArray = new JSONArray(categories);
                    for (int j = 0; j < categoriesJsonArray.length(); j++) {
                        CategoryModel categoryModel = new CategoryModel();
                        JSONObject categoryJsonObject = categoriesJsonArray.getJSONObject(j);
                        String levelTwoCategoryId = categoryJsonObject.getString("category_id");
                        String levelTwoCategoryName = categoryJsonObject.getString("name");
                        String levelTwoCategoryImage = categoryJsonObject.getString("image");
                        categoryModel.setCategoryId(levelTwoCategoryId);
                        categoryModel.setCategoryName(levelTwoCategoryName);
                        categoryModel.setCategoryImage(levelTwoCategoryImage);
                        categoryModelList.add(categoryModel);
                    }
                    categoryView.setAdapter(new CategoryRecyclerAdapter(getActivity(), categoryModelList, new CategoryParentOnClickInteface() {
                        @Override
                        public void onCategoryParentClick(String id, TextView view1, ImageView imageView) {
                            imageView.setColorFilter(Color.argb(255, 246, 109, 99));
                            if (selectedView != null) {
                                if (!selectedView.getTag().equals(imageView.getTag())) {
                                    selectedView.setColorFilter(Color.argb(0, 255, 0, 0));
                                }
                            }
                            selectedView = imageView;
                            selectedView.setTag(id);
                            final List<CategoryModel> categoryModelList = new ArrayList<>();
                            try {
                                for (int j = 0; j < categoriesJsonArray.length(); j++) {
                                    JSONObject jsonObject1 = categoriesJsonArray.getJSONObject(j);
                                    String category_id = jsonObject1.getString("category_id");
                                    String categories = jsonObject1.getString("categories");
                                    if (id.equals(category_id)) {
                                        if (categories.equals("null")) {
                                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Nothing to show", Snackbar.LENGTH_SHORT);
                                            View view = snackbar.getView();
                                            view.setBackgroundColor(Color.parseColor("#f66d63"));
                                            snackbar.show();
                                            if (categoryRecyclerViewChild.getVisibility() == View.VISIBLE) {
                                                categoryRecyclerViewChild.setVisibility(View.INVISIBLE);
                                            }
                                            return;
                                        }
                                        CategoryModel categoryModel1=new CategoryModel();
                                        categoryModel1.setCategoryId(category_id);
                                        categoryModel1.setCategoryName("All");
                                        categoryModel1.setCategoryImage("");
                                        categoryModelList.add(categoryModel1);
                                        JSONArray jsonArray1 = new JSONArray(categories);
                                        for (int k = 0; k < jsonArray1.length(); k++) {
                                            CategoryModel categoryModel = new CategoryModel();
                                            JSONObject jsonObject2 = jsonArray1.getJSONObject(k);
                                            String categoryId = jsonObject2.getString("category_id");
                                            String categoryName = jsonObject2.getString("name");
                                            String categoryImage = jsonObject2.getString("image");
                                            categoryModel.setCategoryId(categoryId);
                                            categoryModel.setCategoryName(categoryName);
                                            categoryModel.setCategoryImage(categoryImage);
                                            categoryModelList.add(categoryModel);
                                        }
                                        categoryRecyclerViewChild.setAdapter(new CategoryListAdapter(getActivity(), categoryModelList));
                                        if (categoryModelList.size() > 0) {
                                            categoryRecyclerViewChild.setVisibility(View.VISIBLE);
                                            if (getTotalHeightofListView() > maxHeight) {
                                                categoryRecyclerViewChild.getLayoutParams().height = maxHeight;
                                                categoryRecyclerViewChild.requestLayout();
                                            } else {
                                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                                categoryRecyclerViewChild.setLayoutParams(layoutParams);
                                            }
                                        } else {
                                            categoryRecyclerViewChild.setVisibility(View.INVISIBLE);
                                        }

                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void hideSignUpAnimationView(final ListView listView) {
        TranslateAnimation anim = new TranslateAnimation(0, 0, 0, 1900);
        anim.setDuration(300);
        anim.setFillAfter(true);
        listView.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                listView.setVisibility(View.INVISIBLE);
                listView.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private int getTotalHeightofListView() {
        ListAdapter mAdapter = categoryRecyclerViewChild.getAdapter();
        int listviewElementsheight = 0;
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, categoryRecyclerViewChild);
            mView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            listviewElementsheight += mView.getMeasuredHeight();
        }
        return listviewElementsheight;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LinearLayout linearLayout = (LinearLayout) view;
        TextView textView = (TextView) linearLayout.findViewById(R.id.content);
        if (lastSelectedTextView != null) {
            lastSelectedTextView.setBackgroundResource(R.drawable.rectangular_edittext_background);
        }
        textView.setBackgroundColor(Color.parseColor("#f66d63"));
        lastSelectedTextView = textView;
        Intent intent = new Intent(getActivity(), ProductListPage.class);
        intent.putExtra("id", linearLayout.getTag().toString());
        intent.putExtra("name", textView.getTag().toString());
        startActivity(intent);
    }

}