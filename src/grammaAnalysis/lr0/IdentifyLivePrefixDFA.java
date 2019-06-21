package grammaAnalysis.lr0;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

/**
 * 2.识别活前缀的DFA
 *
 * @author wdl
 */
public class IdentifyLivePrefixDFA {
    /**
     * 识别活前缀的DFA
     *
     * @param extensionResult 拓广文法的结果
     * @return 识别活前缀的DFA
     */
    static DFA identifyLivePrefixDFA(ExtensionGrammar.Result extensionResult) {
        /*状态集合*/
        ArrayList<LrState> lrStates = new ArrayList<>();
        /* 边的集合*/
        ArrayList<LrSide> lrSides = new ArrayList<>();
        //拓广文法的产生式集合
        ArrayList<String> productSet = extensionResult.getProductSet();
        //非终结符集合
        HashSet<Character> nonTerminalSet = extensionResult.getNonTerminalSet();
        //终结符集合
        HashSet<Character> terminatorSet = extensionResult.getTerminatorSet();
        //初始化LR0的项目集起始项目，将产生式变为项目集
        ArrayList<LrProject> lrProjects = new ArrayList<>();
        for (String product :
                productSet) {
            if (product.substring(1) == "ε") {
                lrProjects.add(new LrProject(product.charAt(0), ""));
            } else {
                lrProjects.add(new LrProject(product.charAt(0), product.substring(1)));
            }
        }

        //状态编号
        int stateIndex = 0;

        //初始化首个核
        ArrayList<LrProject> coreItems = new ArrayList<>();
        coreItems.add(lrProjects.get(0).lrProjectClone());
        LrState lrState = new LrState(coreItems, getClosureItems(coreItems, lrProjects, nonTerminalSet), stateIndex);
        lrStates.add(lrState);

        //将状态放入栈中
        Stack<LrState> stateStack = new Stack<>();
        stateStack.add(lrState);
        //记录一个状态中已经寻找过子状态的字符
        ArrayList<Character> hasDo=new ArrayList<>();
        while (!stateStack.isEmpty()) {
            //获取栈中的项目
            LrState lrState1 = stateStack.pop();
            //获取状态中所有的项目
            ArrayList<LrProject> projects = new ArrayList<>(lrState1.coreItems);
            projects.addAll(lrState1.closureItems);
            //清空上一个状态遍历寻找下一个状态的集合
            hasDo.clear();
            //遍历状态的项目
            for (int i = 0; i < projects.size(); i++) {
                LrProject lrProject = projects.get(i);
                //如果不是规约项目继续推到
                if (!lrProject.isDotLast()) {
                    //获取该项目的移进的字符
                    char ch = lrProject.dotPointerNext();
                    //未移进过才继续移进
                    if (!hasDo.contains(ch)) {
                        //标记处理过
                        hasDo.add(ch);
                        //获取项目的核
                        ArrayList<LrProject> core = getCoreItems(projects, ch);
                        boolean isHave = false;
                        //通过核判断该状态是否已经存在
                        for (LrState state : lrStates) {
                            if (state.isEqual(core)) {
                                //存在记录边
                                lrSides.add(new LrSide(lrState1, state, ch));
                                isHave = true;
                                break;
                            }
                        }
                        //不存在创建新的状态并入栈
                        if (!isHave) {
                            LrState lrState2 = new LrState(core, getClosureItems(core, lrProjects, nonTerminalSet), ++stateIndex);
                            lrStates.add(lrState2);
                            lrSides.add(new LrSide(lrState1, lrState2, ch));
                            stateStack.add(lrState2);
                        }
                    }
                }
            }
        }

        return new DFA(lrStates, lrSides, terminatorSet, nonTerminalSet);
    }

    /**
     * 获取状态的核项目
     *
     * @param lrProjects 初始的项目及
     * @param next       过渡到下一个状态的字符
     * @return 核项目集合
     */
    static ArrayList<LrProject> getCoreItems(ArrayList<LrProject> lrProjects, char next) {
        ArrayList<LrProject> coreItems = new ArrayList<>();
        for (LrProject lrProject :
                lrProjects) {
            if (lrProject.dotPointerNext() == next) {
                LrProject lrProject1 = lrProject.lrProjectClone();
                lrProject1.dotPointerPlus();
                coreItems.add(lrProject1);
            }
        }
        return coreItems;
    }

    /**
     * 获取闭包项
     *
     * @param coreItems      核的集合
     * @param lrProjects     初始项目集
     * @param nonTerminalSet 非终结符集合
     * @return 核的闭包集合
     */
    public static ArrayList<LrProject> getClosureItems(ArrayList<LrProject> coreItems, ArrayList<LrProject> lrProjects, HashSet<Character> nonTerminalSet) {
        ArrayList<LrProject> closureItems = new ArrayList<>();
        //已经加入过的非终结符开头的字符
        ArrayList<Character> nonTerminalHasAdd = new ArrayList<>();
        for (LrProject lrProject : coreItems) {
            if (!nonTerminalHasAdd.contains(lrProject.leftSide)) {
                nonTerminalHasAdd.add(lrProject.leftSide);
            }
        }
        for (LrProject lrProject : coreItems) {
            //核没有终结才会有后续
            if (!lrProject.isDotLast()) {
                char dotNext = lrProject.dotPointerNext();
                //未加入过
                if (!nonTerminalHasAdd.contains(dotNext)) {
                    nonTerminalHasAdd.add(dotNext);
                    for (LrProject project : lrProjects) {
                        if (project.leftSide == dotNext) {
                            closureItems.add(project.lrProjectClone());
                        }
                    }
                }
            }
        }
        //获取初始引入的项目集长度
        int lastClosureItemsLength = closureItems.size();
        int currentClosureItemsLength = lastClosureItemsLength;
        if (lastClosureItemsLength != 0) {
            //遍历核引入的项目查看是否会引入新的项目
            do {
                lastClosureItemsLength = currentClosureItemsLength;
                for (int i = 0; i < closureItems.size(); i++) {
                    LrProject lrProject = closureItems.get(i);
                    //核没有终结才会有后续
                    if (!lrProject.isDotLast()) {
                        char dotNext = lrProject.dotPointerNext();
                        //未加入过
                        if (!nonTerminalHasAdd.contains(dotNext)) {
                            nonTerminalHasAdd.add(dotNext);
                            for (LrProject project : lrProjects) {
                                if (project.leftSide == dotNext) {
                                    closureItems.add(project.lrProjectClone());
                                }
                            }
                        }
                    }
                }
                currentClosureItemsLength = closureItems.size();
            } while (currentClosureItemsLength > lastClosureItemsLength);
        }
        return closureItems;
    }

    /**
     * 识别活前缀的DFA
     */
    static class DFA {
        /**
         * 状态集合
         */
        private ArrayList<LrState> lrStates;
        /**
         * 边的集合
         */
        private ArrayList<LrSide> lrSides;
        /**
         * 终结符集合
         */
        private HashSet<Character> terminatorSet;
        /**
         * 非终结符集合
         */
        private HashSet<Character> nonTerminalSet;

        public DFA(ArrayList<LrState> lrStates, ArrayList<LrSide> lrSides, HashSet<Character> terminatorSet, HashSet<Character> nonTerminalSet) {
            this.lrStates = lrStates;
            this.lrSides = lrSides;
            this.terminatorSet = terminatorSet;
            this.nonTerminalSet = nonTerminalSet;
        }

        public ArrayList<LrState> getLrStates() {
            return lrStates;
        }

        public void setLrStates(ArrayList<LrState> lrStates) {
            this.lrStates = lrStates;
        }

        public ArrayList<LrSide> getLrSides() {
            return lrSides;
        }

        public void setLrSides(ArrayList<LrSide> lrSides) {
            this.lrSides = lrSides;
        }

        public HashSet<Character> getTerminatorSet() {
            return terminatorSet;
        }

        public void setTerminatorSet(HashSet<Character> terminatorSet) {
            this.terminatorSet = terminatorSet;
        }

        public HashSet<Character> getNonTerminalSet() {
            return nonTerminalSet;
        }

        public void setNonTerminalSet(HashSet<Character> nonTerminalSet) {
            this.nonTerminalSet = nonTerminalSet;
        }
    }

    /**
     * LR0的边
     */
    static class LrSide {
        LrState startState;
        LrState endState;
        char way;

        /**
         * 边
         *
         * @param startState 开始状态
         * @param endState   结束状态
         * @param way        边上的条件
         */
        public LrSide(LrState startState, LrState endState, char way) {
            this.startState = startState;
            this.endState = endState;
            this.way = way;
        }
    }
}

