package ru.onliver.translation_streamer.service;

import org.springframework.stereotype.Service;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

@Service
public class HardwareService {

    public String getContainerIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (!iface.isUp() || iface.isLoopback()) {
                    continue;
                }
                String name = iface.getName();
                if (!name.startsWith("eth")) {
                    continue;
                }
                for (InterfaceAddress addr : iface.getInterfaceAddresses()) {
                    InetAddress inetAddr = addr.getAddress();
                    if (inetAddr instanceof Inet4Address) {
                        return inetAddr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            throw new IllegalStateException("Failed to enumerate network interfaces", e);
        }
        throw new IllegalStateException("No active Docker network interface found");
    }

}
