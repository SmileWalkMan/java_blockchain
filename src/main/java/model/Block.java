package model;
/*
    the block data structure
 */


import java.util.List;

public class Block {
    private int index; //索引
    private String hash;  //当前区块哈希值
    private long timestamp; //生成区块的时间戳
    private List<Transaction> transactions; //当前区块交易集合
    private int nonce; //工作量证明
    private String previousHash; //前一个区块的哈希

    public Block(){
        super();
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public Block(int index, long timestamp, List<Transaction> transactions, int nonce, String previousHash, String hash) {
        this.index = index;
        this.hash = hash;
        this.timestamp = timestamp;
        this.transactions = transactions;
        this.nonce = nonce;
        this.previousHash = previousHash;
    }
}
