import java.io.IOException;

public class BCase1_15 extends Vote15Proposer {

    public static long startTime;
    public static long initialSocketTime;
    public static long voteTime;

//    @Override
//    protected void initialProposers() throws IOException {
//        System.out.println("Initialization: ");
//        M1 = new Fast15(M1Port);
//        M2 = new Fast15(M2Port);
//        M3 = new Fast15(M3Port);
//        M4 = new Fast15(M4Port);
//        M5 = new Fast15(M5Port);
//        M6 = new Fast15(M6Port);
//        M7 = new Fast15(M7Port, "M9", true);
//        M8 = new Fast15(M8Port, "M9", true);
//        M9 = new Fast15(M9Port, "M9", true);
//        M10 = new Fast15(M9Port, "M9", true);
//        M11 = new Fast15(M9Port, "M9", true);
//        M12 = new Fast15(M9Port, "M9", true);
//        M13 = new Fast15(M9Port, "M9", true);
//        M14 = new Fast15(M9Port, "M9", true);
//        M15 = new Fast15(M9Port, "M9", true);
//        councilors = new Fast15[]{M1,M2,M3,M4,M5,M6,M7,M8,M9,M10,M11,M12,M13,M14,M15};
//    }

    public static void main(String[] args) throws InterruptedException{
        BCase1_15 bc1 = new BCase1_15();

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
            vote(M9, "M9");

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
