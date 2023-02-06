package util;

public class Time {
    public static float timeStarted = System.nanoTime();// init when app starts
    public static float getTime(){return (float)((System.nanoTime() - timeStarted)* 1E-9); }

}
