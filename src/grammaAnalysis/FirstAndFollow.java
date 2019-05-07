package grammaAnalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;


/**
 * 4.求First集和Follow集
 *
 * @author wdl
 */
public class FirstAndFollow {


    public static void main(String[] args) {
        String product1 = "Aab|Bd";
        String product2 = "Bcd|Cf";
        String product3 = "Cε";
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

    /**
     * 标记错误
     */
    static int sError = 0;

    /**
     * 求First集和Follow集
     *
     * @param cfg 输入CFG
     * @return 返回含有First集和Follow集的产生式
     */
    public static ProductSetToCFG.CFG firstAndFollow(ProductSetToCFG.CFG cfg) {
        //获取终结符集合
        HashSet<Character> terminatorSet = cfg.terminatorSet;
        //获取产生式集合
        ArrayList<ProductSetToCFG.Product> productSet = cfg.productSet;
        //做一个产生式左左侧和下标的映射
        HashMap<Character, Integer> symbolOfIndex = new HashMap<>();
        for (int i = 0; i < productSet.size(); i++) {
            symbolOfIndex.put(productSet.get(i).left, i);
        }

        for (int index = productSet.size() - 1; index >= 0; index--) {
            ProductSetToCFG.Product product = productSet.get(index);
            product.first = getTotalFirst(productSet, terminatorSet, symbolOfIndex, product.left);
        }
        return cfg;
    }

    public static HashSet<Character> getTotalFirst(ArrayList<ProductSetToCFG.Product> productSet, HashSet<Character> terminatorSet, HashMap<Character, Integer> symbolOfIndex, char left) {
        HashSet<Character> first = new HashSet<>();
        int index = symbolOfIndex.get(left);
        ArrayList<String> rights = productSet.get(index).rights;
        for (String right : rights) {
            first.addAll(getFirst(productSet, terminatorSet, symbolOfIndex, left, right));
        }
        return first;
    }

    private static HashSet<Character> getFirst(ArrayList<ProductSetToCFG.Product> productSet, HashSet<Character> terminatorSet, HashMap<Character, Integer> symbolOfIndex, char left, String right) {
        HashSet<Character> first = new HashSet<>();
        char firstChar = right.charAt(0);
        HashSet<Character> tempFirst=new HashSet<>();
        //如果第一个字符是非终结符直接返回
        if (terminatorSet.contains(firstChar)) {
            first.add(firstChar);
        }
        //如果第一个字符为产生式左侧不同则处理
        else if (firstChar != left) {
            int index = symbolOfIndex.get(firstChar);
            if (productSet.get(index).first != null) {
                tempFirst.addAll(productSet.get(index).first);
            } else {
                tempFirst.addAll(getTotalFirst(productSet, terminatorSet, symbolOfIndex, firstChar));
            }
            if (right.length()==1){
                first.addAll(tempFirst);
            }else {
                if (tempFirst.contains('ε')){
                    tempFirst.remove('ε');
                    first.addAll(tempFirst);
                    first.addAll(getFirst(productSet, terminatorSet, symbolOfIndex, right.substring(1).charAt(0), right.substring(1)));
                }else {
                    first.addAll(tempFirst);
                }
            }
        }
        return first;
    }
}
