
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
	static List<PeerInformation> curPeer;
	static List<RFC> rfc;
	Socket clientSocket;
	int peerCount = 0;
	static Date currentDataTime = new Date();
	static Map<String, Integer> hostToPort = new HashMap<String, Integer>();

	public static void main(String[] args) throws IOException{
		ServerSocket serverSocket;
		curPeer = new ArrayList<>();
		rfc = new ArrayList<>();
		try {
			serverSocket = new ServerSocket(Util.SEVER_PORT);
			System.out.println("Sever is running. To exit press ctrl + c or ctrl + z");
			System.out.println("Sever Host Name : " + java.net.InetAddress.getLocalHost().getHostName());
			System.out.println("Sever IP address : " + java.net.InetAddress.getLocalHost().getHostAddress());
			while(true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("a client connects to the server");
				ProcessClient newClient = new ProcessClient(clientSocket, rfc, curPeer);
				newClient.start();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}

class ProcessClient extends Thread {
	List<PeerInformation> curPeer;
	List<RFC> rfc;
	Socket clientSocket;
	DataInputStream dis = null;
	DataOutputStream dos = null;

	public ProcessClient(Socket clientSock, List<RFC> rfc, List<PeerInformation> curPeer) {
		this.clientSocket = clientSock;
		this.curPeer = curPeer;
		this.rfc = rfc;
	}

	public void run() {
		try {
			dis = new DataInputStream(clientSocket.getInputStream());
			dos = new DataOutputStream(clientSocket.getOutputStream()) ;
			while(true) {
				try {
					//if(dis.readUTF() != null || dis.readUTF().length() > 0) {
					String receive = dis.readUTF();
					System.out.println("receive request : \n" + receive);
					System.out.println("");
					//System.out.println("1");
					String[] request = receive.split(Util.CR + Util.LF);
					//System.out.println("1");
					String[] information = request[0].split(" ");
					//System.out.println("1");
					//uploaderHostName,uploaderPort
					String uploaderHostName = request[1].split(" ")[1];
					//System.out.println("1");
					int uploaderPort = Integer.parseInt(request[2].split(" ")[1]);
					//System.out.println("1");
					int rfcNo = 0;
					//System.out.println("1");
					String rfcTitle = "";
					switch(information[0]) {
						case "EXIT":
							String responseExit = "Client Exited" + Util.CR + Util.LF;
							responseExit = responseExit + "Data" + " " + new Date() + Util.CR + Util.LF;
							responseExit = responseExit + "OS:" + " " + Util.OS + Util.CR + Util.LF;
							dos.writeUTF(responseExit);
							//remove
							for(int i = 0; i < rfc.size(); i++) {
								if(rfc.get(i).hostName.equals(uploaderHostName) && rfc.get(i).port == uploaderPort) {
									rfc.remove(i);
									i--;
								}
							}
							for(int i = 0; i < curPeer.size(); i++) {
								if(curPeer.get(i).hostName.equals(uploaderHostName) && curPeer.get(i).portNumber == uploaderPort) {
									curPeer.remove(i);
									i--;
								}
							}
							System.out.println("Server send the response: \n" +  responseExit);
							System.out.println("");
							break;
						case "ADD" :
							rfcTitle = request[3].split(":")[1];
							rfcNo = Integer.parseInt(information[2]);
							String responseADD = Util.VERSION + " " + 200 + " " + "OK" + Util.CR + Util.LF;
							responseADD = responseADD + "RFC" + " " + rfcNo + " " + rfcTitle + " " + uploaderHostName + " " + uploaderPort + Util.CR + Util.LF;
							dos.writeUTF(responseADD);
							RFC newRfc = new RFC(rfcNo, rfcTitle, uploaderHostName, uploaderPort);
							rfc.add(newRfc);
							int flag = 0;
							for(int i = 0; i < curPeer.size(); i++) {
								if(curPeer.get(i).hostName.equals(uploaderHostName) && curPeer.get(i).portNumber == uploaderPort) {
									flag = 1;
								}
							}
							if(flag != 1) {
								curPeer.add(new PeerInformation(uploaderHostName, uploaderPort, true));
							}
							System.out.println(rfc.size() + "Server send the response: \n" +  responseADD);
							System.out.println("");
							break;
						case "LOOKUP" :
							rfcNo = Integer.parseInt(information[2]);
							List<RFC> fit = new ArrayList<RFC>();
							String fitTitle = "";
							for(RFC cur : rfc) {
								if(cur.rfcNo == rfcNo) {
									fit.add(cur);
								}
							}
							if(fit.size() > 0) {
								String responseLookup = Util.VERSION + " " + 200 + " " + "OK" + Util.CR + Util.LF;
								for(int i = 0; i < fit.size(); i++) {
									responseLookup = responseLookup + "RFC" + " " + rfcNo + " " + fit.get(i).rfcTitle + " " + fit.get(i).hostName + " " + fit.get(i).port + Util.CR + Util.LF;
								}
								dos.writeUTF(responseLookup);
								System.out.println("Server send the response: \n" +  responseLookup);
								System.out.println("");
							}else {
								String responseLookup = Util.VERSION + " " + 404 + " " + "Not Found" + Util.CR + Util.LF;
								dos.writeUTF(responseLookup);
								System.out.println("Server send the response: " +  responseLookup);
								System.out.println("");
							}
							break;
						case "LIST_ALL" :
							System.out.println("here is list all");
							if(rfc != null && rfc.size() > 0) {
								//System.out.println("you" + rfc.size());
								String responseAll = Util.VERSION + " " + 200 + " " + "OK" + Util.CR + Util.LF;
								for(RFC cur : rfc) {
									responseAll = responseAll + "RFC" + " " + cur.rfcNo + " " + " " + cur.rfcTitle + " " + cur.hostName + " " + cur.port + Util.CR + Util.LF;
								}
								dos.writeUTF(responseAll);
								System.out.println("Server send the response: \n" +  responseAll);
								System.out.println("");
							}else {
								//System.out.println("mei");
								String responseAll = Util.VERSION + " " + 404 + " " + "NOT FOUND" + Util.CR + Util.LF;
								dos.writeUTF(responseAll);
								System.out.println("Server send the response:\n" +  responseAll);
								System.out.println("");
							}
							break;
						default:
							break;
					}
				//}
				}catch (EOFException e) {
					System.out.println("One client disconnect");
					break;
				} catch(Exception e) {
					e.printStackTrace();
					break;
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
