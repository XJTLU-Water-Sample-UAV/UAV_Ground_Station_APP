package com.uav_app.message_manager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MavsdkUdpClient {
    // 服务器IP地址
    private InetAddress mServerAddress;
    // 服务器端口号
    private final int mServerPort;
    // 对端套接字
    private DatagramSocket mSocket;
    // 是否正在接收
    private boolean isReceiving;

    public MavsdkUdpClient(String targetIp, int targetPort, int nativePort) {
        this.isReceiving = false;
        this.mServerPort = targetPort;
        try {
            mServerAddress = InetAddress.getByName(targetIp);
            mSocket = new DatagramSocket(nativePort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendUdpMessage(byte[] sendBytes) {
        // 开启发送消息子线程
        new Thread(() -> {
            // 发送UDP消息
            DatagramPacket clientPacket = new DatagramPacket(sendBytes, sendBytes.length, mServerAddress, mServerPort);
            try {
                mSocket.send(clientPacket);
            } catch (IOException e) {
            }
        }).start();
    }

    public void startRecvUdpMessage(OnMsgReturnedListener listener) {
        if (isReceiving) {
            return;
        }
        isReceiving = true;
        // 开启监听消息子线程
        new Thread(() -> {
            while (isReceiving) {
                try {
                    byte[] buf = new byte[1024];
                    DatagramPacket serverMsgPacket = new DatagramPacket(buf, buf.length);
                    mSocket.receive(serverMsgPacket);
                    // 生成结果字符串
                    byte[] result = new byte[serverMsgPacket.getLength()];
                    System.arraycopy(serverMsgPacket.getData(), 0, result, 0, serverMsgPacket.getLength());
                    // 收到的服务端消息回调
                    listener.onRecvMessage(result);
                } catch (Exception e) {
                    listener.onRecvError(e);
                }
            }
        }).start();
    }

    public void stopRecvUdpMessage() {
        if (!isReceiving) {
            return;
        }
        isReceiving = false;
        mSocket.close();
    }

    public int getNativePort() {
        return mSocket.getPort();
    }

    public boolean isReceiving() {
        return isReceiving;
    }

    public interface OnMsgReturnedListener {
        void onRecvMessage(byte[] msg);

        void onRecvError(Exception e);
    }
}
