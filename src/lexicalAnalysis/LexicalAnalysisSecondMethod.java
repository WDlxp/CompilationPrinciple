package lexicalAnalysis;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author wdl
 */
class LexicalAnalysisSecondMethod {
    /**
     * 词法分析
     *
     * @param filePath            单词文件路径
     * @param regularFormFilePath 正规式文件路径
     * @param resultFilePath      结果输出文件路径
     * @param isPrint             是否打印过程
     */
    static void lexicalAnalysis(String filePath, String regularFormFilePath, String resultFilePath, boolean isPrint) throws IOException {


        //读取正规式文件和单词文件
        String readWordsFileString = readFile(filePath, " ");
        String readRegularFormString = readFile(regularFormFilePath, "\n");

        ArrayList<String> splitWordsString = splitWords(readWordsFileString);

        //放置正规式返回的miniDFA的集合
        ArrayList<Result> miniDfaResultList = new ArrayList<>();
        //放置正规式集合
        ArrayList<String> regularFormList = new ArrayList<>();
        //将读取出来的正规式按行划分
        String[] regularFormStrings = readRegularFormString.split("\n");
        for (String regularFormString : regularFormStrings) {
            //划分出的取出的非空格才是需要处理的正规式
            if (!regularFormString.equals("")) {
                regularFormList.add(regularFormString);
                Result result = returnMiniDFA(regularFormString, isPrint);
                miniDfaResultList.add(result);
                if (!result.isTrue()) {
                    System.out.println("正规式" + regularFormString + "输入有误");
                }
            }
        }
        //获取miniDFA的结果集合长度
        int len = miniDfaResultList.size();
        if (len != 0) {
            // 构建FileOutputStream对象,文件不存在会自动新建
            FileOutputStream fop = new FileOutputStream(resultFilePath);
            // 构建OutputStreamWriter对象,参数可以指定编码,默认为操作系统默认编码,windows上是gbk
            OutputStreamWriter writer = new OutputStreamWriter(fop, "UTF-8");

            // 构建FileOutputStream对象,文件不存在会自动新建
            int[][] miniDFA = null;
            char[] characters = null;

            if (!readWordsFileString.isEmpty()) {
                for (String cell : splitWordsString) {
                    if (!cell.equals("")) {
                        boolean isRight = false;
                        System.out.println(cell);
                        if (SymbolSets.KEY_WORD_SET.contains(cell)) {
                            writer.append(cell + "\n合法性：" + true + " Java关键字");
                            System.out.println("合法性：" + true + " Java关键字");
                        } else if (SymbolSets.RESERVED_WORD_SET.contains(cell)) {
                            writer.append(cell + "\n合法性：" + true + " Java保留字");
                            System.out.println("合法性： " + true + " Java保留字");
                        } else if (SymbolSets.FLAG_SET.contains(cell)) {
                            writer.append(cell + "\n合法性：" + true + " Java标志符");
                            System.out.println("合法性： " + true + " Java标志符");
                        } else if (SymbolSets.OPERATOR_SET.contains(cell)) {
                            writer.append(cell + "\n合法性：" + true + " Java运算符");
                            System.out.println("合法性： " + true + " Java运算符");
                        } else if (SymbolSets.SPLITTER_SET.contains(cell)) {
                            writer.append(cell + "\n合法性：" + true + " Java分割符");
                            System.out.println("合法性： " + true + " Java分割符");
                        }else {
                            int i = 0;
                            for (; i < len; i++) {
                                Result result = miniDfaResultList.get(i);
                                if (result.isTrue()) {
                                    miniDFA = result.getMiniDFA();
                                    characters = result.getCharacters();
                                    isRight = legitimacy(miniDFA, cell, characters);
                                }
                                if (isRight) {
                                    break;
                                }
                            }
                            if (isRight) {
                                // 写入到缓冲区
                                writer.append(cell + "\n合法性： " + isRight + " 符合第" + (i + 1) + "个正规式: " + regularFormList.get(i));
                                System.out.println("合法性： " + isRight + "  符合第" + (i + 1) + "个正规式: " + regularFormList.get(i));
                            } else {
                                writer.append(cell + "\n合法性： " + isRight);
                                System.out.println("合法性： " + isRight);
                            }
                        }
                        // 换行
                        writer.append("\r\n");
                    }
                }
            } else {
                System.out.println("文件内容为空！");
            }
            // 刷新缓存冲,写入到文件,如果下面已经没有写入的内容了,直接close也会写入
            writer.close();
        } else {
            System.out.println("请在正规式文件中输入正规式以行划分");
        }
    }

    /**
     * 字符集合的集合
     */
    static class SymbolSets {
        /**
         * 关键字集合
         */
        static final HashSet KEY_WORD_SET = new HashSet() {{
            add("abstract");
            add("assert");
            add("boolean");
            add("break");
            add("byte");
            add("case");
            add("catch");
            add("char");
            add("class");
            add("continue");
            add("default");
            add("do");
            add("double");
            add("else");
            add("enum");
            add("extends");
            add("final");
            add("finally");
            add("float");
            add("for");
            add("if");
            add("implements");
            add("import");
            add("instanceof");
            add("int");
            add("interface");
            add("long");
            add("native");
            add("new");
            add("package");
            add("private");
            add("protected");
            add("public");
            add("return");
            add("short");
            add("static");
            add("strictfp");
            add("super");
            add("switch");
            add("synchronized");
            add("this");
            add("throw");
            add("throws");
            add("transient");
            add("try");
            add("void");
            add("volatile");
            add("while");
        }};
        /**
         * 保留字集合
         */
        static final HashSet RESERVED_WORD_SET = new HashSet() {{
            add("goto");
            add("const");
        }};
        /**
         * 标志符集合
         */
        static final HashSet FLAG_SET = new HashSet() {{
            add("null");
            add("true");
            add("false");
        }};
        /**
         * 运算符集合
         */
        static final HashSet OPERATOR_SET = new HashSet() {{
            add("=");
            add("==");
            add("!=");
            add(">=");
            add("<=");
            add("+=");
            add("-=");
            add("*=");
            add("<<=");
            add(">>=");
            add("&=");
            add("^=");
            add("|=");
            add("+");
            add("++");
            add("-");
            add("--");
            add("*");
            add("<");
            add(">");
            add("/");
            add("!");
            add("%");
            add("^");
            add("|");
            add("&");
            add("<<");
            add(">>");
            add(">>>");
            add("~");
            add(":");
            add("?");
        }};

        /**
         * 分割符集合
         */
        static final HashSet SPLITTER_SET = new HashSet() {{
            add(";");
            add("(");
            add(")");
            add("{");
            add("}");
            add("[");
            add("]");
            add(",");
        }};
    }

    /**
     * 读取文件内容
     *
     * @param filePath 文件路径
     * @param spit     划分行的字符
     * @return 文件内容
     */
    private static String readFile(String filePath, String spit) throws IOException {
        //定义一个file对象，用来初始化FileReader
        File file = new File(filePath);
        //定义一个fileReader对象，用来初始化BufferedReader
        FileReader reader = new FileReader(file);
        //new一个BufferedReader对象，将文件内容读取到缓存
        BufferedReader bReader = new BufferedReader(reader);
        //定义一个字符串缓存，将字符串存放缓存中
        StringBuilder sb = new StringBuilder();
        String s = "";
        //逐行读取文件内容，不读取换行符和末尾的空格
        while ((s = bReader.readLine()) != null) {
            //将读取的字符串添加换行符后累加存放在缓存中
            sb.append(s).append(spit);
        }
        bReader.close();
        return sb.toString();
    }

    /**
     * returnMiniDFA返回值的数据结构
     */
    static class Result {
        private int[][] miniDFA;
        private boolean isTrue;
        private char[] characters;

        void setCharacters(char[] characters) {
            this.characters = characters;
        }

        char[] getCharacters() {
            return characters;
        }

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
     * @param regularFormString 正规式
     * @return Result 返回miniDFA和正规式是否正确
     */
    private static Result returnMiniDFA(String regularFormString, boolean print) {
        StringBuilder suffixResult = new StringBuilder();
        SuffixToNFA.NFA nfa = null;
        Result miniDfaResult = new Result();
        if (InfixToSuffix.change(regularFormString, suffixResult) == 0) {
            nfa = SuffixToNFA.changeSuffixToNfa(suffixResult.toString());
            if (print) {
                SuffixToNFA.printNFA(nfa);
            }
            int i = -1;
            char[] characters = new char[nfa.getCharacterSet().size()];
            for (char ch : nfa.getCharacterSet()) {
                characters[++i] = ch;
            }
            miniDfaResult.setCharacters(characters);
            miniDfaResult.setMiniDFA(NFAToDFA.changeNFAToDFA(nfa, print));
            miniDfaResult.setTrue(true);
        } else {
            miniDfaResult.setTrue(false);
        }
        return miniDfaResult;
    }

    /**
     * 判断合法性
     *
     * @param miniDFA    最小DFA的二维数组
     * @param input      输入字符
     * @param characters 字符集
     * @return 返回是否合法
     */
    private static boolean legitimacy(int[][] miniDFA, String input, char[] characters) {
        int current = 0;
        for (int j = 0; j < input.length(); j++) {
            //字符是否存在的标志
            boolean isHave = false;
            char op = input.charAt(j);
            for (int index = 0; index < characters.length; index++) {
                if (characters[index] == op) {
                    isHave = true;
                    if (miniDFA[current][index] == -1) {
                        return false;
                    } else {
                        current = miniDFA[current][index];
                        break;
                    }
                }
            }
            //如果符号不存在直接返回false
            if (!isHave) {
                return false;
            }
        }
        return miniDFA[current][characters.length] == 1;
    }

    //新思路写的部分

    /**
     * 为应该有空格而没有空格的地方添加空格
     *
     * @param wordString 单词字符集
     * @return 返回添加后人的单词字符串
     */
    private static ArrayList<String> splitWords(String wordString) {
        ArrayList<String> resultWordsArrayList = new ArrayList<>();
        //每次截取的单词串
        StringBuilder tempWordString = new StringBuilder();
        int len = wordString.length();
        //每次取出的字符
        char ch;
        //记录前一个字符以及后一个字符的类型并且使用第一个字符进行初始化
        ch = wordString.charAt(0);
        int lastType = witchTypeSet(ch);
        tempWordString.append(ch);
        int currentType;

        for (int index = 1; index < len; index++) {
            ch = wordString.charAt(index);
            currentType = witchTypeSet(ch);
            if (currentType == 1) {
                if (tempWordString.length() != 0 && tempWordString.charAt(0) != ' ') {
                    resultWordsArrayList.add(tempWordString.toString());
                    //清空字符串
                    tempWordString.delete(0, tempWordString.length());
                }
                resultWordsArrayList.add(ch + "");
            } else if (currentType == lastType) {
                tempWordString.append(ch);
            } else {
                if (tempWordString.length() != 0 && tempWordString.charAt(0) != ' ') {
                    resultWordsArrayList.add(tempWordString.toString());
                }
                //清空字符串
                tempWordString.delete(0, tempWordString.length());
                tempWordString.append(ch);
            }
            lastType = currentType;
        }
        return resultWordsArrayList;
    }

    /**
     * 组成运算符的Char集合
     */
    static HashSet<Character> operatorCharSet = new HashSet<>() {{
        add('=');
        add('+');
        add('-');
        add('<');
        add('>');
        add('/');
        add('!');
        add('%');
        add('^');
        add('|');
        add('&');
        add('~');
    }};

    /**
     * 组成分割符的Char集合
     */
    static HashSet<Character> splitterCharSet = new HashSet<>() {{
        add(';');
        add(':');
        add('(');
        add(')');
        add('{');
        add('}');
        add('"');
        add('[');
        add(']');
        add(',');
    }};

    /**
     * 传入单个字符，判断单个字符属于哪种类型
     *
     * @param ch 传入的单个字符
     * @return 返回该传入字符属于的集合0代表操作数集合，1代表分隔符集合，2代表空格,3代表普通字符集合
     */
    public static int witchTypeSet(char ch) {
        if (operatorCharSet.contains(ch)) {
            return 0;
        } else if (splitterCharSet.contains(ch)) {
            return 1;
        } else if (' ' == ch) {
            return 2;
        } else {
            return 3;
        }
    }

}