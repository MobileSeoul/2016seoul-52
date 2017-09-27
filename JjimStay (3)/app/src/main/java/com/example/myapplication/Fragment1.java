package com.example.myapplication;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.InputStream;
import java.util.List;

/**
 * Created by 지명 on 2016-10-22.
 */

public class Fragment1 extends Fragment {

    private static String TAG = "FRAGMENT_1";
    private static String image_URL;

    private RecyclerViewAdapter mAdapter;
    private List<AsyncTask> asyncTacks;
    RecyclerView mRecyclerView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAdapter = new RecyclerViewAdapter(getContext(), MainActivity.spaList, R.layout.fragment_card);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_page1, container, false);

        mRecyclerView = (RecyclerView) layout.findViewById(R.id.mainRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(layout.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addOnItemTouchListener(new RecyclerViewOnItemClickListener(getActivity(), mRecyclerView, new RecyclerViewOnItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View v, final int position) {
                FragmentAdd fragmentAdd = new FragmentAdd();
                Bundle bundle = new Bundle();
                bundle.putParcelable("data", MainActivity.spaList.get(position));
                fragmentAdd.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.main_fragment, fragmentAdd).addToBackStack(null).commit();
            }

            @Override
            public void onItemLongClick(View v, int position) {
            }
        }
        ));

        mRecyclerView.invalidate();
        mAdapter.notifyDataSetChanged();

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onDetach() {
        //stopAsyncTasks();
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }



}