package com.fanny.washheadpad.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Fanny on 18/5/29.
 */

public class IPAddressUtil {

    public static String getIp(Context context) {
        StringBuilder sb = new StringBuilder();
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

//        NetworkInfo.State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
//        if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
//            ip = getLocalIpAddress();
//        }
        if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();

            sb.append(ipAddress & 0xFF).append('.').append
                    ((ipAddress >> 8) & 0xFF).append(".").append
                    ((ipAddress >> 16) & 0xFF).append(".").append
                    ((ipAddress >> 24) & 0xFF);

            Log.e("iputil", ipAddress + "");
            Log.e("iputil", "1:" + (ipAddress & 0xFF));
            Log.e("iputil", "2:" + ((ipAddress >> 8) & 0xFF));
            Log.e("iputil", "3:" + ((ipAddress >> 16) & 0xFF));
            Log.e("iputil", "4:" + ((ipAddress >> 24) & 0xFF));
            Log.e("iputil", sb + "");

        }
        return sb.toString();
    }

    private static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAdress = intf.getInetAddresses(); enumIpAdress.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAdress.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof InetAddress) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return null;
    }
}
