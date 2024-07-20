package sr.Sound;

public class AppParams {

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

    public static int packet1Size = 800;
    public static int packet1Factor = 20;

    
    public AppParams() {
        // TODO Auto-generated constructor stub
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
    public static void setFrameRate ( int fr ) {
        frameRate = fr;
        packetSize = (int) (sampleRate / frameRate) ;
        bufferSize= ( packetSize * channels * sampleSizeInBytes ) ;
    }
    public static void setPacket1Size ( int p1s ) {
        packet1Size = p1s;
        packet1Factor = packet1Size / packetSize;
    }
    public static void setPlayerQueueSize ( int pqs ) {
        packet1Factor = pqs;
        packet1Size = packet1Factor / packetSize;
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
    public static int getPacket1Size() {
        return packet1Size;
    }

    
    public static void show() {
        System.out.println("AppParams = {" );
        System.out.println("\t inputDevice = " + inDevice );
        System.out.println("\t outputDevice = " + outDevice );
        System.out.println("\t sampleRate = " + sampleRate );
        System.out.println("\t sampleSizeInBits = " + sampleSizeInBits );
        System.out.println("\t sampleSizeInBytes = " + sampleSizeInBytes );
        System.out.println("\t channels = " + channels );
        System.out.println("\t signed = " + signed );
        System.out.println("\t bigEndian = " + bigEndian );
        System.out.println("\t packetSize = " + packetSize );
        System.out.println("\t frameRate = " + frameRate );
        System.out.println("\t bufferSize = " + bufferSize );
        System.out.println("\t packet1Factor = " + packet1Factor );
        System.out.println("\t packet1Size = " + packet1Size );
        System.out.println("}" );
    }

}
