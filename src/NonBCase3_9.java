import java.io.IOException;

public class NonBCase3_9 extends NonBy9 {

    public static void main(String[] args){
        NonBCase1_9 nbve = new NonBCase1_9();
        try {
            initialProposers();
            startAccepter();
        }catch (Exception e){
            e.printStackTrace();
        }
        M2.setNeedBreak(true);
        M2.setVary(true);
        M3.setVary(true);
        M4.setVary(true);
        M5.setVary(true);
        M6.setVary(true);
        M7.setVary(true);
        M8.setVary(true);
        M9.setVary(true);

        nbve.voting(M1);
        nbve.voting(M2);
        nbve.voting(M3);

        try{
            Thread.sleep(50000);
            nbve.stop();
        }catch(InterruptedException | IOException e){
            e.printStackTrace();
        }
    }
}
