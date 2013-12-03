package com.mikroskil.android.qattend;

import android.content.Context;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

public class CardSimpleAdapter extends SimpleAdapter {

    public CardSimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
    }

}
