package sr.SimpleLog;

public class SimpleLog {

    public static final int ll_VERBOSE = 0 ;
    public static final int ll_DEBUG   = 1 ;
    public static final int ll_INFO    = 2 ;
    public static final int ll_WARN    = 3 ;
    public static final int ll_ERROR   = 4 ;
    public static final int ll_NONE    = 5 ;
    public static final int NUM_LOG_LEVELS = 6 ;

    private static int ll = ll_VERBOSE; // defecto

    public static String[] sLevels = { "VERBOSE", "DEBUG", "INFO", "WARN", "ERROR", "NONE" };

    public static void setLogLevel ( int l ){
        if      ( l < ll_VERBOSE) ll = ll_VERBOSE;
        else if ( l > ll_NONE   ) ll = ll_NONE;
        else                      ll = l;
    }
    public static int getLogLevel() {
        return ll;
    }
    public static void setLogLevel ( String s ){
        for ( int i=0 ; i<NUM_LOG_LEVELS ; i++ ) {
            if ( sLevels[i].equals(s.toUpperCase())) {
                ll = i;
            }
        }
    }
    public static String getLogLevelString() {
        return sLevels[ll];
    }

    public static void LOGV ( String TAG, String s ) {
        if ( ll <= ll_VERBOSE ) {
            sl_print ( "VERBOSE", TAG, s );
        }
    }
    public static void LOGD ( String TAG, String s ) {
        if ( ll <= ll_DEBUG ) {
            sl_print ( "DEBUG", TAG, s );
        }
    }
    public static void LOGI ( String TAG, String s ) {
        if ( ll <= ll_INFO ) {
            sl_print ( "INFO", TAG, s );
        }
    }
    public static void LOGW ( String TAG, String s ) {
        if ( ll <= ll_WARN ) {
            sl_print ( "WARN", TAG, s );
        }
    }
    public static void LOGE ( String TAG, String s ) {
        if ( ll <= ll_ERROR ) {
            sl_print ( "ERROR", TAG, s );
        }
    }
    private static void sl_print ( String lvl, String TAG, String s){
        System.out.println("[" + lvl + "] " + TAG + ": " + s );
    }
    
}
