package sr.Sound;

import javax.sound.sampled.*;

import sr.SimpleLog.SimpleLog;

public class Recorder { // extends Thread
    private static final String TAG="Recorder";

    private TargetDataLine mic;
    private FloatControl micVolume;
    private String inputDevice="";
    
    private int nReads = 0, nParts = 0;
    private int nBytesRead = 0;
    private long lStartTime = 0L, lLastRead = 0L;


    
    public Recorder() {
        inputDevice="";
        initRecorder();
    }
    public Recorder( String device ) {
        inputDevice=device;
        initRecorder();
    }
    
    private void initRecorder() {
        SimpleLog.LOGD(TAG, "initRecorder( "+inputDevice+" )");

        try {

            //define audio format
            AudioFormat audioFormat = new AudioFormat(
                AppParams.sampleRate, AppParams.sampleSizeInBits, AppParams.channels, AppParams.signed, AppParams.bigEndian
            );

            
            //build line info with audio format
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
            SimpleLog.LOGD(TAG, "info= " + info.toString());
            

            Mixer selectedMixer = Utils.getSelectedMixer(AppParams.inDevice);
            if (selectedMixer == null) {
              mic = (TargetDataLine) AudioSystem.getLine(info);
//              SimpleLog.LOGD(TAG, "mic (AudioSystem): "+mic.toString());
            }
            else {
              mic = (TargetDataLine) selectedMixer.getLine(info);
//              SimpleLog.LOGD(TAG, "mic (selectedMixer): "+mic.toString());
            }
            print("initRecorder");

            mic.flush();
            mic.open(audioFormat ); //open the line for recording
            SimpleLog.LOGD(TAG, "mic open!!");

            micVolume = Utils.getMasterInputVolumeControl();

            SimpleLog.LOGI(TAG, "initialized");
          
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }

    public void startRecorder() {
        SimpleLog.LOGD(TAG, "startRecorder()");
        try {

            mic.start(); // start reading from line

            nReads = 0;
            nParts = 0;
            nBytesRead = 0;
            lStartTime = System.currentTimeMillis();
            lLastRead = lStartTime+1;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        print("startRecorder");
    } 


    public int readRecorder ( byte[] buf, int len) {
        int l=0;
        while (l<len) {
            int r = mic.read(buf,l,len-l);
            nParts++;
            if (r<0) break;
            l+=r;
        }

        if ( l!=len ) {
            SimpleLog.LOGW(TAG, "readRecorder l<len ("+l+"<"+len+")");
        }

        nReads++;
        nBytesRead+=l;
        lLastRead = System.currentTimeMillis();
        
        return l;
    }

    public void stopRecorder() {
        SimpleLog.LOGD(TAG, "stopRecorder()");
        showStats();

        mic.stop();
        mic.close();

        SimpleLog.LOGI(TAG, "Recorder ended");
    }


    public void setVolume ( int v ){
        if ( micVolume!=null )
            micVolume.setValue( (float)(v/100.0f) );
    }




    public void showStats() {
        SimpleLog.LOGI(TAG, "mic.showStats");
        SimpleLog.LOGI(TAG, "\tReads="+nReads+", Parts="+nParts+", BytesRead="+nBytesRead);
        SimpleLog.LOGI(TAG, "\tRunning="+(lLastRead-lStartTime)+"ms"+", Rate="+((float)(1000.0f*nBytesRead)/(float)(lLastRead-lStartTime)));
    }

    private void print( String s ) {
        SimpleLog.LOGD(TAG, s + " = {");
        SimpleLog.LOGD(TAG, "\tmic.isActive=  "+ mic.isActive());
        SimpleLog.LOGD(TAG, "\tmic.isRunning= "+ mic.isRunning());
        SimpleLog.LOGD(TAG, "\tmic.isOpen=    "+ mic.isOpen());

        SimpleLog.LOGD(TAG, "\tmic.getLevel=    "+ mic.getLevel());
        SimpleLog.LOGD(TAG, "\tmic.getFormat=    "+ mic.getFormat());

        SimpleLog.LOGD(TAG, "\tmic.getBufferSize= "+ mic.getBufferSize());
        SimpleLog.LOGD(TAG, "}");
    }




}
