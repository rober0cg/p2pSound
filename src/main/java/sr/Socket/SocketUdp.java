package sr.Socket;

import java.io.*;
import java.net.*;

public class SocketUdp {

    private DatagramSocket s;

    private String sHost = null;
    private InetAddress iaHost = null;
    private int nPort = 0;

    private int nSends = 0;
    private int nBytesSend = 0;
    private int nRecvs = 0;
    private int nBytesRecv = 0;

    public SocketUdp( int rPort ) throws Exception { // socket sin selección de puerto, para envío

        s = new DatagramSocket();
        nPort = rPort;

        nSends = 0;
        nBytesSend = 0;
        nRecvs = 0;
        nBytesRecv = 0;
    }

    public SocketUdp( int lPort, int rPort ) throws Exception { // socket identificando puerto para recepción

        s=new DatagramSocket(lPort);
        nPort = rPort;

        nSends = 0;
        nBytesSend = 0;
        nRecvs = 0;
        nBytesRecv = 0;
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

        return len;
    }

    public void showStats() {
        System.out.println("socketUdp.showStats");
        System.out.println("\tSends="+nSends+", Bytes="+nBytesSend);
        System.out.println("\tRecvs="+nRecvs+", Bytes="+nBytesRecv);
//        System.out.println("\tSends="+nSends+", Bytes="+nBytesSend+", Rate="+((float)(1000.0*nBytesSend)/(float)(lLastSend-lStartTime)));
//        System.out.println("\tRecvs="+nRecvs+", Bytes="+nBytesRecv+", Rate="+((float)(1000.0*nBytesRecv)/(float)(lLastRecv-lStartTime)));
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





