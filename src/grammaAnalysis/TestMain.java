package grammaAnalysis;

import java.util.ArrayList;


/**
 * @author wdl
 */
public class TestMain {
    public static void main(String[] args) {
        //测试用例1
        String product1 = "S(L)|aA|a";
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

        //将产生式转为CFG
        ProductSetToCFG.CFGResult pToCfgResult = ProductSetToCFG.pToCFG(productSet);
//        cfg=pToCFG(null);
        //判断转CFG过程是否出错
        if (pToCfgResult.getsError() == 0) {
            System.out.println("产生式转CFG：");
            ProductSetToCFG.showCFG(pToCfgResult.getCfg());
            //消除左递归
            ProductSetToCFG.CFGResult eliminateLeftRecursionCfgResult = EliminateLeftRecursion.eliminateLeftRecursion(pToCfgResult.getCfg());
            if (eliminateLeftRecursionCfgResult.getsError() == 0) {
                System.out.println("\n消除左递归：");
                ProductSetToCFG.showCFG(eliminateLeftRecursionCfgResult.getCfg());
                //提取左因子
                ProductSetToCFG.CFGResult extractLeftFactorCfgResult = ExtractLeftFactor.extractLeftFactor(eliminateLeftRecursionCfgResult.getCfg());
                if (extractLeftFactorCfgResult.getsError()==0){
                    System.out.println("\n提取左因子：");
                    ProductSetToCFG.showCFG(extractLeftFactorCfgResult.getCfg());
                    //获取First集和Follow集
                    ProductSetToCFG.CFGResult firstAndFollowCfgResult = FirstAndFollow.getFirstAndFollow(eliminateLeftRecursionCfgResult.getCfg());
                    if (firstAndFollowCfgResult.getsError() == 0) {
                        System.out.println("\n获取First集和Follow集：");
                        ProductSetToCFG.showCFG(firstAndFollowCfgResult.getCfg());
                        //生成预测分析表
                        PredictionTable.PreTableResult preTableResult = PredictionTable.predictionTable(firstAndFollowCfgResult.getCfg());
                        if (preTableResult.getsError() == 0) {
                            PredictionTable.showPreTable(preTableResult.getPreTable(), preTableResult.getColSymbolSet(), preTableResult.getRowSymbolSet());
                            //需要判断的字符
                            String input = "a";
                            //下推自动机输出判断结果
                            System.out.println(PushDownAutomaton.predictiveAnalyzerSolution2(input, preTableResult));
                        } else if (preTableResult.getsError() == PredictionTable.EXIST_IMPLICIT_LEFT_FACTOR) {
                            System.out.println("存在隐式左因子，不符合LL(1)文法");
                        }
                    } else if (firstAndFollowCfgResult.getsError() == FirstAndFollow.INTERSECTION_OF_FIRST_AND_FOLLOW_IS_NOT_NULL) {
                        System.out.println("First集含空时与Follow集存在存在交集，不符合LL(1)文法");
                    }
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
