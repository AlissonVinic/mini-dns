import java.io.IOException;
import java.net.*;
import java.util.*;

public class ServidorDNS {
    public static void main(String[] args){
        int porta = 9000;
        DatagramSocket socketServidor = null;
        Map<String, String> mapaServers = new LinkedHashMap<>();
        Set<SocketAddress> clientesRequisitantes = new HashSet<>();

        for(int i = 1; i <= 10; i++){
            mapaServers.put("servidor" + i, "192.168.0." + i + "0");
        }

       try{
           socketServidor = new DatagramSocket(porta);
           System.out.println("Servidor rodando na porta " + porta);

           mapaServers.forEach((nome, ip) -> System.out.println(nome + "-> " + ip));

           while(true){
               //recebimento do nome do servidor
               byte[] bufferRec = new byte[1024];

               DatagramPacket datagramaRec = new DatagramPacket(bufferRec, bufferRec.length);

               socketServidor.receive(datagramaRec);

               String mensagem = new String (datagramaRec.getData(), 0, datagramaRec.getLength());

               System.out.println("Mensagem recebida: " + mensagem);

               //verificação se é requisição ou registro
               String[] partes = mensagem.trim().split("\\s+");

               String msgResposta;

               if (partes.length == 1) {
                   clientesRequisitantes.add(datagramaRec.getSocketAddress());

                   String nome = partes[0];
                   String ip = mapaServers.get(nome);

                   if (ip != null) {
                       msgResposta = ip;
                   } else {
                       msgResposta = "Servidor não encontrado!";
                   }

               } else if (partes.length == 2) {

                   // Atualização
                   String nome = partes[0];
                   String novoIp = partes[1];

                   if (mapaServers.containsKey(nome)) {
                       mapaServers.put(nome, novoIp);
                       msgResposta = "IP atualizado com sucesso!";

                       //mandando a atualização de ip para todos os clientes requisitantes
                       for (SocketAddress cliente : clientesRequisitantes) {

                           String mensagemAtualizacao = nome + "->" + novoIp;

                           byte[] bufferAtualizacao = new byte[1024];
                           bufferAtualizacao = mensagemAtualizacao.getBytes();

                           DatagramPacket pacoteAtualizacao =
                                   new DatagramPacket(
                                           bufferAtualizacao,
                                           bufferAtualizacao.length,
                                           cliente
                                   );

                           socketServidor.send(pacoteAtualizacao);
                       }
                   } else {
                       msgResposta = "Servidor não encontrado!";
                   }

               } else {
                   msgResposta = "Mensagem inválida!";
               }

               byte[] bufferEnvio = new byte[1024];

               bufferEnvio = msgResposta.getBytes();

               DatagramPacket pacoteSend =
                       new DatagramPacket(
                               bufferEnvio,
                               bufferEnvio.length,
                               datagramaRec.getAddress(),
                               datagramaRec.getPort()
                       );

               socketServidor.send(pacoteSend);

               System.out.println("Resposta Enviada: " + msgResposta);
           }
       }catch (IOException e){
           e.printStackTrace();
       }
    }

}
