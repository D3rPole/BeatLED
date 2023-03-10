package networking;

import java.io.IOException;
import java.net.*;
import java.security.InvalidParameterException;

public class Device {
    String ip;
    int port;
    InetAddress address;

    private final DatagramSocket dsocket = new DatagramSocket();

    Device() throws SocketException {}
    public Device(String ip,int port) throws SocketException, UnknownHostException {
        this.ip = ip;
        this.port = port;
        address = InetAddress.getByName(this.ip);
    }

    public void initDevice(){
        try {
            address = InetAddress.getByName(this.ip);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(byte[] buffer) throws IOException, InvalidParameterException {
        if(buffer.length%3 != 0) throw new InvalidParameterException("buffer length should be divisible by 3, cause R,G,B,R,G,B....");
        byte[] data = new byte[buffer.length + 2];
        data[0] = 2;
        data[1] = 1;
        System.arraycopy(buffer, 0, data, 2, buffer.length);
        DatagramPacket packet = new DatagramPacket(data,data.length,address,port);
        dsocket.send(packet);
    }

    public void stop(){
        dsocket.close();
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }
}
