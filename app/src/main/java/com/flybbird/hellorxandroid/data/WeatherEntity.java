package com.flybbird.hellorxandroid.data;

import java.util.List;

/**
 * Created by SuyoungKang on 2016. 2. 19..
 */
public class WeatherEntity {
    public String base;
    public List<Weather> weather;

    public class Weather {
        public int id;
        public String main;
        public String description;
        public String icon;
    }

}
