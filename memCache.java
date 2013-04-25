/*
	MemCache Module
	Authour: Dron Rathore [dron.rathore@gmail.com], Mohit Aggarwal[programmer.mohit@gmail.com]
*/
import java.net.*;
import java.io.*;
import java.util.*;

class memCache{
private int port;
	public memCache(String args[]){
		if(args.length>0){
			try{
				if(args[0].equals("-p"))
				this.port=Integer.parseInt(args[1]);
				else
				this.port=1008;
			}catch(Exception e){
				this.port=1008;
			}
		}else{
			this.port=1008;
		}
		Listener listener=new Listener(port);
	}
	public static void main(String[] args){
		System.out.println("* JCache Server\n* Version 0.1\n* Authour: Dron Rathore, Mohit Aggarwal");
		memCache mc=new memCache(args);
	}
}
/*
	Main Server Class
*/
class Listener{
private Socket socket;
private ServerSocket server;
	Listener(int port){
		try{
			server=new ServerSocket(port);
		}catch(Exception e){
			System.out.println("Exception:\nUnable to bind socket at "+port+"\n"+e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("+ Server Started at port "+port+"\n+ Type q to quit, s to print stats, d to save cache enteries on a local file");
		keyListener keylistener=new keyListener(this);
		new Thread(keylistener);
		try{
			while(true){
				socket=server.accept();
				
			}
		}catch(Exception e){
			System.out.println("- Server Shutted down on user command");
		}
	}
	public void quit(){
		try{
			server.close();
		}catch(Exception e){
			System.out.println("Exception:\nUnable to properly close the socket\n"+e.getMessage());
			e.printStackTrace();
		}
	}
	public void printStats(){
	
	}
	public void saveToLocal(){
	
	}
}
/*
	The Main DB Enteries
*/
class memEnteries{
private HashMap<Integer,String> Database;
private int Counter;
		memEnteries(){
			Database=new HashMap<Integer,String>();
			}
		synchronized public boolean add(String value){
			return false;
		} 
}
/*
	User Authentication responsible
*/
class accessBoy extends Thread{
private Socket client;
private memEnteries memDB;
	public accessBoy(Socket c,memEnteries me){
		this.client=c;
		this.memDB=me;
		start();
	}
	synchronized public void run(){
		try{
			BufferedReader bf=new BufferedReader(new InputStreamReader(client.getInputStream())) ;
		}catch(Exception e){
		
		}
	} 
}
/*
	Thread Listener for server console commands
*/
class keyListener extends Thread{
Listener listener;
	public keyListener(Listener listener){
		this.listener=listener;
		start();
	}
	synchronized public void run(){
		try{
		BufferedReader bf=new BufferedReader(new InputStreamReader(System.in));
		while(true){
			String input=bf.readLine();
			if(input.equals("q")){
				listener.quit();
				System.exit(0);
			}
			if(input.equals("s")){
				listener.printStats();
			}
			if(input.equals("d")){
				listener.saveToLocal();
			}
		}
	}catch(Exception e){
		System.out.println("+ Ctrl+C arrived, closing Server");
		listener.quit();
		System.exit(0);
	}
	}
}
