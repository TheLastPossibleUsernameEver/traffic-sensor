package com.humanzero.sensor;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.pcap4j.packet.Packet;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * Ca
 *
 */

public class App {

	private static final SparkConf conf = new SparkConf().setMaster("local[1]").setAppName("traffic-sensor");

//	private static final Logger logger = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) throws InterruptedException{

		JavaStreamingContext streamingContext = new JavaStreamingContext(conf, Durations.seconds(300000));

		JavaDStream<Packet> networkReceiverStream = streamingContext.receiverStream(new NetworkReceiver());

		JavaDStream<byte[]> byteFlow = networkReceiverStream
				.map(Packet::getRawData);

		byteFlow.count().print();

		streamingContext.start();
		streamingContext.awaitTermination();

	}
}
