package ng.com.addproducts;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by manglide on 4/16/2017.
 */
public class ApplicationName extends Application {
    private static Context context;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void onCreate() {
        super.onCreate();
        ApplicationName.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return ApplicationName.context;
    }
}
