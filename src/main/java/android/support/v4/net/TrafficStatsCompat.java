package android.support.v4.net;

import java.net.Socket;
import java.net.SocketException;

public class TrafficStatsCompat {
    private static final TrafficStatsCompatImpl IMPL = null;

    interface TrafficStatsCompatImpl {
        void clearThreadStatsTag();

        int getThreadStatsTag();

        void incrementOperationCount(int i);

        void incrementOperationCount(int i, int i2);

        void setThreadStatsTag(int i);

        void tagSocket(Socket socket) throws SocketException;

        void untagSocket(Socket socket) throws SocketException;
    }

    static class BaseTrafficStatsCompatImpl implements TrafficStatsCompatImpl {
        private ThreadLocal<SocketTags> mThreadSocketTags = new ThreadLocal<SocketTags>() {
            protected SocketTags initialValue() {
                return new SocketTags();
            }
        };

        private static class SocketTags {
            public int statsTag;

            private SocketTags() {
                this.statsTag = -1;
            }
        }

        BaseTrafficStatsCompatImpl() {
        }

        public void clearThreadStatsTag() {
            ((SocketTags) this.mThreadSocketTags.get()).statsTag = -1;
        }

        public int getThreadStatsTag() {
            return ((SocketTags) this.mThreadSocketTags.get()).statsTag;
        }

        public void incrementOperationCount(int operationCount) {
        }

        public void incrementOperationCount(int tag, int operationCount) {
        }

        public void setThreadStatsTag(int tag) {
            ((SocketTags) this.mThreadSocketTags.get()).statsTag = tag;
        }

        public void tagSocket(Socket socket) {
        }

        public void untagSocket(Socket socket) {
        }
    }

    static class IcsTrafficStatsCompatImpl implements TrafficStatsCompatImpl {
        IcsTrafficStatsCompatImpl() {
        }

        public void clearThreadStatsTag() {
            TrafficStatsCompatIcs.clearThreadStatsTag();
        }

        public int getThreadStatsTag() {
            return TrafficStatsCompatIcs.getThreadStatsTag();
        }

        public void incrementOperationCount(int operationCount) {
            TrafficStatsCompatIcs.incrementOperationCount(operationCount);
        }

        public void incrementOperationCount(int tag, int operationCount) {
            TrafficStatsCompatIcs.incrementOperationCount(tag, operationCount);
        }

        public void setThreadStatsTag(int tag) {
            TrafficStatsCompatIcs.setThreadStatsTag(tag);
        }

        public void tagSocket(Socket socket) throws SocketException {
            TrafficStatsCompatIcs.tagSocket(socket);
        }

        public void untagSocket(Socket socket) throws SocketException {
            TrafficStatsCompatIcs.untagSocket(socket);
        }
    }

    static {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.support.v4.net.TrafficStatsCompat.<clinit>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:116)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:249)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
Caused by: java.lang.NullPointerException
*/
        /*
        r0 = android.os.Build.VERSION.SDK_INT;
        r1 = 14;
        if (r0 < r1) goto L_0x000e;
    L_0x0006:
        r0 = new android.support.v4.net.TrafficStatsCompat$IcsTrafficStatsCompatImpl;
        r0.<init>();
        IMPL = r0;
    L_0x000e:
        r0 = new android.support.v4.net.TrafficStatsCompat$BaseTrafficStatsCompatImpl;
        r0.<init>();
        IMPL = r0;
        goto L_0x000d;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.net.TrafficStatsCompat.<clinit>():void");
    }

    public static void clearThreadStatsTag() {
        IMPL.clearThreadStatsTag();
    }

    public static int getThreadStatsTag() {
        return IMPL.getThreadStatsTag();
    }

    public static void incrementOperationCount(int operationCount) {
        IMPL.incrementOperationCount(operationCount);
    }

    public static void incrementOperationCount(int tag, int operationCount) {
        IMPL.incrementOperationCount(tag, operationCount);
    }

    public static void setThreadStatsTag(int tag) {
        IMPL.setThreadStatsTag(tag);
    }

    public static void tagSocket(Socket socket) throws SocketException {
        IMPL.tagSocket(socket);
    }

    public static void untagSocket(Socket socket) throws SocketException {
        IMPL.untagSocket(socket);
    }
}
