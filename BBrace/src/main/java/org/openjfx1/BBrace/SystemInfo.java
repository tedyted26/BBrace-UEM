package org.openjfx1.BBrace;

//Esta clase nos permite recoger la version de java y javafx que estamos usando

public class SystemInfo {

    public static String javaVersion() {
        return System.getProperty("java.version");
    }

    public static String javafxVersion() {
        return System.getProperty("javafx.version");
    }

}