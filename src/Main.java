import java.io.IOException;

/**
 * @author wdl
 */
public class Main {
    public static void main(String[] args) throws IOException {
        //(((i n|t )*(ab|c.d))*)*
        String regularFormString = "int*";

        String filePath = "src/test.txt";

        boolean isPrint = true;

        LexicalAnalysis.lexicalAnalysis(filePath, regularFormString, isPrint);
    }
}
