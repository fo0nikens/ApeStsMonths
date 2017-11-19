package android.support.v4.text;

import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

class ICUCompatIcs {
    private static final String TAG = "ICUCompatIcs";
    private static Method sAddLikelySubtagsMethod;
    private static Method sGetScriptMethod;

    static {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.support.v4.text.ICUCompatIcs.<clinit>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:116)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:249)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
Caused by: java.lang.NullPointerException
*/
        /*
        r6 = 0;
        r2 = "libcore.icu.ICU";	 Catch:{ Exception -> 0x0026 }
        r0 = java.lang.Class.forName(r2);	 Catch:{ Exception -> 0x0026 }
        if (r0 == 0) goto L_0x0025;	 Catch:{ Exception -> 0x0026 }
    L_0x0009:
        r2 = "getScript";	 Catch:{ Exception -> 0x0026 }
        r3 = 1;	 Catch:{ Exception -> 0x0026 }
        r3 = new java.lang.Class[r3];	 Catch:{ Exception -> 0x0026 }
        r4 = 0;	 Catch:{ Exception -> 0x0026 }
        r5 = java.lang.String.class;	 Catch:{ Exception -> 0x0026 }
        r3[r4] = r5;	 Catch:{ Exception -> 0x0026 }
        sGetScriptMethod = r2;	 Catch:{ Exception -> 0x0026 }
        r2 = "addLikelySubtags";	 Catch:{ Exception -> 0x0026 }
        r3 = 1;	 Catch:{ Exception -> 0x0026 }
        r3 = new java.lang.Class[r3];	 Catch:{ Exception -> 0x0026 }
        r4 = 0;	 Catch:{ Exception -> 0x0026 }
        r5 = java.lang.String.class;	 Catch:{ Exception -> 0x0026 }
        r3[r4] = r5;	 Catch:{ Exception -> 0x0026 }
        sAddLikelySubtagsMethod = r2;	 Catch:{ Exception -> 0x0026 }
    L_0x0026:
        r1 = move-exception;
        sGetScriptMethod = r6;
        sAddLikelySubtagsMethod = r6;
        r2 = "ICUCompatIcs";
        android.util.Log.w(r2, r1);
        goto L_0x0025;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.text.ICUCompatIcs.<clinit>():void");
    }

    ICUCompatIcs() {
    }

    public static String maximizeAndGetScript(Locale locale) {
        String localeWithSubtags = addLikelySubtags(locale);
        if (localeWithSubtags != null) {
            return getScript(localeWithSubtags);
        }
        return null;
    }

    private static String getScript(String localeStr) {
        try {
            if (sGetScriptMethod != null) {
                return (String) sGetScriptMethod.invoke(null, new Object[]{localeStr});
            }
        } catch (IllegalAccessException e) {
            Log.w(TAG, e);
        } catch (InvocationTargetException e2) {
            Log.w(TAG, e2);
        }
        return null;
    }

    private static String addLikelySubtags(Locale locale) {
        String localeStr = locale.toString();
        try {
            if (sAddLikelySubtagsMethod != null) {
                return (String) sAddLikelySubtagsMethod.invoke(null, new Object[]{localeStr});
            }
        } catch (IllegalAccessException e) {
            Log.w(TAG, e);
        } catch (InvocationTargetException e2) {
            Log.w(TAG, e2);
        }
        return localeStr;
    }
}
