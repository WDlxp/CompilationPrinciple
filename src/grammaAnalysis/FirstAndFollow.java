package grammaAnalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


/**
 * 4.求First集和Follow集
 *
 * @author wdl
 */
public class FirstAndFollow {
    public static void main(String[] args) {
    }

    /**
     * 标记错误
     */
    static int sError = 0;
    public static final int INTERSECTION_OF_FIRST_AND_FOLLOW_IS_NOT_NULL = 1;

    /**
     * 求First集和Follow集
     *
     * @param cfg 输入CFG
     * @return 返回含有First集和Follow集的产生式
     */
    public static ProductSetToCFG.CFGResult getFirstAndFollow(ProductSetToCFG.CFG cfg) {
        sError = 0;
        //获取非终结符集合
        HashSet<Character> nonTerminatorSet = cfg.nonTerminatorSet;
        //获取终结符集合
        HashSet<Character> terminatorSet = cfg.terminatorSet;
        //获取产生式集合
        ArrayList<ProductSetToCFG.Product> productSet = cfg.productSet;
        //做一个产生式左左侧和下标的映射
        HashMap<Character, Integer> symbolOfIndex = new HashMap<>(productSet.size());
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
        //求Follow集
        //规则1：将#加入到开始符号的Follow集中
        product = productSet.get(0);
        product.follow = new HashSet<>();
        product.follow.add('#');
        //遍历执行
        // 规则二：若有S->αBβ则将First(B)去除ε加入到Follow(S)中；
        for (int index = 0; index < productSet.size(); index++) {
            //获取产生式
            product = productSet.get(index);
            //获取产生式右侧
            ArrayList<String> rights = product.rights;
            for (String right : rights) {
                //执行遍历执行获取Follow集的规则二
                getFollowRuleTwo(productSet, terminatorSet, nonTerminatorSet, symbolOfIndex, right);
            }
        }

        int preFollowSize;
        int nowFollowSize = 0;
        do {
            //将前一次总的Follow集的大小赋值给
            preFollowSize = nowFollowSize;
            //初始化这一次总的Follow集的大小
            nowFollowSize = 0;
            // 规则三如果S->αB那么将Follow(S)加入到Follow(B),且如果First(B)中含有ε，则将Follow(S)加入到B之前一位的非终结符（如果是终结符无Follow集不需要加）
            for (int index = 0; index < productSet.size(); index++) {
                //获取产生式
                product = productSet.get(index);
                //获取产生式右侧
                ArrayList<String> rights = product.rights;
                for (String right : rights) {
                    //遍历执行规则三
                    getFollowRuleThree(productSet, nonTerminatorSet, symbolOfIndex, right, product);
                }
            }
            //计算执行一次规则三后当前总的Follow集的大小
            for (ProductSetToCFG.Product productItem : productSet) {
                //如果First集中含有空
                if (productItem.first.contains('ε')) {
                    //求First集和Follow集的交集
                    HashSet<Character> intersectionSet = new HashSet<>(productItem.first);
                    intersectionSet.retainAll(productItem.follow);
                    //如果交集不为空则返回错误
                    if (intersectionSet.size() != 0) {
                        sError = INTERSECTION_OF_FIRST_AND_FOLLOW_IS_NOT_NULL;
                        return null;
                    }
                }
                if (productItem.follow != null) {
                    nowFollowSize += productItem.follow.size();
                }
            }
            //当执行一次过后Follow集增大则需要再次执行
        } while (nowFollowSize > preFollowSize);
        return new ProductSetToCFG.CFGResult(cfg,sError);
    }

    /**
     * 执行获取Follow及的规则二：若有S->αBβ则将First(B)去除ε加入到Follow(S)中；
     *
     * @param productSet       产生式集合
     * @param terminatorSet    终结符
     * @param nonTerminatorSet 非终极符
     * @param symbolOfIndex    产生式左侧与对于产生式集合下标的映射
     * @param right            产生式右侧的一项
     */
    private static void getFollowRuleTwo(ArrayList<ProductSetToCFG.Product> productSet, HashSet<Character> terminatorSet, HashSet<Character> nonTerminatorSet, HashMap<Character, Integer> symbolOfIndex, String right) {
        //当前字符
        char currentSymbol;
        //下一个字符
        char nextSymbol;
        //右侧单个式子的长度
        int rightLength;
        //当前字符对应的产生式对应下标
        int currentProductIndex;
        //当前字符对应的产生式
        ProductSetToCFG.Product currentProduct;
        //下一个字符对应的产生式对应下标
        int nextProductIndex;
        //下一个字符对应的产生式
        ProductSetToCFG.Product nextProduct;
        //下一个字符对应产生式的First集
        HashSet<Character> nextProductFirst;
        //获取长度
        rightLength = right.length();
        //遍历执行规则二
        for (int rightIndex = 0; rightIndex < rightLength - 1; rightIndex++) {
            //获取当前所在字符
            currentSymbol = right.charAt(rightIndex);
            //如果当前字符为非终结符
            if (nonTerminatorSet.contains(currentSymbol)) {
                //获取下一个字符
                nextSymbol = right.charAt(rightIndex + 1);
                //获取当前字符对应的产生式
                currentProductIndex = symbolOfIndex.get(currentSymbol);
                currentProduct = productSet.get(currentProductIndex);
                //如果当前产生式的Follow集为空则初始化
                if (currentProduct.follow == null) {
                    currentProduct.follow = new HashSet<>();
                }
                //如果当前字符的下一个字符为终结符
                if (terminatorSet.contains(nextSymbol)) {
                    currentProduct.follow.add(nextSymbol);
                } else {
                    //获取下一个非终结符的First集
                    nextProductIndex = symbolOfIndex.get(nextSymbol);
                    nextProduct = productSet.get(nextProductIndex);
                    //将下一个非终结符的First集去掉ε加给当前字符的Follow集
                    nextProductFirst = new HashSet<>(nextProduct.first);
                    nextProductFirst.remove('ε');
                    currentProduct.follow.addAll(nextProductFirst);
                }
            }
        }
    }

    /**
     * 执行规则三如果S->αB那么将Follow(S)加入到Follow(B),且如果First(B)中含有ε，则将Follow(S)加入到B之前一位的非终结符（如果是终结符无Follow集不需要加）
     *
     * @param productSet       产生式集合
     * @param nonTerminatorSet 非终极符
     * @param symbolOfIndex    产生式左侧与对于产生式集合下标的映射
     * @param right            产生式右侧的一项
     * @param product          当前正在处理的产生式
     */
    private static void getFollowRuleThree(ArrayList<ProductSetToCFG.Product> productSet, HashSet<Character> nonTerminatorSet, HashMap<Character, Integer> symbolOfIndex, String right, ProductSetToCFG.Product product) {
        //当前字符
        char currentSymbol;
        //右侧单个式子的长度
        int rightLength;
        //当前字符对应的产生式对应下标
        int currentProductIndex;
        //当前字符对应的产生式
        ProductSetToCFG.Product currentProduct;
        //获取长度
        rightLength = right.length();
        //获取最后一个字符
        currentSymbol = right.charAt(rightLength - 1);
        //如果最后一个字符是非终结符
        if (nonTerminatorSet.contains(currentSymbol)) {
            //获取当前字符对应的产生式
            currentProductIndex = symbolOfIndex.get(currentSymbol);
            currentProduct = productSet.get(currentProductIndex);
            //如果产生式为空则初始化
            if (currentProduct.follow == null) {
                currentProduct.follow = new HashSet<>();
            }
            //规则三如果S->αB那么将Follow(S)加入到Follow(B)(在Follow(S)不为空的时候操作才有意义)
            if (product.follow != null) {
                currentProduct.follow.addAll(product.follow);
                //如果First(B)中含有ε，则将Follow(S)加入到B之前一位的非终结符（如果是终结符无Follow集不需要加）
                if (currentProduct.first.contains('ε')) {
                    if (rightLength > 1) {
                        getFollowRuleThree(productSet, nonTerminatorSet, symbolOfIndex, right.substring(0, rightLength - 1), product);
                    }
                }
            }
        }
    }

    /**
     * 获取产生式总的First集
     *
     * @param productSet    产生式集合
     * @param terminatorSet 终态集
     * @param symbolOfIndex 产生式左侧与对于产生式集合下标的映射
     * @param left          需要求First集的产生式左侧
     * @return 放回First集
     */
    private static HashSet<Character> getTotalFirst(ArrayList<ProductSetToCFG.Product> productSet, HashSet<Character> terminatorSet, HashMap<Character, Integer> symbolOfIndex, char left) {
        //初始化一个First集
        HashSet<Character> first = new HashSet<>();
        //获取当前求First集的产生式的下标
        int index = symbolOfIndex.get(left);
        //获取这个产生式的右侧
        ArrayList<String> rights = productSet.get(index).rights;
        //遍历产生式右侧求每一项的First集，整合起来即为整个产生式的First集
        HashSet<Character> rightItemFirst;
        for (String right : rights) {
            rightItemFirst = getFirst(productSet, terminatorSet, symbolOfIndex, left, right);
            if (productSet.get(index).rightFirsts == null) {
                productSet.get(index).rightFirsts = new ArrayList<>();
            }
            productSet.get(index).rightFirsts.add(rightItemFirst);
            first.addAll(rightItemFirst);
        }
        return first;
    }

    /**
     * 求产生式右侧一项的First集
     *
     * @param productSet    产生式集合
     * @param terminatorSet 终态集
     * @param symbolOfIndex 产生式左侧与对于产生式集合下标的映射
     * @param left          需要求First集的产生式左侧
     * @param right         产生式右侧的某一项
     * @return 返回一项的First集
     */
    private static HashSet<Character> getFirst(ArrayList<ProductSetToCFG.Product> productSet, HashSet<Character> terminatorSet, HashMap<Character, Integer> symbolOfIndex, char left, String right) {
        //初始化所求First集
        HashSet<Character> first = new HashSet<>();
        //获取第一个字符
        char firstChar = right.charAt(0);
        //初始化一个过渡的First集
        HashSet<Character> tempFirst = new HashSet<>();
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
            if (right.length() == 1) {
                //则直接将前面求得的结果作为该项最终结果
                first.addAll(tempFirst);
            }
            //不仅一个字符
            else {
                //判断该First集中是否含有ε，如果有
                if (tempFirst.contains('ε')) {
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
