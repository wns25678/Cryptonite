package Client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import Function.PacketProcesser;


public class Client_Server_Connector extends Thread
{
	private static Client_Server_Connector _singleton = null;

	private SocketChannel _channel;
	public PacketProcesser receive;
	public PacketProcesser send;

	private Client_Server_Connector() throws InterruptedException
	{
		try 
		{		
			_channel = SocketChannel.open();
			_channel.configureBlocking(true);
			_channel.connect(new InetSocketAddress("localhost", 4444));

			while (!_channel.finishConnect()) 
			{
				Thread.sleep(1);
				System.out.println("still connecting");
			}
			
			receive = new PacketProcesser(_channel, false);
			send = new PacketProcesser(_channel, false);			
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	

	public static Client_Server_Connector getInstance() throws InterruptedException
	{
		if(_singleton == null)
		{
			_singleton = new Client_Server_Connector();
		}

		return _singleton;
	}
}