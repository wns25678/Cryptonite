package Crypto;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.StringBufferInputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.awt.Dialog;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;

import Client.Client_GetGPS;
import Client.Client_Progressbar;
import Client.Client_Server_Connector;
import Function.Function;
import Function.PacketProcessor;
import Function.PacketRule;

public class KeyReposit extends Thread implements PacketRule
{
	private static KeyReposit _singleton = null;
	
	private int _level = 1;
	
	private Client_GetGPS cgp = null;

	private SecretKey _aesKey_lv1 = null; // Default AES Key
	private SecretKey _aesKey_lv2 = null; // PBKDF2
	private SecretKey _aesKey_lv3 = null; // AES Key based on physical address
											// using GPS sensors
	private SecretKey _rsaKey = null; // AES Key for comunication
	
	private Client_Progressbar _cpb = null;
	
	private boolean flag = true;
	private Client_Server_Connector _css=null;
	
	private KeyReposit() 
	{
		_cpb = new Client_Progressbar(4);
	}
	
	public void logout()
	{
		_aesKey_lv1 = null;
		_aesKey_lv2 = null;
		_aesKey_lv3 = null;
	}
	
	public void run()
	{
		_css = Client_Server_Connector.getInstance();
		PathGetter pg = new PathGetter();
		String password=null;
		while(flag)
		{
			try 
			{
				pg.accept();
				String path = pg.receive();
				
				if(_aesKey_lv1 == null) { continue; }
				if(path.substring(path.length()-4, path.length()).contains("cnec"))
				{

					password = getPassword();
					//System.out.println(getPBK(password) + " " + getPBK(password).length());
					makeLv2Key(password);
					
					new Decrypter(path,_aesKey_lv2).start();
					
				}
				else
					new Decrypter(path,_aesKey_lv1).start();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	
	private String getPassword() {
		// TODO 자동 생성된 메소드 스텁
		return (String) JOptionPane.showInputDialog(null, "Input Password", "Password", JOptionPane.PLAIN_MESSAGE, null, null, null);
	}

	public static KeyReposit getInstance() {
		if (_singleton == null) {
			_singleton = new KeyReposit();
		}
		return _singleton;
	}
	
	public void set_level(int level){
		this._level = level;
	}

	public void set_aesKey(SecretKey aesKey) {
		this._aesKey_lv1 = aesKey;
		//cgp = new Client_GetGPS();
	}

	public void set_rsaKey(SecretKey rsaKey) {
		this._rsaKey = rsaKey;
	}

	public SecretKey get_aesKey() {
		SecretKey aesKey = null;
		switch (_level) {
		case 1:
			aesKey = _aesKey_lv1;
			break;
		case 2:
			//makeLv2Key();
			aesKey = _aesKey_lv2;
			break;
		case 3:
			makeLv3Key();
			aesKey = _aesKey_lv3;
			break;
		}
		return aesKey;
	}

	public SecretKey get_rsaKey() {
		return _rsaKey;
	}
	
	public String get_fileExtention() {
		String fileExtension = null;
		
		switch (_level) {
		case 1:
			fileExtension = ".cnec";
			break;
		case 2:
			fileExtension = ".cnmc";
			break;
		case 3:
			fileExtension = ".cnac";
			break;
		}
		return fileExtension;
	}

	private void makeLv2Key(String password) {
		_aesKey_lv2 = new SecretKeySpec(getPBK(password).concat("0000").getBytes(),"AES");
		
	}
	
	private void makeLv3Key(){
		
		byte[] originalAesKeyBytes = _aesKey_lv1.getEncoded();
		byte[] xorAesKeyBytes = new byte[originalAesKeyBytes.length];
		
		cgp.getGPS();
		byte[] xposBytes = Function.doubleToByteArray(cgp.get_xpos());
		byte[] yposBytes = Function.doubleToByteArray(cgp.get_ypos());
		
		for(int i =0; i<originalAesKeyBytes.length ; i++){
			if(i < xposBytes.length){
				xorAesKeyBytes[i] = (byte) (originalAesKeyBytes[i] ^ xposBytes[i] ^ yposBytes[i]);
			} else {
				xorAesKeyBytes[i] = originalAesKeyBytes[i];
			}
		}	
		_aesKey_lv3 = new SecretKeySpec(xorAesKeyBytes, "AES");
	}
	
	private String getPBK (String password)
	{
		byte[] op = new byte[1024];
		String salt = null;
		int iteration = 0;
		byte size =1;
		try {
			op[0]=GET_PBKDF2;
			op[1]=size;
			_css.send.setPacket(op).write();
			
			salt = new String(_css.receive.setAllocate(16).read().getByte());
			iteration = byteArrayToInt(_css.receive.setAllocate(4).read().getByte());
			
			System.out.println(salt +"  " + iteration);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pbkdf2(password,salt,iteration);
		
	}
	public  int byteArrayToInt(byte bytes[]) {
		return ((((int)bytes[0] & 0xff) << 24) |
				(((int)bytes[1] & 0xff) << 16) |
				(((int)bytes[2] & 0xff) << 8) |
				(((int)bytes[3] & 0xff)));
	} 
    public String pbkdf2(String password, String salt, int iterations) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), iterations, 20*8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return new String(Base64.getEncoder().encode(hash));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("error on pbkdf2", e);
        }
    }
}
