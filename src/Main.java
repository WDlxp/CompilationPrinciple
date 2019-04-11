import java.io.IOException;

/**
 * @author wdl
 */
public class Main {
    public static void main(String[] args){

        String regularFormFilePath = "src/regularForm.txt";
        String wordsFilePath = "src/test.txt";

        boolean isPrint = true;

        LexicalAnalysis.lexicalAnalysis(wordsFilePath, regularFormFilePath, isPrint);
    }
}
