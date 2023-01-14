import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class Client {
    private static Socket socket;
    private static DataOutputStream dos = null;
    private static DataInputStream dis = null;

    public static void initConnection() {
        try {
            socket = new Socket("localhost", Consts.PORT);
            dos = getDataOutputStream();
            dis = getDataInputStream();
        } catch (ConnectException e)  {
            System.out.println("Connection Refused");
            System.exit(-1);
        }  catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        if (dos == null || dis == null) {
            System.out.println("streams are null");
            System.exit(-1);
        }
    }

    public static void main(String[] args) {

        initConnection();

        while (true) {
            int receivedData = 0;
            try {
                receivedData = dis.readInt();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }

            if (receivedData == 0) {
                break;
            }

            System.out.println(receivedData);
        }



    }

    public static DataInputStream getDataInputStream() throws IOException {
        if (socket == null)
            return null;
        return new DataInputStream(socket.getInputStream());
    }
    public static DataOutputStream getDataOutputStream() throws IOException {
        if (socket == null)
            return null;
        return new DataOutputStream(socket.getOutputStream());
    }

}

class FIFO {}
class LRU {}
class SC {}
