package grammaAnalysis.lr0;

import java.util.ArrayList;

import static grammaAnalysis.ll1.GrammarAnalysis.getProductSetAndWords;

/**
 * 1.拓广文法
 *
 * @author wdl
 */
public class ExtensionGrammar {
    public static void main(String[] args) {
        ArrayList<String> productSet = new ArrayList<>();
        ArrayList<String> words = new ArrayList<>();
        String path = "src/grammaAnalysis/lr0/productSet";
        //获取输入的产生式集合和单词集合
        getProductSetAndWords(path, productSet, words);
    }
}
