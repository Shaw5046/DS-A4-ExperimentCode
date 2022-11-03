

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;

public class Fast13 {

    private int port;
    private int leaderPort = 2181;
    private boolean isCheating = false;
    private String cheatingValue = "";
    private boolean isDone = false;
    private boolean isLeader = false;
    private String acceptedValue = "null";
    private long acceptedProId = -1L;
    private String prepareValue = "null";
    private long prepareProId = -1L;
    private Long roundedNum = 200L;

    //******************************************
    private int acceptorsPort[] = new int[13];
    private Socket[] acceptors = new Socket[13];
    //******************************************

    private boolean isChecked = false;
    private Socket proposer = null;
    private Socket leader = null;
    private ServerSocket ss;
    private BufferedReader bufferedReader = null;
    private BufferedWriter bufferedWriter = null;

    public void setDone(boolean done) throws IOException{
        setIsDone(done);
        closeProposer();
        closeBuffer();
        closeServerSocket();
    }
    public void setIsDone(boolean done){
        isDone = done;
    }
    public void closeProposer() throws IOException {
        proposer.close();
    }
    public void closeBuffer() throws IOException {
        bufferedReader.close();
        bufferedWriter.close();
    }
    public void closeServerSocket() throws IOException {
        ss.close();
    }

    public boolean isChecked() { return  isChecked;}
    public boolean getIsLeader(){return  isLeader;}
    public int getPort(){return port;}
    public String getAcceptedValue(){return acceptedValue;}
    HashMap<Integer,String > acceptor_ports = new HashMap<>();
    private static final LinkedList<LeaderProperty> pokRecords = new LinkedList<>();

    public Fast13(int port, String cheatingValue, boolean isCheating){
        this.port = port;
        this.cheatingValue = cheatingValue;
        this.isCheating = isCheating;
        confSettings();
    }

    public Fast13(int port){
        this.port = port;
        if(leaderPort == port)
            isLeader = true;
        confSettings();
    }

    // ********************************************
    public void confSettings(){
        acceptor_ports.put(2181,"M1");
        acceptor_ports.put(2182,"M2");
        acceptor_ports.put(2183,"M3");
        acceptor_ports.put(2184,"M4");
        acceptor_ports.put(2185,"M5");
        acceptor_ports.put(2186,"M6");
        acceptor_ports.put(2187,"M7");
        acceptor_ports.put(2188,"M8");
        acceptor_ports.put(2189,"M9");
        acceptor_ports.put(2190,"M10");
        acceptor_ports.put(2191,"M11");
        acceptor_ports.put(2192,"M12");
        acceptor_ports.put(2193,"M13");
//*****************************************
        System.out.println(acceptor_ports.get(port)+" admit "+acceptor_ports.get(leaderPort) + " is leader. ");

        for(int i=0; i<acceptorsPort.length; i++){
            acceptorsPort[i] =2181+i;
        }
    }

    public void startup() throws IOException{
        ss = new ServerSocket(port);
        System.out.println("Accepter " + acceptor_ports.get(port) + "(" + port +"): is online. ");

        while (!isDone){
            proposer = ss.accept();
            bufferedReader = new BufferedReader(new InputStreamReader(proposer.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(proposer.getOutputStream()));
            String message = bufferedReader.readLine();
            System.out.println("Log:: Socket "+ port+" Receive: "+message +" from "+ proposer.getPort());
            convert(message, bufferedWriter);
        }
    }

    public void synchronize(){
        for (int i =1; i< acceptorsPort.length; i++){
            if(acceptorsPort[i] == port)
                continue;

            int finalI = i;
            new Thread(()->{
                try{
                    System.out.println(acceptor_ports.get(port) + " need to connect with " + acceptorsPort[finalI] + " to send synchronize");
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(acceptors[finalI].getOutputStream()));
                    bufferedWriter.write("synchronization");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                    BufferedReader bufferReader = new BufferedReader(new InputStreamReader(acceptors[finalI].getInputStream()));
                    String message = bufferReader.readLine();
                    System.out.println(acceptor_ports.get(port) + " get feedback: " + message );
                    String[] splitMessage = message.split("@");
                    if(splitMessage[0].equalsIgnoreCase("pok")){
                        System.out.println("leader " + acceptor_ports.get(port) + " receive " + splitMessage[1]);
                        recordPokFromAcceptors(splitMessage[1]);
                        checkMajority();
                    }
                 }catch (IOException e){
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public void initializeSocket(){
        for(int i=0; i<acceptors.length; i++){
            if(acceptorsPort[i] == port){
                continue;
            }
            try {
                acceptors[i] = new Socket("0.0.0.0", acceptorsPort[i]);
            }catch (IOException e){
                System.out.println("error::"+ acceptorsPort[i]);
                e.printStackTrace();
            }
        }
    }


    public void vote(String message){
        for(int i=1; i< acceptors.length; i++){
            int finalI = i;
            if(acceptorsPort[finalI] == port){
                continue;
            }

            new Thread(()->{
                try {
                    System.out.println("need to connect with " + acceptorsPort[finalI] + "to propose a proposal" + message);
                    OutputStreamWriter osw = new OutputStreamWriter(acceptors[finalI].getOutputStream());
                    BufferedWriter bufferedWriter = new BufferedWriter(osw);
                    bufferedWriter.write(message);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }).start();
        }
    }


    private synchronized  void convert(String message, BufferedWriter bw) throws IOException{
        System.out.println("Accepter " + acceptor_ports.get(port) + ": received: "+ message);
        if(message == null)
            return;
        if(message.equalsIgnoreCase("synchronization")){
            System.out.println("reply to synchronization ");
            String messageForLeader = "";
            if(isCheating)
                message = "pok@" + acceptedProId + "," + cheatingValue;
            else
                message = "pok@" + acceptedProId + "," + acceptedValue;

            bw.write(message);
            bw.newLine();
            bw.flush();
            return;
        }

        String[] splitMessage = message.split("@");
        if(splitMessage[0].equalsIgnoreCase("pre")){
            String[] Message =  splitMessage[1].split(",");
            System.out.println(acceptor_ports.get(this.port) + ": " + this.acceptedValue + ":" + message);
            if(isCheating){
                this.acceptedValue = cheatingValue;
                this.prepareProId = Long.parseLong(Message[0]);
                if(prepareProId >= acceptedProId){
                    this.acceptedProId = prepareProId;
                }
            }
            else{
                if(acceptedValue.equalsIgnoreCase("null")){
                    accept(splitMessage[1]);
                }
                else{
                    acceptClassic(splitMessage[1]);
                }
            }
            sync();
        }
        else if(splitMessage[0].equalsIgnoreCase("any")){
            if(isCheating)
                this.acceptedValue = cheatingValue;
            else{
                this.acceptedProId = roundedNum;
                if(!this.prepareValue.equalsIgnoreCase("null")){
                    this.acceptedValue = this.prepareValue;
                }
                else {
                    this.acceptedValue = "any";
                }
            }
        }
        else if(splitMessage[0].equalsIgnoreCase("acc")){
            String[] idPairValue = splitMessage[1].split(",");
            System.out.println("log:: " + splitMessage[1] + "," + idPairValue.length);
            if(isCheating)
                this.acceptedValue = this.cheatingValue;
            else{
                this.acceptedValue = idPairValue[1];
            }
            sync();
        }

        else if(splitMessage[0].equalsIgnoreCase("check")){
            checkup(splitMessage[1]);
        }
    }

    private synchronized void checkup(String message){
        LinkedList<ValueResult> vr = new LinkedList<ValueResult>();
        vr.add(new ValueResult(message));
    }

    private synchronized void acceptClassic(String message){
        String[] splitMessage = message.split(",");
        prepareProId =  Long.parseLong(splitMessage[0]);
        if(prepareProId > acceptedProId)
            acceptedValue = splitMessage[1];
    }

    private  synchronized void accept(String message){
        String[] splitMessage;
        splitMessage = message.split(",");
        prepareProId = Long.parseLong(splitMessage[0]);
        if(prepareProId >= acceptedProId)
            acceptedProId = prepareProId;
        else
            return;
        acceptedValue = splitMessage[1];
    }



    private synchronized  void checkMajority(){
        for (LeaderProperty pokRecord : pokRecords) {
            if (pokRecord.count >= 4 && !isChecked) {
                isChecked = true;
                feedbackToAcceptorsFromLeader();
                break;
            }
        }
    }

    private synchronized  void feedbackToAcceptorsFromLeader(){
        for(int i=0; i<acceptorsPort.length; i++){
            if(acceptorsPort[i] == port)
                continue;
            int finalI =i;
            new Thread(()->{
                try{
                    String output;
                    if(acceptedValue.equalsIgnoreCase("null"))
                        output = "any@" + acceptedProId + ",null";
                    else
                        output = "acc@" + acceptedProId + "," + acceptedValue;

                    System.out.println(acceptor_ports.get(port) + " send  to M" + (finalI+1)+ "(" + acceptors[finalI].getPort() + "):" + output);
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(acceptors[finalI].getOutputStream()));
                    bufferedWriter.write(output);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    System.out.println("send ending");
                }catch (IOException e){
                    e.printStackTrace();
                }
            }).start();
        }
    }



    private synchronized  void recordPokFromAcceptors(String splitMessage){
        String[] idValuePair = splitMessage.split(",");
        prepareProId = Long.parseLong(idValuePair[0]);
        if(prepareProId >= acceptedProId && !idValuePair[1].equalsIgnoreCase("null"))
            acceptedValue = idValuePair[1];

        System.out.println("leader " + port + "'s accepted value is " + acceptedValue);
        boolean found = false;
        if(pokRecords.size()>0){
            for(LeaderProperty x: pokRecords){
                if(x.value.equalsIgnoreCase(idValuePair[1])){
                    found = true;
                    x.count ++;
                    break;
                }
            }
        }
        if(!found)
            pokRecords.add(new LeaderProperty(Long.parseLong(idValuePair[0]), idValuePair[1]));

    }

    private synchronized void sync(){
        for(int i =0; i< acceptorsPort.length; i++){
            if(acceptorsPort[i] == port)
                continue;
            int finalI = i;
            new Thread(()->{
                try {
                    System.out.println(acceptor_ports.get(port) + " sync (" + acceptedProId +") to M" +(finalI+1) );
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(acceptors[finalI].getOutputStream()));
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }).start();
        }
    }

}
