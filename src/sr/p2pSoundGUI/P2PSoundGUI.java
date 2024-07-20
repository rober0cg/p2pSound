package sr.p2pSoundGUI;

import sr.Sound.*;


import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;
import java.io.IOException;
import java.io.OutputStream;



public class P2PSoundGUI extends JFrame implements ActionListener, ItemListener, ChangeListener {

    static final long serialVersionUID = 103050709L;

    private static final String appName = "p2pSoundGUI";
    private static final String defPort = "5555";

    private static Proceso proceso = new Proceso ();

 
    static final int tWidth = 18;
    private static int fWidth = 600, fHeight = 600, sHeight=280;

// Bloque AUDIO
    private static int xA0=10, yA0=5;

//    protected static JLabel lAudio;
    private static String sAudio = "Audio";
    private static int lxlAudio     = 10+xA0, lylAudio     = 4+yA0, swlAudio      = 80, shlAudio     = tWidth;
    
//    private static JLabel lEntrada, lSalida;
    private static String sEntrada = "Entrada", sSalida = "Salida";
    private static int lxlEntrada   = 20+xA0, lylEntrada   = 24+yA0, swlEntrada   = 60, shlEntrada   = tWidth;
    private static int lxlSalida    = 20+xA0, lylSalida    = 44+yA0, swlSalida    = 60, shlSalida    = tWidth;
    private static JComboBox<String> cbEntrada, cbSalida;
    private static int lxcbEntrada  = 80+xA0, lycbEntrada  = 24+yA0, swcbEntrada  =480, shcbEntrada  = tWidth;
    private static int lxcbSalida   = 80+xA0, lycbSalida   = 44+yA0, swcbSalida   =480, shcbSalida   = tWidth;

    private static String scEntrada[];
    private static String scSalida[];


// Bloque CALIDAD / ANCHO DE BANDA
    private static int xQ0=10, yQ0=80;

    protected static JLabel lCalidad;
    private static String sCalidad = "Calidad sonido";
    private static int lxlCalidad   = 10+xQ0, lylCalidad   = 4+yQ0, swlCalidad    =180, shlCalidad   = tWidth;
    
//    private static JLabel lRate, lChannels, lBits;
    private static String sRate = "Rate", sChannels = "Channels", sBits = "Bits";
    private static int lxlRate      = 20+xQ0, lylRate      = 24+yQ0, swlRate      = 60, shlRate      = tWidth;
    private static int lxlChannels  = 20+xQ0, lylChannels  = 44+yQ0, swlChannels  = 60, shlChannels  = tWidth;
    private static int lxlBits      = 20+xQ0, lylBits      = 64+yQ0, swlBits      = 60, shlBits      = tWidth;

    protected static JComboBox<String> cbRate, cbChannels, cbBits;
    private static int lxcbRate     = 80+xQ0, lycbRate     = 24+yQ0, swcbRate     =120, shcbRate     = tWidth;
    private static int lxcbChannels = 80+xQ0, lycbChannels = 44+yQ0, swcbChannels =120, shcbChannels = tWidth;
    private static int lxcbBits     = 80+xQ0, lycbBits     = 64+yQ0, swcbBits     =120, shcbBits     = tWidth;

    private static String scRate[] = { "8 Khz", "16 Khz", "44.1 Khz", "48 Khz" };
    private static String scRateDefault = scRate[0];
    private static String scChannels[] = { "1 (mono)", "2 (stereo)" };
    private static String scChannelsDefault = scChannels[0];
    private static String scBits[] = { "8 (1 Byte)", "16 (2 Byte)" };
    private static String scBitsDefault = scBits[0];
    
    private static JLabel lAnchoBanda ;
    private static String sAnchoBanda = null;
    private static int lxlAnchoBanda= 80+xQ0, lylAnchoBanda= 84+yQ0, swlAnchoBanda=290, shlAnchoBanda= 2*tWidth;


// Bloque PAQUETES
    private static int xP0=300, yP0=80;

//    protected static JLabel lPaquete;
    private static String sPaquete = "Tamaño paquetes";
    private static int lxlPaquete   = 10+xP0, lylPaquete   = 4+yP0, swlPaquete    =180, shlPaquete   = tWidth;

//    private static JLabel lMxPaquete, lM1Paquete;
    private static String sMxPaquete = "Todos", sM1Paquete = "Primero";
    private static int lxlMxPaquete    = 20+xP0, lylMxPaquete    =24+yP0, swlMxPaquete    = 60, shlMxPaquete    = tWidth;
    private static int lxlM1Paquete    = 20+xP0, lylM1Paquete    =44+yP0, swlM1Paquete    = 60, shlM1Paquete    = tWidth;

    protected static JSpinner snMxPaquete, snM1Paquete;
    private static int lxsnMxPaquete   = 80+xP0, lysnMxPaquete   =24+yP0, swsnMxPaquete   =120, shsnMxPaquete   = tWidth;
    private static int lxsnM1Paquete   = 80+xP0, lysnM1Paquete   =44+yP0, swsnM1Paquete   =120, shsnM1Paquete   = tWidth;

    private static String scMxPaquete[] = {  "4",  "8", "16", "24",  "32",  "48",  "64",  "92", "128", "256", "384", "512", "768", "1024" };
    private static String scMxPaqueteDefault = scMxPaquete[4];
    private static String scM1Paquete[] = { "32", "48", "64", "92", "128", "172", "256", "384", "512", "768", "1024", "1536", "2048", "3072", "4096" };
    private static String scM1PaqueteDefault = scM1Paquete[6];

    private static JLabel lPacketSizes ;
    private static String sPacketSizes = null;
    private static int lxlPacketSizes  = 80+xP0, lylPacketSizes  = 64+yP0, swlPacketSizes =290, shlPacketSizes  = 2*tWidth;


//    protected static JLabel l3;
//    protected static JTextField t3;
//    protected static JButton b1, b2, b3;


    // Bloque CONEXION
    private static int xC0=220, yC0=180;

//    private static JLabel lConex;
    private static String sConex = "Conexión";
    private static int lxlConex     = 10+xC0, lylConex     =  4+yC0, swlConex     = 80, shlConex     = tWidth;

    private static JRadioButton rbLlamante, rbLlamado;
    private static ButtonGroup bgLlamanteLlamado;
    private static String sLlamante = "Llamante", sLlamado = "Llamado";
    private static int lxrbLlamante = 20+xC0, lyrbLlamante = 24+yC0, swrbLlamante =100, shrbLlamante = tWidth; 
    private static int lxrbLlamado  =120+xC0, lyrbLlamado  = 24+yC0, swrbLlamado  =100, shrbLlamado  = tWidth; 

//    private static JLabel lRemote, lPort;
    private static String sRemote = "Host", sPort = "Port";
    private static int lxlRemote    = 20+xC0, lylRemote    = 44+yC0, swlRemote    = 40, shlRemote    = tWidth;
    private static int lxlPort      = 20+xC0, lylPort      = 64+yC0, swlPort      = 40, shlPort      = tWidth;

    private static JTextField tHost, tPort;
    private static int lxtHost      = 60+xC0, lytHost      = 44+yC0, swtHost      =140, shtHost      = tWidth;
    private static int lxtPort      = 60+xC0, lytPort      = 64+yC0, swtPort      =140, shtPort      = tWidth;

    private static JButton bLlamada;
    private static String sLlama = "Iniciar Llamada", sEspera = "Esperar llamada";
    private static int lxbLlamada   =220+xC0, lybLlamada   = 24+yC0, swbLlamada   =130, shbLlamada   = 28;

    private static JButton bFinSalir;
    private static String sFinSalir = "Salir", sFinLlamada = "Fin Llamada";
    private static int lxbFin       =220+xC0, lybFin       = 54+yC0, swbFin       =130, shbFin       = 28;

    private static String acLlamante = "client", acLlamado = "server";
    private static String acSalir = "salir", acFin = "fin";


    // Bloque LOG
    protected static JTextArea output;
    protected static JScrollPane log;

    // ContentPane principal: JSplitPan
    protected static JSplitPane sp;



    public P2PSoundGUI() {

    }


    private JLabel newJLabel ( String s, int x, int y, int w, int h ) {
        JLabel jc = new JLabel(s);
        jc.setLocation(x,y);
        jc.setSize(w,h);
        return jc;
    }
    private JRadioButton newJRadioButton ( String s, int x, int y, int w, int h ) {
        JRadioButton jc = new JRadioButton(s);
        jc.setLocation(x,y);
        jc.setSize(w,h);
        return jc;
    }
    private JTextField newJTextField ( String s, int x, int y, int w, int h ) {
        JTextField jc = new JTextField(s);
        jc.setLocation(x,y);
        jc.setSize(w,h);
        return jc;
    }
    private JButton newJButton ( String s, int x, int y, int w, int h ) {
        JButton jc = new JButton(s);
        jc.setLocation(x,y);
        jc.setSize(w,h);
        return jc;
    }
    private JComboBox<String> newJComboBoxString ( String[] s, int x, int y, int w, int h ) {
        JComboBox<String> jc = new JComboBox<String>(s);
        jc.setLocation(x,y);
        jc.setSize(w,h);
        return jc;
    }

    private JSpinner newJSpinnerListModel ( String[] s, int x, int y, int w, int h ) {
        JSpinner jc = new JSpinner(new SpinnerListModel(s));
        jc.setLocation(x,y);
        jc.setSize(w,h);
        return jc;
    }

    public Container createContentPane() {
        JPanel c = new JPanel(new BorderLayout());
        c.setOpaque(true);

    // Bloque AUDIO
        c.add( newJLabel( sAudio, lxlAudio, lylAudio, swlAudio, shlAudio ) );

        c.add( newJLabel( sEntrada, lxlEntrada, lylEntrada, swlEntrada, shlEntrada ) );
        scEntrada = Utils.getInputDevices();
        c.add( cbEntrada = newJComboBoxString(scEntrada,lxcbEntrada, lycbEntrada,swcbEntrada, shcbEntrada));

        c.add( newJLabel( sSalida, lxlSalida, lylSalida, swlSalida, shlSalida ) );
        scSalida = Utils.getOutputDevices();
        c.add( cbSalida = newJComboBoxString(scSalida,lxcbSalida, lycbSalida,swcbSalida, shcbSalida));

    // Bloque CALIDAD
        c.add( newJLabel( sCalidad, lxlCalidad, lylCalidad, swlCalidad, shlCalidad ) );

        c.add( newJLabel( sRate, lxlRate, lylRate, swlRate, shlRate ) );
        c.add( cbRate = newJComboBoxString(scRate,lxcbRate, lycbRate,swcbRate, shcbRate));
            cbRate.addItemListener(this);

        c.add( newJLabel( sChannels, lxlChannels, lylChannels, swlChannels, shlChannels ) );
        c.add( cbChannels = newJComboBoxString(scChannels,lxcbChannels, lycbChannels,swcbChannels, shcbChannels));
            cbChannels.addItemListener(this);

        c.add( newJLabel( sBits, lxlBits, lylBits, swlBits, shlBits ) );
        c.add( cbBits = newJComboBoxString(scBits,lxcbBits, lycbBits,swcbBits, shcbBits));
            cbBits.addItemListener(this);

        sAnchoBanda = calcAnchoBanda ( scRate[0], scChannels[0], scBits[0] );
        c.add( lAnchoBanda = newJLabel( sAnchoBanda, lxlAnchoBanda, lylAnchoBanda, swlAnchoBanda, shlAnchoBanda ) );
            lAnchoBanda.setFont(new Font(null, Font.PLAIN, 11));

    // Bloque PAQUETE
        c.add( newJLabel( sPaquete, lxlPaquete, lylPaquete, swlPaquete, shlPaquete ) );

        c.add( newJLabel( sMxPaquete, lxlMxPaquete, lylMxPaquete, swlMxPaquete, shlMxPaquete ) );
        c.add( snMxPaquete = newJSpinnerListModel(scMxPaquete, lxsnMxPaquete, lysnMxPaquete, swsnMxPaquete, shsnMxPaquete));
            snMxPaquete.setValue(scMxPaqueteDefault);
            snMxPaquete.addChangeListener(this);

        c.add( newJLabel( sM1Paquete, lxlM1Paquete, lylM1Paquete, swlM1Paquete, shlM1Paquete ) );
        c.add( snM1Paquete = newJSpinnerListModel(scM1Paquete, lxsnM1Paquete, lysnM1Paquete, swsnM1Paquete, shsnM1Paquete));
            snM1Paquete.setValue(scM1PaqueteDefault);
            snM1Paquete.addChangeListener(this);
            
        sPacketSizes = calcPacketSizes ( scRateDefault, scChannelsDefault, scBitsDefault, scMxPaqueteDefault, scM1PaqueteDefault );
        c.add( lPacketSizes = newJLabel( sPacketSizes, lxlPacketSizes, lylPacketSizes, swlPacketSizes, shlPacketSizes ) );
            lPacketSizes.setFont(new Font(null, Font.PLAIN, 11));

    // Bloque CONEXION
        c.add( newJLabel( sConex, lxlConex, lylConex, swlConex, shlConex ) );

        bgLlamanteLlamado = new ButtonGroup();
        c.add( rbLlamante = newJRadioButton(sLlamante,lxrbLlamante,lyrbLlamante,swrbLlamante,shrbLlamante) );
            rbLlamante.setSelected(true);
            rbLlamante.setVisible(true);
            rbLlamante.addActionListener(this);
        bgLlamanteLlamado.add(rbLlamante);
        c.add( rbLlamado = newJRadioButton(sLlamado,lxrbLlamado,lyrbLlamado,swrbLlamado,shrbLlamado) );
            rbLlamado.setSelected(false);
            rbLlamado.setVisible(true);
            rbLlamado.addActionListener(this);
        bgLlamanteLlamado.add(rbLlamado);

        c.add( newJLabel( sRemote, lxlRemote, lylRemote, swlRemote, shlRemote ) );
        c.add( tHost = newJTextField(null,lxtHost, lytHost,swtHost, shtHost));

        c.add( newJLabel( sPort, lxlPort, lylPort, swlPort, shlPort ) );
        c.add( tPort = newJTextField(defPort,lxtPort, lytPort,swtPort, shtPort));
    
        c.add( bLlamada = newJButton(sLlama, lxbLlamada,lybLlamada, swbLlamada,shbLlamada));
            bLlamada.setActionCommand(acLlamante);
            bLlamada.setEnabled(true);
            bLlamada.addActionListener(this);

        c.add( bFinSalir = newJButton(sFinSalir, lxbFin,lybFin, swbFin,shbFin));
            bFinSalir.setActionCommand(acSalir);
            bFinSalir.setEnabled(true);
            bFinSalir.addActionListener(this);

    //Bloque LOG
        output = new JTextArea(5,30);
        output.setEditable(false);
        log = new JScrollPane(output);
        log.setLocation(0,400);
        c.add(log);

    // Redirigir System.out -> a JTextArea output
        PrintStream out = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                output.append(""+(char)(b & 0xFF));
            }
        });
        System.setOut(out);

    // Dividir pantalla en dos
        sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, c, log);
        sp.setDividerLocation(sHeight);
        sp.setDividerSize(0);
        sp.setEnabled(false);

        return sp;
    }

    private void dropContentPane() {
        cbEntrada = null;
        cbSalida = null;

    // Bloque CALIDAD
        cbRate = null;
        cbChannels = null;
        cbBits = null;
        lAnchoBanda = null;

    // Bloque PAQUETE
        snMxPaquete = null;
        snM1Paquete = null;
        lPacketSizes = null;

    // Bloque CONEXION
        rbLlamante = null;
        rbLlamado = null;
        tHost = null;
        tPort = null;
        bLlamada = null;
        bFinSalir = null;

    //Bloque LOG
        output = null;
        log = null;

    // Dividir pantalla en dos
        sp = null;

        return;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        
        if ( o==rbLlamante ) {
//            lRemote.setEnabled(true);
            tHost.setEnabled(true);
            bLlamada.setActionCommand(acLlamante);
            bLlamada.setText(sLlama);
        }
        else if ( o==rbLlamado ) {
//            lRemote.setEnabled(false);
            tHost.setEnabled(false);
            bLlamada.setActionCommand(acLlamado);
            bLlamada.setText(sEspera);
        }

        if ( o==bLlamada ) {
            String ac = e.getActionCommand();
            String host = null;
            String port = null;
            if ( ac.equals(acLlamante) ) {
                host = tHost.getText();
                port = tPort.getText();
                if ( host== null || host.equals("") || port== null || port.equals("") ) {
                    System.out.println ( "NECESARIO HOST Y PORT" );
                    return;
                }
            }
            else
            if ( ac.equals(acLlamado) ) {
                port = tPort.getText();
                if ( port== null || port.equals("") ) {
                    System.out.println ( "NECESARIO PORT" );
                    return;
                }
            }

            bLlamada.setEnabled(false);
            bFinSalir.setText(sFinLlamada);
            bFinSalir.setActionCommand(acFin);

            String I = (String)cbEntrada.getSelectedItem();
            String O = (String)cbSalida.getSelectedItem();
            String rate = (String)cbRate.getSelectedItem();
            String channels = (String)cbChannels.getSelectedItem();
            String bits = (String)cbBits.getSelectedItem();
            String Mx = (String) snMxPaquete.getValue();
            String M1 = (String) snM1Paquete.getValue();

            int r = rateToInt ( rate );
            int c = channelsToInt ( channels );
            int b = bitsToInt ( bits );
            int nx = Integer.valueOf(Mx);
            int n1 = Integer.valueOf(M1);

            proceso.Parametros(I,O,r,c,b,nx,n1);

            if ( host==null ) { // server, llamado
                proceso.EjecutaLlamado (port) ;
            }
            else { // client, llamante
                proceso.EjecutaLlamante (host, port) ;
            }

        }

        if ( o==bFinSalir ) {
            String ac = e.getActionCommand();
            System.out.println ( "bFin:"+ac);

            if ( ac.equals(acFin) ) { // Cortar Conexión
                proceso.Finaliza();

                bLlamada.setEnabled(true);
                bFinSalir.setText(sFinSalir);
                bFinSalir.setActionCommand(acSalir);

            }
            else if ( ac.equals(acSalir) ){ // Salir de la aplicación
                dispose();
                dropContentPane();
                System.exit(0);
            }
            else {
                System.out.println("DESCONOCIDO");
            }
        }

        return;
    }
    
    @Override
    public void itemStateChanged(ItemEvent e) {
        Object o = e.getSource();

        if ( o==cbRate  || o==cbChannels  || o==cbBits ) {
            String rate = (String)cbRate.getSelectedItem();
            String channels = (String)cbChannels.getSelectedItem();
            String bits = (String)cbBits.getSelectedItem();
            String bps = calcAnchoBanda ( rate, channels, bits);
            lAnchoBanda.setText(bps);

            String sMx = (String) snMxPaquete.getValue();
            String sM1 = (String) snM1Paquete.getValue();
            String ps = calcPacketSizes ( rate, channels, bits, sMx, sM1 );
            lPacketSizes.setText(ps);
        }

        return;
    }
 
    @Override
    public void stateChanged (ChangeEvent e) {
        Object o = e.getSource();
        
        if ( o==snMxPaquete  ||  o==snM1Paquete ) {
            String rate = (String)cbRate.getSelectedItem();
            String channels = (String)cbChannels.getSelectedItem();
            String bits = (String)cbBits.getSelectedItem();

            String sMx = (String) snMxPaquete.getValue();
            String sM1 = (String) snM1Paquete.getValue();
            String ps = calcPacketSizes ( rate, channels, bits, sMx, sM1 );
            lPacketSizes.setText(ps);
        }

        return;
    }
    
    
    
    private String calcAnchoBanda ( String R, String C, String B) {
        int r = rateToInt ( R );
        int c = channelsToInt ( C );
        int b = bitsToInt ( B );

        int kbps = r*c*b / 1000 ;
        int KBps = kbps / 8;
        float tFrame = 1000.0f / r ;

        String s = "<html>"
                    + kbps + "kbps (" + KBps + "KB/s) <br>"
                    +" 1 Frame = " + tFrame + "ms - " + ( c * b / 8 ) + "B"
                 +"</html>";

        return s;
    }
    
    private int rateToInt ( String R ) {
//  private static String scRate[] = { "8 Khz", "16 Khz", "44.1 Khz", "48 Khz" };
        int r=0;
        if      ( R.equals(scRate[0]) ) r= 8000;
        else if ( R.equals(scRate[1]) ) r=16000;
        else if ( R.equals(scRate[2]) ) r=44100;
        else if ( R.equals(scRate[3]) ) r=48000;
        else r=0;
        return r;
    }
    private int channelsToInt ( String C ) {
//  private static String scChannels[] = { "mono (1 ch)", "stereo (2 ch)" };
        int c=0;
        if      ( C.equals(scChannels[0]) ) c=1;
        else if ( C.equals(scChannels[1]) ) c=2;
        else c=0;
        return c;
    }
    private int bitsToInt ( String B ) {
//  private static String scBits[] = { "8 bits (1 Byte)", "16 bits (2 Byte)" };
        int b=0;
        if      ( B.equals(scBits[0]) ) b= 8;
        else if ( B.equals(scBits[1]) ) b=16;
        else b=0;
        return b;
    }


    private String calcPacketSizes ( String R, String C, String B, String Mx, String M1) {
        int r = rateToInt ( R );
        int c = channelsToInt ( C );
        int b = bitsToInt ( B );
        if ( r==0 ) {
            String s = "";
            return s;
        }

        int nx = Integer.valueOf(Mx);
        int n1 = Integer.valueOf(M1);
        float tFrame = 1000.0f / r ;

        int bx = nx * c * b / 8;
        int b1 = n1 * c * b / 8;
        
        String s = "<html>"
                    + "Tiempo todos  = " + nx * tFrame + "ms - " + bx + "B<br>"
                    + "Tiempo primer = " + n1 * tFrame + "ms - " + b1 + "B"
                 + "</html>";

        return s;
    }

    

    private static void createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame(appName);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        P2PSoundGUI app = new P2PSoundGUI();

//        frame.setJMenuBar(app.createMenuBar());
        frame.setContentPane(app.createContentPane());

//        frame.pack();
        frame.setSize(fWidth, fHeight);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);        
        frame.setVisible(true);

        return;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
        return;
    }
    
}
