public class Main {

    public static void main(String[] args) {
        boolean[] isFinishState=new boolean[10];
        isFinishState[0]=true;
        isFinishState[1]=isFinishState[1]||isFinishState[0];
        for (boolean i:isFinishState){
            System.out.println(i);
        }
    }
}
