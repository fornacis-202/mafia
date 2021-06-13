package com.company;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * The type Streams , keeps the player's socket , inputStream and outputStream.
 */
public class Streams {
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket socket;

    /**
     * Instantiates a new Streams.
     *
     * @param socket the socket
     */
    public Streams(Socket socket){
        try {
            this.socket=socket;
             in = new ObjectInputStream(socket.getInputStream());
             out = new ObjectOutputStream(socket.getOutputStream());
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Gets out.
     *
     * @return the out
     */
    public ObjectOutputStream getOut() {
        return out;
    }

    /**
     * Gets in.
     *
     * @return the in
     */
    public ObjectInputStream getIn() {
        return in;
    }

    /**
     * Close boolean.
     *
     * @return true if successful
     */
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
