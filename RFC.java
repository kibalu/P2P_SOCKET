public class RFC {
	int rfcNo ;	// representative of the RFC.
	String rfcTitle ;	// title of the RFC
	String hostName ;	// the hostname of the peer that contains the RFC.
	int port ;

	public RFC(int rfcNo, String rfcTitle, String hostName, int port) {
		this.rfcNo = rfcNo;
		this.rfcTitle = rfcTitle;
		this.hostName = hostName;
		this.port = port ;
	}
}
