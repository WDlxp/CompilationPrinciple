package grammaAnalysis.lr0;

import grammaAnalysis.ll1.EliminateLeftRecursion;

import java.util.ArrayList;
import java.util.HashSet;

import static grammaAnalysis.ll1.GrammarAnalysis.getProductSetAndWords;

/**
 * 1.拓广文法
 *
 * @author wdl
 */
public class ExtensionGrammar {

    /**
     * 结果封装好拓广文法的产生式集合以及
     */
    static class Result {
        private ArrayList<String> productSet;
        /**
         * 终结符集合
         */
        private HashSet<Character> terminatorSet;
        /**
         * 非终结符集合
         */
        private HashSet<Character> nonTerminalSet;
        public Result() {
            productSet = new ArrayList<>();
            terminatorSet = new HashSet<>();
            nonTerminalSet = new HashSet<>();
        }
        public Result(ArrayList<String> productSet, HashSet<Character> terminatorSet,
                      HashSet<Character> nonTerminalSet) {
            this.productSet = productSet;
            this.terminatorSet = terminatorSet;
            this.nonTerminalSet = nonTerminalSet;
        }
        public ArrayList<String> getProductSet() {
            return productSet;
        }

        public void setProductSet(ArrayList<String> productSet) {
            this.productSet = productSet;
        }

        public HashSet<Character> getTerminatorSet() {
            return terminatorSet;
        }

        public void setTerminatorSet(HashSet<Character> terminatorSet) {
            this.terminatorSet = terminatorSet;
        }

        public HashSet<Character> getNonTerminalSet() {
            return nonTerminalSet;
        }

        public void setNonTerminalSet(HashSet<Character> nonTerminalSet) {
            this.nonTerminalSet = nonTerminalSet;
        }
    }

    static Result extensionGrammar(ArrayList<String> productSet) {
        /*拓广后的产生式集合*/
        ArrayList<String> extensionProductSet = new ArrayList<>();
        /*终结符集合*/
        HashSet<Character> terminatorSet = new HashSet<>();
        /*非终结符集合*/
        HashSet<Character> nonTerminalSet = new HashSet<>();
        //获取非终态集
        for (String product : productSet) {
            nonTerminalSet.add(product.charAt(0));
        }
        //标记已经使用过的非终结符
        EliminateLeftRecursion.NewChar.flagUsedChar(nonTerminalSet);
        //获取新的终结符
        char newChar = EliminateLeftRecursion.NewChar.getNewChar(nonTerminalSet);
        //放入第一个拓广的
        extensionProductSet.add(newChar + productSet.get(0).substring(0, 1));
        for (String product : productSet) {
            char productLeft = product.charAt(0);
            //将右侧分割为数组
            String[] productRights = product.substring(1).split("\\|");
            for (String productRight :
                    productRights) {
                //拓广文法
                extensionProductSet.add(productLeft + productRight);
                for (int i = 0; i < productRight.length(); i++) {
                    if (!nonTerminalSet.contains(productRight.charAt(i))) {
                        terminatorSet.add(productRight.charAt(i));
                    }
                }
            }
        }
        return new Result(extensionProductSet, terminatorSet, nonTerminalSet);
    }

    /**
     * 展示拓广文法
     * @param result 拓广文法
     */
    static void showExtensionGrammar(Result result){
        System.out.println("-------------拓广文法--------------");
        System.out.println(result.getProductSet());
        System.out.println(result.getNonTerminalSet());
        System.out.println(result.getTerminatorSet());
    }
}
