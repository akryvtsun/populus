package com.akryvtsun.populus;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class LocalWebServer implements Runnable {

    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(7777);
            Socket s = ss.accept();
            System.out.println("Server socket has received smth on " + s);
            PrintStream ps = new PrintStream(s.getOutputStream());

            File file = new File(
                App.class.getClassLoader().getResource("index.html").getFile()
            );
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                ps.println(line);
            }
            fr.close();
            ps.close();
            //                 s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
