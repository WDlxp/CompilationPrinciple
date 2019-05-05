package grammaAnalysis;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * 将产生式集合P转化为CFG
 *
 * @author wdl
 */
public class PToCFG {
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
        CFG cfg=pToCFG(productSet);
//        cfg=pToCFG(null);
        if (sError == 0) {
            showCFG(cfg);
        }else if (sError==1){
            System.out.println("产生式集合为空，请检查输入的产生式集合");
        }
    }

    /**
     * CFG的数据结构定义
     */
    static class CFG {
        HashSet<Character> terminatorSet;
        HashSet<Character> nonTerminatorSet;
        char start;
        ArrayList<String> productSet;

        public CFG(HashSet<Character> terminatorSet, HashSet<Character> nonTerminatorSet, char start, ArrayList<String> productSet) {
            this.terminatorSet = terminatorSet;
            this.nonTerminatorSet = nonTerminatorSet;
            this.start = start;
            this.productSet = productSet;
        }
    }
    /**
     * 通过全局的变量判断是否正确进行
     */
    static int sError = 0;
    /**
     * 定义错误的类型的对应数字
     */
    public static final int P_IS_NULL = 1;

    /**
     * 将产生式集合P转化为CFG
     *
     * @param productSet 产生式集合
     * @return 返回CFG的集合
     */
    public static CFG pToCFG(ArrayList<String> productSet) {
        sError=0;
        if (productSet == null || productSet.size() == 0) {
            sError = P_IS_NULL;
            return null;
        }
        //初始化
        HashSet<Character> terminatorSet = new HashSet<>();
        HashSet<Character> nonTerminatorSet = new HashSet<>();
        char start = productSet.get(0).charAt(0);

        //获取非终态集
        for (String pItem : productSet) {
            nonTerminatorSet.add(pItem.charAt(0));
        }
        //获取终态集
        for (String pItem : productSet) {
            char ch;
            //从下标1开始扫描产生式右边
            for (int index = 1; index < pItem.length(); index++) {
                //获取字符
                ch = pItem.charAt(index);
                //不是非终结符且不是|则放入终结符中
                if (!nonTerminatorSet.contains(ch) && ch != '|') {
                    terminatorSet.add(ch);
                }
            }
        }
        CFG cfg = new CFG(terminatorSet, nonTerminatorSet, start, productSet);
        return cfg;
    }

    /**
     * 打印CFG
     *
     * @param cfg
     */
    public static void showCFG(CFG cfg) {
        //打印非终结符
        System.out.println("终结符T:");
        for (char ch:cfg.terminatorSet){
            System.out.print(ch+"\t");
        }
        //打印终结符
        System.out.println("\n非终结符N:");
        for (char ch:cfg.nonTerminatorSet){
            System.out.print(ch+"\t");
        }
        //打印开始字符
        System.out.println("\n开始字符S:"+cfg.start);
        //打印产生式集合
        System.out.println("产生式集合P:");
        for (String product:cfg.productSet){
            System.out.println(product.charAt(0)+"-->"+product.substring(1,product.length()));
        }
    }
}
