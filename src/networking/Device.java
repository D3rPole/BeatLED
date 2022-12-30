package networking;

import java.io.IOException;
import java.net.*;
import java.security.InvalidParameterException;
import java.util.Random;

public class Device {
    String ip;
    int port;
    InetAddress address;

    private DatagramSocket dsocket = new DatagramSocket();

    public Device(String ip,int port) throws SocketException, UnknownHostException {
        this.ip = ip;
        this.port = port;
        address = InetAddress.getByName(this.ip);
    }

    public void send(byte[] buffer) throws IOException, InvalidParameterException {
        if(buffer.length%3 != 0) throw new InvalidParameterException("buffer length should be divisible by 3, since R,G,B,R,G,B....");
        byte[] data = new byte[buffer.length + 2];
        data[0] = 2;
        data[1] = 5;
        for (int i = 0; i < buffer.length; i++) {
            data[i + 2] = buffer[i];
        }
        DatagramPacket packet = new DatagramPacket(data,data.length,address,port);
        dsocket.send(packet);
    }

    public void stop(){
        dsocket.close();
    }
}
