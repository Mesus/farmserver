package com.cbd5;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class PostgresCommon {

	private static Connection con = null;
	
	public static Properties prop = null;
	public boolean conn() {
		prop = new Properties();
		try {
			// ��ȡ�����ļ�a.properties
			// InputStream in = new BufferedInputStream(new FileInputStream(
			// "conn.properties"));
			InputStream in = getClass().getResourceAsStream("conn.properties");
			prop.load(new InputStreamReader(in, "UTF-8")); // /���������б�
			Iterator<String> it = prop.stringPropertyNames().iterator();
			while (it.hasNext()) {
				String key = it.next();
				System.out.println(key + ":" + prop.getProperty(key));
			}
			in.close();

		} catch (Exception e) {
			System.out.println(e);

			return false;
		}
		String url = prop.getProperty("url");
		String username = prop.getProperty("username");
		String password = prop.getProperty("password"); // �����������������ݿ�
		try {
			Class.forName("org.postgresql.Driver").newInstance();
			con = DriverManager.getConnection(url, username, password);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				con.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return false;
			}
			return false;
		}
		return true;
	}

	public String query(String sql) {
		boolean success = true;
		String rst = "";
		try {
			Statement st = con.createStatement();
			// String sql = " SELECT * FROM "+table+" "+where+"" ;
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				// System.out.print(rs.getInt( 1 ));
//				System.out.println(rs.getObject(1));
				rst = rs.getString(1);
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// success = false;
		}
		return rst;
	}

	public double queryDouble(String sql) {
		boolean success = true;
		double rst = 0;
		try {
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				// System.out.print(rs.getInt( 1 ));
//				System.out.println(rs.getObject(1));
				rst = rs.getDouble(1);
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// success = false;
		}
		return rst;
	}



	public int fieldExist(String schema, String table, String column) {
		int rst = 0;
		try {
			Statement st = con.createStatement();
			String sql = " select count(*) from information_schema.columns WHERE table_schema='"
					+ schema
					+ "' and table_name ='"
					+ table
					+ "' and  column_name='" + column + "'";
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				rst = rs.getInt(1);
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// success = false;
		}
		return rst;
	}

	public JSONArray resultSetToJson(String sql) {
		// json����
		JSONArray array = new JSONArray();

		try {
			// ��ȡ����
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();

			// ����ResultSet�е�ÿ�����
			while (rs.next()) {
				JSONObject jsonObj = new JSONObject();
//				System.out.println("///////////" + rs.getRow());
				// ����ÿһ��
				for (int i = 1; i <= columnCount; i++) {
					String columnName = metaData.getColumnLabel(i);
					//
					String value = rs.getString(columnName);
//					if(sql.indexOf("sczz") == -1){
						if(columnName.indexOf("����") != -1 || columnName.indexOf("�ܼ�") != -1){
							if("".equals(value)){
								value = "0";
							}else{
								value = value.substring(0, value.indexOf(".")+4);
//								if(value.indexOf("-") != -1){
//									jsonObj = null;
//									break;
//								}
							}
						}
//					}
					jsonObj.put(columnName, value);
				}
				if(jsonObj != null){
					array.add(jsonObj);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return array;
	}

	public JSONObject resultToJson(String sql) {

		JSONObject jsonObj = new JSONObject();
		try {
			// ��ȡ����
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();

			// ����ResultSet�е�ÿ�����
			while (rs.next()) {
				System.out.println("///////////" + rs.getRow());
				// ����ÿһ��
				for (int i = 1; i <= columnCount; i++) {
					String columnName = metaData.getColumnLabel(i);
					String value = rs.getString(columnName);
					jsonObj.put(columnName, value);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonObj;
	}
	public JSONObject resultToJsonForEachBase(String sql) {

		JSONObject jsonObj = new JSONObject();
		try {
			// ��ȡ����
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			ResultSetMetaData metaData = rs.getMetaData();

			String farm = "";
			String rkje = "";
			String ckje = "";
			String kcje = "";
			boolean n = false;
			// ����ResultSet�е�ÿ�����
			while (rs.next()) {
				String farm_col = metaData.getColumnLabel(1);
				String value = rs.getString(farm_col);
				farm += value+",";
				
				String rkje_col = metaData.getColumnLabel(2);
				String rkje_val = rs.getString(rkje_col);
				rkje += rkje_val+",";
				
				String ckje_col = metaData.getColumnLabel(3);
				String ckje_val = rs.getString(ckje_col);
				ckje += ckje_val+",";
				
				String kcje_col = metaData.getColumnLabel(4);
				String kcje_val = rs.getString(kcje_col);
				kcje += kcje_val+",";
				n = true;
			}
			if(n){
				jsonObj.put("cate", farm.substring(0, farm.length()-1));
				jsonObj.put("rkje", rkje.substring(0, rkje.length()-1));
				jsonObj.put("ckje", ckje.substring(0, ckje.length()-1));
				jsonObj.put("kcje", kcje.substring(0, kcje.length()-1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonObj;
	}
	public JSONArray resultSetToJsonSpec(String sql) {
		// json����
		JSONArray array = new JSONArray();

		try {
			// ��ȡ����
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();
			// ����ResultSet�е�ÿ�����
			while (rs.next()) {
				JSONObject jsonObj = new JSONObject();
				double bqkc = -99999;
				// ����ÿһ��
				for (int i = 1; i <= columnCount; i++) {
					String columnName = metaData.getColumnLabel(i);
					String value = rs.getString(columnName);
					if ("zj".equals(value)) {
						String variety = "";
						String k_desc = "";
						String gys = "";

						if ("����Ʒ��".equals(metaData.getColumnLabel(4))) {
							variety = rs.getObject(3).toString();
							k_desc = rs.getObject(4).toString();
							gys = rs.getObject(5)!=null?rs.getObject(5).toString():"";
						} else {
							variety = rs.getObject(2).toString();
							k_desc = rs.getObject(3).toString();
							gys = rs.getObject(4).toString();
						}
						String sql_k = "SELECT price FROM kind2price WHERE variety='"
								+ variety + "' AND k_desc='" + k_desc + "' AND gys='"+gys+"'";
						double price = queryDouble(sql_k);
//						price = (double)(Math.round(price*1000)/1000.0);
						if(columnName.indexOf("����ܼ�") != -1){
							double tmp = Double.parseDouble(jsonObj.get("�����").toString());
							tmp = (double)(Math.round(tmp * price *1000)/1000.0);
							value = Double.toString(tmp);
						}else{
							value = Double.toString((Math.round(rs.getDouble(i - 1) * price*1000)/1000.0));
//							System.out.println(rs.getDouble(i - 1)+"///////"+price);
//							value = Double.toString(rs.getDouble(i - 1) * price);
//							System.out.println(value);
						}
					}
					double rkl = 0;
					double cksl = 0;
					if ("�����".equals(columnName)) {
						String[] w = rs.getString(columnName).split("@");
						String fw = "";
						if(!"N".equals(w[0])){
							fw = "farm='"+w[0]+"' and ";
						}
//						String sql_m = "SELECT max(insert_time) FROM inventory where "+fw+" insert_time>='"+w[1]+"' and insert_time<='"+w[2]+"'";
//						String time = query(sql_m);
						String pm = "";
						String mc = "";
						String gys = "";
						if ("����Ʒ��".equals(metaData.getColumnLabel(4))) {
							pm = rs.getObject(3)!=null?rs.getObject(3).toString():"";
							mc = rs.getObject(4).toString();
							gys = rs.getObject(5)!=null?rs.getObject(5).toString():"";
							rkl = Double.parseDouble(rs.getObject(7)!=null?rs.getObject(7).toString():"0.0");
							cksl = Double.parseDouble(rs.getObject(9)!=null?rs.getObject(9).toString():"0.0");
						} else {
							pm = rs.getObject(2).toString();
							mc = rs.getObject(3).toString();
							gys = rs.getObject(4).toString();
							rkl = Double.parseDouble(rs.getObject(6)!=null?rs.getObject(6).toString():"0.0");
							cksl = Double.parseDouble(rs.getObject(8)!=null?rs.getObject(8).toString():"0.0");
						}
						
						String sql_k = "SELECT sqkc FROM inventory WHERE pm='"
								+ pm + "' AND mc='" + mc + "' AND gys='"+gys+"' AND "+fw+"  insert_time like '%"+w[2].substring(0, w[2].length()-2)+"%'";
						double sqkc = queryDouble(sql_k);
//						
						bqkc = sqkc + rkl +cksl;
						sqkc = (double)(Math.round(sqkc*1000)/1000.0);
						value = Double.toString(sqkc);
					}
					jsonObj.put(columnName, value);
				}
				if(bqkc != -99999 && bqkc>0){
					array.add(jsonObj);
				}
				bqkc = -99999;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return array;
	}

	public JSONObject resultSetToStat(String sql,String farm) {
		// json����
		JSONObject crk = new JSONObject();
		
		double rkl = 0;
		double ckl = 0;
		double kcl = 0;
		try {
			// ��ȡ����
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();
			// ����ResultSet�е�ÿ�����
			while (rs.next()) {
				JSONObject jsonObj = new JSONObject();

				// ����ÿһ��
				for (int i = 1; i <= columnCount; i++) {
					String columnName = metaData.getColumnLabel(i);
					String value = rs.getString(columnName);
					if ("zj".equals(value)) {
						String variety = "";
						String k_desc = "";
						String gys = "";
						if ("����Ʒ��".equals(metaData.getColumnLabel(4))) {
							variety = rs.getObject(3).toString();
							k_desc = rs.getObject(4).toString();
							gys = rs.getObject(5)!=null?rs.getObject(5).toString():"";
						} else {
							variety = rs.getObject(2).toString();
							k_desc = rs.getObject(3).toString();
							gys = rs.getObject(4).toString();
						}
						String sql_k = "SELECT price FROM kind2price WHERE variety='"
								+ variety + "' AND k_desc='" + k_desc + "' AND gys='"+gys+"'";
						double price = queryDouble(sql_k);
//						price = (double)(Math.round(price*1000)/1000.0);
						if(columnName.indexOf("����ܼ�") != -1){
							double tmp = Double.parseDouble(jsonObj.get("�����").toString());
							tmp = (double)(Math.round(tmp * price *1000)/1000.0);
							kcl += tmp;
							value = Double.toString(tmp);
						}
						if(columnName.indexOf("����ܼ�") != -1){
							value = Double.toString((Math.round(rs.getDouble(i - 1) * price*1000)/1000.0));
							double tmp = rs.getDouble(i - 1) * price;
							rkl += tmp;
						}
						if(columnName.indexOf("�����ܼ�") != -1){
							value = Double.toString((Math.round(rs.getDouble(i - 1) * price*1000)/1000.0));
							double tmp = rs.getDouble(i - 1) * price;
							ckl += tmp;
						}
					}
					if ("�����".equals(columnName)) {
						String[] w = rs.getString(columnName).split("@");
						String fw = "";
						String sql_m = "";
						String time = "";
						if(!"N".equals(farm)){
							fw = "farm='"+farm+"' and ";
						}
						sql_m = "SELECT max(insert_time) FROM inventory where "+fw+"insert_time>='"+w[0]+"' and insert_time<='"+w[1]+"'";
						time = query(sql_m);
						String pm = "";
						String mc = "";
						String gys = "";
						if ("����Ʒ��".equals(metaData.getColumnLabel(4))) {
							pm = rs.getObject(3)!=null?rs.getObject(3).toString():"";
							mc = rs.getObject(4).toString();
							gys = rs.getObject(5)!=null?rs.getObject(5).toString():"";
						} else {
							pm = rs.getObject(2).toString();
							mc = rs.getObject(3).toString();
							gys = rs.getObject(4).toString();
						}
						//
//						double rkl = Double.parseDouble(rs.getObject(7)!=null?rs.getObject(7).toString():"0.0");
//						double cksl = Double.parseDouble(rs.getObject(9)!=null?rs.getObject(9).toString():"0.0");
						//
						
						String sql_k = "SELECT sqkc FROM inventory WHERE pm='"
								+ pm + "' AND mc='" + mc + "' AND gys='"+gys+"' AND "+fw+" insert_time='"+time+"'";
						double sqkc = queryDouble(sql_k);
//						
//						double bqkc = sqkc + rkl -cksl;
						sqkc = (double)(Math.round(sqkc*1000)/1000.0);
						value = Double.toString(sqkc);
					}
					jsonObj.put(columnName, value);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		crk.put("rksl", Math.round(rkl *1000)/1000.0);
		crk.put("cksl", Math.round(ckl *1000)/1000.0);
		crk.put("kcl", Math.round(kcl *1000)/1000.0);
		
		return crk;
	}
	public String queryStrArr(String sql) {
		boolean success = true;
		String rst = "";
		try {
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= columnCount; i++) {
					rst += rs.getString(i) + ",";
				}
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// success = false;
		}
		return rst;
	}

	public JSONObject resultKVToJson(String sql) {
		JSONObject jsonObj = new JSONObject();
		try {
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			// json����

			// ����ResultSet�е�ÿ�����
			while (rs.next()) {
				
				System.out.println("///////////" + rs.getRow());
				// ����ÿһ��
				jsonObj.put(rs.getString(1), rs.getString(2));
//				array.add(jsonObj);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonObj;
	}

	public String queryPl(String pm) {
		boolean success = true;
		String rst = "";
		try {
			Statement st = con.createStatement();
			String sql = "select l.pl_name from pm m,pl l where m.pm_name='"
					+ pm + "' and m.pl_id=l.id";
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				System.out.println(rs.getObject(1));
				rst = rs.getString(1);
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// success = false;
		}
		return rst;
	}

	public ArrayList querySingle(String sql) {
		boolean success = true;
		String rst = "";
		ArrayList al = new ArrayList();
		try {
			Statement st = con.createStatement();
			// String sql = " SELECT * FROM "+table+" "+where+"" ;
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				rst = rs.getString(1);
				al.add(rst);
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// success = false;
		}
		return al;
	}

	public String queryMulti(String sql) {
		boolean success = true;
		String rst = "";
		ArrayList al = new ArrayList();
		StringBuffer sb = new StringBuffer();
		try {
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			ResultSetMetaData m = null;
			m = rs.getMetaData();

			int columns = m.getColumnCount();

			while (rs.next()) {
				StringBuffer sb1 = new StringBuffer();
				for (int i = 1; i <= columns; i++) {
					String tmp = m.getColumnName(i);
					if ("insert_time".equals(tmp)) {
						sb.append(rs.getString(i));
					} else {
						sb.append(rs.getDouble(i));
					}
					sb.append(",");
					// al.add(sb.toString());
				}
				sb = sb.deleteCharAt(sb.length() - 1);
				sb.append("|");
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// success = false;
		}
		return sb.toString();
	}

	public ArrayList queryDoubleArr(String sql) {
		boolean success = true;
		String rst = "";
		ArrayList al = new ArrayList();
		try {
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			ResultSetMetaData m = null;
			m = rs.getMetaData();

			int columns = m.getColumnCount();
//            System.out.println(sql);
            while (rs.next()) {
				for (int i = 1; i <= columns; i++) {
					double r = rs.getDouble(i);
//                    System.out.println(r);
                    al.add(r);
					// al.add(sb.toString());
				}
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// success = false;
		}
		return al;
	}

    public ArrayList queryArr(String sql) {
        boolean success = true;
		String rst = "";
		ArrayList al = new ArrayList();
		try {
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			ResultSetMetaData m = null;
			m = rs.getMetaData();

			int columns = m.getColumnCount();

			while (rs.next()) {
				for (int i = 1; i <= columns; i++) {
					String r = rs.getString(i);
//                    System.out.println(r);
                    al.add(r);
					// al.add(sb.toString());
				}
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// success = false;
		}
        return al;
    }
	public int insert(String sql) {
		// conn();
		int rst = 0;

		try {
			PreparedStatement pst = con.prepareStatement(sql + "\r\n\r\n");
			writeLog(sql);
			int row = pst.executeUpdate();
			rst = row;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			writeLog(e.getMessage());
			System.out.println(sql);
		}
		writeLog("�����Z��Y��:" + rst);
		return rst;
	}

	public void disconn() {
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void writeLog(String str) {
		try {
			String path = new File("").getAbsolutePath();
			path += "/SqlLog.log";
			File file = new File(path);
			if (!file.exists())
				file.createNewFile();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			FileOutputStream out = new FileOutputStream(file, true); // ���׷�ӷ�ʽ��true
			StringBuffer sb = new StringBuffer();
			sb.append("\r\n\r\n-----------" + sdf.format(new Date())
					+ "------------\r\n");
			sb.append(str + "\n");
			out.write(sb.toString().getBytes("utf-8"));// ע����Ҫת����Ӧ���ַ�
			out.close();
		} catch (IOException ex) {
			System.out.println(ex.getStackTrace());
		}
	}

	public JSONObject columns_info(String name) {
		String sql = "SELECT table_schema as s,TABLE_NAME as n,COLUMN_NAME as f,data_type as t,is_nullable as l FROM information_schema.columns WHERE TABLE_NAME = '"
				+ name + "' AND column_default is null";
		JSONObject jsonObj = new JSONObject();
		try {
			// ��ȡ����
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);

//			String must = "";
			// ����ResultSet�е�ÿ�����
			while (rs.next()) {
				String schema = rs.getString("s");
				String table_name = rs.getString("n");
				jsonObj.put("tn", schema+"."+table_name);
				
				String field = rs.getString("f");
				String type = rs.getString("t");
				String isnull = rs.getString("l");
//				if("NO".equalsIgnoreCase(isnull)){
//					must += field+",";
//				}
				JSONObject j = new JSONObject();
				j.put("type", type);
				j.put("nullable", isnull);
				jsonObj.put(field, j);
			}
//			jsonObj.put("Required", must);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonObj;
	}

    public HashMap resultToMap(String sql) {

        HashMap map = new HashMap();
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                String key = "";
                String val = "";
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    String value = rs.getString(columnName);
                    if (i == 1) {
                        key = value;
                    } else {
                        val += "/" + value;
                    }
                }
                map.put(key, val);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return map;
    }

    /**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		PostgresCommon pc = new PostgresCommon();
		pc.conn();
	}

}
