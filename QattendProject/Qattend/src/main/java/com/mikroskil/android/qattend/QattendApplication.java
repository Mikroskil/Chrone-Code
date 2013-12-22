package com.mikroskil.android.qattend;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class QattendApplication extends Application {

    public static final boolean APPDEBUG = true;

    @Override
    public void onCreate() {
        super.onCreate();

        if (APPDEBUG) {
            ParseObject.registerSubclass(Event.class);
            Parse.initialize(this, "eGmWE14H2rY6yOWHgwZnePrGystQYh7IjcSx2s3E", "JD2C1AxJpMYxmYlDaWp94d7y8jBErhcpWN9RI9iw");
        } else {
            Parse.initialize(this, "dqqiXSTmaYEa3pRpAUdUlROm1xJSHMhywXdzKNYy", "zQfaZsKkvJo0lqJ3WL5zSd03SwQitTmE256IXZCL");
        }
    }

}
