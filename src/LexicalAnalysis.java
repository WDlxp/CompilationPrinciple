import java.io.*;

/**
 * @author wdl
 */
public class LexicalAnalysis {
    /**
     * 词法分析
     *
     * @param filePath            单词文件路径
     * @param regularFormFilePath 正规式文件路径
     * @param resultFilePsth      结果输出文件路径
     * @param isPrint             是否打印过程
     */
    static void lexicalAnalysis(String filePath, String regularFormFilePath, String resultFilePsth,boolean isPrint) throws IOException {
        String readWordsFileString = null;
        String readRegularFormString = null;
        try {
            readWordsFileString = readFile(filePath);
            readRegularFormString = readFile(regularFormFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Result result = returnMiniDFA(readRegularFormString, isPrint);
        if (result.isTrue()) {
            // 构建FileOutputStream对象,文件不存在会自动新建
            FileOutputStream fop = new FileOutputStream(resultFilePsth);
            // 构建OutputStreamWriter对象,参数可以指定编码,默认为操作系统默认编码,windows上是gbk
            OutputStreamWriter writer = new OutputStreamWriter(fop, "UTF-8");

            // 构建FileOutputStream对象,文件不存在会自动新建
            int[][] miniDFA = result.getMiniDFA();
            char[] characters = result.getCharacters();
            assert readWordsFileString != null;
            if (!readWordsFileString.isEmpty()) {
                System.out.println("文件内容为:" + readWordsFileString);
                String[] division = readWordsFileString.split(" ");
                for (String cell : division) {
                    boolean isRight = legitimacy(miniDFA, cell, characters);
                    System.out.println("合法性判断结果：" + isRight);
                    // 写入到缓冲区
                    writer.append(cell+":"+isRight);
                    // 换行
                    writer.append("\r\n");
                }
            } else {
                System.out.println("文件内容为空！");
            }
            // 刷新缓存冲,写入到文件,如果下面已经没有写入的内容了,直接close也会写入
            writer.close();
        } else {
            System.out.println("输入有误");
        }
    }

    /**
     * 读取文件内容
     *
     * @param filePath 文件路径
     * @return 文件内容
     */
    private static String readFile(String filePath) throws IOException {
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
//            sb.append(s).append("\n");
            sb.append(s);
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
}
