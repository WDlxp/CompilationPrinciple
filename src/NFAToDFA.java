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
        String regularFormString = "(((a b|b )*(ab|c.d))*)*";
        regularFormString = "int ab";
        StringBuilder result = new StringBuilder();
        System.out.println("正规式为：" + regularFormString);

        if (InfixToSuffix.change(regularFormString, result) == 0) {
            System.out.println("后缀表达式：" + result.toString());
            SuffixToNFA.NFA nfa = SuffixToNFA.changeSuffixToNfa(result.toString());
            SuffixToNFA.printNFA(nfa);
            changeNFAToDFA(nfa, true);
        } else {
            System.out.println("输入有误");
        }
    }

    /**
     * NFA转DFA
     *
     * @param nfa   传入NFA
     * @param print 是否打印过程
     * @return 返回一个最小DFA的二维矩阵，横坐标对应字符集的顺序，最后列代表是否为终态1代表是0代表不是，同时第一行即状态0代表入口
     */
    static int[][] changeNFAToDFA(SuffixToNFA.NFA nfa, boolean print) {
        //是否打印过程
        boolean isPrint = print;

        List<Integer> stateList = nfa.getStateList();
        HashSet<Character> characterSet = nfa.getCharacterSet();
        char[] characters = new char[characterSet.size()];
        /* 用于记录是否为终态 */
        boolean[] isFinishState = new boolean[stateList.size()];
        /* 终态初始化 */
        isFinishState[nfa.getFinishIndex()] = true;
        /* 将set集合转化为char数组 */
        int i = -1;
        for (char ch : characterSet) {
            characters[++i] = ch;
        }
        if (characterSet.size() == 1) {
            int[][] minDFA = new int[1][2];
            minDFA[0][0] = -1;
            minDFA[0][1] = 1;
            if (isPrint) {
                printMinDFA(characters, minDFA);
            }
            return minDFA;
        }

        /*1.使用HashSet作为转移矩阵 */
        HashSet<Integer>[][] hashSets = new HashSet[stateList.size()][characterSet.size()];
        stateTransitionMatrix(nfa, characters, hashSets);

        if (isPrint) {
            /*打印转移矩阵*/
            printMoveSet(characters, stateList, hashSets, stateList.size(), characterSet.size(), isFinishState);
        }
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
        if (tempIndex != -1) {

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
                int oldTempCount = tempCount;
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
                if (tempCount == oldTempCount) {
                    System.out.println("输入正规式成环目前未能解决");
                    return null;
                }
            }
        }
        if (isPrint) {
            /*打印转移矩阵去除控转移*/
            System.out.println("去除空转移");
            printMoveSet(characters, stateList, hashSets, stateList.size(), characterSet.size(), isFinishState);
        }
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
                for (int row = 0; row <= stateColumnIndex - 1; row++) {
                    if (hashSets1[row] != null && hashSets1[row].size() == hashSetsDFA[j][stateColumnIndex].size() && hashSets1[row].equals(hashSetsDFA[j][stateColumnIndex])) {
                        hashSets1[row].clear();
                        hashSets1[row].add(j);
                    }
                }
            }
        }

        if (isPrint) {
            /* 打印DFA */
            System.out.println("整理重命名");
            printMoveSet(characters, stateList, hashSetsDFA, newStateIndexCount + 1, characterSet.size(), isFinishStateDFA);
        }
        /*4.DFA最小化*/

        /*
        未使用HashSet<Integer>[] hashSetsMinDFA = new HashSet[newStateIndexCount+1]因为无序
        使用ArrayList可以保证入口状态在第一个List中的第一位，也就可以在下面的最小化时保证入口状态在下标为0（即第一个）集合
        */
        ArrayList<Integer>[] arrayListMinDFA = new ArrayList[newStateIndexCount + 1];
        /* 初始化将原有状态集分为终态与非终态两个集合(同时将起始状态所在集合作为第一个集合) */
        arrayListMinDFA[0] = new ArrayList<>();
        arrayListMinDFA[1] = new ArrayList<>();

        int setCount = 2;
        /* 用来记录对应状态转移的字符串 */
        String[] stateMoveStrings = new String[newStateIndexCount + 1];
        for (int row = 0; row <= newStateIndexCount; row++) {
            if (isFinishStateDFA[row] == isFinishStateDFA[0]) {
                arrayListMinDFA[0].add(row);
            } else {
                arrayListMinDFA[1].add(row);
            }
        }
        int oldSetCount = 0;
        /* 当不再有新的集合出现时停止 */
        while (setCount > oldSetCount) {
            /* 将当前集合个数赋值给oldSetCount */
            oldSetCount = setCount;
            /* 遍历当前集合需找是否需要分裂 */
            for (int setIndex = 0; setIndex < setCount; setIndex++) {
                int count = arrayListMinDFA[setIndex].size();
                /* 当前集合的长度>1才需要进行判断是否分裂 */
                if (count > 1) {
                    /* 记录第一个状态和获取每个状态的转移字符串 */
                    for (int state : arrayListMinDFA[setIndex]) {
                        StringBuilder moveString = new StringBuilder();
                        for (int k = 0; k < characters.length; k++) {
                            moveString.append(whichSetIndex(arrayListMinDFA, setCount, hashSetsDFA[state][k]));
                        }
                        stateMoveStrings[state] = moveString.toString();
                    }
                    /* 遍历以第一个状态(arrayListMinDFA[setIndex].get(0))为该集合的标准将与第一个状态转移不符的重新建立一个集合 */
                    ArrayList newSet = new ArrayList();
                    Iterator<Integer> it = arrayListMinDFA[setIndex].iterator();
                    while (it.hasNext()) {
                        int state = it.next();
                        if (!stateMoveStrings[state].equals(stateMoveStrings[arrayListMinDFA[setIndex].get(0)])) {
                            newSet.add(state);
                            it.remove();
                        }
                    }
                    /* 如果不空代表有新分裂的集合，将新分裂的集合加入集合数组中 */
                    if (!newSet.isEmpty()) {
                        arrayListMinDFA[setCount] = new ArrayList<>();
                        arrayListMinDFA[setCount].addAll(newSet);
                        setCount++;
                    }
                }
            }
        }

        /* 最小DFA的整理 */
        for (int k = 0; k < setCount; k++) {
            if (arrayListMinDFA[k] != null) {
                for (int state = 1; state < arrayListMinDFA[k].size(); state++) {
                    mergeTwoRows(characters, hashSetsDFA, isFinishStateDFA, arrayListMinDFA[k].get(0), arrayListMinDFA[k].get(state));
                }
            }
        }

        if (isPrint) {
            /* 打印DFA */
            System.out.println("整理后的DFA最小化");
            printMoveSet(characters, stateList, hashSetsDFA, newStateIndexCount + 1, characterSet.size(), isFinishStateDFA);
        }

        /* 最小DFA重命名(使用二维数组来装最后的DFA并返回，最后增加一列用来表示是否终态) */
        int[][] minDFA = new int[setCount][characters.length + 1];
        for (int j = 0; j < setCount; j++) {
            if (arrayListMinDFA[j].size() != 0) {
                int firstState = arrayListMinDFA[j].get(0);
                for (int col = 0; col < characters.length; col++) {
                    int sateIndex = whichSetIndex(arrayListMinDFA, setCount, hashSetsDFA[firstState][col]);
                    minDFA[j][col] = sateIndex;
                }
                if (isFinishStateDFA[firstState]) {
                    minDFA[j][characters.length] = 1;
                }
            }
        }
        if (isPrint) {
            printMinDFA(characters, minDFA);
        }
        return minDFA;
    }

    /**
     * 打印最小化DFA
     *
     * @param characters 字符集
     * @param minDFA     最小化DFA数组
     */
    private static void printMinDFA(char[] characters, int[][] minDFA) {
        /* 显示整理好后重命名的DFA */
        System.out.println("DFA最小化");
        System.out.print("\t");
        for (char ch : characters) {
            System.out.print(ch + "\t");
        }
        System.out.println("终态");
        int state = 0;
        for (int[] ints : minDFA) {
            System.out.print(state++ + "\t");
            for (int i1 : ints) {
                System.out.print(i1 + "\t");
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
    private static int whichSetIndex(ArrayList[] hashSetsMinDFA, int setCount, HashSet<Integer> state) {
        if (state == null) {
            return -1;
        }
        for (int col = 0; col < setCount; col++) {
            if (hashSetsMinDFA[col].containsAll(state)) {
                return col;
            }
        }
        return -1;
    }

    /**
     * 边装换为状态转移矩阵
     *
     * @param nfa        对应的NFA
     * @param characters 字符集
     * @param hashSets   状态转移矩阵
     */
    private static void stateTransitionMatrix(SuffixToNFA.NFA nfa, char[] characters, HashSet[][] hashSets) {
        for (SuffixToNFA.Side side : nfa.getMoveList()) {
            /*获取当前需要添加的位置下标*/
            int state = side.getPreState();
            char ch = side.getTransferCondition();
            int characterIndex = -1;
            while (true) {
                if (characters[++characterIndex] == ch) {
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
     * @param characters    字符集
     * @param hashSets      转移矩阵
     * @param isFinishState 是否终态标志
     * @param firstState    第一个状态
     * @param secondState   第二个状态
     */
    private static void mergeTwoRows(char[] characters, HashSet<Integer>[][] hashSets, boolean[] isFinishState, int firstState, int secondState) {
        for (int k = 0; k < characters.length; k++) {
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
