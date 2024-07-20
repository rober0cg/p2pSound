package sr.Player;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import sr.GetOpts.GetOpt;
import sr.Sound.AppParams;
import sr.Sound.Player;
import sr.Sound.Utils;

public class FileToSpk {

    public FileToSpk() {
    }

    private static final String appName = "FileToSpk";

    private static String inFile="", outDevice="";
    private static int frameRate=100;

    private static Player pPLY = null;
    private static FileInputStream fis = null;
    
    
    private static volatile boolean FIN=false;

    private static final String options = "i:o:R:h";
    private static final String help = 
        "opciones válidas:"+
        "\n\t -i 'fichero entrada'"+
        "\n\t -o 'patrón', para especificar dispositivo de salida de sonido distinto de " +AppParams.outDevice+
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
                inFile = g.getOptarg();
                break;
            case 'o':
                outDevice = g.getOptarg();
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
        if ( inFile=="" )  inFile=AppParams.inputFile;
        if ( outDevice=="" ) outDevice=AppParams.outDevice;
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

        pPLY = new Player(outDevice);

        try {
            fis = new FileInputStream(inFile);
        }
        catch ( FileNotFoundException e) {
            System.out.println("ERROR creando fichero: "+ e.toString() );
            return;
        }


        Long lTime = 1000L / AppParams.frameRate; // Periodo
        System.out.println( "frameRate = "+ AppParams.frameRate+" Hz. - "+lTime+" ms." );
        

        Thread tPlayRCV = new Thread(new Runnable() {
            public void run() {
                System.out.println("thread PlayRCV start");
                pPLY.startPlayer();

                int l = AppParams.bufferSize ;
                byte[] b = new byte[ l ];

                while (!FIN) {
                    int r=0 ;
                    try {
                        r=fis.read(b);
                    } catch (IOException e) {
                        System.out.println("ERROR leyendo fichero: "+ e.toString() );
                    }

                    if ( r<=0 ) {
                        System.out.println("thread PlayRCV: fin fichero");
                        FIN=true;
                        break ;
                    }

                    l = r;
                    pPLY.sendPlayer(b, 0, l);
                    System.out.println( Utils.timeStamp() + " thread PlayRCV pPLY.sendPlayer(len) = "+ l );
                    Utils.dump(b,r);

                }
                System.out.println("thread PlayRCV end");
            }
        });

        tPlayRCV.start();
        
        
        
        Thread.sleep(100, 0); //espera 0,1seg
        while (!FIN ) {
            try {
                System.out.print("cli> ");
                int k = System.in.read();
                switch ( k ) {
                case 'S':
                case 's':
                    System.out.println("Playerer stats:");
                    pPLY.showStats();
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

        pPLY.stopPlayer();
        
        fis.close();

        Thread.sleep(100, 0); //espera 0,1seg

        
        return;
    }

}
