package aspire2droneteam.com.dronescanningapp;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.secneo.sdk.Helper;

public class MApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        Helper.install(MApplication.this);
    }
}
