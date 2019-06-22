package grammaAnalysis.lr0;

import java.util.ArrayList;

import static grammaAnalysis.ll1.GrammarAnalysis.getProductSetAndWords;
import static grammaAnalysis.lr0.ExtensionGrammar.extensionGrammar;
import static grammaAnalysis.lr0.ExtensionGrammar.showExtensionGrammar;

/**
 * 测试使用
 *
 * @author wdl
 */
public class TestMain {
    public static void main(String[] args) {
        ArrayList<String> productSet = new ArrayList<>();
        ArrayList<String> words = new ArrayList<>();
        String path = "src/grammaAnalysis/lr0/productSet";
        //获取输入的产生式集合和单词集合
        getProductSetAndWords(path, productSet, words);
        ExtensionGrammar.Result result = extensionGrammar(productSet);
        showExtensionGrammar(result);

        IdentifyLivePrefixDFA.DFA identifyLivePrefixDFA = IdentifyLivePrefixDFA.identifyLivePrefixDFA(result);
        IdentifyLivePrefixDFA.showDfa(identifyLivePrefixDFA);
    }
}
