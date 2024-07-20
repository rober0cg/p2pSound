package sr.Recorder;

import sr.Sound.Recorder;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import sr.GetOpts.GetOpt;
import sr.Sound.AppParams;
import sr.Sound.Utils;

public class MicToFile {

    public MicToFile() {
    }

    private static final String appName = "MicToFile";

    private static String inDevice="", outFile="";
    private static int frameRate=100;

    private static Recorder rREC = null;
    private static FileOutputStream fos = null;
    
    
    private static volatile boolean FIN=false;

    
    private static final String options = "i:o:R:h";
    private static final String help = 
        "opciones válidas:"+
        "\n\t -i 'patrón', para especificar dispositivo de captura de sonido distinto de " +AppParams.inDevice+
        "\n\t -o 'fichero salida'"+
        "\n\t -R frameRate, para especificar frecuencia envío distinta de " +AppParams.frameRate+
        "\n\t -h, esta ayuda"+
        ""
    ;
    private static int Argumentos(String[] args) {
        GetOpt g = new GetOpt( appName, args, options );
        int c;
        while ( (c=g.getopt()) != -1) {
            switch (c) {
            case 'i':
                inDevice = g.getOptarg();
                break;
            case 'o':
                outFile = g.getOptarg();
                break;
            case 'R':
                frameRate = Integer.valueOf(g.getOptarg());
                break;
            case 'h':
            case '?':
                System.out.println(help);
                return 0;
            default:
                System.out.println(appName +": opción desconocida '"+(char)c+"'");
                return -1;
            }
        }

// si no se especifican, los "por defecto"
        if ( inDevice=="" )  inDevice=AppParams.inDevice;
        if ( outFile=="" )   outFile=AppParams.outputFile;
        if ( frameRate==0 )  frameRate=AppParams.frameRate;

        return 0;
    }

    
    public static void main(String[] args) throws Exception {

        if (Argumentos(args)<0) {
            System.out.println(appName +": error argumentos");
            System.out.println(help);
            return;
        }

        if ( frameRate != AppParams.frameRate )
            AppParams.setFrameRate( frameRate );

        AppParams.show();

        rREC = new Recorder(inDevice);

        try {
            fos = new FileOutputStream(outFile);
        }
        catch ( FileNotFoundException e) {
            System.out.println("ERROR creando fichero: "+ e.toString() );
            return;
        }


        Long lTime = 1000L / AppParams.frameRate; // Periodo
        System.out.println( "frameRate = "+ AppParams.frameRate+" Hz. - "+lTime+" ms." );
        
        Thread tSendREC = new Thread(new Runnable() {
            public void run() {
                System.out.println("thread SendREC start");
                rREC.startRecorder();
                byte[] b = new byte[AppParams.bufferSize];
                int l = 0;

                while (!FIN) {
                    l = rREC.readRecorder(b,AppParams.bufferSize);
                    System.out.println( Utils.timeStamp() + " thread SendREC rREC.readRecorder ("+ AppParams.bufferSize +") = "+ l +" bytes." );
                    Utils.dump(b,l);

                    try {
                        fos.write(b);
                    } catch (IOException e) {
                        System.out.println("ERROR excribiendo fichero: "+ e.toString() );
                    }
                }
                System.out.println("thread SendREC end");
            }
        });

        
        
        
        tSendREC.start();

//        Thread.sleep(100, 0); //espera 0,1seg
        while (!FIN ) {
            try {
                System.out.print("cli> ");
                int k = System.in.read();
                switch ( k ) {
                case 'S':
                case 's':
                    System.out.println("Recorder stats:");
                    rREC.showStats();
                    break;
                case 'Q':
                case 'q':
                    FIN=true;
                    break;
                default:
                    continue;
                }
            }
            catch (IOException e){
                System.out.println("Error reading from user");
            }
        }

        Thread.sleep(100, 0); //espera 0,1seg

        rREC.stopRecorder();
        
        fos.close();

        Thread.sleep(100, 0); //espera 0,1seg

        
        return;
    }
    
}
