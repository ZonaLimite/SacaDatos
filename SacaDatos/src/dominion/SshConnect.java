package dominion;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SshConnect {
	
	JSch jsch = new JSch();
    Session session = null;
    public void connect() {
    try {
        String privateKeyPath = "/home/tecnico/Intercambio/key_store/hostkey2";
		jsch.addIdentity(privateKeyPath );	    
        session = jsch.getSession("s000113", "172.21.34.206", 22);
        session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
        java.util.Properties config = new java.util.Properties(); 
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
    } catch (JSchException e) {
        throw new RuntimeException("Failed to create Jsch Session object.", e);
    }
    
    //The next step is to connect to the remote host and execute an arbitrary command over SSH:

        String command = "echo \"Sit down, relax, mix yourself a drink and enjoy the show...\" ";
        try {
            session.connect();
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            ((ChannelExec) channel).setPty(false);
            channel.connect();
            channel.disconnect();
            session.disconnect();
        } catch (JSchException e) {
            throw new RuntimeException("Error durring SSH command execution. Command: " + command);
        }
    }
    
    static public void main(String[] args){
    	SshConnect sshconnect = new SshConnect();
    	sshconnect.connect();
    	
    }
}
