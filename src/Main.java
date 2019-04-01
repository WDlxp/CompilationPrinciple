import java.util.HashSet;

public class Main {

    public static void main(String[] args) {
        boolean[] isFinishState=new boolean[10];
        isFinishState[0]=true;
        isFinishState[1]=isFinishState[1]||isFinishState[0];
        for (boolean i:isFinishState){
            System.out.println(i);
        }
        HashSet<Integer> hashSet=new HashSet();
        hashSet.add(1);
        hashSet.add(2);
        hashSet.add(3);

        HashSet<Integer> hashSet1=new HashSet();

        hashSet1.add(1);

        System.out.println(hashSet.equals(hashSet1));
        System.out.println(hashSet.size()==hashSet1.size());
        System.out.println(hashSet.contains(1));
    }
}
