import java.io.*;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class Proposer_10 {
    private boolean isVary = false;
    private static final int CONNECTION_DELAY = 7000;
    private static final int RETRY_TIMES = 5;

    private int port;
    private long pid;
    private String votingName = "";
    private String candidataValue = "";
    private String loacalhost = "0.0.0.0";
    private int ack_num = 0;
    private int acceptNum = 0;
    private long acceptPid = -1L;
    private Map<Integer, String> port_acceptor = new HashMap<>();
    private Socket proposer = null;
    private ServerSocket ss = null;
    private boolean needBreak = false;

    Proposer_10(int port, String votingName){
        this.port = port;
        this.votingName = votingName;
        confSettings();
    }


    public  void setNeedBreak(boolean needBreak) { this.needBreak = needBreak;}
    public String getVotingName(){return this.votingName;}

    public String getCandidataValue(){return this.candidataValue;}

    public void setVary(boolean isVary){this.isVary = isVary;}
    public  int getAck_num(){return this.ack_num;}

    public long getPid(){return pid;}
    public int getPort(){return port;}
    public int getAcceptNum(){return acceptNum;}

    public void confSettings(){
        //**********************************************
        port_acceptor.put(2181,"M1");
        port_acceptor.put(2182,"M2");
        port_acceptor.put(2183,"M3");
        port_acceptor.put(2184,"M4");
        port_acceptor.put(2185,"M5");
        port_acceptor.put(2186,"M6");
        port_acceptor.put(2187,"M7");
        port_acceptor.put(2188,"M8");
        port_acceptor.put(2189,"M9");
        port_acceptor.put(2190,"M10");
        //**********************************************
    }

    public void end() throws IOException{
        proposer.close();
        ss.close();
    }

    public void startAcceptor(int port) throws IOException,InterruptedException{
        File backup = new File(port_acceptor.get(port) + ".txt");
        if(backup.exists()){
            FileReader fw = new FileReader(backup);
            BufferedReader br = new BufferedReader(fw);

            String backupMessage = "";
            backupMessage = br.readLine();
            if(backupMessage.length() > 0){
                if(backupMessage.contains(",")){
                    String proposal[] = backupMessage.split(",");
                    acceptPid = Long.parseLong(proposal[0]);
                    candidataValue = proposal[1];
                } else{
                    acceptPid = Long.parseLong(backupMessage);
                }
            }
        }

        ss = new ServerSocket(port);

        DataOutputStream dos = null;
        DataInputStream dis = null;

        while(true){
            proposer = ss.accept();
            dis = new DataInputStream(proposer.getInputStream());
            String message = dis.readUTF();
            PrepareOrCommit(proposer,message);
            if(needBreak){
                proposer.close();
                ss.close();
                break;
            }
        }
    }


    public void prepare(String acceptIp, int acceptPort, long idPrefix) throws IOException,InterruptedException{
        Socket proposer = null;
        String proposerName = "M" + (port-2188);
        String acceptorName = "M" + (acceptPort-2188);
        System.out.println("Proposer " + proposerName + ": broadcast learning message to " + acceptPort);
        System.out.println("acceptorName  "+acceptorName);
        int tryTime = 0;

        while (tryTime < RETRY_TIMES){
            tryTime++;
            try{
                proposer = new Socket(acceptIp, acceptPort);
            } catch (ConnectException e){
                //e.printStackTrace();
                System.out.println(proposerName + " preparation cannot connect to server proposer 114");
                Thread.sleep(3000);
                continue;
            }
            break;
        }

        if(proposer == null){
            System.out.println(proposerName + ":: connection is fail");
            return;
        }

        DataOutputStream bdos = new DataOutputStream(proposer.getOutputStream());
        DataInputStream bdis = new DataInputStream(proposer.getInputStream());
        String proposalNum = String.valueOf(idPrefix)+port;
        pid = Long.parseLong(proposalNum);
        bdos.writeUTF(proposalNum);
        bdos.flush();
        String recipe = "";

        try {
            recipe = bdis.readUTF();
        }catch (Exception e){
            System.out.println("connection is failed");
        }

        if(recipe.length() == 0)
            return;
        else if(recipe.equalsIgnoreCase("promise")){
            ack_num++;
        }
        else if(recipe.contains(",")){
            ack_num++;
            String pidCanPair[] = recipe.split(",");
            if(Long.parseLong(pidCanPair[0]) >= acceptPid){
                votingName = pidCanPair[1];
                System.out.println("Proposer" + proposerName + "change the votingName to :" + votingName);

            }
        }
        else {
            pid = Math.max(pid, Long.parseLong(recipe));
            System.out.println("In preparing Phase: Propser" + proposerName + "need to update the proposal number");
        }
    }

    public void commit(String acceptIp, int acceptPort, String votingName) throws IOException,InterruptedException{
        int tryTime = 0;
        Socket proposer = null;

        File backup = new File(String.valueOf(port_acceptor.get(port))+".txt");
        if(!backup.exists())
            backup.createNewFile();

        FileWriter fw = new FileWriter(backup);
        BufferedWriter bw = new BufferedWriter(fw);
        candidataValue = votingName;
        acceptPid = pid;

        String proposerName = "M"+(port-2180);
        String accepterName = "M"+(acceptPort-2180);

        while (tryTime < RETRY_TIMES){
            tryTime ++;
            try {
                proposer = new Socket(acceptIp,acceptPort);
            }catch (Exception e){
                System.out.println(proposerName + " committing:: is fail to connect to "+ accepterName);
                System.out.println("Retry in 3 second");
                Thread.sleep(3000);
                continue;
            }
            break;
        }
        if(proposer == null){
            System.out.println(proposerName + "connection is fail");
            return;
        }

        DataOutputStream bdos = new DataOutputStream(proposer.getOutputStream());
        DataInputStream bdis = new DataInputStream(proposer.getInputStream());
        bdos.writeUTF(votingName+","+pid);
        bdos.flush();
        String reply = bdis.readUTF();

        if(reply.equalsIgnoreCase("Ack")){
            acceptNum++;
        }else if(reply.contains(",")){
            String pid_cand[] = reply.split(",");
            if(Long.parseLong(pid_cand[0]) > acceptPid){
                acceptPid = Long.parseLong(pid_cand[0]);
                candidataValue = pid_cand[1];
                System.out.println("Proposer " + proposerName + "change the value to :" +candidataValue);
            }
        }
        else{
            System.out.println("In commit phase: Proposer " + proposerName + "need to update the proposal number");
        }

        bw.write(acceptPid + "," + candidataValue);
        bw.close();
        fw.close();

    }



    private  void PrepareOrCommit(Socket proposer, String message) throws IOException,InterruptedException{
        System.out.println("Accepter" + port_acceptor.get(port) + ": received" + message);
        if(message.contains(",")){
            commitStage(proposer,message);
        }else if(message.contains(":"))
            learnValue(message);
        else{
            prepareStage(proposer,message);
        }
    }



    private void prepareStage(Socket proposer, String message) throws IOException,InterruptedException{
        File backup = new File(String.valueOf(port_acceptor.get(port))+".txt");
        if(!backup.exists())
            backup.createNewFile();

        FileWriter fw = new FileWriter(backup);
        BufferedWriter bw = new BufferedWriter(fw);
        DataOutputStream data_out = new DataOutputStream(proposer.getOutputStream());
        long comingPid = Long.parseLong(message);
        System.out.print(message + "------->");

        if(isVary && port >= 2181){
            Thread.sleep((int)(CONNECTION_DELAY + Math.random()));
        }

        if(comingPid > acceptPid){
            if(candidataValue.length() == 0){
                acceptPid = comingPid;
                bw.write(String.valueOf(acceptPid));
                data_out.writeUTF("promise");
            }else{
                acceptPid = comingPid;
                bw.write(acceptPid + "," + candidataValue);
                data_out.writeUTF(pid + "," + candidataValue);
            }
            bw.close();
            fw.close();
        }
        else
            data_out.writeUTF(String.valueOf(acceptPid));
    }

    private synchronized void commitStage(Socket proposer, String message) throws IOException,InterruptedException{
        File backup = new File(port_acceptor.get(port) +".txt");
        if(!backup.exists())
            backup.createNewFile();

        FileWriter fw = new FileWriter(backup);
        BufferedWriter bw = new BufferedWriter(fw);

        DataOutputStream data_out = new DataOutputStream(proposer.getOutputStream());
        String[] canIdPari = message.split(",");

        if(isVary && port >= 2181){
            Thread.sleep((int) (CONNECTION_DELAY + Math.random()));
        }

        System.out.println("Accepter" + port_acceptor.get(port) + "is comparing " + message);

        if(pid <= Long.parseLong(canIdPari[1])){
//            if(candidataValue.length()==0){
                pid = Long.parseLong(canIdPari[1]);
                acceptPid = pid;
                candidataValue = canIdPari[0];

                bw.write(pid + "," + candidataValue);
                data_out.writeUTF("Ack");
//            }
        }
        else{
            data_out.writeUTF(String.valueOf(pid));
        }

        bw.close();
        fw.close();
    }


    public void tellToLearn(String acceptIp, int acceptPort) throws IOException,InterruptedException{
        Socket proposer = null;
        String proposerName = "M" + (port-2180);
        String acceptorName = "M" + (acceptPort-2180);
        System.out.println("Proposer " + proposerName + ": broadcast learning message to " + acceptPort);
        int tryTime = 0;

        while (tryTime < RETRY_TIMES){
                tryTime++;
                try{proposer = new Socket(acceptIp, acceptPort);}
                catch (ConnectException e){
                    Thread.sleep(3000);
                    continue;
                }
                break;
        }


        if(proposer == null){
            System.out.println(proposerName + ":: connection is fail");
            return;
        }
        DataOutputStream bdos = new DataOutputStream(proposer.getOutputStream());
        bdos.writeUTF(acceptPid + ":" + candidataValue);
    }







    private void learnValue(String message) throws IOException{
        File backup = new File(String.valueOf(port_acceptor.get(port))+".txt");
        if(!backup.exists())
            backup.createNewFile();

        FileWriter fw = new FileWriter(backup);
        BufferedWriter bw = new BufferedWriter(fw);

        String [] canIdpari = message.split(":");
        pid = Long.parseLong(canIdpari[0]);
        acceptPid = pid;
        candidataValue = canIdpari[1];
        bw.write(pid + "," + candidataValue);
        bw.close();
        fw.close();
    }


}


















