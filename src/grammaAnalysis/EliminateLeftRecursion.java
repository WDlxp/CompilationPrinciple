package grammaAnalysis;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * 2.消除左递归
 * 2.1消除显式左递归
 * 2.2隐式左递归转显式左递归：找到-->隐式-->显式调用2.1
 *
 * @author wdl
 */
public class EliminateLeftRecursion {
    public static void main(String[] args) {
    }

    /**
     *  标记错误
     */
    static int sError=0;
    public static final int UNABLE_TO_ELIMINATE_LEFT_RECURSION=1;
    /**
     * 消除左递归（显式）
     *
     * @param cfg 输入CFG
     * @return 返回消除左递归后新的CFG
     */
    public static ProductSetToCFG.CFG eliminateLeftRecursion(ProductSetToCFG.CFG cfg) {
        sError=0;
        //获取非终结符集合
        HashSet<Character> nonTerminatorSet = cfg.nonTerminatorSet;
        //标记已使用的字符
        NewChar.flagUsedChar(nonTerminatorSet);
        //获取产生式集合
        ArrayList<String> productSet = cfg.productSet;
        //产生式的左边
        char productStart;
        //新的产生式集合
        ArrayList<String> newProductSet=new ArrayList<>();
        //遍历产生式消除显式左递归
        for (String product : productSet) {
            //获取产生式的左边
            productStart = product.charAt(0);
            //将产生式的右边分割成数组
            String[] productSplits = product.substring(1).split("\\|");
            //标记对应下标的产生式是否左递归
            int[] flags = new int[productSplits.length];
            //存在左递归的项的个数
            int leftRecursionNumber=0;
            for (int index=0;index<productSplits.length;index++){
                if (productSplits[index].charAt(0)==productStart){
                    //标记该项存在左递归
                    flags[index]=1;
                    leftRecursionNumber++;
                }
            }
            //产生左递归
            if (leftRecursionNumber!=0){
                //如果所有项目都存在左递归，则说明该左递归无法去除
                if (leftRecursionNumber==productSplits.length){
                    sError=UNABLE_TO_ELIMINATE_LEFT_RECURSION;
                    return null;
                }
                //获取新的产生式字符
                char newProductStart=NewChar.getNewChar(nonTerminatorSet);

                StringBuilder oldProduct=new StringBuilder();
                oldProduct.append(productStart);

                StringBuilder newProduct=new StringBuilder();
                newProduct.append(newProductStart);
                for (int flagIndex=0;flagIndex<flags.length;flagIndex++){
                    //标记为1说明是存在递归的项
                    if (flags[flagIndex]==1){
                        newProduct.append(productSplits[flagIndex].substring(1)).append(newProductStart).append("|");
                    }else {
                        oldProduct.append(productSplits[flagIndex]).append(newProductStart).append("|");
                    }
                }
                //旧去除尾部的“|”
                oldProduct.setLength(oldProduct.length()-1);
                //尾部加上'ε'
                newProduct.append('ε');
                //添加到新的产生式集合中
                newProductSet.add(oldProduct.toString());
                newProductSet.add(newProduct.toString());
            }else {
                newProductSet.add(product);
            }
        }
        return new ProductSetToCFG.CFG(cfg.terminatorSet,nonTerminatorSet,cfg.start,newProductSet);
    }

    /**
     * 获取新的不重复字符的方法
     */
    private static class NewChar {
        /**
         * 用于标记字符是否已经使用过0未使用，1已经使用
         */
        private static int[] charUsedFlag = new int[26];
        /**
         * 需找新字符时使用的下标
         */
        private static int index = 0;

        /**
         * 标记已经使用过的字符
         *
         * @param nonTerminatorSet 非终态集
         */
        public static void flagUsedChar(HashSet<Character> nonTerminatorSet) {
            for (char ch : nonTerminatorSet) {
                charUsedFlag[ch - 'A'] = 1;
            }
        }

        /**
         * 获取新的未重复的字符
         *
         * @param nonTerminatorSet 非终态集
         * @return 返回新的字符，无则返回’ ‘
         */
        public static char getNewChar(HashSet<Character> nonTerminatorSet) {
            for (; index < 26; index++) {
                if (charUsedFlag[index] == 0) {
                    //使用后记得标记
                    charUsedFlag[index] = 1;
                    char newChar = (char) (index + 'A');
                    nonTerminatorSet.add(newChar);
                    return newChar;
                }
            }
            return ' ';
        }
    }
}
