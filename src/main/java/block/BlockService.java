package block;
import com.alibaba.fastjson.JSON;
import com.sun.javafx.css.CalculatedValue;
import model.*;
import security.CryptoUtil;

import javax.xml.transform.TransformerConfigurationException;
import java.util.*;

public class BlockService {
    private List<Block> blockChain=new ArrayList<Block>(); //区块链结构
    private Map<String, Wallet> myWalletMap=new HashMap<>();//当前节点钱包集合
    private Map<String,Wallet> otherWalletMap=new HashMap<>();//其他钱包节点

    /*
        交易集合
     */
    private List<Transaction> allTransactions=new ArrayList<>();
    /*
        已打包转账交易
     */
    private  List<Transaction> packedTransactions=new ArrayList<>();

    public BlockService(){
        Block genesisBlock=new Block(1,System.currentTimeMillis(),new ArrayList<Transaction>(),1,"1","1");
        blockChain.add(genesisBlock);
        System.out.println("生成创世区块:"+ JSON.toJSONString(genesisBlock));
    }
    /*
    获取最新的区块 链上的最后一个区块
     */
    public Block getLatestBlock(){
        return blockChain.size()>0?blockChain.get(blockChain.size()-1):null;
    }

    /*
    添加新区块
     */
    public boolean addBlock(Block newBlock){
        if(isValidNewBlock(newBlock,getLatestBlock())){
            blockChain.add(newBlock);
            packedTransactions.addAll(newBlock.getTransactions());
            return true;
        }
        return false;
    }


    /*
    验证新区快是否有效
     */
    public boolean isValidNewBlock(Block newBlock,Block previousBlock){
        if(!previousBlock.getHash().equals(newBlock.getPreviousHash())){
            System.out.println("新区块的前一个区块hash验证不通过");
            return false;
        }else{
            String hash= calculatedHash(newBlock.getPreviousHash(),newBlock.getTransactions(),newBlock.getNonce());
            if(!hash.equals(newBlock.getHash())){
                System.out.println("新区块的hash无效:"+hash+" "+newBlock.getHash());
                return false;
            }
            if(!isValidHash(newBlock.getHash())){
                return false;
            }
        }
        return true;
    }
    /*
    验证整个区块链是否有效
     */
    private boolean isValidChain(List<Block> chain){
        Block block=null;
        Block lastBlock=chain.get(0);
        int currentIndex=1;
        while(currentIndex<chain.size()){
            block=chain.get(currentIndex);
            if(!isValidNewBlock(block,lastBlock)){
                return false;
            }
            lastBlock=block;
            currentIndex++;
        }
        return true;
    }
    /*
    替换本地区块链
     */
    public void replaceChain(List<Block> newBlocks){
        if(isValidChain(newBlocks)&&newBlocks.size()>blockChain.size()){
            blockChain=newBlocks;
            //更新已打包交易集合
            packedTransactions.clear();
            blockChain.forEach(block ->{
                packedTransactions.addAll(block.getTransactions());
            } );
        }else{
            System.out.println("接收的区块链无效");
        }
    }
/*
创建新的区块
 */
    private Block createNewBlock(int nonce,String previousHash,String hash,List<Transaction> blockTxs){
        Block block =new Block(blockChain.size()+1,System.currentTimeMillis(),blockTxs,nonce,previousHash,hash);
           if(addBlock(block)){
            return block;
        }
           return null;
    }
    /*
    验证hash值是否满足系统条件
     */
    private boolean isValidHash(String hash){
        return hash.startsWith("0000");
    }
    /*
    计算区块的hash值
     */
    private String calculatedHash(String previousHash, List<Transaction> currentTransactions, int nonce) {
        return CryptoUtil.SHA256(previousHash+JSON.toJSONString(currentTransactions)+nonce);
    }

    /*
      挖矿函数
     */
    public Block mine(String toAddress){
        allTransactions.add(newCoinbaseTx(toAddress));
        List<Transaction> blockTxs=new ArrayList<Transaction>(allTransactions);
        blockTxs.removeAll(packedTransactions);
        verifyAllTransactions(blockTxs);
        String newBlockHash="";
        int nonce=0;
        long start=System.currentTimeMillis();
        System.out.println("开始挖矿");
        while(true){
            //计算新区块的hash值
            newBlockHash=calculatedHash(getLatestBlock().getHash(),blockTxs,nonce);
            if(isValidHash(newBlockHash)){
                System.out.println("挖矿完成，正确的hash值："+newBlockHash);
                System.out.println("挖矿耗时："+(System.currentTimeMillis()-start)+"ms");
                break;
            }
            System.out.println("错误的hash值："+newBlockHash);
            nonce++;
        }
        Block block=createNewBlock(nonce,getLatestBlock().getHash(),newBlockHash,blockTxs);
        return block;
    }
    /*
    验证所有交易是否有效
     */
    private void verifyAllTransactions(List<Transaction> blockTxs) {
        List<Transaction> invalidTxs=new ArrayList<>();
        for(Transaction tx:blockTxs){
            if(!verifyTransactions(tx)){
                invalidTxs.add(tx);
            }
        }
        blockTxs.removeAll(invalidTxs);
        //去除无效的交易
        allTransactions.removeAll(invalidTxs);
    }
    /*
    生成区块的奖励交易
     */
    private Transaction newCoinbaseTx(String toAddress) {
        TransactionInput txIn=new TransactionInput("0",-1,null,null);
        Wallet wallet =myWalletMap.get(toAddress);
        //指定生成区块奖励位10btc
        TransactionOutput txOut=new TransactionOutput(10,wallet.getHashPubKey());
        return new Transaction(CryptoUtil.UUID(),txIn,txOut);
    }


    /*
    创建交易
     */
     public Transaction createTransaction(Wallet senderWallet,Wallet recipientWallet,int amount){
            List<Transaction> unspentTxs=findUnspentTransactions(senderWallet.getAddress());
            Transaction prevTx=null;
            for(Transaction transaction:unspentTxs){
                if(transaction.getTxOut().getValue()==amount){
                    prevTx=transaction;
                    break;
                }
            }
            if(prevTx==null){
                return null;
            }
            TransactionInput txIn=new TransactionInput(prevTx.getId(),amount,null,senderWallet.getPublicKey());
            TransactionOutput txOut=new TransactionOutput(amount,recipientWallet.getHashPubKey());
            Transaction transaction =new Transaction(CryptoUtil.UUID(),txIn,txOut);
            transaction.sign(senderWallet.getPrivateKey(),prevTx);
            allTransactions.add(transaction);
            return transaction;
     }


    /*
    查找未被消费的交易
     */
    private List<Transaction> findUnspentTransactions(String address) {
        List<Transaction> unspentTxs=new ArrayList<Transaction>();
        Set<String> spentTxs =new HashSet<String>();
        for(Transaction tx:allTransactions){
            if(tx.coinbaseTx()){
                continue;
            }
            if(address.equals(Wallet.getAddress(tx.getTxIn().getPublicKey()))){
                spentTxs.add(tx.getTxIn().getTxId());
            }
        }
        for(Block block:blockChain){
            List<Transaction> transactions=block.getTransactions();
            for(Transaction tx:transactions){
                if(address.equals(CryptoUtil.MD5(tx.getTxOut().getPublicKeyHash()))){
                    if(!spentTxs.contains(tx.getId())){
                        unspentTxs.add(tx);
                    }
                }
            }
        }
        return unspentTxs;
    }
    private Transaction findTransaction(String id){
        for(Transaction tx:allTransactions){
            if(id.equals(tx.getId())){
                return tx;
            }
        }
        return null;
    }



/*
验证交易
 */
    private boolean verifyTransactions(Transaction tx) {
        if(tx.coinbaseTx()){
            return true;
        }
        Transaction prevTx =findTransaction(tx.getTxIn().getTxId());
        return tx.verify(prevTx);
    }
    /*
    创建钱包
     */
    public Wallet createWallet(){
        Wallet wallet=Wallet.generateWallet();
        String address=wallet.getAddress();
        myWalletMap.put(address,wallet);
        return wallet;
    }

    /*
    获取钱包余额
     */
    public int getWalletBalance(String address){
        List<Transaction> unspentTxs=findUnspentTransactions(address);
        int balance=0;
        for(Transaction transaction:unspentTxs){
            balance+=transaction.getTxOut().getValue();
        }
        return balance;
    }

    public List<Block> getBlockChain() {
        return blockChain;
    }

    public void setBlockChain(List<Block> blockChain) {
        this.blockChain = blockChain;
    }

    public Map<String, Wallet> getMyWalletMap() {
        return myWalletMap;
    }

    public void setMyWalletMap(Map<String, Wallet> myWalletMap) {
        this.myWalletMap = myWalletMap;
    }

    public Map<String, Wallet> getOtherWalletMap() {
        return otherWalletMap;
    }

    public void setOtherWalletMap(Map<String, Wallet> otherWalletMap) {
        this.otherWalletMap = otherWalletMap;
    }

    public List<Transaction> getAllTransactions() {
        return allTransactions;
    }

    public void setAllTransactions(List<Transaction> allTransactions) {
        this.allTransactions = allTransactions;
    }

    public List<Transaction> getPackedTransactions() {
        return packedTransactions;
    }

    public void setPackedTransactions(List<Transaction> packedTransactions) {
        this.packedTransactions = packedTransactions;
    }
}
