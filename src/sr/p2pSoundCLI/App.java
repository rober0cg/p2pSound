package sr.p2pSoundCLI;

import sr.GetOpts.*;
import sr.Socket.SocketTcp;
import sr.Socket.SocketUdp;
import sr.Sound.*;

import java.io.IOException;

public class App {

    private static final String appName = "p2pSoundCLI";

    private static String remote="", port="", inDevice="", outDevice="";
    private static int frameRate=0;
    private static int playerQueueSize=0;

    private static volatile boolean FIN=false;

    private static Recorder rREC = null;
    private static Player pPLY = null;


    public App() {
        // TODO Auto-generated constructor stub
    }

    private static final String options = "r:p:i:o:R:Q:h";
    private static final String help = 
        "opciones válidas:"+
        "\n\t -r remote -p port, para actuar como llamante (ejempo: -r rober.ddns.net -p 5555, -r 8.8.8.8 -p 8888)"+
        "\n\t -p port, para actuar como llamado (ejmeplo -p 5555, -p 8888)"+
        "\n\t -i 'patrón', para especificar dispositivo de captura de sonido distinto de " +AppParams.inDevice+
        "\n\t -o 'patrón', para especificar dispositivo de salida de sonido distinto de " +AppParams.outDevice+
        "\n\t -R frameRate, para especificar frecuencia envío distinta de " +AppParams.frameRate+
        "\n\t -Q playerQueueSize, para especificar primer envío distinto de " +AppParams.packet1Factor+
        "\n\t -h, esta ayuda"+
        ""
    ;
    private static int Argumentos(String[] args) {
        GetOpt g = new GetOpt( appName, args, options );
        int c;
        while ( (c=g.getopt()) != -1) {
            switch (c) {
            case 'r':
                remote = g.getOptarg();
                break;
            case 'p':
                port = g.getOptarg();
                break;
            case 'i':
                inDevice = g.getOptarg();
                break;
            case 'o':
                outDevice = g.getOptarg();
                break;
            case 'R':
                frameRate = Integer.valueOf(g.getOptarg());
                break;
            case 'Q':
                playerQueueSize = Integer.valueOf(g.getOptarg());
                break;
            case 'h':
            case '?':
                System.out.println(help);
                return 0;
            default:
                System.out.println(appName +": opción desconocida '"+(char)c+"'");
                return -1;
            }
        }
// port es requerido
        if ( port=="" ) {
            System.out.println(appName+": port es requerido");
            return -1;
        }

// si no hay remote, estamos en modo servidor
        if ( remote=="") {
            System.out.println(appName +" en modo 'llamado' en el puerto "+port);
        }
        else {
            System.out.println(appName +" en modo 'llamante' a "+remote+":"+port);
        }

// si no se especifican, los "por defecto"
        if ( inDevice=="" )  inDevice=AppParams.inDevice;
        if ( outDevice=="" ) outDevice=AppParams.outDevice;
        if ( frameRate==0 )  frameRate=AppParams.frameRate;
        if ( playerQueueSize==0 ) playerQueueSize=AppParams.packet1Factor;

        return 0;
    }

    public static void main(String[] args) throws Exception {

        if (Argumentos(args)<0) {
            System.out.println(appName +": error argumentos");
            System.out.println(help);
            return;
        }

        if ( inDevice != AppParams.inDevice ) AppParams.setInDevice( inDevice );
        if ( outDevice != AppParams.outDevice ) AppParams.setOutDevice( outDevice );
        if ( frameRate != AppParams.frameRate ) AppParams.setFrameRate( frameRate );
        if ( playerQueueSize != AppParams.packet1Factor ) AppParams.setPlayerQueueSize ( playerQueueSize );
        AppParams.show();

        Long lTime = 1000L / AppParams.frameRate; // Periodo
        System.out.println( "frameRate = "+ AppParams.frameRate+" Hz. - "+lTime+" ms." );

        int nPortCTL = Integer.valueOf(port) ;
        int nPortDAT = nPortCTL ; //+1;

        SocketUdp sDAT = new SocketUdp( nPortDAT );

        SocketTcp sCTL = new SocketTcp( remote, nPortCTL );
        String peer = sCTL.getPeer();
        sDAT.setHost( peer );

        rREC = new Recorder(AppParams.inDevice);
        pPLY = new Player(AppParams.outDevice);

        
        Thread tSendREC = new Thread(new Runnable() {
            public void run() {
                System.out.println("thread SendREC start");
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
    
//                        System.out.println( "thread SendREC sDAT.send(rt.getPacketLen()) = "+ rt.getPacketLen() +" bytes." );
                        int s = sDAT.send( rt.getPacketBuf(), rt.getPacketLen() );
                        if ( s<=0 ) {
                            System.out.println("thread SendREC: socket closed");
                            FIN=true;
                            break;
                        }
                    }
                }

                System.out.println("thread SendREC end");
            }

        });

        Thread tPlayRCV = new Thread(new Runnable() {
            public void run() {
                System.out.println("thread PlayRCV start");
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
//                        System.out.println( "thread PlayRCV sDAT.recv(rt.getPacketLen()) = "+ r + " bytes." );

                        rt.recvBuffer( b, l );
//                        Utils.FadeInOut_8BitsMono(b,AppParams.bufferSize,0,20);

                        pPLY.sendPlayer( b, 0, l );

/*
                        int offset=0;
                        int framesToSkeep = rt.sync( AppParams.frameRate );
                        if ( framesToSkeep > 0 ) {
                            offset = framesToSkeep * AppParams.sampleSizeInBytes ;
                            System.out.println( "thread PlayRCV framesToSkeep = "+framesToSkeep+", offset = "+offset );
                        }
                        pPLY.sendPlayer(b, offset, l-offset);
                        System.out.println( "thread PlayRCV pPLY.sendPlayer(off,len) = "+ offset +","+ (l-offset) );
*/
                    }
                }

                System.out.println("thread PlayRCV end");
            }
        });

        tSendREC.start();
        tPlayRCV.start();

        Thread.sleep(100, 0); //espera 0,1seg
        while (!FIN ) {
            try {
                System.out.print("cli> ");
                int k = System.in.read();
                switch ( k ) {
                case 'S':
                case 's':
                    System.out.println("Recorder stats:");
                    rREC.showStats();
                    System.out.println("Player stats:");
                    pPLY.showStats();
                    break;
                case 'Q':
                case 'q':
                    FIN=true;
                    break;
                default:
                    continue;
                }
            }
            catch (IOException e){
                System.out.println("Error reading from user");
            }
        }

        Thread.sleep(100, 0); //espera 0,1seg

        rREC.stopRecorder();
        pPLY.stopPlayer();

        Thread.sleep(100, 0); //espera 0,1seg

        sDAT.close();
        sCTL.close();
    }
    
}


