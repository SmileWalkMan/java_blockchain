package p2p;

/*
p2p消息类
 */


public class Message {
    private int type;
    private String data;
    public Message(){ }
    public Message(int type){
        this.type=type;
    }
    public Message(int type,String data){
        this.type=type;
        this.data=data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
