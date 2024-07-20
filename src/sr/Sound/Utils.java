package sr.Sound;

import javax.sound.sampled.*;

import java.util.ArrayList;
import java.util.List;


public class Utils {

    private static int DUMP_LINE_SIZE=32;

    public Utils() {
        // TODO Auto-generated constructor stub
    }

    public static Mixer getSelectedMixer(String toFind) {
        for(Mixer.Info info : AudioSystem.getMixerInfo()) {
            String mixer = info.getName();
            System.out.println("getSelectedMixer.Info: "+ info.getName());
            if ( mixer.toLowerCase().contains(toFind.toLowerCase()) ) {
                System.out.println("getSelectedMixer("+toFind+") FOUND!");
                return AudioSystem.getMixer(info);
            }
        }
        System.out.println("getSelectedMixer("+toFind+") NOT FOUND");
        return null;
    }

    public static String[] getInputDevices() {
        List<String> ls = new ArrayList<String>() ;
        Mixer.Info[] mis = AudioSystem.getMixerInfo();
        for (Mixer.Info mi: mis){
            Mixer m = AudioSystem.getMixer(mi);
            Line.Info[] lineInfos = m.getTargetLineInfo();
            if( lineInfos.length>=1 && 
                    lineInfos[0].getLineClass().equals(TargetDataLine.class) ) {
                ls.add(mi.getName());
            }
        }
        return ls.toArray( new String[ls.size()]);
    }

    public static String[] getOutputDevices() {
        List<String> ls = new ArrayList<String>() ;
        Mixer.Info[] mis = AudioSystem.getMixerInfo();
        for (Mixer.Info mi: mis){
            Mixer m = AudioSystem.getMixer(mi);
            Line.Info[] lineInfos = m.getSourceLineInfo();
            if( lineInfos.length>=1 && 
                    lineInfos[0].getLineClass().equals(SourceDataLine.class) ) {
                ls.add(mi.getName());
            }
        }
        return ls.toArray( new String[ls.size()]);
    }


    public static int FadeInOut_8BitsMono( byte[] buf, int len, int pctAmpl, int pctTime ) {

        // pctAmpl(%) entre 0 y 100
        // FadeIn tiene que pasar de pctAmpl a 100, el FadeOut de 100 a pctAmpl 

        // pctTime(%) entre 0 y 100, siendo 100 la mitad del buffer 

        int fadeAmpl = 100 - pctAmpl; 

        // FadeIn - FadeOut índices
        int fadeInMinInd = 0;
        int fadeInMaxInd = (int) (( (float)(pctTime * len) / 2.0f) / 100.0f );
        int fadeOutMinInd = len - fadeInMaxInd ;
        int fadeOutMaxInd = len;

        for ( int i=fadeInMinInd; i<fadeInMaxInd; i++ ) {
            float factor = ( ((float)(i - fadeInMinInd) / (float)fadeInMaxInd ) * (float)(fadeAmpl) ) + (float)pctAmpl ;
            float v = (float)buf[i];
            v *= (factor/100.0f);
            buf[i] = (byte)v;
        }

        for ( int i=fadeOutMinInd; i<fadeOutMaxInd; i++ ) {
            float factor = ( ((float)(fadeOutMaxInd - i) / (float)fadeOutMaxInd ) * (float)(fadeAmpl) ) + (float)pctAmpl ;
            float v = (float)buf[i];
            v *= (factor/100.0f);
            buf[i] = (byte)v;
        }
        
        return 0;
    }

    
    public static void dump ( byte[] b ) {
        dump( b, b.length );
    }
    public static void dump( byte[] b, int l ) {
        int i, r=0;
        for ( i=0; i<l; i++ ) {
            r= i%DUMP_LINE_SIZE ;
            if ( r == 0 ) System.out.print(String.format("%04d(%04x): ", i, i));
            System.out.print(String.format("%02x ", b[i]));
            if ( r == (DUMP_LINE_SIZE-1) ) System.out.println();
        }
        if ( r != (DUMP_LINE_SIZE-1) ) System.out.println();
    }

    private static long first=0L;
    private static long last=0L;
    public static String timeStamp () {
        long now = System.currentTimeMillis();
        long prev = last;
        if (first==0L) {
            first=now;
            last=now;
            return String.format("%16d ms", now );
        }
        else {
            last = now;
            return String.format("%4d ms %8d ms", now-prev, now-first );
        }
    }
}
