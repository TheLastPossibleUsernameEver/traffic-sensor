package com.humanzero.sensor;

import org.pcap4j.core.*;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Ca
 *
 */

public class App {

//	private static void capturePackages(){
//		Will be implemented later
//	}

	private static List<PcapNetworkInterface> getNetworkInterfaceList() throws IOException{

		List<PcapNetworkInterface> networkInterfaceList = null;

		try {
			networkInterfaceList = Pcaps.findAllDevs();
		} catch (PcapNativeException e){
			e.printStackTrace();
		}

		if (networkInterfaceList == null || networkInterfaceList.isEmpty()){
			throw new IOException("No interfaces to capture");
		}

		return networkInterfaceList;
	}

    public static void main( String[] args ){

			final Logger logger = LoggerFactory.getLogger(App.class);

			List<PcapNetworkInterface> networkInterfaceList = null;

			try {
				networkInterfaceList = App.getNetworkInterfaceList();
			} catch (IOException e){
				e.printStackTrace();
			}

			int snapLen = 65536;
			PromiscuousMode mode = PromiscuousMode.PROMISCUOUS;
			int timeout = 10;

	    	for (PcapNetworkInterface networkInterface :
					networkInterfaceList
					){

	    				new Thread(()-> {

	    					PcapHandle packageHandler = null;

							try {
								packageHandler = networkInterface.openLive(snapLen, mode, timeout);
							} catch (PcapNativeException e) {
								e.printStackTrace();
							}

							Packet packet = null;
							try {
								if (packageHandler != null) {
									packet = packageHandler.getNextPacketEx();
								}
							} catch (PcapNativeException | TimeoutException | NotOpenException | EOFException e) {
								e.printStackTrace();
							}

							if (packageHandler != null) {
								if (packet != null) {
									logger.info(packageHandler.getTimestamp() +
											" Header is: " +
											packet.getHeader() +
											" : " +
											"Length is: " +
											packet.length());
								}
							}

							if (packageHandler != null) {
								packageHandler.close();
							}

						}).start();
				}
    }
}
