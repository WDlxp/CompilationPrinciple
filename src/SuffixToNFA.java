import java.util.*;

/**
 * 后缀表达式转NFA
 * 思路：
 * 通过全局的一个stateCount来保证状态不重名
 * 通过五元组存取NFA
 * 通过三元组存取边
 * 逐个取后缀表达式的字符
 * 如果取到字符为*则计数count++非*则将count=0
 * 如果count>1则说明出现连续的*操作符直接略过
 * 之后根据Thomson算法根据情况进行操作
 *
 * @author wdl
 */
public class SuffixToNFA {
    public static void main(String[] args) {
        String regularFormString = "(((a b|b )*(ab|c.d))*)*";
        regularFormString="int ab";
        StringBuilder result = new StringBuilder();
        System.out.println("正规式为：" + regularFormString);

        if (InfixToSuffix.change(regularFormString, result) == 0) {
            System.out.println("后缀表达式：" + result.toString());
            NFA nfa = changeSuffixToNfa(result.toString());
           printNFA(nfa);
        } else {
            System.out.println("输入有误");
        }
    }

    static void printNFA(NFA nfa){
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
        for (Side side : nfa.getMoveList()) {
            System.out.print("(" + side.preState + "," + side.nextState + "," + side.transferCondition + ")");
        }
        System.out.println("\n起始状态:" + nfa.getStateList().get(nfa.getStartIndex()));
        System.out.println("结束状态:" + nfa.getStateList().get(nfa.getFinishIndex()));
    }
    /**
     * 记录边的三元组
     * 前一个状态preState
     * 后一个状态nextState
     * 转移条件transferCondition
     */
    static class Side {
        private int preState;
        private int nextState;
        private char transferCondition;

        Side(int preState, int nextState, char transferCondition) {
            this.preState = preState;
            this.nextState = nextState;
            this.transferCondition = transferCondition;
        }

        public int getPreState() {
            return preState;
        }

        public int getNextState() {
            return nextState;
        }

        public char getTransferCondition() {
            return transferCondition;
        }
    }

    /**
     * ε
     * 使用五元组表示NFA(S,Σ,move,s0,F)
     * stateList:状态集
     * characterSet:字符集
     * moveList边的集合
     * startIndex初态(下标)
     * finishIndex终态(下标)
     */
    static class NFA {
        private List<Integer> stateList;
        private HashSet<Character> characterSet;
        private List<Side> moveList;
        private int startIndex;
        private int finishIndex;

        NFA(List<Integer> stateList, HashSet<Character> characterSet, List<Side> moveList, int startIndex, int finishIndex) {
            this.stateList = stateList;
            this.characterSet = characterSet;
            this.moveList = moveList;
            this.startIndex = startIndex;
            this.finishIndex = finishIndex;
        }

        List<Integer> getStateList() {
            return stateList;
        }

        HashSet<Character> getCharacterSet() {
            return characterSet;
        }

        List<Side> getMoveList() {
            return moveList;
        }

        int getStartIndex() {
            return startIndex;
        }

        int getFinishIndex() {
            return finishIndex;
        }
    }

    /**
     * 将后缀表达式转换为NFA
     *
     * @param suffix 传入后缀表达式
     * @return 返回最后的NFA(由于前面的正规式已经验证对错对错 ， 此处无需再验证对错)
     */
    public static NFA changeSuffixToNfa(String suffix) {
        /* 存放NFA的栈 */
        Stack<NFA> nfaStack = new Stack<>();
        /* 状态的个数防止状态重名 */
        int stateCount = -1;
        /* 用于记录*连续出现的次数，连续出现仅计算第一次 */
        int count = 0;

        for (int i = 0; i < suffix.length(); i++) {
            char ch = suffix.charAt(i);
            /*通过count来忽略连续*操作符的情况*/
            if (ch == '*') {
                count++;
            } else {
                count = 0;
            }
            if (count > 1) {
                continue;
            }
            //判断是否为操作数
            boolean isOperand = ch != '.' && ch != '*' && ch != '|';
            /* 如果是操作数则直接加上状态和条件然后放入栈中即可 */
            if (isOperand) {
                List<Integer> stateList = new ArrayList<>();

                stateList.add(++stateCount);
                stateList.add(++stateCount);

                HashSet<Character> charSet = new HashSet<>();
                charSet.add(ch);

                Side side = new Side(stateList.get(0), stateList.get(1), ch);
                List<Side> moveList = new ArrayList<>();

                moveList.add(side);
                NFA nfa = new NFA(stateList, charSet, moveList, 0, 1);
                nfaStack.push(nfa);
                continue;
            }
            /* 操作符为'*'闭包,则从栈中取出1个nfa操作完后将新的NFA入栈*/
            if (ch == '*') {
                NFA nfa = nfaStack.pop();
                List<Integer> nfaStateList = nfa.getStateList();
                HashSet<Character> nfaCharSet = nfa.getCharacterSet();
                List<Side> nfaMoveList = nfa.getMoveList();

                int newStartState = ++stateCount;
                int newFinishState = ++stateCount;

                Side side1 = new Side(newStartState, nfaStateList.get(nfa.getStartIndex()), 'ε');
                Side side2 = new Side(newStartState, newFinishState, 'ε');
                Side side3 = new Side(nfaStateList.get(nfa.getFinishIndex()), newFinishState, 'ε');
                Side side4 = new Side(newFinishState, nfaStateList.get(nfa.getStartIndex()), 'ε');

                nfaStateList.add(newStartState);
                nfaStateList.add(newFinishState);

                nfaCharSet.add('ε');

                nfaMoveList.add(side1);
                nfaMoveList.add(side2);
                nfaMoveList.add(side3);
                nfaMoveList.add(side4);

                int newStartIndex = nfaStateList.size() - 2;
                int newFinishIndex = nfaStateList.size() - 1;

                NFA newNfa = new NFA(nfaStateList, nfaCharSet, nfaMoveList, newStartIndex, newFinishIndex);

                nfaStack.push(newNfa);
                continue;
            }
            //如果不是*则接下来就是.或|提前取出该用的
            NFA nextNfa = nfaStack.pop();
            NFA firstNfa = nfaStack.pop();
            List<Integer> firstStateList = firstNfa.getStateList();
            List<Integer> nextStateList = nextNfa.getStateList();
            HashSet<Character> firstCharSet = firstNfa.getCharacterSet();
            List<Side> firstMoveList = firstNfa.getMoveList();
            //如果遇到操作符.则从栈中取出两个nfa操作完后将新的NFA入栈
            if (ch == '.') {
                int len = firstStateList.size();
                int firstStartIndex = firstNfa.getStartIndex();
                int nextFinishIndex = nextNfa.getFinishIndex();

                Side side = new Side(firstStateList.get(firstNfa.getFinishIndex()), nextStateList.get(nextNfa.getStartIndex()), 'ε');

                firstCharSet.add('ε');
                firstStateList.addAll(nextStateList);

                firstCharSet.addAll(nextNfa.getCharacterSet());

                firstMoveList.addAll(nextNfa.getMoveList());
                firstMoveList.add(side);

                NFA nfa = new NFA(firstStateList, firstCharSet, firstMoveList, firstStartIndex, nextFinishIndex + len);
                nfaStack.push(nfa);
                continue;
            }
            //到这里则说明是操作符|则从栈中取出两个nfa操作完后将新的NFA入栈
            int newStartState = ++stateCount;
            int newFinishState = ++stateCount;

            Side side1 = new Side(newStartState, firstStateList.get(firstNfa.getStartIndex()), 'ε');
            Side side2 = new Side(firstStateList.get(firstNfa.getFinishIndex()), newFinishState, 'ε');

            Side side3 = new Side(newStartState, nextStateList.get(nextNfa.getStartIndex()), 'ε');
            Side side4 = new Side(nextStateList.get(nextNfa.getFinishIndex()), newFinishState, 'ε');

            firstCharSet.add('ε');

            firstStateList.addAll(nextStateList);
            firstStateList.add(newStartState);
            firstStateList.add(newFinishState);

            firstCharSet.addAll(nextNfa.getCharacterSet());

            firstMoveList.addAll(nextNfa.getMoveList());
            firstMoveList.add(side1);
            firstMoveList.add(side2);
            firstMoveList.add(side3);
            firstMoveList.add(side4);

            int newStartIndex = firstStateList.size() - 2;
            int newFinishIndex = firstStateList.size() - 1;

            NFA nfa = new NFA(firstStateList, firstCharSet, firstMoveList, newStartIndex, newFinishIndex);
            nfaStack.push(nfa);
        }
        return nfaStack.pop();
    }
}
