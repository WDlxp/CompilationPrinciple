import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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
     *
     * @param args
     */
    public static void main(String[] args) {
        String string = "(((a b|b )*(ab|c.d))*)*";
//        string = "(ab|b)*ab";
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
        NFAToDFA.changeNFAToDFA(nfa);
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

        /* 用于记录是否为终态 */
        boolean[] isFinishState = new boolean[stateList.size()];

        /* 终态初始化 */
        isFinishState[nfa.getFinishIndex()] = true;

        /* 将set集合转化为String数组 */
        int i = -1;
        for (char ch : characterSet) {
            characterStrings[++i] = ch;
        }
        /* 使用HashSet作为转移矩阵 */
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
            if (hashSets[state][characterIndex] == null) {
                HashSet<Integer> hashSet = new HashSet<>();
                hashSet.add(side.getNextState());
                hashSets[state][characterIndex] = hashSet;
            } else {
                hashSets[state][characterIndex].add(side.getNextState());
            }
        }


        /*打印转移矩阵*/
        printMoveSet(characterSet, stateList, hashSets, isFinishState);

        /*去除空转移*/

        /* 找出ε所在的列下标 */
        int tempIndex = -1;
        for (int charIndex = 0; charIndex < characterStrings.length; charIndex++) {
            if (characterStrings[charIndex] == 'ε') {
                tempIndex = charIndex;
                break;
            }
        }
        /* 说明在字符中无空转移了 */
        if (tempIndex == -1) {
            return;
        }
        int tempCount = 0;
        /*
        第一种思路每次找到空转移为空的状态，遍历查找含该状态空转移的状态的消除
        while (tempCount < stateList.size()) {
            tempCount = 0;
            for (int index = 0; index < stateList.size(); index++) {
                HashSet<Integer> tempToStateSet = hashSets[index][tempIndex];
                if (tempToStateSet == null || tempToStateSet.isEmpty()) {
                    tempCount++;
                    for (int i1 = 0; i1 < stateList.size(); i1++) {
                        if (hashSets[i1][tempIndex] != null && hashSets[i1][tempIndex].contains(index)) {
                            mergeTwoRows(characterStrings,hashSets,isFinishState,i1,index);
                            hashSets[i1][tempIndex].remove(index);
                        }
                    }
                }
            }
        }*/

        /* 第二种思路，每次找控转移非空的状态，根据控转移下的状态查找对应状态是否可合并过来*/
        while (tempCount < stateList.size()) {
            tempCount = 0;
            for (int index = 0; index < stateList.size(); index++) {
                HashSet<Integer> tempToStateSet = hashSets[index][tempIndex];
                if (tempToStateSet == null || tempToStateSet.isEmpty()) {
                    tempCount++;
                } else {
                    Iterator<Integer> it = tempToStateSet.iterator();
                    while (it.hasNext()) {
                        int state = it.next();
                        if (hashSets[state][tempIndex] == null || hashSets[state][tempIndex].isEmpty()) {
                            mergeTwoRows(characterStrings, hashSets, isFinishState, index, state);
                            it.remove();
                        }
                    }
                }
            }
        }
        /*打印转移矩阵*/
        printMoveSet(characterSet, stateList, hashSets, isFinishState);
    }

    /**
     * 打印状态转移矩阵
     *
     * @param characterSet  字符集
     * @param stateList     状态集
     * @param hashSets      转移矩阵
     * @param isFinishState 是否为终态的标记数组
     */
    private static void printMoveSet(HashSet<Character> characterSet, List<Integer> stateList, HashSet<Integer>[][] hashSets, boolean[] isFinishState) {
        System.out.println("状态转移矩阵：");
        System.out.print("\t");
        for (char ch : characterSet) {
            System.out.print(" " + ch + "  \t");
        }
        System.out.println("终态");
        int stateIndex = -1;
        for (HashSet<Integer>[] hashSet : hashSets) {
            System.out.print(stateList.get(++stateIndex) + "\t");
            for (HashSet<Integer> integers : hashSet) {
                if (integers != null) {
                    if (integers.isEmpty()) {
                        System.out.print("null\t");
                    } else {
                        for (int states : integers) {
                            System.out.print(states + " ");
                        }
                        System.out.print("  \t");
                    }
                } else {
                    System.out.print("null\t");
                }
            }
            System.out.println(isFinishState[stateIndex]);
        }
    }

    /**
     * 将第二行合并到第一行
     * @param characterStrings 字符集
     * @param hashSets         转移矩阵
     * @param isFinishState    是否终态标志
     * @param firstState       第一个状态
     * @param secondState      第二个状态
     */
    private static void mergeTwoRows(char[] characterStrings, HashSet<Integer>[][] hashSets, boolean[] isFinishState, int firstState, int secondState) {
        for (int k = 0; k < characterStrings.length; k++) {
            if (hashSets[secondState][k] != null && !hashSets[secondState][k].isEmpty()) {
                if (hashSets[firstState][k] == null) {
                    hashSets[firstState][k] = new HashSet<>();
                }
                hashSets[firstState][k].addAll(hashSets[secondState][k]);
            }
        }
        isFinishState[firstState] = isFinishState[firstState] || isFinishState[secondState];
    }
}
