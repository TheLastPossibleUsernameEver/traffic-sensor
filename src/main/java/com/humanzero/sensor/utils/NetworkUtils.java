package com.humanzero.sensor.utils;

import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.util.NifSelector;

import java.io.IOException;

public class NetworkUtils {

    public static void selectInterface(PcapNetworkInterface networkInterface){
        try {
            networkInterface = new NifSelector().selectNetworkInterface();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
