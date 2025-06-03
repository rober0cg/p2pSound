package sr.Sound;

import javax.sound.sampled.*;

import java.util.ArrayList;
import java.util.List;

import sr.SimpleLog.SimpleLog;

public class Utils {
    private static final String TAG = "Utils" ;

    private static int DUMP_LINE_SIZE=32;

    public Utils() {
        // nada que hacer
    }

    public static Mixer getSelectedMixer(String device) {
        for(Mixer.Info info : AudioSystem.getMixerInfo()) {
            String mixer = info.getName();
            if ( device.toLowerCase().contains(mixer.toLowerCase()) ) {
                return AudioSystem.getMixer(info);
            }
        }
        SimpleLog.LOGE(TAG, "getSelectedMixer("+device+") NOT FOUND");
        return null;
    }

    public static String[] getInputDevices() {
        SimpleLog.LOGD(TAG, "getInputDevices" );
        List<String> ls = new ArrayList<String>() ;
        Mixer.Info[] mis = AudioSystem.getMixerInfo();
        for (Mixer.Info mi: mis){
            Mixer m = AudioSystem.getMixer(mi);
            Line.Info[] lineInfos = m.getTargetLineInfo();
            if( lineInfos.length>=1 && 
                    lineInfos[0].getLineClass().equals(TargetDataLine.class) ) {
                String dev = mi.getName() + " - " + mi.getDescription().replace("Direct Audio Device: ","");
                ls.add( dev );
                SimpleLog.LOGD(TAG, "TargetDataLine = " + dev );
            }
        }
        return ls.toArray( new String[ls.size()]);
    }

    public static String[] getOutputDevices() {
        SimpleLog.LOGD(TAG, "getOutputDevices" );
        List<String> ls = new ArrayList<String>() ;
        Mixer.Info[] mis = AudioSystem.getMixerInfo();
        for (Mixer.Info mi: mis){
            Mixer m = AudioSystem.getMixer(mi);
            Line.Info[] lineInfos = m.getSourceLineInfo();
            if( lineInfos.length>=1 && 
                    lineInfos[0].getLineClass().equals(SourceDataLine.class) ) {
                String dev = mi.getName() + " - " + mi.getDescription().replace("Direct Audio Device: ","");
                ls.add( dev );
                SimpleLog.LOGD(TAG, "SourceDataLine = " + dev );
            }
        }
        return ls.toArray( new String[ls.size()]);
    }



    public static FloatControl getMasterOutputVolumeControl ( ) {

        Line line = null ;

        Mixer.Info[] mis = AudioSystem.getMixerInfo();
        for (Mixer.Info mi: mis){
            Mixer m = AudioSystem.getMixer(mi);
            SimpleLog.LOGD(TAG, "getMixer:" + mi.getName());

            for ( Line.Info li : m.getSourceLineInfo() ) {
                try {
                    Line aux = m.getLine(li);
                    SimpleLog.LOGD(TAG, "mixer.getLine:" + aux.getLineInfo().toString());

                    if ( aux.getLineInfo().toString().contains("Master") ) {
                        line = aux;
                        break;
                    }
                }
                catch ( LineUnavailableException e ) {
                    line = null;
                }
            }
        }

        if ( line==null ) {
            SimpleLog.LOGW(TAG, "getMasterOutputVolumeControl 'line' no encontrado" );
            return null;
        }

        Control[] Ctls = line.getControls();
        if ( Ctls==null || Ctls.length==0 )
            return null;
        for ( Control ctl : Ctls ) {
            if ( ctl.getType().equals(FloatControl.Type.VOLUME) )
                return (FloatControl)ctl;
            if ( ctl instanceof CompoundControl ) {
                CompoundControl cctl = (CompoundControl)ctl;
                Control[] subCtls = cctl.getMemberControls();
                if ( subCtls==null || subCtls.length==0 )
                    return null;
                for ( Control sctl : subCtls ) {
                    if ( sctl.getType().equals(FloatControl.Type.VOLUME) ) {
                        SimpleLog.LOGI(TAG, "getMasterOutputVolumeControl encontrado!!" );
                        return (FloatControl)ctl;
                    }
                }
            }
        }
        SimpleLog.LOGW(TAG, "getMasterOutputVolumeControl 'ctl' no encontrado" );
        return null;
    }


    public static FloatControl getMasterInputVolumeControl ( ) {

        Line line = null ;

        Mixer.Info[] mis = AudioSystem.getMixerInfo();
        for (Mixer.Info mi: mis){
            Mixer m = AudioSystem.getMixer(mi);
            SimpleLog.LOGD(TAG, "getMixer:" + mi.getName());

            for ( Line.Info li : m.getTargetLineInfo() ) {
                try {
                    Line aux = m.getLine(li);
                    SimpleLog.LOGD(TAG, "mixer.getLine:" + aux.getLineInfo().toString() );
                    if ( aux.getLineInfo().toString().contains("Master") ) {

                        line = aux;
                        break;
                    }
                }
                catch ( LineUnavailableException e ) {
                    line = null;
                }
            }
        }

        if ( line==null ) {
            SimpleLog.LOGW(TAG, "getMasterInputVolumeControl 'line' no encontrado" );
            return null;
        }

        Control[] Ctls = line.getControls();
        if ( Ctls==null || Ctls.length==0 )
            return null;
        for ( Control ctl : Ctls ) {
            if ( ctl.getType().equals(FloatControl.Type.VOLUME) )
                return (FloatControl)ctl;
            if ( ctl instanceof CompoundControl ) {
                CompoundControl cctl = (CompoundControl)ctl;
                Control[] subCtls = cctl.getMemberControls();
                if ( subCtls==null || subCtls.length==0 )
                    return null;
                for ( Control sctl : subCtls ) {
                    if ( sctl.getType().equals(FloatControl.Type.VOLUME) ) {
                        SimpleLog.LOGI(TAG, "getMasterInputVolumeControl encontrado!!" );
                        return (FloatControl)ctl;
                    }
                }
            }
        }
        SimpleLog.LOGW(TAG, "getMasterInputVolumeControl 'ctl' no encontrado" );
        return null;
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
        if ( SimpleLog.getLogLevel() <= SimpleLog.ll_DEBUG ) {
            dump( b, b.length );
        }
    }
    public static void dump( byte[] b, int l ) {
        if ( SimpleLog.getLogLevel() <= SimpleLog.ll_DEBUG ) {
            int i, r=0;
            for ( i=0; i<l; i++ ) {
                r= i%DUMP_LINE_SIZE ;
                if ( r == 0 ) System.out.print(String.format("%04d(%04x): ", i, i));
                System.out.print(String.format("%02x ", b[i]));
                if ( r == (DUMP_LINE_SIZE-1) ) System.out.println();
            }
            if ( r != (DUMP_LINE_SIZE-1) ) System.out.println();

        }
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


    public static void msSleep ( int ms ) {
        try {
            Thread.sleep(ms, 0);
        } catch (InterruptedException e) {
        }

    }

}
