package sr.Sound;

import javax.sound.sampled.*;

public class Recorder { // extends Thread

    private TargetDataLine mic;
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
        System.out.println("initRecorder( "+inputDevice+" )");

        try {

            //define audio format
            AudioFormat audioFormat = new AudioFormat(
                AppParams.sampleRate, AppParams.sampleSizeInBits, AppParams.channels, AppParams.signed, AppParams.bigEndian
            );

            
            //build line info with audio format
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
            System.out.println ("info= " + info.toString());
            

            Mixer selectedMixer = Utils.getSelectedMixer(AppParams.inDevice);
            if (selectedMixer == null) {
              mic = (TargetDataLine) AudioSystem.getLine(info);
              System.out.println("mic (AudioSystem): "+mic.toString());
            }
            else {
              mic = (TargetDataLine) selectedMixer.getLine(info);
              System.out.println("mic (selectedMixer): "+mic.toString());
            }
            
            System.out.println ("mic.isActive=  "+ mic.isActive());
            System.out.println ("mic.isRunning= "+ mic.isRunning());
            System.out.println ("mic.isOpen=    "+ mic.isOpen());

            System.out.println ("mic.getLevel=    "+ mic.getLevel());
            System.out.println ("mic.getFormat=    "+ mic.getFormat());

            System.out.println ("mic.getBufferSize= "+ mic.getBufferSize());

            mic.flush();
            System.out.println ("mic flush OK");
            
//            mic.open(audioFormat, AppParams.bufferSize); //open the line for recording
            mic.open(audioFormat ); //open the line for recording
            System.out.println ("mic open!!");
            System.out.println ("mic.getBufferSize= "+ mic.getBufferSize());

            System.out.println("recording initialized...");
          
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }

    public void startRecorder() {
        System.out.println("startRecorder()");
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
            System.out.println("readRecorder l<len ("+l+"<"+len+")");
        }

        nReads++;
        nBytesRead+=l;
        lLastRead = System.currentTimeMillis();
        
        return l;
    }


    public void showStats() {
        System.out.println("mic.Reads="+nReads);
        System.out.println("mic.Parts="+nParts);
        System.out.println("mic.BytesRead="+nBytesRead);
        System.out.println("mic.Running="+(lLastRead-lStartTime)+"ms");
        System.out.println("mic.Rate="+((float)(1000.0f*nBytesRead)/(float)(lLastRead-lStartTime)));
    }
    
    
    public void stopRecorder() {
        System.out.println("stopRecorder()");
        showStats();

        mic.stop();
        mic.close();

        System.out.println("Recorder ended");
    }
    
}
