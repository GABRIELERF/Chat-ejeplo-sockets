/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Sockets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class Cliente {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        MarcoCliente mimarco = new MarcoCliente();

        mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

}
//Contruccion de la ventana

class MarcoCliente extends JFrame {

    public MarcoCliente() {

        setBounds(300, 300, 280, 350);

        LaminaMarcoCliente milamina = new LaminaMarcoCliente();

        add(milamina);

        setVisible(true);
        
        addWindowListener(new EnvioOnline());
    }

}

class EnvioOnline extends WindowAdapter {

    @Override
    public void windowOpened(WindowEvent e) {

        try {
            //Socket para enviar un estado de online al servidor
            Socket online = new Socket("192.168.1.67",9999);
            PaqueteEnvio datos= new PaqueteEnvio();
            datos.setMensaje("Online");
            ObjectOutputStream paquete_datos= new ObjectOutputStream(online.getOutputStream() );
            paquete_datos.writeObject(datos);
            online.close();
                   
            
        } catch (IOException ex) {
            Logger.getLogger(EnvioOnline.class.getName()).log(Level.SEVERE, null, ex);
        }

        super.windowOpened(e); //To change body of generated methods, choose Tools | Templates.
    }

}
//Panel donde estan los componentes Swing

class LaminaMarcoCliente extends JPanel implements Runnable {

    private JTextField campo1;
    private JComboBox ip;
    private JButton miboton;
    private JTextArea campo_chat;
    private JLabel nick;

// Contructor del panel
    public LaminaMarcoCliente() {

        JLabel n_nick = new JLabel("Nick: ");

        String nick_usuario = JOptionPane.showInputDialog("Nick:");

        nick = new JLabel();

        add(n_nick);

        nick.setText(nick_usuario);

        add(nick);

        JLabel texto = new JLabel("  Online:");

        add(texto);

        ip = new JComboBox();
/*
        ip.addItem("Usuario 1");
        ip.addItem("Usuario 1");
        ip.addItem("Usuario 1");
*/
        add(ip);

        campo_chat = new JTextArea(12, 20);
        add(campo_chat);

        campo1 = new JTextField(20);

        add(campo1);

        miboton = new JButton("Enviar");

        miboton.addActionListener(new EnviaTexto());

        add(miboton);

        Thread mi_hilo = new Thread(this);

        mi_hilo.start();
    }

    @Override
    //Codigo del hilo
    public void run() {

        try {

            ServerSocket servidor_cliente = new ServerSocket(9090);
            Socket cliente;
            PaqueteEnvio paquete_recibido;

            while (true) {

                cliente = servidor_cliente.accept();
                ObjectInputStream flujo_entrada = new ObjectInputStream(cliente.getInputStream());
                paquete_recibido = (PaqueteEnvio) flujo_entrada.readObject();
                if (!paquete_recibido.getMensaje().equals("Online")){
                campo_chat.append(paquete_recibido.getNick() + ": " + paquete_recibido.getMensaje() + " \n");
                }
                else{
                    //campo_chat.append(paquete_recibido.getIps()+  " \n");
                    
                    ArrayList<String> ips_menu= new ArrayList<>();
                    ips_menu=paquete_recibido.getIps();
                    ip.removeAllItems();
                    
                    for(String s:ips_menu){
                        
                        ip.addItem(s);
                    }
                }
          

            }
        } catch (IOException ex) {
            Logger.getLogger(LaminaMarcoCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LaminaMarcoCliente.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //Clase privada para gestionar los eventos
    private class EnviaTexto implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            campo_chat.append("Yo: " + campo1.getText() + "\n");
            try {

                Socket sckt = new Socket("192.168.1.67", 9999);

                PaqueteEnvio datos = new PaqueteEnvio();
                datos.setNick(nick.getText());
                datos.setIp(ip.getSelectedItem().toString());
                datos.setMensaje(campo1.getText());

                ObjectOutputStream paquete_datos = new ObjectOutputStream(sckt.getOutputStream());
                paquete_datos.writeObject(datos);
                sckt.close();

                /*Ejemplo para solo enviar datos
                DataOutputStream flujo_salida= new DataOutputStream(sckt.getOutputStream());
                flujo_salida.writeUTF(campo1.getText());
                flujo_salida.close();
                 */
            } catch (IOException ex) {

                System.out.println(ex.getMessage());

            }

        }

    }

}
// Clase para serializar los paquetes   

class PaqueteEnvio implements Serializable {

    private String nick, ip, mensaje;
    
    private ArrayList <String> ips;

    public ArrayList<String> getIps() {
        return ips;
    }

    public void setIps(ArrayList<String> ips) {
        this.ips = ips;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

}
