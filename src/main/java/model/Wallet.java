package model;

import security.CryptoUtil;
import security.RSACoder;

import java.security.interfaces.RSAKey;
import java.util.Map;

/*
钱包数据结构
 */
public class Wallet {
    private String publicKey;   //公钥
    private String privateKey;   //私钥
    public Wallet(){}
    public Wallet(String publicKey){
        this.publicKey=publicKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public Wallet(String publicKey, String privateKey){
        this.publicKey=publicKey;
        this.privateKey=privateKey;
    }
    public static Wallet generateWallet(){
        Map<String,Object> initKey;
        try{
            initKey = RSACoder.initKey();
            String publicKey=RSACoder.getPublicKey(initKey);
            String privateKey=RSACoder.getPrivateKey(initKey);
            return new Wallet(publicKey,privateKey);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    /*
    获取钱包地址
     */
    public String getAddress(){
        String publicKeyHash=hashPubKey(publicKey);
        return CryptoUtil.MD5(publicKeyHash);
    }
    /*
    根据钱包公钥生成钱包地址
     */
    public static String getAddress(String publicKey){
        String publicKeyHash=hashPubKey(publicKey);
        return CryptoUtil.MD5(publicKeyHash);
    }

    /*
    获取钱包公钥hash
     */
    public String getHashPubKey(){
        return CryptoUtil.SHA256(publicKey);
    }

    /*
    生成钱包公钥hash
     */
    public static String hashPubKey(String publicKey){
        return CryptoUtil.SHA256(publicKey);
    }



}
