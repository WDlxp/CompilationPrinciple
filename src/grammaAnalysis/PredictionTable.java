package grammaAnalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static grammaAnalysis.FirstAndFollow.INTERSECTION_OF_FIRST_AND_FOLLOW_IS_NOT_NULL;

/**
 * 5.通过First集与Follow集生成对应的预测分析表
 *
 * @author wdl
 */
public class PredictionTable {
    public static void main(String[] args) {
        //测试用例1
        String product1 = "S(L)|aA";
        String product2 = "AS|ε";
        String product3 = "LSB";
        String product4 = "B,SB|ε";
        ArrayList<String> productSet = new ArrayList<>();
        productSet.add(product1);
        productSet.add(product2);
        productSet.add(product3);
        productSet.add(product4);
        ProductSetToCFG.CFG cfg = ProductSetToCFG.pToCFG(productSet);
//        cfg=pToCFG(null);
        if (ProductSetToCFG.sError == 0) {
            ProductSetToCFG.showCFG(cfg);
            ProductSetToCFG.CFG cfg1 = EliminateLeftRecursion.eliminateLeftRecursion(cfg);
            if (EliminateLeftRecursion.sError == 0) {
                ProductSetToCFG.showCFG(cfg1);
                ProductSetToCFG.CFG cfg2 = FirstAndFollow.getFirstAndFollow(cfg1);
                if (FirstAndFollow.sError == 0) {
                    ProductSetToCFG.showCFG(cfg2);
                    String[][] preTable=PredictionTable.predictionTable(cfg2);
                    if (PredictionTable.sError==0){
                        System.out.println();
                        for (String[] strings:preTable){
                            for (String string:strings){
                                System.out.print(string+" ");
                            }
                            System.out.println();
                        }
                    }else if (PredictionTable.sError==PredictionTable.EXIST_IMPLICIT_LEFT_FACTOR){
                        System.out.println("存在隐式左因子，不符合LL(1)文法");
                    }
                } else if (FirstAndFollow.sError == INTERSECTION_OF_FIRST_AND_FOLLOW_IS_NOT_NULL) {
                    System.out.println("First集含空时与Follow集存在存在交集，不符合LL(1)文法");
                }

            } else if (EliminateLeftRecursion.sError == EliminateLeftRecursion.UNABLE_TO_ELIMINATE_LEFT_RECURSION) {
                System.out.println("存在无法消除的左递归，不符合LL(1)文法");
            } else if (EliminateLeftRecursion.sError == EliminateLeftRecursion.SYMBOL_OVERFLOW) {
                System.out.println("超出可使用的字符集，无法处理");
            }
        } else if (ProductSetToCFG.sError == ProductSetToCFG.P_IS_NULL) {
            System.out.println("产生式集合为空，请检查输入的产生式集合");
        }
    }
    /**
     * 标记错误
     */
    static int sError = 0;
    public static final int EXIST_IMPLICIT_LEFT_FACTOR = 1;

    /**
     * 生成预测分析表
     *
     * @param cfg 传入CFG
     * @return String[][]预测分析表
     */
    public static String[][] predictionTable(ProductSetToCFG.CFG cfg) {

        //获取终结符
        HashSet<Character> terminatorSet = cfg.terminatorSet;
        //获取产生式集合
        ArrayList<ProductSetToCFG.Product> productSet = cfg.productSet;
        //终结符去ε加上#与列坐标的Map映射
        terminatorSet.remove('ε');
        terminatorSet.add('#');
        int columnLength = terminatorSet.size();
        HashMap<Character, Integer> terminatorSymbolMap = new HashMap<>(columnLength);
        int columnIndex = 0;
        for (Character ch : terminatorSet) {
            terminatorSymbolMap.put(ch, columnIndex++);
        }

        //非终结符与行坐标的Map映射
        int rowLength = productSet.size();
        HashMap<Character, Integer> nonTerminatorSymbolMap = new HashMap<>(rowLength);
        ProductSetToCFG.Product product;
        for (int rowIndex = 0; rowIndex < rowLength; rowIndex++) {
            product = productSet.get(rowIndex);
            nonTerminatorSymbolMap.put(product.left, rowIndex);
        }

        //声明预测分析表
        String[][] preTable = new String[rowLength][columnLength];
        //填写产生式时的行坐标和列坐标
        int rowIndex;
        int colIndex;

        //产生式右侧的每一项
        ArrayList<String> rights;
        //产生式右侧每一项对应的First集
        ArrayList<HashSet<Character>> rightFirsts;

        HashSet<Character> leftFirst;
        HashSet<Character> follow;
        HashSet<Character> rightFirst;
        /*
        思路：
        遍历每一个产生式
        然后遍历每一个产生式的右侧，取出每一项右侧的First集进行遍历，填写需要填写该右侧项的预测分析表空格
        如果填写过程中遇到一个空格不为空（即说明前面填写过，存在重复，即存在隐式左递归）
        填写完成后检查是产生式总体的First集中是否含ε,如果含ε则访问其Follow集在预测分析表中对应位置内填入ε
         */
        for (ProductSetToCFG.Product productItem : productSet) {
            //获取当前填写的行坐标
            rowIndex = nonTerminatorSymbolMap.get(productItem.left);
            //获取当前产生式右侧每一项
            rights = productItem.rights;
            //获取当前产生式右侧每个集合的First集
            rightFirsts = productItem.rightFirsts;
            //遍历产生式右侧每一项
            for (int index = 0; index < rightFirsts.size(); index++) {
                rightFirst = rightFirsts.get(index);
                //遍历每一项first集一次性填写需要填写该项的地方
                for (char ch : rightFirst) {
                    if (ch != 'ε') {
                        //获取到对应的列坐标
                        colIndex = terminatorSymbolMap.get(ch);
                        //如果当前表格不为空则说明前面已经有一个产生式到达这里，再次计算出一个相同的值说明存在隐式左递归
                        if (preTable[rowIndex][colIndex] != null) {
                            sError = EXIST_IMPLICIT_LEFT_FACTOR;
                            return null;
                        }
                        preTable[rowIndex][colIndex] = rights.get(index);
                    }
                }
            }
            leftFirst=productItem.first;
            if (leftFirst.contains('ε')) {
                follow=productItem.follow;
                for (char ch:follow){
                    //获取到对应的列坐标
                    colIndex = terminatorSymbolMap.get(ch);
                    //如果当前表格不为空则说明前面已经有一个产生式到达这里，再次计算出一个相同的值说明存在隐式左递归
                    if (preTable[rowIndex][colIndex] != null) {
                        sError = EXIST_IMPLICIT_LEFT_FACTOR;
                        return null;
                    }
                    preTable[rowIndex][colIndex] = "ε";
                }
            }

        }
        return preTable;
    }
}
