
//**********************************************
public class NonBCase1_15 extends NonBy15 {
//**********************************************
    public static long startTime;
    public static long initialSocketTime;
    public static long voteTime;

    public static void main(String[] args) {
        NonBCase1_15 nbve = new NonBCase1_15();
        try{
            startTime = System.currentTimeMillis();
            initialProposers();
            startAccepter();
            Thread.sleep(100);
            initialSocketTime = System.currentTimeMillis();
            nbve.voting(M1);
            nbve.voting(M2);
            nbve.voting(M3);
            nbve.voting(M15);
            //============================


            Thread.sleep(1000);
            nbve.stop();
            voteTime = System.currentTimeMillis();
            System.out.println("initial time: " + (initialSocketTime - startTime)+"ms");
            System.out.println("vote time: " + (voteTime - initialSocketTime)+"ms");
        }catch (Exception e){
            System.out.println("finished.");
        }


    }
}
