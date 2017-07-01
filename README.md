# SpiderMaster

Enpower [SpiderSDK](https://github.com/xiaoshenke/SpiderSDK) with Master/Agent mode.


### Implement
1 build a RPC framewrok using Netty.                    
2 build Master/Agent with the framework.  

#### Rpc Framework mode
1 classic cs mode:                        
client --> request rpc --> server --> response rpc --> client                    
2 request transmit mode:if server can't handle the request,it will find one of the so-called providers to handle request.                           
          client --> request rpc --> server --> request rpc --> providers(client) --> response rpc --> server --> response rpc --> client
          
 
#### Current Implemented Feature
1 agent register to master                      
2 agent send heartbeat to master                
3 agent report self status(working fine,blocked,switching proxy) to master               
4 agent request proxy from master            
5 master monit all agents's states
   
   
#### Todo  
        
           
           
check out the project to know details~

