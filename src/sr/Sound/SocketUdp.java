package sr.Sound;

import java.io.*;
import java.net.*;

public class SocketUdp {

    private DatagramSocket s;

    private String sHost = null;
    private InetAddress iaHost = null;
    private int nPort = 0;
//    private String sPeer = null;

    private int nSends = 0, nSendParts = 0;
    private int nBytesSend = 0;
    private int nRecvs = 0, nRecvParts = 0;
    private int nBytesRecv = 0;
    private long lStartTime = 0L, lLastSend = 0L, lLastRecv = 0L;

    public SocketUdp( int port ) throws Exception {

//        sHost = host;
//        iaHost = InetAddress.getByName(sHost);
        nPort = port;

        s=new DatagramSocket(nPort);

        nSends = 0;
        nSendParts = 0;
        nBytesSend = 0;
        nRecvs = 0;
        nRecvParts = 0;
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
//            e.printStackTrace();
            return -1;
        }

        //dpr.getSocketAddress().toString()
        //dpr.getData()
        //dpr.getLength()


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
            e.printStackTrace();
            return -1;
        }
       
        nSends++;
        nBytesSend+=len;
        lLastSend = System.currentTimeMillis();

        return len;
    }

    public void showStats() {
        System.out.println("socketUdp.Sends="+nSends);
        System.out.println("socketUdp.SendParts="+nSendParts);
        System.out.println("socketUdp.BytesSend="+nBytesSend);
        System.out.println("socketUdp.SendRate="+((float)(1000.0*nBytesSend)/(float)(lLastSend-lStartTime)));
        System.out.println("socketUdp.Recvs="+nRecvs);
        System.out.println("socketUdp.RecvParts="+nRecvParts);
        System.out.println("socketUdp.BytesRecv="+nBytesRecv);
        System.out.println("socketUdp.RecvRate="+((float)(1000.0*nBytesRecv)/(float)(lLastRecv-lStartTime)));
    }
    
    
    public void close() {
        System.out.println("socket.close()");
        showStats();

        try {
            s.close();
        } catch (NullPointerException e) {
            return;
        }
        return;
    }
        
}





