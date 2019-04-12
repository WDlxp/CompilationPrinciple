package lexicalAnalysis;
/**
 * 中缀表达式转后缀表达式的工具类
 * 思路：
 * 1.首先将输入的中缀表达式尾部加上#
 * 2.在操作符栈中压人#
 * 3.每次取出当前非空字符和下一个非空格字符
 * 4.两个字符判断字符冲突如两个连续的||等冲突直接返回错误
 * 5.根据当前字符做相应操作
 * 6.根据前后两个字符判断是否需要加.
 * 7.完成运算返回输入是否错误，后缀表达式即suffixStr
 * <p>
 * . 代表连接
 * | 代表或运算
 * * 代表闭包
 *
 * @author wdl
 */

import java.util.Stack;

public class InfixToSuffix {
    /**
     * 代码测试使用
     *
     * @param args
     */
    public static void main(String[] args) {
        String string = "(a b|e.d)|abcd*";
        string = "int  ab";
        StringBuilder result = new StringBuilder();
        if (change(string, result) == 0) {
            System.out.println(result);
        } else {
            System.out.println("输入有误");
        }
    }

    /**
     * 将中缀表达式转换为后缀表达式
     *
     * @param infixStr  传入的中缀表达式
     * @param suffixStr 传出的后缀表达式
     * @return 返回0代表成功-1代表输入有误
     */
    static int change(String infixStr, StringBuilder suffixStr) {
        /* 用于存放操作符的栈 */
        Stack<Character> stack = new Stack<>();
        stack.push('#');
        /*输入的正规式尾部加入#辅助判断算式是否已经完毕*/
        infixStr = infixStr + '#';
        int i = 0;
        char op = infixStr.charAt(i);
        while (op != '#') {
            /*
            跳过空格
             */
            if (op == ' ') {
                op = infixStr.charAt(++i);
                continue;
            }
            /*
            nowFlag记录当前字符的代号
            nextFlag记录下一个字符的代号
             */
            int nowFlag = whichOperator(op);

            /*下一个非空字符下标*/
            int nextIndex = i + 1;
            while (infixStr.charAt(nextIndex) == ' ') {
                nextIndex++;
            }
            int nextFlag = whichOperator(infixStr.charAt(nextIndex));
            /*
             两个操作符相连报错情况三种
             1.|后跟| . * ) #
             2..后跟| . * ) #
             3.(后跟| . *
            */
            boolean bool = ((nowFlag == 2 || nowFlag == 3) && (nextFlag == 1 || nextFlag == 2 || nextFlag == 3 || nextFlag == 4 || nextFlag == 6)) || (nowFlag == 5 && (nextFlag == 2 || nextFlag == 3 || nextFlag == 4));
            if (bool) {
                return -1;
            }

            /*
            根据当前字符做相应操作
             */
            if (nowFlag == -1) {
                /*操作数直接放入结果*/
                suffixStr.append(op);
            } else if (nowFlag == 5) {
                /*如果式左括号则直接压栈*/
                stack.push(op);
            } else if (nowFlag == 6) {
                /*如果是右括号则将左括号之上的符号出栈，如果到底也未发现左括号就报错退出*/
                while ((stack.peek() != '#') && (stack.peek() != '(')) {
                    suffixStr.append(stack.pop());
                }
                if (stack.peek() == '#') {
                    return -1;
                } else if (stack.peek() == '(') {
                    stack.pop();
                }
            } else {
                while (nowFlag <= whichOperator(stack.peek()) && stack.peek() != '(') {
                    /*如果当前操作符的优先级小于栈顶的操作符且栈顶的操作符不是是左括号则将栈顶元素出栈*/
                    suffixStr.append(stack.pop());
                }
                stack.push(op);
            }

            /*
            需要增加.的三种情况
            1.操作数后面是操作数或（
            2.）后面接着(或者操作数
            3.*后面跟着(或者操作数
             */
            boolean addSituation = (nowFlag == -1) && (nextFlag == -1 || nextFlag == 5) || (nowFlag == 6) && (nextFlag == -1 || nextFlag == 5) || (nowFlag == 4) && (nextFlag == -1 || nextFlag == 5);
            if (addSituation) {
                /*连续两个操作数*/
                op = '.';
            } else {
                /*无需增加操作.则将下一个字符串赋值给op,下标给i*/
                i = nextIndex;
                op = infixStr.charAt(i);
            }
        }
        /*
        如果最后非#则说明有运算符滞留在栈中
         */
        while (stack.peek() != '#') {
            suffixStr.append(stack.pop());
        }
        if (suffixStr.length() == 0) {
            return -1;
        }
        return 0;
    }

    /**
     * @return 判断是哪个操作符-1代表不是操作符即为操作数
     */
    private static int whichOperator(char operator) {
        switch (operator) {
            case '#':
                return 1;
            case '|':
                return 2;
            case '.':
                return 3;
            case '*':
                return 4;
            case '(':
                return 5;
            case ')':
                return 6;
            default:
                return -1;
        }
    }
}
