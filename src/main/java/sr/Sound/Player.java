package sr.Sound;

import javax.sound.sampled.*;

import sr.SimpleLog.SimpleLog;

public class Player { // extends Thread
    private static final String TAG = "Player";
 
    private SourceDataLine spk;
    private FloatControl spkVolume;
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
        
        SimpleLog.LOGD(TAG, "initPlayer( "+outputDevice+" )");
        try {
            //define audio format
            AudioFormat audioFormat = new AudioFormat(
                AppParams.sampleRate, AppParams.sampleSizeInBits, AppParams.channels, AppParams.signed, AppParams.bigEndian
            );

            //build line info with audio format
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            SimpleLog.LOGD(TAG,"info= " + info.toString());

            Mixer selectedMixer = Utils.getSelectedMixer(AppParams.outDevice);
            if (selectedMixer == null) {
              spk = (SourceDataLine) AudioSystem.getLine(info);
//              SimpleLog.LOGD(TAG, "spk (AudioSystem): "+spk.toString());
            }
            else {
              spk = (SourceDataLine) selectedMixer.getLine(info);
//              SimpleLog.LOGD(TAG, "spk (selectedMixer): "+spk.toString());
            }
            print("initPlayer");
            spk.flush();
            spk.open(audioFormat); //open the speakr for writing
            SimpleLog.LOGD(TAG, "spk open!!");

            spkVolume = Utils.getMasterOutputVolumeControl() ;

            SimpleLog.LOGI(TAG,"player initialized...");
          
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        }

    }

    
    public void startPlayer() {
        SimpleLog.LOGD(TAG, "startPlayer()");
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
        print("startPlayer");
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
            SimpleLog.LOGW(TAG, "sendPlayer l<len ("+l+"<"+len+")");
        }

        nSends++;
        nBytesSend+=l;
        lLastSend = System.currentTimeMillis();
        
        return l;
    }
    public int sendPlayer ( byte[] buf, int len) {
        return sendPlayer ( buf, 0, len );
    }

    public void stopPlayer() {
        SimpleLog.LOGD(TAG,"stopPlayer()");
        showStats();

        spk.drain();
        spk.stop();
        spk.close();

        SimpleLog.LOGI(TAG,"Player ended");
    }


    public void setVolume ( int v ){
        if ( spkVolume!=null )
            spkVolume.setValue( (float)(v/100.0f) );
    }



    public void showStats() {
        SimpleLog.LOGI(TAG, "spk.showStats");
        SimpleLog.LOGI(TAG, "\tSends="+nSends+", Parts="+nParts+", BytesSend="+nBytesSend);
        SimpleLog.LOGI(TAG, "\tRunning="+(lLastSend-lStartTime)+"ms"+", Rate="+((float)(1000.0*nBytesSend)/(float)(lLastSend-lStartTime)));
    }

    private void print( String s ) {
        SimpleLog.LOGD(TAG, s + " = {");
        SimpleLog.LOGD(TAG, "\tspk.isActive=  "+ spk.isActive());
        SimpleLog.LOGD(TAG, "\tspk.isRunning= "+ spk.isRunning());
        SimpleLog.LOGD(TAG, "\tspk.isOpen=    "+ spk.isOpen());

        SimpleLog.LOGD(TAG, "\tspk.getLevel=    "+ spk.getLevel());
        SimpleLog.LOGD(TAG, "\tspk.getFormat=    "+ spk.getFormat());

        SimpleLog.LOGD(TAG, "\tspk.getBufferSize= "+ spk.getBufferSize());
        SimpleLog.LOGD(TAG, "}");

    }



}

