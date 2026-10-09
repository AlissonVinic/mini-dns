import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class ClienteRequisitante {
    public static void main(String[] args){
        DatagramSocket socketCliente = null;
        Scanner sc;
        try{
            socketCliente = new DatagramSocket();

            sc = new Scanner(System.in);

            System.out.print("Nome do servidor: ");
            String mensagem = sc.nextLine();

            byte[] bytesMensagem = mensagem.getBytes();
            InetAddress host = InetAddress.getByName("localhost");
            int portaServidor = 9000;
            DatagramPacket requisicao =
                    new DatagramPacket(
                            bytesMensagem,
                            bytesMensagem.length,
                            host,
                            portaServidor
                    );
            socketCliente.send(requisicao);

            //recebimento
            byte[] buffer = new byte[1024];
            DatagramPacket resposta =
                    new DatagramPacket(buffer, buffer.length);
            socketCliente.receive(resposta);
            System.out.println("Resposta: " +
                    new String(resposta.getData(), 0, resposta.getLength()));

            //recebimento de atualizações
            while (true) {

                byte[] bufferAtualizacao = new byte[1024];
                DatagramPacket atualizacao = new DatagramPacket(bufferAtualizacao, bufferAtualizacao.length);
                socketCliente.receive(atualizacao);

                System.out.println("Atualização recebida: " + new String(atualizacao.getData(), 0, atualizacao.getLength()));
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
