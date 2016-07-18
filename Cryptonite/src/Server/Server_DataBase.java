package Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/* ===================================< DataBase Connector >=============================================
 *
 *  Maker   : IN JAE LEE
 *  Date    : 2016.07.19
 *  Version : 1.0.0
 * ======================================================================================================
 * DDL (Data Definition Language)
 *  (a) CREATE
 *  (b) ALTER
 *  (c) DROP
 *  (d) TRUNCATE
 * 
 * DML (Data Definition Language)
 *  (a) SELECT
 *  (b) INSERT
 *  (c) DELETE
 *  (d) UPDATA
 * ======================================================================================================
 * Example <MYSQL>
 * 
 * Server_DataBase DB;
 * DB.Setting_DB("com.mysql.jdbc.Driver", "jdbc.mysql://127.0.0.1:3306/"+DataBase_Name, "root", "0000");
 * DB = DB.getInstance();
 * ======================================================================================================
 */

public class Server_DataBase 
{
	static private Server_DataBase DB;
	
	private String _jdbc_driver_name = null;
	private Connection _con = null;
	private Statement _stmt = null;
	
	private String _url = null;
	private String _id = null;
	private String _passowrd = null;
	
	private Server_DataBase()
	{
		try 
		{
			Class.forName(_jdbc_driver_name);
			_con = DriverManager.getConnection(_url, _id, _passowrd);
			_stmt = _con.createStatement();
		} 
		catch (ClassNotFoundException e) 
		{ 
			System.out.println("ERROR: can't connect jdbc");	 // e.printStackTrace();
		} 
		catch (SQLException e)
		{
			System.out.println("ERROR: can't connect database"); // e.printStackTrace();
		}
	}
	
	public void Init_DB(String jdbc_driver_name,String url, String id, String password)
	{
		_jdbc_driver_name = jdbc_driver_name;
		_url = url;
		_id = id;
		_passowrd = password;
		
		if(DB != null) { Exit_DB(); }
		DB = new Server_DataBase();
	}
	
	public Server_DataBase getInstance()
	{
		if(DB == null) { DB = new Server_DataBase(); }
		
		return DB;
	}
	
	public void Update(String sql)
	{
		try 
		{
			_stmt.executeUpdate(sql);
		} 
		catch (SQLException e) 
		{
			System.out.println("ERROR: Wrong Query");			//	e.printStackTrace();
		}
	}
	public ResultSet Query(String sql)
	{
		ResultSet result;
		try 
		{
			result = _stmt.executeQuery(sql);
		} 
		catch (SQLException e) 
		{
			System.out.println("can't find");					//	e.printStackTrace();
			return null;
		}
		return result;
	}
	
	public void Exit_DB()
	{
		try 
		{
			_stmt.close();
			_con.close();
		}
		catch (SQLException e) 
		{
			System.out.println("can't exit db");				//	e.printStackTrace();
		}
	}
	
}
