import block.BlockService;
import http.HttpService;
import p2p.P2PClient;
import p2p.P2PServer;
import p2p.P2PService;

public class Main {
    /*
    区块链节点启动入口
     */


    public static void main(String []args ){
        if(args !=null&&(args.length==1||args.length==2||args.length==3)){
            try{
                BlockService blockService=new BlockService();
                P2PService p2pService =new P2PService(blockService);
                startP2PServer(args,p2pService);
                HttpService httpService=new HttpService(blockService,p2pService);
                int httpPort =Integer.valueOf(args[0]);
                httpService.initHTTPServer(httpPort);

            }catch(Exception e){
                System.out.println("startup is error:"+e.getMessage());
            }
        }else {
            System.out.println("usage: java -jar blockchain.jar 8081 7001");
        }
    }
    private static void startP2PServer(String []args,P2PService p2pService){
        P2PServer p2pServer =new P2PServer(p2pService);
        P2PClient p2pClient =new P2PClient(p2pService);
        int p2pPort=Integer.valueOf(args[1]);
        //启动P2P服务
        p2pServer.initP2PServer(p2pPort);
        if(args.length==3&&args[2]!=null){
            //链接到服务器（另一个端口号的节点）
            p2pClient.connectToPeer(args[2]);
        }
    }
}
