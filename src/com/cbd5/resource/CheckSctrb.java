package com.cbd5.resource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.cbd5.Application;
import com.cbd5.PostgresCommon;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;


@Path("/sctrbchk")
public class CheckSctrb {

	PostgresCommon pc = Application.pc;
	
	@Path("q")
	@GET
	@Produces(value=MediaType.APPLICATION_JSON)
    public String total(@QueryParam("type") String type,@QueryParam("farm") String farm,@QueryParam("time") String time,@QueryParam("callback") String cb){
		
		JSONObject j = new JSONObject();
		
		
		JSONArray array = new JSONArray();
		
			time = time.replaceAll("/", "");
			time += "24";
			
			CheckSctrb gsz = new CheckSctrb();
			String m_name = gsz.covertType(Integer.parseInt(type));
			
		if(m_name.indexOf("蔬菜育苗") != -1){
			array = yjscymzb(farm, time);
		}
		
		if(m_name.indexOf("蔬菜种植表") != -1){
			array = sczzzb(farm, time);
		}
		
		String[] types = {"种子","生物制剂","肥料","物资"};
		for (int i = 0; i < types.length; i++) {
			if(m_name.indexOf(types[i]) != -1){
				array = zb(farm, time, types[i]);
				break;
			}
		}
			
		
		
		
		if(array == null || array.size() == 0){
			j.put("arr", 0);
		}else{
			j.put("arr", array);
		}
		String tmp = cb+"("+j+")";
    	return tmp;
    }

	//按照类型转换表名
	public String covertType(int type){
		String sql = "select t_name from correspond where xh="+type+"";
		String t_name = pc.query(sql);
		return t_name;
	}
	/**
	 * 种子出入库总表
	 * @param farm
	 * @param time
	 * @return
	 */
	private JSONArray zb(String farm,String time,String type){
		JSONArray array = new JSONArray();
//		String[] time_arr = time.split("@");
		String t_start = "";
		
		StringBuffer outside = new StringBuffer();
		outside.append("SELECT DISTINCT b1.pmbm as 品名编码,");
		StringBuffer b1 = new StringBuffer();
		b1.append("(SELECT t2.s_pmbm as pmbm,");
		String s_type = "1";
		String gb = " GROUP BY 1,2,3,4,5,6,7";
		if("种子".equals(type)){
//			outout.append("kcb.pl as 品类,kcb.pm as 品名,kcb.spm as 种子品种,");
			outside.append("b1.pl as 品类,b1.pm as 品名,b1.spm as 种子品种,");
			b1.append("t1.pl as pl,t1.pm as pm,t1.spm as spm,");
		}
		if("生物制剂".equals(type)){
//			outout.append("kcb.pm as 生物制剂品名,kcb.spm as 商品名,");
			outside.append("b1.pm as 生物制剂品名,b1.spm as 商品名,");
			b1.append("t1.pm as pm,t1.spm as spm,");
			s_type = "2";
			gb = " GROUP BY 1,2,3,4,5,6";
		}
		if("肥料".equals(type)){
//			outout.append("kcb.pm as 肥料品名,kcb.spm as 商品名,");
			outside.append("b1.pm as 肥料品名,b1.spm as 商品名,");
			b1.append("t1.pm as pm,t1.spm as spm,");
			s_type = "3";
			gb = " GROUP BY 1,2,3,4,5,6";
		}
		if("物资".equals(type)){
//			outout.append("kcb.pm as 农用物资品名,");
			outside.append("b1.pm as 农用物资品名,");
			b1.append("t1.pm as pm,");
			s_type = "4";
			gb = " GROUP BY 1,2,3,4,5";
		}
		
		CheckSctrb gsz = new CheckSctrb();
		GetSctrbStat gss = new GetSctrbStat();
		
		String rday = gss.getrday();
		
		String rday_tablename = rday;
		if(!"N".equals(farm)){
			rday_tablename = farm+"_"+rday;
		}
		
		// 查询一天并且是盘库日
		boolean one_rday = false;
//		String time_start = time_arr[0].substring(0, 8);
//		if (time_start.equals(time_arr[1].substring(0, 8))) {
//			if (time_arr[1].substring(0, 8).equals(rday)) {
//				one_rday = true;
//			}
//		}
//		if (!one_rday) {
//			if (gss.containRday(farm,rday,time)) {
				try {
					SimpleDateFormat sdf =   new SimpleDateFormat( "yyyyMMdd");
					Date timestart = sdf.parse(rday);
					Calendar c = Calendar.getInstance();  
					c.setTime(timestart);   //设置当前日期  
					c.add(Calendar.DATE, 1); //日期加1天  
					Date date = c.getTime();
					t_start = sdf.format(date)+"00";
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//			}
//		}
		boolean create = gsz.get_rday( farm, rday_tablename, rday);
		if(!create){
			rday_tablename = "zero";
		}
//		outout.append("kcb.gys as 供应商,kcb.gg as 规格,kcb.dw as 单位,");
		outside.append("b1.gys as 供应商,b1.gg as 规格,b1.dw as 单位,");
		b1.append("t1.gys as gys,t1.gg as gg,t1.dw as dw,");
		
//		outout.append("crkb.rksl as 入库数量,crkb.rkzj AS 入库总价,");
//		outside.append("b1.rksl as rksl,b1.rkzj AS rkzj,");
		b1.append("SUM(t2.s_rkl) as rksl,SUM(t2.s_rkje) AS rkzj");
		
//		outout.append("crkb.cksl as 出库数量,crkb.ckzj AS 出库总价,");
//		outside.append("b0.cksl as cksl,b0.ckzj AS ckzj,");
		
//		outout.append("kcb.kcsl AS 库存数量,kcb.kczj AS 库存总价");
		
//		if(one_rday){
//			outside.append("(b1.rksl - b0.cksl) AS kcsl,(b1.rkzj - b0.ckzj) AS kczj");
//		}else{
			outside.append("(b2.kcsl + b1.rksl - b0.cksl) AS 库存数量,(b2.kczj+b1.rkzj-b0.ckzj) AS 库存总价");
//		}
//		outout.append(" FROM ");
		b1.append(" FROM dic_pm t1,sctrb.sctrb_rk t2");
		b1.append(" WHERE t1.pmbm = t2.s_pmbm");
//		sbsql.append(" AND t2.s_id = t3.rkid");
		b1.append(" AND t2.s_type='"+s_type+"'");
//		sbsql.append(" AND t3.s_type='"+s_type+"'");
		b1.append(" AND t2.s_orid ");
		b1.append("in ");
		b1.append("(SELECT o_id FROM sctrb.ori_data WHERE");
		if(!"N".equals(farm)){
			b1.append(" o_name LIKE '%"+farm+"%' ");
			b1.append("and");
		}
		b1.append(" o_timeflag>='"+t_start+"' and o_timeflag<='"+time+"')");
//		sbsql.append(" AND t2.s_rkl+t3.s_ckl > 0");
		b1.append(gb);
		b1.append(" ORDER BY 1");
		b1.append(" ) b1");
		b1.append(",");
		//
		StringBuffer b0 = new StringBuffer();
		b0.append("(");
		b0.append("SELECT ");
		b0.append("t2.s_pmbm as pmbm,");
		b0.append("SUM(t2.s_ckl) AS cksl,");
		b0.append("SUM(t2.s_ckje) AS ckzj ");
		b0.append("FROM ");
		b0.append("dic_pm t1,sctrb.sctrb_ck t2 ");
		b0.append("WHERE ");
		b0.append("t1.pmbm = t2.s_pmbm ");
		b0.append(" AND t2.s_type='"+s_type+"'");
		b0.append(" AND t2.s_orid ");
		b0.append("in ");
		b0.append("(SELECT o_id FROM sctrb.ori_data WHERE");
		if(!"N".equals(farm)){
			b0.append(" o_name LIKE '%"+farm+"%' ");
			b0.append("and");
		}
		b0.append(" o_timeflag>='"+t_start+"' and o_timeflag<='"+time+"') ");
		b0.append(" GROUP BY 1");
		b0.append(") b0");
		
		StringBuffer b2 = new StringBuffer();
		if(!one_rday){
			b2.append(",");
			b2.append("(SELECT pmbm,SUM (kcsl) AS kcsl,SUM (kczj) AS kczj");
			b2.append(" FROM ");
			b2.append("sctrb.\""+rday_tablename+"\" ");
			b2.append("WHERE lx='"+s_type+"'");
			b2.append("GROUP BY 1");
			b2.append(") b2");
			b2.append(" WHERE b1.pmbm = b2.pmbm");
			b2.append(" AND b0.pmbm = b2.pmbm");
			b2.append(" and (b2.kcsl + b1.rksl - b0.cksl)<0");
//			b2.append(" AND abs(b2.kcsl)+abs(b1.rksl)+abs(b0.cksl)>0 ");
		}
		outside.append(" FROM ");
		outside.append(b1.toString());
		outside.append(b0.toString());
		outside.append(b2.toString());
//		outside.append(" ) as kcb,");
		
		
		System.out.println(type+"出入库总表:"+outside.toString());
		array = pc.resultSetToJson(outside.toString());
		return array;
	}
	/**
	 * 创建盘库临时表
	 * @param farm 农场名称
	 * @param rday_tablename 盘库数据临时表表名
	 * @param rday 盘库日(yyyyMMdd)
	 */
	public boolean get_rday(String farm,String rday_tablename,String rday){
		boolean r = true;
		String f = "";
		if(!"N".equals(farm)){
			f = "o_name LIKE '%"+farm+"%' AND";
		}
		int ex = Integer.parseInt(pc.query("select count(*) from pg_class where relname = '"+rday_tablename+"'"));
		if(ex == 0){
//			String q_time = time+rday;
			String ori_sql = "SELECT count(*) FROM sctrb.ori_data WHERE  "+f+" o_timeflag LIKE '%"+rday+"%'";
			System.out.println("查询盘库临时表:"+ori_sql);
			int oir = Integer.parseInt(pc.query(ori_sql) );
			if(oir > 0){
				StringBuffer sb = new StringBuffer();
				sb.append("CREATE TABLE sctrb.\""+rday_tablename+"\" AS");
				sb.append(" SELECT");
				sb.append(" t1.s_pmbm as pmbm,t1.s_type as lx,");
				sb.append("SUM (t1.s_rkl) - SUM (t2.s_ckl) AS kcsl,");
				sb.append("SUM (t1.s_rkl * t3.dj) - SUM (t2.s_ckl * t3.dj) AS kczj");
				sb.append(" FROM");
				sb.append(" sctrb.sctrb_rk t1,sctrb.sctrb_ck t2,dic_pm t3");
				sb.append(" WHERE");
				sb.append(" t1.s_pmbm = t3.pmbm");
				sb.append(" AND t1.s_id = t2.rkid");
//			sb.append(" AND t1.s_type = '"+type+"'");
				sb.append(" AND t1.s_orid IN (");
				sb.append("SELECT o_id FROM sctrb.ori_data WHERE "+f+" o_timeflag LIKE '%"+rday+"%'");
				sb.append(")");
				sb.append(" GROUP BY 1,2 ORDER BY 1");
				
				pc.insert(sb.toString());
			}else{
				r= false;
			}
		}
		return r;
	}
	/**
	 * 生物制剂出入库总表
	 * @param farm
	 * @param time
	 * @return
	 */
	private JSONArray q_rday(String farm,String time,String type){
		JSONArray array = new JSONArray();
		String[] time_arr = time.split("@");
		StringBuffer sbsql = new StringBuffer();
		sbsql.append("SELECT ");
		sbsql.append("ROW_NUMBER () OVER () AS 序号,pl as 品类,pm as 品名,zzpz as 种子品种,gys as 供应商,gg as 规格,jldw as 计量单位,");
		sbsql.append("sum(rkl) as 入库数量,");
		sbsql.append("sum(rkl*price) as \"入库总价（元）\",");
		sbsql.append("sum(cksl) as 出库数量,");
		sbsql.append("sum(cksl*price) as \"出库总价（元）\",");
		sbsql.append("SUM(rkl-cksl) as 库存数量,");
		sbsql.append("SUM((rkl - cksl) * price) as \"库存总价（元）\" ");
		sbsql.append("from sctrb where ");
		sbsql.append("sheet like '%"+type+"%' ");
		if(!"N".equals(farm)){
			sbsql.append(" and farm='"+farm+"' ");
		}
		sbsql.append("and insert_time>='"+time_arr[0]+"' and insert_time<='"+time_arr[1]+"' and rkl+cksl>0 group by 2,3,4,5,6,7");
		
		String sql = sbsql.toString();
		if("肥料".equals(type) || "物资".equals(type)){
			sql = sql.replaceAll("rkl", "rksl");
			String py_name = ChineseToPinYin.converterToFirstSpell(type);
			sql = sql.replaceAll("pm", py_name+"pm");
			sql = sql.replaceAll("品名", type+"品名");
			sql = sql.replaceAll("zzpz", "spm");
			sql = sql.replaceAll("种子品种", "商品名");
		}
		if("生物制剂".equals(type)){
			String py_name = ChineseToPinYin.converterToFirstSpell(type);
			sql = sql.replaceAll("pm", py_name+"pm");
			sql = sql.replaceAll("品名", type+"品名");
			sql = sql.replaceAll("zzpz", "spm");
			sql = sql.replaceAll("种子品种", "商品名");
		}
		System.out.println(type+"出入库总表:"+sql);
		array = pc.resultSetToJson(sql);			
		return array;
	}
	/**
	 * 肥料出入库总表
	 * @param farm
	 * @param time
	 * @return
	 */
	private JSONArray q_unrday(String farm,String time,String type){
		String[] time_arr = time.split("@");
		String rday = time_arr[0].substring(0, 6)+"01";
		String time_end = time_arr[1].substring(0, time_arr[0].length()-2);
		StringBuffer sbsql = new StringBuffer();
		sbsql.append("SELECT ");
		sbsql.append("ROW_NUMBER () OVER () AS 序号,t1.pl as 品类,t1.pm as 品名,t1.zzpz as 种子品种,t1.gys as 供应商,t1.gg as 规格,t1.jldw as 计量单位,");
		sbsql.append("SUM (t1.rkl) AS 入库数量,");
		sbsql.append("SUM (t1.rkl * t1.price) AS \"入库总价（元）\",");
		sbsql.append("SUM (t1.cksl) AS 出库数量,");
		sbsql.append("SUM (t1.cksl * t1.price) AS \"出库总价（元）\",");
		sbsql.append("t2.kcl AS 库存数量,");
		sbsql.append("t2.kcje AS \"库存总价（元）\" ");
		sbsql.append("from sctrb t1, ");
		sbsql.append("(SELECT pm,zzpz,gys,gg,SUM (rkl - cksl) AS kcl,sum((rkl-cksl)*price) AS kcje FROM sctrb WHERE ");
		sbsql.append("sheet like '%"+type+"%' ");
		sbsql.append("AND insert_time>='"+rday+"00'");
		sbsql.append("AND insert_time<='"+time_arr[1]+"'");
		if(!"N".equals(farm)){
			sbsql.append(" and farm='"+farm+"' ");
		}
		sbsql.append(" GROUP BY 1,2,3,4");
		sbsql.append(") t2");
		sbsql.append(" WHERE ");
		sbsql.append("t1.pm = t2.pm AND t1.zzpz = t2.zzpz AND t1.gys = t2.gys AND ");
		sbsql.append("t1.sheet like '%"+type+"%' ");
		if(!"N".equals(farm)){
			sbsql.append(" and t1.farm='"+farm+"' ");
		}
		sbsql.append("and t1.insert_time>='"+time_arr[0]+"' and t1.insert_time<='"+time_arr[1]+"' and t1.rkl+t1.cksl>0 group by 2,3,4,5,6,7,12,13");
		String sql = sbsql.toString();
		if("肥料".equals(type) || "物资".equals(type)){
			sql = sql.replaceAll("rkl", "rksl");
			String py_name = ChineseToPinYin.converterToFirstSpell(type);
			sql = sql.replaceAll("pm", py_name+"pm");
			sql = sql.replaceAll("zzpz", "spm");
			sql = sql.replaceAll("品名", type+"品名");
			sql = sql.replaceAll("种子品种", "商品名");
		}
		if("生物制剂".equals(type)){
			String py_name = ChineseToPinYin.converterToFirstSpell(type);
			sql = sql.replaceAll("pm", py_name+"pm");
			sql = sql.replaceAll("zzpz", "spm");
			sql = sql.replaceAll("品名", type+"品名");
			sql = sql.replaceAll("种子品种", "商品名");
		}
		System.out.println(type+"出入库总表:"+sql);
		JSONArray array = pc.resultSetToJson(sql);
		return array;
	}
	/**
	 * 物资出入库总表
	 * @param farm
	 * @param time
	 * @return
	 */
	private JSONArray b14wzcrkzb(String farm,String time){
		String[] time_arr = time.split("@");
		StringBuffer sbsql = new StringBuffer();
		sbsql.append("SELECT ");
		sbsql.append("ROW_NUMBER () OVER () AS 序号,wzpm as 物资名称,spm as 商品名,gys as 供应商,jldw as 计量单位,");
		sbsql.append("round(sum(cast((case when rksl='' then '0' when rksl is null then '0' else rksl end) as numeric))::NUMERIC,2) as 入库数量,");
		sbsql.append("'zj'::text as \"入库总价（元）\",");
		sbsql.append("sum(cast((case when cksl='' then '0' when cksl is null then '0' else cksl end) as numeric)) as 出库数量,");
		sbsql.append("'zj'::text as \"出库总价（元）\",");
//		sbsql.append("sum(cast((case when rksl='' then '0' when rksl is null then '0' else rksl end) as numeric))-sum(cast((case when cksl='' then '0' when cksl is null then '0' else cksl end) as numeric)) as 库存量,");
		String wString = farm+"@"+time;
		sbsql.append("'"+wString+"'::text as 库存量,");
		sbsql.append("'zj'::text as \"库存总价（元）\" ");
		sbsql.append("from sctrb where ");
		sbsql.append("sheet like '%物资%' ");
		if(!"N".equals(farm)){
			sbsql.append(" and farm='"+farm+"' ");
		}
//		sbsql.append("and (cast((case when rksl='' then '0' when rksl is null then '0' else rksl end) as numeric))+(cast((case when cksl='' then '0' when cksl is null then '0' else cksl end) as numeric))>0 ");
		sbsql.append("and insert_time>='"+time_arr[0]+"' and insert_time<='"+time_arr[1]+"' group by 2,3,4,5");
		
		
		JSONArray array = pc.resultSetToJsonSpec(sbsql.toString());
		return array;
	}
	/**
	 * 有机蔬菜育苗总表
	 * @param farm
	 * @param time
	 * @return
	 */
	private JSONArray yjscymzb(String farm,String time){
//		String bom = time.substring(0, 6)+"0100";
//		String eom = time.substring(0, 8)+"24";
		String[] time_arr = time.split("@");
		StringBuffer sbsql = new StringBuffer();
		sbsql.append("select t1.pl as 品类,t1.pm as 品名,t1.dw as 单位,sum(t2.s_sl) as 总数量,sum(t2.s_ymmj) as 总育苗面积,sum(t2.s_yjyzl) as 总预计移栽量 ");
		sbsql.append("FROM ");
		sbsql.append("dic_pm t1,");
		sbsql.append("sctrb.yjscym t2");
		sbsql.append(" WHERE t1.pmbm=t2.s_pmbm AND ");
		sbsql.append("t2.s_orid ");
		sbsql.append("in ");
		sbsql.append("(SELECT o_id FROM sctrb.ori_data WHERE");
		if(!"N".equals(farm)){
			sbsql.append(" o_name LIKE '%"+farm+"%' ");
			sbsql.append("and");
		}
		sbsql.append(" o_timeflag>='"+time_arr[0]+"' and o_timeflag<='"+time_arr[1]+"')");
		sbsql.append(" and t2.s_sl>0");
		sbsql.append(" GROUP BY 1,2,3");
		System.out.println("有机蔬菜育苗总表"+sbsql.toString());
		JSONArray array = pc.resultSetToJson(sbsql.toString());
		return array;
	}
	
	/**
	 * 蔬菜种植总表
	 * @param farm
	 * @param time
	 * @return
	 */
	private JSONArray sczzzb(String farm,String time){
//		String bom = time.substring(0, 6)+"0100";
//		String eom = time.substring(0, 8)+"24";
		String[] time_arr = time.split("@");
		StringBuffer sbsql = new StringBuffer();
		sbsql.append("select t1.pl as 品类,t1.pm as 品名,t1.dw as 单位,sum(t2.s_zzmj) as 总种植面积,sum(t2.s_zzsl) as 总种植数量,sum(t2.s_yjcsl) as 总预计采收量 ");
		sbsql.append("FROM ");
		sbsql.append("dic_pm t1,");
		sbsql.append("sctrb.sczz t2");
		sbsql.append(" WHERE t1.pmbm=t2.s_pmbm AND ");
		sbsql.append("t2.s_orid ");
		sbsql.append("in ");
		sbsql.append("(SELECT o_id FROM sctrb.ori_data WHERE");
		if(!"N".equals(farm)){
			sbsql.append(" o_name LIKE '%"+farm+"%' ");
			sbsql.append("and");
		}
		sbsql.append(" o_timeflag>='"+time_arr[0]+"' and o_timeflag<='"+time_arr[1]+"')");
		sbsql.append(" and t2.s_zzmj>0 and t2.s_zzsl>0");
		sbsql.append(" GROUP BY 1,2,3");
		System.out.println("蔬菜种植总表"+sbsql.toString());
		JSONArray array = pc.resultSetToJson(sbsql.toString());
		return array;
	}
}
