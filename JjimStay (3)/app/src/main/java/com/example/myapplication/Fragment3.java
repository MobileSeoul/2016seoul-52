package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


/**
 * Created by jacob on 2016-08-28.
 */
public class Fragment3 extends Fragment implements View.OnClickListener {

    Button searchNormal, searchDetail;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_page3, container, false);

        searchNormal = (Button) layout.findViewById(R.id.searchNormal);
        searchDetail = (Button) layout.findViewById(R.id.searchDetail);

        getFragmentManager().beginTransaction().replace(R.id.searchPage, new CommonSearchFragment()).commit();
        searchDetail.setBackgroundColor(Color.parseColor("#FFF3F4F4"));

        searchDetail.setOnClickListener(this);
        searchNormal.setOnClickListener(this);


        return layout;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchNormal:
                getFragmentManager().beginTransaction().replace(R.id.searchPage, new CommonSearchFragment()).commit();
                searchDetail.setBackgroundColor(Color.parseColor("#FFF3F4F4"));
                searchNormal.setBackgroundColor(Color.parseColor("#ffFCB315"));
                break;
            case R.id.searchDetail:
                getFragmentManager().beginTransaction().replace(R.id.searchPage, new CustomSearchFragment()).commit();
                searchNormal.setBackgroundColor(Color.parseColor("#FFF3F4F4"));
                searchDetail.setBackgroundColor(Color.parseColor("#ffFCB315"));
                break;
        }
    }
}
