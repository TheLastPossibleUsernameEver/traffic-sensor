package com.humanzero.sensor;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Ca
 *
 */

public class App {

	private static final SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("traffic-sensor");
	private static final int BATCH_INTERVAL = 15;
	private static Logger logger = Logger.getRootLogger();
	private static String kafkaTopic = "alerts";
	private static String kafkaMessage = "You have a new alert!";
	private static Map<String, Object> kafkaParams = new HashMap<>();
	private static long minLimit = 1073741824;
	private static long maxLimit = 1024;
	private static JavaStreamingContext streamingContext =
			new JavaStreamingContext(conf, Durations.seconds(BATCH_INTERVAL));

	private static void bootstrapKafka(){
		kafkaParams.put("bootstrap.servers", "localhost:9092");
		kafkaParams.put("key.serializer", StringSerializer.class);
		kafkaParams.put("value.serializer", StringSerializer.class);
	}

//	private static void bootstrapHive(){
//
//	}

	public static void main(String[] args) throws InterruptedException{
		logger.setLevel(Level.WARN);

		bootstrapKafka();

		Producer<String, String> kafkaProducer = new KafkaProducer<>(kafkaParams);


		JavaDStream<Long> byteFlow = streamingContext
				.receiverStream(new NetworkReceiver())
				.map(packet -> ArrayUtils.toObject(packet.getRawData()))
				.flatMap(x -> Arrays.asList(x).iterator())
				.count();

		byteFlow.print();

		byteFlow.foreachRDD(rdd -> {
			if (rdd.count() < minLimit || rdd.count() > maxLimit){
				kafkaProducer.send(new ProducerRecord<>(kafkaTopic, kafkaMessage));
				kafkaProducer.close();
			}
		});

		streamingContext.start();
		streamingContext.awaitTermination();

	}
}
