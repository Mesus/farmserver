package com.cbd5.resource.humitrue;

import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.cbd5.Application;
import com.cbd5.PostgresCommon;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
//什么接口？
@Path("/retrieve")
public class Retrieve {

	PostgresCommon pc = Application.pc;
	HashMap tab = Application.tab;

	@Path("jddmandjdmc")
	@GET
	@Produces(value = MediaType.APPLICATION_JSON)
	public String getJson(@QueryParam("jddm") String jddm,@QueryParam("callback") String cb) {
		JSONObject j = new JSONObject();

		StringBuffer sql = new StringBuffer();
		sql.append("select f_name as 农场名称,f_bm as 基地代码");
		sql.append(" from ");
		sql.append("dic_factory");
		sql.append(" where substring(f_bm,0,3)~*'01'");
		if( jddm != null){
			sql.append(" and f_bm='"+jddm+"'");
		}
		JSONArray array = pc.resultSetToJson(sql.toString());
		if (array == null || array.size() == 0) {
			j.put("arr", 0);
		} else {
			j.put("arr", array);
		}
		String tmp = cb + "(" + j + ")";
		return tmp;
	}

	@Path("scmcandpl")
	@GET
	@Produces(value = MediaType.APPLICATION_JSON)
	public String listdata(@QueryParam("pmdm") String pmdm, @QueryParam("callback") String cb) {
		JSONObject j = new JSONObject();

		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append("pm.pm_name as pm,pl.pl_name as pl ");
		sql.append("from ");
		sql.append("pm pm, pl pl ");
		sql.append("where ");
		sql.append("pm.pl_id=pl.\"id\" ");
		sql.append("and pm.\"id\"='"+pmdm+"'");
		JSONArray array = pc.resultSetToJson(sql.toString());
		if (array == null || array.size() == 0) {
			j.put("arr", 0);
		} else {
			j.put("arr", array);
		}
		String tmp = cb + "(" + j + ")";
		return tmp;
	}

	@Path("trjcb_yxzclsj")
	@GET
	@Produces(value = MediaType.APPLICATION_JSON)
	public String deletedata(@QueryParam("condition") String w,@QueryParam("time") String time, @QueryParam("jddm") String jddm,@QueryParam("dkh") String dkh,@QueryParam("callback") String cb) {
		JSONObject j = new JSONObject();
	
		
		StringBuffer sql = new StringBuffer();
		sql.append("select clsj,");
		sql.append(w);
		sql.append(" from ");
		sql.append("humitrue.trjcb ");
		sql.append("where ");
		sql.append("clsj=(select max(clsj) from humitrue.trjcb where "+w+">0 ");
		sql.append("and enable=true");
		
//		Iterator<?> obj=where.keys();
//		while (obj.hasNext()) {// 遍历JSONObject
//			String key = (String) obj.next().toString();
//			String data = where.getString(key);
//			sql.append(" and t1."+key+"='" + data + "' ");
//		}
		if( jddm != null){
			sql.append(" and jddm='" + jddm + "' ");
		}
		if(dkh != null){
			sql.append(" and dkh='" + dkh + "' ");
		}
		if (time != null) {
			String[] timeStrings = time.split("@");
			if (timeStrings.length == 2) {
				sql.append(" and clsj>='" + timeStrings[0] + "' ");
				sql.append(" and clsj<='" + timeStrings[1] + "'");
			}
		}
		sql.append(")");
//		String ob = fields.getString("orderby");
//		sql.append(" order by t1."+ob+" asc");
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

	@Path("func")
	@GET
	@Produces(value = MediaType.APPLICATION_JSON)
	public String listdata(@QueryParam("jddm") String jddm, @QueryParam("dkh") String dkh,
			 @QueryParam("time") String time, @QueryParam("callback") String cb) {
		JSONObject j = new JSONObject();

		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		sql.append("t.clsj,ROUND(AVG(t.wd),2) as wd,ROUND(AVG(t.sd),2) as sd");
		sql.append(" from ");
		sql.append(" (select left(clsj,10) as clsj,wd,sd ");
		sql.append("from humitrue.wsdjc ");
		sql.append("where ");
		sql.append(" enable=true");
		if( jddm != null){
			sql.append(" and jddm='" + jddm + "' ");
		}
		if(dkh != null){
			sql.append(" and dkh='" + dkh + "' ");
		}
		if (time != null) {
			String[] timeStrings = time.split("@");
			if (timeStrings.length == 2) {
				sql.append(" and clsj>='" + timeStrings[0] + "' ");
				sql.append(" and clsj<='" + timeStrings[1] + "'");
			}
		}
		sql.append(" )t");
		sql.append(" group by 1 ");
		sql.append(" order by clsj asc");
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


	@Path("jdmc")
	@GET
	@Produces(value = MediaType.APPLICATION_JSON)
	public String getJdmc(@QueryParam("callback") String cb) {
		JSONObject j = new JSONObject();

		StringBuffer sql = new StringBuffer();
		sql.append("select farm from farm");
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
