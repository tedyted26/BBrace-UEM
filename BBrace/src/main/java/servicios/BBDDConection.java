package servicios;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


import org.sqlite.SQLiteConfig;

public class BBDDConection {
	
	//Database credentials (mariadb server)
	static final String USER="pr_bbrace";
	static final String PASS="BbRacE.44";
	
	private String driver = "org.mariadb.jdbc.Driver";
    private String url = "jdbc:mariadb://2.139.176.212:3306/prbbrace";

    /**
     * Devuelve la conexion abierta con la BBDD
     * @return (Connection) conexion
     * @throws ClassNotFoundException
     * @throws SQLException
     */
	protected Connection getConexion() throws ClassNotFoundException, SQLException{
	    Class.forName(driver);
	    
	    SQLiteConfig config = new SQLiteConfig();
	    config.enforceForeignKeys(true);
	    
	    Connection con = DriverManager.getConnection(url, USER, PASS);
	    System.out.println("Conexión establecida");
	    
	    return con;
	    
	}
}
