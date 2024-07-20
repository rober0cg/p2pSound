package sr.Sound;

// RTCP Header
//        0                   1                   2                   3
//        0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
//        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//header  |V=2|P|    RC   |   PT=RR=201   |             length            |
//        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//        |                     SSRC of packet sender                     |
//        +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
//report  |                           fraction lost                       |
//block   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//1       |              cumulative number of packets lost                |
//        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//        |           extended highest sequence number received           |
//        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//        |                      interarrival jitter                      |
//        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//        |                         last SR (LSR)                         |
//        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//        |                   delay since last SR (DLSR)                  |
//        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

// SimpleRTPacket Header
//        0                   1                   2                   3
//        0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
//        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//header  |  sequenceNumbre (b[0], b[1])  |    dataLength (b[0], b[1])    |
//        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//        |             intTimeStamp (b[0], b[1], b[2], b[3]              |
//        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+



public class SimpleRTPacket {
    //size of the RTP header:
    private static int HEADER_SIZE = 8;

    //Fields that compose the MyRTP header
    private int sequenceNum; // 2 bytes: valor entre 0 y 64K 
    private int timeStamp; // 4 bytes: valor de System.currentTimeMillis() - firstTimeMillis;
    private int dataLen; // n bytes de "carga útil"

    //Bitstream of the RTP packet
    private byte[] packetBuf;
    private int packetLen ;

    // Control tiempo
    private long firstTimeStamp = 0L; // primer envío o primera recepción
    private long recvTimeStamp; // instante recepción


// constructor para reutilizar su buffer con setter y getter
    public SimpleRTPacket ( int len) {
        sequenceNum = 0;
        timeStamp = 0;
        dataLen = len;
        packetLen = HEADER_SIZE + len;
        packetBuf = new byte[ packetLen ];
        validate("SimpleRTPacket(len)");
    }


    private boolean validate( String s ) {
        if ( packetLen != (HEADER_SIZE + dataLen) ) {
            print(s);
            System.out.println (
                "SimpleRTPacket ERROR in '"+ s +"': "+
                    "packetLen("+ packetLen +") != "+
                    "HEADER("+ HEADER_SIZE +") + dataLen("+ dataLen +")"
            );
            return false;
        }
        return true;
    }
    
    public int getSequenceNum() {
        return sequenceNum;
    }
    public int getDataLen() {
        return dataLen;
    }
    public int getTimeStamp() {
        return timeStamp;
    }
    
    public int getPacketLen() {
        return packetLen;
    }
    public byte[] getPacketBuf() {
        return packetBuf;
    }


    public void sendBuffer ( byte[] buf, int len) {
        dataLen = len;
        packetLen = HEADER_SIZE + dataLen;

        sequenceNum++;
        packetBuf[0] = (byte) ((sequenceNum >>>  0) & 0xff);
        packetBuf[1] = (byte) ((sequenceNum >>>  8) & 0xff);

        packetBuf[2] = (byte) ((dataLen     >>>  0) & 0xff);
        packetBuf[3] = (byte) ((dataLen     >>>  8) & 0xff);

        long now = System.currentTimeMillis();
        if ( firstTimeStamp==0L ) {
            firstTimeStamp = now;
        }
        timeStamp = (int)(now - firstTimeStamp);
        packetBuf[4] = (byte) ((timeStamp   >>>  0) & 0xff);
        packetBuf[5] = (byte) ((timeStamp   >>>  8) & 0xff);
        packetBuf[6] = (byte) ((timeStamp   >>> 16) & 0xff);
        packetBuf[7] = (byte) ((timeStamp   >>> 24) & 0xff);

        System.arraycopy( buf, 0, packetBuf, HEADER_SIZE, len );

//        dump ( packetBuf, HEADER_SIZE );

        validate("sendBuffer(buf,len)");
    }


    public void recvBuffer ( byte[] buf, int len ) {
//        System.arraycopy ( buf, 0, packetBuf, 0, HEADER_SIZE );
//        System.arraycopy ( buf, 0, packetBuf, 0, len );

        int[] ub = new int[HEADER_SIZE];
        for (int i=0; i<HEADER_SIZE; i++ ) {
            ub[i] = Byte.toUnsignedInt( packetBuf[i] );
        }
        sequenceNum = ub[0] + (ub[1]<<8) ;
        dataLen     = ub[2] + (ub[3]<<8) ;
        timeStamp   = ub[4] + (ub[5]<<8) + (ub[6]<<26) + (ub[7]<<24) ;
        packetLen = HEADER_SIZE + dataLen;

        long now = System.currentTimeMillis();
        if ( firstTimeStamp==0L ) {
            firstTimeStamp = now;
        }
        recvTimeStamp = now;

//        dump ( buf, HEADER_SIZE );
        System.arraycopy ( packetBuf, HEADER_SIZE, buf, 0, packetLen-HEADER_SIZE );

        validate("recvBuffer(buf,len)");        
    }

    public int sync ( int frameRate ) {
        int remoteTimeStamp = timeStamp;
        int localTimeStamp = (int)( recvTimeStamp - firstTimeStamp );

        int diffTimeStamp = remoteTimeStamp - localTimeStamp; 

        if ( diffTimeStamp == 0) { // sincronizados
            return 0;
        }
        if ( diffTimeStamp > 0 ) { // recibido antes de lo esperado
            return 0;
        }

        if ( diffTimeStamp < 0 ) { // recibido con retraso respecto a lo esperado
        // identificar retraso y reducir buffer de muestras expiradas
            System.out.println("SimpleRTPacket.sync() retraso = "+ diffTimeStamp + "(" + remoteTimeStamp + "-" + localTimeStamp + ")" );
            
            int framePeriod = 1000 / frameRate ; // milisegundos entre cada frame
            int framesToSkeep = -diffTimeStamp / framePeriod ;
            
            return framesToSkeep;
        }

        return 0;
    }

    public void print( String s ) {
        System.out.println("RTPacket in "+ s +"= {"+
                "\n\tsequenceNum = "+sequenceNum+ " (" +String.format("%02x %02x", packetBuf[0], packetBuf[1]) + ")"+
                "\n\tdataLen     = "+dataLen+" (" +String.format("%02x %02x", packetBuf[2], packetBuf[3]) + ")"+
                "\n\ttimeStamp   = "+timeStamp+" (" +String.format("%02x %02x %02x %02x", packetBuf[4], packetBuf[5], packetBuf[6], packetBuf[7]) + ")"+
                "\n\tpacketLen   = "+packetLen+
                "\n\tdataBuff[]  = ["+packetBuf[HEADER_SIZE+0]+","+packetBuf[HEADER_SIZE+1]+","+packetBuf[HEADER_SIZE+2]+","+packetBuf[HEADER_SIZE+3]+"...]"+
                "\n}"
            );
/*
        System.out.println("RTPacket in "+ s +"= {"+
            "\n\tsequenceNumber = "+sequenceNum+
            "\n\tdataLength = "+dataLen+
            "\n\tincTimeStamp = "+timeStamp+
            "\n\tpacketLen = "+packetLen+
            "\n\tpacktBuf[] = ["+packetBuf[0]+","+packetBuf[1]+","+packetBuf[2]+","+packetBuf[3]+"...]"+
            "\n}"
        );
*/
    }

/*
    private void dump ( byte[] b, int l) {
        int i;
        for ( i=0; i<l; i++ ) {
            System.out.print( String.format( "%02x ", b[i] ) );
            if ( i%16 == 15 )  System.out.println();
        }
        if ( i%16 != 15 ) System.out.println();
    }
*/
}
