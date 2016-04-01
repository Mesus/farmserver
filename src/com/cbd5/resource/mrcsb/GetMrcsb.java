package com.cbd5.resource.mrcsb;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import net.sf.json.JSONObject;

import com.cbd5.Application;
import com.cbd5.PostgresCommon;

@Path("/mrcs")
public class GetMrcsb {

    PostgresCommon pc = Application.pc;

    @Path("q")
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public String total(@QueryParam("time") String time,
                        @QueryParam("callback") String cb) {
        JSONObject j = new JSONObject();

        time = time.replaceAll("-", "");
        String hj = hj(time);
        j.put("hj", hj);

        String mx = mx(time);
        j.put("mx", mx);
        String tmp = cb + "(" + j + ")";
        return tmp;
    }

    private String hj(String time) {

        double mcczl = 0, jpckcl = 0, jpcckl = 0, spckcl = 0, spcckl = 0, mrccl = 0;

        Dic_farm df = new Dic_farm();
        ArrayList farm = df.get();
        for (int i = 0; i < farm.size(); i++) {
            String f = farm.get(i).toString();
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT ");
            sql.append("b2.mcczl,b1.jpckcl,b1.jpcckl,b1.spckcl,b1.spcckl,b3.mrccl ");
            sql.append("FROM (");
            sql.append("SELECT ");
            sql.append("SUM (T.jpczkc) AS jpckcl,SUM (T.jpccksl) AS jpcckl,SUM (T.spczk) AS spckcl,SUM (T.spccksl) AS spcckl ");
            sql.append("FROM ");
            sql.append("mrcsb.b2mrcsb T ");
            sql.append("WHERE ");
            sql.append("T.parent_id IN (SELECT ID FROM mrcsb.originaldata WHERE insert_time=(SELECT max(insert_time) FROM mrcsb.originaldata WHERE insert_time~*'" + time + "') and farm='" + f + "') ");

            sql.append(") AS b1,");
            sql.append("(SELECT SUM (T .mcczl) AS mcczl ");
            sql.append("FROM mrcsb.b2mcczb T ");
            sql.append("WHERE ");
            sql.append("T.parent_id IN (SELECT ID FROM mrcsb.originaldata WHERE insert_time=(SELECT max(insert_time) FROM mrcsb.originaldata WHERE insert_time~*'" + time + "') and farm='" + f + "') ");

            sql.append(") AS b2,");
            sql.append("(SELECT SUM (T.mrccl) AS mrccl ");
            sql.append("FROM mrcsb.mryjcc T ");
            sql.append("WHERE ");
            sql.append("T.parent_id IN (SELECT ID FROM mrcsb.originaldata WHERE insert_time=(SELECT max(insert_time) FROM mrcsb.originaldata WHERE insert_time~*'" + time + "') and farm='" + f + "') ");

            sql.append(") AS b3");
            ArrayList tmp = pc.queryDoubleArr(sql.toString());
            System.out.println("毛菜采摘量(Kg)精品菜库存量(Kg)精品菜出库量(Kg)商品菜库存量(Kg)商品菜出库量(Kg)明日预计出菜数量(Kg)");
            System.out.println(f + ":" + tmp.get(0) + "," + tmp.get(1) + "," + tmp.get(2) + "," + tmp.get(3) + "," + tmp.get(4) + "," + tmp.get(5));
            mcczl += (Double) tmp.get(0);
            jpckcl += (Double) tmp.get(1);
            jpcckl += (Double) tmp.get(2);
            spckcl += (Double) tmp.get(3);
            spcckl += (Double) tmp.get(4);
            mrccl += (Double) tmp.get(5);
        }

        ArrayList result = new ArrayList();//结果集合
        result.add(mcczl);
        result.add(jpckcl);
        result.add(jpcckl);
        result.add(spckcl);
        result.add(spcckl);
        result.add(mrccl);

        StringBuffer code = new StringBuffer();
        if (result.size() > 0) {
            code.append("<table class=\"uk-table uk-table-striped uk-table-condensed uk-text-nowrap\">");
            code.append("<thead>");
            code.append("<tr><th></th><th>毛菜采摘量(Kg)</th><th>精品菜库存量(Kg)</th><th>精品菜出库量(Kg)</th>");
            code.append("<th>商品菜库存量(Kg)</th><th>商品菜出库量(Kg)</th><th>明日预计出菜数量(Kg)</th>");
            code.append("</tr></thead><tbody><tr><td>合计：</td>");
            String smcczl = result.get(0).toString();
            if (smcczl.substring(smcczl.indexOf("."), smcczl.length()).length() > 4) {
                smcczl = smcczl.substring(0, smcczl.indexOf(".") + 4);
            }
            String sjpckcl = result.get(1).toString();
            if (sjpckcl.substring(sjpckcl.indexOf("."), sjpckcl.length()).length() > 4) {
                sjpckcl = sjpckcl.substring(0, sjpckcl.indexOf(".") + 4);
            }
            String sjpcckl = result.get(2).toString();
            if (sjpcckl.substring(sjpcckl.indexOf("."), sjpcckl.length()).length() > 4) {
                sjpcckl = sjpcckl.substring(0, sjpcckl.indexOf(".") + 4);
            }
            String sspckcl = result.get(3).toString();
            if (sspckcl.substring(sspckcl.indexOf("."), sspckcl.length()).length() > 4) {
                sspckcl = sspckcl.substring(0, sspckcl.indexOf(".") + 4);
            }
            String sspcckl = result.get(4).toString();
            if (sspcckl.substring(sspcckl.indexOf("."), sspcckl.length()).length() > 4) {
                sspcckl = sspcckl.substring(0, sspcckl.indexOf(".") + 4);
            }
            String smrccl = result.get(5).toString();
            if (smrccl.substring(smrccl.indexOf("."), smrccl.length()).length() > 4) {
                smrccl = smrccl.substring(0, smrccl.indexOf(".") + 4);
            }
            code.append("<td>" + smcczl + "</td>");
            code.append("<td>" + sjpckcl + "</td>");
            code.append("<td>" + sjpcckl + "</td>");
            code.append("<td>" + sspckcl + "</td>");
            code.append("<td>" + sspcckl + "</td>");
            code.append("<td>" + smrccl + "</td>");
            code.append("</tr></tbody></table>");
        }
        return code.toString();
    }

    private String mx(String time) {
        String[] pl = {"PL04", "PL01", "PL02", "PL06", "PL07"};
        StringBuffer code = new StringBuffer();
        code.append("<table class=\"uk-table uk-table-striped uk-table-condensed uk-text-nowrap\">");
        code.append("<thead>");
        code.append("<tr><th></th><th>毛菜采摘量(Kg)</th><th>精品菜库存量(Kg)</th><th>精品菜出库量(Kg)</th>");
        code.append("<th>商品菜库存量(Kg)</th><th>商品菜出库量(Kg)</th><th>明日预计出菜数量(Kg)</th>");
        code.append("</tr></thead><tbody>");
        for (int i = 0; i < pl.length; i++) {
            String sFarm = pl[i];

            String pm = "";
            switch (i) {
                case 0:
                    pm = "叶菜";
                    break;
                case 1:
                    pm = "果菜";
                    break;
                case 2:
                    pm = "根茎类";
                    break;
                case 3:
                    pm = "玉米类";
                    break;
                case 4:
                    pm = "豆类";
                    break;
            }
            double mcczl = 0, jpckcl = 0, jpcckl = 0, spckcl = 0, spcckl = 0, mrccl = 0;
            Dic_farm df = new Dic_farm();
            ArrayList farm = df.get();
            for (int j = 0; j < farm.size(); j++) {
                String f = farm.get(j).toString();
                StringBuffer sql = new StringBuffer();
                sql.append("SELECT ");
//            sql.append("'"+pm+"' as 品名,");
                sql.append("CASE WHEN mcb.mcczl ISNULL THEN 0 ELSE mcb.mcczl END as \"毛菜采摘量(Kg)\",");
                sql.append("CASE WHEN mbb.jpckcl ISNULL THEN 0 ELSE mbb.jpckcl END as \"精品菜库存量(Kg)\",");
                sql.append("CASE WHEN mbb.jpcckl ISNULL THEN 0 ELSE mbb.jpcckl END as \"精品菜出库量(Kg)\",");
                sql.append("CASE WHEN mbb.spckcl ISNULL THEN 0 ELSE mbb.spckcl END as \"商品菜库存量(Kg)\",");
                sql.append("CASE WHEN mbb.spcckl ISNULL THEN 0 ELSE mbb.spcckl END as \"商品菜出库量(Kg)\",");
                sql.append("CASE WHEN mcc.mryjcc ISNULL THEN 0 ELSE mcc.mryjcc END as \"明日预计出菜数量(Kg)\" ");
                sql.append("FROM ");
                sql.append("(");
                sql.append(" SELECT ");
                sql.append("SUM (mc.mcczl) AS mcczl");
                sql.append(" FROM ");
                sql.append("mrcsb.b2mcczb mc,");
                sql.append("PUBLIC .pm pm ");
                sql.append("WHERE ");
                sql.append("mc.parent_id IN (SELECT ID FROM mrcsb.originaldata WHERE insert_time=(SELECT max(insert_time) FROM mrcsb.originaldata WHERE insert_time~*'" + time + "') and farm='" + f + "') ");
                sql.append(" AND pm.pl_id = '" + pl[i] + "' ");
                sql.append("AND mc.pmdm = pm. ID ");
                sql.append(" ) AS mcb,");
                sql.append("(");
                sql.append("SELECT ");
                sql.append("SUM (mr.jpczkc) AS jpckcl,SUM (mr.jpccksl) AS jpcckl,SUM (mr.spczk) AS spckcl,");
                sql.append("SUM (mr.spccksl) AS spcckl ");
                sql.append("FROM ");
                sql.append("mrcsb.b2mrcsb mr,PUBLIC .pm pm ");
                sql.append("WHERE ");
                sql.append("mr.parent_id IN (SELECT ID FROM mrcsb.originaldata WHERE insert_time=(SELECT max(insert_time) FROM mrcsb.originaldata WHERE insert_time~*'" + time + "') and farm='" + f + "') ");
                sql.append(" AND pm.pl_id = '" + pl[i] + "' ");
                sql.append("AND mr.pmdm = pm. ID ");
                sql.append(") AS mbb,");
                sql.append("(");
                sql.append("SELECT ");
                sql.append("SUM (cc.mrccl) AS mryjcc ");
                sql.append("FROM ");
                sql.append("mrcsb.mryjcc cc,PUBLIC .pm pm ");
                sql.append("WHERE ");
                sql.append("cc.parent_id IN (SELECT ID FROM mrcsb.originaldata WHERE insert_time=(SELECT max(insert_time) FROM mrcsb.originaldata WHERE insert_time~*'" + time + "') and farm='" + f + "') ");
                sql.append(" AND pm.pl_id = '" + pl[i] + "' ");
                sql.append("AND cc.pmdm = pm. ID ");
                sql.append(") AS mcc ");
                System.out.println(sql.toString());
                ArrayList tmp = pc.queryDoubleArr(sql.toString());

                System.out.println("明细======\r\n毛菜采摘量(Kg)精品菜库存量(Kg)精品菜出库量(Kg)商品菜库存量(Kg)商品菜出库量(Kg)明日预计出菜数量(Kg)");
                System.out.println(pm + ":" + f + ":" + tmp.get(0) + "," + tmp.get(1) + "," + tmp.get(2) + "," + tmp.get(3) + "," + tmp.get(4) + "," + tmp.get(5));
                mcczl += (Double) tmp.get(0);
                jpckcl += (Double) tmp.get(1);
                jpcckl += (Double) tmp.get(2);
                spckcl += (Double) tmp.get(3);
                spcckl += (Double) tmp.get(4);
                mrccl += (Double) tmp.get(5);
            }
            ArrayList result = new ArrayList();
            result.add(mcczl);
            result.add(jpckcl);
            result.add(jpcckl);
            result.add(spckcl);
            result.add(spcckl);
            result.add(mrccl);

            if (result.size() > 0) {
                code.append("<tr><td><a href=\"\" data-uk-modal=\"\" class=\"uk-text-primary\" onclick=\"qghz('" + sFarm + "')\">" + pm + "</a></td>");
                String smcczl = result.get(0).toString();
                if (smcczl.substring(smcczl.indexOf("."), smcczl.length()).length() > 4) {
                    smcczl = smcczl.substring(0, smcczl.indexOf(".") + 4);
                }
                String sjpckcl = result.get(1).toString();
                if (sjpckcl.substring(sjpckcl.indexOf("."), sjpckcl.length()).length() > 4) {
                    sjpckcl = sjpckcl.substring(0, sjpckcl.indexOf(".") + 4);
                }
                String sjpcckl = result.get(2).toString();
                if (sjpcckl.substring(sjpcckl.indexOf("."), sjpcckl.length()).length() > 4) {
                    sjpcckl = sjpcckl.substring(0, sjpcckl.indexOf(".") + 4);
                }
                String sspckcl = result.get(3).toString();
                if (sspckcl.substring(sspckcl.indexOf("."), sspckcl.length()).length() > 4) {
                    sspckcl = sspckcl.substring(0, sspckcl.indexOf(".") + 4);
                }
                String sspcckl = result.get(4).toString();
                if (sspcckl.substring(sspcckl.indexOf("."), sspcckl.length()).length() > 4) {
                    sspcckl = sspcckl.substring(0, sspcckl.indexOf(".") + 4);
                }
                String smrccl = result.get(5).toString();
                if (smrccl.substring(smrccl.indexOf("."), smrccl.length()).length() > 4) {
                    smrccl = smrccl.substring(0, smrccl.indexOf(".") + 4);
                }
                code.append("<td>" + smcczl + "</td>");
                code.append("<td>" + sjpckcl + "</td>");
                code.append("<td>" + sjpcckl + "</td>");
                code.append("<td>" + sspckcl + "</td>");
                code.append("<td>" + sspcckl + "</td>");
                code.append("<td>" + smrccl + "</td>");
                code.append("</tr>");
            }
        }
        code.append("</tbody></table>");
        System.out.println(code.toString());
        return code.toString();
    }


}
