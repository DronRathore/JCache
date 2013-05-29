/*
	MemCache Module
	Using HashMap
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
private memEnteries mDB;
private int CurrentThreads=0;
	Listener(int port){
		try{
			server=new ServerSocket(port);
		}catch(Exception e){
			System.out.println("Exception:\nUnable to bind socket at "+port+"\n"+e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("+ Server Started at port "+port+"\n+ q = quit\n+ s = print stats\n+ t = print working threads\n+ d = save cache enteries on a local file");
		keyListener keylistener=new keyListener(this);
		new Thread(keylistener);
		try{
			mDB=new memEnteries();
			while(true){
				socket=server.accept();
				mDB.addThread((++CurrentThreads),socket);
				new Thread(new accessBoy(socket,mDB,CurrentThreads));
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
		
		System.out.println("\nDatabase Stats:\n------------------------------------------------------");
		System.out.println("Enteries: "+mDB.getCounter());
		System.out.println("Working Threads: "+mDB.getCurrentThreads()+" working threads");
	}
	public void saveToLocal(){
	
	}
	public void printThreads(){
		System.out.println("\nCurrent Threads:\n------------------------------------------------------");
		Iterator iter=mDB.getThreads().values().iterator();
		while(iter.hasNext()){
			System.out.println(iter.next());
		}
		System.out.println("\nTotal: "+mDB.getCurrentThreads()+" working threads");
	}
}
/*
	The Main DB Enteries
*/
class memEnteries{
private HashMap<Integer,String> Database;
private HashMap<Integer,String> Threads;
private int Counter;
private int CurrentThreads=0;
private String value;
		memEnteries(){
			Database=new HashMap<Integer,String>();
			Threads=new HashMap<Integer,String>();
		}
		public HashMap getThreads(){
			return this.Threads;
		}
		public int getCounter(){
			return this.Counter;
		}
		public int getCurrentThreads(){
			return this.CurrentThreads;
		}
		synchronized public boolean add(Integer hash,String value){
			try{
				Database.put(hash,value);
				Counter++;
				return true;
			}catch(Exception e){
				return false;
			}
		}
		synchronized public boolean remove(Integer hash){
			try{
				Database.remove(hash);
				Counter--;
				return true;
			}catch(Exception e){
				return false;
			}
		}
		synchronized public String get(Integer hash){
			try{
				value=Database.get(hash);
				return value;
			}catch(Exception e){
				return "";
			}
		}
		synchronized public void addThread(Integer hash,Socket client){
			Threads.put(hash,client.getInetAddress().getHostAddress()+" : "+client.getInetAddress().getHostName());
			CurrentThreads++;
		}
		synchronized public void removeThread(Integer hash){
			Threads.remove(hash);
			CurrentThreads--;
		}
}
/*
	User Authentication and MemCache Worker
*/
class accessBoy extends Thread{
private Socket client;
private memEnteries memDB;
private String command;
private int ThreadID;
private boolean isLoggedIn;
	public accessBoy(Socket c,memEnteries me,int ThreadID){
		this.client=c;
		this.memDB=me;
		this.ThreadID=ThreadID;
		start();
	}
	synchronized public void run(){
		try{
			BufferedReader bf=new BufferedReader(new InputStreamReader(client.getInputStream()));
			command=bf.readLine();
		}catch(Exception e){
			this.memDB.removeThread(this.ThreadID);
		}
		this.memDB.removeThread(this.ThreadID);
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
			if(input.equals("q")||input.equals("quit")||input.equals("exit")){
				listener.quit();
				System.exit(0);
			}
			if(input.equals("s")){
				listener.printStats();
			}
			if(input.equals("d")){
				listener.saveToLocal();
			}
			if(input.equals("t")){
				listener.printThreads();
			}
		}
		}catch(Exception e){
		System.out.println("+ Ctrl+C arrived, closing Server");
		listener.quit();
		System.exit(0);
		}
	}
}