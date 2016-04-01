package com.cbd5.resource;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;

import com.cbd5.Application;
import com.cbd5.PostgresCommon;

@Path("/sctr_stat")
public class GetSctrbStat {

	static PostgresCommon pc = Application.pc;

	@Path("w")
	@GET
	@Produces(value = MediaType.APPLICATION_JSON)
	public String oiw(@QueryParam("farm") String farm, @QueryParam("time") String time,
			@QueryParam("callback") String cb) {
		JSONObject j = new JSONObject();

		int err = 0;

		if (farm == null) {
			farm = "N";
		}
		if (time == null) {
			time = "N";
		} else {
			String[] timeStrings = time.split("@");
			if (timeStrings.length <= 1) {
				err = 2;
			}
		}
		if (cb == null) {
			err = 4;
		}

		if (err > 0) {
			j.put("error", err);
			String tmp = cb + "(" + j + ")";
			return tmp;
		}

		// Jedis je = Application.jedis;
		// String key = "SctrbStat:"+farm+"#"+time;
		// boolean key_ex =je .exists(key);
		// if(key_ex){
		// String tmp = cb+"("+je.get(key)+")";
		// return tmp;
		// }

		JSONArray array = new JSONArray();

		GetSctrbStat gss = new GetSctrbStat();

		String rday = getrday();
		j.put("zz", gss.crkzb(farm, time, "1", rday));
		j.put("fl", gss.crkzb(farm, time, "3", rday));
		j.put("swzj", gss.crkzb(farm, time, "2", rday));
		j.put("wz", gss.crkzb(farm, time, "4", rday));
		System.out.println("111111111111");
		 if("N".equals(farm)){
			 JSONObject tmp = gss.eachBase(farm, time, "1", rday);
			 j.put("cate", tmp.get("cate"));
			 j.put("zz_rkl",tmp.get("rkl"));
			 j.put("zz_cksl",tmp.get("ckl"));
			 j.put("zz_kcl",tmp.get("kcl"));
			 
			 tmp = gss.eachBase(farm, time, "2", rday);
			 j.put("fl_rkl",tmp.get("rkl"));
			 j.put("fl_cksl",tmp.get("ckl"));
			 j.put("fl_kcl",tmp.get("kcl"));
			 
			 tmp = gss.eachBase(farm, time, "3", rday);
			 j.put("swzj_rkl",tmp.get("rkl"));
			 j.put("swzj_cksl",tmp.get("ckl"));
			 j.put("swzj_kcl",tmp.get("kcl"));
			 
			 tmp = gss.eachBase(farm, time, "4", rday);
			 j.put("wz_rkl",tmp.get("rkl"));
			 j.put("wz_cksl",tmp.get("ckl"));
			 j.put("wz_kcl",tmp.get("kcl"));
//		 String farm_dic = gss.farm_dic();
//		 j.put("cate", farm_dic);
		 
		//// String farm_dic = "北京,上海,广州,扬州,成都,山东,云南,惠州";
		// JSONObject all = gss.stat(farm_dic, time);
		//
		// j.put("zz_rkl",all.get("zz_rkl"));
		//
		// j.put("zz_cksl",all.get("zz_cksl"));
		//
		// j.put("zz_kcl",all.get("zz_kcl"));
		//
		// j.put("fl_rkl",all.get("fl_rkl"));
		//
		// j.put("fl_cksl",all.get("fl_cksl"));
		//
		// j.put("fl_kcl",all.get("fl_kcl"));
		//
		// j.put("swzj_rkl",all.get("swzj_rkl"));
		//
		// j.put("swzj_cksl",all.get("swzj_cksl"));
		//
		// j.put("swzj_kcl",all.get("swzj_kcl"));
		//
		// j.put("wz_rkl",all.get("wz_rkl"));
		//
		// j.put("wz_cksl",all.get("wz_cksl"));
		//
		// j.put("wz_kcl",all.get("wz_kcl"));
		//
		 }
		if (array == null || array.size() == 0) {
			j.put("arr", 0);
		}
		// je.set(key, j.toString());
		// je.expire(key, 1800);
		System.out.println(j.toString());
		String tmp = cb + "(" + j + ")";
		return tmp;
	}

	public String getrday() {
		String rday_sql = "SELECT value FROM config WHERE name='盘库日'";
		String rday = pc.query(rday_sql);
		return rday;
	}
	private Double[] ck_rk(String farm, String time, String s_type, String rday) {
		Double[] crk = new Double[2];
		JSONObject json = new JSONObject();
		String[] time_arr = time.split("@");
		StringBuffer sbsql = new StringBuffer();


		//盘库临时表表名,全国还是某个基地
		String rday_tablename = rday;
		if(!"N".equals(farm)){
			rday_tablename = farm+"_"+rday;
		}
		//end
		
		//创建临时表
		GetSctrbZb gsz = new GetSctrbZb();
		gsz.get_rday( farm, rday_tablename, rday);
		//end
		sbsql.append("SELECT ");
		sbsql.append("sum(b1.rksl) as rksl,sum(b2.cksl) as cksl");
		sbsql.append(" FROM ");
		sbsql.append("(");
		
		sbsql.append("SELECT ");

		sbsql.append("t1.s_id as sid,");
		sbsql.append("SUM(t1.s_rkje) AS rksl");

		sbsql.append(" FROM sctrb.sctrb_rk t1");
		sbsql.append(" WHERE ");
		sbsql.append(" t1.s_type = '" + s_type + "'");
		sbsql.append(" AND t1.s_orid ");
		sbsql.append("in ");
		sbsql.append("(SELECT o_id FROM sctrb.ori_data WHERE");
		if (!"N".equals(farm)) {
			sbsql.append(" o_name LIKE '%" + farm + "%' ");
			sbsql.append("and");
		}
		sbsql.append(" o_timeflag>='" + time_arr[0] + "' and o_timeflag<='" + time_arr[1] + "')");
		sbsql.append(" GROUP BY 1");
		sbsql.append(") b1,");
		
		sbsql.append("(");
		
		sbsql.append("SELECT ");

		sbsql.append("t1.rkid AS rkid,");
		sbsql.append("SUM(t1.s_ckje) AS cksl");

		sbsql.append(" FROM sctrb.sctrb_ck t1");
		sbsql.append(" WHERE ");
		sbsql.append(" t1.s_type = '" + s_type + "'");
		sbsql.append(" AND t1.s_orid ");
		sbsql.append("in ");
		sbsql.append("(SELECT o_id FROM sctrb.ori_data WHERE");
		if (!"N".equals(farm)) {
			sbsql.append(" o_name LIKE '%" + farm + "%' ");
			sbsql.append("and");
		}
		sbsql.append(" o_timeflag>='" + time_arr[0] + "' and o_timeflag<='" + time_arr[1] + "')");
		sbsql.append(" GROUP BY 1");
		sbsql.append(") b2 ");
		sbsql.append("WHERE b1.sid = b2.rkid");
		System.out.println(sbsql.toString());
		json = pc.resultToJson(sbsql.toString());
//		String kcl_str = "";
//		Double kcl = 0.0;
		Double rkl = 0.0;
		Double ckl = 0.0;
		if (json.get("rksl") != null) {
			rkl = Double.parseDouble(json.get("rksl").toString());
		}else{
//			json.put("rksl", rkl);
		}
		crk[0] = rkl;
		if (json.get("cksl") != null) {
			ckl = Double.parseDouble(json.get("cksl").toString());
		}else{
//			json.put("cksl", ckl);
		}
		crk[1] = ckl;
		
		return crk;
	}
	/**
	 * 种子出入库总表
	 * 
	 * @param farm
	 * @param time
	 * @return
	 */
	private JSONObject crkzb(String farm, String time, String s_type, String rday) {
		JSONObject json = new JSONObject();
		String[] time_arr = time.split("@");
		String t_start = time_arr[0];
//		StringBuffer sbsql = new StringBuffer();

//		sbsql.append("SELECT ");

		//盘库临时表表名,全国还是某个基地
//		String rday_tablename = rday;
//		if(!"N".equals(farm)){
//			rday_tablename = farm+"_"+rday;
//		}
		//end
		
		//创建临时表
//		GetSctrbZb gsz = new GetSctrbZb();
//		gsz.get_rday( farm, rday_tablename, rday);
		//end

//		sbsql.append("SUM (t1.s_rkje) AS rksl,");
//		sbsql.append("SUM (t2.s_ckje) AS cksl");

		// 查询一天并且是盘库日
		boolean one_rday = false;
		String time_start = time_arr[0].substring(0, 8);
		if (time_start.equals(time_arr[1].substring(0, 8))) {
			if (time_arr[1].substring(0, 8).equals(rday)) {
				one_rday = true;
			}
		}
		if (!one_rday) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				Date timestart = sdf.parse(rday);
				Calendar c = Calendar.getInstance();
				c.setTime(timestart); // 设置当前日期
				c.add(Calendar.DATE, 1); // 日期加1天
				Date date = c.getTime();
				t_start = sdf.format(date) + "00";
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		sbsql.append(" FROM sctrb.sctrb_rk t1,sctrb.sctrb_ck t2");
//		sbsql.append(" WHERE t1.s_id = t2.rkid");
//		sbsql.append(" AND t1.s_type = '" + s_type + "'");
//		sbsql.append(" AND t2.s_type = '" + s_type + "'");
//		sbsql.append(" AND t1.s_orid ");
//		sbsql.append("in ");
//		sbsql.append("(SELECT o_id FROM sctrb.ori_data WHERE");
//		if (!"N".equals(farm)) {
//			sbsql.append(" o_name LIKE '%" + farm + "%' ");
//			sbsql.append("and");
//		}
//		sbsql.append(" o_timeflag>='" + t_start + "' and o_timeflag<='" + time_arr[1] + "')");
//		System.out.println(sbsql.toString());
//		json = pc.resultToJson(sbsql.toString());
		String kcl_str = "";
		Double kcl = 0.0;
//		Double rkl = 0.0;
//		Double ckl = 0.0;
//		if (json.get("rksl") != null) {
//			rkl = Double.parseDouble(json.get("rksl").toString());
//		}else{
//			json.put("rksl", rkl);
//		}
//		if (json.get("cksl") != null) {
//			ckl = Double.parseDouble(json.get("cksl").toString());
//		}else{
//			json.put("cksl", ckl);
//		}
		Double[] crk = new Double[2];
		GetSctrbStat gss = new GetSctrbStat();
		crk = gss.ck_rk(farm, time, s_type, rday);
		if (one_rday) {
			kcl = crk[0] - crk[1];
			kcl_str = String.valueOf(kcl);
		} else {
			kcl = crkzb1(farm, s_type, rday);
			String kc_crktime = t_start+"@"+time_arr[1];
			Double[] kccrk = gss.ck_rk(farm, kc_crktime, s_type, rday);
			kcl = kcl + kccrk[0] - kccrk[1];
			kcl_str = String.valueOf(kcl);
			if (kcl_str.substring(kcl_str.indexOf(".") + 1, kcl_str.length()).length() > 3) {
				kcl_str = kcl_str.substring(0, kcl_str.indexOf(".") + 3);
			}
		}
		json.put("rksl", crk[0]);
		json.put("cksl", crk[1]);
		json.put("kcl", kcl_str);
		return json;
	}

	private Double crkzb1(String farm, String s_type, String rday) {
		String tn = rday;
		if (!"N".equals(farm)) {
			tn = farm + "_" + rday;
		}
		JSONObject json = new JSONObject();
		StringBuffer sbsql = new StringBuffer();

		sbsql.append("SELECT ");

		sbsql.append("SUM(t4.kczj) as kczj");
		sbsql.append(" FROM sctrb.\"" + tn + "\" t4");
		sbsql.append(" WHERE t4.lx='" + s_type + "'");
		System.out.println(sbsql.toString());
		json = pc.resultToJson(sbsql.toString());
		Double d2 = 0.0;
		if (json.get("kczj") != null) {
			d2 = Double.parseDouble(json.get("kczj").toString());
		}
		return d2;
	}

	public boolean containRday(String farm, String qtime, String time) {
		String[] time_arr = time.split("@");
		StringBuffer sbsql = new StringBuffer();
		sbsql.append("SELECT count(*) FROM ");
		sbsql.append("sctrb.ori_data WHERE ");
		if (!"N".equals(farm)) {
			sbsql.append(" o_name ~*'" + farm + "' ");
			sbsql.append("and");
		}
		sbsql.append("'" + qtime + "' in ");
		sbsql.append("(SELECT DISTINCT substring(o_timeflag,0,9) ");
		sbsql.append("FROM sctrb.ori_data WHERE ");
		sbsql.append("o_timeflag BETWEEN ");
		sbsql.append("'" + time_arr[0] + "' AND '" + time_arr[1] + "' ");
		if (!"N".equals(farm)) {
			sbsql.append("and");
			sbsql.append(" o_name ~*'" + farm + "' ");
		}
		sbsql.append(" )");
		boolean contain = false;
		int r = Integer.parseInt(pc.query(sbsql.toString()));
		if (r > 0) {// 时间区间内含有盘库日
			contain = true;
		}
		return contain;
	}

	private String maxtime(String time, String farm) {
		String[] time_arr = time.split("@");
		StringBuffer sbsql = new StringBuffer();
		sbsql.append("SELECT max(o_timeflag) FROM sctrb.ori_data WHERE");
		if (!"N".equals(farm)) {
			sbsql.append(" o_name LIKE '%" + farm + "%' ");
			sbsql.append("and");
		}

		sbsql.append(" o_timeflag>='" + time_arr[0] + "' and o_timeflag<='" + time_arr[1] + "'");
		String max = pc.query(sbsql.toString());
		return max;
	}

	private void get_rday(String type, String farm, String time) {
		String tn = time;
		if (!"N".equals(farm)) {
			tn = farm + "_" + time;
		}
		int ex = Integer.parseInt(pc.query("select count(*) from pg_class where relname = '" + tn + "'"));
		if (ex == 0) {
			String rday_sql = "SELECT value FROM config WHERE name='盘库日'";
			String rday = pc.query(rday_sql);
			String q_time = time + rday;

			StringBuffer sb = new StringBuffer();
			sb.append("CREATE TABLE sctrb.\"" + tn + "\" AS");
			sb.append(" SELECT");
			sb.append(" t1.s_pmbm as pmbm,t1.s_type as lx,");
			sb.append("SUM (t1.s_rkl) - SUM (t2.s_ckl) AS kcsl,");
			sb.append("SUM (t1.s_rkl * t3.dj) - SUM (t2.s_ckl * t3.dj) AS kczj");
			sb.append(" FROM");
			sb.append(" sctrb.sctrb_rk t1,sctrb.sctrb_ck t2,dic_pm t3");
			sb.append(" WHERE");
			sb.append(" t1.s_pmbm = t3.pmbm");
			sb.append(" AND t1.s_id = t2.rkid");
			// sb.append(" AND t1.s_type = '"+type+"'");
			sb.append(" AND t1.s_orid IN (");
			sb.append("SELECT o_id FROM sctrb.ori_data WHERE o_name LIKE '%" + farm + "%' AND o_timeflag LIKE '%"
					+ q_time + "%'");
			sb.append(")");
			sb.append(" GROUP BY 1,2 ORDER BY 1");

			pc.insert(sb.toString());
		}
	}

	private JSONObject drilldown_crk(String time, String crk, String sheet) {
		String[] time_arr = time.split("@");
		StringBuffer sbsql = new StringBuffer();

		sbsql.append("SELECT ");
		sbsql.append("DISTINCT farm,");
		sbsql.append("sum(cast((case when " + crk + "='' then '0' when " + crk + " is null then '0' else " + crk
				+ " end) as numeric)) ");
		sbsql.append("FROM sctrb WHERE ");
		sbsql.append("insert_time>='" + time_arr[0] + "' ");
		sbsql.append("AND insert_time<='" + time_arr[1] + "' ");
		sbsql.append("AND sheet LIKE '%" + sheet + "%' ");
		sbsql.append("GROUP BY 1");
		JSONObject array = pc.resultKVToJson(sbsql.toString());

		return array;
	}

	private JSONObject drilldown_kcl(String time, String rk, String ck, String sheet) {
		String[] time_arr = time.split("@");
		StringBuffer sbsql = new StringBuffer();

		sbsql.append("SELECT ");
		sbsql.append("DISTINCT farm,");
		sbsql.append("sum(cast((case when " + rk + "='' then '0' when " + rk + " is null then '0' else " + rk
				+ " end) as numeric))-sum(cast((case when " + ck + "='' then '0' when " + ck + " is null then '0' else "
				+ ck + " end) as numeric)) ");
		sbsql.append("FROM sctrb WHERE ");
		sbsql.append("insert_time>='" + time_arr[0] + "' ");
		sbsql.append("AND insert_time<='" + time_arr[1] + "' ");
		sbsql.append("AND sheet LIKE '%" + sheet + "%' ");
		sbsql.append("GROUP BY 1");
		JSONObject array = pc.resultKVToJson(sbsql.toString());

		return array;
	}

	private static String farm_dic() {
		String sql = "select farm from farm";
		String farmString = pc.queryStrArr(sql);
		return farmString;
	}

	private JSONObject eachBase(String farm, String time, String s_type, String rday) {
		JSONObject json = new JSONObject();
		String[] time_arr = time.split("@");
		String t_start = time_arr[0];
		StringBuffer sbsql = new StringBuffer();

		sbsql.append("SELECT ");

		//盘库临时表表名,全国还是某个基地
		String rday_tablename = rday;
		//end
		
		//创建临时表
		GetSctrbZb gsz = new GetSctrbZb();
		gsz.get_rday( farm, rday_tablename, rday);
		//end
		sbsql.append("substring(t3.o_name,0,3) AS farm,");
		sbsql.append("SUM (t1.s_rkje) AS rksl,");
		sbsql.append("SUM (t2.s_ckje) AS cksl,");
		sbsql.append("SUM (t1.s_rkje)-SUM (t2.s_ckje) AS kcsl");
		// 查询一天并且是盘库日
		boolean one_rday = false;
		String time_start = time_arr[0].substring(0, 8);
		if (time_start.equals(time_arr[1].substring(0, 8))) {
			if (time_arr[1].substring(0, 8).equals(rday)) {
				one_rday = true;
			}
		}else{
			if (containRday(farm,rday,time)) {
				try {
					SimpleDateFormat sdf =   new SimpleDateFormat( "yyyyMMdd");
					Date timestart = sdf.parse(time_start);
					Calendar c = Calendar.getInstance();  
					c.setTime(timestart);   //设置当前日期  
					c.add(Calendar.DATE, 1); //日期加1天  
					Date date = c.getTime();
					t_start = sdf.format(date)+"00";
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		sbsql.append(" FROM sctrb.sctrb_rk t1,sctrb.sctrb_ck t2,sctrb.ori_data t3");
		sbsql.append(" WHERE t1.s_id = t2.rkid");
		sbsql.append(" AND t1.s_type = '" + s_type + "'");
		sbsql.append(" AND t2.s_type = '" + s_type + "'");
		sbsql.append(" AND t1.s_orid=t3.o_id");
		sbsql.append(" AND t1.s_orid ");
		sbsql.append("in ");
		sbsql.append("(SELECT o_id FROM sctrb.ori_data WHERE");
		if (!"N".equals(farm)) {
			sbsql.append(" o_name LIKE '%" + farm + "%' ");
			sbsql.append("and");
		}
		sbsql.append(" o_timeflag>='" + t_start + "' and o_timeflag<='" + time_arr[1] + "')");
		sbsql.append(" GROUP BY 1 ORDER BY 1");
		System.out.println(sbsql.toString());
		JSONObject json_sql = pc.resultToJsonForEachBase(sbsql.toString());
		if(json_sql.size() > 0){
			json.put("cate", json_sql.get("cate").toString());
			json.put("rkl", json_sql.get("rkje").toString());
			json.put("ckl", json_sql.get("ckje").toString());
			
			if (!one_rday) {
				String[] f = json_sql.get("cate").toString().split(",");
				String[] kc = json_sql.get("kcje").toString().split(",");
				String kcl = "";
				for (int i = 0; i < f.length; i++) {
					Double zb1 = crkzb1(f[i], s_type, rday);
					Double kcl_d = zb1 - Double.parseDouble(kc[i].toString());
					String kcl_str = Double.toString(kcl_d);
					if(kcl_str.substring(kcl_str.indexOf("."),kcl_str.length()).length() > 4){
						kcl_str = kcl_str.substring(0, kcl_str.indexOf(".")+4);
					}
					kcl += kcl_str + ",";
				}
				json.put("kcl", kcl.substring(0, kcl.length() - 1));
			} else {
				json.put("kcl", json_sql.get("kcje").toString());
			}
		}
		else{
			String fram = this.farm_dic();
			String[] f = fram.split(",");
			json.put("cate", fram);
			String kcl = "";
			for (int i = 0; i < f.length; i++) {
				Double zb1 = crkzb1(f[i], s_type, rday);
				kcl += zb1 + ",";
			}
			json.put("kcl", kcl.substring(0, kcl.length() - 1));
			
			json.put("rkl", "0,0,0,0,0,0,0,0");
			json.put("ckl", "0,0,0,0,0,0,0,0");
		}
		
		
//		json.put("kcl", kcl_str);
		System.out.println(json.toString());
		return json;
	}
}
