import java.io.FileWriter;
import java.io.IOException;
public class FileManager {
    public static void saveTransaction(String text) {
        try {
            FileWriter writer = new FileWriter("transactions.txt",true);
            writer.write(text);
            writer.write(" ");
            writer.close();
        }
        catch(IOException e) {
            System.out.println("cannot save transaction.");
        }
    }
}

