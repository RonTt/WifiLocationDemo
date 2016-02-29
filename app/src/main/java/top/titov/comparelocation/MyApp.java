package top.titov.comparelocation;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;

public class MyApp extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }

    @Override
    public void onTerminate() {
        sContext = null;
        super.onTerminate();
    }

    public static Context getAppContext() {
        return sContext;
    }

    public static int getColorFromRes(int pId) {
        return getAppContext().getResources().getColor(pId);
    }

    public static String getStringFromRes(int pId){
        return getAppContext().getString(pId);
    }
}
