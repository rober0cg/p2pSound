package sr.Sound;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sr.Socket.SocketTcp;
import sr.Socket.SocketUdp;
import sr.SimpleLog.SimpleLog;

public class Proceso {
    private static final String TAG = "Proceso";

    private static volatile boolean FIN=false;

    private static SocketUdp sRCV=null;
    private static SocketUdp sSND=null;
    private static SocketTcp sCTL=null;
    private static Recorder rREC = null;
    private static Player pPLY = null;

    // Devices
    private static String sDevIn  = null ;
    private static String sDevOut = null ;

    // Mensajes
    private static String sMsgFormatInit = "INIT:r=%06d,c=%1d,b=%02d,px=%04d,p1=%04d";
    private static String sMsgRegexpInit = "INIT:r=(\\d+),c=(\\d+),b=(\\d+),px=(\\d+),p1=(\\d+)";

    private static String sMsgConfirm = "OK";
    private static String sMsgReject = "KO";

    private static String sMsgEnd = "END" ;

    private static Runnable runFin = null;

    private static final int MS_SLEEP_START_SEND = 600;
    private static final int MS_SLEEP_FIN = 200;
    private static final int MS_SLEEP_AFTER_THREAD_START = 200;

//    public Proceso() {
        // nada que hacer
//    }

    public void Parametros ( String I, String O, int r, int c, int b, int px, int p1 ) {
        sDevIn = I;
        sDevOut = O;
        AppParams.init(sDevIn,sDevOut,r,c,b,px,p1);
        AppParams.show();
        return;
    }

    private void exitSockets () {
        if ( sCTL != null ) { sCTL.close(); sCTL = null; }
        if ( sRCV != null ) { sRCV.close(); sRCV = null; }
        if ( sSND != null ) { sSND.close(); sSND = null; }
    }

    private int initSocketsUdp ( int port ) {
    // Sockets UDP para la transferencia del audio
        try {
            sRCV = new SocketUdp( port, port );
            sSND = new SocketUdp( port );
        } catch (Exception e) {
            e.printStackTrace();
            exitSockets();
            return -1;
        }
        return 0;
    }


    public int ConectaLlamante (String remote, String port ) {
        int nPortCTL = Integer.valueOf(port) ;
        int nPortRCV = nPortCTL ;

    // Sockets UDP para la transferencia de audio, uno para cada sentido
        if ( initSocketsUdp ( nPortRCV ) != 0 ) {
            return -1;
        }

    // Socket TCP para la llamada al otro extremo, y por el que se envía la configuración de audio
        try {
            sCTL = new SocketTcp( remote, nPortCTL );
            int rc = sCTL.connect();
            if ( rc<0 ) {
                exitSockets();
                return -2;
            }
            String peer = sCTL.getPeer();
            sRCV.setHost(peer);
            sSND.setHost(peer);
            SimpleLog.LOGI(TAG, "PEER="+peer);
        } catch (Exception e) {
            exitSockets();
            return -2;
        }

    // Envío de la configuraicón de audio
        int r = AppParams.getSampleRate();
        int c = AppParams.getChannels();
        int b = AppParams.getSampleSizeInBits();
        int px = AppParams.getPacketSize();
        int p1 = AppParams.getPacketSize1();

        // envío configuración
        String strParams = String.format(sMsgFormatInit, r, c, b, px, p1);
        byte[] bufParams = strParams.getBytes();
        sCTL.send(bufParams);
        SimpleLog.LOGI(TAG, "SEND srtParams=" + strParams);

        // respuesta
        byte bufResp[] = new byte[20];
        sCTL.recv( bufResp );
        String strResp = new String(bufResp);
        SimpleLog.LOGI(TAG, "RECV strResp=" + strResp );

        // error
        if ( strResp== null || strResp.equals("") || strResp.equals(sMsgReject) ) {
            SimpleLog.LOGE(TAG, "ERROR: parámetros rechazados.");
            exitSockets();
            return -3;
        }

        // ok
        return 0;
    }

    public void cancelaLlamado() {
        if ( sCTL != null ) {
            if ( !sCTL.cancelAccept() ) { // true ya estaba conectado, false en accept
                sCTL = null;
            }
        }
    }
    public int ConectaLlamado ( String port ) {
        int nPortCTL = Integer.valueOf(port) ;
        int nPortRCV = nPortCTL ;

    // Sockets UDP para la transferencia de audio, uno para cada sentido
        if ( initSocketsUdp ( nPortRCV ) != 0 ) {
            return -1;
        }

    // Socket TCP esperando llamada del otro extremo, y por el que recibiremos la configuración de audio
        try {
            sCTL = new SocketTcp( nPortCTL );
            int rc = sCTL.accept();
            if ( rc!=0 ) {  // <0 error, >0 cancel
                exitSockets();
                return rc;
            }
            String peer = sCTL.getPeer();
            sRCV.setHost(peer);
            sSND.setHost(peer);
            SimpleLog.LOGI(TAG, "PEER="+peer);
        } catch (Exception e) {
            exitSockets();
            return -2;
        }

    // Recepción parámetros y envío respuesta
        byte bufRecv[] = new byte[128];
        int len = sCTL.recv( bufRecv );
        byte[] bufParams = new byte[len];
        System.arraycopy(bufRecv, 0, bufParams, 0, len);
        String strParams = new String(bufParams);
        SimpleLog.LOGI(TAG, "RECV strParams("+len+")=[" + strParams + "]");

        // recepción
        Pattern pPatternRecvParams = Pattern.compile(sMsgRegexpInit);
        CharSequence chsParams = strParams;
        Matcher mMatcherRecvParams = pPatternRecvParams.matcher(chsParams);

        // error y corto
        if ( mMatcherRecvParams.matches()==false ) {
            SimpleLog.LOGE(TAG, "ERROR: patrón de parámetros desconocido");
            String strReject = sMsgReject;
            byte[] bufReject = strReject.getBytes();
            sCTL.send(bufReject);
            SimpleLog.LOGI(TAG, "SEND srtReject=" + strReject );
            exitSockets();
            return -3;
        }

        int r = Integer.valueOf(mMatcherRecvParams.group(1));
        int c = Integer.valueOf(mMatcherRecvParams.group(2));
        int b = Integer.valueOf(mMatcherRecvParams.group(3));
        int px = Integer.valueOf(mMatcherRecvParams.group(4));
        int p1 = Integer.valueOf(mMatcherRecvParams.group(5));

        AppParams.init(sDevIn,sDevOut,r,c,b,px,p1);
        AppParams.show();

        // confirmación OK
        String strConfirm = sMsgConfirm;
        byte[] bufConfirm = strConfirm.getBytes();
        sCTL.send(bufConfirm);
        SimpleLog.LOGI(TAG, "SEND srtConfirm=" + strConfirm );

        return 0;
    }

    public int getRate() {
        return AppParams.getSampleRate();
    }
    public int getChannels() {
        return AppParams.getChannels();
    }
    public int getBits() {
        return AppParams.getSampleSizeInBits();
    }
    public int getPacketXSize() {
        return AppParams.getPacketSize();
    }
    public int getPacket1Size() {
        return AppParams.getPacketSize1();
    }


    public void Ejecuta(Runnable cbFin) {
        rREC = new Recorder(AppParams.inDevice);
        pPLY = new Player(AppParams.outDevice);


        Thread tPeerCtrl = new Thread( new Runnable() {
            public void run() {
                SimpleLog.LOGI(TAG, "thread PeerCtrl START");
                PeerCtrl();
                SimpleLog.LOGI(TAG, "thread PeerCtrl END");
            }
        });

        Thread tPlayRecvd = new Thread(new Runnable() {
            public void run() {
                SimpleLog.LOGI(TAG, "thread PlayRecvd START");
                PlayRecvd();
                SimpleLog.LOGI(TAG, "thread PlayRecvd END");
            }
        });

        Thread tSendRecrd = new Thread(new Runnable() {
            public void run() {
                SimpleLog.LOGI(TAG, "thread SendRecrd START");
                SendRecrd();
                SimpleLog.LOGI(TAG, "thread SendRecrd END");
            }

        });


        runFin = cbFin ;
        FIN = false;

        tPeerCtrl.start();
        Utils.msSleep ( MS_SLEEP_AFTER_THREAD_START );

        tPlayRecvd.start();
        Utils.msSleep ( MS_SLEEP_AFTER_THREAD_START );

        tSendRecrd.start();
        Utils.msSleep ( MS_SLEEP_AFTER_THREAD_START );

        return;
    }

    private void PeerCtrl() {

        while (!FIN) {
            byte bufRecv[] = new byte[128];
            int len = sCTL.recv( bufRecv );
            if ( len <= 0 ) {
                SimpleLog.LOGE(TAG, "RECV strMsg len <= 0");
                break;
            }
            byte[] bufMsg = new byte[len];
            System.arraycopy(bufRecv, 0, bufMsg, 0, len);
            String strMsg = new String(bufMsg);
            SimpleLog.LOGI(TAG, "RECV strMsg("+len+")=[" + strMsg + "]");

            if ( strMsg== null || strMsg.equals("") ) {
                SimpleLog.LOGW(TAG, "AVISO: mensaje desconocido. Seguir.");
            }
            else
            if ( strMsg.equals(sMsgEnd) ) {
                SimpleLog.LOGI(TAG, "INFO: recibido FIN. Cortar.");

                if ( runFin != null ){
                      SimpleLog.LOGD(TAG, "Avisando a SWING...");
                      runFin.run();
                }
                runFin = null;

                Fin();

                break;
            }
        }
    }

    private void PlayRecvd( ) {
        long usFirst = AppParams.getTimePacket1();
        long usTodos = AppParams.getTimePacket();

        SimpleRTPacket rt = new SimpleRTPacket(usFirst,usTodos);

        pPLY.startPlayer();

        // Primera recepción con buffer mayor
        {
            int l = AppParams.bufferSize * AppParams.packetFactor1 ;
            byte[] b = new byte[ l ];
            rt.setDataLen(l);
/**/
            int rc = sRCV.recv( rt.getbPacketBuf(), rt.getnPacketLen() );
            if ( rc<=0 ) {
                SimpleLog.LOGE(TAG, "thread PlayRCV: socket closed");
                FIN=true;
            }
            else {
                rc = rt.recvBuffer( b, l );
                if ( rc!=0 ) { // error no fatal
                    SimpleLog.LOGE(TAG, "thread PlayRCV: recvBuffer error "+rc);
                }
                else {
                    pPLY.sendPlayer( b, 0, l );
                }
            }
/**/
/*
            int rc = rt.recv( sDAT, b, l );
            if ( rc==-1 ) { // error fatal
                SimpleLog.LOGE(TAG, "thread PlayRCV: socket closed");
                FIN=true;
            }
            else
            if ( rc!=0 ) { // error no fatal
                SimpleLog.LOGW(TAG, "thread PlayRCV: recvBuffer status "+rc);
            }
            else {
                pPLY.sendPlayer( b, 0, l );
            }

*/
        }

        // Bucle de recepción y envío a speaker
        {
            int l = AppParams.bufferSize ;
            byte[] b = new byte[ l ];
            rt.setDataLen(l);
            while (!FIN) {
/**/
                int rc = sRCV.recv( rt.getbPacketBuf(), rt.getnPacketLen() );
                if ( rc<=0 ) {
                    SimpleLog.LOGE(TAG, "thread PlayRCV: socket closed");
                    FIN=true;
                    break ;
                }
                rc = rt.recvBuffer( b, l );
                if ( rc<0 ) { // error, aunque no fatal: continuamos sin reproducir
                    SimpleLog.LOGE(TAG, "thread PlayRCV: recvBuffer error "+rc);
                }
                else {
                    pPLY.sendPlayer( b, 0, l );
                }
/**/
/*
                int rc = rt.recv( sDAT, b, l );
                if ( rc<0 ) {
                    SimpleLog.LOGE(TAG, "thread PlayRCV: socket closed");
                    FIN=true;
                    break ;
                }
                else
                if ( rc!=0 ) {
                    SimpleLog.LOGW(TAG, "thread PlayRCV: recvBuffer status "+rc);
                }
                else {
                    pPLY.sendPlayer( b, 0, l );
                }
*/
            }
        }

        pPLY.stopPlayer();
        return;
    }

    private void SendRecrd () {
        SimpleRTPacket rt = new SimpleRTPacket();

        rREC.startRecorder();

        Utils.msSleep(MS_SLEEP_START_SEND);

        // Primer envío con buffer mayor
        {
            int l = AppParams.bufferSize * AppParams.packetFactor1 ;
            byte b[] = new byte[l];
            rt.setDataLen(l);
            int r = rREC.readRecorder(b,l);
            rt.sendBuffer ( b, r );
            int rc = sSND.send( rt.getbPacketBuf(), rt.getnPacketLen() );
            if ( rc<=0 ) {
                SimpleLog.LOGE(TAG, "thread SendREC: socket closed");
                FIN=true;
            }
        }

        // Bucle de lectura de micro y envío
        {
            int l = AppParams.bufferSize;
            byte[] b = new byte[l];
            rt.setDataLen(l);
            while (!FIN) {
                int r = rREC.readRecorder(b,AppParams.bufferSize);
                rt.sendBuffer ( b, r );
                int rc = sSND.send( rt.getbPacketBuf(), rt.getnPacketLen() );
                if ( rc<=0 ) {
                    SimpleLog.LOGE(TAG, "thread SendREC: socket closed");
                    FIN=true;
                    break;
                }
            }
        }

        rREC.stopRecorder();
        return;
    }



    public void Finaliza () {
        SimpleLog.LOGD(TAG, "Proceso::Finaliza()" );

        if ( sCTL != null ) { // envío fin
            String strEnd = sMsgEnd;
            byte[] bufEnd = strEnd.getBytes();
            sCTL.send(bufEnd);
            SimpleLog.LOGI(TAG, "SEND srtEnd=" + strEnd );
        }

        Fin();

        return;
    }

    private void Fin () {
        FIN = true;

        Utils.msSleep(MS_SLEEP_FIN);

        exitSockets();

        runFin = null ;

        return;

    }

    public void setVolumePlayer ( int v ) {
        if ( pPLY!=null )
            pPLY.setVolume(v);
    }

    public void setVolumeRecorder ( int v ) {
        if ( rREC!=null )
            rREC.setVolume(v);
    }

}
