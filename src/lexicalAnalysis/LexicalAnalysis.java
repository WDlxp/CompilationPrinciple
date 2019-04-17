package lexicalAnalysis;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author wdl
 */
class LexicalAnalysis {
    /**
     * 词法分析
     *
     * @param filePath            单词文件路径
     * @param regularFormFilePath 正规式文件路径
     * @param resultFilePath      结果输出文件路径
     * @param isPrint             是否打印过程
     */
    static void lexicalAnalysis(String filePath, String regularFormFilePath, String resultFilePath, boolean isPrint) throws IOException {

        //关键字集合
        HashSet keyWordSet = new HashSet() {{
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
        //标志符集合
        HashSet reservedwordset = new HashSet(){{
            add("goto");
            add("const");
        }};
        //标志符集合
        HashSet flagSet = new HashSet(){{
            add("null");
            add("true");
            add("false");
        }};

        //读取正规式文件和单词文件
        String readWordsFileString = readFile(filePath, " ");
        String readRegularFormString = readFile(regularFormFilePath, "\n");
        //分割字符,预处理文件内容
        String readWordsFileString1 = chuli(readWordsFileString);
        System.out.println("文件内容："+ readWordsFileString);
        System.out.println("处理后文件内容："+readWordsFileString1);

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
                String[] division = readWordsFileString1.split(" ");
                for (String cell : division) {
                    if (!cell.equals("")) {
                        boolean isRight = false;
                        System.out.println(cell);
                        if (keyWordSet.contains(cell)){
                            writer.append(cell + ": " + true + " Java关键字");
                            System.out.println("合法性判断结果： " + true +  " Java关键字");
                        }else if (reservedwordset.contains(cell)){
                            writer.append(cell + ": " + true + " Java保留字");
                            System.out.println("合法性判断结果： " + true +  " Java保留字");
                        }else if (flagSet.contains(cell)){
                            writer.append(cell + ": " + true + " Java标志符");
                            System.out.println("合法性判断结果： " + true +  " Java标志符");
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
                                writer.append(cell + ": " + isRight + " 符合第" + (i + 1) + "个正规式: " + regularFormList.get(i));
                                System.out.println("合法性判断结果： " + isRight + "  符合第" + (i + 1) + "个正规式: " + regularFormList.get(i));
                            } else {
                                writer.append(cell + ":" + isRight);
                                System.out.println("合法性判断结果： " + isRight);
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
    private static String chuli(String string) {
        HashSet yunSuanFuSet = new HashSet() {{
            add("=");
            add("+");
            add("-");
            add("<");
            add(">");
            add("/");
            add("(");
            add(")");
            add("{");
            add("}");
        }};
        HashSet fenGeFuSet = new HashSet() {{
            add(";");
            add(":");
            add("|");
            add("1");
        }};
        StringBuilder stringBuilder = new StringBuilder();
        boolean isyunsuanfu = true;
        StringBuilder same = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char op = string.charAt(i);
            if (yunSuanFuSet.contains(String.valueOf(op))) {
                //是运算符
                same.append(op);
                isyunsuanfu = true;
            }else if (fenGeFuSet.contains(String.valueOf(op))){
                stringBuilder.append(" ").append(op).append(" ");
            }else {
                //不是运算符
                if (isyunsuanfu){
                    //上一个是运算符
                    stringBuilder.append(" ").append(same.toString()).append(" ");
                    same.delete(0,same.length());
                    isyunsuanfu = false;
                }
                stringBuilder.append(op);
            }
        }
        return stringBuilder.toString();
    }
}