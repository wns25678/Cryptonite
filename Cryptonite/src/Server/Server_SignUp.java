package Server;

public class Server_SignUp extends Server_Funtion 
{
	@Override
	public void Checker(byte[] packet) 
	{
		_packetMaxCount = packet[1];
	}

	@Override
	public void running(Server_Client_Activity activity) 
	{
		System.out.println("SignUp running");
		int count = 0;
		while(activity.IsReadable())
		{
			count++;
			System.out.println(count + " read");
			activity._receiveQueue.remove();
		}
		System.out.println(activity._receiveQueue.isEmpty());
		
	}
}
