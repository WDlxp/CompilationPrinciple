package grammaAnalysis;

import java.util.*;

/**
 * 下推自动机
 *
 * @author 22939
 */
public class PushDownAutomaton {
    public static void main(String[] args) {
        String input = "id+id*id";

    }

    public static Boolean predictiveAnalyzer(String input, PredictionTable.PreTableResult preTableResult) {
        //符号栈
        Stack<Character> symbolStack = new Stack<>();
        //当前输入队列
        Queue<Character> inputQueue = new LinkedList<Character>();
        ;
        //初始化输入队列
        for (int index = 0; index < input.length(); index++) {
            if (input.charAt(index) != ' ') {
                inputQueue.offer(input.charAt(index));
            }
        }
        //如果输入为空则报错
        if (inputQueue.isEmpty()) {
            System.out.println("输入式子为空！请重新输入");
            return false;
        } else {
            inputQueue.offer('#');
        }
        //初始化符号栈
        symbolStack.push('#');
        symbolStack.push(preTableResult.getRowSymbolSet().get(0));

        System.out.println("symbolStack:" + symbolStack);
        System.out.println("inputQueue:" + inputQueue);

        //下推自动机判断过程
        while (inputQueue.peek() != '#') {
            //栈顶终结符
            if (preTableResult.getColSymbolMap().containsKey(symbolStack.peek())) {
                //是否匹配
                if (symbolStack.peek().equals(inputQueue.peek())) {
                    //如果匹配则均删除
                    symbolStack.pop();
                    inputQueue.poll();
                } else {
                    //不匹配，栈顶元素不同
                    return false;
                }
            }
            //栈顶是非终结符
            if (preTableResult.getRowSymbolMap().containsKey(symbolStack.peek())) {
                //非终结符对应的行号
                int row = preTableResult.getRowSymbolMap().get(symbolStack.peek());
                //终结符对应的列号
                int col = preTableResult.getColSymbolMap().get(inputQueue.peek());
                //预测分析表对应位置内容
                String temp = preTableResult.getPreTable()[row][col];
                if (temp.isEmpty()) {
                    //预测分析表对应位置内容为空
                    return false;
                }
                //将原非终结符出符号栈
                symbolStack.pop();
                //预测分析表格内容倒序进符号栈r
                for (int num = temp.length() - 1; num > 0; num--) {
                    symbolStack.push(temp.charAt(num));
                }
            }
            if (symbolStack.peek() == 'ε') {
                //如果符号栈栈顶为空，则直接出栈
                symbolStack.pop();
            }

            if (symbolStack.peek() == '#') {
                //输入队列不为空，但符号栈只剩#号
                return false;
            }
        }
        //当输入队列匹配完了
        if (symbolStack.peek() == '#') {
            //输入队列只剩#，符号栈只剩#号
            return true;
        } else {
            while (symbolStack.size() > 1) {
                if (preTableResult.getRowSymbolSet().contains(symbolStack.peek())) {
                    symbolStack.pop();
                } else {
                    return false;
                }
            }
            return true;
        }

    }

    /**
     * 下推自动机
     *
     * @param input          需要判断的字符
     * @param preTableResult 预测分析表封装结果
     * @return 返回是否符合
     */
    public static Boolean predictiveAnalyzerSolution2(String input, PredictionTable.PreTableResult preTableResult) {
        //获取列字符集合
        HashSet<Character> colSymbolSet = preTableResult.getColSymbolSet();
        //获取行字符集合
        ArrayList<Character> rowSymbolSet = preTableResult.getRowSymbolSet();
        //列字符与列坐标的匹配
        HashMap<Character, Integer> colSymbolMap = preTableResult.getColSymbolMap();
        //行字符与行坐标的匹配
        HashMap<Character, Integer> rowSymbolMap = preTableResult.getRowSymbolMap();
        //获取预测分析表
        String[][] preTable = preTableResult.getPreTable();

        //计算中使用的符号栈
        Stack<Character> symbolStack = new Stack<>();
        //初始化符号栈
        symbolStack.push('#');
        symbolStack.push(rowSymbolSet.get(0));
        //初始化输入字符
        input = input + "#";
        //记录扫描字符串的下标
        int inputIndex = 0;

        //行列坐标
        int rowIndex;
        int colIndex;

        //获取的中间结果
        String tempResult;
        //获取中间结果的额长度
        int tempLength;
        //符号栈不为空的情况下一直出栈
        while (!symbolStack.isEmpty()) {
            //从符号栈中pop出一个符号
            char ch = symbolStack.pop();
            //如果列坐标中包含则说明是终结符
            if (colSymbolSet.contains(ch)) {
                if (ch == '#') {
                    //如果在输入字符未到#而字符栈到#则说明不匹配否则匹配成功
                    return input.charAt(inputIndex) == '#';
                }
                //不为#则查看是否匹配
                else {
                    //如果匹配
                    if (ch == input.charAt(inputIndex)) {
                        //字符往前移动一格，字符栈字符已经在前面出栈
                        inputIndex++;
                    }
                }
            }
            //非终结符则寻找
            else {
                //判断当前扫描字符在不在列坐标字符中
                if (colSymbolSet.contains(input.charAt(inputIndex))) {
                    //获取行坐标
                    rowIndex = rowSymbolMap.get(ch);
                    //获取列坐标
                    colIndex = colSymbolMap.get(input.charAt(inputIndex));
                    //查表获取结果
                    tempResult = preTable[rowIndex][colIndex];
                    //如果结果为空则说明不匹配
                    if (tempResult == null) {
                        return false;
                    }
                    //如果结果不为空且不为ε（为ε不需要处理，直接下一轮因此）处理
                    else if (!tempResult.equals("ε")) {
                        //结果倒序压入符号栈中
                        tempLength = tempResult.length();
                        for (int i = tempLength - 1; i >= 0; i--) {
                            symbolStack.push(tempResult.charAt(i));
                        }
                    }
                }
                //出现列字符不符的字符（终结符）报错
                else {
                    return false;
                }
            }
        }
        //符号栈已经空，但是并未匹配到字符串的#则返回不匹配
        return false;
    }
}

