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
//              System.out.println("spk (AudioSystem): "+spk.toString());
            }
            else {
              spk = (SourceDataLine) selectedMixer.getLine(info);
//              System.out.println("spk (selectedMixer): "+spk.toString());
            }
/*
            System.out.println ("spk.isActive=  "+ spk.isActive());
            System.out.println ("spk.isRunning= "+ spk.isRunning());
            System.out.println ("spk.isOpen=    "+ spk.isOpen());

            System.out.println ("spk.getLevel=    "+ spk.getLevel());
            System.out.println ("spk.getFormat=    "+ spk.getFormat());

            System.out.println ("spk.getBufferSize= "+ spk.getBufferSize());
*/
            spk.flush();
            spk.open(audioFormat); //open the speakr for writing
            System.out.println ("spk open!!");
            System.out.println("player initialized...");
          
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
/*
        System.out.println ("spk.isActive=  "+ spk.isActive());
        System.out.println ("spk.isRunning= "+ spk.isRunning());
        System.out.println ("spk.isOpen=    "+ spk.isOpen());

        System.out.println ("spk.getLevel=    "+ spk.getLevel());
        System.out.println ("spk.getFormat=    "+ spk.getFormat());

        System.out.println ("spk.getBufferSize= "+ spk.getBufferSize());
*/
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
        System.out.println("spk.showStats");
        System.out.println("\t.Sends="+nSends);
        System.out.println("\t.Parts="+nParts);
        System.out.println("\t.BytesSend="+nBytesSend);
//        System.out.println("\t.StartTime="+lStartTime);
//        System.out.println("\t.LastSend="+lLastSend);
        System.out.println("\t.Running="+(lLastSend-lStartTime)+"ms");
        System.out.println("\t.Rate="+((float)(1000.0*nBytesSend)/(float)(lLastSend-lStartTime)));
    }
    
    public void stopPlayer() {
        System.out.println("stopPlayer()");
        showStats();

        spk.drain();
        spk.stop();
        spk.close();

        System.out.println("Player ended");
    }

}
