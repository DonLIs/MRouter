package me.donlis.router;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.ActivityCompat;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

import me.donlis.annotation.RouterBean;

public class MRouter {

    private static Application mContext;

    private static MRouter instance;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public static MRouter getInstance(){
        if(instance == null){
            synchronized (MRouter.class){
                if(instance == null){
                    instance = new MRouter();
                }
            }
        }
        return instance;
    }

    private MRouter(){

    }

    public static void init(Application application){
        mContext = application;
        try{
            install();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void install()
            throws PackageManager.NameNotFoundException,
            InterruptedException, ClassNotFoundException,
            NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        Set<String> classSet = ClassUtils.getFileNameByPck(mContext, "me.donlis.mrouter.router");
        for (String path : classSet) {
            ((IRoute) Class.forName(path).getConstructor().newInstance()).load(RouterCollection.group);
        }

        for (Map.Entry<String, RouterBean> entry : RouterCollection.group.entrySet()) {
            System.out.println(entry.getKey()+":"+entry.getValue().getClazz().toString());
        }
    }

    public Postcard build(String path){
        return new Postcard(path);
    }

    public void navigation(final Context context, Postcard postcard){
        final Context curContext = context == null ? mContext : context;

        prepare(postcard);

        final Intent intent = new Intent(curContext,postcard.getClazz());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ActivityCompat.startActivity(curContext,intent,null);
            }
        });
    }

    private void prepare(Postcard postcard) {
        RouterBean routerBean = RouterCollection.group.get(postcard.getPath());
        if(routerBean != null){
            postcard.setClazz(routerBean.getClazz());
        }
    }

}
