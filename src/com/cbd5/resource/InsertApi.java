package com.cbd5.resource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.cbd5.Application;
import com.cbd5.PostgresCommon;
import com.cbd5.TestBean;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Path("/alter")
public class InsertApi {

	PostgresCommon pc = Application.pc;
	HashMap tab = Application.tab;

	@Path("ins")
	@GET
	@Produces(value = MediaType.APPLICATION_JSON)
	public String getJson(@QueryParam("name") String name, @QueryParam("form") String form,
			@QueryParam("user") String user, @QueryParam("callback") String cb) {
		JSONObject j = new JSONObject();

		//
		if (!tab.containsKey(name)) {
			j.put("error", "Table is not found");
			String tmp = cb + "(" + j + ")";
			return tmp;
		}
		JSONObject tab_info = pc.columns_info(tab.get(name).toString());
		if (tab_info.size() == 0) {
			j.put("error", "Related table is not found in current database");
			String tmp = cb + "(" + j + ")";
			return tmp;
		}

		if (user == null) {
			j.put("error", "User info is empty");
			String tmp = cb + "(" + j + ")";
			return tmp;
		}

		JSONArray arr = JSONArray.fromObject(form);

		StringBuffer all = new StringBuffer();
		for (int i = 0; i < arr.size(); i++) {
			JSONObject data = (JSONObject) arr.get(i);
			System.out.println(data.toString());

			Iterator<?> obj = data.keys();
			while (obj.hasNext()) {// 遍历JSONObject
				String key = (String) obj.next().toString();
				if (!tab_info.containsKey(key)) {
					j.put("error", "Field" + key + "is not found in the current database");
					String tmp = cb + "(" + j + ")";
					return tmp;
				}
			}
			//
			String t_name = tab_info.getString("tn");

			StringBuffer sql = new StringBuffer();
			sql.append("INSERT INTO ");
			sql.append(t_name);

			String field = "(";
			String value = "(";
			Iterator<?> objkey = tab_info.keys();
			while (objkey.hasNext()) {// 遍历JSONObject
				String key = (String) objkey.next().toString();
				System.out.println(key);
				if (data.containsKey(key)) {
					field += key + ",";
					JSONObject tmp = (JSONObject) tab_info.get(key);
					String type = tmp.getString("type");
					if ("character varying".equals(type)) {
						value += "'" + data.getString(key) + "',";
					}
					if ("numeric".equals(type)) {
						String num_val = data.getString(key);
						if (num_val.indexOf(".") > 0) {
							int dot = num_val.substring(num_val.indexOf(".") + 1, num_val.length()).length();
							if (dot > 1) {
								num_val = num_val.substring(0, num_val.indexOf(".") + 2);
							}
						}
						value += num_val + ",";
					}
				} else {
					if (!"tn".equals(key)) {
						JSONObject tmp = (JSONObject) tab_info.get(key);
						String nullable = tmp.getString("nullable");
						if ("NO".equalsIgnoreCase(nullable)) {
							if (data.containsKey(key)) {
								String num_val = data.getString(key);
								if (num_val.substring(num_val.indexOf(".") + 1, num_val.length()).length() > 1) {
									num_val = num_val.substring(0, num_val.indexOf(".") + 1);
								}
								value += num_val + ",";
								field += key + ",";
							} else {
								if ("yhm".equals(key)) {
									value += "'" + user + "',";
									field += key + ",";
									continue;
								}
								String type = tmp.getString("type");
								if ("uuid".equals(type)) {
									String uuid = UUID.randomUUID().toString();
									value += "'" + uuid + "',";
									field += key + ",";
								}
								if ("character varying".equals(type)) {
									value += "'" + data.getString(key) + "',";
									field += key + ",";
								}
								if ("numeric".equals(type)) {
									String num_val = data.getString(key);
									if (num_val.indexOf(".") > 0) {
										int dot = num_val.substring(num_val.indexOf(".") + 1, num_val.length())
												.length();
										if (dot > 1) {
											num_val = num_val.substring(0, num_val.indexOf(".") + 1);
										}
									}
									value += num_val + ",";
									field += key + ",";
								}
							}
						}
					}
				}
			}
			field = field.substring(0, field.length() - 1) + ")";
			value = value.substring(0, value.length() - 1) + ")";

			sql.append(field);
			sql.append(" VALUES");
			sql.append(value + ";");
			all.append(sql.toString());
		}
		int rst = pc.insert(all.toString());
		if (rst > 0) {
			j.put("result", "Insert rows " + rst);
		} else {
			j.put("result", "No record is insert");
		}
		String tmp = cb + "(" + j + ")";
		return tmp;
	}

	@Path("list")
	@GET
	@Produces(value = MediaType.APPLICATION_JSON)
	public String listdata(@QueryParam("name") String name, @QueryParam("where") String w,
			@QueryParam("field") String field, @QueryParam("time") String time, @QueryParam("callback") String cb) {
		JSONObject j = new JSONObject();

		//
		if (!tab.containsKey(name)) {
			j.put("error", "Table is not found");
			String tmp = cb + "(" + j + ")";
			return tmp;
		}
		JSONObject tab_info = pc.columns_info(tab.get(name).toString());
		if (tab_info.size() == 0) {
			j.put("error", "Related table is not found in current database");
			String tmp = cb + "(" + j + ")";
			return tmp;
		}
		
		if(field == null){
			j.put("error", "Field is empty");
			String tmp = cb + "(" + j + ")";
			return tmp;
		}
		JSONObject fields = null;
		try {
			 fields = JSONObject.fromObject(field);
		} catch (Exception e) {
			j.put("error", "Field is empty");
			String tmp = cb + "(" + j + ")";
			return tmp;
		}
		
		JSONObject where = null;
		try {
			 where = JSONObject.fromObject(w);
		} catch (Exception e) {
			j.put("error", "Where is abnormality");
			String tmp = cb + "(" + j + ")";
			return tmp;
		}
		
		String f = fields.getString("fields");
		String t_name = tab_info.getString("tn");
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
//		sql.append("t1.xh as id,t2.f_name as 基地名称,t1.dkh as 地块号,t1.wd as \"温度(℃)\",t1.sd as \"湿度(%RH)\",t1.clsj as 测量时间 ");
		sql.append("t1.xh as id,t2.f_name as 基地名称,");
		String[] field_arr = f.split(",");
		for(int i=0; i<field_arr.length; i++){
			sql.append("t1."+field_arr[i]+",");
		}
		sql = sql.deleteCharAt(sql.length()-1);
		sql.append(" from ");
		sql.append(t_name + " t1,");
		sql.append("dic_factory t2 ");
		sql.append("where ");
		sql.append("t1.jddm=t2.f_bm ");
		sql.append("and t1.enable=true");
		
		Iterator<?> obj=where.keys();
		while (obj.hasNext()) {// 遍历JSONObject
			String key = (String) obj.next().toString();
			String data = where.getString(key);
			if("scmc".equals(key)){
				sql.append(" and t1."+key+"~*'" + data + "' ");
			}else{
				sql.append(" and t1."+key+"='" + data + "' ");
			}
		}
		if (time != null) {
			String[] timeStrings = time.split("@");
			if (timeStrings.length == 2) {
				sql.append(" and t1.clsj>='" + timeStrings[0] + "' ");
				sql.append(" and t1.clsj<='" + timeStrings[1] + "'");
			}
		}
		String ob = fields.getString("orderby");
		sql.append(" order by t1."+ob+" asc");
		System.out.println(sql);
		JSONArray array = pc.resultSetToJson(sql.toString());
		if (array == null || array.size() == 0) {
			j.put("arr", 0);
		} else {
			j.put("arr", array);
		}
		String tmp = cb + "(" + j + ")";
		return tmp;
	}

	@Path("del")
	@GET
	@Produces(value = MediaType.APPLICATION_JSON)
	public String deletedata(@QueryParam("name") String name, @QueryParam("id") String uuid,
			@QueryParam("user") String user, @QueryParam("callback") String cb) {
		JSONObject j = new JSONObject();
		//
		if (!tab.containsKey(name)) {
			j.put("error", "Table is not found");
			String tmp = cb + "(" + j + ")";
			return tmp;
		}
		JSONObject tab_info = pc.columns_info(tab.get(name).toString());
		if (tab_info.size() == 0) {
			j.put("error", "Related table is not found in current database");
			String tmp = cb + "(" + j + ")";
			return tmp;
		}

		if (user == null) {
			j.put("error", "User info is empty");
			String tmp = cb + "(" + j + ")";
			return tmp;
		}
		if (uuid != null) {
			String[] arr = uuid.split("@");
			for (int i = 0; i < arr.length; i++) {
				String t_name = tab_info.getString("tn");

				String sql = "UPDATE " + t_name + " SET enable=false,yhm='" + user + "' WHERE xh='" + arr[i] + "'";
				int rst = pc.insert(sql);

				if (rst > 0) {
					j.put("result", "Delete record " + rst);
				} else {
					j.put("result", "No record is delete");
				}
			}
		} else {
			j.put("error", "Id is empty");
		}
		String tmp = cb + "(" + j + ")";
		return tmp;
	}

	@Path("upd")
	@GET
	@Produces(value = MediaType.APPLICATION_JSON)
	public String update(@QueryParam("name") String name, @QueryParam("form") String form,
			@QueryParam("user") String user, @QueryParam("callback") String cb) {
		JSONObject j = new JSONObject();

		//
		if (!tab.containsKey(name)) {
			j.put("error", "Table is not found");
			String tmp = cb + "(" + j + ")";
			return tmp;
		}
		JSONObject tab_info = pc.columns_info(tab.get(name).toString());
		if (tab_info.size() == 0) {
			j.put("error", "Related table is not found in current database");
			String tmp = cb + "(" + j + ")";
			return tmp;
		}

		if (user == null) {
			j.put("error", "User info is empty");
			String tmp = cb + "(" + j + ")";
			return tmp;
		}

		JSONArray arr = JSONArray.fromObject(form);

		StringBuffer all = new StringBuffer();
		for (int i = 0; i < arr.size(); i++) {
			JSONObject data = (JSONObject) arr.get(i);
			System.out.println(data.toString());

			Iterator<?> obj = data.keys();
			while (obj.hasNext()) {// 遍历JSONObject
				String key = (String) obj.next().toString();
				if (!tab_info.containsKey(key)) {
					j.put("error", "Field" + key + "is not found in the current database");
					String tmp = cb + "(" + j + ")";
					return tmp;
				}
			}
			//
			String t_name = tab_info.getString("tn");

			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE ");
			sql.append(t_name);
			sql.append(" SET ");
			Iterator<?> objkey = tab_info.keys();
			while (objkey.hasNext()) {// 遍历JSONObject
				String key = (String) objkey.next().toString();
				System.out.println(key);
				if (data.containsKey(key)) {
					JSONObject tmp = (JSONObject) tab_info.get(key);
					String type = tmp.getString("type");
					if ("character varying".equals(type)) {
						sql.append(key + "='" + data.getString(key) + "',");
					}
					if ("numeric".equals(type)) {
						String num_val = data.getString(key);
						if (num_val.indexOf(".") > 0) {
							int dot = num_val.substring(num_val.indexOf(".") + 1, num_val.length()).length();
							if (dot > 1) {
								num_val = num_val.substring(0, num_val.indexOf(".") + 2);
							}
						}
						sql.append(key + "=" + num_val + ",");
					}
				}

			}

			sql.append("yhm='" + user + "'");
			if (data.containsKey("xh")) {
				String xh = data.getString("xh");
				sql.append(" where xh='" + xh + "';");
			} else {
				j.put("error", "Id is empty");
				String tmp = cb + "(" + j + ")";
				return tmp;
			}
			all.append(sql.toString());
		}
		int rst = pc.insert(all.toString());
		if (rst > 0) {
			j.put("result", "Update rows " + rst);
		} else {
			j.put("result", "No record is Update");
		}

		String tmp = cb + "(" + j + ")";
		return tmp;
	}

	@Path("query")
	@GET
	@Produces(value = MediaType.APPLICATION_JSON)
	public String querydata(@QueryParam("name") String name, @QueryParam("id") String uuid,
			@QueryParam("callback") String cb,@QueryParam("field") String field) {
		
		JSONObject j = new JSONObject();

		//
		if (!tab.containsKey(name)) {
			j.put("error", "Table is not found");
			String tmp = cb + "(" + j + ")";
			return tmp;
		}
		JSONObject tab_info = pc.columns_info(tab.get(name).toString());
		if (tab_info.size() == 0) {
			j.put("error", "Related table is not found in current database");
			String tmp = cb + "(" + j + ")";
			return tmp;
		}
		if(field == null){
			j.put("error", "Field is empty");
			String tmp = cb + "(" + j + ")";
			return tmp;
		}
		JSONObject fields = null;
		try {
			 fields = JSONObject.fromObject(field);
		} catch (Exception e) {
			j.put("error", "Field is empty");
			String tmp = cb + "(" + j + ")";
			return tmp;
		}
		String f = fields.getString("fields");
		String t_name = tab_info.getString("tn");
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append(f);
		sql.append(" from ");
		sql.append(t_name );
		sql.append(" where ");
		sql.append(" enable=true");
		sql.append(" and xh='" + uuid + "'");
		System.out.println(sql.toString());
		JSONArray array = pc.resultSetToJson(sql.toString());
		if (array == null || array.size() == 0) {
			j.put("arr", 0);
		} else {
			j.put("arr", array);
		}
		String tmp = cb + "(" + j + ")";
		return tmp;
	}
}
