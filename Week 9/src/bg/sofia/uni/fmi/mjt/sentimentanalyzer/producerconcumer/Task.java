package bg.sofia.uni.fmi.mjt.sentimentanalyzer.producerconcumer;

public record Task (String inputID, String text){
    public static final Task KILL_YOURSELF = new Task("KILL_YOURSELF", "");
}
