public class Main {

    public static void main(String[] args) {
        String string = "(a b|b )*(ab|c.d)*";
        StringBuilder result = new StringBuilder();
        if (InfixToSuffix.change(string, result) == 0) {
            System.out.println(result);
        } else {
            System.out.println("输入有误");
        }
    }
}
