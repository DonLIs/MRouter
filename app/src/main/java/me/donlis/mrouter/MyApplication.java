package me.donlis.mrouter;

import android.app.Application;

import me.donlis.router.MRouter;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MRouter.init(this);
    }
}
