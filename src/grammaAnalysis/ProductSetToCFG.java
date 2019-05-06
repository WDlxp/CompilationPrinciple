package grammaAnalysis;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * 1.将产生式集合P转化为CFG
 *
 * @author wdl
 */
public class ProductSetToCFG {
    public static void main(String[] args) {
    }

    /**
     * CFG的数据结构定义
     */
    static class CFG {
        HashSet<Character> terminatorSet;
        HashSet<Character> nonTerminatorSet;
        char start;
        ArrayList<Product> productSet;

        public CFG(HashSet<Character> terminatorSet, HashSet<Character> nonTerminatorSet, char start, ArrayList<Product> productSet) {
            this.terminatorSet = terminatorSet;
            this.nonTerminatorSet = nonTerminatorSet;
            this.start = start;
            this.productSet = productSet;
        }
    }

    /**
     * 产生式的数据结构
     */
    static class Product {
        /**
         * 产生式左边
         */
        char left;
        /**
         * 产生式右侧式子的数组
         */
        ArrayList<String> rights;

        public Product(char left, ArrayList<String> rights) {
            this.left = left;
            this.rights = rights;
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
        sError = 0;
        if (productSet == null || productSet.size() == 0) {
            sError = P_IS_NULL;
            return null;
        }
        //初始化
        HashSet<Character> terminatorSet = new HashSet<>();
        HashSet<Character> nonTerminatorSet = new HashSet<>();
        char start = productSet.get(0).charAt(0);

        //获取非终态集
        for (String product : productSet) {
            nonTerminatorSet.add(product.charAt(0));
        }
        //产生式数组
        ArrayList<Product> newProductSet = new ArrayList<>();
        ArrayList<String> rights;

        //获取终态集
        for (String product : productSet) {
            char ch;
            //将右侧分割为数组
            String[] ps = product.substring(1).split("\\|");
            //初始化数组
            rights = new ArrayList<>();
            for (String p : ps) {
                //将式子加到right数组中
                rights.add(p);
                //从下标1开始扫描产生式右边式子
                for (int index = 0; index < p.length(); index++) {
                    //获取字符
                    ch = p.charAt(index);
                    //不是非终结符且（由于前面做过分割因此不会是|）则放入终结符中
                    if (!nonTerminatorSet.contains(ch)) {
                        terminatorSet.add(ch);
                    }
                }
            }
            //创建一个新的产生式并加入产生式数组中
            newProductSet.add(new Product(product.charAt(0), rights));
        }
        return new CFG(terminatorSet, nonTerminatorSet, start, newProductSet);
    }

    /**
     * 打印CFG
     *
     * @param cfg
     */
    public static void showCFG(CFG cfg) {
        //打印非终结符
        System.out.println("终结符T:" + cfg.terminatorSet);
        //打印终结符
        System.out.println("非终结符N:" + cfg.nonTerminatorSet);
        //打印开始字符
        System.out.println("开始字符S:" + cfg.start);
        //打印产生式集合
        System.out.println("产生式集合P:");
        for (Product product : cfg.productSet) {
            System.out.print(product.left + "-->");
            ArrayList<String> rights = product.rights;
            for (int index = 0; index < rights.size() - 1; index++) {
                System.out.print(rights.get(index) + "|");
            }
            System.out.println(rights.get(rights.size() - 1));
        }
    }
}
