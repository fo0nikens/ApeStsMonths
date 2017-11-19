package android.support.v4.text;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Locale;

public class TextUtilsCompat {
    private static String ARAB_SCRIPT_SUBTAG = "Arab";
    private static String HEBR_SCRIPT_SUBTAG = "Hebr";
    private static final TextUtilsCompatImpl IMPL;
    public static final Locale ROOT;

    private static class TextUtilsCompatImpl {
        private TextUtilsCompatImpl() {
        }

        @NonNull
        public String htmlEncode(@NonNull String s) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                switch (c) {
                    case '\"':
                        sb.append("&quot;");
                        break;
                    case '&':
                        sb.append("&amp;");
                        break;
                    case '\'':
                        sb.append("&#39;");
                        break;
                    case '<':
                        sb.append("&lt;");
                        break;
                    case '>':
                        sb.append("&gt;");
                        break;
                    default:
                        sb.append(c);
                        break;
                }
            }
            return sb.toString();
        }

        public int getLayoutDirectionFromLocale(@Nullable Locale locale) {
            if (!(locale == null || locale.equals(TextUtilsCompat.ROOT))) {
                String scriptSubtag = ICUCompat.maximizeAndGetScript(locale);
                if (scriptSubtag == null) {
                    return getLayoutDirectionFromFirstChar(locale);
                }
                if (scriptSubtag.equalsIgnoreCase(TextUtilsCompat.ARAB_SCRIPT_SUBTAG) || scriptSubtag.equalsIgnoreCase(TextUtilsCompat.HEBR_SCRIPT_SUBTAG)) {
                    return 1;
                }
            }
            return 0;
        }

        private static int getLayoutDirectionFromFirstChar(@NonNull Locale locale) {
            switch (Character.getDirectionality(locale.getDisplayName(locale).charAt(0))) {
                case (byte) 1:
                case (byte) 2:
                    return 1;
                default:
                    return 0;
            }
        }
    }

    private static class TextUtilsCompatJellybeanMr1Impl extends TextUtilsCompatImpl {
        private TextUtilsCompatJellybeanMr1Impl() {
            super();
        }

        @NonNull
        public String htmlEncode(@NonNull String s) {
            return TextUtilsCompatJellybeanMr1.htmlEncode(s);
        }

        public int getLayoutDirectionFromLocale(@Nullable Locale locale) {
            return TextUtilsCompatJellybeanMr1.getLayoutDirectionFromLocale(locale);
        }
    }

    static {
        /* JADX: method processing error */
/*
Error: java.lang.OutOfMemoryError: Java heap space
*/
        /*
        r2 = 0;
        r0 = android.os.Build.VERSION.SDK_INT;
        r1 = 17;
        if (r0 < r1) goto L_0x0022;
    L_0x0007:
        r1 = new android.support.v4.text.TextUtilsCompat$TextUtilsCompatJellybeanMr1Impl;
        r1.<init>();
        IMPL = r1;
    L_0x000e:
        r1 = new java.util.Locale;
        r2 = "";
        r3 = "";
        r1.<init>(r2, r3);
        ROOT = r1;
        r1 = "Arab";
        ARAB_SCRIPT_SUBTAG = r1;
        r1 = "Hebr";
        HEBR_SCRIPT_SUBTAG = r1;
    L_0x0022:
        r1 = new android.support.v4.text.TextUtilsCompat$TextUtilsCompatImpl;
        r1.<init>();
        IMPL = r1;
        goto L_0x000e;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.text.TextUtilsCompat.<clinit>():void");
    }

    @NonNull
    public static String htmlEncode(@NonNull String s) {
        return IMPL.htmlEncode(s);
    }

    public static int getLayoutDirectionFromLocale(@Nullable Locale locale) {
        return IMPL.getLayoutDirectionFromLocale(locale);
    }
}
