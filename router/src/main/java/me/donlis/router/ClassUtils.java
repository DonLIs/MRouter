package me.donlis.router;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

import dalvik.system.DexFile;

public final class ClassUtils {

    public static Set<String> getFileNameByPck(Application application,final String packageName)
            throws PackageManager.NameNotFoundException, InterruptedException {
        final Set<String> classNames = new HashSet<>();

        List<String> paths = getPaths(application);
        final CountDownLatch ai = new CountDownLatch(paths.size());

        ThreadPoolExecutor threadPoolExecutor = PoolExecutorManager.newExecutor(paths.size());

        for (final String path : paths) {
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    DexFile dexFile = null;
                    try {
                        dexFile = new DexFile(path);
                        Enumeration<String> entries = dexFile.entries();
                        while (entries.hasMoreElements()){
                            String element = entries.nextElement();
                            if(!TextUtils.isEmpty(element) && element.startsWith(packageName)){
                                classNames.add(element);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        if(dexFile != null){
                            try {
                                dexFile.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        ai.countDown();
                    }
                }
            });
        }

        ai.await();

        return classNames;
    }

    public static List<String> getPaths(Context context) throws PackageManager.NameNotFoundException {
        List<String> result = new ArrayList<>();

        String packageName = context.getPackageName();
        ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
        result.add(applicationInfo.sourceDir);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            if(applicationInfo.splitSourceDirs != null){
                result.addAll(Arrays.asList(applicationInfo.splitSourceDirs));
            }
        }
        return result;
    }

}
