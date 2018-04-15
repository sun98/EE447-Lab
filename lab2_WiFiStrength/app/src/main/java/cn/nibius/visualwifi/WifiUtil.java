package cn.nibius.visualwifi;

/**
 * Created by Nibius at 2018/4/11 23:07.
 */
public class WifiUtil {
    public static int dbm2Int(int level) {
        if (level > -60) return 4;
        else if (level >= -80) return 3;
        else if (level >= -100) return 2;
        else if (level >= -113) return 1;
        else return 0;
    }
}
