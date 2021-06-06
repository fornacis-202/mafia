package com.company;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Streams {
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket socket;
    public Streams(Socket socket){
        try {
            this.socket=socket;
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        }catch (IOException e){
            System.out.println("streams did not created");
        }
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public boolean close(){
        try {
            out.close();
            in.close();
            socket.close();
            return true;
        } catch (IOException e) {
            System.out.println("can not close Streams");
            return false;
        }
    }
}
