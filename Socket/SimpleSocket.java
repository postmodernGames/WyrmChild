package Socket;

import java.net.*;
import java.io.*;
import javax.net.ssl.*;


public class SimpleSocket {
	
	public static void main(String[] args) {
		try {
			communicate("toronto.kuali.co","/api/cm/courses/", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjU4ZGU5YzcwNzZkMmExNDEzNmE1YTc1NyIsImlzcyI6Imt1YWxpLmNvIiwiZXhwIjoxNTIyNTIwMDQ4LCJpYXQiOjE0OTA5ODQwNDh9.m4AvpBspEKlUSVMvjUTKHmqOPJJqtu0t9zkNgRqi_EY");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String readLine(InputStream in){
		String buf ="";
		byte b;
		try {
			do{
				b = (byte)in.read();
				buf += (char)b;
			}while(b!=10  && !buf.equals("\r"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buf;
		
	}
	
	public static void communicate(String address, String function, String key) throws UnknownHostException, IOException{
		SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
		
		    
		try(Socket s = ssf.createSocket(address,443)){
			
			PrintWriter pw = new PrintWriter(s.getOutputStream());
			pw.println("GET " + function + " HTTP/1.1");
			pw.println("Host: " + address);
			pw.println("Content-Type: application/json");
			pw.println("Authorization: Bearer " +key); 

			
			/*toronto-stg.kuali.co*/ //eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjU4NzdhY2E2YWFjODQ5MGJkNTU3MDY2MiIsImlzcyI6Imt1YWxpLmNvIiwiZXhwIjoxNTE1NzczOTkwLCJpYXQiOjE0ODQyMzc5OTB9.TTLFeBOvsrT7QYcIzjF8IHwmUFHUapb4iLKMcN3uPMU");
			/*toronto.kuali.co*/ //		pw.println("Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjU4NzdhYmM4MzczNmE1NDk1ZmE0OGM1ZCIsImlzcyI6Imt1YWxpLmNvIiwiZXhwIjoxNTE1NzczNzY4LCJpYXQiOjE0ODQyMzc3Njh9.65BGORbVXQHX-d4OXiXYSjsN34NT7WYkhSnTRsxpvKc");
			pw.println("Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjU4ZGU5YzcwNzZkMmExNDEzNmE1YTc1NyIsImlzcyI6Imt1YWxpLmNvIiwiZXhwIjoxNTIyNTIwMDQ4LCJpYXQiOjE0OTA5ODQwNDh9.m4AvpBspEKlUSVMvjUTKHmqOPJJqtu0t9zkNgRqi_EY");
			/*toronto-sbx.kuali.co*/	//	pw.println("Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjU4NTA2ZDc4ZjM4YmQyNmZhMWNjMjE5MyIsImlzcyI6Imt1YWxpLmNvIiwiZXhwIjoxNTEzMjAxOTEyLCJpYXQiOjE0ODE2NjU5MTJ9.VK7NvKDCtNI4d-CDcptSBA4J9kS7k8iToGJdbiSHS3o");
			
			pw.println("");
			
			pw.flush();
			InputStream inS = s.getInputStream();
				
			byte t = 0;
			int z;
			String str = new String();
			boolean state1 = true;
			boolean state2 = false;
			boolean state3 = false;
			int contentLength = 0;
			
			
			
			
		    PrintWriter writer = new PrintWriter("socketOut.2.txt", "UTF-8");
		    try{
		    while(true) {
		    	if(state1){
		    		str = readLine(inS);
		    	//	System.out.print(str);
		    		if(str.length() > "Content-Length:".length()  && str.substring(0, "Content-Length:".length()).equals("Content-Length:")){
		    			contentLength = Integer.parseInt(str.trim().substring("Content-Length:".length()+1));			
		    		}
		    		if(str.length() ==0 || str.equals("\n") || str.equals("\r")){
		    			state3 = true;
		    			state1=false;
		    		}
		    	}
		    	//state3 indicates we've passed the headers
		    	if(state3){
		    		if(contentLength<=0) break;
		    		z = inS.read();
			    	if(z==-1){
			    		break;
			    	}
			    	
			    	contentLength--;
			    	t = (byte)z;
	//	    		System.out.print((char)t);
		    		writer.print((char)t);
		    	}

		    }
		    } catch(Exception e){
		    	writer.flush();
		    	writer.close();
		 
		    }
			writer.flush();
			writer.close();
	 
		}
	}
}
