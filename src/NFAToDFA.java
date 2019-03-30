import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * NFA转DFA
 * 思路：
 * 将三元组转换成二维转移矩阵
 *
 * @author wdl
 */
public class NFAToDFA {
    /**
     * 测试代码使用
     * @param args
     */
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
        changeNFAToDFA(nfa);
    }

    /**
     * NFA转DFA
     *
     * @param nfa 传入NFA
     */
    static void changeNFAToDFA(SuffixToNFA.NFA nfa) {
        List<Integer> stateList = nfa.getStateList();
        HashSet<Character> characterSet = nfa.getCharacterSet();
        char[] characterStrings = new char[characterSet.size()];
        int i = -1;
        for (char ch : characterSet) {
            characterStrings[++i] = ch;
        }
        HashSet<Integer>[][] hashSets = new HashSet[stateList.size()][characterSet.size()];

        for (SuffixToNFA.Side side : nfa.getMoveList()) {
            /*获取当前需要添加的位置下标*/
            int state = side.getPreState();
            char ch = side.getTransferCondition();
            int characterIndex = -1;
            while (true) {
                if (characterStrings[++characterIndex] == ch) {
                    break;
                }
            }
            /*
            判断当前位置是否为空，为空则创建新的HashSet加入，不为空则直接在原有基础上加入
             */
            if (hashSets[state][characterIndex]==null){
                HashSet<Integer> hashSet = new HashSet<>();
                hashSet.add(side.getNextState());
                hashSets[state][characterIndex]=hashSet;
            }else {
                hashSets[state][characterIndex].add(side.getNextState());
            }
        }

        /*
          打印转移矩阵
         */
        System.out.println("状态转移矩阵：");
        System.out.print("\t");
        for (char ch:characterSet){
            System.out.print(" "+ch+"  \t");
        }
        System.out.println("");
        int stateIndex=-1;
        for (HashSet<Integer>[] hashSet : hashSets) {
            System.out.print(stateList.get(++stateIndex) + "\t");
            for (HashSet<Integer> integers : hashSet) {
                if (integers != null) {
                    for (int states : integers) {
                        System.out.print( states + " ");
                    }
                    System.out.print("  \t");
                } else {
                    System.out.print("null\t");
                }
            }
            System.out.println("");
        }

        /*
          去除空转移
         */


    }
}
