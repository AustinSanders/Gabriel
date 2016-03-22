package c2.util;

import java.net.*;
import java.security.*;
import java.util.*;

public class UIDGenerator{

	private static boolean inited = false;
	
	protected static String machineId;
	protected static Random rnd;
	protected static int counter = 0;
	
	public static void main(String[] args){
		System.out.println(generateUID());
	}
	
	protected static void init(){
		machineId = "[anonymous]";
		try{
			InetAddress localAddress = InetAddress.getLocalHost();
			byte[] byteAddress = localAddress.getAddress();
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < byteAddress.length; i++){
				sb.append(Long.toHexString((long)(byteAddress[i] & 0xff)));
			}
			machineId = sb.toString();
		}
		catch(Exception e){
		}
		//rnd = new SecureRandom(SecureRandom.getSeed(20));
		rnd = new Random();
		inited = true;
	}
	
	public static String generateUID(){
		if(!inited){
			init();
		}
		return generateUID("uid");
	}
	
	public static String generateUID(String prefix){
		if(!inited){
			init();
		}
		return prefix + "." + machineId + "." + Long.toHexString(System.currentTimeMillis()) + "." + Long.toHexString(rnd.nextLong()) + "." + Long.toHexString((counter++));
	}

}

