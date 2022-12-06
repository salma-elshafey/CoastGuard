import java.util.Comparator;
import java.util.PriorityQueue;

public class testQ {
    String num;

    public int getNum() {
        return Integer.parseInt(num.split(",")[0]);
    }
    public int getNum2() {
        return Integer.parseInt(num.split(",")[1]);
    }

    public testQ(String num) {
        this.num = num;
    }
    public String toString(){
        return  num;
    }

    public static void main(String[] args) {
        testQ n1 = new testQ("3,2");
        testQ n2 = new testQ("3,1");
        testQ n3 = new testQ("3,1");
        testQ n4 = new testQ("3,10");

        Comparator<testQ> heuristicOrder = Comparator.comparing(testQ::getNum).thenComparing(testQ::getNum2);
        PriorityQueue<testQ> q = new PriorityQueue<>( heuristicOrder );
        q.add(n1);
        System.out.println(q.toString());

        q.add(n2);
        System.out.println(q.toString());

        q.add(n4);
        System.out.println(q.toString());

        q.add(n3);
        System.out.println(q.toString());

        testQ p;
        while((p = q.poll()) != null)
            System.out.println(p);
    }
}
