package com.cbd5.resource.mrcsb;

import com.cbd5.Application;
import com.cbd5.PostgresCommon;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Administrator on 2016/2/17 0017.
 */
@Path("/table2")
public class Mrmx {

    PostgresCommon pc = Application.pc;
    HashMap tab = Application.tab;

    @Path("getpm")
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public String getPm(@QueryParam("pldm") String pldm, @QueryParam("callback") String cb) {
        JSONObject j = new JSONObject();

        StringBuffer sql = new StringBuffer();
        sql.append("select id as pmdm,pm_name as pm from pm where pl_id='" + pldm + "'");
        JSONArray array = pc.resultSetToJson(sql.toString());
        if (array == null || array.size() == 0) {
            j.put("arr", 0);
        } else {
            j.put("arr", array);
        }
        String tmp = cb + "(" + j + ")";
        return tmp;
    }

    @Path("material")
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public String getTable(@QueryParam("table") String name, @QueryParam("time") String time, @QueryParam("farm") String farm,
                           @QueryParam("where") String condition, @QueryParam("callback") String cb) {

        JSONObject j = new JSONObject();
        StringBuffer sql = new StringBuffer();
        StringBuffer sumsql = new StringBuffer();

        /*String timetj="";
        if (time.length() ==8)
            timetj=" where insert_time<='" + time + "'";*/

        String strtime = "";
        if (time != null) {
            String[] timeStrings = time.split("@");
            if (timeStrings.length == 2) {
                strtime = " insert_time>='" + timeStrings[0] + "' and insert_time<='" + timeStrings[1] + "'";
            }
        }
        sql.append("select farm,t1.pmdm,pm_name as pm,pl_id as pl");
        sumsql.append("select");
        String sheet = "";
        StringBuffer fromstr = new StringBuffer();

        if (name.equals("mcczb")) {
            sql.append(",dkh,czsj,czr,mcczl as czsl,jgccsl as ccsl,(case when scdd is null then '' else scdd end) as ddh");
            sumsql.append(" sum(mcczl) as r1,sum(jgccsl) as r2");
            fromstr.append(" from mrcsb.b2mcczb t1 left join pm t2 on t1.pmdm=t2.id where insert_time=(select max(insert_time) from mrcsb.originaldata where" + strtime + " and farm='" + farm + "') and farm='" + farm + "'");
        }

        if (name.equals("jpcjgb")) {
            sql.append(",jpcjg_1 as r1,jpcjg_2 as r2,jpcjg_3 as r3,jpcjgsl as r4");
            sheet = "2";
            sumsql.append(" sum(jpcjg_1) as r1,sum(jpcjg_2) as r2,sum(jpcjg_3) as r3,sum(jpcjgsl) as r4");
        }
        if (name.equals("spcjgb")) {
            sql.append(",spcjgsl as r1,spcjg_ccsl as r2");
            sheet = "3";
            sumsql.append(" sum(spcjgsl) as r1,sum(spcjg_ccsl) as r2");
        }
        if (name.equals("jpczkcb")) {
            sql.append(",jpczkc_1 as r1,jpczkc_2 as r2,jpczkc_3 as r3,jpczkc_4 as r4,jpczkc as r5");
            sheet = "4";
            sumsql.append(" sum(jpczkc_1) as r1,sum(jpczkc_2) as r2,sum(jpczkc_3) as r3,sum(jpczkc_4) as r4,sum(jpczkc) as r5");
        }
        if (name.equals("jpcdbrkb")) {
            sql.append(",jpcdbrk_1 as r1,jpcdbrk_2 as r2,(case when dbdd is null then '' else dbdd end) as ddh");
            sheet = "5";
            sumsql.append(" sum(jpcdbrk_1) as r1");
        }
        if (name.equals("spczkcb")) {
            sql.append(",spczkc_1 as r1,spczkc_2 as r2,spczkc_3 as r3,spczkc_4 as r4,spczk as r5");
            sheet = "6";
            sumsql.append(" sum(spczkc_1) as r1,sum(spczkc_2) as r2,sum(spczkc_3) as r3,sum(spczkc_4) as r4,sum(spczk) as r5");
        }
        if (name.equals("spcdbrkb")) {
            sql.append(",spcdbrk_1 as r1,spcdbrk_2 as r2,(case when dbdd is null then '' else dbdd end) as ddh");
            sheet = "7";
            sumsql.append(" sum(spcdbrk_1) as r1");
        }
        if (name.equals("jpcckb")) {
            sql.append(",jpcck_1 as r1,jpcck_2 as r2,jpcck_3 as r3,jpccksl as r4");
            sheet = "8";
            sumsql.append(" sum(jpcck_1) as r1,sum(jpcck_2) as r2,sum(jpcck_3) as r3,sum(jpccksl) as r4");
        }
        if (name.equals("jpctgckb")) {
            sql.append(",jpctgck_1 as r1,jpctgck_2 as r2,jpctgck_3 as r3,jpctgck_4 as r4,jpctgck_5 as r5,jpctgck_6 as r6");
            sheet = "9";
            sumsql.append(" sum(jpctgck_1) as r1,sum(jpctgck_3) as r2,sum(jpctgck_5) as r3");
        }
        if (name.equals("jpcdbckb")) {
            sql.append(",jpcdbck_1 as r1,jpcdbck_2 as r2,(case when dbdd is null then '' else dbdd end) as ddh");
            sheet = "10";
            sumsql.append(" sum(jpcdbck_1) as r1");
        }
        if (name.equals("spcckb")) {
            sql.append(",spcck_1 as r1,spcck_2 as r2,spccksl as r3");
            sheet = "11";
            sumsql.append(" sum(spcck_1) as r1,sum(spcck_2) as r2,sum(spccksl) as r3");
        }
        if (name.equals("spcdbckb")) {
            sql.append(",spcdbck_1 as r1,spcdbck_2 as r2,(case when dbdd is null then '' else dbdd end) as ddh");
            sheet = "12";
            sumsql.append(" sum(spcdbck_1) as r1");
        }
        if (name.equals("drspcckb")) {
            sql.append(",drspcck_1 as r1,(case when xsdd is null then '' else xsdd end) as ddh");
            sheet = "13";
            sumsql.append(" sum(drspcck_1) as r1");
        }

        if (name.equals("mryjccb")) {
            sql.append(",mrccl as r1,dat_ccl as rr,xzcc_1 as r2,xzcc_2 as r3,xzcc_3 as r4,xzcc_4 as r5,xzcc_5 as r6,xzcc_6 as r7,xzcc_7 as r8,xzcc_hj as r9,next_month as r10,next_two_month as r11,next_three_month as r12");
            sumsql.append(" sum(mrccl) as r1,sum(dat_ccl) as rr,sum(xzcc_1) as r2,sum(xzcc_2) as r3,sum(xzcc_3) as r4,sum(xzcc_4) as r5,sum(xzcc_5) as r6,sum(xzcc_6) as r7,sum(xzcc_7) as r8,sum(xzcc_hj) as r9,sum(next_month) as r10,sum(next_two_month) as r11,sum(next_three_month) as r12");
            fromstr.append(" from mrcsb.mryjcc t1 left join pm t2 on t1.pmdm=t2.id where insert_time=(select max(insert_time) from mrcsb.originaldata where" + strtime + " and farm='" + farm + "') and farm='" + farm + "'");
        }

        if (!sheet.equals("")) {
            fromstr.append(" from mrcsb.b2mrcsb t1 left join pm t2 on t1.pmdm=t2.id where sheet=" + sheet + " and insert_time=(select max(insert_time) from mrcsb.originaldata where" + strtime + " and farm='" + farm + "') and farm='" + farm + "'");
        }


        JSONObject where = null;
        try {
            where = JSONObject.fromObject(condition);
        } catch (Exception e) {
            j.put("error", "Where is abnormality");
            String tmp = cb + "(" + j + ")";
            return tmp;
        }
        Iterator<?> obj = where.keys();
        while (obj.hasNext()) {
            String key = (String) obj.next().toString();
            String data = where.getString(key);
            if (key.equals("dbdd") || key.equals("xsdd") || key.equals("scdd")) {
                if (data.equals(""))
                    fromstr.append(" and (" + key + " is null or " + key + "='')");
                else
                    fromstr.append(" and " + key + " ~* '" + data + "'");
            } else
                fromstr.append(" and " + key + "='" + data + "'");
        }

        sql.append(fromstr + " order by pmdm");
        sumsql.append(fromstr);

        JSONArray array = pc.resultSetToJson(sql.toString());
        if (array == null || array.size() == 0) {
            j.put("arr", 0);
        } else {
            j.put("arr", array);
        }
        JSONArray sumarray = pc.resultSetToJson(sumsql.toString());
        if (sumarray == null || sumarray.size() == 0) {
            j.put("sumarr", 0);
        } else {
            j.put("sumarr", sumarray);
        }
        String tmp = cb + "(" + j + ")";
        return tmp;
    }

    @Path("statistics")
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public String getSumTable(@QueryParam("time") String time, @QueryParam("farm") String farm,
                              @QueryParam("where") String condition, @QueryParam("callback") String cb) {
        JSONObject j = new JSONObject();
        StringBuffer sql = new StringBuffer();
        StringBuffer sumsql = new StringBuffer();
        StringBuffer wherestr = new StringBuffer();

        String strtime = "";
        if (time != null) {
            String[] timeStrings = time.split("@");
            if (timeStrings.length == 2) {
                strtime = " insert_time>='" + timeStrings[0] + "' and insert_time<='" + timeStrings[1] + "'";
            }
        }

        wherestr.append("");

        JSONObject where = null;
        where = JSONObject.fromObject(condition);
        Iterator<?> obj = where.keys();
        while (obj.hasNext()) {
            String key = (String) obj.next().toString();
            String data = where.getString(key);
            wherestr.append(" and " + key + "='" + data + "'");
        }

        if (farm.equals("全部")) {
            sumsql.append("select sum(r1) as r1,sum(r2) as r2,sum(r3) as r3,sum(r4) as r4,sum(r5) as r5,sum(r6) as r6,sum(r7) as r7,sum(r8) as r8" +
                    " from (select distinct farm from mrcsb.originaldata) table4 left join" +
                    " (select t1.farm,sum(mcczl) as r1 from (select farm,max(insert_time) as insert_time from mrcsb.originaldata where" + strtime + " group by farm) tz,mrcsb.b2mcczb t1 left join pm on t1.pmdm=pm.id where t1.insert_time=tz.insert_time and t1.farm=tz.farm" + wherestr.toString() + " group by t1.farm) table1" +
                    " on table4.farm=table1.farm left join" +
                    " (select t2.farm,sum(jpcjgsl) as r2,sum(spcjgsl) as r3,sum(jpczkc) as r4,sum(spczk) as r5,sum(jpccksl) as r6,sum(spccksl) as r7 from (select farm,max(insert_time) as insert_time from mrcsb.originaldata where" + strtime + " group by farm) tz,mrcsb.b2mrcsb t2 left join pm on t2.pmdm=pm.id where t2.insert_time=tz.insert_time and t2.farm=tz.farm" + wherestr.toString() + " group by t2.farm) table2" +
                    " on table4.farm=table2.farm left join" +
                    " (select t3.farm,sum(mrccl) as r8 from (select farm,max(insert_time) as insert_time from mrcsb.originaldata where" + strtime + " group by farm) tz,mrcsb.mryjcc t3 left join pm on t3.pmdm=pm.id where t3.insert_time=tz.insert_time and t3.farm=tz.farm" + wherestr.toString() + " group by t3.farm) table3" +
                    " on table4.farm=table3.farm");
            sql.append("select pmdm,pm,pl_id as pl,mcczl,jpcjgsl,spcjgsl,jpczkc,spczkc,jpccksl,spccksl,mryjccl" +
                    " from (" +
                    "select pm.id as pmdm,pm_name as pm,pl_id,(case when sum(r1) is null then 0 else sum(r1) end) as mcczl" +
                    ",(case when sum(r2) is null then 0 else sum(r2) end) as jpcjgsl,(case when sum(r3) is null then 0 else sum(r3) end) as spcjgsl" +
                    ",(case when sum(r4) is null then 0 else sum(r4) end) as jpczkc,(case when sum(r5) is null then 0 else sum(r5) end) as spczkc" +
                    ",(case when sum(r6) is null then 0 else sum(r6) end) as jpccksl,(case when sum(r7) is null then 0 else sum(r7) end) as spccksl" +
                    ",(case when sum(r8) is null then 0 else sum(r8) end) as mryjccl" +
                    " from pm left join (" +
                    "select pmdm,sum(mcczl) as r1" +
                    " from(select farm,max(insert_time) as insert_time from mrcsb.originaldata where" + strtime + " group by farm) tz,mrcsb.b2mcczb t1 left join pm on t1.pmdm=pm.id where t1.insert_time=tz.insert_time and t1.farm=tz.farm group by pmdm) table1" +
                    " on pm.id=table1.pmdm left join" +
                    " (select pmdm,sum(jpcjgsl) as r2,sum(spcjgsl) as r3,sum(jpczkc) as r4,sum(spczk) as r5,sum(jpccksl) as r6,sum(spccksl) as r7 from(select farm,max(insert_time) as insert_time from mrcsb.originaldata where" + strtime + " group by farm) tz,mrcsb.b2mrcsb t2 left join pm on t2.pmdm=pm.id where t2.insert_time=tz.insert_time and t2.farm=tz.farm group by pmdm) table2" +
                    " on pm.id=table2.pmdm left join" +
                    " (select pmdm,sum(mrccl) as r8 from (select farm,max(insert_time) as insert_time from mrcsb.originaldata where" + strtime + " group by farm) tz,mrcsb.mryjcc t3 left join pm on t3.pmdm=pm.id where t3.insert_time=tz.insert_time and t3.farm=tz.farm group by pmdm) table3" +
                    " on pm.id=table3.pmdm group by pm.id order by pm.id" +
                    ") tb1" +
                    " where (mcczl+jpcjgsl+spcjgsl+jpczkc+spczkc+jpccksl+spccksl+mryjccl)>0" + wherestr.toString());
        } else {
            sumsql.append("select r1,r2,r3,r4,r5,r6,r7,r8 from" +
                    "(select sum(mcczl) as r1 from mrcsb.b2mcczb st1 left join pm on st1.pmdm=pm.id where insert_time=(select max(insert_time) from mrcsb.originaldata where" + strtime + " and farm='" + farm + "') and farm='" + farm + "'" + wherestr.toString() + ") t1," +
                    "(select sum(jpcjgsl) as r2,sum(spcjgsl) as r3,sum(jpczkc) as r4,sum(spczk) as r5,sum(jpccksl) as r6,sum(spccksl) as r7 from mrcsb.b2mrcsb st2 left join pm on st2.pmdm=pm.id where insert_time=(select max(insert_time) from mrcsb.originaldata where" + strtime + " and farm='" + farm + "') and farm='" + farm + "'" + wherestr.toString() + ") t2," +
                    "(select sum(mrccl) as r8 from mrcsb.mryjcc st3 left join pm on st3.pmdm=pm.id where insert_time=(select max(insert_time) from mrcsb.originaldata where" + strtime + " and farm='" + farm + "') and farm='" + farm + "'" + wherestr.toString() + ") t3");
            sql.append("select pmdm,pm,pl_id as pl,mcczl,jpcjgsl,spcjgsl,jpczkc,spczkc,jpccksl,spccksl,mryjccl" +
                    " from(" +
                    "select pm.id as pmdm,pm_name as pm,pl_id,(case when r1 is null then 0 else r1 end) as mcczl" +
                    ",(case when r2 is null then 0 else r2 end) as jpcjgsl,(case when r3 is null then 0 else r3 end) as spcjgsl" +
                    ",(case when r4 is null then 0 else r4 end) as jpczkc,(case when r5 is null then 0 else r5 end) as spczkc" +
                    ",(case when r6 is null then 0 else r6 end) as jpccksl,(case when r7 is null then 0 else r7 end) as spccksl" +
                    ",(case when r8 is null then 0 else r8 end) as mryjccl" +
                    " from pm left join" +
                    "(select pmdm,sum(mcczl) as r1 from mrcsb.b2mcczb where insert_time=(select max(insert_time) from mrcsb.originaldata where" + strtime + " and farm='" + farm + "') and farm='" + farm + "' group by pmdm) t1" +
                    " on pm.id=t1.pmdm left join" +
                    "(select pmdm,sum(jpcjgsl) as r2,sum(spcjgsl) as r3,sum(jpczkc) as r4,sum(spczk) as r5,sum(jpccksl) as r6,sum(spccksl) as r7 from mrcsb.b2mrcsb where insert_time=(select max(insert_time) from mrcsb.originaldata where" + strtime + " and farm='" + farm + "') and farm='" + farm + "' group by pmdm) t2" +
                    " on pm.id=t2.pmdm left join" +
                    "(select pmdm,sum(mrccl) as r8 from mrcsb.mryjcc where insert_time=(select max(insert_time) from mrcsb.originaldata where" + strtime + " and farm='" + farm + "') and farm='" + farm + "' group by pmdm) t3" +
                    " on pm.id=t3.pmdm order by pm.id" +
                    ") t4" +
                    " where (mcczl+jpcjgsl+spcjgsl+jpczkc+spczkc+jpccksl+spccksl+mryjccl)>0" + wherestr.toString());
        }

        JSONArray array = pc.resultSetToJson(sql.toString());
        if (array == null || array.size() == 0) {
            j.put("arr", 0);
        } else {
            j.put("arr", array);
        }
        JSONArray sumarray = pc.resultSetToJson(sumsql.toString());
        if (sumarray == null || sumarray.size() == 0) {
            j.put("sumarr", 0);
        } else {
            j.put("sumarr", sumarray);
        }
        String tmp = cb + "(" + j + ")";
        return tmp;
    }

    @Path("counttable")
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public String getConutTable(@QueryParam("field") String field, @QueryParam("time") String time, @QueryParam("callback") String cb) {
        JSONObject j = new JSONObject();
        StringBuffer sql = new StringBuffer();
        StringBuffer sumsql = new StringBuffer();

        String strtime = "";
        if (time != null) {
            String[] timeStrings = time.split("@");
            if (timeStrings.length == 2) {
                strtime = " insert_time>='" + timeStrings[0] + "' and insert_time<='" + timeStrings[1] + "'";
            }
        }

        sumsql.append("select sum(case farm when '北京' then " + field + " else 0 end) as 北京,sum(case farm when '上海' then " + field + " else 0 end) as 上海" +
                ",sum(case farm when '广州' then " + field + " else 0 end) as 广州,sum(case farm when '惠州' then " + field + " else 0 end) as 惠州" +
                ",sum(case farm when '成都' then " + field + " else 0 end) as 成都,sum(case farm when '山东' then " + field + " else 0 end) as 山东" +
                ",sum(case farm when '云南' then " + field + " else 0 end) as 云南,sum(case farm when '扬州' then " + field + " else 0 end) as 扬州,sum(case farm when '长沙' then " + field + " else 0 end) as 长沙,sum(" + field + ") as 全国" +
                " from(select t1.farm as farm,xzcc_hj as xzcc,next_month as xycc,next_two_month as xlycc,next_three_month as xsycc from mrcsb.mryjcc as t1,(select farm,max(insert_time) as insert_time from mrcsb.originaldata where" + strtime + " group by farm) as t2 where t1.farm=t2.farm and t1.insert_time=t2.insert_time) t3");
        sql.append("select pmdm,pm,pl,北京,上海,广州,惠州,成都,山东,云南,扬州,长沙,全国" +
                " from (" +
                "select pmdm,pm_name as pm,pl_id as pl,sum(case farm when '北京' then " + field + " else 0 end) as 北京" +
                ",sum(case farm when '上海' then " + field + " else 0 end) as 上海,sum(case farm when '广州' then " + field + " else 0 end) as 广州" +
                ",sum(case farm when '惠州' then " + field + " else 0 end) as 惠州,sum(case farm when '成都' then " + field + " else 0 end) as 成都" +
                ",sum(case farm when '山东' then " + field + " else 0 end) as 山东,sum(case farm when '云南' then " + field + " else 0 end) as 云南" +
                ",sum(case farm when '扬州' then " + field + " else 0 end) as 扬州,sum(case farm when '长沙' then " + field + " else 0 end) as 长沙,sum(" + field + ") as 全国" +
                " from(" +
                "select t1.farm as farm,pmdm,xzcc_hj as xzcc,next_month as xycc,next_two_month as xlycc,next_three_month as xsycc" +
                " from mrcsb.mryjcc as t1,(select farm,max(insert_time) as insert_time from mrcsb.originaldata where" + strtime + " group by farm) as t2" +
                " where t1.farm=t2.farm and t1.insert_time=t2.insert_time" +
                ")t3 left join pm on t3.pmdm=pm.id" +
                " group by pmdm,pm_name,pl_id" +
                ") t4 where 北京+上海+广州+惠州+成都+山东+云南+扬州+长沙>0 order by pmdm");

        JSONArray array = pc.resultSetToJson(sql.toString());
        if (array == null || array.size() == 0) {
            j.put("arr", 0);
        } else {
            j.put("arr", array);
        }
        JSONArray sumarray = pc.resultSetToJson(sumsql.toString());
        if (sumarray == null || sumarray.size() == 0) {
            j.put("sumarr", 0);
        } else {
            j.put("sumarr", sumarray);
        }
        String tmp = cb + "(" + j + ")";
        return tmp;
    }
}
