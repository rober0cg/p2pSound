package sr.Sound;

public class AppState {

    public enum AppMode { LLAMANTE, LLAMADO };
    public static AppMode appMode = AppMode.LLAMANTE;


    public enum AppStat { INICIO, CONNECTING, CONNECTED, ENDING };
    public static AppStat appStat = AppStat.INICIO;

}
