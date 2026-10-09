import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class ClienteRegistrador {
    public static void main(String[] args){
        DatagramSocket socketCliente = null;
        Scanner sc;

        try {
            socketCliente = new DatagramSocket();

            sc = new Scanner(System.in);

            int portaServidor = 9000;
            InetAddress host = InetAddress.getByName("localhost");

            while (true) {
                System.out.print("Nome do servidor (ou fim): ");
                String nome = sc.nextLine();
                if (nome.equalsIgnoreCase("fim")) {
                    break;
                }
                System.out.print("Novo ip: ");
                String novoIP = sc.nextLine();
                String atualizacao = nome + " " + novoIP;

                // envio
                byte[] bytesMensagem = new byte[1024];
                bytesMensagem = atualizacao.getBytes();
                DatagramPacket pacoteSend = new DatagramPacket(
                        bytesMensagem,
                        bytesMensagem.length,
                        host,
                        portaServidor
                );

                socketCliente.send(pacoteSend);

                // recebimento
                byte[] buffer = new byte[1024];
                DatagramPacket resposta = new DatagramPacket(buffer, buffer.length);
                socketCliente.receive(resposta);
                System.out.println("Resposta: " +
                        new String(
                                resposta.getData(),
                                0,
                                resposta.getLength()
                        ));
            }

        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
