import java.io.File;
import java.io.IOException;

/**
 * @author wdl
 */
public class Main {
    public static void main(String[] args) throws IOException {
        //(((i n|t )*(ab|c.d))*)*
        String regularFormString = "int";

        File testFile = new File("src/test.txt");
        //
        String filePath = testFile.getPath();

        boolean isPrint = true;
        LexicalAnalysis.lexicalAnalysis(filePath, regularFormString, isPrint);
    }
}
