package grammaAnalysis;

import java.util.ArrayList;

/**
 * @author wdl
 */
public class TestMain {
    public static void main(String[] args) {
        String product1 = "Aab|Bc|Ac";
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
            EliminateLeftRecursion.eliminateLeftRecursion(cfg);
        }else if (ProductSetToCFG.sError==ProductSetToCFG.P_IS_NULL){
            System.out.println("产生式集合为空，请检查输入的产生式集合");
        }
    }
}
