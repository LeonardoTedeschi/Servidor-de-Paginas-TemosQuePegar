import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class BaseServidor {
    public static void main(String[] args) {
        final int port = 4747; // Porta do servidor
        final String directoryPath = "C:\\Users\\leote\\Desktop\\Servidor de Paginas\\TemosQuePegar\\arquivos"; // Caminho do diretório desejado

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Favor Serguir para a porta: " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Um novo Treinador Chegou: " + clientSocket);

                // Criar uma nova thread para lidar com a conexão do cliente
                Thread thread = new Thread(() -> {
                    try {
                        // Obter o fluxo de entrada e saída para receber a solicitação e enviar a resposta ao cliente
                        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        OutputStream outputStream = clientSocket.getOutputStream();
                        PrintWriter writer = new PrintWriter(outputStream, true);

                        // Obter a solicitação do cliente
                        String request = reader.readLine();
                        System.out.println("Buscando informações: " + request);

                        // Verificar se a solicitação é válida e se é uma solicitação GET
                        if (request != null && request.startsWith("GET")) {
                            // Extrair o caminho solicitado do cabeçalho GET
                            String path = request.split(" ")[1];

                            // Verificar se é uma solicitação para o caminho especial "/Header"
                            if (path.equals("/Header")) {
                                // Enviar a resposta de cabeçalho HTTP com o conteúdo de texto
                                writer.println("HTTP/1.1 200 OK");
                                writer.println("Content-Type: text/plain");
                                writer.println();

                                // Enviar o cabeçalho HTTP enviado pelo cliente
                                writer.println("Cabeçalho HTTP enviado pelo cliente:");
                                writer.println();
                                writer.println(request);

                                writer.println();
                                writer.println("Fim do cabeçalho.");
                            } else if (path.equals("/")) {
                                // Enviar a resposta de cabeçalho HTTP com o conteúdo HTML
                                writer.println("HTTP/1.1 200 OK");
                                writer.println("Content-Type: text/html");
                                writer.println();

                                // Gerar o conteúdo HTML
                                writer.println("<html>");
                                writer.println("<head><title>PokeCode Showdown</title>");
                                writer.println("<style>");
                                writer.println("a:link { color: #00E545; background-color: transparent; text-decoration: none; }");
                                writer.println("a:visited { color: #FFA700; background-color: transparent; text-decoration: none;}");
                                writer.println("a:hover { color: #FF3882; background-color: transparent; text-decoration: none;}");
                                writer.println("body {background-image: url('https://www.10wallpaper.com/wallpaper/2560x1600/1211/Firefox_Pokemon-Brand_advertising_Wallpapers_2560x1600.jpg'); background-repeat: no-repeat; background-size: cover;}");
                                writer.println("</style>");
                                writer.println("</head>");
                                writer.println("<body>");
                                writer.println("<h2>Guia:</h2>");
                                writer.println("<h4><a href=\"https://www.smogon.com/dex/sv/formats/national-dex/\">Regras NationalDex</a></h4>");
                                writer.println("<h3>Pokemons disponiveis:</h3>");
                                writer.println("<ul>");

                                // Listar os arquivos e pastas no diretório
                                List<String> arquivos = listItems(directoryPath);
                                for (String item : arquivos) {
                                    writer.println("<li><a href=\"" + item + "\">" + item + "</a></li>");
                                }
 
                                writer.println("</ul>");
                                writer.println("color: #ffffff; <h3>Siga a Glitch para saber mais:</h3>");
                                writer.println("<h4>No Instragram: <a href=\"https://www.instagram.com/glitch_ufsj/\">Glich_UFSJ</a></h4>");
                                writer.println("<h4>Na Twitch: <a href=\"https://www.twitch.tv/glitch_ufsj\">Glich UFSJ</a></h4>");
                                writer.println("</body>");
                                writer.println("</html>");
                            } else {
                                // Remover a barra inicial do caminho
                                String filePath = directoryPath + File.separator + path.substring(1);

                                // Verificar se o arquivo ou pasta existe
                                File file = new File(filePath);
                                if (file.exists()) {
                                    if (file.isFile()) {
                                        // É um arquivo, enviar o arquivo como resposta
                                        FileInputStream fileInputStream = new FileInputStream(file);

                                        // Enviar a resposta de cabeçalho HTTP com o tipo de conteúdo adequado
                                        writer.println("HTTP/1.1 200 OK");
                                        writer.println("Content-Type: application/octet-stream");
                                        writer.println("Content-Disposition: attachment; filename=\"" + file.getName() + "\"");
                                        writer.println();

                                        // Enviar o conteúdo do arquivo
                                        byte[] buffer = new byte[1024];
                                        int bytesRead;
                                        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                                            outputStream.write(buffer, 0, bytesRead);
                                        }

                                        fileInputStream.close();
                                    } else if (file.isDirectory()) {
                                        // É uma pasta, redirecionar para a página que lista os arquivos e pastas dentro da pasta
                                        writer.println("HTTP/1.1 301 Moved Permanently");
                                        writer.println("Location: /" + path + "/");
                                        writer.println();
                                    }
                                } else {
                                    // O arquivo ou pasta não existe, enviar resposta de página não encontrada (404)
                                    writer.println("HTTP/1.1 404 Not Found");
                                    writer.println("Content-Type: text/html");
                                    writer.println();

                                    // Gerar o conteúdo HTML para a página não encontrada
                                    writer.println("<html>");
                                    writer.println("<head><title>Esse lugar não existe</title></head>");
                                    writer.println("<body>");
                                    writer.println("<h1>Acho que você se perdeu treinador!</h1>");
                                    writer.println("<p>Não achei o que estava buscando</p>");
                                    writer.println("</body>");
                                    writer.println("</html>");
                                }
                            }
                        }

                        // Fechar o fluxo de entrada, saída e a conexão com o cliente
                        reader.close();
                        writer.close();
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> listItems(String directoryPath) {
        List<String> arquivos = new ArrayList<>();

        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String fileName = file.getName();
                    arquivos.add(fileName);
                } else if (file.isDirectory()) {
                    String folderName = file.getName() + "/";
                    arquivos.add(folderName);
                }
            }
        }

        return arquivos;
    }
}