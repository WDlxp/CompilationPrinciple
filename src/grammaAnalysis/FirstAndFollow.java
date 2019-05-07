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
        String product1 = "Aab|Be";
        String product2 = "Bcd|Cf|ε";
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
        //自下而上求First集
        ProductSetToCFG.Product product;
        for (int index = productSet.size() - 1; index >= 0; index--) {
            //获取当前的产生式
            product = productSet.get(index);
            //求当前产生式的first集
            product.first = getTotalFirst(productSet, terminatorSet, symbolOfIndex, product.left);
        }
        return cfg;
    }

    /**
     * 获取产生式总的First集
     * @param productSet 产生式集合
     * @param terminatorSet 终态集
     * @param symbolOfIndex 产生式左侧与对于产生式集合下标的映射
     * @param left 需要求First集的产生式左侧
     * @return 放回First集
     */
    public static HashSet<Character> getTotalFirst(ArrayList<ProductSetToCFG.Product> productSet, HashSet<Character> terminatorSet, HashMap<Character, Integer> symbolOfIndex, char left) {
        //初始化一个First集
        HashSet<Character> first = new HashSet<>();
        //获取当前求First集的产生式的下标
        int index = symbolOfIndex.get(left);
        //获取这个产生式的右侧
        ArrayList<String> rights = productSet.get(index).rights;
        //遍历产生式右侧求每一项的First集，整合起来即为整个产生式的First集
        for (String right : rights) {
            first.addAll(getFirst(productSet, terminatorSet, symbolOfIndex, left, right));
        }
        return first;
    }

    /**
     *求产生式右侧一项的First集
     * @param productSet 产生式集合
     * @param terminatorSet 终态集
     * @param symbolOfIndex 产生式左侧与对于产生式集合下标的映射
     * @param left 需要求First集的产生式左侧
     * @param right 产生式右侧的某一项
     * @return 返回一项的First集
     */
    private static HashSet<Character> getFirst(ArrayList<ProductSetToCFG.Product> productSet, HashSet<Character> terminatorSet, HashMap<Character, Integer> symbolOfIndex, char left, String right) {
        HashSet<Character> first = new HashSet<>();
        //获取第一个字符
        char firstChar = right.charAt(0);
        //初始化一个过渡的First集
        HashSet<Character> tempFirst=new HashSet<>();
        //如果第一个字符是非终结符直接返回
        if (terminatorSet.contains(firstChar)) {
            first.add(firstChar);
        }
        //如果第一个字符为非产生式且与产生式左侧不同则处理（与产生式左侧相同不处理）
        else if (firstChar != left) {
            //获取第一个字符对应产生式集合的位置
            int index = symbolOfIndex.get(firstChar);
            //如果该非总结符对应产生式的First集非空即说明已经求得该产生式的First集，直接使用即可
            if (productSet.get(index).first != null) {
                tempFirst.addAll(productSet.get(index).first);
            }
            //否则重新求这个非终结符对应的产生式的First集
            else {
                tempFirst.addAll(getTotalFirst(productSet, terminatorSet, symbolOfIndex, firstChar));
            }
            //如果当前仅有一个字符
            if (right.length()==1){
                //则直接将前面求得的结果作为该项最终结果
                first.addAll(tempFirst);
            }
            //不仅一个字符
            else {
                //判断该First集中是否含有ε，如果有
                if (tempFirst.contains('ε')){
                    //将该First集去掉ε加到所求项的First集中
                    tempFirst.remove('ε');
                    first.addAll(tempFirst);
                    //将该字符后面字符的First集加到所求项的First集中
                    first.addAll(getFirst(productSet, terminatorSet, symbolOfIndex, right.substring(1).charAt(0), right.substring(1)));
                }
                //不含ε则将该First集直接加到所求项的First集中
                else {
                    first.addAll(tempFirst);
                }
            }
        }
        return first;
    }
}
