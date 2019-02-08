package model;

/*
交易参数数据结构
 */
public class TransactionParam {
    private String sender;  //发送方钱包地址
    private String recipient;  //接收方钱包地址
    private int amount;  //发送金额

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
