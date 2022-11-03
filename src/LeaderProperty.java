public class LeaderProperty {
    String value = "null";
    int count =0;
    long pid = -1L;
    LeaderProperty(long pid, String value){
        this.pid = pid;
        this.value = value;
        this.count = 1;
    }
}
