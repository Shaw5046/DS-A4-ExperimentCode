//***********************************************
public class BCase1_11 extends Vote11Proposer {

    public static long startTime;
    public static long initialSocketTime;
    public static long voteTime;

    public static void main(String[] args) throws InterruptedException{
        BCase1_11 bc1 = new BCase1_11();

        try{
            startTime = System.currentTimeMillis();
            bc1.initialProposers();
            startAcceptors();
            Thread.sleep(100);
            bc1.initialSockets();
            initialSocketTime = System.currentTimeMillis();
            vote(M1,"M1");
            vote(M2,"M2");
            vote(M3,"M3");
            voteTime = System.currentTimeMillis();

        }catch (Exception e){
            e.printStackTrace();
        }
        Thread.sleep(1000);

        bc1.displayValue();
        bc1.learnResult();
        System.out.println("initial time: " + (initialSocketTime - startTime)+"ms");
        System.out.println("vote time: " + (voteTime - initialSocketTime)+"ms");
    }
}
