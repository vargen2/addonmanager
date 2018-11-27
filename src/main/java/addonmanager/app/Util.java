package addonmanager.app;

public class Util {

    public static final String LINE = System.getProperty("line.separator");

    public static String parse(String input, String start, String end) {
        int startI = input.indexOf(start) + start.length();
        String mid = input.substring(startI);
        return mid.substring(0, mid.indexOf(end)).trim();
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            App.LOG.severe("Util.sleep() " + e.getMessage());
        }
    }
}
