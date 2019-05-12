package grammaAnalysis;

import java.util.ArrayList;



/**
 * @author wdl
 */
public class TestMain {
    public static void main(String[] args) {
        String product1 = "Aab|Ac";
        String product2 = "BAb|bc|Ac|bC";
        String product3 = "Cab|bA|bd";
        String product4 = "Cac|df";
        ArrayList<String> productSet = new ArrayList<>();
        productSet.add(product1);
        productSet.add(product2);
        productSet.add(product3);
        productSet.add(product4);
        ProductSetToCFG.CFG cfg=ProductSetToCFG.pToCFG(productSet);
//        cfg=pToCFG(null);
        if (ProductSetToCFG.sError == 0) {
            ProductSetToCFG.showCFG(cfg);
            ProductSetToCFG.CFG cfg1=EliminateLeftRecursion.eliminateExplicitLeftRecursion(cfg);
            if (EliminateLeftRecursion.sError==0){
                ProductSetToCFG.showCFG(cfg1);

                ProductSetToCFG.CFG cfg2=ExtractLeftFactor.extractLeftFactor(cfg1);
                ProductSetToCFG.showCFG(cfg2);

                ProductSetToCFG.CFG cfg3 = FirstAndFollow.getFirstAndFollow(cfg1);
                if (FirstAndFollow.sError==0){
                    ProductSetToCFG.showCFG(cfg3);
                }else if (FirstAndFollow.sError==FirstAndFollow.INTERSECTION_OF_FIRST_AND_FOLLOW_IS_NOT_NULL){
                    System.out.println("First集含空时与Follow集存在存在交集，不符合LL(1)文法");
                }
            } else if (EliminateLeftRecursion.sError==EliminateLeftRecursion.UNABLE_TO_ELIMINATE_LEFT_RECURSION){
                System.out.println("存在无法消除的左递归，不符合LL(1)文法");
            }else if (EliminateLeftRecursion.sError==EliminateLeftRecursion.SYMBOL_OVERFLOW){
                System.out.println("超出可使用的字符集，无法处理");
            }
        }else if (ProductSetToCFG.sError==ProductSetToCFG.P_IS_NULL){
            System.out.println("产生式集合为空，请检查输入的产生式集合");
        }
    }
}
