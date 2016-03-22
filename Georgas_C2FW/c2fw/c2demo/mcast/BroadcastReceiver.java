package c2demo.mcast;

// Import some needed classes
import java.net.*;

public class BroadcastReceiver{
	
	public static void main(String[] args){
		// Which port should we listen to
		int port = 5000;
		// Which address

		try{
			
			// Create the socket and bind it to port 'port'.
			DatagramSocket s = new DatagramSocket(port);
			
			// Create a DatagramPacket and do a receive
			byte buf[] = new byte[1024];
			DatagramPacket pack = new DatagramPacket(buf, buf.length);
			s.receive(pack);
			
			// Finally, let us do something useful with the data we just received,
			// like print it on stdout :-)
			System.out.println("Received data from: " + pack.getAddress().toString() +
				":" + pack.getPort() + " with length: " +
				pack.getLength());
			System.out.write(pack.getData(),0,pack.getLength());
			System.out.println();
			
			// And when we have finished receiving data leave the multicast group and
			// close the socket
			s.close();
		}
		catch(Exception e){
			e.printStackTrace();
			return;
		}
	}
}
