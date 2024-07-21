package sr.Socket;

import java.io.*;
import java.net.*;

public class SocketTcp {

    private Socket s;
    private DataOutputStream dos;
    private DataInputStream dis;
    private ServerSocket ss=null;
    private volatile boolean CANCEL_ACCEPT = false;

    private String Host = null;
    private int Port = 0;
    private String Peer = null;

    private int nSends = 0;
//    private int nSendParts = 0;
    private int nBytesSend = 0;
    private int nRecvs = 0;
//    private int nRecvParts = 0;
    private int nBytesRecv = 0;
    private long lStartTime = 0L, lLastSend = 0L, lLastRecv = 0L;

    public SocketTcp( String sHost, int nPort ) {
        // cliente, llama
        Host = sHost;
        Port = nPort;
        ss = null;

    }
    
    public int connect() {
        try {
            s = new Socket(Host, Port );
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
            return -1;
        }

        return conectado();
    }

    public SocketTcp( int nPort ) {
    // servidor, espera llamada
        Port = nPort;
    }

    public int accept() throws Exception {
        ss = new ServerSocket();
        ss.setReuseAddress(true);
        ss.bind(new InetSocketAddress(Port));
        ss.setSoTimeout(100); // esperas de 1 décima para comprobar si cancelan
        CANCEL_ACCEPT=false;
        while(!CANCEL_ACCEPT) {
            try {
                s=ss.accept();
                break;
            }
            catch (SocketTimeoutException e) {
                continue;
            }
            catch (Exception e) {
                ss.close();
                throw e;
            }
        }
        if (CANCEL_ACCEPT) {
            return -1;
        }

        return conectado();
    }

    public void cancelAccept() {
        CANCEL_ACCEPT=true;
    }

    private int conectado() {
//      Peer = s.getInetAddress().toString().replace("/","");
      Peer = s.getInetAddress().getHostAddress();
      System.out.println("SocketTcp connected with: "+ Peer +":"+s.getPort());

      try {
          s.setTcpNoDelay(true);
//          System.out.println("SocketTcp.getTcpNoDelay="+s.getTcpNoDelay());
      } catch (SocketException e) {
      }

      nSends = 0;
//      nSendParts = 0;
      nBytesSend = 0;
      nRecvs = 0;
//      nRecvParts = 0;
      nBytesRecv = 0;
      lStartTime = System.currentTimeMillis();
      lLastSend = lStartTime+1;
      lLastRecv = lStartTime+1;
      
      try {
          dis=new DataInputStream(s.getInputStream());
          dos=new DataOutputStream(s.getOutputStream());
      } catch (IOException e) {
          e.printStackTrace();
          return -1;
      }

      return 0;
        
    }
    

    public String getPeer () {
        return Peer;
    }

    public int recv( byte[] buf, int len) {
        int l=0;
        try {
            while (l<len) {
                int r = dis.read(buf,l,len-l);
//                nRecvParts++;
                if (r<0) break;
                if (r==0) {
                    System.out.println("recvSocket 0");
                    break;
                }
                l+=r;
            }

            if ( l!=len ) {
                System.out.println("recvSocket l<len ("+l+"<"+len+")");
            }

            nRecvs++;
            nBytesRecv+=l;
            lLastRecv = System.currentTimeMillis();

        } catch (IOException e) {
            return -1;
        }
        return l;
    }

    public int recv( byte[] buf) {
        int len=buf.length;
        int l;
        try {
            l = dis.read(buf,0,len);
//            nRecvParts++;
            if (l<=0) {
                System.out.println("recvSocket<=0");
                return 0;
            }

            nRecvs++;
            nBytesRecv+=l;
            lLastRecv = System.currentTimeMillis();

        } catch (IOException e) {
            return -1;
        }
        return l;
    }

    
    public int send( byte[] buf, int len) {
        int l=len;
        try {
            dos.write(buf,0,len);
//            nSendParts++;
        } catch (IOException e) {
            return -1;
        }

        nSends++;
        nBytesSend+=l;
        lLastSend = System.currentTimeMillis();

        return l;
    }
    public int send( byte[] buf) {
        int len=buf.length;
        int l=len;
        try {
            dos.write(buf,0,len);
//            nSendParts++;
        } catch (IOException e) {
            return -1;
        }

        nSends++;
        nBytesSend+=l;
        lLastSend = System.currentTimeMillis();

        return l;
    }

    public void showStats() {
        System.out.println("socketTcp.showStats");
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
//        System.out.println("socketTcp.close()");
        showStats();

        try {
            s.close();
            if ( ss!=null ) {
                ss.close();
            }
        } catch (IOException e) {
        } catch (NullPointerException e) {
        }
    }
    
}
