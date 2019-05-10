package grammaAnalysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.lang.String;
import java.util.Stack;

/**
 * 3.提取左因子
 * @author wdl
 */
public class ExtractLeftFactor {
    public static void main(String[] args){
        String product1 = "AabD";
        String product2 = "DcD|ε";
        String product3 = "BAb|bc|Ac|bC";
        String product4 = "Cab|bA|bd|ab|df";
//        A-->abD
//        D-->cD|ε
//        B-->Ab|bc|Ac|bC
//        C-->ab|bA|bd|ab|df


        ArrayList<String> productSet = new ArrayList<>();
        productSet.add(product1);
        productSet.add(product2);
        productSet.add(product3);
        productSet.add(product4);
        ProductSetToCFG.CFG cfg=ProductSetToCFG.pToCFG(productSet);
        ProductSetToCFG.showCFG(cfg);
        ProductSetToCFG.CFG newCfg = ExtractLeftFactor.extractLeftFactor(cfg);
        ProductSetToCFG.showCFG(newCfg);
    }
    public static ProductSetToCFG.CFG extractLeftFactor(ProductSetToCFG.CFG cfg1){
        // ProductSetToCFG.CFG cfg1 = EliminateLeftRecursion.eliminateLeftRecursion(cfg);
        //获取非终结符集合
        HashSet<Character> nonTerminatorSet = cfg1.nonTerminatorSet;
        //获取终结符集合
        HashSet<Character> terminatorSet=cfg1.terminatorSet;
        //标记已使用的字符
        EliminateLeftRecursion.NewChar.flagUsedChar(cfg1.nonTerminatorSet);
        //获取产生式集合
        ArrayList<ProductSetToCFG.Product> productSet = cfg1.productSet;
        //新的产生式集合
        ArrayList<ProductSetToCFG.Product> newProductSet=new ArrayList<>();
        //建一个放产生式的栈
        Stack<ProductSetToCFG.Product> productStack = new Stack<>();
        boolean flag1 = false;
        //检查是否存在左因子
        for (ProductSetToCFG.Product product : productSet) {
            ArrayList<String> rights2 = product.rights;
            for (int i = 0; i < rights2.size(); i++){
                char c = rights2.get(0).charAt(0);
                for (int j = i+1; j < rights2.size();j++){
                    if (c == rights2.get(j).charAt(0)) {
                        flag1 = true;
                        break;
                    }
                }
                if (flag1) {
                    break;
                }
            }
            if (flag1) {
                break;
            }
        }
        if (!flag1) {
            return cfg1;
        }else {
            for (ProductSetToCFG.Product product : productSet) {
                productStack.push(product);
                newProductSet.add(product);
            }
        }
        //将所有产生式放入栈中，依次提取
        while (!productStack.empty()){
            ProductSetToCFG.Product p = productStack.pop();
            ArrayList<String> rights1 = p.rights;
            int i,j,n=rights1.size() ;
            boolean flag = false;
            char commonFactor = ' ';
            ArrayList<String> newRights = new ArrayList<String>();
            boolean [] index = new boolean[n];
            //遍历每个产生式右部的字符串
            for (String singleString : rights1){
                i = rights1.indexOf(singleString);
                if (i == n-1) {
                    break;
                }
                if (!index[i]){
                    if (rights1.size() == 1 ) {
                        break;
                    }
                    String str2 = singleString;
                    //int length = rights1.size();
                    for (j = i+1;j <n ;j++){

                        //判断是否存在左因子，比较每个字符第一个字符
                        if (str2.charAt(0) == rights1.get(j).charAt(0)){
                            flag = true;
                            commonFactor = str2.charAt(0);
                            if (str2.substring(1).equals("")){
                                newRights.add("ε");
                            }else if(!index[i]){
                                newRights.add(str2.substring(1));
                            }
                            if (rights1.get(j).substring(1).equals("")){
                                newRights.add("ε");
                            }else if (!index[j]){
                                newRights.add(rights1.get(j).substring(1));
                            }
                            index[i] = true;
                            index[j] = true;


                        }
                    }
                    //一旦找到一组左因子就先退出
                    if (flag) {
                        break;
                    }
                }

            }
            //移除原产生式中被提取左因子的字符串
            for (int m = n-1; m >= 0; m--){
                if (index[m]) {
                    rights1.remove(m);
                }
                //若存在一组左因子，将移除被提取左因子的产生式再放入栈中
                if (flag) {
                    p.rights = rights1;
                    productStack.push(p);
                }

            }
            //若存在左因子，将提取出的newRights和新生成的newProductStart组成新的产生式放入栈中
            if (commonFactor != ' ') {
                char newProductStart = EliminateLeftRecursion.NewChar.getNewChar(nonTerminatorSet);
                nonTerminatorSet.add(newProductStart);
                String str = String.valueOf(commonFactor) + newProductStart;
                rights1.add(str);
                ProductSetToCFG.Product newProductSet1 = new ProductSetToCFG.Product(newProductStart, newRights);
                productStack.push(newProductSet1);
                productSet.add(newProductSet1);
            }
        }

        return new ProductSetToCFG.CFG(terminatorSet,nonTerminatorSet,cfg1.start,productSet);
    }
}