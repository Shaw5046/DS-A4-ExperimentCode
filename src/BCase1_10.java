//***********************************************
public class BCase1_10 extends Vote10Proposer {

    public static long startTime;
    public static long initialSocektTime;
    public static long voteTime;

    public static void main(String[] args) throws InterruptedException{
        BCase1_10 bc1 = new BCase1_10();

        try{
            startTime = System.currentTimeMillis();
            bc1.initialProposers();
            startAcceptors();
            Thread.sleep(100);
            bc1.initialSockets();
            initialSocektTime = System.currentTimeMillis();
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
        System.out.println("initial time: " + (initialSocektTime - startTime)+"ms");
        System.out.println("vote time: " + (voteTime - initialSocektTime)+"ms");
    }
}
