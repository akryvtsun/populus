package com.akryvtsun.populus;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class App {

    public static void main(String[] args) throws URISyntaxException, IOException {
        System.out.println("Hello World!");
        Desktop.getDesktop().browse(new URI("https://echo.msk.ru/"));
    }
}
