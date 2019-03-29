/**
 * NFA转DFA
 * 思路：
 *将三元组转换成二维转移矩阵
 * @author wdl
 */
public class NFAToDFA {
    public static void main(String[] args) {
        String string = "(((a b|b )*(ab|c.d))*)*";
        StringBuilder result = new StringBuilder();
        System.out.println("正规式为：" + string);

        SuffixToNFA.NFA nfa = null;

        if (InfixToSuffix.change(string, result) == 0) {
            System.out.println("后缀表达式：" + result.toString());
            nfa = SuffixToNFA.changeSuffixToNfa(result.toString());
            System.out.println("NFA为：");
            System.out.println("状态集：");
            for (int i : nfa.getStateList()) {
                System.out.print(i + "\t");
            }
            System.out.println("\n字符集：");
            for (char ch : nfa.getCharacterSet()) {
                System.out.print(ch + "\t");
            }
            System.out.println("\n转移集（边）：");
            for (SuffixToNFA.Side side : nfa.getMoveList()) {
                System.out.print("(" + side.getPreState() + "," + side.getNextState() + "," + side.getTransferCondition() + ")");
            }
            System.out.println("\n起始状态:" + nfa.getStateList().get(nfa.getStartIndex()));
            System.out.println("结束状态:" + nfa.getStateList().get(nfa.getFinishIndex()));
        } else {
            System.out.println("输入有误");
        }
    }
}
