package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Kakao.KakaoSignupActivity;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 지명 on 2016-10-22.
 */

public class FragmentAdd extends Fragment {
    public static String TAG = "FragmentAdd";

    private AsyncTask detailImageDownloadTask;

    private List<BitmapDrawable> drawables;

    private ViewPager viewPager;
    private TextView spaName;
    private TextView ratingText;
    private SimpleRatingBar ratingBar;
    private CheckBox favoritecheckBox;
    private TextView detailText;
    private ImageButton callButton;
    private TextView addressPhoneTextView;
    private ImageView fitnessImageView;
    private ImageView beautyImageView;
    private ImageView pcView;
    private ImageView arcadeView;
    private Location location;
    private RelativeLayout mapContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        View layout = inflater.inflate(R.layout.fragment_add_page, container, false);
        viewPager = (ViewPager) layout.findViewById(R.id.detailImageViewPager);


        spaName = (TextView) layout.findViewById(R.id.details_name_textView);
        ratingText = (TextView) layout.findViewById(R.id.details_rating_textView);
        ratingBar = (SimpleRatingBar) layout.findViewById(R.id.rating_bar);
        favoritecheckBox = (CheckBox) layout.findViewById(R.id.favoriteCheckBox);
        detailText = (TextView) layout.findViewById(R.id.details_textview);
        callButton = (ImageButton) layout.findViewById(R.id.callImageButton);
        mapContainer = (RelativeLayout) layout.findViewById(R.id.map_view_container);

        addressPhoneTextView = (TextView) layout.findViewById(R.id.address_phone_detail);

        fitnessImageView = (ImageView) layout.findViewById(R.id.facility_fitness_imageview);
        beautyImageView = (ImageView) layout.findViewById(R.id.facility_beauty_imageview);
        pcView = (ImageView) layout.findViewById(R.id.facility_PC_imageview);
        arcadeView = (ImageView) layout.findViewById(R.id.facility_arcade_imageview);

        //넘어온데이터
        final Spa spa = (Spa) getArguments().get("data");

        detailImageDownloadTask = new AsyncDetailImage();
        detailImageDownloadTask.execute(spa.getDetailImages_url());

        ratingBar.setOnRatingBarChangeListener(new SimpleRatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(SimpleRatingBar simpleRatingBar, float rating, boolean fromUser) {
                //ratingBar.setRating(Float.parseFloat(spa.getRating()));
            }
        });
        // 이름 지정
        spaName.setText(spa.getSpaName());

        //평가 점수
        ratingBar.setRating(Float.parseFloat(spa.getRating()));
        ratingText.setText(spa.getRating());


        favoritecheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
                    JSONArray array = new JSONArray(sp.getString(KakaoSignupActivity.userId, "[]"));
                    int i;
                    for (i = 0; i < array.length(); i++) {
                        if (array.getString(i).equals(spa.getSpa_id())) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                array.remove(i);
                            }
                            sp.edit().putString(KakaoSignupActivity.userId, array.toString()).commit();
                            favoritecheckBox.setChecked(false);
                            Toast.makeText(getActivity(), "즐겨찾기에서 제거되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (i == array.length()) {
                        //추가
                        array.put(spa.getSpa_id());
                        sp.edit().putString(KakaoSignupActivity.userId, array.toString()).commit();
                        favoritecheckBox.setChecked(true);
                        Toast.makeText(getActivity(), "즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "수정에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        detailText.setText(spa.getDetail());

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + spa.getPhone()));
                startActivity(intent);
            }
        });

        //위시리스트(=즐겨찾기)에 등록 되어있으면 하트 이미지 변경
        try {
            String favorite = sp.getString(KakaoSignupActivity.userId, "[]");
            JSONArray array = new JSONArray(favorite);
            for (int i = 0; i < array.length(); i++) {
                if (array.getString(i).equals(spa.getSpa_id())) {
                    favoritecheckBox.setChecked(true);
                }
            }
        } catch (JSONException e) {
            Toast.makeText(getContext(), "위시리스트를 가져오는데에 실패했습니다.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        // 주소, 전화번호 출력
        addressPhoneTextView.setText(spa.getFullAddress() + "\n Tel : " + spa.getPhone());

        // 해당하는 시설 있으면 이미지 변경
        List<String> relaxList = Arrays.asList(spa.getRelax());
        List<String> activityList = Arrays.asList(spa.getActivity());
        if (relaxList.contains("beauty")) {
            beautyImageView.setBackgroundResource(R.drawable.beauty_selected);
        }
        if (activityList.contains("PC")) {
            pcView.setBackgroundResource(R.drawable.pc_selected);
        }
        if (activityList.contains("health")) {
            fitnessImageView.setBackgroundResource(R.drawable.fitness_selected);
        }
        if (activityList.contains("arcade")) {
            arcadeView.setBackgroundResource(R.drawable.arcade_selected);
        }


        // 맵에서 사용할 위치 좌표
        location = spa.getLocation();

        //다음 지도
        MapView mapView = new MapView(getContext());
        mapView.setDaumMapApiKey(getResources().getString(R.string.daum_app_key));
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(location.getLatitude(), location.getLongitude()), 1, true);
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(spa.getSpaName());
        marker.setTag(0);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(location.getLatitude(), location.getLongitude()));
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        mapView.addPOIItem(marker);
        mapContainer.addView(mapView);

        return layout;
    }

    private class AsyncDetailImage extends AsyncTask<String, Long, BitmapDrawable[]> {
        @Override
        protected void onPostExecute(BitmapDrawable[] drawables) {
            super.onPostExecute(drawables);
            ViewPagerAdapter mAdapter = new ViewPagerAdapter(drawables, getActivity().getApplicationContext());
            viewPager.setAdapter(mAdapter);
        }

        @Override
        protected BitmapDrawable[] doInBackground(String... params) {
            String imageBaseURL = getResources().getString(R.string.ImageBaseURL);
            BitmapDrawable[] drawables = new BitmapDrawable[params.length];
            for (int i = 0; i < params.length; i++) {
                try {
                    if (this.isCancelled()) break;
                    InputStream is = new URL(imageBaseURL + params[i]).openStream();
                    BitmapDrawable drawable = new BitmapDrawable(getResources(), BitmapFactory.decodeStream(is));
                    drawables[i] = drawable;
                } catch (IOException e) {
                    Log.d(TAG, "Exception in for loop : index " + i);
                    e.printStackTrace();
                }
            }
            return drawables;
        }
    }

    @Override
    public void onDetach() {
        detailImageDownloadTask.cancel(true); // task 강제 종료
        super.onDetach();
    }
}
