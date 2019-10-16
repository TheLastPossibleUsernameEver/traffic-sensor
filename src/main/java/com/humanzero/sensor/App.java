package com.humanzero.sensor;

import com.humanzero.sensor.utils.NetworkUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Ca
 *
 */

public class App {

	private static final SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("traffic-sensor");

	private static final Logger logger = LoggerFactory.getLogger(App.class);

	private static PcapNetworkInterface networkInterface = null;

	private static LinkedBlockingQueue<Packet> packetQueue = new LinkedBlockingQueue<>();

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

						packetQueue.add(packet);

//						logger.info("Captured: " + packetQueue.size() + " packets." );
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}).start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws InterruptedException {

		NetworkUtils.selectInterface(networkInterface);
		collectPackets();

		JavaSparkContext sparkContext = new JavaSparkContext(conf);

//		This line of code may be useful later
		JavaStreamingContext streamingContext = new JavaStreamingContext(sparkContext, Durations.seconds(1));


		List<Packet> list = new LinkedList<>();

		JavaRDD<Packet> rawDataRDD;

				while (true){

					packetQueue.drainTo(list);

					rawDataRDD = sparkContext.parallelize(list);

					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					Queue<JavaRDD<Packet>> RDDqueue  = new LinkedList<>();
					RDDqueue.add(rawDataRDD);

					JavaDStream dStream = streamingContext.queueStream(RDDqueue);
					dStream.print();


					streamingContext.start();
					streamingContext.awaitTermination();

					logger.info("Got " + ""  + "total amount of data");
				}

	}
}
