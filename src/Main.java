public class Main {

    public static void main(String[] args) {
        String string = "(((a b|b )*(ab|c.d))*)*";
        StringBuilder result = new StringBuilder();
        System.out.println("正规式为：" + string);

        SuffixToNFA.NFA nfa = null;

        if (InfixToSuffix.change(string, result) == 0) {
            System.out.println("后缀表达式：" + result.toString());
            nfa = SuffixToNFA.changeSuffixToNfa(result.toString());
            SuffixToNFA.printNFA(nfa);
            /* 返回一个最小DFA的二维矩阵，横坐标对应字符集的顺序，最后列代表是否为终态1代表是0代表不是，同时第一行即状态0代表入口 */
            int[][] minDFA = NFAToDFA.changeNFAToDFA(nfa, true);
        } else {
            System.out.println("输入有误");
        }
    }
}
