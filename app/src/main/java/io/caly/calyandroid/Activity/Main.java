package io.caly.calyandroid.Activity;

import android.support.v7.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 23
 */

public class Main {

    public static void main(String[] args){
        String json =
                "{\n" +
                "\"name\":\"hello!\",\n" +
                "\"data\":{\n" +
                "\"name\":\"jspiner\",\n" +
                "\"age\":8,\n" +
                "\"birth\":1996\n" +
                "},\n" +
                "\"friends\":[\n" +
                "\"john\",\n" +
                "\"smith\",\n" +
                "\"sam\"\n" +
                "]\n" +
                "}";

        DataJson dataJson = new Gson().fromJson(json, DataJson.class);

        System.out.println("name : " + dataJson.name);

        for(DataJson.Book book : dataJson.books){
            System.out.println("book name : " + book.name);
            System.out.println("book price : " + book.price);
        }

    }

    class DataJson {

        @SerializedName("name")
        public String name;

        @SerializedName("data")
        public Data data;

        @SerializedName("books")
        public List<Book> books;

        class Data{
            @SerializedName("name")
            public String name;

            @SerializedName("age")
            public int age;

            @SerializedName("birth")
            public int birth;
        }

        class Book{
            @SerializedName("name")
            public String name;

            @SerializedName("price")
            public int price;
        }

    }

}
