package com.cbd5.resource;

import java.lang.reflect.Method;
import java.util.ArrayList;

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


@Path("/sctr")
public class GetSctrb {

	PostgresCommon pc = Application.pc;
	
	@Path("w")
	@GET
	@Produces(value=MediaType.APPLICATION_JSON)
    public String oiw(@QueryParam("sheet") String sheet,@QueryParam("farm") String farm,@QueryParam("time") String time,@QueryParam("callback") String cb){
		JSONObject j = new JSONObject();
		
		int err = 0;
		
		if(sheet == null){ err = 1; }
		if(farm == null){
			farm = "N";
		}
		if (time == null) {
			err = 3;
		} else {
			String[] timeStrings = time.split("@");
			if (timeStrings.length <= 1) {
				err = 2;
			}
		}
		if(cb == null){ err = 4; }
		
		if(err > 0){
			j.put("error", err);
			String tmp = cb+"("+j+")";
			return tmp;
		}

		
		JSONArray array = new JSONArray();
		
			

			GetSctrb gs = new GetSctrb();
			String[] m_name = gs.covertType(Integer.parseInt(sheet));
			Method[] methods = gs.getClass().getDeclaredMethods();
			for(int i = 0; i < methods.length; i++){
				if(methods[i].getName().equals(m_name[0])){
					try {
						methods[i].setAccessible(true);
						String sql = (String)methods[i].invoke(gs, new Object[]{farm,time,m_name[1]});
						System.out.println(sql);
						array = pc.resultSetToJson(sql.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		
		
		
		if(array == null || array.size() == 0){
			j.put("arr", 0);
		}else{
			j.put("arr", array);
		}
//		je.set(key, j.toString());
//		je.expire(key, 1800);
		System.out.println(j.toString());
		String tmp = cb+"("+j+")";
    	return tmp;
    }

	@Path("q")
	@GET
	@Produces(value=MediaType.APPLICATION_JSON)
    public String rday_finish(@QueryParam("callback") String cb){
		int err = 0;
		if(cb == null){ err = 4; }
		JSONObject j = new JSONObject();
		if(err > 0){
			j.put("error", err);
			String tmp = cb+"("+j+")";
			return tmp;
		}
		
		String rst = "N";
		String r = pc.query("SELECT value FROM config WHERE name='盘库完成'");
		if(r.equals("Y")){
			rst = "Y";
		}
		j.put("finish", rst);
		String tmp = cb+"("+j+")";
		return tmp;
	}
	private String finish(){
		String rst = "";
		String r = pc.query("SELECT value FROM config WHERE name='盘库完成'");
		if(r.equals("Y")){
			rst = "Y";
		}
		return rst;
	}
	//按照类型转换表名
	public String[] covertType(int type){
		String sql = "select t_name from correspond where xh="+type+"";
		String t_name = pc.query(sql);
		String py_name = ChineseToPinYin.converterToFirstSpell(t_name);
		String[] arr = {py_name,t_name};
		return arr;
	}
	/**
	 * 种子入库表
	 * @param farm
	 * @param time
	 * @param sheet
	 * @return
	 */
	private String zzrkb(String farm,String time,String sheet){
		String[] time_arr = time.split("@");
		StringBuffer sbsql = new StringBuffer();
		sbsql.append("SELECT ");
		sbsql.append("ROW_NUMBER () OVER () AS 序号,t1.pl as 品类,t1.pm as 品名,t1.spm as 种子品种,t1.gys as 供应商,t1.gg as 规格,t1.dw as 单位,t1.dj AS 单价,t2.s_rkl as 入库量,t2.s_dqsj as 到期时间,t1.comments as 备注 ");
		sbsql.append("FROM ");
		sbsql.append("dic_pm t1,");
		sbsql.append("sctrb.sctrb_rk t2");
		sbsql.append(" WHERE t1.pmbm=t2.s_pmbm AND ");
		sbsql.append("t2.s_type='1' ");
		sbsql.append("AND t2.s_orid ");
		sbsql.append("in ");
		sbsql.append("(SELECT o_id FROM sctrb.ori_data WHERE");
		if(!"N".equals(farm)){
			sbsql.append(" o_name LIKE '%"+farm+"%' ");
			sbsql.append("and");
		}
		sbsql.append(" o_timeflag>='"+time_arr[0]+"' and o_timeflag<='"+time_arr[1]+"')");
		
		sbsql.append(" AND t2.s_rkl>0");
		return sbsql.toString();
	}
	/**
	 * 种子出库表
	 * @param farm
	 * @param time
	 * @param sheet
	 * @return
	 */
	private String zzckb(String farm,String time,String sheet){
		String[] time_arr = time.split("@");
		StringBuffer sbsql = new StringBuffer();
		sbsql.append("SELECT ");
		sbsql.append("ROW_NUMBER () OVER () AS 序号,t1.pl as 品类,t1.pm as 品名,t1.spm as 种子品种,t1.gys as 供应商,t1.gg as 规格,t1.dw as 单位,t1.dj AS 单价,t2.s_dkh as 地块号,t2.s_ckl as 出库量,t1.comments as 备注,(case when t2.scdd is null then '' else t2.scdd end) as 生产订单号 ");
		sbsql.append("FROM ");
		sbsql.append("dic_pm t1,");
		sbsql.append("sctrb.sctrb_ck t2");
		sbsql.append(" WHERE t1.pmbm=t2.s_pmbm AND ");
		sbsql.append("t2.s_type='1' ");
		sbsql.append("AND t2.s_orid ");
		sbsql.append("in ");
		sbsql.append("(SELECT o_id FROM sctrb.ori_data WHERE");
		if(!"N".equals(farm)){
			sbsql.append(" o_name LIKE '%"+farm+"%' ");
			sbsql.append("and");
		}
		sbsql.append(" o_timeflag>='"+time_arr[0]+"' and o_timeflag<='"+time_arr[1]+"')");
		
		sbsql.append(" AND t2.s_ckl>0");
		return sbsql.toString();
	}
	/**
	 * 生物制剂入库表
	 * @param farm
	 * @param time
	 * @param sheet
	 * @return
	 */
	private String swzjrkb(String farm,String time,String sheet){
		String[] time_arr = time.split("@");
		StringBuffer sbsql = new StringBuffer();
		sbsql.append("SELECT ");
		sbsql.append("ROW_NUMBER () OVER () AS 序号,t1.pm as 生物制剂品名,t1.spm as 商品名,t1.gys as 供应商,t1.gg as 规格,t1.dw as 单位,t1.dj AS 单价,t2.s_rkl as 入库量,t2.s_dqsj as 到期时间,t1.comments as 备注 ");
		sbsql.append("FROM ");
		sbsql.append("dic_pm t1,");
		sbsql.append("sctrb.sctrb_rk t2");
		sbsql.append(" WHERE t1.pmbm=t2.s_pmbm AND ");
		sbsql.append("t2.s_type='2' ");
		sbsql.append("AND t2.s_orid ");
		sbsql.append("in ");
		sbsql.append("(SELECT o_id FROM sctrb.ori_data WHERE");
		if(!"N".equals(farm)){
			sbsql.append(" o_name LIKE '%"+farm+"%' ");
			sbsql.append("and");
		}
		sbsql.append(" o_timeflag>='"+time_arr[0]+"' and o_timeflag<='"+time_arr[1]+"')");
		
		sbsql.append(" AND t2.s_rkl>0");
		return sbsql.toString();
	}
	/**
	 * 生物制剂出库表
	 * @param farm
	 * @param time
	 * @param sheet
	 * @return
	 */
	private String swzjckb(String farm,String time,String sheet){
		String[] time_arr = time.split("@");
		StringBuffer sbsql = new StringBuffer();
		sbsql.append("SELECT ");
		sbsql.append("ROW_NUMBER () OVER () AS 序号,t1.pm as 生物制剂品名,t1.spm as 商品名,t1.gys as 供应商,t1.gg as 规格,t1.dw as 单位,t1.dj AS 单价,t2.s_dkh as 地块号,t2.s_ckl as 出库量,t1.comments as 备注,(case when t2.scdd is null then '' else t2.scdd end) as 生产订单号 ");
		sbsql.append("FROM ");
		sbsql.append("dic_pm t1,");
		sbsql.append("sctrb.sctrb_ck t2");
		sbsql.append(" WHERE t1.pmbm=t2.s_pmbm AND ");
		sbsql.append("t2.s_type='2' ");
		sbsql.append("AND t2.s_orid ");
		sbsql.append("in ");
		sbsql.append("(SELECT o_id FROM sctrb.ori_data WHERE");
		if(!"N".equals(farm)){
			sbsql.append(" o_name LIKE '%"+farm+"%' ");
			sbsql.append("and");
		}
		sbsql.append(" o_timeflag>='"+time_arr[0]+"' and o_timeflag<='"+time_arr[1]+"')");
		
		sbsql.append(" AND t2.s_ckl>0");
		return sbsql.toString();
	}
	/**
	 * 肥料入库表
	 * @param farm
	 * @param time
	 * @param sheet
	 * @return
	 */
	private String flrkb(String farm,String time,String sheet){
		String[] time_arr = time.split("@");
		StringBuffer sbsql = new StringBuffer();
		sbsql.append("SELECT ");
		sbsql.append("ROW_NUMBER () OVER () AS 序号,t1.pm as 肥料品名,t1.spm as 商品名,t1.gys as 供应商,t1.gg as 规格,t1.dw as 单位,t1.dj AS 单价,t2.s_rkl as 入库量,t2.s_dqsj as 到期时间,t1.comments as 备注 ");
		sbsql.append("FROM ");
		sbsql.append("dic_pm t1,");
		sbsql.append("sctrb.sctrb_rk t2");
		sbsql.append(" WHERE t1.pmbm=t2.s_pmbm AND ");
		sbsql.append("t2.s_type='3' ");
		sbsql.append("AND t2.s_orid ");
		sbsql.append("in ");
		sbsql.append("(SELECT o_id FROM sctrb.ori_data WHERE");
		if(!"N".equals(farm)){
			sbsql.append(" o_name LIKE '%"+farm+"%' ");
			sbsql.append("and");
		}
		sbsql.append(" o_timeflag>='"+time_arr[0]+"' and o_timeflag<='"+time_arr[1]+"')");
		
		sbsql.append(" AND t2.s_rkl>0");
		return sbsql.toString();
	}
	/**
	 * 肥料出库表
	 * @param farm
	 * @param time
	 * @param sheet
	 * @return
	 */
	private String flckb(String farm,String time,String sheet){
		String[] time_arr = time.split("@");
		StringBuffer sbsql = new StringBuffer();
		sbsql.append("SELECT ");
		sbsql.append("ROW_NUMBER () OVER () AS 序号,t1.pm as 肥料品名,t1.spm as 商品名,t1.gys as 供应商,t1.gg as 规格,t1.dw as 单位,t1.dj AS 单价,t2.s_dkh as 地块号,t2.s_ckl as 出库量,t1.comments as 备注,(case when t2.scdd is null then '' else t2.scdd end) as 生产订单号 ");
		sbsql.append("FROM ");
		sbsql.append("dic_pm t1,");
		sbsql.append("sctrb.sctrb_ck t2");
		sbsql.append(" WHERE t1.pmbm=t2.s_pmbm AND ");
		sbsql.append("t2.s_type='3' ");
		sbsql.append("AND t2.s_orid ");
		sbsql.append("in ");
		sbsql.append("(SELECT o_id FROM sctrb.ori_data WHERE");
		if(!"N".equals(farm)){
			sbsql.append(" o_name LIKE '%"+farm+"%' ");
			sbsql.append("and");
		}
		sbsql.append(" o_timeflag>='"+time_arr[0]+"' and o_timeflag<='"+time_arr[1]+"')");
		
		sbsql.append(" AND t2.s_ckl>0");
		return sbsql.toString();
	}
	/**
	 * 其他农用物资入库表
	 * @param farm
	 * @param time
	 * @param sheet
	 * @return
	 */
	private String qtnywzrkb(String farm,String time,String sheet){
		String[] time_arr = time.split("@");
		StringBuffer sbsql = new StringBuffer();
		sbsql.append("SELECT ");
		sbsql.append("ROW_NUMBER () OVER () AS 序号,t1.pm as 农用物资品名,t1.gys as 供应商,t1.gg as 规格,t1.dw as 单位,t1.dj AS 单价,t2.s_rkl as 入库量,t2.s_dqsj as 到期时间,t1.comments as 备注 ");
		sbsql.append("FROM ");
		sbsql.append("dic_pm t1,");
		sbsql.append("sctrb.sctrb_rk t2");
		sbsql.append(" WHERE t1.pmbm=t2.s_pmbm AND ");
		sbsql.append("t2.s_type='4' ");
		sbsql.append("AND t2.s_orid ");
		sbsql.append("in ");
		sbsql.append("(SELECT o_id FROM sctrb.ori_data WHERE");
		if(!"N".equals(farm)){
			sbsql.append(" o_name LIKE '%"+farm+"%' ");
			sbsql.append("and");
		}
		sbsql.append(" o_timeflag>='"+time_arr[0]+"' and o_timeflag<='"+time_arr[1]+"')");
		
		sbsql.append(" AND t2.s_rkl>0");
		return sbsql.toString();
	}
	/**
	 * 物资出库表
	 * @param farm
	 * @param time
	 * @param sheet
	 * @return
	 */
	private String wzckb(String farm,String time,String sheet){
		String[] time_arr = time.split("@");
		StringBuffer sbsql = new StringBuffer();
		sbsql.append("SELECT ");
		sbsql.append("ROW_NUMBER () OVER () AS 序号,t1.pm as 农用物资品名,t1.gys as 供应商,t1.gg as 规格,t1.dw as 单位,t1.dj AS 单价,t2.s_dkh as 地块号,t2.s_ckl as 出库量,t1.comments as 备注,(case when t2.scdd is null then '' else t2.scdd end) as 生产订单号 ");
		sbsql.append("FROM ");
		sbsql.append("dic_pm t1,");
		sbsql.append("sctrb.sctrb_ck t2");
		sbsql.append(" WHERE t1.pmbm=t2.s_pmbm AND ");
		sbsql.append("t2.s_type='4' ");
		sbsql.append("AND t2.s_orid ");
		sbsql.append("in ");
		sbsql.append("(SELECT o_id FROM sctrb.ori_data WHERE");
		if(!"N".equals(farm)){
			sbsql.append(" o_name LIKE '%"+farm+"%' ");
			sbsql.append("and");
		}
		sbsql.append(" o_timeflag>='"+time_arr[0]+"' and o_timeflag<='"+time_arr[1]+"')");
		
		sbsql.append(" AND t2.s_ckl>0");
		return sbsql.toString();
	}
	/**
	 * 有机蔬菜育苗总表
	 * @param farm
	 * @param time
	 * @param sheet
	 * @return
	 */
	private String yjscym(String farm,String time,String sheet){
		String[] time_arr = time.split("@");
		StringBuffer sbsql = new StringBuffer();
		
		sbsql.append("select t1.pl as 品类,t1.pm as 品名,t1.spm as 种子品种,t1.gys as 供应商,t1.gg as 规格,t1.dw as 单位,t1.dj AS 单价,t2.s_dkh as 地块号,t2.s_sl as 数量,t2.s_ymmj as 育苗面积,t2.s_yjyzsj as 预计移栽时间,t2.s_yjyzl as 预计移栽量,(case when t2.scdd is null then '' else t2.scdd end) as 生产订单号 ");
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
		
		sbsql.append(" AND t2.s_sl>0");
		return sbsql.toString();
	}
	/**
	 * 蔬菜种植
	 * @param farm
	 * @param time
	 * @param sheet
	 * @return
	 */
	private String sczz(String farm,String time,String sheet){
		String[] time_arr = time.split("@");
		StringBuffer sbsql = new StringBuffer();
		
		sbsql.append("select t1.pl as 品类,t1.pm as 品名,t1.spm as 种子品种,t1.gys as 供应商,t1.gg as 规格,t1.dw as 单位,t1.dj AS 单价,t2.s_dkh as 地块号,t2.s_zzmj as 种植面积,t2.s_zzsj as 种植时间,t2.s_zzsl as 种植数量,t2.s_yjcs_ksrq as 采收开始日期,t2.s_yjcs_jsrq as 采收结束日期,t2.s_yjcsl as 预计采收量,(case when t2.scdd is null then '' else t2.scdd end) as 生产订单号 ");
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
		
		sbsql.append(" AND t2.s_zzsl>0");
		return sbsql.toString();
	}
}
