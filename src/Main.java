/**
 * @author wdl
 */
public class Main {
    public static void main(String[] args) {
        /* (((i n|t )*(ab|c.d))*)* */
        String string = "int";
        String input = "int";
        boolean print = true;
        System.out.println("合法性判断结果：" + isLegitimacy(string, input, print));
    }

    /**
     * 合法性判断
     *
     * @param string 正规式
     * @param input  判断的单词
     * @param print  是否打印过程
     * @return 返回是否合法
     */
    private static boolean isLegitimacy(String string, String input, boolean print) {
        if (returnMiniDFA(string, print).isTrue()) {
            int[][] miniDFA = returnMiniDFA(string, print).getMiniDFA();
            SuffixToNFA.NFA nfa = null;
            StringBuilder result = new StringBuilder();
            InfixToSuffix.change(string, result);
            nfa = SuffixToNFA.changeSuffixToNfa(result.toString());
            return legitimacy(miniDFA, input, nfa);
        }
        return false;
    }

    /**
     * returnMiniDFA返回值的数据结构
     */
    static class Result {
        private int[][] miniDFA;
        private boolean isTrue;

        int[][] getMiniDFA() {
            return miniDFA;
        }

        void setMiniDFA(int[][] miniDFA) {
            this.miniDFA = miniDFA;
        }

        boolean isTrue() {
            return isTrue;
        }

        void setTrue(boolean aTrue) {
            isTrue = aTrue;
        }
    }

    /**
     * @param string 正规式
     * @return Result 返回miniDFA和正规式是否正确
     */
    private static Result returnMiniDFA(String string, boolean print) {
        StringBuilder result = new StringBuilder();

        SuffixToNFA.NFA nfa = null;
        Result result1 = new Result();
        if (InfixToSuffix.change(string, result) == 0) {
            nfa = SuffixToNFA.changeSuffixToNfa(result.toString());
            if (print) {
                SuffixToNFA.printNFA(nfa);
            }

            result1.setMiniDFA(NFAToDFA.changeNFAToDFA(nfa, print));
            result1.setTrue(true);

        } else {

            result1.setTrue(false);
        }
        return result1;
    }


    private static boolean legitimacy(int[][] miniDFA, String input, SuffixToNFA.NFA nfa) {
        int i = -1;
        char[] characters = new char[nfa.getCharacterSet().size()];
        for (char ch : nfa.getCharacterSet()) {
            characters[++i] = ch;
        }
        int current = 0;
        for (int j = 0; j < input.length(); j++) {
            char op = input.charAt(j);
            for (int index = 0; index < nfa.getCharacterSet().size(); index++) {
                if (characters[index] == op) {
                    if (miniDFA[current][index] == -1) {
                        if (j == input.length() - 1) {
                            return miniDFA[current][characters.length] == 1;
                        }
                        return false;
                    } else {
                        current = miniDFA[current][index];
                        break;
                    }
                }
            }
        }
        return false;
    }
}
