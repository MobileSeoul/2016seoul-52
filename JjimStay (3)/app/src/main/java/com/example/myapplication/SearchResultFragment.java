package com.example.myapplication;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jacob on 2016-10-30.
 */

public class SearchResultFragment extends Fragment {
    private static String TAG = "FRAGMENT_1";
    private static String image_URL;

    private List<Spa> spaList;
    private RecyclerViewAdapter mAdapter;
    private List<AsyncTask> asyncTacks;

    public void stopAsyncTasks(){
        for(AsyncTask task : asyncTacks){
            task.cancel(true);
        }
        asyncTacks.clear();
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        image_URL = getResources().getString(R.string.ImageBaseURL);
        spaList = new LinkedList<Spa>();
        mAdapter = new RecyclerViewAdapter(getContext(), spaList, R.layout.fragment_card);
        asyncTacks = new LinkedList<>();
        setSpaList(); // test용 데이터 세팅
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_search_result, container, false);

        RecyclerView mRecyclerView = (RecyclerView) layout.findViewById(R.id.search_result_recyclerview);
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
            public void onItemLongClick(View v, int position) {}
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

        Bundle bundle = getArguments();
        String url = bundle.getString("url");
        RequestParameterMap parameterMap = (RequestParameterMap)bundle.getSerializable("parameters");

        AsyncTask task= new AsyncSearchTask(url).execute(parameterMap);
        asyncTacks.add(task);
    }


    @Override
    public void onDetach() {
        stopAsyncTasks();
        super.onDetach();

    }

    public class AsyncSearchTask extends AsyncTask<RequestParameterMap,Integer, Long>{
        private String URL;

        public AsyncSearchTask(String URL) {
            super();
            this.URL = URL;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            asyncTacks.remove(this); // 완료되면 비동기테스크 목록에서 제거
            mAdapter.notifyDataSetChanged();
        }

        @Override
        protected Long doInBackground(RequestParameterMap... params) {

            if(params.length == 0){
                Toast.makeText(getContext(), "Wrong arguments", Toast.LENGTH_SHORT).show();

            } else {
                RequestParameterMap parameters = params[0];
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
                    }
                    else { // reponse code 200
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        rawJson = br.readLine();
                        Log.d("SeachResultFragment", "doInBackground: "+rawJson);
                        JSONObject parsed = new JSONObject(rawJson);
                        int count = parsed.getInt("count");
                        JSONArray spas = parsed.getJSONArray("result");

                        for(int i=0;i<count;i++){
                            if(this.isCancelled()) break;
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

                            //평가
                            spa.setRating(spaObject.getString("rating"));
                            Log.v("값", spa.getMainImage().toString());
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
