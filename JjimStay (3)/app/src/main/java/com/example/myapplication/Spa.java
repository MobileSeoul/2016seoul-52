package com.example.myapplication;

import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Public on 2016-07-20.
 */
public class Spa implements Parcelable, Comparable<Spa> {
    public static Parcelable.Creator<Spa> CREATOR = new ClassLoaderCreator<Spa>() {
        @Override
        public Spa createFromParcel(Parcel parcel, ClassLoader classLoader) {
            return new Spa(parcel);
        }

        @Override
        public Spa createFromParcel(Parcel parcel) {
            return new Spa(parcel);
        }

        @Override
        public Spa[] newArray(int i) {
            return new Spa[i];
        }
    };


    private String spa_id;
    private String spaName;
    private String[] relax;
    private String[] activity;
    private String phone;
    private Location location;
    private int[] price;
    private String minPrice;
    private String rating;
    private String dong;
    private String gu;
    private String city;
    private String detailAddress;
    private BitmapDrawable mainImage;
    private String[] detailImages_url;
    private String detail;
    private double distance;

    public Spa(Parcel parcel) {
        spa_id = parcel.readString();
        spaName = parcel.readString();
        phone = parcel.readString();
        city = parcel.readString();
        gu = parcel.readString();
        dong = parcel.readString();
        detailAddress = parcel.readString();
        detail = parcel.readString();
        rating = parcel.readString();
        detailImages_url = (String[]) parcel.readArray(String.class.getClassLoader());
        relax = (String[]) parcel.readArray(String.class.getClassLoader());
        activity = (String[]) parcel.readArray(String.class.getClassLoader());
    }

    public Spa(String spa_id, String name) {
        this.spa_id = spa_id;
        this.spaName = name;
    }

    public String getSpa_id() {
        return spa_id;
    }

    public void setSpa_id(String spa_id) {
        this.spa_id = spa_id;
    }


    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String[] getDetailImages_url() {
        return detailImages_url;
    }

    public void setDetailImages_url(String[] detailImages_url) {
        this.detailImages_url = detailImages_url;
    }


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String[] getRelax() {
        return relax;
    }

    public void setRelax(String[] relax) {
        this.relax = relax;
    }

    public String[] getActivity() {
        return activity;
    }

    public void setActivity(String[] activity) {
        this.activity = activity;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int[] getPrice() {
        return price;
    }

    public void setPrice(int[] price) {
        this.price = price;
    }

    public String getDong() {
        return dong;
    }

    public void setDong(String dong) {
        this.dong = dong;
    }

    public String getGu() {
        return gu;
    }

    public void setGu(String gu) {
        this.gu = gu;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public String getSimpleAddress() {
        return getCity() + " " + getGu();
    }

    public String getFullAddress(){
        return getCity() + ' ' + // 서울
                getGu() + ' ' + // 중구
                getDong() + ' ' + // 명동
                getDetailAddress(); // 100-1 번지
    }

    public String getSpaName() {
        return spaName;
    }

    public String getMinPrice() {
        return String.valueOf(price[0]);
    }

    public BitmapDrawable getMainImage() {
        return mainImage;
    }

    public void setMainImage(BitmapDrawable drawable) {
        this.mainImage = drawable;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(spa_id);
        parcel.writeString(spaName);
        parcel.writeString(phone);
        parcel.writeString(city);
        parcel.writeString(gu);
        parcel.writeString(dong);
        parcel.writeString(detailAddress);
        parcel.writeString(detail);
        parcel.writeString(rating);
        parcel.writeArray(detailImages_url);
        parcel.writeStringArray(relax);
        parcel.writeStringArray(activity);
    }

    @Override
    public String toString() {
        return new StringBuilder().append("\nspa_id : ").append(getSpa_id())
                .append("\nspaName : ").append(getSpaName()).toString();
    }


    // 거리순 정렬을 위해서 구현
    @Override
    public int compareTo(Spa another) {
        if (this.getDistance() > another.getDistance()) {
            return 1;
        } else if (this.getDistance() < another.getDistance()) {
            return -1;
        } else {
            return 0;
        }
    }
}
