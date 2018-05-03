import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) throws UnknownHostException, IOException {
		OutputStream out;
		DataOutputStream dos;
		InputStream in;
		DataInputStream dis;
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please enter server IP Address");
		String serveIp = scanner.nextLine();
		Socket clientSocket = new Socket(serveIp, 7734);
		System.out.println("Your Information");
		System.out.println("=========================================");
		System.out.println("Your IP Address : " + java.net.InetAddress.getLocalHost().getHostAddress());
		System.out.println("Your Host Name : " + java.net.InetAddress.getLocalHost().getHostName());
		System.out.println("=========================================");
		String uploaderHostName = java.net.InetAddress.getLocalHost().getHostName();
		System.out.println("Please enter your port");

		int uploadClientPort = scanner.nextInt();
		int rfcNo = 0;
		Socket peerSocket = null;

		ServerSocket uploadServerSocket = new ServerSocket(uploadClientPort);
		Thread peerToPeer = new Thread(new PeerToPeer(uploadServerSocket, uploadClientPort));
		peerToPeer.start();
		int option = -1;
		while(true) {
			System.out.println("Choose Your Option");
			System.out.println("1. ADD A RFC");
			System.out.println("2. LOOK FOR AN RFC");
			System.out.println("3. LIST ALL RFC");
			System.out.println("4. DOWNLOAD RFC");
			System.out.println("5. EXIT");
			try {
				option = new Scanner(System.in).nextInt();
				out = clientSocket.getOutputStream();
				dos = new DataOutputStream(out);
				if(option == 1) {
					System.out.println("Please enter RFC no:");
					rfcNo = new Scanner(System.in).nextInt();
					System.out.println("Please enter RFC Title:");
					String rfcTitle = new Scanner(System.in).nextLine();
					StringBuilder sb = new StringBuilder();
					sb.append("ADD" + " " + "RFC" + " " + rfcNo + " " + Util.VERSION + Util.CR + Util.LF);
					sb.append("Host:" + " " + uploaderHostName + Util.CR + Util.LF);
					sb.append("Port:" + " " + uploadClientPort + Util.CR + Util.LF);
					sb.append("Title:" + " " + rfcTitle + Util.CR + Util.LF);
					dos.writeUTF(sb.toString());
					System.out.println("Send Requect:\n" + sb.toString());
					in = clientSocket.getInputStream();
					dis = new DataInputStream(in);
					String response = dis.readUTF();
					System.out.println("\nResponse from the server is \n" + response);
				}else if(option == 2) {
					System.out.println("Please input the RFC No to look");
					rfcNo = new Scanner(System.in).nextInt();
					System.out.println("Please input the RFC Title");
					String rfcTitle = new Scanner(System.in).nextLine();
					StringBuilder sb = new StringBuilder();
					sb.append("LOOKUP" + " " + "RFC" + " " + rfcNo + " " + Util.VERSION + Util.CR + Util.LF);
					sb.append("Host:"  + " " + uploaderHostName + Util.CR + Util.LF);
					sb.append("Port:" + " " + uploadClientPort + Util.CR + Util.LF);
					sb.append("Title" + " " + rfcTitle + " " + Util.CR + Util.LF);
					dos.writeUTF(sb.toString());
					System.out.println("Send Requect:\n" + sb.toString());
					in = clientSocket.getInputStream();
					dis = new DataInputStream(in);
					String response = dis.readUTF();
					System.out.println("\nResponse from the server is \n" + response);
				}else if(option == 3) {
					StringBuilder sb = new StringBuilder();
					sb.append("LIST_ALL" + " " + Util.VERSION + Util.CR + Util.LF);
					sb.append("Host:" + " " + uploaderHostName + Util.CR + Util.LF);
					sb.append("Port:" + " " + uploadClientPort + Util.CR + Util.LF);
					dos.writeUTF(sb.toString());
					System.out.println("Send Requect:\n" + sb.toString());
					in = clientSocket.getInputStream();
					dis = new DataInputStream(in);
					String response = dis.readUTF();
					System.out.println("\nResponse from the server is \n" + response);
				}else if(option == 4) {
					System.out.println("\nPlease enter the RFC Number you want to get\n");
					rfcNo = new Scanner(System.in).nextInt();
					System.out.println("\nPlease enter the Host Name\n");
					String peerName = new Scanner(System.in).nextLine();
					System.out.println("\nPlease enter the Host Port");
					int peerPort = new Scanner(System.in).nextInt();
					peerSocket = new Socket(peerName, peerPort);
					out = peerSocket.getOutputStream();
					dos = new DataOutputStream(out);
					StringBuilder sb = new StringBuilder();
					sb.append("GET" + " " + "RFC" + " " + rfcNo + " " + Util.VERSION + Util.CR + Util.LF);
					sb.append("Host:" + " " + peerName + " " + Util.CR + Util.LF);
					sb.append("OS:" + " " + Util.OS + " " + Util.CR + Util.LF);
					dos.writeUTF(sb.toString());
					System.out.println("Send Requect:\n" + sb.toString());

					in = peerSocket.getInputStream();
					dis = new DataInputStream(in);
					String response = dis.readUTF();
					System.out.println("\nResponse from the server is \n" + response);
					if(peerHasFile(response)) {
						boolean ff = DownLoadFile(rfcNo, uploadClientPort, response);
						if(ff) {
							out = clientSocket.getOutputStream();
							dos = new DataOutputStream(out);
							sb = new StringBuilder();
							sb.append("ADD" + " " + "RFC" + " " + rfcNo + " " + Util.VERSION + Util.CR + Util.LF);
							sb.append("Host:" + " " + uploaderHostName + Util.CR + Util.LF);
							sb.append("Port:" + " " + uploadClientPort + Util.CR + Util.LF);
							sb.append("Title:" + " " + "Copy from " + peerPort + Util.CR + Util.LF);
							dos.writeUTF(sb.toString());
							System.out.println("Send Requect:\n" + sb.toString());
							in = clientSocket.getInputStream();
							dis = new DataInputStream(in);
							String newresponse = dis.readUTF();
							System.out.println("\nResponse from the server is \n" + newresponse);
						}
					}
				}else if(option == 5) {
					StringBuilder sb = new StringBuilder();
					sb.append("EXIT" + " " + Util.CR + Util.LF);
					sb.append("Host:" + " " + uploaderHostName + Util.CR + Util.LF);
					sb.append("Port:" + " " + uploadClientPort + Util.CR + Util.LF);
					dos.writeUTF(sb.toString());
					System.out.println("Send Requect:\n" + sb.toString());
					in = clientSocket.getInputStream();
					dis = new DataInputStream(in);
					String response = dis.readUTF();
					System.out.println("\nResponse from the server is \n" + response);
					if( in != null)
						in.close() ;
					if( dis != null)
						dis.close() ;
					if( out != null)
						out.close() ;
					if( dos != null)
						dos.close() ;
					if( peerSocket != null){
						peerSocket.close() ;
					}
					clientSocket.close() ;
					System.out.println("\nClosed connection with the server.");
					System.out.println("-------------------------------------------------------------------------------");
					System.exit(0);
				}else {
					System.out.println("Please enter right number");
				}
			}catch(InputMismatchException e){
				System.out.println("Please enter a number");
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean peerHasFile(String response) {
		String lines[] = response.split(Util.CR + Util.LF);
		String flag[] = lines[0].split(" ");
		String status = flag[1];
		return status.equals("200");

	}
	public static boolean DownLoadFile(int rfcNo, int uploadClientPort, String response) {
		String lines[] = response.split(Util.CR + Util.LF);
		File filePath = new File(uploadClientPort + "");
		if(!filePath.exists()) {
			filePath.mkdirs();
		}
		File file = new File(filePath.getAbsolutePath() + File.separator + rfcNo + ".txt");
		try {
			if(file.exists()) {
				return false;
			}
			file.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(file)) ;
			for(int i =6; i < lines.length; i++) {
				bw.write(lines[i]);
				bw.append(Util.CR + Util.LF);
			}
			bw.close();
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}

class PeerToPeer extends Thread{
	ServerSocket serverSocket = null;
	int uploadClientPort;
	//OutputStream out;
	DataOutputStream dos;
	//InputStream in;
	DataInputStream dis;
	Socket rfcRequestSocket;
	DateFormat dateFormat = new SimpleDateFormat("E d MM y HH:mm:ss z");
	public PeerToPeer(ServerSocket serverSocket, int uploadClientPort) {
		this.serverSocket = serverSocket;
		this.uploadClientPort = uploadClientPort;
	}

	public void run() {
		while(true) {
			try {
				rfcRequestSocket = serverSocket.accept();
				dis = new DataInputStream(rfcRequestSocket.getInputStream());
				String request = dis.readUTF();
				String[] flag = request.split(" ");
				//String answer = parseRequest(flag[0], flag[2]);
				StringBuilder sb = new StringBuilder();
				File file = new File(uploadClientPort + File.separator + flag[2] + ".txt");
				if(!flag[0].equals("GET")) {
				    sb.append(Util.VERSION + " " + 400 + " " + "BAD_REQUEST" + Util.CR + Util.LF);
				    sb.append("Date:" + new Date() + Util.CR + Util.LF);
				    sb.append("OS:" + " " + Util.OS + Util.CR + Util.LF);
				}else if(file.exists() && file.isFile()) {
					sb.append(Util.VERSION + " " + 200 + " " + "OK" + Util.CR + Util.LF);
					sb.append("Date:" + " " + new Date() + Util.CR + Util.LF);
					sb.append("OS:" + " " + Util.OS + Util.CR + Util.LF);
					sb.append("Last-Modified" + " " + dateFormat.format(file.lastModified()) + Util.CR + Util.LF);
					sb.append("Content-Length:" + " " + file.length() + Util.CR + Util.LF);
					sb.append("Content-Type" + " " + "text/text" + Util.CR + Util.LF);
					String fileContent = readFileContents(file) ;
					sb.append(fileContent + Util.CR + Util.LF);
				}else {
					System.out.println("Not find the file");
					sb.append(Util.VERSION + " " + 404 + " " + "NOT_FOUND" + Util.CR + Util.LF);
					sb.append("Date:" + " " + new Date() + Util.CR + Util.LF);
					sb.append("OS:" + " " + Util.OS + Util.CR + Util.LF);
				}
				dos = new DataOutputStream(rfcRequestSocket.getOutputStream());
				dos.writeUTF(sb.toString()) ;
				System.out.println("Sent the response:\n" + sb.toString());
				rfcRequestSocket.close();
//			} catch(FileNotFoundException e){
//				System.out.println("Not find the file");
//				StringBuilder sb = new StringBuilder();
//				sb.append(Util.VERSION + " " + 404 + " " + "NOT_FOUND" + Util.CR + Util.LF);
//				sb.append("Date:" + " " + new Date() + Util.CR + Util.LF);
//				sb.append("OS:" + " " + Util.OS + Util.CR + Util.LF);
//				dos = new DataOutputStream(rfcRequestSocket.getOutputStream());
//				dos.writeUTF(sb.toString()) ;
//				System.out.println("Sent the response:\n" + sb.toString());
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	private static String readFileContents(File file) throws IOException {

//		BufferedReader br = new BufferedReader(new FileReader(f)) ;
//		StringBuilder fileContentSb = new StringBuilder();
//		String line ;
//
//		while( (line = br.readLine()) != null){
//			fileContentSb.append(line) ;
//			fileContentSb.append(Util.CR + Util.LF) ;
//		}
//		br.close() ;
//		return fileContentSb.toString() ;
		StringBuilder sb = new StringBuilder();
		InputStream  is = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		for(String line = reader.readLine(); line  != null; line = reader.readLine()) {
			sb.append(line);
			sb.append(Util.LF);
		}
		return sb.toString();
	}
}
