package com.example.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Kakao.KakaoSignupActivity;

/**
 * Created by 지명 on 2016-10-22.
 */

public class Fragment4 extends Fragment {

    private TextView nameText, noticeText, eventText, addText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_page4, container, false);


        Log.v("페이지", "페이지4");

        nameText = (TextView) layout.findViewById(R.id.textView2);
        noticeText = (TextView)layout.findViewById(R.id.noticeText);
        eventText = (TextView)layout.findViewById(R.id.eventText);
        addText = (TextView)layout.findViewById(R.id.addText);

        noticeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "공지사항을 클릭하셨습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        eventText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "이벤트를 클릭하셨습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        addText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "더보기를 클릭하셨습니다.", Toast.LENGTH_SHORT).show();
            }
        });



        nameText.setText(KakaoSignupActivity.userName);

        return layout;
    }
}
