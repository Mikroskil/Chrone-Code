package com.mikroskil.android.qattend;

import android.app.Application;

import com.mikroskil.android.qattend.db.model.ParseEvent;
import com.mikroskil.android.qattend.db.model.ParseMember;
import com.mikroskil.android.qattend.db.model.ParseMembership;
import com.mikroskil.android.qattend.db.model.ParseOrganization;
import com.mikroskil.android.qattend.db.model.ParseTicket;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class QattendApp extends Application {

    public static final String TAG = "QATTEND";
    public static final boolean DEBUG = true;

    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(ParseEvent.class);
        ParseObject.registerSubclass(ParseOrganization.class);
        ParseObject.registerSubclass(ParseMembership.class);
        ParseObject.registerSubclass(ParseTicket.class);
        ParseUser.registerSubclass(ParseMember.class);

        if (DEBUG) {
            Parse.initialize(this, "eGmWE14H2rY6yOWHgwZnePrGystQYh7IjcSx2s3E", "JD2C1AxJpMYxmYlDaWp94d7y8jBErhcpWN9RI9iw");
        } else {
            Parse.initialize(this, "dqqiXSTmaYEa3pRpAUdUlROm1xJSHMhywXdzKNYy", "zQfaZsKkvJo0lqJ3WL5zSd03SwQitTmE256IXZCL");
        }
    }

}
