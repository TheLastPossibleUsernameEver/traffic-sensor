package com.humanzero.sensor;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.pcap4j.packet.Packet;

import java.util.Arrays;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * Ca
 *
 */

public class App {

	private static final SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("traffic-sensor");

	private static final int BATCH_INTERVAL = 15;

	private static Logger logger = Logger.getRootLogger();

	public static void main(String[] args) throws InterruptedException{

		logger.setLevel(Level.WARN);

		JavaStreamingContext streamingContext = new JavaStreamingContext(conf, Durations.seconds(BATCH_INTERVAL));

		JavaDStream<Packet> networkReceiverStream = streamingContext.receiverStream(new NetworkReceiver());

		JavaDStream<Byte> byteFlow = networkReceiverStream
				.map(packet -> ArrayUtils.toObject(packet.getRawData()))
				.flatMap(x -> Arrays.asList(x).iterator());

		byteFlow.count().print();

		streamingContext.start();
		streamingContext.awaitTermination();

	}
}
