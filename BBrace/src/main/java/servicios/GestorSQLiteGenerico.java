package servicios;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GestorSQLiteGenerico {
	//Clase conexion y conexion en si
	private Connection con;
	private BBDDConection bbddConection;

	public GestorSQLiteGenerico() {
		bbddConection = new BBDDConection();
	}
	
	/**
	 * Abre la conexion
	 */
	private void initConection() {
		try {
			this.con = bbddConection.getConexion();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Metodo generico para insertar valores en una tabla dado el nombre de cada columna
	 * El numero de valores y columnas han de ser iguales, e ir en orden
	 * @param tabla (String) nombre de la tabla
	 * @param columnas (ArrayList<String>) nombre de cada columna
	 * @param valores (ArrayLIst(Object) valores de cada columna, en orden
	 * @return 0 si no ha tenido exito, 1 si se realiza correctamente, -1 tamaño de arrays incompatible
	 */
//	public int insertValuesIntoTable(String tabla, ArrayList<String> columnas, ArrayList<Object> valores) {
//		int operationSuccess = 0;
//		if(columnas.size() == valores.size()) {
//			initConection();
//
//			String queryIni = "INSERT INTO "+tabla;
//			String queryCol = "(";
//
//			for (String c:columnas) {
//				queryCol = queryCol+""+c;
//				if (columnas.get(columnas.size()-1) != c) queryCol=queryCol+",";
//				else queryCol = queryCol+") ";
//			}
//
//			String queryVal = "(";
//			for (Object o : valores){
//				queryVal = queryVal + "?";
//				if (valores.get(valores.size()-1) != o) queryVal = queryVal + ",";
//				else queryVal = queryVal +");";
//			}
//
//			String query = queryIni + queryCol + "VALUES" + queryVal;			
//
//			try {
//				PreparedStatement pstmt = con.prepareStatement(query);
//				for (int i = 0; i<valores.size(); i++) {
//					pstmt.setObject(i+1, valores.get(i));
//				}
//				operationSuccess = pstmt.executeUpdate();
//				pstmt.close();		
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}finally {				
//				try {
//					con.close();
//					System.out.println("Conexion cerrada");
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}else operationSuccess = -1;
//
//		return operationSuccess;
//	}

	/**
	 * Metodo generico para insertar valores en una tabla dado el nombre de cada columna.
	 * 
	 * Este metodo permite pasar un conjunto de valores como atributo (VarArgs)
	 * la longitud de cada valor y de las columnas han de ser iguales, e ir en orden
	 * 
	 * 
	 * @param tabla
	 * @param columnas
	 * @param valores
	 * @return
	 */
	@SafeVarargs
	public final int insertValuesIntoTable(String tabla, ArrayList<String> columnas, ArrayList<Object>... valores) {
		int operationSuccess = 0;
		
		boolean isAllSizeValoresEqual = true;
		int longitud = valores[0].size();
		for(ArrayList<Object> val : valores) {
			if (longitud != val.size()) isAllSizeValoresEqual = false;
		}
		
		if(columnas.size() == longitud && isAllSizeValoresEqual) {
			initConection();

			String queryIni = "INSERT INTO "+tabla;
			String queryCol = "(";

			for (String c:columnas) {
				queryCol = queryCol+""+c;
				if (columnas.get(columnas.size()-1) != c) queryCol=queryCol+",";
				else queryCol = queryCol+") ";
			}

			String queryValsTogether = "";
			for(ArrayList<Object> val : valores) {
				String queryVal = "(";
				for (Object o : val){
					queryVal = queryVal + "?";
					if (val.get(val.size()-1) != o) queryVal = queryVal + ",";
					else queryVal = queryVal +")";
				}
				if(valores[valores.length-1] != val) queryVal = queryVal + ",";
				else queryVal = queryVal +";";
				queryValsTogether = queryValsTogether + queryVal;
			}
			

			String query = queryIni + queryCol + "VALUES" + queryValsTogether;			

			try {
				PreparedStatement pstmt = con.prepareStatement(query);
				int j = 1;
				for (ArrayList<Object> val : valores) {
					for (int i = 0; i<val.size(); i++) {
						pstmt.setObject(j, val.get(i));
						j++;
					}
				}
				operationSuccess = pstmt.executeUpdate();
				pstmt.close();		
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {				
				try {
					con.close();
					System.out.println("Conexion cerrada");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else operationSuccess = -1;
		return operationSuccess;
	}
	/**
	 * Metodo genérico para updatear un elemento concreto de una tabla
	 * El numero de valores y columnas han de ser iguales, e ir en orden
	 * @param tabla (String) nombre de la tabla en SQLite
	 * @param pkValue (Object) el valor de la clave primaria del elemento
	 * @param columna (ArrayList<String>) nombre de las columnas a modificar
	 * @param dato (ArrayList<Object>) nuevos valores del elemento a modificar
	 * @return 0 si no ha tenido exito, 1 si se realiza correctamente, -1 tamaño de arrays incompatible
	 */
	public int updateTableField(String tabla, Object pkValue, ArrayList<String> columnas, ArrayList<Object> colValues) {
		int operationSuccess = 0;
		if(columnas.size() == colValues.size()) {
			initConection();
			//Para diferenciar entre dni e id en la query, segun el tipo de tabla
			String pkType;
			if( tabla == "enfermero" | tabla == "medico" | tabla == "padre" | tabla == "gestor") 
				pkType = "dni";
			else pkType = "id";

			String query = "UPDATE "+tabla+" SET ";
			String queryCol = ""; 
			String queryVal;
			if(pkValue instanceof String) queryVal = " WHERE "+pkType+" = '"+pkValue+"'";
			else queryVal = " WHERE "+pkType+" = "+pkValue;
			

			//Construimos la string de las columnas
			for (String c : columnas) {
				queryCol = queryCol + c + " = ?";
				if (columnas.get(columnas.size()-1) != c) queryCol = queryCol + " , ";
			}

			query = query + queryCol + queryVal+";";

			try {
				PreparedStatement pstmt = con.prepareStatement(query);

				for (int i = 0; i<colValues.size(); i++) {
					pstmt.setObject(i+1, colValues.get(i));
				}

				//pstmt.setObject(colValues.size(), pkValue);

				operationSuccess = pstmt.executeUpdate();
				System.out.println("op Succ " + colValues.get(0) + ": "+operationSuccess);
				pstmt.close();			
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					con.close();
					System.out.println("Conexion cerrada");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else operationSuccess = -1;

		return operationSuccess;
	}
	
	public int selectMaxIdUnionFromNotificaciones(){
		int idUnionObtenido = 0;
		
		initConection();
		String query = "SELECT MAX(idUnion) FROM notificaciones;";
		
		try {
			PreparedStatement pstmt = con.prepareStatement(query);        
	        ResultSet rslt = pstmt.executeQuery();

	       
	        while(rslt.next()) {
	        	idUnionObtenido = rslt.getInt(1);
	        }
	        
			pstmt.close();
					
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				con.close();
				System.out.println("Conexion cerrada");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		if(idUnionObtenido == 0) {
			idUnionObtenido ++;
		}
		
		
		return idUnionObtenido;
		
	}
	
	public int selectMaxIdOrdenFromNotifiaciones(int idUnion) {
		int idOrden = 0;
		
		initConection();
		String query = "SELECT MAX(idOrden) FROM notificaciones WHERE idUnion=" + idUnion+";";
		
		try {
			PreparedStatement pstmt = con.prepareStatement(query);        
	        ResultSet rslt = pstmt.executeQuery();

	       
	        while(rslt.next()) {
	        	idOrden = rslt.getInt(1);
	        }
	        
			pstmt.close();
					
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				con.close();
				System.out.println("Conexion cerrada");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		if(idOrden == 0) {
			idOrden ++;
		}
		
		
		return idOrden;
		
		
		
	}
	
	/**
	 * Metodo generico para leer cualquier elemento de una tabla usando el valor de uno o varios elementos como filtro
	 * Si el array de columnas esta vacio, selecciona todo
	 * @param tabla (String) nombre de la tabla en SQLite
	 * @param columna (ArrayList<String>) nombre de columna del elemento/s que vamos a usar como filtro
	 * @param colValue (ArrayList<Object>) valor/es del elemento en la/s columna/s
	 * @return ArrayList de mapas con la informacion de las filas que se han buscado
	 */
	public ArrayList<Map<String, Object>> selectFromTable(String tabla, ArrayList<String> columnas, ArrayList<Object> colValues) {
		ArrayList<Map<String, Object>> arrayResultados = new ArrayList<>();//TODO DANI
		if (columnas.size() == colValues.size()) {
			
			initConection();
	
			String queryIni = "SELECT * FROM "+ tabla; 
			String queryCol = " WHERE ";
			//Construimos la string de las columnas
			for (String c : columnas) {
				queryCol = queryCol + c + " = ?";
				if (columnas.get(columnas.size()-1) != c) queryCol = queryCol + " AND ";
				else queryCol = queryCol + ";";
			}
			//Juntamos la string inicial con la de las columnas
			String query = queryIni;
			if(columnas.size() > 0) {
				query = query + queryCol;
			}
			
			try {
				PreparedStatement pstmt = con.prepareStatement(query);
				for (int i = 0; i<colValues.size(); i++) {
					pstmt.setObject(i+1, colValues.get(i));
				}
		        
		        ResultSet rslt = pstmt.executeQuery();
		        //Para contar las columnas y obtener nombres de cada una
		        ResultSetMetaData rsltMetaData = rslt.getMetaData();
		        int numeroColumnas = rsltMetaData.getColumnCount();
		        //bucle para insertar mapas dentro del array segun las filas obtenidas
		        while(rslt.next()) {
		        	Map<String, Object> mapaPorFila = new HashMap<>();
		        	//bucle para insertar los datos en el mapa, tomando la clave como el nombre de la
		        	//columna, y el valor como el mismo en la tabla
		        	for(int i = 1; i<= numeroColumnas; i++) {	
		        		mapaPorFila.put(rsltMetaData.getColumnName(i), rslt.getObject(i));
		        	}	
		        	arrayResultados.add(mapaPorFila);
		        }
		        
				pstmt.close();
						
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					con.close();
					System.out.println("Conexion cerrada");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		}

		return arrayResultados;	

	}
	
	

	/**
	 * Metodo generico para eliminar valores en una tabla dado el valor del identificador
	 * @param tabla (String) nombre de la tabla
	 * @param pkValue (Object) valor de la clave primaria
	 * @return 0 si no ha tenido exito, 1 si se realiza correctamente
	 */
	public int deleteValuesFromTable(String tabla,  Object pkValue) {
		int operationSuccess = 0;
		initConection();
		//Para diferenciar entre dni e id en la query, segun el tipo de tabla
		String pkType;
		if( tabla == "enfermero" | tabla == "medico" | tabla == "padre" | tabla == "gestor") 
			pkType = "dni";
		else if(tabla == "tcenfermerobebe") pkType = "id_bebe";
		else pkType = "id";

		String query = "DELETE FROM "+tabla+" WHERE "+pkType+" = ?;";
		
		try {
			PreparedStatement pstmt = con.prepareStatement(query);
			pstmt.setObject(1, pkValue);
			
			operationSuccess = pstmt.executeUpdate();		
			
			pstmt.close();
						
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				con.close();
				System.out.println("Conexion cerrada");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}

		return operationSuccess;
	}



}
