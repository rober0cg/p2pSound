package sr.p2pSoundGUI;

import sr.Sound.*;
import sr.GetOpts.*;
import sr.SimpleLog.SimpleLog;


import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.FontUIResource;

import java.awt.*;
import java.awt.event.*;




public class P2PSoundGUI extends JFrame implements ActionListener, ItemListener, ChangeListener {

    static final long serialVersionUID = 103050709L;

    private static final String TAG = "P2PSoundGUI";

    private static final String appName = "p2pSoundGUI";
    private static String defHost = "rober.ddns.net";
    private static String defPort = "5555";
    private static String defInDevice = null;
    private static String defOutDevice = null;

    private static int conexLlamante = 1;
    private static int conexLlamado  = 2;
    private static int defModoConex  = conexLlamante;

    private static Proceso proceso = new Proceso();

//    private static AppState appState = new AppState();
 
    static final int tHeight = 20;
    private static int fWidth = 800, fHeight = 500, sHeight=360;

// Bloque AUDIO
    private static int xA0=10, yA0=10;

    private static String sAudio = "Audio";
    private static int lxlAudio     = 10+xA0, lylAudio     = 6+yA0, swlAudio      = 80, shlAudio     = tHeight;
    
    private static String sEntrada = "Entrada", sSalida = "Salida";
    private static int lxlEntrada   = 20+xA0, lylEntrada   = 28+yA0, swlEntrada   = 80, shlEntrada   = tHeight;
    private static int lxlSalida    = 20+xA0, lylSalida    = 50+yA0, swlSalida    = 80, shlSalida    = tHeight;
    private static JComboBox<String> cbEntrada, cbSalida;
    private static int lxcbEntrada  =100+xA0, lycbEntrada  = 28+yA0, swcbEntrada  =520, shcbEntrada  = tHeight;
    private static int lxcbSalida   =100+xA0, lycbSalida   = 50+yA0, swcbSalida   =520, shcbSalida   = tHeight;

// Bloque NIVELES
    private static int xV0=680, yV0=10;

    private static String sLevel = "Niveles";
    private static int lxlLevel     = 10+xV0, lylLevel     = 6+yV0, swlLevel      = 80, shlLevel     = tHeight;
    private static JSlider slEntrada, slSalida;
    private static int lxslEntrada  = 10+xV0, lyslEntrada  = 28+yV0, swslEntrada  = 30, shslEntrada  = 150;
    private static int lxslSalida   = 40+xV0, lyslSalida   = 28+yV0, swslSalida   = 30, shslSalida   = 150;

    private static String scEntrada[];
    private static String scSalida[];


// Bloque CALIDAD / ANCHO DE BANDA
    private static int xQ0=80, yQ0=100;

    protected static JLabel lCalidad;
    private static String sCalidad = "Calidad sonido";
    private static int lxlCalidad   = 10+xQ0, lylCalidad   = 6+yQ0, swlCalidad    =180, shlCalidad   = tHeight;
    
    private static String sRate = "Rate", sChannels = "Channels", sBits = "Bits";
    private static int lxlRate      = 20+xQ0, lylRate      = 28+yQ0, swlRate      = 80, shlRate      = tHeight;
    private static int lxlChannels  = 20+xQ0, lylChannels  = 50+yQ0, swlChannels  = 80, shlChannels  = tHeight;
    private static int lxlBits      = 20+xQ0, lylBits      = 72+yQ0, swlBits      = 80, shlBits      = tHeight;

    protected static JComboBox<String> cbRate, cbChannels, cbBits;
    private static int lxcbRate     =100+xQ0, lycbRate     = 28+yQ0, swcbRate     =120, shcbRate     = tHeight;
    private static int lxcbChannels =100+xQ0, lycbChannels = 50+yQ0, swcbChannels =120, shcbChannels = tHeight;
    private static int lxcbBits     =100+xQ0, lycbBits     = 72+yQ0, swcbBits     =120, shcbBits     = tHeight;

    private static String scRate[] = { "8 Khz", "16 Khz", "44.1 Khz", "48 Khz" };
    private static String scRateDefault = scRate[0];
    private static String scChannels[] = { "1 (mono)", "2 (stereo)" };
    private static String scChannelsDefault = scChannels[0];
    private static String scBits[] = { "8 (1 Byte)", "16 (2 Byte)" };
    private static String scBitsDefault = scBits[0];
    
    private static JLabel lAnchoBanda ;
    private static String sAnchoBanda = null;
    private static int lxlAnchoBanda= 40+xQ0, lylAnchoBanda= 96+yQ0, swlAnchoBanda=290, shlAnchoBanda= 2*tHeight;


// Bloque PAQUETES
    private static int xP0=400, yP0=100;

    private static String sPaquete = "Frames por paquete";
    private static int lxlPaquete   = 10+xP0, lylPaquete   = 6+yP0, swlPaquete    =180, shlPaquete   = tHeight;

    private static String sM1Paquete = "Primero", sMxPaquete = "Todos";
    private static int lxlM1Paquete    = 20+xP0, lylM1Paquete    =28+yP0, swlM1Paquete    = 80, shlM1Paquete    = tHeight;
    private static int lxlMxPaquete    = 20+xP0, lylMxPaquete    =50+yP0, swlMxPaquete    = 80, shlMxPaquete    = tHeight;

    protected static JSpinner snMxPaquete, snM1Paquete;
    private static int lxsnM1Paquete   =100+xP0, lysnM1Paquete   =28+yP0, swsnM1Paquete   =120, shsnM1Paquete   = tHeight;
    private static int lxsnMxPaquete   =100+xP0, lysnMxPaquete   =50+yP0, swsnMxPaquete   =120, shsnMxPaquete   = tHeight;

    private static String scM1Paquete[] = { "16", "24", "32", "48", "64", "92", "128", "172", "256", "384", "512", "768", "1024", "1536", "2048" };
    private static String scM1PaqueteDefault = scM1Paquete[2];
    private static String scMxPaquete[] = {  "4",  "8", "16", "24",  "32",  "48",  "64",  "92", "128", "256", "384", "512", "768", "1024" };
    private static String scMxPaqueteDefault = scMxPaquete[2];

    private static JLabel lPacketSizes ;
    private static String sPacketSizes = null;
    private static int lxlPacketSizes  = 40+xP0, lylPacketSizes  = 74+yP0, swlPacketSizes =290, shlPacketSizes  = 2*tHeight;


// Bloque CONEXION
    private static int xC0=360, yC0=240;

    private static String sConex = "Conexión";
    private static int lxlConex     = 10+xC0, lylConex     =  6+yC0, swlConex     = 80, shlConex     = tHeight;

    private static JRadioButton rbLlamante, rbLlamado;
    private static ButtonGroup bgLlamanteLlamado;
    private static String sLlamante = "Llamante", sLlamado = "Llamado";
    private static int lxrbLlamante = 20+xC0, lyrbLlamante = 28+yC0, swrbLlamante =100, shrbLlamante = tHeight; 
    private static int lxrbLlamado  =120+xC0, lyrbLlamado  = 28+yC0, swrbLlamado  =100, shrbLlamado  = tHeight; 

    private static String sRemote = "Host", sPort = "Port";
    private static int lxlRemote    = 20+xC0, lylRemote    = 50+yC0, swlRemote    = 40, shlRemote    = tHeight;
    private static int lxlPort      = 20+xC0, lylPort      = 72+yC0, swlPort      = 40, shlPort      = tHeight;

    private static JTextField tHost, tPort;
    private static int lxtHost      = 60+xC0, lytHost      = 50+yC0, swtHost      =160, shtHost      = tHeight;
    private static int lxtPort      = 60+xC0, lytPort      = 72+yC0, swtPort      =160, shtPort      = tHeight;

    private static JButton bLlamada;
    private static String sLlama = "Iniciar Llamada", sEspera = "Esperar llamada";
    private static int lxbLlamada   =240+xC0, lybLlamada   = 28+yC0, swbLlamada   =160, shbLlamada   = 31;
    private static Thread tLlamada = null;

    private static JButton bFinSalir;
    private static String sFinSalir = "Salir", sFinLlamada = "Fin Llamada", sFinEspera = "Fin Espera";
    private static int lxbFin       =240+xC0, lybFin       = 60+yC0, swbFin       =160, shbFin       = 31;

    private static String acLlamante = "client", acLlamado = "server";
    private static String acSalir = "salir", acFin = "fin", acFinEspera= "finEspera";


// Bloque LOG_LEVEL
    private static int xL0=80, yL0=260;

    private static String sTrazas = "Trazas";
    private static int lxlTrazas    = 10+xL0, lylTrazas    =  6+yL0, swlTrazas    = 80, shlTrazas    = tHeight;

    private static String sLogLevel = "LogLevel";
    private static int lxlLogLevel  = 20+xL0, lylLogLevel  = 28+yL0, swlLogLevel  = 80, shlLogLevel  = tHeight;

    protected static JComboBox<String> cbLogLevel;
    private static int lxcbLogLevel =100+xL0, lycbLogLevel = 28+yL0, swcbLogLevel =120, shcbLogLevel = tHeight;

    private static String scLogLevel[] = SimpleLog.sLevels;
//    private static String scLogLevelDefault = SimpleLog.getLogLevelString();


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
    private JSlider newJSlider ( String[] s, int x, int y, int w, int h ) {
        JSlider jc = new JSlider(JSlider.VERTICAL,0,100,80);
        jc.setLocation(x,y);
        jc.setSize(w,h);
        jc.setMinorTickSpacing(10);
        jc.setMajorTickSpacing(25);
        return jc;
    }

    private String findString ( String f, String[] S ) {
        String x=null;
        if ( f==null || f.equals(""))
            return x;
        for ( String s : S ) {
            if ( s.toLowerCase().contains(f.toLowerCase()) ) {
                x = s;
                break;
            }
        }
        return x;
    }

    public Container createContentPane() {
        JPanel c = new JPanel(new BorderLayout());
        c.setOpaque(true);


    // Bloque AUDIO
        c.add( newJLabel( sAudio, lxlAudio, lylAudio, swlAudio, shlAudio ) );

        c.add( newJLabel( sEntrada, lxlEntrada, lylEntrada, swlEntrada, shlEntrada ) );
        scEntrada = Utils.getInputDevices();
        c.add( cbEntrada = newJComboBoxString(scEntrada,lxcbEntrada, lycbEntrada,swcbEntrada, shcbEntrada));
            cbEntrada.setSelectedItem(findString(defInDevice,scEntrada));

        c.add( newJLabel( sSalida, lxlSalida, lylSalida, swlSalida, shlSalida ) );
        scSalida = Utils.getOutputDevices();
        c.add( cbSalida = newJComboBoxString(scSalida,lxcbSalida, lycbSalida,swcbSalida, shcbSalida));
            cbSalida.setSelectedItem(findString(defOutDevice,scSalida));
    
    // Bloque NIVELES
        c.add( newJLabel( sLevel, lxlLevel, lylLevel, swlLevel, shlLevel ) );

        c.add( slEntrada = newJSlider(scEntrada,lxslEntrada, lyslEntrada,swslEntrada, shslEntrada));
            slEntrada.addChangeListener(this);

        c.add( slSalida = newJSlider(scSalida,lxslSalida, lyslSalida,swslSalida, shslSalida));
            slSalida.addChangeListener(this);

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
            lAnchoBanda.setFont(new Font(null, Font.PLAIN, 12));

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
            lPacketSizes.setFont(new Font(null, Font.PLAIN, 12));

    // Bloque CONEXION
        c.add( newJLabel( sConex, lxlConex, lylConex, swlConex, shlConex ) );

        bgLlamanteLlamado = new ButtonGroup();
        c.add( rbLlamante = newJRadioButton(sLlamante,lxrbLlamante,lyrbLlamante,swrbLlamante,shrbLlamante) );
            rbLlamante.setSelected(defModoConex==conexLlamante);
            rbLlamante.setVisible(true);
            rbLlamante.addActionListener(this);
        bgLlamanteLlamado.add(rbLlamante);
        c.add( rbLlamado = newJRadioButton(sLlamado,lxrbLlamado,lyrbLlamado,swrbLlamado,shrbLlamado) );
            rbLlamado.setSelected(defModoConex==conexLlamado);
            rbLlamado.setVisible(true);
            rbLlamado.addActionListener(this);
        bgLlamanteLlamado.add(rbLlamado);

        c.add( newJLabel( sRemote, lxlRemote, lylRemote, swlRemote, shlRemote ) );
        c.add( tHost = newJTextField(defHost,lxtHost, lytHost,swtHost, shtHost));
            tHost.setEnabled(defModoConex==conexLlamante);

        c.add( newJLabel( sPort, lxlPort, lylPort, swlPort, shlPort ) );
        c.add( tPort = newJTextField(defPort,lxtPort, lytPort,swtPort, shtPort));
    
        c.add( bLlamada = newJButton(defModoConex==conexLlamante ? sLlama : sEspera, lxbLlamada,lybLlamada, swbLlamada,shbLlamada));
            bLlamada.setActionCommand( defModoConex==conexLlamante ? acLlamante : acLlamado );
            bLlamada.setEnabled(true);
            bLlamada.addActionListener(this);

        c.add( bFinSalir = newJButton(sFinSalir, lxbFin,lybFin, swbFin,shbFin));
            bFinSalir.setActionCommand(acSalir);
            bFinSalir.setEnabled(true);
            bFinSalir.addActionListener(this);


    // Bloque LOG_LEVEL
        c.add( newJLabel( sTrazas, lxlTrazas, lylTrazas, swlTrazas, shlTrazas ) );

        c.add( newJLabel( sLogLevel, lxlLogLevel, lylLogLevel, swlLogLevel, shlLogLevel ) );
        c.add( cbLogLevel = newJComboBoxString(scLogLevel,lxcbLogLevel, lycbLogLevel,swcbLogLevel, shcbLogLevel));
            cbLogLevel.setSelectedItem(SimpleLog.getLogLevelString());
            cbLogLevel.addItemListener(this);


    //Bloque LOG
        output = new JTextArea(5,30);
        output.setEditable(false);
        output.setFont(new Font("Courier", Font.PLAIN, 12));
        log = new JScrollPane(output);
        log.setLocation(0,400);
        c.add(log);

    // Dividir pantalla en dos
        sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, c, log);
        sp.setDividerLocation(sHeight);
        sp.setDividerSize(0);
        sp.setEnabled(false);


    // Fijar font para toda la ventana y sus componentes
        changeFont_R(c, new FontUIResource(new Font(null, Font.PLAIN, 14)));


    // Reaccionar a ESC saliendo
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                SimpleLog.LOGD(TAG, "frame.ActionEvent VK_ESCAPE");
                String ac = bFinSalir.getActionCommand();
                finOrdenado ( ac );
            }
        };
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        c.registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);


        return sp;
    }


    private void changeFont_R(Container root, Font font) {
        for (Component c : root.getComponents()) {
            c.setFont(font);
            if (c instanceof Container) {
                changeFont_R((Container) c, font);
            }  
        }
    }

    private void dropContentPane() {
        cbEntrada = null;
        cbSalida = null;
        slEntrada = null;
        slSalida = null;

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

    // Bloque LOG_LEVEL
        cbLogLevel = null;

    // Bloque LOG
        output = null;
        log = null;

    // Dividir pantalla en dos
        sp = null;

        return;
    }


    private void externalFinConex(){
        bLlamada.setEnabled(true);
        bFinSalir.setText(sFinSalir);
        bFinSalir.setActionCommand(acSalir);
        return;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        
        if ( o==rbLlamante ) {
            SimpleLog.LOGD(TAG, "rbLlamante");
            tHost.setEnabled(true);
            bLlamada.setActionCommand(acLlamante);
            bLlamada.setText(sLlama);
        }
        else if ( o==rbLlamado ) {
            SimpleLog.LOGD(TAG, "rbLlamante");
            tHost.setEnabled(false);
            bLlamada.setActionCommand(acLlamado);
            bLlamada.setText(sEspera);
        }

        if ( o==bLlamada ) {
            SimpleLog.LOGD(TAG, "bLlamada");
            String ac = e.getActionCommand();
            String host = null;
            String port = null;
            if ( ac.equals(acLlamante) ) {
                host = tHost.getText();
                port = tPort.getText();
                if ( host== null || host.equals("") || port== null || port.equals("") ) {
                    SimpleLog.LOGW(TAG, "NECESARIO HOST Y PORT" );
                    return;
                }
                bFinSalir.setText(sFinLlamada);
                bFinSalir.setActionCommand(acFin);
            }
            else
            if ( ac.equals(acLlamado) ) {
                port = tPort.getText();
                if ( port== null || port.equals("") ) {
                    SimpleLog.LOGW(TAG, "NECESARIO PORT" );
                    return;
                }
                bFinSalir.setText(sFinEspera);
                bFinSalir.setActionCommand(acFinEspera);
            }
            bLlamada.setEnabled(false);



            tLlamada = new Thread(new Runnable() {
                public void run() {
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

                    int rc=0;
                    if ( ac.equals(acLlamante) ) {// client, llamante
                        rc = proceso.ConectaLlamante (tHost.getText(), tPort.getText()) ;
                    }
                    else { // server, llamado. recupera datos de configuración
                        rc = proceso.ConectaLlamado (tPort.getText()) ;
                    }

                    if ( rc != 0 ) {  // >0 cancel
                        if ( rc<0 ) { // <0 error
                            SimpleLog.LOGE(TAG, "ERROR: ConectaLlamante/Llamado rc="+rc);
                        }
                        bLlamada.setEnabled(true);
                        bFinSalir.setText(sFinSalir);
                        bFinSalir.setActionCommand(acSalir);
                        return;
                    }

                    if ( ac.equals(acLlamado) ) {// llamado, server... actualizar datos de configuración audio
                        r = proceso.getRate();
                        c = proceso.getChannels();
                        b = proceso.getBits();
                        nx = proceso.getPacketXSize();
                        n1 = proceso.getPacket1Size();

                        cbRate.setSelectedIndex( rateToIndex(r) );
                        cbChannels.setSelectedIndex( channelsToIndex(c) );
                        cbBits.setSelectedIndex( bitsToIndex(b) );
                        String Nx = (String) ""+nx;
                        String N1 = (String) ""+n1;
                        snMxPaquete.setValue(Nx);
                        snM1Paquete.setValue(N1);

                        bFinSalir.setText(sFinLlamada);
                        bFinSalir.setActionCommand(acFin);
                    }
                    
                    // Inicio de los hilos de envía captura y reproduce recibido
                    Runnable runFin = () -> externalFinConex();
                    proceso.Ejecuta( runFin );

                }
            });
            tLlamada.start();
            
            return;
        }

        if ( o==bFinSalir ) {
            SimpleLog.LOGD(TAG, "bSalir");
            String ac = e.getActionCommand();
            finOrdenado ( ac );
            return;
        }

        return;
    }
    

    private void finOrdenado ( String ac ) {
        if ( ac.equals(acFinEspera) ) { // Dejar de esperar
            if ( tLlamada!=null ) {
                if ( tLlamada.getState()!=Thread.State.TERMINATED ) {
                    proceso.cancelaLlamado();
                    try {
                        tLlamada.join();
                    } catch (InterruptedException e1) {
                    }
                }
                tLlamada=null;
            }
        }
        else
        if ( ac.equals(acFin) ) { // Cortar Conexión
            proceso.Finaliza();

            bLlamada.setEnabled(true);
            bFinSalir.setText(sFinSalir);
            bFinSalir.setActionCommand(acSalir);

        }
        else
        if ( ac.equals(acSalir) ){ // Salir de la aplicación
            dispose();
            dropContentPane();
            System.exit(0);
        }
        else {
            SimpleLog.LOGW(TAG, "DESCONOCIDO ac: " + ac);
        }
    }


    @Override
    public void itemStateChanged(ItemEvent e) {
        Object o = e.getSource();

        if ( e.getStateChange() != ItemEvent.SELECTED ) {
            return;
        }

        if ( o==cbRate  || o==cbChannels  || o==cbBits ) {
            SimpleLog.LOGD(TAG, "cbRate|cbChannels|cbBits");

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

        if ( o==cbLogLevel ) {
            SimpleLog.LOGD(TAG, "cbLogLevel");
            int logLevel = cbLogLevel.getSelectedIndex();
            SimpleLog.setLogLevel( logLevel );
//            SimpleLog.LOGI(TAG, "LOG_LEVEL = "+logLevel);
        }

        return;
    }
 
    @Override
    public void stateChanged (ChangeEvent e) {
        Object o = e.getSource();
        
        if ( o==snM1Paquete  ||  o==snMxPaquete ) {
            SimpleLog.LOGD(TAG, "snM1Paquete|snMxPaquete");
            String rate = (String)cbRate.getSelectedItem();
            String channels = (String)cbChannels.getSelectedItem();
            String bits = (String)cbBits.getSelectedItem();

            String sM1 = (String) snM1Paquete.getValue();
            String sMx = (String) snMxPaquete.getValue();
            String ps = calcPacketSizes ( rate, channels, bits, sMx, sM1 );
            lPacketSizes.setText(ps);
        }

        if ( o==slEntrada ) {
            SimpleLog.LOGD(TAG, "slEntrada: " + slEntrada.getValue());
            proceso.setVolumeRecorder(slSalida.getValue());
        }
        if ( o==slSalida ) {
            SimpleLog.LOGD(TAG, "slSalida: " + slSalida.getValue());
            proceso.setVolumePlayer(slEntrada.getValue());
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

        String s = "<html><pre>"
                    + kbps + "kbps (" + KBps + "KB/s) <br>"
                    +"1 Frame = " + tFrame + "ms - " + ( c * b / 8 ) + "B"
                 + "</pre></html>";

        return s;
    }

//  private static String scRate[] = { "8 Khz", "16 Khz", "44.1 Khz", "48 Khz" };
    private int rateToInt ( String R ) {
        int r=0;
        if      ( R.equals(scRate[0]) ) r= 8000;
        else if ( R.equals(scRate[1]) ) r=16000;
        else if ( R.equals(scRate[2]) ) r=44100;
        else if ( R.equals(scRate[3]) ) r=48000;
        return r;
    }
    private int rateToIndex ( int r ) {
        int i=0;
        if      ( r== 8000 ) i=0;
        else if ( r==16000 ) i=1;
        else if ( r==44100 ) i=2;
        else if ( r==48000 ) i=3;
        return i;
    }

//  private static String scChannels[] = { "mono (1 ch)", "stereo (2 ch)" };
    private int channelsToInt ( String C ) {
        int c=0;
        if      ( C.equals(scChannels[0]) ) c=1;
        else if ( C.equals(scChannels[1]) ) c=2;
        return c;
    }
    private int channelsToIndex ( int c ) {
        int i=0;
        if      ( c==1 ) i=0;
        else if ( c==2 ) i=1;
        return i;
    }
//  private static String scBits[] = { "8 bits (1 Byte)", "16 bits (2 Byte)" };
    private int bitsToInt ( String B ) {
        int b=0;
        if      ( B.equals(scBits[0]) ) b= 8;
        else if ( B.equals(scBits[1]) ) b=16;
        return b;
    }
    private int bitsToIndex ( int b ) {
        int i=0;
        if      ( b== 8 ) i=0;
        else if ( b==16 ) i=1;
        return i;
    }


    private String calcPacketSizes ( String R, String C, String B, String Mx, String M1) {
        int r = rateToInt ( R );
        int c = channelsToInt ( C );
        int b = bitsToInt ( B );
        if ( r==0 ) {
            String s = "";
            return s;
        }

        int n1 = Integer.valueOf(M1);
        int nx = Integer.valueOf(Mx);
        float tFrame = 1000.0f / r ;

        int b1 = n1 * c * b / 8;
        int bx = nx * c * b / 8;
        
        String s = "<html><pre>"
                    + "T.primer = " + n1 * tFrame + "ms - " + b1 + "B<br>"
                    + "T.todos  = " + nx * tFrame + "ms - " + bx + "B"
                 + "</pre></html>";

        return s;
    }

    

    private static void createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(false);
        JFrame frame = new JFrame(appName);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        P2PSoundGUI app = new P2PSoundGUI();

//        frame.setJMenuBar(app.createMenuBar());
        frame.setContentPane(app.createContentPane());

        frame.getRootPane().setDefaultButton(bLlamada);

//        frame.pack();
        frame.setSize(fWidth, fHeight);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);        
        frame.setVisible(true);


        return;
    }



    private static final String options = "r:p:i:o:d:h";
    private static final String help = 
        "opciones válidas:"+
        "\n\t -r remote -p port, para actuar como llamante (ejempo: -r rober.ddns.net -p 5555, -r 8.8.8.8 -p 8888)"+
        "\n\t -p port, para actuar como llamado (ejmeplo -p 5555, -p 8888)"+
        "\n\t -i 'patrón', para especificar dispositivo de captura de sonido distinto de "+AppParams.inDevice+
        "\n\t -o 'patrón', para especificar dispositivo de salida de sonido distinto de "+AppParams.outDevice+
        "\n\t -d {TRACE|DEBUG|INFO|WARN|ERROR|NONE}, para establecer log level inicial"+
        "\n\t -h, esta ayuda"+
        ""
    ;
    private static String remote=null, port=null, inDevice=null, outDevice=null, loglevel=null;

    private static int Argumentos(String[] args) {
        GetOpt g = new GetOpt( appName, args, options );
        int c;
        while ( (c=g.getopt()) != -1) {
            switch (c) {
            case 'r':
                remote = g.getOptarg();
                defHost = remote;
                break;
            case 'p':
                port = g.getOptarg();
                defPort = port;
                break;
            case 'i':
                inDevice = g.getOptarg();
                defInDevice  = inDevice;
                break;
            case 'o':
                outDevice = g.getOptarg();
                defOutDevice = outDevice;
                break;
            case 'd':
                loglevel = g.getOptarg();
                SimpleLog.setLogLevel(loglevel);
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

        if ( remote!=null && port!=null ) { // entendemos modo llamante
            defModoConex  = conexLlamante;
        }
        else
        if ( remote==null && port!=null ) { // entendemos modo llamado
            defModoConex  = conexLlamado;
        }


        return 0;
    }

    public static void main(String[] args) {

        int rc = Argumentos(args);
        if ( rc!=0 ) {
            if ( rc<0 ) {
                System.out.println(appName +": error argumentos");
            }
            System.out.println(help);
            return;
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });

        return;
    }
    
}



/*
 * 
 * 
//
// Redirigir System.out -> a JTextArea output
//
//import java.io.PrintStream;
//import java.io.IOException;
//import java.io.OutputStream;
//    private static int ccLog; // Contador caracteres
//        ccLog=0;
//        PrintStream out = new PrintStream(new OutputStream() {
//            @Override
//            public void write(int b) throws IOException {
//                char c = (char)(b & 0xFF);
//                output.append(""+c);
//                if ( ccLog>80 || c=='\r' || c=='\n') {
//                    output.setCaretPosition(output.getDocument().getLength());
//                    ccLog=0;
//                }
//                ccLog++;
//            }
//        });
//        System.setOut(out);
//
 * 
 * 
 */

