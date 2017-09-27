package com.example.myapplication;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by KangJinho on 2016-10-04.
 */

public class ViewPagerAdapter extends PagerAdapter {


    private BitmapDrawable[] resources;
    private LayoutInflater inflater;


    public ViewPagerAdapter(BitmapDrawable[] resources, Context c) {
        super();
        this.resources = resources;
        this.inflater = LayoutInflater.from(c);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (resources != null) {
            View view = inflater.inflate(R.layout.fragment_detail_viewpager, container, false);
            ImageView iv = (ImageView) view.findViewById(R.id.DetailImageViewPagerImageView);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ) {
                iv.setBackground(resources[position]);
            }
            container.addView(view, 0);
            return view;
        } else {
            return null;
        }

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        if (resources == null) {
            return 0;
        } else {
            return resources.length;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
