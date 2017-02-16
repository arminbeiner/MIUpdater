package models;
import com.jcraft.jsch.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;

import com.jcraft.jsch.Logger;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.util.io.pem.PemWriter;



/**
 * Created by Armin on 30.01.2017.
 */
public class SSHConnection {

    private String IP;
    private int port;
    private Session session = null;
    private Channel channel1 = null;
    private Channel channel2 = null;
    private String user = "scsupdater";




    //private String remoteFile = "/home/scsupdater/test.txt";

/*    public String getUser() {
        return user;
    }

    public String getPwd() {
        return pwd;
    }*/

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean getSession() {
        return session.isConnected();
    }

    public boolean getChannel1() {
        return channel1.isConnected();
    }

    public boolean getChannel2() {
        return channel2.isConnected();
    }

    public void sshSessionConnect(){

        try {
            FileInputStream is = new FileInputStream("C:\\Program Files\\Java\\jdk1.8.0_101\\bin\\MIUpdater.keystore");

            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(is, "Password".toCharArray());
            Key privKey = keystore.getKey("miupdater", "Password".toCharArray());

            StringWriter stringWriter = new StringWriter();
            JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter);
            pemWriter.writeObject(privKey);
            pemWriter.close();
            System.out.println(stringWriter);

            byte[] privateKeyPEM = stringWriter.toString().getBytes();

            JSch jsch = new JSch();
            jsch.addIdentity("miupdater", privateKeyPEM, null, null);
            session = jsch.getSession(user, getIP(), getPort());
            session.setConfig("PreferredAuthentications", "publickey");
            //session.setPassword(pwd);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(3000);
        }catch(KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | UnrecoverableEntryException | JSchException ex){
            ex.getMessage();
            System.out.println(ex);
        }
    }

    public void sshChannel1Connect(){

        try{
            channel1 = session.openChannel("exec");
            String command1 = "sudo /home/scsupdater/test.sh";
            ((ChannelExec)channel1).setCommand(command1);
            InputStream in = channel1.getInputStream();
            ((ChannelExec)channel1).setErrStream(System.err);
            channel1.connect();
        } catch(JSchException | IOException e){
            e.getMessage();
        }

    }

    public void sshChannel2Connect() {

        try{
            channel2 = session.openChannel("exec");
            String command2 = "echo hier wäre das Script >> /home/scsupdater/hallo";
            ((ChannelExec)channel2).setCommand(command2);
            InputStream in = channel2.getInputStream();
            ((ChannelExec)channel2).setErrStream(System.err);
            channel2.connect();
        }catch (JSchException | IOException e) {
            e.getMessage();
        }
    }

    public void sshChannelDisconnect(){

            if(channel1.isConnected() || channel2.isConnected()){
                channel1.disconnect();
                channel2.disconnect();
            }

    }

    public void sshSessionDissconnect() {
        session.disconnect();
    }

}