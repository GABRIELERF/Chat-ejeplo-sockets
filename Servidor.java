/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Sockets;

import javax.swing.*;

import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLServerSocket;

public class Servidor {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        MarcoServidor mimarco = new MarcoServidor();

        mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}

class MarcoServidor extends JFrame implements Runnable {

    private JTextArea areatexto;

    public MarcoServidor() {

        setBounds(300, 300, 380, 350);

        JPanel milamina = new JPanel();

        milamina.setLayout(new BorderLayout());

        areatexto = new JTextArea();

        milamina.add(areatexto, BorderLayout.CENTER);

        add(milamina);

        setVisible(true);

        Thread h = new Thread(this);
        h.start();

    }

    @Override
    public void run() {

        try {

            ServerSocket servidor = new ServerSocket(9999);
            String nick, ip, mensaje;
            PaqueteEnvio paquete_recibido;
            ArrayList<String> lista_ip = new ArrayList<>();

            while (true) {

                Socket misocket = servidor.accept();

                /*
                Ejemplo para solo recibir datos
                DataInputStream flujo_entrada = new DataInputStream(misocket.getInputStream());
                String msj = flujo_entrada.readUTF();
                areatexto.append(msj + "\n");*/
                ObjectInputStream paquete_datos = new ObjectInputStream(misocket.getInputStream());
                paquete_recibido = (PaqueteEnvio) paquete_datos.readObject();
                nick = paquete_recibido.getNick();
                ip = paquete_recibido.getIp();
                mensaje = paquete_recibido.getMensaje();

                if (!mensaje.equals("Online")) {

                    areatexto.append(nick + ": " + mensaje + "\t Para: " + ip);
                    Socket enviar_destinatario = new Socket(ip, 9090);
                    ObjectOutputStream paquete_reenvio = new ObjectOutputStream(enviar_destinatario.getOutputStream());
                    paquete_reenvio.writeObject(paquete_recibido);

                    paquete_reenvio.close();
                    enviar_destinatario.close();
                    misocket.close();
                } else {
                    //Detecta la ip que esta se a conectado al servidor
                    InetAddress localizacion = misocket.getInetAddress();
                    String ip_remota = localizacion.getHostAddress();
                    System.out.println("Online " + ip_remota);
                    lista_ip.add(ip_remota);
                    paquete_recibido.setIps(lista_ip);
                    for (String s : lista_ip) {
                        
                        Socket enviar_destinatario = new Socket(s, 9090);
                        ObjectOutputStream paquete_reenvio = new ObjectOutputStream(enviar_destinatario.getOutputStream());
                        paquete_reenvio.writeObject(paquete_recibido);

                        paquete_reenvio.close();
                        enviar_destinatario.close();
                        misocket.close();

                    }

                }

            }
        } catch (IOException ex) {

            Logger.getLogger(MarcoServidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MarcoServidor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
