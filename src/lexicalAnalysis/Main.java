package lexicalAnalysis;

import java.io.IOException;

/**
 * @author wdl
 */
public class Main {
    public static void main(String[] args) {
        String regularFormFilePath = "src/lexicalAnalysis/regularForm.txt";
        String wordsFilePath = "src/lexicalAnalysis/test.txt";
        String resultFilePath = "src/lexicalAnalysis/result.txt";

        boolean isPrint = false;

        try {
            LexicalAnalysis.lexicalAnalysis(wordsFilePath, regularFormFilePath, resultFilePath, isPrint);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
