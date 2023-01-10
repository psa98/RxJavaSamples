package com.example.rxjavasamples.ui.main;

import java.text.DateFormat;
import java.util.Date;

public class Utils {

    static String timestampDateShort(){
        return  DateFormat.getTimeInstance(DateFormat.MEDIUM).format(new Date());
    }
}
