package com.yongqi.wallet.bean;

import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * author ：SunXiao
 * date : 2021/2/3 18:43
 * package：com.yongqi.wallet.bean
 * description :
 */
public class RawStringJsonAdapter extends TypeAdapter<String> {

    @Override
    public void write(JsonWriter out, String value) throws IOException {
        out.jsonValue(value);
    }

    @Override
    public String read(JsonReader in) throws IOException {
        return new JsonParser().parse(in).toString();
    }
}