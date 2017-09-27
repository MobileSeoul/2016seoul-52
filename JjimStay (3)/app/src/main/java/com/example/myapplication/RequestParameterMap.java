package com.example.myapplication;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by jacob on 2016-10-28.
 * Web서버에 Post request를 위해서 parameter를 저장하는 클래스.
 * toString 메소드로 저장된 키-벨류를 request에 필요한 형식으로 바꾸어 줌
 */

public class RequestParameterMap extends HashMap<String,String> {
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        Iterator<String> iterator = this.keySet().iterator();
        while(iterator.hasNext()){
            String key = iterator.next();
            builder.append(key).append('=').append(this.get(key));
            if(iterator.hasNext()) {
                builder.append('&');
            }
        }
        return builder.toString();
    }
}
