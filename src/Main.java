import java.io.IOException;

/**
 * @author wdl
 */
public class Main {
    public static void main(String[] args) {

        String regularFormFilePath = "src/regularForm.txt";
        String wordsFilePath = "src/test.txt";
        String resultFilePath = "src/result.txt";

        boolean isPrint = true;

        try {
            LexicalAnalysis.lexicalAnalysis(wordsFilePath, regularFormFilePath, resultFilePath, isPrint);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
