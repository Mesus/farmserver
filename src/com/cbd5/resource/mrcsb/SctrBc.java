package com.cbd5.resource.mrcsb;

import com.cbd5.Application;
import com.cbd5.PostgresCommon;
import com.cbd5.resource.GetSctrbStat;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/2/26 0026.
 */
@Path("/table1")
public class SctrBc {
    PostgresCommon pc = Application.pc;
    HashMap tab = Application.tab;

    @Path("bctable")
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public String getBc(@QueryParam("farm") String farm,@QueryParam("time") String time,@QueryParam("callback") String cb) {
        JSONObject j = new JSONObject();

        String Pkdate="";
        GetSctrbStat pk=new GetSctrbStat();
        Pkdate = pk.getrday();

        String strtime="";
        String strtime1="";
        if (time != null) {
            String[] timeStrings = time.split("@");
            if (timeStrings.length == 2) {
                strtime=" o_timeflag>='" + timeStrings[0] + "' and o_timeflag<='" + timeStrings[1] + "'";
                strtime1=" o_timeflag>='" + Pkdate + "00' and o_timeflag<='" + timeStrings[1] + "'";
            }
        }



        String strfarm="";
        if(farm != null) {
            strfarm = " and o_name like '" + farm + "%'";
            Pkdate=farm+"_"+Pkdate;
        }


        //pc.query(sqlpk)


        StringBuffer sql = new StringBuffer();
        sql.append("select pmdm as 品名代码,pm as 包材品名,spm as 商品名,gys as 供应商,gg as 规格,dw as 单位,入库数量,入库总价,出库数量,出库总价,库存数量,库存总价" +
                    " from (" +
                    "select tt.pmbm as pmdm,pm,spm,gys,gg,dw" +
                    ",(case when rkl is null then 0 else rkl end) as 入库数量,(case when rkl*dj is null then 0 else rkl*dj end) as 入库总价" +
                    ",(case when ckl is null then 0 else ckl end) as 出库数量,(case when ckl*dj is null then 0 else ckl*dj end) as 出库总价" +
                    ",(case when kcl is null then 0 else kcl end) as 库存数量,(case when kcl*dj is null then 0 else kcl*dj end) as 库存总价" +
                    " from dic_pm tt left join" +
                    " (select s_pmbm as pmdm,sum(s_rkl) as rkl from sctrb.sctrb_rk" +
                    " where s_orid in (select o_id from sctrb.ori_data where"+strtime+strfarm+")" +
                    " group by s_pmbm) t1 on tt.pmbm=t1.pmdm left join" +
                    " (select s_pmbm as pmdm,sum(s_ckl) as ckl from sctrb.sctrb_ck" +
                    " where s_orid in (select o_id from sctrb.ori_data where"+strtime+strfarm+")" +
                    " group by s_pmbm) t2 on tt.pmbm=t2.pmdm left join" +
                    " (select aa.pmbm as pmdm,rk,ck,((case when rk is null then 0 else rk end)-(case when ck is null then 0 else ck end)+(case when kcsl is null then 0 else kcsl end)) as kcl" +
                    " from dic_pm aa left join" +
                    " (select s_pmbm as pmdm,sum(s_rkl) as rk from sctrb.sctrb_rk where s_orid in (select o_id from sctrb.ori_data where"+strtime1+strfarm+") and s_type=5 group by pmdm) as a1" +
                    " on aa.pmbm=a1.pmdm left join" +
                    " (select s_pmbm as pmdm,sum(s_ckl) as ck from sctrb.sctrb_ck where s_orid in (select o_id from sctrb.ori_data where"+strtime1+strfarm+") and s_type=5 group by pmdm) as a2" +
                    " on aa.pmbm=a2.pmdm left join" +
                    " (select pmbm,kcsl from sctrb.\""+Pkdate+"\" where lx=5) a3" +
                    " on aa.pmbm=a3.pmbm"+
                    " where aa.fl='包材')t3 on tt.pmbm=t3.pmdm" +
                    " where tt.fl='包材') table1" +
                    " where 入库数量<>0 or 入库总价<>0 or 出库数量<>0 or 出库总价<>0 or 库存数量<>0 or 库存总价<>0" +
                    " order by pmdm");
        JSONArray array = pc.resultSetToJson(sql.toString());
        if (array == null || array.size() == 0) {
            j.put("arr", 0);
        } else {
            j.put("arr", array);
        }
        String tmp = cb + "(" + j + ")";
        return tmp;
    }

    @Path("bccrkb")
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public String getSonBc(@QueryParam("sheet") String sheet,@QueryParam("farm") String farm,@QueryParam("time") String time,@QueryParam("callback") String cb) {
        JSONObject j = new JSONObject();

        String strtime="";
        if (time != null) {
            String[] timeStrings = time.split("@");
            if (timeStrings.length == 2) {
                strtime=" o_timeflag>='" + timeStrings[0] + "' and o_timeflag<='" + timeStrings[1] + "'";
            }
        }
        String strfarm="";
        if(farm != null)
            strfarm=" and o_name like '"+farm+"%'";

        StringBuffer sql = new StringBuffer();
        if(sheet.equals("21"))
            sql.append("select t1.pmdm as 品名代码,pm as 包材品名,spm as 商品名,gys as 供应商,gg as 规格,dw as 单位,rk as 入库量,s_dqsj as 到期时间,s_bz as 备注" +
                        " from (" +
                            "select s_pmbm as pmdm,s_rkl as rk,s_dqsj,s_bz from sctrb.sctrb_rk where s_orid in (select o_id from sctrb.ori_data where"+strtime+strfarm+") and s_type=5" +
                        ") t1 left join dic_pm t2 on t1.pmdm=t2.pmbm" +
                        " where rk<>0" +
                        " order by t1.pmdm");
        if(sheet.equals("22"))
            sql.append("select t1.pmdm as 品名代码,pm as 包材品名,spm as 商品名,gys as 供应商,gg as 规格,dw as 单位,ck as 出库量,xsdd as 销售单号,s_bz as 备注" +
                        " from (" +
                             "select s_pmbm as pmdm,s_ckl as ck,xsdd,s_bz from sctrb.sctrb_ck where s_orid in (select o_id from sctrb.ori_data where"+strtime+strfarm+") and s_type=5" +
                        ") t1 left join dic_pm t2 on t1.pmdm=t2.pmbm" +
                        " where ck<>0" +
                        " order by t1.pmdm");

        JSONArray array = pc.resultSetToJson(sql.toString());
        if (array == null || array.size() == 0) {
            j.put("arr", 0);
        } else {
            j.put("arr", array);
        }
        String tmp = cb + "(" + j + ")";
        return tmp;
    }



    @Path("Pkdate")
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public String getPkr(@QueryParam("callback") String cb) {
        JSONObject j = new JSONObject();

        String Pkdate="";
        GetSctrbStat pk=new GetSctrbStat();
        Pkdate = pk.getrday();

        if (Pkdate.equals("")) {
            j.put("arr", 0);
        } else {
            j.put("arr", Pkdate);
        }
        String tmp = cb + "(" + j + ")";
        return tmp;
    }
}
