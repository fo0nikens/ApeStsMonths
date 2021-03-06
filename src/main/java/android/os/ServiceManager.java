package android.os;

import android.util.Log;
import com.android.internal.os.BinderInternal;
import java.util.HashMap;
import java.util.Map;

public final class ServiceManager {
    private static final String TAG = "ServiceManager";
    private static HashMap<String, IBinder> sCache = new HashMap();
    private static IServiceManager sServiceManager;

    private static IServiceManager getIServiceManager() {
        if (sServiceManager != null) {
            return sServiceManager;
        }
        sServiceManager = ServiceManagerNative.asInterface(BinderInternal.getContextObject());
        return sServiceManager;
    }

    public static IBinder getService(String name) {
        try {
            IBinder service = (IBinder) sCache.get(name);
            if (service != null) {
                return service;
            }
            return getIServiceManager().getService(name);
        } catch (RemoteException e) {
            Log.e(TAG, "error in getService", e);
            return null;
        }
    }

    public static void addService(String name, IBinder service) {
        try {
            getIServiceManager().addService(name, service, false);
        } catch (RemoteException e) {
            Log.e(TAG, "error in addService", e);
        }
    }

    public static void addService(String name, IBinder service, boolean allowIsolated) {
        try {
            getIServiceManager().addService(name, service, allowIsolated);
        } catch (RemoteException e) {
            Log.e(TAG, "error in addService", e);
        }
    }

    public static IBinder checkService(String name) {
        try {
            IBinder service = (IBinder) sCache.get(name);
            if (service != null) {
                return service;
            }
            return getIServiceManager().checkService(name);
        } catch (RemoteException e) {
            Log.e(TAG, "error in checkService", e);
            return null;
        }
    }

    public static String[] listServices() throws RemoteException {
        try {
            return getIServiceManager().listServices();
        } catch (RemoteException e) {
            Log.e(TAG, "error in listServices", e);
            return null;
        }
    }

    public static void initServiceCache(Map<String, IBinder> cache) {
        if (sCache.size() != 0) {
            throw new IllegalStateException("setServiceCache may only be called once");
        }
        sCache.putAll(cache);
    }
}
