
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.util.LinkedList;

public class Vote9Proposer {
    protected static Fast9 M1;
    protected static Fast9 M2;
    protected static Fast9 M3;
    protected static Fast9 M4;
    protected static Fast9 M5;
    protected static Fast9 M6;
    protected static Fast9 M7;
    protected static Fast9 M8;
    protected static Fast9 M9;

    protected static final int M1Port = 2181;
    protected static final int M2Port = 2182;
    protected static final int M3Port = 2183;
    protected static final int M4Port = 2184;
    protected static final int M5Port = 2185;
    protected static final int M6Port = 2186;
    protected static final int M7Port = 2187;
    protected static final int M8Port = 2188;
    protected static final int M9Port = 2189;
    protected static int leaderport = 0;

    protected static final String localhost = "0.0.0.0";
    protected static final long CURRENT_TIME_LEADING_NUM = 1602015000000L;
    protected static final String candidates[] = {"M1", "M2", "M3"};
    protected static final int[] portSet = {M1Port, M2Port, M3Port, M4Port, M5Port, M6Port, M7Port, M8Port, M9Port};

    public static long startTest;
    public static long endTime;


    protected static   Fast9[] councilors;
    protected void initialProposers() throws IOException{
        System.out.println("Initialization:");
        M1 = new Fast9(M1Port);
        M2 = new Fast9(M2Port);
        M3 = new Fast9(M3Port);
        M4 = new Fast9(M4Port);
        M5 = new Fast9(M5Port);
        M6 = new Fast9(M6Port);
        M7 = new Fast9(M7Port);
        M8 = new Fast9(M8Port);
        M9 = new Fast9(M9Port);
        councilors = new Fast9[]{M1,M2,M3,M4,M5,M6,M7,M8,M9};
    }

    protected static void startAcceptors(){
        for(int i =0; i< councilors.length; i++){
            int finalI = i;
            new Thread(()->{
                try{
                    System.out.println("M"+(finalI + 1) + " is starting");
                    councilors[finalI].startup();
                }catch (ConnectException e){
                    e.printStackTrace();
                }catch (SocketException e){
                    return;
                }catch (IOException e){
                    System.out.println("there is an error in"+ councilors[finalI].acceptor_ports.get(councilors[finalI].getPort()));
                    e.printStackTrace();
                }
            }).start();
        }
    }

    protected void initialSockets(){
        for(int i=0; i< councilors.length; i++){
            councilors[i].initializeSocket();
        }
        for(int i =0; i<councilors.length; i++){
            if(councilors[i].getIsLeader()){
                leaderport = i;
                councilors[i].synchronize();
                break;
            }
        }
    }

    protected static void vote(Fast9 M, String candidate){
        long pidPrefix = System.currentTimeMillis() - CURRENT_TIME_LEADING_NUM;
        String pid = String.valueOf(pidPrefix);
        pid = pid + M.getPort();
        String message = "pre@" + pid + "," + candidate;
        System.out.println(M.getPort() + "will send " + message);
        M.vote(message);
    }

    protected void displayValue(){
        for(Fast9 fc:councilors){
            System.out.println(fc.getAcceptedValue());
        }
    }


    protected void  learnResult(){
        LinkedList<ResultCollection> res = new LinkedList<>();
        for(int i =0;i<councilors.length; i++){
            boolean isFound = false;
            for(int j=0; j<res.size();j++){
                isFound =  false;
                if(res.get(j).value.equalsIgnoreCase(councilors[i].getAcceptedValue())) {
                    res.get(j).count++;
                    if (res.get(j).count > 4) {
                        System.out.println("The result is " + res.get(j).value);
                        gameOver();
                        return;

                    }
                }
            }
            if(!isFound){
                res.add((new ResultCollection(councilors[i].getAcceptedValue())));
            }
        }
        System.out.println("No consistency.");
        gameOver();
    }

    protected void gameOver(){
        for(int i=0; i<councilors.length; i++){
            try{
                councilors[i].setDone(true);

            }catch (IOException e){
                System.out.println("see you!");
            }
        }
    }

}




















