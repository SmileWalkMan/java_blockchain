package model;
/*
交易输出数据结构
 */
public class TransactionOutput {
    private int value;
    private String publicKeyHash; //接收方钱包公钥哈希值
    public TransactionOutput(){
        super();
    }

    public TransactionOutput(int value, String publicKeyHash) {
        this.value = value;
        this.publicKeyHash = publicKeyHash;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getPublicKeyHash() {
        return publicKeyHash;
    }

    public void setPublicKeyHash(String publicKeyHash) {
        this.publicKeyHash = publicKeyHash;
    }
}
