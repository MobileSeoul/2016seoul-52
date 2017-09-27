package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

/**
 * Created by jacob on 2016-08-28.
 */
public class CustomSearchFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_custom_search, null);

        final Spinner city = (Spinner)v.findViewById(R.id.addr_spinner1);
        final Spinner gu = (Spinner)v.findViewById(R.id.addr_spinner2);

        final CheckBox fitnessCheckBox = (CheckBox)v.findViewById(R.id.facility_fitness_checkBox);
        final CheckBox beautyCheckBox = (CheckBox)v.findViewById(R.id.facility_beauty_checkBox);
        final CheckBox pcCheckBox = (CheckBox)v.findViewById(R.id.facility_PC_checkBox);
        final CheckBox arcadeCheckBox = (CheckBox)v.findViewById(R.id.facility_arcade_checkBox);

        Button searchButton = (Button)v.findViewById(R.id.custom_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new SearchResultFragment();

                Bundle bundle = new Bundle();
                bundle.putString("url",getResources().getString(R.string.CustomSearchURL));

                RequestParameterMap parameters = new RequestParameterMap();

                parameters.put("city", city.getSelectedItem().toString());
                parameters.put("gu",gu.getSelectedItem().toString());
                //relax, activity
                bundle.putSerializable("parameters",parameters);

                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.main_fragment, fragment).addToBackStack(null).commit();
            }
        });
        return v;
    }
}
