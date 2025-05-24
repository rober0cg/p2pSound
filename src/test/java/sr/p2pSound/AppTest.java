package sr.p2pSound;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


import sr.Sound.*;


/**
 * Unit test for simple App.
 */
public class AppTest {

    /**
     * Rigorous Test :-)
     */
    @Test
    public void test01_Send() {
        String test = "test01";
        System.out.println();
        System.out.println(test + " - INICIO");
        System.out.println();

        int rc=0;

        int l = 32;
        byte bSend[] = new byte[l];

        for ( int i=0 ; i<l ; i++ ) {
            bSend[i]= (byte)i;
        }

        SimpleRTPacket rtSend = new SimpleRTPacket( l );

        System.out.println("rtSend.getDataLen()="+rtSend.getnDataLen());
        System.out.println("rtSend.getPacketLen()="+rtSend.getnPacketLen());
        System.out.println();

        header();

        rc = rtSend.sendBuffer ( bSend, l );
        assertTrue(rc==0);

        rtSend.printsend(test);
        dump(rtSend.getbPacketBuf(),rtSend.getnPacketLen());


        final int TEST01_NUM_ITER = 50;
        final int TEST01_MS_DEALY = 10;

        for ( int i=1; i<TEST01_NUM_ITER ; i++ ) {
            try { Thread.sleep(TEST01_MS_DEALY);} catch (InterruptedException e) { }

            rc = rtSend.sendBuffer ( bSend, l );
            assertTrue(rc==0);

        }

        System.out.println();
        System.out.printf("tras %d iters con %d ms en cada una...\n", TEST01_NUM_ITER, TEST01_MS_DEALY);
        System.out.println();

        rtSend.printsend(test);
        dump(rtSend.getbPacketBuf(),rtSend.getnPacketLen());

        System.out.println();
        System.out.println(test + " - FIN");
        System.out.println();

        assertTrue(rc==0);
    }


    @Test
    public void test02_Recv() {
        String test ="test02";
        System.out.println();
        System.out.println(test + " - INICIO");
        System.out.println();

        int rc=0;

        int l = 32;
        byte bSend[] = new byte[l];
        byte bRecv[] = new byte[l];

        for ( int i=0 ; i<l ; i++ ) {
            bSend[i]= (byte)i;
        }

        SimpleRTPacket rtSend = new SimpleRTPacket( l );
        SimpleRTPacket rtRecv = new SimpleRTPacket( l );

        System.out.println("rtSend.getDataLen()="+rtSend.getnDataLen());
        System.out.println("rtSend.getPacketLen()="+rtSend.getnPacketLen());
        System.out.println();

        header();

        // recibo lo mismo que envío
        System.out.println ( test + ": 01 - recibo lo mismo que envío");
        rtSend.sendBuffer ( bSend, l );

        rtSend.printsend(test+".rtSend");
        dump(rtSend.getbPacketBuf(),rtSend.getnPacketLen());

        System.arraycopy( rtSend.getbPacketBuf(), 0, rtRecv.getbPacketBuf(), 0, rtSend.getnPacketLen() );

        rc = rtRecv.recvBuffer ( bRecv, l );
        System.out.println ( test + " rtRecv.recvBuffr rc = " + rc);

        rtRecv.printrecv(test+".rtRecv");
        dump(rtRecv.getbPacketBuf(),rtRecv.getnPacketLen());

        assertTrue( rtSend.getnPacketLen()   == rtRecv.getnPacketLen()   );
        assertTrue( rtSend.getnDataLen()     == rtRecv.getnDataLen()     );
        assertTrue( rtSend.getlSequenceNum() == rtRecv.getlSequenceNum() );
        assertTrue( rtSend.getTsTimeStamp()   == rtRecv.getTsTimeStamp()   );
        assertTrue(rc==0);
        System.out.println();


        // forzar secuencia repetida == a la última recibida
        System.out.println ( test + ": 02 - forzar secuencia repetida");

        rtSend.setlSequenceNum (0);
        rtSend.sendBuffer( bSend, l ); // seq == 1

        System.arraycopy( rtSend.getbPacketBuf(), 0, rtRecv.getbPacketBuf(), 0, rtSend.getnPacketLen() );

        rc = rtRecv.recvBuffer ( bRecv, l );
        System.out.println ( test + " rtRecv.recvBuffr rc = " + rc);

        rtRecv.printrecv(test+".rtRecv");
        dump(rtRecv.getbPacketBuf(),rtRecv.getnPacketLen());

        assertTrue( rtSend.getnPacketLen()   == rtRecv.getnPacketLen()   );
        assertTrue( rtSend.getnDataLen()     == rtRecv.getnDataLen()     );
        assertTrue( rtSend.getlSequenceNum() == rtRecv.getlSequenceNum() );
        assertTrue( rtSend.getTsTimeStamp()   == rtRecv.getTsTimeStamp()   );
        assertTrue(rc==rtSend.rcRecvRepetido);
        System.out.println();


        // forzar salto de secuencia >+1 ultima recibida
        System.out.println ( test + ": 03 - forzar salto de secuencia");
        rtSend.setlSequenceNum (2);
        rtSend.sendBuffer( bSend, l ); // seq == 3

        rtSend.printsend(test+".rtSend");
        dump(rtSend.getbPacketBuf(),rtSend.getnPacketLen());

        System.arraycopy( rtSend.getbPacketBuf(), 0, rtRecv.getbPacketBuf(), 0, rtSend.getnPacketLen() );

        rc = rtRecv.recvBuffer ( bRecv, l );
        System.out.println ( test + " rtRecv.recvBuffr rc = " + rc);

        rtRecv.printrecv(test+".rtRecv");
        dump(rtRecv.getbPacketBuf(),rtRecv.getnPacketLen());

        assertTrue( rtSend.getnPacketLen()   == rtRecv.getnPacketLen()   );
        assertTrue( rtSend.getnDataLen()     == rtRecv.getnDataLen()     );
        assertTrue( rtSend.getlSequenceNum() == rtRecv.getlSequenceNum() );
        assertTrue( rtSend.getTsTimeStamp()   == rtRecv.getTsTimeStamp()   );
        assertTrue(rc==rtSend.rcRecvSalto);
        System.out.println();


        // forzar secuencia repetida < que la última recibida
        System.out.println ( test + ": 04 - forzar secuencia repetida tras salto");
        rtSend.setlSequenceNum (0);
        rtSend.sendBuffer( bSend, l ); // seq == 1

        System.arraycopy( rtSend.getbPacketBuf(), 0, rtRecv.getbPacketBuf(), 0, rtSend.getnPacketLen() );

        rc = rtRecv.recvBuffer ( bRecv, l );
        System.out.println ( test + " rtRecv.recvBuffr rc = " + rc);

        rtRecv.printrecv(test+".rtRecv");
        dump(rtRecv.getbPacketBuf(),rtRecv.getnPacketLen());

        assertTrue( rtSend.getnPacketLen()   == rtRecv.getnPacketLen()   );
        assertTrue( rtSend.getnDataLen()     == rtRecv.getnDataLen()     );
        assertTrue( rtSend.getlSequenceNum() <  rtRecv.getlSequenceNum() );
        assertTrue( rtSend.getTsTimeStamp()   == rtRecv.getTsTimeStamp()   );
        assertTrue(rc==rtSend.rcRecvRepetido);
        System.out.println();



        System.out.println();
        System.out.println(test + " - FIN");
        System.out.println();

        assertTrue(true);
    }


    @Test
    public void test03_SendRecv() {
        String test ="test03";
        System.out.println();
        System.out.println(test + " - INICIO");
        System.out.println();


        int l = 32;
        byte bSend[] = new byte[l];
        byte bRecv[] = new byte[l];

        for ( int i=0 ; i<l ; i++ ) {
            bSend[i]= (byte)i;
        }

        SimpleRTPacket rtSend = new SimpleRTPacket( l );
        SimpleRTPacket rtRecv = new SimpleRTPacket( l );


        final int TEST03_NUM_ITER = 50;
        final int TEST03_MS_DEALY = 10;

        for ( int i=0; i<TEST03_NUM_ITER ; i++ ) {
            rtSend.sendBuffer ( bSend, l );
            dump(rtSend.getbPacketBuf(),rtSend.getnDataLen());
            rtSend.printsend(test+".rtSend");

            System.arraycopy( rtSend.getbPacketBuf(), 0, rtRecv.getbPacketBuf(), 0, rtSend.getnPacketLen() );
            try { Thread.sleep(TEST03_MS_DEALY);} catch (InterruptedException e) { }

            rtRecv.recvBuffer ( bRecv, l);
            dump(rtRecv.getbPacketBuf(),rtSend.getnDataLen());
            rtRecv.printrecv(test+".rtRecv");

            System.out.println("--------");
        }

        System.out.println();
        System.out.println(test + " - FIN");
        System.out.println();

        assertTrue(true);
    }

    @Test
    public void test04_LongToByteToLong(){
        String test ="test04";
        System.out.println();
        System.out.println(test + " - INICIO");
        System.out.println();

        long t, l;

        t = 0L;
        l = l2b2l(t);
        assertTrue(t==l);
        System.out.println();

        t = 1024L;
        l = l2b2l(t);
        assertTrue(t==l);
        System.out.println();

        t = 1000000000L;
        l = l2b2l(t);
        assertTrue(t==l);
        System.out.println();

        t = 2000000000L;
        l = l2b2l(t);
        assertTrue(t==l);
        System.out.println();

        t = 3000000000L;
        l = l2b2l(t);
        assertTrue(t==l);
        System.out.println();

        t = 4000000001L;
        l = l2b2l(t);
        assertTrue(t==l);
        System.out.println();

        System.out.println();
        System.out.println(test + " - FIN");
        System.out.println();

        assertTrue(true);
    }

    private long l2b2l ( long t ) {
        long l;
        byte[] b = new byte[4];


        System.out.printf("t=%d - %08x\n", t, t);

        b[0] = (byte) (t & 0xff); t >>= 8;
        b[1] = (byte) (t & 0xff); t >>= 8;
        b[2] = (byte) (t & 0xff); t >>= 8;
        b[3] = (byte) (t & 0xff); t >>= 8;

        dump ( b, 4);

        l = 0L;
        l <<= 8; l |= ( b[3] & 0xff);
        l <<= 8; l |= ( b[2] & 0xff);
        l <<= 8; l |= ( b[1] & 0xff);
        l <<= 8; l |= ( b[0] & 0xff);

        System.out.printf("l=%d - %08x\n", l, l);


        
        return l;
    }


    private void header () {
        System.out.println("34 12|xx xx|xx xx xx xx|xx xx xx xx|data");
    }
    private void dump ( byte[] b, int l) {
        int i;
        for ( i=0; i<l; i++ ) {
            System.out.print( String.format( "%02x ", b[i] ) );
            if ( i    == 30 ) { System.out.print("..."); break; } // pintar sólo una línea
            if ( i%32 == 31 )  System.out.println();
        }
        if ( i%32 != 0 ) System.out.println();
    }
}
