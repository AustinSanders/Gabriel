package c2demo.mcast;

// Import some needed classes
import java.net.*;

public class MulticastSender{
	
	public static void main(String[] args){
		// Which port should we send to
		int port = 5000;
		// Which address
		String group = "225.4.5.6";
		// Which ttl
		int ttl = 15;
		
		try{
			// Create the socket but we don't bind it as we are only going to send data
			MulticastSocket s = new MulticastSocket();
			s.setTimeToLive(8);
			// Note that we don't have to join the multicast group if we are only
			// sending data and not receiving
			
			// Fill the buffer with some data
			byte buf[] = new byte[10];
			for (int i=0; i<buf.length; i++) buf[i] = (byte)i;
			// Create a DatagramPacket 
			DatagramPacket pack = new DatagramPacket(buf, buf.length,
				InetAddress.getByName(group), port);
			// Do a send. Note that send takes a byte for the ttl and not an int.
			s.send(pack);
			
			// And when we have finished sending data close the socket
			s.close();
		}
		catch(Exception e){
			e.printStackTrace();
			return;
		}
	}
}
