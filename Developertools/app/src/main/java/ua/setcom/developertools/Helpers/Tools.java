package ua.setcom.developertools.Helpers;

import android.util.DisplayMetrics;
import android.util.Log;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * Created by daniil on 10/23/14.
 */
public class Tools {

    public static int toPixels(DisplayMetrics dm, float dps) {
        final float scale = dm.density;
        return (int) (dps * scale + 0.5f);
    }

    public static String getLocalIpv4Address() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        if (isIPv4) {
                            return sAddr;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
