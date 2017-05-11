package io.caly.calyandroid.model.dataModel;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 12
 */

public class TestModel {

    public int year;
    public int month;
    public int day;
    public String summary;
    public String time;
    public String location;

    public TestModel(){

    }

    public TestModel(int year, int month, int day, String summary, String time, String location){
        this.year = year;
        this.month = month;
        this.day = day;
        this.summary = summary;
        this.time = time;
        this.location = location;
    }


}
