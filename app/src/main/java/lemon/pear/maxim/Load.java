package lemon.pear.maxim;

/**
 * 加载JNI类
 */

public class Load {
    public native String stringFromJNI();

    static {
        System.loadLibrary("native-lib");
    }
}
