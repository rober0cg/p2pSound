package sr.Sound;

import javax.sound.sampled.*;

public class Player { // extends Thread
 
    private SourceDataLine spk;
    private String outputDevice="";

    private int nSends = 0, nParts = 0;
    private int nBytesSend = 0;
    private long lStartTime = 0L, lLastSend = 0L;


    public Player() {
        outputDevice="";
        initPlayer();
    }
    public Player(String device) {
        outputDevice=device;
        initPlayer();
    }

    private void initPlayer() {
        
        System.out.println("initPlayer( "+outputDevice+" )");
        try {

            //define audio format
            AudioFormat audioFormat = new AudioFormat(
                AppParams.sampleRate, AppParams.sampleSizeInBits, AppParams.channels, AppParams.signed, AppParams.bigEndian
            );

            
            //build line info with audio format
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            System.out.println ("info= " + info.toString());
            

            Mixer selectedMixer = Utils.getSelectedMixer(AppParams.outDevice);
            if (selectedMixer == null) {
              spk = (SourceDataLine) AudioSystem.getLine(info);
              System.out.println("spk (AudioSystem): "+spk.toString());
            }
            else {
              spk = (SourceDataLine) selectedMixer.getLine(info);
              System.out.println("spk (selectedMixer): "+spk.toString());
            }
            
            System.out.println ("spk.isActive=  "+ spk.isActive());
            System.out.println ("spk.isRunning= "+ spk.isRunning());
            System.out.println ("spk.isOpen=    "+ spk.isOpen());

            System.out.println ("spk.getLevel=    "+ spk.getLevel());
            System.out.println ("spk.getFormat=    "+ spk.getFormat());

            System.out.println ("spk.getBufferSize= "+ spk.getBufferSize());

            spk.flush();
            System.out.println ("spk flush OK");

//            spk.open(audioFormat, AppParams.bufferSize); //open the speakr for writing
            spk.open(audioFormat); //open the speakr for writing
            System.out.println ("spk open!!");
            System.out.println ("spk.getBufferSize= "+ spk.getBufferSize());

            System.out.println("playing initialized...");
          
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        }

    }

    
    public void startPlayer() {
        System.out.println("startPlayer()");
        try {

            spk.start(); // start writing to speaker

            nSends = 0;
            nParts = 0;
            nBytesSend = 0;
            lStartTime = System.currentTimeMillis();
            lLastSend = lStartTime+1;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println ("spk.isActive=  "+ spk.isActive());
        System.out.println ("spk.isRunning= "+ spk.isRunning());
        System.out.println ("spk.isOpen=    "+ spk.isOpen());

        System.out.println ("spk.getLevel=    "+ spk.getLevel());
        System.out.println ("spk.getFormat=    "+ spk.getFormat());

        System.out.println ("spk.getBufferSize= "+ spk.getBufferSize());
        
    }

    public int sendPlayer ( byte[] buf, int off, int len) {
//        spk.drain();

        int l=off;
        while (l<len) {
            int s = spk.write(buf,l,len-l);
            nParts++;
            if (s<0) break;
            l+=s;
        }
        if ( l!=len ) {
            System.out.println("sendPlayer l<len ("+l+"<"+len+")");
        }

        nSends++;
        nBytesSend+=l;
        lLastSend = System.currentTimeMillis();
        
        return l;
    }
    public int sendPlayer ( byte[] buf, int len) {
        return sendPlayer ( buf, 0, len );
    }


    public void showStats() {
        System.out.println("spk.Sends="+nSends);
        System.out.println("spk.Parts="+nParts);
        System.out.println("spk.BytesSend="+nBytesSend);
//        System.out.println("spk.StartTime="+lStartTime);
//        System.out.println("spk.LastSend="+lLastSend);
        System.out.println("spk.Running="+(lLastSend-lStartTime)+"ms");
        System.out.println("spk.Rate="+((float)(1000.0*nBytesSend)/(float)(lLastSend-lStartTime)));
    }
    
    public void stopPlayer() {
        System.out.println("stopPlayer()");
        showStats();

        spk.drain();
        spk.stop();
        spk.close();

        System.out.println("Player ended");
    }

    
    
    
    
/*    
    private volatile boolean FIN=false;
    private void startPlaying() {

        try {

            spk.start(); // start writing to speaker

            int l=0, t=0, n=0, s=AppParams.bufferSize;
            byte[] b = new byte[s];
            System.out.println ("write buffer size= "+ s);

            while ( !FIN ) {
// TCP RECV
                for ( int i=0; i<s ; i++ ) {
                    b[i] = (byte)(i * 200 / s);
                }
// TCP RECV
                l = spk.write(b, 0, s);
                t+=l;
                n++;
            }
            System.out.println("write "+ t + " bytes");
            System.out.println("write "+ n + " opers");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    } 
*/

    
/*    
    @Override
    public void run() {
        initPlayer();
        startPlaying();
    }
*/

}
