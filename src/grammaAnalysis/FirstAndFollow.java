package grammaAnalysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;


/**
 * 4.求First集和Follow集
 * @author wdl
 */
public class FirstAndFollow {


    public static void main(String[] args) {

    }
    /**
     *  标记错误
     */
    static int sError=0;
    /**
     * 求First集和Follow集
     * @param cfg 输入CFG
     * @return 返回含有First集和Follow集的产生式
     */
    public static ProductSetToCFG.CFG firstAndFollow(ProductSetToCFG.CFG cfg){
        //获取非终结符
        HashSet<Character> nonTerminatorSet=cfg.nonTerminatorSet;
        HashSet<Character> terminatorSet=cfg.terminatorSet;
        //获取产生式集合
        ArrayList<ProductSetToCFG.Product> productSet=cfg.productSet;
        ArrayList<String> rights;
        for (ProductSetToCFG.Product product:productSet){
            product.first=new HashSet<>();
            product.follow=new HashSet<>();
            //获取产生式右边的集合
            rights=product.rights;
            //第一个字符
            char firstSymbol;
            for (int index=0;index<rights.size();index++){
                //如果是终结符，则直接加入First集中
                firstSymbol=rights.get(index).charAt(0);
                if (terminatorSet.contains(firstSymbol)){
                    product.first.add(firstSymbol);
                }else {
                    Stack<Character> symbolStack=new Stack<>();
                    symbolStack.push(firstSymbol);
                    while (!symbolStack.isEmpty()){
                        Character symbol=symbolStack.pop();
                    }
                }
            }
        }

        return null;
    }
}