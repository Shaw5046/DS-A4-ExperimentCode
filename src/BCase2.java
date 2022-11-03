import  java.io.IOException;

public class BCase2 extends Vote9Proposer {

    @Override
    protected void initialProposers() throws IOException{
        System.out.println("Initialization: ");
        M1 = new Fast9(M1Port);
        M2 = new Fast9(M2Port);
        M3 = new Fast9(M3Port);
        M4 = new Fast9(M4Port);
        M5 = new Fast9(M5Port);
        M6 = new Fast9(M6Port);
        M7 = new Fast9(M7Port, "M4", true);
        M8 = new Fast9(M8Port, "M4", true);
        M9 = new Fast9(M9Port, "M4", true);
        councilors = new Fast9[]{M1,M2,M3,M4,M5,M6,M7,M8,M9};
    }

    public static void main(String[] args) throws IOException{
        BCase2 bc2 =  new BCase2();
        try{
            bc2.initialProposers();
            startAcceptors();
            Thread.sleep(100);
            bc2.initialSockets();
            vote(M3,"M3");
            vote(M2,"M2");
        }catch (IOException | InterruptedException e){
            e.printStackTrace();
        }

//        Thread.sleep(1000);
        bc2.displayValue();
        bc2.learnResult();
    }

}
