package com.humanzero.sensor;

import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.receiver.Receiver;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.NifSelector;

import java.util.Objects;

/**
 * Custom Receiver that receives the data from network interfaces
 *
 */

public class NetworkReceiver extends Receiver<Packet> {

    NetworkReceiver(){
        super(StorageLevel.MEMORY_AND_DISK_2());
    }

    @Override
    public void onStart() {
        new Thread(this::receive).start();
    }

    @Override
    public void onStop() {

    }

    private void receive(){

        PcapNetworkInterface networkInterface = null;

        try {
            networkInterface = new NifSelector().selectNetworkInterface();
        } catch (Exception e) {
            e.printStackTrace();
        }

        PcapHandle packetHandler;
        int snapshotLength = 65536;
        int timeout = 10;

        try {

            packetHandler = Objects
                    .requireNonNull(networkInterface)
                    .openLive(snapshotLength, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, timeout);

            while(packetHandler.getNextPacketEx()!=null){

                    store(packetHandler.getNextPacketEx());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
