package sr.Socket;

import java.io.*;
import java.net.*;

public class SocketUdp {

    private DatagramSocket s;

    private String sHost = null;
    private InetAddress iaHost = null;
    private int nPort = 0;
//    private String sPeer = null;

    private int nSends = 0;
//    private int nSendParts = 0;
    private int nBytesSend = 0;
    private int nRecvs = 0;
//    private int nRecvParts = 0;
    private int nBytesRecv = 0;
    private long lStartTime = 0L, lLastSend = 0L, lLastRecv = 0L;

    public SocketUdp( int port ) throws Exception {

//        sHost = host;
//        iaHost = InetAddress.getByName(sHost);
        nPort = port;

        s=new DatagramSocket(nPort);

        nSends = 0;
//        nSendParts = 0;
        nBytesSend = 0;
        nRecvs = 0;
//        nRecvParts = 0;
        nBytesRecv = 0;
        lStartTime = System.currentTimeMillis();
        lLastSend = lStartTime+1;
        lLastRecv = lStartTime+1;

    }

    public void setHost ( String host ) throws Exception {
        sHost = host;
        iaHost = InetAddress.getByName(sHost);
    }
    
    public int recv( byte[] buf, int len) {
        int l=0;

        DatagramPacket dpr=new DatagramPacket(buf,len);
        try {
            s.receive(dpr);
        } catch (IOException e) {
            return -1;
        }

        String sFrom = dpr.getAddress().getHostAddress();
        if (!sFrom.equals(sHost) ) {
            System.out.println("recvSocket From != Host ("+ sFrom +" != "+ sHost +")");
        }
        
        l = dpr.getLength();
        if ( l!=len ) {
            System.out.println("recvSocket l<len ("+l+"<"+len+")");
        }

        nRecvs++;
        nBytesRecv+=l;
        lLastRecv = System.currentTimeMillis();

        return l;
    }

    public int send( byte[] buf, int len) {
        
        DatagramPacket dps = new DatagramPacket(buf,len,iaHost,nPort);
        try {
            s.send(dps);
        } catch (IOException e) {
            return -1;
        }
       
        nSends++;
        nBytesSend+=len;
        lLastSend = System.currentTimeMillis();

        return len;
    }

    public void showStats() {
        System.out.println("socketUdp.showStats");
        System.out.println("\t.Sends="+nSends);
//        System.out.println("\t.SendParts="+nSendParts);
        System.out.println("\t.BytesSend="+nBytesSend);
        System.out.println("\t.SendRate="+((float)(1000.0*nBytesSend)/(float)(lLastSend-lStartTime)));
        System.out.println("\t.Recvs="+nRecvs);
//        System.out.println("\t.RecvParts="+nRecvParts);
        System.out.println("\t.BytesRecv="+nBytesRecv);
        System.out.println("\t.RecvRate="+((float)(1000.0*nBytesRecv)/(float)(lLastRecv-lStartTime)));
    }
    
    
    public void close() {
//        System.out.println("socketUdp.close()");
        showStats();

        try {
            s.close();
        } catch (NullPointerException e) {
            return;
        }
        return;
    }
        
}





