package com.humanzero.sensor;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
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

	private static final SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("traffic-sensor");

	private static final Logger logger = LoggerFactory.getLogger(App.class);

	private static PcapNetworkInterface networkInterface = null;

	private static List<Byte[]> packetList = new ArrayList<>();

	private static void selectInterface(){
		try {
			networkInterface = new NifSelector().selectNetworkInterface();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void collectPackets(){
		try {
			int snapshotLength = 65536;
			int timeout = 10;
			PcapHandle packageHandler = networkInterface.openLive(snapshotLength, PromiscuousMode.PROMISCUOUS, timeout);

			new Thread(()->{
				try {
					while (true) {
						Packet packet = packageHandler.getNextPacketEx();
						logger.info(packageHandler.getTimestamp().toString() + " Header is: " +
								packet.getHeader() + " Payload is: " +
								packet.getPayload());

						//converts raw packet data to Byte[] and adds it to packetList
						packetList.add(ArrayUtils.toObject(packet.getRawData()));

						logger.info("Captured: " + packetList.size() + " packets." );
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}).start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		selectInterface();
		collectPackets();

		JavaSparkContext sparkContext = new JavaSparkContext(conf);

//		This line of code may be useful later
//		JavaStreamingContext javaStreamingContext = new JavaStreamingContext(sparkContext, Durations.seconds(1));

		JavaRDD<Byte[]> rawDataRDD = sparkContext
				.parallelize(packetList);

//				while (true){
//					Thread.sleep(300000);
//
//					rawDataRDD
//				}

	}
}
