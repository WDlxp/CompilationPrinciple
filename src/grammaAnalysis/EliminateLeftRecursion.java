package grammaAnalysis;

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
     * 消除左递归（显式）
     * @param cfg 输入CFG
     * @return 返回消除左递归后新的CFG
     */
    public static ProductSetToCFG.CFG eliminateLeftRecursion(ProductSetToCFG.CFG cfg){
        //获取非终结符集合
        HashSet<Character> nonTerminatorSet=cfg.nonTerminatorSet;
        NewChar.flagUsedChar(nonTerminatorSet);
        System.out.println(NewChar.getNewChar(nonTerminatorSet));
        System.out.println(nonTerminatorSet);
        System.out.println(NewChar.getNewChar(nonTerminatorSet));
        System.out.println(nonTerminatorSet);
        return null;
    }

    /**
     * 获取新的不重复字符的方法
     */
    private static class NewChar{
        /**
         *  用于标记字符是否已经使用过0未使用，1已经使用
         */
        private static int[] charUsedFlag = new int[26];
        /**
         * 需找新字符时使用的下标
         */
        private static int index=0;
        /**
         * 标记已经使用过的字符
         * @param nonTerminatorSet 非终态集
         */
        public static void flagUsedChar(HashSet<Character> nonTerminatorSet){
            for (char ch : nonTerminatorSet) {
                charUsedFlag[ch - 'A'] = 1;
            }
        }

        /**
         * 获取新的未重复的字符
         * @param nonTerminatorSet 非终态集
         * @return 返回新的字符，无则返回’ ‘
         */
        public static char getNewChar(HashSet<Character> nonTerminatorSet){
            for (; index < 26; index++) {
                if (charUsedFlag[index] == 0) {
                    //使用后记得标记
                    charUsedFlag[index]=1;
                    char newChar=(char) (index+'A');
                    nonTerminatorSet.add(newChar);
                    return newChar;
                }
            }
            return ' ';
        }
    }
}
