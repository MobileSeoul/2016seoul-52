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
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * Created by jacob on 2016-08-28.
 */
public class CommonSearchFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_common_search, null);
        final EditText queryEditText = (EditText)v.findViewById(R.id.query_edit_text);
        ImageButton search_button = (ImageButton)v.findViewById(R.id.common_search_button);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new SearchResultFragment();

                Bundle bundle = new Bundle();
                bundle.putString("url",getResources().getString(R.string.CommonSearchURL));

                RequestParameterMap parameters = new RequestParameterMap();
                parameters.put("query",queryEditText.getText().toString());
                bundle.putSerializable("parameters",parameters);

                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.main_fragment, fragment).addToBackStack(null).commit();

                InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(queryEditText.getWindowToken(), 0);

            }
        });

        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(queryEditText, InputMethodManager.SHOW_FORCED);

        return v;
    }
}
