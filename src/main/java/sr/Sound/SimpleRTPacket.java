package sr.Sound;

import sr.Socket.SocketUdp;

import sr.SimpleLog.SimpleLog;

// SimpleRTPacket Header
//        +---------------+---------------+---------------+---------------+
//        |               |    1          |        2      |            3  |
//        |0 1 2 3 4 5 6 7|8 9 0 1 2 3 4 5|6 7 8 9 0 1 2 3|4 5 6 7 8 9 0 1|
//        +---------------+---------------+---------------+---------------+
//header  |     0xbc      |    command    |    dataLength (b[0], b[1])    |
//        +-------------------------------+-------------------------------+
//        |            sequenceNumber (b[0], b[1], b[2], b[3])            |
//        +-------------------------------+-------------------------------+
//        |             intTimeStamp (b[0], b[1], b[2], b[3])             |
//        +-------------------------------+-------------------------------+
//data    |                                                               |
//        .                              data                             .
//        |                                                               |
//        +-------------------------------+-------------------------------+



public class SimpleRTPacket {
    private static final String TAG = "SimpleRTPacket" ;

    //size of the MyRTP header:
    private static int HEADER_SIZE = 12;
    private static final int HEADER_OFFSET_ID   = 0;
    private static final int HEADER_OFFSET_CMD  = 1;
    private static final int HEADER_OFFSET_DLEN = 2;
    private static final int HEADER_OFFSET_SEQ  = 4;
    private static final int HEADER_OFFSET_TS   = 8;
    private static final int HEADER_OFFSET_DATA = HEADER_SIZE ;


    //Fields that compose the MyRTP header
    private static final int idMyRTP = 0xbc;

    private int nCommand;
    private static final int nCmdSend = 0; // command DATA
    private static final int nCmdSendTS0 = 1;  // commmand DATA + resetTimeStamp
    private static final long usMaxTS = ( 30L * 1000L * 1000L ); // 30 seg para test

    private long lSequenceNum; // 4 bytes: valor entre 0 y 4.000.000.000 
    private long tsTimeStamp; // 4 bytes: microsegundos desde primer envío
    private int nDataLen; // n bytes de "carga útil"

    //Bitstream of the RTP packet
    private byte[] bPacketBuf;
    private int nPacketLen ;

    // Control tiempo (us)
    private long usTimeFirst = 0L;
    private long usTimeTodos = 0L ;
 
    private long usDSend = 0L;
    @SuppressWarnings("unused")
    private long usDSendDelay = 0L;
    private long usFirstSend = 0L;
    @SuppressWarnings("unused")
    private long usPrevSend = 0L;
    private long usLastSend = 0L;
    private long usDRecv = 0L;
    private long usDRecvDelay = 0L;
    private long usFirstRecv = 0L;
    @SuppressWarnings("unused")
    private long usPrevRecv = 0L;
    private long usLastRecv = 0L;

    private static final int SEND_NUMD = 10000;
    private static final int RECV_NUMD = 1000;
    private static int dSend = SEND_NUMD;
    private static int dRecv = RECV_NUMD;

    public static final int rcSendOK = 0;
    public static final int rcSendERR = -1;
    public static final int rcRecvOK = 0;
    public static final int rcRecvERR = -1;
    public static final int rcRecvSalto = 1;
    public static final int rcRecvIdUfo = 2;
    public static final int rcRecvRetraso = 3;
    public static final int rcRecvRepetido = -2;
    public static final int rcRecvDiffLen = -3;

// constructor para reutilizar su buffer con setter y getter
    public SimpleRTPacket ( ) {
        lSequenceNum = 0;
        tsTimeStamp = 0;
        usTimeFirst = 0L;
        usTimeTodos = 0L;
        nDataLen = 0;
        nPacketLen = HEADER_SIZE + nDataLen;
        bPacketBuf = null;
        dSend = SEND_NUMD;
        dRecv = RECV_NUMD;
        validate("SimpleRTPacket()");
    }
    public SimpleRTPacket ( int len) {
        lSequenceNum = 0;
        tsTimeStamp = 0;
        usTimeFirst = 0L;
        usTimeTodos = 0L;
        nDataLen = len;
        nPacketLen = HEADER_SIZE + nDataLen;
        bPacketBuf = new byte[ nPacketLen ];
        dSend = SEND_NUMD;
        dRecv = RECV_NUMD;
        validate("SimpleRTPacket(len)");
    }
    public SimpleRTPacket ( long usFirst, long usTodos ) {
        lSequenceNum = 0;
        tsTimeStamp = 0;
        usTimeFirst = usFirst;
        usTimeTodos = usTodos;
        nDataLen = 0;
        nPacketLen = HEADER_SIZE + nDataLen;
        bPacketBuf = null;
        dSend = SEND_NUMD;
        dRecv = RECV_NUMD;
        validate("SimpleRTPacket(us1st,usAll)");
    }

    public void setDataLen ( int len ){
        nDataLen = len;
        nPacketLen = HEADER_SIZE + len;
        bPacketBuf = new byte[ nPacketLen ];
        validate("setDataLen(len)");
    }

    private boolean validate( String s ) {
        if ( nPacketLen != (HEADER_SIZE + nDataLen) ) {
            printlong(s);
            SimpleLog.LOGE(TAG,
                "SimpleRTPacket ERROR in '"+ s +"': "+
                    "packetLen("+ nPacketLen +") != "+
                    "HEADER("+ HEADER_SIZE +") + dataLen("+ nDataLen +")"
            );
            return false;
        }
        return true;
    }

//  getter & setter
    public long   getlSequenceNum()         { return lSequenceNum; }
    public int    getnDataLen()             { return nDataLen; }
    public long   getTsTimeStamp()          { return tsTimeStamp; }
    public int    getnPacketLen()           { return nPacketLen; }
    public byte[] getbPacketBuf()           { return bPacketBuf; }
    public void   setlSequenceNum(long sn)  { lSequenceNum = sn; } // sólo para tests

//  prints
    public void print( String s ) {
        if ( SimpleLog.getLogLevel() <= SimpleLog.ll_DEBUG ) {
            System.out.println(s+"= {"+lSequenceNum+", "+tsTimeStamp +", "+nDataLen+", "+nPacketLen+"}");
        }
    }
    public void printlong( String s ) {
        if ( SimpleLog.getLogLevel() <= SimpleLog.ll_DEBUG ) {
            System.out.println(
                "RTPacket in "+ s +"= {"+
                "\n\tnPacketLen   = "+nPacketLen+
                "\n\tlSequenceNum = "+lSequenceNum+ " (" +String.format("%02x %02x %02x %02x", bPacketBuf[4], bPacketBuf[5], bPacketBuf[6], bPacketBuf[7]) + ")"+
                "\n\ttsTimeStamp  = "+tsTimeStamp+" (" +String.format("%02x %02x %02x %02x", bPacketBuf[8], bPacketBuf[9], bPacketBuf[10], bPacketBuf[11]) + ")"+
                "\n\tnDataLen     = "+nDataLen+" (" +String.format("%02x %02x", bPacketBuf[2], bPacketBuf[3]) + ")"+
                "\n\tdataBuff[]  = ["+bPacketBuf[HEADER_SIZE+0]+","+bPacketBuf[HEADER_SIZE+1]+","+bPacketBuf[HEADER_SIZE+2]+","+bPacketBuf[HEADER_SIZE+3]+"...]"+
                "\n}"
            );
        }
    }
    public void printsend( String s ) {
        if ( SimpleLog.getLogLevel() <= SimpleLog.ll_INFO ) {
            System.out.println(s+
                "={ cmd:"+nCommand+", seq:"+lSequenceNum+", ts:"+tsTimeStamp+ ", dlen:"+nDataLen+", plen:"+nPacketLen+
//                ", 1st:"+ (usLastSend - usFirstSend) +
//                ", prv:"+ (usLastSend - usPrevSend) +
//                ", dly:"+ usSendDelay +
                "}"
            );
        }
    }
    public void printrecv( String s ) {
        if ( SimpleLog.getLogLevel() <= SimpleLog.ll_INFO ) {
            System.out.println(s+
                "={ cmd:"+nCommand+", seq:"+lSequenceNum+", ts:"+tsTimeStamp+ ", dlen:"+nDataLen+", plen:"+nPacketLen+
                ", 1st:"+ (usLastRecv - usFirstRecv) +
//                ", prv:"+ (usLastRecv - usPrevRecv) +
                ", dly:"+ usDRecvDelay + "(" +  usTimeFirst + ")" +
                "}"
            );
        }
    }

//  microsegundos
    private long microTime() {
        return ( System.nanoTime() / 1000L ) ;
    }


//  type to buffer
    private void longToByte4 ( long l, byte[] b, int offset ){
        b[offset+0] = (byte) (l & 0xff); l >>= 8;
        b[offset+1] = (byte) (l & 0xff); l >>= 8;
        b[offset+2] = (byte) (l & 0xff); l >>= 8;
        b[offset+3] = (byte) (l & 0xff);
        return;
    }
    private void intToByte2 ( int i, byte[] b, int offset ){
        b[offset+0] = (byte) (i & 0xff); i >>= 8;
        b[offset+1] = (byte) (i & 0xff);
        return;
    }
    private void intToByte1 ( int i, byte[] b, int offset ){
        b[offset+0] = (byte) (i & 0xff);
        return;
    }

//  buffer to type
    private long byteToLong4 ( byte[] b, int offset ) {
        long l = 0L;
        l |= ( b[offset+3] & 0xff); l <<= 8;
        l |= ( b[offset+2] & 0xff); l <<= 8;
        l |= ( b[offset+1] & 0xff); l <<= 8;
        l |= ( b[offset+0] & 0xff);
        return l;
    }
    private int byteToInt2 ( byte[] b, int offset ) {
        int i = 0;
        i |= ( b[offset+1] & 0xff); i <<= 8;
        i |= ( b[offset+0] & 0xff);
        return i;
    }
    private int byteToInt1 ( byte[] b, int offset ) {
        int i = 0;
        i |= ( b[offset+0] & 0xff);
        return i;
    }

//
//  S E N D s
//
    public int send ( SocketUdp s, byte[] buf, int len) {
        int rc;
        
        rc = sendBuffer(buf, len);
        if ( rc != rcSendOK )
            return rc;

        rc = s.send( bPacketBuf, nPacketLen );
        if ( rc < 0 ) rc = rcSendERR;
        else          rc = rcSendOK;

        return  rc;
    }
    public int sendBuffer ( byte[] buf, int len) {

        nDataLen = len;
        nPacketLen = HEADER_SIZE + nDataLen;

        long now = microTime();
        if ( usFirstSend == 0L )  usFirstSend = now;
        if ( usLastSend  == 0L )  usLastSend  = now;
        usPrevSend = usLastSend;
        usLastSend = now;

        // protoID
        intToByte1(idMyRTP, bPacketBuf, HEADER_OFFSET_ID);

        // Command
        if ( usDSend > usMaxTS ) {
            nCommand = nCmdSendTS0;
            usFirstSend = now;
        }
        else {
            nCommand = nCmdSend;
        }
        intToByte1(nCommand, bPacketBuf, HEADER_OFFSET_CMD);

        // dataLen
        intToByte2(nDataLen, bPacketBuf, HEADER_OFFSET_DLEN);

        // sequenceNumber
        lSequenceNum++;
        longToByte4(lSequenceNum, bPacketBuf, HEADER_OFFSET_SEQ);

        // tsTimeStamp
        tsTimeStamp = now - usFirstSend ;
        longToByte4(tsTimeStamp, bPacketBuf, HEADER_OFFSET_TS);

        usDSend = tsTimeStamp;
        usDSendDelay = usDSend - (usLastSend - usFirstSend) ;

        System.arraycopy( buf, 0, bPacketBuf, HEADER_OFFSET_DATA, len );

        if ( ++dSend >= SEND_NUMD || nCommand==nCmdSendTS0 ) {
            printsend("sendBuffer");
            dSend = 0;
        }

        return rcSendOK;
    }

//
//  R E C V s
//
    public int recv ( SocketUdp s, byte[] buf, int len ) {
        int rc=rcRecvOK;

        int l = s.recv( bPacketBuf, nPacketLen );
        if ( l<=0 ) {
            SimpleLog.LOGE(TAG, "recv: SocketUdp cerrado");
            return rcRecvERR;
        }

        rc = recvBuffer(buf, len);
        return rc;
    }
    public int recvBuffer ( byte[] buf, int len ) {
        int rc=rcRecvOK;

        // idProto
        int idProto = byteToInt1 ( bPacketBuf, HEADER_OFFSET_ID);
        if ( idProto != idMyRTP ) { // error leve aunque raro raro
            SimpleLog.LOGW(TAG, "recvBuffer: protocolo desconocido "+idProto);
            rc = rcRecvIdUfo;
        }

        // nCommand
        nCommand = byteToInt1 ( bPacketBuf, HEADER_OFFSET_CMD);

        // nDataLen
        nDataLen = byteToInt2 ( bPacketBuf, HEADER_OFFSET_DLEN);
        nPacketLen = HEADER_SIZE + nDataLen;
        if ( len != nDataLen ) {
            SimpleLog.LOGW(TAG, "AVISO: recvBuffer: len("+len+") != nDataLen("+nDataLen+")" );
            printrecv("recvBuffer");
            printlong("recvBuffer len != nDataLen");
            if ( nDataLen > len ) { nDataLen = len; }
            rc = rcRecvDiffLen;
        }

        // lSequenceNumber
        long seqNum = byteToLong4 ( bPacketBuf, HEADER_OFFSET_SEQ);
        if ( seqNum <= lSequenceNum ) { // repetido!!
            SimpleLog.LOGW(TAG, "AVISO: recvBuffer: secuencia repetida "+seqNum);
            printrecv("recvBuffer");
            printlong("recvBuffer secuencia repetida");
            rc = rcRecvRepetido;
        }
        else
        if ( seqNum > lSequenceNum+1 ) {
            SimpleLog.LOGW(TAG,"AVISO: recvBuffer: salto en secuencia: de "+ lSequenceNum +" a "+seqNum);
            lSequenceNum = seqNum;
            rc = rcRecvSalto;
        }
        else {
            lSequenceNum = seqNum;
            rc = rcRecvOK;
        }

        // tsTimeStamp
        tsTimeStamp = byteToLong4 ( bPacketBuf, HEADER_OFFSET_TS);
        usDRecv = tsTimeStamp;

        long now = microTime();
        if ( usFirstRecv == 0L ) { usFirstRecv = now; }
        if ( usLastRecv  == 0L ) { usLastRecv  = now; }
        usPrevRecv = usLastRecv;
        usLastRecv = now;

        if ( nCommand==nCmdSendTS0 ) { // llega TS a 0: inicializamos y compensamos
            usFirstRecv = now + usDRecvDelay;
        }

        usDRecvDelay = usDRecv - (usLastRecv - usFirstRecv) ;
        if ( usTimeFirst > 0L ) {
            if ( usDRecvDelay > ( usTimeFirst + usTimeTodos ) )
                rc = rcRecvRetraso;
        }


        System.arraycopy ( bPacketBuf, HEADER_OFFSET_DATA, buf, 0, nDataLen );

        if ( ++dRecv >= RECV_NUMD || nCommand==nCmdSendTS0 ) {
            printrecv("recvBuffer");
            dRecv = 0;
        }

        return rc;
    }

}

