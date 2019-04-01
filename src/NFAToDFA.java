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
        StringBuilder result = new StringBuilder();
        System.out.println("正规式为：" + string);

        SuffixToNFA.NFA nfa = null;

        if (InfixToSuffix.change(string, result) == 0) {
            System.out.println("后缀表达式：" + result.toString());
            nfa = SuffixToNFA.changeSuffixToNfa(result.toString());
            SuffixToNFA.printNFA(nfa);
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
    private static void changeNFAToDFA(SuffixToNFA.NFA nfa) {
        List<Integer> stateList = nfa.getStateList();
        HashSet<Character> characterSet = nfa.getCharacterSet();
        char[] characters = new char[characterSet.size()];
        /* 用于记录是否为终态 */
        boolean[] isFinishState = new boolean[stateList.size()];
        /* 终态初始化 */
        isFinishState[nfa.getFinishIndex()] = true;
        /* 将set集合转化为String数组 */
        int i = -1;
        for (char ch : characterSet) {
            characters[++i] = ch;
        }
        /*1.使用HashSet作为转移矩阵 */
        HashSet<Integer>[][] hashSets = new HashSet[stateList.size()][characterSet.size()];
        stateTransitionMatrix(nfa, characters, hashSets);

        /*打印转移矩阵*/
        printMoveSet(characters, stateList, hashSets, stateList.size(), characterSet.size(), isFinishState);

        /*2.去除空转移*/

        /* 找出ε所在的列下标 */
        int tempIndex = -1;
        for (int charIndex = 0; charIndex < characters.length; charIndex++) {
            if (characters[charIndex] == 'ε') {
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

        /* 第二种思路，每次找空转移非空的状态，根据空转移下的状态查找对应状态是否可合并过来*/
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
                            mergeTwoRows(characters, hashSets, isFinishState, index, state);
                            it.remove();
                        }
                    }
                }
            }
        }
        /*打印转移矩阵*/
        printMoveSet(characters, stateList, hashSets, stateList.size(), characterSet.size(), isFinishState);

        /*3.整理转移矩阵形成DFA*/
        HashSet<Integer>[][] hashSetsDFA = new HashSet[stateList.size() * 2][characterSet.size() + 1];
        /* 用于记录新的转移矩阵的状态是否为终态 */
        boolean[] isFinishStateDFA = new boolean[stateList.size() * 2];
        int newStateIndexCount = 0;
        /* 获取开始状态 */
        int startState = nfa.getStartIndex();
        /* 用来状态放置的列下标 */
        int stateColumnIndex = characterSet.size();
        /* 初始化第一行 */
        hashSetsDFA[0][stateColumnIndex] = new HashSet();
        hashSetsDFA[0][stateColumnIndex].add(startState);
        for (int k = 0; k < characters.length; k++) {
            if (hashSets[startState][k] != null && !hashSets[startState][k].isEmpty()) {
                hashSetsDFA[0][k] = new HashSet<>();
                hashSetsDFA[0][k].addAll(hashSets[startState][k]);
            }
        }
        isFinishStateDFA[0] = isFinishState[startState];
        /* 在新的转移矩阵中整理DFA */
        for (int i1 = 0; i1 <= newStateIndexCount; i1++) {
            for (int j = 0; j < characters.length; j++) {
                if (hashSetsDFA[i1][j] != null && !hashSetsDFA[i1][j].isEmpty()) {
                    /* 判断状态是否已经存在 */
                    boolean isExits = false;
                    for (int l = 0; l <= newStateIndexCount; l++) {
                        /* 说明状态已经存在 */
                        if (hashSetsDFA[l][characters.length].equals(hashSetsDFA[i1][j])) {
                            isExits = true;
                            break;
                        }
                    }
                    /*状态不存在时创建新的状态，增加新的状态数newStateIndexCount++*/
                    if (!isExits) {
                        /* 新增一个状态 */
                        newStateIndexCount++;
                        hashSetsDFA[newStateIndexCount][stateColumnIndex] = new HashSet<>();
                        hashSetsDFA[newStateIndexCount][stateColumnIndex].addAll(hashSetsDFA[i1][j]);
                        /* 将状态从原来的矩阵中复制过来 */
                        for (int state : hashSetsDFA[i1][j]) {
                            for (int k = 0; k < characters.length; k++) {
                                if (hashSets[state][k] != null && !hashSets[state][k].isEmpty()) {
                                    if (hashSetsDFA[newStateIndexCount][k] == null) {
                                        hashSetsDFA[newStateIndexCount][k] = new HashSet<>();
                                    }
                                    hashSetsDFA[newStateIndexCount][k].addAll(hashSets[state][k]);
                                }
                            }
                            isFinishStateDFA[newStateIndexCount] = isFinishStateDFA[newStateIndexCount] || isFinishState[state];
                        }
                    }
                }
            }
        }
        /*状态重命名*/
        for (int j = 0; j <= newStateIndexCount; j++) {
            for (int col = 0; col <= newStateIndexCount; col++) {
                HashSet[] hashSets1 = hashSetsDFA[col];
                for (int row = 0; row <= stateColumnIndex - 2; row++) {
                    if (hashSets1[row] != null && hashSets1[row].size() == hashSetsDFA[j][stateColumnIndex].size() && hashSets1[row].equals(hashSetsDFA[j][stateColumnIndex])) {
                        hashSets1[row].clear();
                        hashSets1[row].add(j);
                    }
                }
            }
        }

        /* 打印DFA */
        printMoveSet(characters, stateList, hashSetsDFA, newStateIndexCount + 1, characterSet.size(), isFinishStateDFA);

        /*4.DFA最小化*/
        HashSet<Integer>[] hashSetsMinDFA = new HashSet[newStateIndexCount+1];
        /* 初始化将原有状态集分为终态与非终态两个集合 */
        hashSetsMinDFA[0] = new HashSet<Integer>();
        hashSetsMinDFA[1] = new HashSet<Integer>();
        int setCount = 2;
        /* 用来记录对应状态转移的字符串 */
        String[] stateMoveStrings = new String[newStateIndexCount + 1];
        for (int row = 0; row <= newStateIndexCount; row++) {
            if (isFinishStateDFA[row]) {
                hashSetsMinDFA[0].add(row);
            } else {
                hashSetsMinDFA[1].add(row);
            }
        }
        int oldSetCount =0;
        /* 当不再有新的集合出现时停止 */
        while (setCount > oldSetCount) {
            /* 将当前集合个数赋值给oldSetCount */
            oldSetCount=setCount;
            /* 遍历当前集合需找是否需要分裂 */
            for (int setIndex = 0; setIndex < setCount; setIndex++) {
                int count = hashSetsMinDFA[setIndex].size();
                /* 当前集合的长度>1才需要进行判断是否分裂 */
                if (count > 1) {
                    /* 记录第一个状态和获取每个状态的转移字符串 */
                    int firstState = -1;
                    for (int state : hashSetsMinDFA[setIndex]) {
                        if (firstState == -1) {
                            firstState = state;
                        }
                        StringBuilder moveString = new StringBuilder();
                        for (int k = 0; k < characters.length; k++) {
                            moveString.append(whichSetIndex(hashSetsMinDFA, setCount, hashSetsDFA[state][k]));
                        }
                        stateMoveStrings[state] = moveString.toString();
                    }
                    /* 遍历以第一个状态为该集合的标准将与第一个状态转移不符的重新建立一个集合 */
                    HashSet newSet = new HashSet();
                    Iterator<Integer> it = hashSetsMinDFA[setIndex].iterator();
                    while (it.hasNext()) {
                        int state = it.next();
                        if (!stateMoveStrings[state].equals(stateMoveStrings[firstState])) {
                            newSet.add(state);
                            it.remove();
                        }
                    }
                    /* 如果不空代表有新分裂的集合，将新分裂的集合加入集合数组中 */
                    if (!newSet.isEmpty()) {
                        hashSetsMinDFA[setCount]=new HashSet<>();
                        hashSetsMinDFA[setCount].addAll(newSet);
                        setCount++;
                    }
                }
            }
        }
        //打印看看过程
        for (String s : stateMoveStrings) {
            System.out.println(s);
        }
        for (int k = 0; k < setCount; k++) {
            for (int state : hashSetsMinDFA[k]) {
                System.out.print(state + " ");
            }
            System.out.println(" ");
        }
    }

    /**
     * 返回状态所在集合
     *
     * @param hashSetsMinDFA 状态集
     * @param setCount       状态集有效个数
     * @param state          查找的状态
     * @return 返回状态在状态集的下标-1则代表是空
     */
    private static int whichSetIndex(HashSet[] hashSetsMinDFA, int setCount, HashSet<Integer> state) {
        if (state == null) {
            return -1;
        }
        int currentState = -1;
        for (int s : state) {
            currentState = s;
        }
        for (int col = 0; col < setCount; col++) {
            if (hashSetsMinDFA[col].contains(currentState)) {
                return col;
            }
        }
        return -1;
    }

    /**
     * 边装换为状态转移矩阵
     *
     * @param nfa              对应的NFA
     * @param characterStrings 字符集
     * @param hashSets         状态转移矩阵
     */
    private static void stateTransitionMatrix(SuffixToNFA.NFA nfa, char[] characterStrings, HashSet[][] hashSets) {
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
    }

    /**
     * 打印状态转移矩阵
     *
     * @param characters          字符集
     * @param stateList           状态集
     * @param hashSets            转移矩阵
     * @param hashSetRowNumber    hashSet需要打印的行数目
     * @param hashSetColumnNumber hashSet需要打印的列数目
     * @param isFinishState       是否为终态的标记数组
     */
    private static void printMoveSet(char[] characters, List<Integer> stateList, HashSet<Integer>[][] hashSets, int hashSetRowNumber, int hashSetColumnNumber, boolean[] isFinishState) {
        System.out.println("状态转移矩阵：");
        System.out.print("\t");
        if (hashSetColumnNumber <= characters.length) {
            for (int i = 0; i < hashSetColumnNumber; i++) {
                System.out.print(" " + characters[i] + "  \t");
            }
        } else {
            for (char ch : characters) {
                System.out.print(" " + ch + "  \t");
            }
        }

        System.out.println("终态");
        for (int row = 0; row < hashSetRowNumber; row++) {
            HashSet<Integer>[] hashSet = hashSets[row];
            System.out.print(stateList.get(row) + "\t");
            for (int col = 0; col < hashSetColumnNumber; col++) {
                if (hashSet[col] != null) {
                    if (hashSet[col].isEmpty()) {
                        System.out.print("null\t");
                    } else {
                        for (int states : hashSet[col]) {
                            System.out.print(states + " ");
                        }
                        System.out.print("  \t");
                    }
                } else {
                    System.out.print("null\t");
                }
            }
            System.out.println(isFinishState[row]);
        }
    }

    /**
     * 将第二行合并到第一行
     *
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
