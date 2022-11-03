import java.io.IOException;

public class NonBCase2_9 extends NonBy9 {

    public static void main(String[] args){
        NonBCase1_9 nbve = new NonBCase1_9();

        try{
            initialProposers();
            startAccepter();
        }catch (Exception e){
            e.printStackTrace();
        }
        M2.setNeedBreak(true);

        nbve.voting(M1);
        nbve.voting(M2);
        nbve.voting(M3);

        try{
            Thread.sleep(30000);
            nbve.stop();
        }catch (InterruptedException | IOException e){
            e.printStackTrace();
        }
    }
}
