package com.cbd5.resource.yjkg;

import com.cbd5.Application;
import com.cbd5.PostgresCommon;
import com.cbd5.resource.mrcsb.Dic_farm;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Vicent on 3/21/16.
 */
@Path("/yjkg")
public class Yjcc {

    PostgresCommon pc = Application.pc;

    Dic_farm df = new Dic_farm();
    ArrayList farm_list = df.get();
    @Path("q")
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public String total(@QueryParam("time") String time,
                        @QueryParam("callback") String cb) {
        JSONObject j = new JSONObject();

        time = time.replaceAll("-", "");
        JSONArray arr = pc.resultSetToJson(yjccQuery(time));
        JSONObject total = yjccTotal(time);

        //2016-04-01 Don't empty file is generated
        if (total.size() > 0 && arr.size() > 0) {
            GenerateExcel ge = new GenerateExcel();
            ge.generate(arr, total, time);
        }
        j.put("rst", arr);
        j.put("hj", total);


        String tmp = cb + "(" + j + ")";
        return tmp;
    }


    private String yjccQuery(String time) {
        StringBuffer yjcc = new StringBuffer();
        yjcc.append("SELECT tpm.pm_name AS 品名,");
        yjcc.append("CASE WHEN tpm.pl_id='PL01' THEN '果菜' WHEN tpm.pl_id='PL02' THEN '根茎类' WHEN tpm.pl_id='PL04' THEN '叶菜' ");
        yjcc.append("WHEN tpm.pl_id='PL06' THEN '玉米类' WHEN tpm.pl_id='PL07' THEN '豆类' ELSE tpm.pl_id END AS 品类,");


        String kchj = "";
        String cclhj = "";
        String dathj = "";
        for (int i = 0; i < farm_list.size(); i++) {
            yjcc.append("COALESCE(t" + i + ".kc,0) AS " + farm_list.get(i) + "库存,");
            kchj += "COALESCE(t" + i + ".kc,0)+";
            yjcc.append("COALESCE(t" + i + ".ccl,0) AS " + farm_list.get(i) + "出菜量,");
            cclhj += "COALESCE(t" + i + ".ccl,0)+";
            yjcc.append("COALESCE(t" + i + ".dat,0) AS " + farm_list.get(i) + "后日出菜量,");
            dathj += "COALESCE(t" + i + ".dat,0)+";
        }
        dathj = dathj.substring(0, dathj.length() - 1);
        yjcc.append(kchj.substring(0, kchj.length() - 1) + " AS 合计库存,");
        yjcc.append(cclhj.substring(0, cclhj.length() - 1) + " AS 合计出菜量,");
        yjcc.append(dathj + " AS 合计后日出菜量 ");
        yjcc.append("FROM ");
        yjcc.append(" pm tpm");
        for (int i = 0; i < farm_list.size(); i++) {
            yjcc.append(" LEFT JOIN ");
            yjcc.append("(SELECT b1.pmdm,b1.kc,b2.ccl,b2.dat FROM ");
            yjcc.append("(SELECT ");
            yjcc.append("cs.pmdm  AS pmdm,");
            yjcc.append("CASE WHEN sum(cs.jpczkc) ISNULL ");
            yjcc.append("THEN 0 ");
            yjcc.append("ELSE sum(cs.jpczkc) END AS kc ");
            yjcc.append("FROM mrcsb.b2mrcsb cs ");
            yjcc.append("WHERE cs.parent_id = ( ");
            yjcc.append("SELECT id ");
            yjcc.append("FROM mrcsb.originaldata ");
            yjcc.append("WHERE insert_time = ( ");
            yjcc.append("SELECT max(insert_time) ");
            yjcc.append("FROM mrcsb.originaldata ");
            yjcc.append("WHERE filename ~* '" + farm_list.get(i).toString() + "农场" + time + "') AND filename ~* '" + farm_list.get(i).toString() + "') ");
            yjcc.append("GROUP BY 1) ");
            yjcc.append("b1,");
            yjcc.append("(SELECT ");
            yjcc.append("cc.pmdm,");
            yjcc.append("CASE WHEN sum(cc.mrccl) ISNULL ");
            yjcc.append("THEN 0 ");
            yjcc.append("ELSE sum(cc.mrccl) END  AS ccl,");
            yjcc.append("sum(cc.dat_ccl) AS dat ");
            yjcc.append("FROM mrcsb.mryjcc cc ");
            yjcc.append("WHERE cc.parent_id = ( ");
            yjcc.append("SELECT id ");
            yjcc.append("FROM mrcsb.originaldata ");
            yjcc.append("WHERE insert_time = ( ");
            yjcc.append("SELECT max(insert_time) ");
            yjcc.append("FROM mrcsb.originaldata ");
            yjcc.append("WHERE filename ~* '" + farm_list.get(i).toString() + "农场" + time + "') AND filename ~* '" + farm_list.get(i).toString() + "') ");
            yjcc.append("GROUP BY 1) ");
            yjcc.append("b2");
            yjcc.append(" WHERE b1.pmdm=b2.pmdm");
            yjcc.append(" )");
            yjcc.append("t" + i);
            yjcc.append(" ON tpm.id=t" + i + ".pmdm");
        }
        yjcc.append(" WHERE " + kchj + cclhj + dathj + ">0");
        String sql = yjcc.toString();
        System.out.println(sql);

        return sql;
    }

    private JSONObject yjccTotal(String time) {
        JSONObject json = new JSONObject();

        StringBuffer yjcc = new StringBuffer();
        yjcc.append("SELECT ");
//        yjcc.append("CASE WHEN tpm.pl_id='PL01' THEN '果菜' WHEN tpm.pl_id='PL02' THEN '根茎类' WHEN tpm.pl_id='PL04' THEN '叶菜' ");
//        yjcc.append("WHEN tpm.pl_id='PL06' THEN '玉米类' WHEN tpm.pl_id='PL07' THEN '豆类' ELSE tpm.pl_id END AS 品类,");


        String kchj = "";
        String cclhj = "";
        String dathj = "";
        for (int i = 0; i < farm_list.size(); i++) {
            yjcc.append("SUM( COALESCE(t" + i + ".kc,0) ) AS " + farm_list.get(i) + "库存,");
            kchj += "COALESCE(t" + i + ".kc,0)+";
            yjcc.append("SUM( COALESCE(t" + i + ".ccl,0) ) AS " + farm_list.get(i) + "出菜量,");
            cclhj += "COALESCE(t" + i + ".ccl,0)+";
            yjcc.append("SUM( COALESCE(t" + i + ".dat,0) ) AS " + farm_list.get(i) + "后日出菜量,");
            dathj += "COALESCE(t" + i + ".dat,0)+";
        }
        dathj = dathj.substring(0, dathj.length() - 1);
        yjcc.append("SUM(" + kchj.substring(0, kchj.length() - 1) + ") AS 合计库存,");
        yjcc.append("SUM(" + cclhj.substring(0, cclhj.length() - 1) + ") AS 合计出菜量,");
        yjcc.append("SUM(" + dathj + ") AS 合计后日出菜量 ");
        yjcc.append("FROM ");
        yjcc.append(" pm tpm");
        for (int i = 0; i < farm_list.size(); i++) {
            yjcc.append(" LEFT JOIN ");
            yjcc.append("(SELECT b1.pmdm,b1.kc,b2.ccl,b2.dat FROM ");
            yjcc.append("(SELECT ");
            yjcc.append("cs.pmdm  AS pmdm,");
            yjcc.append("CASE WHEN sum(cs.jpczkc) ISNULL ");
            yjcc.append("THEN 0 ");
            yjcc.append("ELSE sum(cs.jpczkc) END AS kc ");
            yjcc.append("FROM mrcsb.b2mrcsb cs ");
            yjcc.append("WHERE cs.parent_id = ( ");
            yjcc.append("SELECT id ");
            yjcc.append("FROM mrcsb.originaldata ");
            yjcc.append("WHERE insert_time = ( ");
            yjcc.append("SELECT max(insert_time) ");
            yjcc.append("FROM mrcsb.originaldata ");
            yjcc.append("WHERE filename ~* '" + farm_list.get(i).toString() + "农场" + time + "') AND filename ~* '" + farm_list.get(i).toString() + "') ");
            yjcc.append("GROUP BY 1) ");
            yjcc.append("b1,");
            yjcc.append("(SELECT ");
            yjcc.append("cc.pmdm,");
            yjcc.append("CASE WHEN sum(cc.mrccl) ISNULL ");
            yjcc.append("THEN 0 ");
            yjcc.append("ELSE sum(cc.mrccl) END  AS ccl,");
            yjcc.append("sum(cc.dat_ccl) AS dat ");
            yjcc.append("FROM mrcsb.mryjcc cc ");
            yjcc.append("WHERE cc.parent_id = ( ");
            yjcc.append("SELECT id ");
            yjcc.append("FROM mrcsb.originaldata ");
            yjcc.append("WHERE insert_time = ( ");
            yjcc.append("SELECT max(insert_time) ");
            yjcc.append("FROM mrcsb.originaldata ");
            yjcc.append("WHERE filename ~* '" + farm_list.get(i).toString() + "农场" + time + "') AND filename ~* '" + farm_list.get(i).toString() + "') ");
            yjcc.append("GROUP BY 1) ");
            yjcc.append("b2");
            yjcc.append(" WHERE b1.pmdm=b2.pmdm");
            yjcc.append(" )");
            yjcc.append("t" + i);
            yjcc.append(" ON tpm.id=t" + i + ".pmdm");
        }
        yjcc.append(" WHERE " + kchj + cclhj + dathj + ">0");
        String sql = yjcc.toString();
        System.out.println("Total=====" + sql);
        json = pc.resultToJson(sql);
        return json;
    }
    public static void main(String[] args) {
        PostgresCommon pc = new PostgresCommon();
        pc.conn();
        Application.pc = pc;
        Yjcc kq = new Yjcc();
        String time = "20160301";
        String yjcc = kq.yjccQuery(time);
        JSONArray y = pc.resultSetToJson(yjcc);
        JSONObject j = kq.yjccTotal(time);
        GenerateExcel ge = new GenerateExcel();
        ge.generate(y, j, time);
    }
}
