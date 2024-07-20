package sr.Sound;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Proceso {


    private static volatile boolean FIN=false;

    private static SocketUdp sDAT;
    private static SocketTcp sCTL;
    private static Recorder rREC = null;
    private static Player pPLY = null;

    private static String sFormatSendParams = "r=%06d,c=%1d,b=%02d,px=%04d,p1=%04d";
    private static String sRegexpRecvParams = "r=(\\d+),c=(\\d+),b=(\\d+),px=(\\d+),p1=(\\d+)";

    private static String sConfirmParams = "OK";
    private static String sRejectParams = "KO";
    
    public Proceso() {
        // TODO Auto-generated constructor stub

    }

    public void Parametros ( String I, String O, int r, int c, int b, int px, int p1 ) {
        AppParams.setInDevice ( I );
        AppParams.setOutDevice ( O );
        AppParams.setSampleRate ( r );
        AppParams.setChannels ( c );
        AppParams.setSampleSizeInBits ( b );
        AppParams.setPacketSize ( px );
        AppParams.setPacket1Size ( p1 );
        AppParams.show();
        return;
    }

    public int EjecutaLlamante (String remote, String port ) {
        int nPortCTL = Integer.valueOf(port) ;
        int nPortDAT = nPortCTL ;

    // Socket UDP para la transferencia del audio
        try {
            sDAT = new SocketUdp( nPortDAT );
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    // Socket TCP para la llamada al otro extremo, y por el que se envía la configuración de audio
        try {
            sCTL = new SocketTcp( remote, nPortCTL );
            String peer = sCTL.getPeer();
            sDAT.setHost( peer );
        } catch (Exception e) {
            e.printStackTrace();
            sDAT.close();
            return -2;
        }

    // Envío de la configuraicón de audio
        int r = AppParams.getSampleRate();
        int c = AppParams.getChannels();
        int b = AppParams.getSampleSizeInBits();
        int px = AppParams.getPacketSize();
        int p1 = AppParams.getPacket1Size();

        String strParams = String.format(sFormatSendParams, r, c, b, px, p1);
        byte[] bufParams = strParams.getBytes();
        sCTL.send(bufParams);
        System.out.println( "SEND srtParams=" + strParams );

        byte bufResp[] = new byte[20];
        sCTL.recv( bufResp );
        String strResp = new String(bufResp);
        System.out.println( "RECV strResp=" + strResp );

        if ( strResp== null || strResp.equals("") || strResp.equals(sRejectParams) ) {
            System.out.println( "ERROR: parámetros rechazados.");
            sCTL.close();
            sDAT.close();
            return -3;
        }

    // Inicio de los hilos de envía captura y reproduce recibido
        IniciaHilosEnviaRecibe();

        return 0;
    }

    public int EjecutaLlamado ( String port ) {
        int nPortCTL = Integer.valueOf(port) ;
        int nPortDAT = nPortCTL ;

    // Socket UDP para la transferencia del audio
        try {
            sDAT = new SocketUdp( nPortDAT );
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    // Socket TCP esperando llamada del otro extremo, y por el que recibiremos la configuración de audio
        try {
            sCTL = new SocketTcp( "", nPortCTL );
            String peer = sCTL.getPeer();
            sDAT.setHost( peer );
            System.out.println("PEER="+peer);
        } catch (Exception e) {
            e.printStackTrace();
            sDAT.close();
            return -2;
        }

    // Recepción parámetros y envío respuesta
        byte bufRecv[] = new byte[80];
        int len = sCTL.recv( bufRecv );
        byte[] bufParams = new byte[len];
        System.arraycopy(bufRecv, 0, bufParams, 0, len);
        String strParams = new String(bufParams);
        System.out.println( "RECV strParams("+len+")=[" + strParams + "]");

        Pattern pPatternRecvParams = Pattern.compile(sRegexpRecvParams);
        CharSequence chsParams = strParams;
        Matcher mMatcherRecvParams = pPatternRecvParams.matcher(chsParams);
        if ( mMatcherRecvParams.matches()==false ) {
            System.out.println("ERROR: patrón de parámetros desconocido");
            String strReject = sRejectParams;
            byte[] bufReject = strReject.getBytes();
            sCTL.send(bufReject);
            System.out.println( "SEND srtReject=" + strReject );
            sCTL.close();
            sDAT.close();
            return -3;
        }

        int r = Integer.valueOf(mMatcherRecvParams.group(1));
        int c = Integer.valueOf(mMatcherRecvParams.group(2));
        int b = Integer.valueOf(mMatcherRecvParams.group(3));
        int px = Integer.valueOf(mMatcherRecvParams.group(4));
        int p1 = Integer.valueOf(mMatcherRecvParams.group(5));

        AppParams.setSampleRate ( r );
        AppParams.setChannels ( c );
        AppParams.setSampleSizeInBits ( b );
        AppParams.setPacketSize ( px );
        AppParams.setPacket1Size ( p1 );
        AppParams.show();

        String strConfirm = sConfirmParams;
        byte[] bufConfirm = strConfirm.getBytes();
        sCTL.send(bufConfirm);
        System.out.println( "SEND srtConfirm=" + strConfirm );

        // Inicio de los hilos de envía captura y reproduce recibido
        IniciaHilosEnviaRecibe();

        return 0;
    }

    public void IniciaHilosEnviaRecibe () {
        rREC = new Recorder(AppParams.inDevice);
        pPLY = new Player(AppParams.outDevice);

        Thread tSendREC = new Thread(new Runnable() {
            public void run() {
                System.out.println("thread SendREC start");
                SendREC();
                System.out.println("thread SendREC end");
            }

        });

        Thread tPlayRCV = new Thread(new Runnable() {
            public void run() {
                System.out.println("thread PlayRCV start");
                PlayRCV();
                System.out.println("thread PlayRCV end");
            }
        });

        tSendREC.start();
        tPlayRCV.start();

        // Thread.sleep(100, 0); //espera 0,1seg
        return;
    }
    
    private void SendREC () {
        rREC.startRecorder();

        // Primer envío con buffer mayor
        {
            int l = AppParams.bufferSize * AppParams.packet1Factor ;
            byte b[] = new byte[l];
            SimpleRTPacket rt = new SimpleRTPacket( l );
            int r = rREC.readRecorder(b,l);
            rt.sendBuffer ( b, r );
            int s = sDAT.send( rt.getPacketBuf(), rt.getPacketLen() );
            if ( s<=0 ) {
                System.out.println("thread SendREC: socket closed");
                FIN=true;
            }
        }

        // Bucle de lectura de micro y envío
        {
            int l = AppParams.bufferSize;
            byte[] b = new byte[l];
            SimpleRTPacket rt = new SimpleRTPacket( l );
            while (!FIN) {
                int r = rREC.readRecorder(b,AppParams.bufferSize);
                rt.sendBuffer ( b, r );
                int s = sDAT.send( rt.getPacketBuf(), rt.getPacketLen() );
                if ( s<=0 ) {
                    System.out.println("thread SendREC: socket closed");
                    FIN=true;
                    break;
                }
            }
        }

        rREC.stopRecorder();
        return;
    }

    private void PlayRCV( ) {
        pPLY.startPlayer();

        // Primera recepción con buffer mayor
        {
            int l = AppParams.bufferSize * AppParams.packet1Factor ;
            byte[] b = new byte[ l ];
            SimpleRTPacket rt = new SimpleRTPacket( l );
            int r = sDAT.recv( rt.getPacketBuf(), rt.getPacketLen() );
            if ( r<=0 ) {
                System.out.println("thread PlayRCV: socket closed");
                FIN=true;
            }
            else {
                rt.recvBuffer( b, l );
                pPLY.sendPlayer( b, 0, l );
            }
        }
        
        // Bucle de recepción y envío a speaker
        {
            int l = AppParams.bufferSize ;
            byte[] b = new byte[ l ];
            SimpleRTPacket rt = new SimpleRTPacket( l );
            while (!FIN) {
                int r = sDAT.recv( rt.getPacketBuf(), rt.getPacketLen() );
                if ( r<=0 ) {
                    System.out.println("thread PlayRCV: socket closed");
                    FIN=true;
                    break ;
                }
                rt.recvBuffer( b, l );
                pPLY.sendPlayer( b, 0, l );
                // Utils.FadeInOut_8BitsMono(b,AppParams.bufferSize,0,20);
                /* int offset=0;
                 * int framesToSkeep = rt.sync( AppParams.frameRate );
                 * if ( framesToSkeep > 0 ) {
                 *     offset = framesToSkeep * AppParams.sampleSizeInBytes ;
                 *     System.out.println( "thread PlayRCV framesToSkeep = "+framesToSkeep+", offset = "+offset );
                 * }
                 * pPLY.sendPlayer(b, offset, l-offset);
                 */
            }
        }

        pPLY.stopPlayer();
        return;
    }

    
    
    
    public void Finaliza () {
        FIN = true;

        try {
            Thread.sleep(100, 0); //espera 0,1seg
        } catch (InterruptedException e) {
        } 
        
        sCTL.close();
        sDAT.close();
        return;
    }
    
}
