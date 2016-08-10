package Server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

import Function.PacketProcesser;
import Function.PacketRule;

/*
 * @author In Jae Lee
 * 
 */

public class Server_Client_Activity implements PacketRule 
{
	private final static int LIMIT_PACKET = 10;
	
	private SocketChannel _channel;
	private Server_Client_Manager _manager;
	private int _clientCode;
	
	private Queue<Integer> _readableQueue;
	
	private int _packetCount = 0;
	private int _usedCount = 1;

	public LinkedList<Server_Funtion> _funtionList;
	
	public PacketProcesser receive;
	public PacketProcesser send;
	
	public Integer _readableCount = 0;
	public int _readingCount = 0;

	public Server_User_Info _loginInfo;	

	public Server_Client_Activity(Selector selector, SelectionKey key, int clientCode)
	{
		try 
		{
			ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
	        _channel = serverSocketChannel.accept();
	        
	        _channel.configureBlocking(false);
            SelectionKey clientKey = _channel.register(selector, 
            									SelectionKey.OP_READ,SelectionKey.OP_WRITE);
            clientKey.attach(this);
            
            _clientCode = clientCode;
            
            _readableQueue = new LinkedList<Integer>();
            _funtionList = new LinkedList<Server_Funtion>();
            
            _loginInfo = new Server_User_Info();
            _manager = Server_Client_Manager.getInstance();

            System.out.println(_channel.toString() + "connect");
            
            receive = new PacketProcesser(_channel, true);
            send = new PacketProcesser(_channel, true); 
            
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	public void Receiver() throws IOException 
	{	
		receive.read();
		
		_packetCount++;
		_readableCount++;
		
		if(_packetCount == 1)
		{
			_readableCount--;
			_manager.packetChecker(this);		
		}
		else
		{

			if(_funtionList.getLast()._packetMaxCount == _packetCount)
			{
				_readableQueue.offer(_readableCount);
				_packetCount = 0;
				_readableCount = 0;
				_manager.requestManage(_clientCode);
			}
			else if(_readableCount >= _funtionList.getLast().getLimitSize())
			{			
				_readableQueue.offer(_readableCount);
				_readableCount = 0;
				_manager.requestManage(_clientCode);
			}
		}
	}
	
	public void readableUpdate()
	{
		_readingCount = _readableQueue.remove();
		_usedCount +=  _readingCount;
	}
	
	public void finishCheck()
	{
		if(_usedCount == _funtionList.getFirst()._packetMaxCount)
		{
			_funtionList.removeFirst();
			_usedCount = 1;
		}
	}
	
	public boolean IsReadable()
	{
		if(_readingCount > 0)
		{
			_readingCount--;
			return true;
		}
		else
		{
			return false;	
		}
	}
	
	public int getClientCode() { return _clientCode; }
	public Server_User_Info getUserInfo() { return _loginInfo; }
	
	public void close() 
	{
		System.out.println(_channel.toString() + "Stop Connect");
		try {
			_channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
