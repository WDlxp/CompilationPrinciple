package grammaAnalysis.lr0;

import java.util.ArrayList;

import static grammaAnalysis.ll1.GrammarAnalysis.getProductSetAndWords;
import static grammaAnalysis.lr0.ExtensionGrammar.extensionGrammar;

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
        System.out.println("拓广文法：");
        System.out.println(result.getProductSet());
        System.out.println(result.getNonTerminalSet());
        System.out.println(result.getTerminatorSet());

        IdentifyLivePrefixDFA.DFA identifyLivePrefixDFA = IdentifyLivePrefixDFA.identifyLivePrefixDFA(result);
        ArrayList<LrState> lrStates = identifyLivePrefixDFA.getLrStates();
        ArrayList<IdentifyLivePrefixDFA.LrSide> lrSides = identifyLivePrefixDFA.getLrSides();
        for (LrState state :
                lrStates) {
            ArrayList<LrProject> coreItems = state.coreItems;
            ArrayList<LrProject> closureItems = state.closureItems;
            System.out.println("状态" + state.stateIndex);
            for (LrProject lrProject :
                    coreItems) {
                System.out.println(lrProject.leftSide + "-->" + lrProject.rightSide + lrProject.dotPointer);
            }
            for (LrProject lrProject :
                    closureItems) {
                System.out.println(lrProject.leftSide + "-->" + lrProject.rightSide + lrProject.dotPointer);
            }
        }

        for (IdentifyLivePrefixDFA.LrSide side :
                lrSides) {
            System.out.println(side.startState.stateIndex + "--" + side.way + "-->" + side.endState.stateIndex);
        }
    }
}
