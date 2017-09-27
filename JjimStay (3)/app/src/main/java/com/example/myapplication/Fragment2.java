package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.example.myapplication.RequestParameterMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.myapplication.Kakao.KakaoSignupActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by 지명 on 2016-10-22.
 */

public class Fragment2 extends Fragment {

    public static String TAG = Fragment2.class.getName();
    private static String image_URL;

    private List<Spa> spaList;
    private SharedPreferences sp;
    private RecyclerView.Adapter mAdapter;
    private List<AsyncTask> asyncTacks;
    RecyclerView mRecyclerView;
    public void stopAsyncTasks(){
        for(AsyncTask task : asyncTacks){
            task.cancel(true);
        }
        asyncTacks.clear();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Context mContext = getContext();
        if(isVisibleToUser && mContext != null){
            setSpaList();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        image_URL = getResources().getString(R.string.ImageBaseURL);
        asyncTacks = new LinkedList<>();
        spaList = new LinkedList<Spa>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        View layout = inflater.inflate(R.layout.fragment_page2, container, false);


        ImageButton backButton = (ImageButton)layout.findViewById(R.id.backKeyImage);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().popBackStackImmediate();
            }
        });
        mAdapter = new RecyclerViewAdapter(layout.getContext(), spaList, R.layout.fragment_card_wishlist);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.favoriteRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(layout.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addOnItemTouchListener(new RecyclerViewOnItemClickListener(getActivity(), mRecyclerView, new RecyclerViewOnItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View v, final int position) {
                FragmentAdd fragmentAdd = new FragmentAdd();
                Bundle bundle = new Bundle();
                bundle.putParcelable("data", spaList.get(position));
                fragmentAdd.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.main_fragment, fragmentAdd).addToBackStack(null).commit();
            }

            @Override
            public void onItemLongClick(View v, int position) {
            }
        }
        ));

        return layout;
    }

    public void setSpaList() {
        final PackageManager pm = this.getContext().getPackageManager();
        final LocationManager locationManager = (LocationManager)this.getContext().getSystemService(Context.LOCATION_SERVICE);
        int res = pm.checkPermission("android.permission.ACCESS_FINE_LOCATION",getContext().getPackageName());
        Location location = null;
        if(res == PackageManager.PERMISSION_GRANTED){
//            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            // todo : 이건 그냥 테스트용 삭제예정
            location = new Location(LocationManager.NETWORK_PROVIDER);
            location.setLongitude(126.97796919000007);
            location.setLatitude(37.566535);
        } else {
            Toast.makeText(getContext(),"Location Permission denied",Toast.LENGTH_SHORT).show();
            location = new Location(LocationManager.NETWORK_PROVIDER);
            // 좌표 권한이 없을 때, 서울 홍익대 좌표로 설정함
            location.setLongitude(126.97796919000007);
            location.setLatitude(37.566535);
        }

        try {
            String jsonArray = sp.getString(KakaoSignupActivity.userId,"[]");
            JSONArray array = new JSONArray(jsonArray);

            if(array.length() > 0){ // 즐겨찾기가 없는 경우 request 하지 않음
                StringBuilder builder = new StringBuilder();
                for(int i = 0; i < array.length() ; i++){
                    builder.append((String)array.get(i)).append(' ');
                }
                RequestParameterMap parameters = new RequestParameterMap();
                parameters.put("spa_id",builder.toString());
                AsyncTask task = new AsyncIDSearchTask(getResources().getString(R.string.SpaIDSearchURL)).execute(parameters);
                asyncTacks.add(task);
            } else {
                Toast.makeText(getContext(), "위시리스트가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Toast.makeText(getContext(), "위시리스트를 불러오는중 에러가 발생했습니다.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        stopAsyncTasks();
        super.onDetach();
    }

    public class AsyncIDSearchTask extends AsyncTask<RequestParameterMap,Integer, Long>{
        private String URL;

        public AsyncIDSearchTask(String URL) {
            super();
            this.URL = URL;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            asyncTacks.remove(this); // 완료되면 비동기테스크 목록에서 제거
            mRecyclerView.invalidate();
            mAdapter.notifyDataSetChanged();
        }

        @Override
        protected Long doInBackground(RequestParameterMap... params) {
//            Location myLocation = null;
            spaList.clear();

            if(params.length == 0){
                Toast.makeText(getContext(), "Wrong arguments", Toast.LENGTH_SHORT).show();

            } else {
                RequestParameterMap parameters = params[0];
//                myLocation = new Location(LocationManager.NETWORK_PROVIDER);
//                myLocation.setLatitude(Double.parseDouble(parameters.get("latitude")));
//                myLocation.setLongitude(Double.parseDouble(parameters.get("longitude")));
                String rawJson = null;
                try {
                    // URL Connection
                    HttpURLConnection conn = (HttpURLConnection)new URL(this.URL).openConnection();
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestMethod("POST");
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                    writer.write(parameters.toString());
                    writer.flush(); // connect()

                    int responseCode = conn.getResponseCode();
                    if (responseCode != 200) {
                        Log.d("RESPONSE_CODE","" + responseCode); //provide a more meaningful exception message
                        Toast.makeText(getContext(), "서버에 접속 할 수 없습니다.\nRESPONSE_CODE : "+ responseCode, Toast.LENGTH_SHORT).show();
                    }
                    else { // reponse code 200
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        rawJson = br.readLine();
                        Log.d(TAG, "doInBackground: "+rawJson);
                        JSONObject parsed = new JSONObject(rawJson);
                        int count = parsed.getInt("count");
                        JSONArray spas = parsed.getJSONArray("result");

                        for(int i=0;i<count;i++){
                            JSONObject spaObject = spas.getJSONObject(i);
                            Spa spa = new Spa(spaObject.getString("spa_id"), spaObject.getString("spa_name"));

                            JSONArray relaxArray = spaObject.getJSONArray("relax");
                            String[] relaxes = new String[relaxArray.length()];
                            for(int j=0;j<relaxArray.length();j++){
                                relaxes[j] = relaxArray.optString(j);
                            }
                            spa.setRelax(relaxes);

                            JSONArray activityArray= spaObject.getJSONArray("activity");
                            String[] activities = new String[activityArray.length()];
                            for(int j=0;j<activityArray.length();j++){
                                activities[j] = activityArray.optString(j);
                            }
                            spa.setActivity(activities);

                            // 전화 번호
                            spa.setPhone(spaObject.getString("phone"));

                            //스파 위치, 현재 위치부터의 거리
                            JSONObject locationObject = spaObject.getJSONObject("location");
                            Location spaLocation = new Location(LocationManager.NETWORK_PROVIDER);
                            spaLocation.setLatitude(locationObject.getDouble("latitude"));
                            spaLocation.setLongitude(locationObject.getDouble("longitude"));
                            spa.setLocation(spaLocation);
//                            spa.setDistance(spaLocation.distanceTo(myLocation));

                            //가격
                            JSONArray priceArray = spaObject.getJSONArray("price");
                            int[] prices = new int[priceArray.length()];
                            for(int j=0;j < priceArray.length();j++) {
                                prices[j] = priceArray.optInt(j);
                            }
                            spa.setPrice(prices);

                            //주소
                            JSONObject addressObject = spaObject.getJSONObject("address");
                            spa.setCity(addressObject.getString("city"));
                            spa.setGu(addressObject.getString("gu"));
                            spa.setDong(addressObject.getString("dong"));
                            spa.setDetailAddress(addressObject.getString("detail"));

                            //자세한 이미지
                            JSONArray urlArray = spaObject.getJSONArray("detail_images");
                            String[] urls = new String[urlArray.length()];
                            for(int j=0;j < urlArray.length();j++) {
                                urls[j] = urlArray.optString(j);
                            }
                            spa.setDetailImages_url(urls);

                            //메인 이미지
                            if(!this.isCancelled()) {
                                AsyncTask asyncTask = new AsyncMainImageDownloader(spa).execute(spaObject.getString("main_image"));
                                asyncTacks.add(asyncTask);
                            }

                            //자세한 사항
                            spa.setDetail(spaObject.getString("details"));

                            // 평가
                            spa.setRating(spaObject.getString("rating"));

                            spaList.add(spa);
                        }
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }

    private class AsyncMainImageDownloader extends AsyncTask<String,Integer,BitmapDrawable>{
        Spa spa;

        public AsyncMainImageDownloader(Spa spa) {
            Log.d(TAG, "AsyncMainImageDownloader: START MAIN DOWNLOAD");
            this.spa = spa;
        }

        @Override
        protected void onPostExecute(BitmapDrawable drawable) {
            super.onPostExecute(drawable);
            asyncTacks.remove(this); // 완료되면 비동기테스크 목록에서 제거
            spa.setMainImage(drawable);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        protected BitmapDrawable doInBackground(String... urls) {
            String url = urls[0];
            if(url.equals("")) return null;
            InputStream in = null;
            try {
                in = new java.net.URL(image_URL +url).openStream();
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
                return null;
            }
            return new BitmapDrawable(getResources(), BitmapFactory.decodeStream(in));
        }
    }
}