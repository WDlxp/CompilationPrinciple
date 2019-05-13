package grammaAnalysis;

import java.util.ArrayList;


/**
 * @author wdl
 */
public class TestMain {
    public static void main(String[] args) {
        //测试用例1
        String product1 = "S(L)|aA";
        String product2 = "AS|ε";
        String product3 = "LSB";
        String product4 = "B,SB|ε";
        ArrayList<String> productSet = new ArrayList<>();
        productSet.add(product1);
        productSet.add(product2);
        productSet.add(product3);
        productSet.add(product4);
//        测试用例2
//        String product1 = "ABb|Ac";
//        String product2 = "BAb|bc|Ac|bC";
//        String product3 = "Cab|bA|bd";
//        String product4 = "Dac|df";
//        ArrayList<String> productSet = new ArrayList<>();
//        productSet.add(product1);
//        productSet.add(product2);
//        productSet.add(product3);
//        productSet.add(product4);
        ProductSetToCFG.CFGResult pToCfgResult = ProductSetToCFG.pToCFG(productSet);
//        cfg=pToCFG(null);
        if (pToCfgResult.getsError() == 0) {
            ProductSetToCFG.showCFG(pToCfgResult.getCfg());
            ProductSetToCFG.CFGResult eliminateLeftRecursionCfgResult = EliminateLeftRecursion.eliminateLeftRecursion(pToCfgResult.getCfg());
            if (eliminateLeftRecursionCfgResult.getsError() == 0) {
                ProductSetToCFG.showCFG(eliminateLeftRecursionCfgResult.getCfg());
                ProductSetToCFG.CFGResult firstAndFollowCfgResult = FirstAndFollow.getFirstAndFollow(eliminateLeftRecursionCfgResult.getCfg());
                if (firstAndFollowCfgResult.getsError() == 0) {
                    ProductSetToCFG.showCFG(firstAndFollowCfgResult.getCfg());
                    PredictionTable.PreTableResult preTableResult = PredictionTable.predictionTable(firstAndFollowCfgResult.getCfg());
                    if (preTableResult.getsError() == 0) {
                        PredictionTable.showPreTable(preTableResult.getPreTable(), preTableResult.getColSymbolSet(), preTableResult.getRowSymbolSet());
                        String input="a";
                        System.out.println(PushDownAutomaton.predictiveAnalyzerSolution2(input,preTableResult));
                    } else if (preTableResult.getsError() == PredictionTable.EXIST_IMPLICIT_LEFT_FACTOR) {
                        System.out.println("存在隐式左因子，不符合LL(1)文法");
                    }
                } else if (firstAndFollowCfgResult.getsError() == FirstAndFollow.INTERSECTION_OF_FIRST_AND_FOLLOW_IS_NOT_NULL) {
                    System.out.println("First集含空时与Follow集存在存在交集，不符合LL(1)文法");
                }

            } else if (eliminateLeftRecursionCfgResult.getsError() == EliminateLeftRecursion.UNABLE_TO_ELIMINATE_LEFT_RECURSION) {
                System.out.println("存在无法消除的左递归，不符合LL(1)文法");
            } else if (eliminateLeftRecursionCfgResult.getsError() == EliminateLeftRecursion.SYMBOL_OVERFLOW) {
                System.out.println("超出可使用的字符集，无法处理");
            }
        } else if (pToCfgResult.getsError() == ProductSetToCFG.P_IS_NULL) {
            System.out.println("产生式集合为空，请检查输入的产生式集合");
        }
    }
}
