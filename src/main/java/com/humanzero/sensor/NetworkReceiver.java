package com.humanzero.sensor;

import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.receiver.Receiver;
import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;

import java.io.EOFException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

/**
 * Custom Receiver that receives the data from all network interfaces
 */

public class NetworkReceiver extends Receiver<Packet> {

    NetworkReceiver(){
        super(StorageLevel.MEMORY_AND_DISK_2());
    }

    @Override
    public void onStart() {
        new Thread(() -> {
            try {
                receive();
            } catch (PcapNativeException | EOFException | TimeoutException | NotOpenException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onStop() {
    }

    private static PcapHandle openPacketHandler(PcapNetworkInterface networkInterface) throws PcapNativeException {

        int snapshotLength = 65536;
        int timeout = 10;

        return networkInterface
                .openLive(snapshotLength, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, timeout);
    }

    private void receive() throws PcapNativeException, EOFException, TimeoutException, NotOpenException {

        PcapHandle packetHandler = openPacketHandler(Pcaps.getDevByName("any"));

            while(Objects.requireNonNull(packetHandler).getNextPacketEx()!=null){
                    store(packetHandler.getNextPacketEx());
            }

    }
}
