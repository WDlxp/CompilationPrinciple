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

                ProductSetToCFG.CFG cfg2=ExtractLeftFactor.extractLeftFactor(cfg1);
                ProductSetToCFG.showCFG(cfg2);
            } else if (EliminateLeftRecursion.sError==EliminateLeftRecursion.UNABLE_TO_ELIMINATE_LEFT_RECURSION){
                System.out.println("存在无法消除的左递归，不符合LL(1)文法");
            }else if (EliminateLeftRecursion.sError==EliminateLeftRecursion.SYMBOL_OVERFLOW){
                System.out.println("超出可使用的字符集，无法处理");
            }
        }else if (ProductSetToCFG.sError==ProductSetToCFG.P_IS_NULL){
            System.out.println("产生式集合为空，请检查输入的产生式集合");
        }
    }
    public static ProductSetToCFG.CFG extractLeftFactor(ProductSetToCFG.CFG cfg){
        // ProductSetToCFG.CFG cfg1 = EliminateLeftRecursion.eliminateLeftRecursion(cfg);
        //获取非终结符集合
        HashSet<Character> nonTerminatorSet = cfg.nonTerminatorSet;
        //获取终结符集合
        HashSet<Character> terminatorSet=cfg.terminatorSet;
        //标记已使用的字符
        EliminateLeftRecursion.NewChar.flagUsedChar(cfg.nonTerminatorSet);
        //获取产生式集合
        ArrayList<ProductSetToCFG.Product> productSet = cfg.productSet;
        //新的产生式集合
        ArrayList<ProductSetToCFG.Product> newProductSet=new ArrayList<>();
        //建一个放产生式的栈
        Stack<ProductSetToCFG.Product> productStack = new Stack<>();
        for (ProductSetToCFG.Product product : productSet) {
            productStack.push(product);
            newProductSet.add(product);
        }
        while (!productStack.empty()){
            ArrayList<String> rights1 = productStack.pop().rights;
            int i,j ;
            char commonFactor = ' ';
            ArrayList<String> newRights = new ArrayList<String>();
            boolean [] index = new boolean[100];
            for (String singleString : rights1){
                i = rights1.indexOf(singleString);
                if (index[i] == false){
                    if (rights1.size() == 1 ) {
                        break;
                    }
                    //Boolean flag = false;
                    String str2 = singleString;
                    //int length = rights1.size();
                    for (j = i+1;j <rights1.size() ;j++){
                        if (str2.charAt(0) == rights1.get(j).charAt(0)){
                            //flag = true;
                            commonFactor = str2.charAt(0);
                            if (str2.substring(1).equals("")){
                                newRights.add("ε");
                            }else if(index[i] == false){
                                newRights.add(str2.substring(1));
                            }
                            if (rights1.get(j).substring(1).equals("")){
                                newRights.add("ε");
                            }else if (index[j] == false){
                                newRights.add(rights1.get(j).substring(1));
                            }
                            index[i] = true;
                            index[j] = true;

//                          rights1.remove(j);
//                          rights1.remove(singleString);
                        }
                    }
                }
            }
            for (int m = rights1.size()-1; m >= 0; m--){
                if (index[m] == true) {
                    rights1.remove(m);
                }

//                if (j == rights1.size() && flag == true)
//                    rights1.remove(singleString);
            }

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
        //productSet.addAll(newProductSet);
        return new ProductSetToCFG.CFG(terminatorSet,nonTerminatorSet,cfg.start,productSet);
    }
}
