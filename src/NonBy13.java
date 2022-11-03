
import java.io.IOException;


public class NonBy13 {

    protected static final String localhost = "0.0.0.0";
    protected static final long CURRENT_TIME_LEADING_NUM = 1602015000000L;
    protected static final String candidates[] = {"M1","M2","M3"};
    //**********************************************
    protected static final int[] portSet = {2181,2182,2183,2184,2185,2186,2187,2188,2189,2190,
            2191,2192,2193};
    //**********************************************

    //**********************************************
    protected static Proposer_13 M1;
    protected static Proposer_13 M2;
    protected static Proposer_13 M3;
    protected static Proposer_13 M4;
    protected static Proposer_13 M5;
    protected static Proposer_13 M6;
    protected static Proposer_13 M7;
    protected static Proposer_13 M8;
    protected static Proposer_13 M9;
    protected static Proposer_13 M10;
    protected static Proposer_13 M11;
    protected static Proposer_13 M12;
    protected static Proposer_13 M13;
    //**********************************************


    //**********************************************
    protected static final int M1Port = 2181;
    protected static final int M2Port = 2182;
    protected static final int M3Port = 2183;
    protected static final int M4Port = 2184;
    protected static final int M5Port = 2185;
    protected static final int M6Port = 2186;
    protected static final int M7Port = 2187;
    protected static final int M8Port = 2188;
    protected static final int M9Port = 2189;
    protected static final int M10Port = 2190;
    protected static final int M11Port = 2191;
    protected static final int M12Port = 2192;
    protected static final int M13Port = 2193;
    //**********************************************

    protected static void initialProposers(){
        System.out.println("Initialization:");
        //**********************************************
        M1 = new Proposer_13(M1Port,candidates[0]);
        M2 = new Proposer_13(M2Port,candidates[1]);
        M3 = new Proposer_13(M3Port,candidates[2]);
        M4 = new Proposer_13(M4Port,candidates[(int)(Math.random()*3)]);
        M5 = new Proposer_13(M5Port,candidates[(int)(Math.random()*3)]);
        M6 = new Proposer_13(M6Port,candidates[(int)(Math.random()*3)]);
        M7 = new Proposer_13(M7Port,candidates[(int)(Math.random()*3)]);
        M8 = new Proposer_13(M8Port,candidates[(int)(Math.random()*3)]);
        M9 = new Proposer_13(M9Port,candidates[(int)(Math.random()*3)]);
        M10 = new Proposer_13(M9Port,candidates[(int)(Math.random()*3)]);
        M11 = new Proposer_13(M9Port,candidates[(int)(Math.random()*3)]);
        M12 = new Proposer_13(M9Port,candidates[(int)(Math.random()*3)]);
        M13 = new Proposer_13(M9Port,candidates[(int)(Math.random()*3)]);
        //**********************************************
    }

    protected static void startAccepter(){

        new Thread(()-> {
            try {
                M1.startAcceptor(M1Port);
            } catch (Exception e) {
                System.out.println("close the socket");
            }
        }).start();

        new Thread(()-> {
            try {
                M2.startAcceptor(M2Port);
            } catch (Exception e) {
                System.out.println("close the socket");
            }
        }).start();

        new Thread(()-> {
            try {
                M3.startAcceptor(M3Port);
            } catch (Exception e) {
                System.out.println("close the socket");
            }
        }).start();

        new Thread(()-> {
            try {
                M4.startAcceptor(M4Port);
            } catch (Exception e) {
                System.out.println("close the socket");
            }
        }).start();

        new Thread(()-> {
            try {
                M5.startAcceptor(M5Port);
            } catch (Exception e) {
                System.out.println("close the socket");
            }
        }).start();

        new Thread(()-> {
            try {
                M6.startAcceptor(M6Port);
            } catch (Exception e) {
                System.out.println("close the socket");
            }
        }).start();

        new Thread(()-> {
            try {
                M7.startAcceptor(M7Port);
            } catch (Exception e) {
                System.out.println("close the socket");
            }
        }).start();

        new Thread(()-> {
            try {
                M8.startAcceptor(M8Port);
            } catch (Exception e) {
                System.out.println("close the socket");
            }
        }).start();

        new Thread(()-> {
            try {
                M9.startAcceptor(M9Port);
            } catch (Exception e) {
                System.out.println("close the socket");
            }
        }).start();

        new Thread(()-> {
            try {
                M10.startAcceptor(M10Port);
            } catch (Exception e) {
                System.out.println("close the socket");
            }
        }).start();

        new Thread(()-> {
            try {
                M11.startAcceptor(M11Port);
            } catch (Exception e) {
                System.out.println("close the socket");
            }
        }).start();

        new Thread(()-> {
            try {
                M12.startAcceptor(M12Port);
            } catch (Exception e) {
                System.out.println("close the socket");
            }
        }).start();
        //**********************************************
        new Thread(()-> {
            try {
                M13.startAcceptor(M13Port);
            } catch (Exception e) {
                System.out.println("close the socket");
            }
        }).start();
        //**********************************************


    }

    //**********************************************
    protected static void prepare(Proposer_13 proposer, long pidPrefix){
        //**********************************************
        for(int i=0; i< portSet.length; i++){
            int finalI = i;
            if(proposer.getPort() == portSet[finalI]){
                System.out.println(("In phase 1:" + portSet[i] + "prepare for voting" + proposer.getVotingName()));
                continue;
            }

            int finalI1 = i;
            new Thread(){
                @Override
                public synchronized  void  run(){
                    try{
                        proposer.prepare(localhost, portSet[finalI1],pidPrefix);
                        System.out.println("preparation *****************");
                    } catch (Exception e){
                        System.out.println("lost connection");
                    }
                }
            }.start();
        }
    }

    //**********************************************
    protected static void commit(Proposer_13 proposer, String candidate){
        //**********************************************
        for(int i =0; i<portSet.length; i++){
            int finalI = i;
            if(proposer.getPort() == portSet[finalI])
                continue;
            new Thread(){
                @Override
                public synchronized void  run(){
                    try {
                        proposer.commit(localhost, portSet[finalI], candidate);
                    }catch (IOException | InterruptedException e){
                        System.out.println("lost connection");
                    }
                }
            }.start();
        }
    }


    //**********************************************
    protected  void learn(Proposer_13 proposer){
        //**********************************************
        for(int i =0; i<portSet.length;i++){
            int finalI = i;
            if(proposer.getPort() == portSet[finalI])
                continue;
            new Thread(){
                @Override
                public synchronized void run(){
                    try{
                        proposer.tellToLearn(localhost,portSet[finalI]);
//                    }catch (IOException){
//                        System.out.println("lost connection");
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }.start();
        }
    }

    //**********************************************
    protected void voting(Proposer_13 counciler){
        //**********************************************
        new Thread(()->{
            long pidPrefix = System.currentTimeMillis() - CURRENT_TIME_LEADING_NUM;
            while (true){
                try{
                    prepare(counciler,pidPrefix);
                    Thread.sleep((long)(Math.random()*6000));

                    if(counciler.getAck_num() > portSet.length/2){
                        System.out.println("****************************************************");
                        System.out.println("**********************************M " + (counciler.getPort()-2180) + " has more than half promise");
                        System.out.println("****************************************************");
                        commit(counciler, counciler.getVotingName());
                        Thread.sleep((long) (Math.random()*3000));
                        if(counciler.getAcceptNum() > portSet.length/2){
                            System.out.println("learn stage:");
                            learn(counciler);
                        }
                        break;
                    }else {
                        pidPrefix = counciler.getPid()/1000 +1 ;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    protected void stop() throws IOException{
        //**********************************************
        M1.end();
        M2.end();
        M3.end();
        M4.end();
        M5.end();
        M6.end();
        M7.end();
        M8.end();
        M9.end();
        M10.end();
        M11.end();
        M12.end();
        M13.end();
        //**********************************************
    }


}
