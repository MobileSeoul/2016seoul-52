package com.example.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import com.example.myapplication.Kakao.KakaoSignupActivity;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Created by 지명 on 2016-10-22.
 */

public class MainLogin extends AppCompatActivity {

    private MainLogin.SessionCallback callback;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 2;
    private static String TAG = "MainLogin";
    public static String image_URL = "http://jjimstay2-ohsungkim.cloud.or.kr/images/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkPermission();
        MainActivity.spaList = new LinkedList<>();
        MainActivity.asyncTacks = new LinkedList<>();
        setSpaList();
        try {
            PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("Test", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException ignored) {

        }

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
    }


    private void checkPermission() {
        //권한이 없을경우
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //사용자가 임의로 권한 취소시킨 경우 재요청
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);

            } else {
                //권한 요청(최초 요청)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data))
            return;

        super.onActivityResult(requestCode, resultCode, data);
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            redirectSignActivity();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Logger.e(exception);
            }
            //다시 로그인화면 호출
            setContentView(R.layout.activity_login);
        }
    }

    protected void redirectSignActivity() {
        final Intent intent = new Intent(this, KakaoSignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }


    public void setSpaList() {
        final PackageManager pm = this.getPackageManager();
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        int res = pm.checkPermission("android.permission.ACCESS_FINE_LOCATION", getPackageName());
        Location location = null;
        if (res == PackageManager.PERMISSION_GRANTED) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } else {
            location = new Location(LocationManager.NETWORK_PROVIDER);
            // 좌표 권한이 없을 때, 서울 홍익대 좌표로 설정함
            location.setLongitude(126.97796919000007);
            location.setLatitude(37.566535);
        }

        RequestParameterMap parameters = new RequestParameterMap();
        parameters.put("longitude", String.valueOf(location.getLongitude()));
        parameters.put("latitude", String.valueOf(location.getLatitude()));
        parameters.put("range", String.valueOf(7));
        AsyncTask task = new AsyncSearchTask(getResources().getString(R.string.GPSSearchURL)).execute(parameters);
        MainActivity.asyncTacks.add(task);
    }

    public class AsyncSearchTask extends AsyncTask<RequestParameterMap, Integer, Long> {
        private String URL;
        ProgressDialog progressDialog;
        String[] diaogText;

        public AsyncSearchTask(String URL) {
            super();
            this.URL = URL;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            MainActivity.asyncTacks.remove(this); // 완료되면 비동기테스크 목록에서 제거
            Collections.sort(MainActivity.spaList);
        }


        @Override
        protected Long doInBackground(RequestParameterMap... params) {
            Location myLocation = null;

            if (params.length == 0) {
            } else {
                RequestParameterMap parameters = params[0];
                myLocation = new Location(LocationManager.NETWORK_PROVIDER);
                myLocation.setLatitude(Double.parseDouble(parameters.get("latitude")));
                myLocation.setLongitude(Double.parseDouble(parameters.get("longitude")));
                String rawJson = null;
                try {
                    // URL Connection
                    HttpURLConnection conn = (HttpURLConnection) new URL(this.URL).openConnection();
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestMethod("POST");
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                    writer.write(parameters.toString());
                    writer.flush(); // connect()

                    int responseCode = conn.getResponseCode();
                    if (responseCode != 200) {
                        Log.d("RESPONSE_CODE", "" + responseCode); //provide a more meaningful exception message
                    } else { // reponse code 200
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        rawJson = br.readLine();
                        JSONObject parsed = new JSONObject(rawJson);
                        int count = parsed.getInt("count");
                        JSONArray spas = parsed.getJSONArray("result");

                        for (int i = 0; i < count; i++) {
                            publishProgress(i);
                            JSONObject spaObject = spas.getJSONObject(i);
                            Spa spa = new Spa(spaObject.getString("spa_id"), spaObject.getString("spa_name"));

                            JSONArray relaxArray = spaObject.getJSONArray("relax");
                            String[] relaxes = new String[relaxArray.length()];
                            for (int j = 0; j < relaxArray.length(); j++) {
                                relaxes[j] = relaxArray.optString(j);
                            }
                            spa.setRelax(relaxes);

                            JSONArray activityArray = spaObject.getJSONArray("activity");
                            String[] activities = new String[activityArray.length()];
                            for (int j = 0; j < activityArray.length(); j++) {
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
                            spa.setDistance(spaLocation.distanceTo(myLocation));

                            //가격
                            JSONArray priceArray = spaObject.getJSONArray("price");
                            int[] prices = new int[priceArray.length()];
                            for (int j = 0; j < priceArray.length(); j++) {
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
                            for (int j = 0; j < urlArray.length(); j++) {
                                urls[j] = urlArray.optString(j);
                            }
                            spa.setDetailImages_url(urls);

                            //메인 이미지
                            if (!this.isCancelled()) {
                                AsyncTask asyncTask = new AsyncMainImageDownloader(spa).execute(spaObject.getString("main_image"));
                                MainActivity.asyncTacks.add(asyncTask);
                            }

                            //자세한 사항
                            spa.setDetail(spaObject.getString("details"));
                            spa.setRating(spaObject.getString("rating"));
                            MainActivity.spaList.add(spa);
                        }
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
    private class AsyncMainImageDownloader extends AsyncTask<String, Integer, BitmapDrawable> {
        Spa spa;

        public AsyncMainImageDownloader(Spa spa) {
            this.spa = spa;
        }


        @Override
        protected void onPostExecute(BitmapDrawable drawable) {
            super.onPostExecute(drawable);
            MainActivity.asyncTacks.remove(this); // 완료되면 비동기테스크 목록에서 제거
            spa.setMainImage(drawable);
        }

        @Override
        protected BitmapDrawable doInBackground(String... urls) {
            String url = urls[0];
            if (url.equals("")) return null;
            InputStream in = null;
            try {
                in = new java.net.URL(image_URL + url).openStream();
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
                return null;
            }
            return new BitmapDrawable(getResources(), BitmapFactory.decodeStream(in));
        }
    }
}
