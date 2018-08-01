package com.arathus.materaexample.view_pager;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.arathus.materaexample.R;

public class CustomPagerAdapter extends PagerAdapter {

    private Context mContext;

    public CustomPagerAdapter(Context context) {
        mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        ModelObject modelObject = ModelObject.values()[position];
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(modelObject.getLayoutResId(), collection, false);
        if (position == 0 || position == 2) {

            DisplayMetrics deviceDisplayMetrics = new DisplayMetrics();

            // populate the DisplayMetrics object with the display characteristics
            Activity act = (Activity) mContext;
            act.getWindowManager().getDefaultDisplay().getMetrics(deviceDisplayMetrics);

            // get the width and height
            int size = (deviceDisplayMetrics.heightPixels > deviceDisplayMetrics.widthPixels) ? deviceDisplayMetrics.heightPixels : deviceDisplayMetrics.widthPixels;

            ImageView img = layout.findViewById(R.id.imageview_01);

            img.getLayoutParams().width = (position == 0) ? (size / 4) : (size / 5);
            img.getLayoutParams().height = (position == 0) ? ((size / 4) * 50 / 28) : (size / 5);
            img.requestLayout();
        }

        collection.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return ModelObject.values().length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        ModelObject customPagerEnum = ModelObject.values()[position];
        return mContext.getString(customPagerEnum.getTitleResId());
    }

}
