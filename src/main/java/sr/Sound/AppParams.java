package sr.Sound;

import sr.SimpleLog.SimpleLog;


public class AppParams {
    private static final String TAG = "AppParams" ;

    public static String inDevice = "MICRÓFONO" ;
    public static String outDevice = "ALTAVOCES" ;

    public static String inputFile = "inputfile.pcm" ;
    public static String outputFile = "outputfile.pcm" ;

    public static float sampleRate = 8000.0f; //16000.0f;
    public static int sampleSizeInBits = 8;
    public static int channels = 1;
    public static boolean signed = true;
    public static boolean bigEndian = false;

    public static int packetSize = 40; // sampleRate / frameRate
    public static int frameRate = 200;  // sampleRate / packetSize
    public static int sampleSizeInBytes = sampleSizeInBits/8 ;
    public static int bufferSize = ( packetSize * channels * sampleSizeInBytes );

    public static int packetSize1 = 800;
    public static int packetFactor1 = 20;

    public static long usTimeFrame = 0L;
    public static long usTimePacket1 = 0L;
    public static long usTimePacket = 0L;


    public AppParams() {
        // nada que hacer
    }

    public static void init ( String I, String O, int r, int c, int b, int ps, int ps1 ){
        inDevice = I;
        outDevice = O;

        sampleRate = r;

        channels = c;

        sampleSizeInBits = b;
        sampleSizeInBytes = sampleSizeInBits / 8;

        packetSize = ps;
        frameRate = (int) (sampleRate / packetSize) ;
        bufferSize= ( packetSize * channels * sampleSizeInBytes ) ;

        packetSize1 = ps1;
        packetFactor1 = packetSize1 / packetSize;

        usTimeFrame = (long)(1000000.0f / (float)sampleRate) ;
        usTimePacket1 = packetSize1 * usTimeFrame;
        usTimePacket  = packetSize  * usTimeFrame;
    }

    public static void setInDevice ( String id ) {
        inDevice = id;
    }
    public static void setOutDevice ( String od ) {
        outDevice = od;
    }
    public static void setSampleRate ( int sr ) {
        sampleRate = (float)sr;
    }
    public static void setChannels ( int c ) {
        channels = c;
    }
    public static void setSampleSizeInBits ( int b ) {
        sampleSizeInBits = b;
        sampleSizeInBytes = sampleSizeInBits / 8;
    }

    public static void setPacketSize ( int ps ) {
        packetSize = ps;
        frameRate = (int) (sampleRate / packetSize) ;
        bufferSize= ( packetSize * channels * sampleSizeInBytes ) ;
    }
    public static void setPacketSize1 ( int p1s ) {
        packetSize1 = p1s;
        packetFactor1 = packetSize1 / packetSize;
    }

    public static void setFrameRate ( int fr ) {
        frameRate = fr;
        packetSize = (int) (sampleRate / frameRate) ;
        bufferSize= ( packetSize * channels * sampleSizeInBytes ) ;
    }

    public static void setPlayerQueueSize ( int pqs ) {
        packetFactor1 = pqs;
        packetSize1 = packetFactor1 / packetSize;
    }

    public static int getSampleRate() {
        return (int)sampleRate;
    }
    public static int getChannels() {
        return channels;
    }
    public static int getSampleSizeInBits() {
        return sampleSizeInBits;
    }
    public static int getPacketSize() {
        return packetSize;
    }
    public static int getPacketSize1() {
        return packetSize1;
    }
    public static long getTimePacket1() {
        return usTimePacket1;
    }
    public static long getTimePacket() {
        return usTimePacket;
    }

    
    public static void show() {
        SimpleLog.LOGD(TAG,"AppParams = {" );
        SimpleLog.LOGD(TAG,"\t inputDevice = " + inDevice );
        SimpleLog.LOGD(TAG,"\t outputDevice = " + outDevice );
        SimpleLog.LOGD(TAG,"\t sampleRate = " + sampleRate );
        SimpleLog.LOGD(TAG,"\t sampleSizeInBits = " + sampleSizeInBits );
        SimpleLog.LOGD(TAG,"\t sampleSizeInBytes = " + sampleSizeInBytes );
        SimpleLog.LOGD(TAG,"\t channels = " + channels );
        SimpleLog.LOGD(TAG,"\t signed = " + signed );
        SimpleLog.LOGD(TAG,"\t bigEndian = " + bigEndian );
        SimpleLog.LOGD(TAG,"\t packetSize = " + packetSize );
        SimpleLog.LOGD(TAG,"\t frameRate = " + frameRate );
        SimpleLog.LOGD(TAG,"\t bufferSize = " + bufferSize );
        SimpleLog.LOGD(TAG,"\t packet1Factor = " + packetFactor1 );
        SimpleLog.LOGD(TAG,"\t packet1Size = " + packetSize1 );
        SimpleLog.LOGD(TAG,"\t usTimeFrame = " + usTimeFrame);
        SimpleLog.LOGD(TAG,"\t usTimePacket = " + usTimePacket);
        SimpleLog.LOGD(TAG,"\t usTimePacket1 = " + usTimePacket1);
        SimpleLog.LOGD(TAG,"}" );
    }

}
