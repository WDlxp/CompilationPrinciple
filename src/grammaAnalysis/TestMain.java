package grammaAnalysis;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author wdl
 */
public class TestMain {
    public static void main(String[] args) {
        String product1 = "Aab|Ac";
        String product2 = "BAb|bc|Ac|bC";
        String product3 = "Cab|bA|bd";
        String product4 = "Cab|df";
        ArrayList<String> productSet = new ArrayList<>();
        productSet.add(product1);
        productSet.add(product2);
        productSet.add(product3);
        productSet.add(product4);
        ProductSetToCFG.CFG cfg=ProductSetToCFG.pToCFG(productSet);
//        cfg=pToCFG(null);
        if (ProductSetToCFG.sError == 0) {
            ProductSetToCFG.showCFG(cfg);
            ProductSetToCFG.CFG cfg1=EliminateLeftRecursion.eliminateLeftRecursion(cfg);
            if (EliminateLeftRecursion.sError==0){
                ProductSetToCFG.showCFG(cfg1);
                ProductSetToCFG.CFG cfg2=FirstAndFollow.firstAndFollow(cfg1);
                ArrayList<ProductSetToCFG.Product> productSet1=cfg2.productSet;
                for (ProductSetToCFG.Product product:productSet1){
                    System.out.println(product.first);
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
