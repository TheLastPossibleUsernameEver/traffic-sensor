package com.humanzero.sensor;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.NifSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Ca
 *
 */

public class App {

	private static final Logger logger = LoggerFactory.getLogger(App.class);

	private static PcapNetworkInterface networkInterface = null;

	private static List<Packet> packetList = new ArrayList<>();

	private static void collectPackets(){
		try {
			int snapshotLength = 65536;
			int timeout = 10;
			PcapHandle packageHandler = networkInterface.openLive(snapshotLength, PromiscuousMode.PROMISCUOUS, timeout);

			new Thread(()->{
				try {
					while (true) {
						Packet packet = packageHandler.getNextPacketEx();
						System.out.println(packageHandler.getTimestamp().toString() + " Header is: " +
								packet.getHeader() + " Payload is: " +
								packet.getPayload());
						packetList.add(packet);

						logger.info("Captured: " + packetList.size() + " packets." );
					}
				} catch (Exception e) {

				}

			}).start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("traffic-sensor");
		JavaStreamingContext javaStreamingContext = new JavaStreamingContext(conf, Durations.seconds(1));

		try {
			networkInterface = new NifSelector().selectNetworkInterface();
		} catch (IOException e) {
			e.printStackTrace();
		}

		collectPackets();

	}
}
