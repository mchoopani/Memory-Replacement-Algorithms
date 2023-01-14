import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(Consts.PORT);
        Socket socket = serverSocket.accept();

        DataOutputStream socketOut = null;
        try {
            socketOut = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Client connected");

        try {
            int memorySize = ThreadLocalRandom.current().nextInt(1, 10 + 1);
            socketOut.writeInt(memorySize);

            int memoryAccessCount = ThreadLocalRandom.current().nextInt(5, 100 + 1);
            for (int i = 0; i < memoryAccessCount; i++) {
                int memoryAddress = ThreadLocalRandom.current().nextInt(5, 100 + 1);
                socketOut.writeInt(memoryAddress);
                System.out.println(memoryAddress);

                int delay = ThreadLocalRandom.current().nextInt(1, 2000 + 1);
                Thread.sleep(delay);
            }

            socketOut.writeInt(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}