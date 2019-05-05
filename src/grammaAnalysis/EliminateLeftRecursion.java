package grammaAnalysis;

import java.util.HashSet;

/**
 * 2.消除左递归
 * 2.1消除显式左递归
 * 2.2隐式左递归转显式左递归：找到-->隐式-->显式调用2.1
 *
 * @author wdl
 */
public class EliminateLeftRecursion {
    public static void main(String[] args) {
        HashSet<Character> set = new HashSet<Character>() {{
            add('A');
            add('B');
            add('C');
            add('D');
            add('E');
            add('F');
            add('G');
        }};
        int[] charSet = new int[26];
        System.out.println(set);
        for (int i : charSet) {
            System.out.print(i + ",");
        }

        //标记已经使用过的字符
        for (char ch : set) {
            charSet[ch - 'A'] = 1;
        }
        System.out.println();
        for (int i : charSet) {
            System.out.print(i + ",");
        }
        System.out.println();
        //寻找一个未使用的字符
        int index = 0;
        for (; index < 26; index++) {
            if (charSet[index] == 0) {
                //使用后记得标记
                charSet[index]=1;
                set.add((char) (index+'A'));
                break;
            }
        }
        System.out.println("找到未使用过的字符：" + (char) ('A' + index));
        System.out.println(set);
    }
}
