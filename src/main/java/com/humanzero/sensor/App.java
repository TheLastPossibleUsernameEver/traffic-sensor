package com.humanzero.sensor;

import java.io.IOException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import java.util.List;
import org.pcap4j.core.Pcaps;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Captures one package from each network interface
 *
 */
public class App {

    public static void main( String[] args ) throws PcapNativeException{

	//private static final Logger logger = LoggerFactory.getLogger(App.class); will be implemented later

        List<PcapNetworkInterface> networkInterfaceList = Pcaps.findAllDevs();

	int snapLen = 65536;
	PromiscuousMode mode = PromiscuousMode.PROMISCUOUS;
	int timeout = 10;

	    for (PcapNetworkInterface networkInterface : 
	    		networkInterfaceList){
			   PcapHandle handle = networkInterface.openLive(snapLen, mode, timeout);
				
			   Packet packet = handle.getNextPacketEx();
			   //will be implemented with logger
			   System.out.println("Header is: " + 
					       packet.getHeader() + 
					       " : " + 
					       "Length is" + 
					       packet.length());
			   handle.close();
		}
    }
}
