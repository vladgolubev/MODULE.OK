package ua.samosfator.moduleok.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

import java.util.Date;

import de.greenrobot.event.EventBus;

public class App extends Application {

    private static Context mContext;
    private static Point screenSize;
    private static Date updateTime;

    private static boolean isLoggedIn;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        Preferences.init(getApplicationContext());
    }

    public static Context getContext() {
        return mContext;
    }

    public static boolean hasInternetConnection() {
        final ConnectivityManager conMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static Point getScreenSize() {
        if (screenSize == null) {
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            screenSize = new Point();
            if (App.isAndroidNewerIceCreamSandwich()) {
                display.getSize(screenSize);
            } else {
                screenSize.set(display.getWidth(), display.getHeight());
            }
        }
        return screenSize;
    }

    public static void registerClassForEventBus(Object that) {
        if (!EventBus.getDefault().isRegistered(that)) {
            EventBus.getDefault().register(that);
        }
    }

    public static String getVersion() {
        PackageInfo packageInfo;
        try {
            packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        return "";
    }

    public static boolean isServiceRunning(Class<? extends Service> serviceClass) {
        ActivityManager manager = (ActivityManager) App.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAndroidNewerIceCreamSandwich() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    public static boolean isLoggedIn() {
        return isLoggedIn;
    }

    public static void setIsLoggedIn(boolean loginState) {
        App.isLoggedIn = loginState;
    }
}