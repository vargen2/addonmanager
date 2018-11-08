package addonmanager.app.net;

public class Util {

    public static String parse(String input, String start, String end) {
        int startI = input.indexOf(start) + start.length();
        String mid = input.substring(startI);
        return mid.substring(0, mid.indexOf(end)).trim();
    }
}
