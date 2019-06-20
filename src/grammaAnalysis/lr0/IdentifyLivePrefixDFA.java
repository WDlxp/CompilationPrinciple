package grammaAnalysis.lr0;

import java.util.ArrayList;
import java.util.Calendar;
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
        //获取拓广的结果
        ArrayList<String> productSet = extensionResult.getProductSet();
        HashSet<Character> nonTerminalSet = extensionResult.getNonTerminalSet();
        HashSet<Character> terminatorSet = extensionResult.getTerminatorSet();
        //初始化LR0的项目集起始
        ArrayList<LrProject> lrProjects = new ArrayList<>();
        for (String product :
                productSet) {
            if (product.substring(1) == "ε") {
                lrProjects.add(new LrProject(product.charAt(0), ""));
            } else {
                lrProjects.add(new LrProject(product.charAt(0), product.substring(1)));
            }
        }
        //初始化首个核
        int stateIndex = 0;
        ArrayList<LrProject> coreItems = new ArrayList<>();
        coreItems.add(lrProjects.get(0).lrProjectClone());
        LrState lrState = new LrState();
        //核
        lrState.coreItems = coreItems;
        //核求出闭包
        lrState.closureItems = getClosureItems(coreItems, lrProjects, nonTerminalSet);
        lrState.stateIndex = stateIndex;
        lrStates.add(lrState);

        Stack<LrState> stateStack = new Stack<>();
        stateStack.add(lrState);

        ArrayList<Character> hasDo;
        while (!stateStack.isEmpty()) {
            LrState lrState1 = stateStack.pop();
            ArrayList<LrProject> projects = new ArrayList<>(lrState1.coreItems);
            projects.addAll(lrState1.closureItems);
            hasDo = new ArrayList<>();
            for (int i = 0; i < projects.size(); i++) {
                LrProject lrProject = projects.get(i);
                if (!lrProject.isDotLast()) {
                    char ch = lrProject.dotPointerNext();
                    if (!hasDo.contains(ch)) {
                        hasDo.add(ch);
                        ArrayList<LrProject> core = getCoreItems(projects, ch);
                        boolean isHave = false;
                        for (LrState state : lrStates) {
                            if (state.isEqual(core)) {
                                lrSides.add(new LrSide(lrState1, state, ch));
                                isHave = true;
                                break;
                            }
                        }
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
     * @param coreItems 核的集合
     * @return 核的闭包
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

