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
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 蔬菜采收表每日统计
 *
 * @author vicent
 */
@Path("/mrtj")
public class Mrtj {

    PostgresCommon pc = Application.pc;

    @Path("nchzsj")
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public String getJson(@QueryParam("pl") String pl, @QueryParam("rq") String time, @QueryParam("callback") String cb) {
        JSONObject rst = new JSONObject();

        time = time.replaceAll("-", "");
        JSONObject ttt = pc.resultToJson("SELECT * FROM mrcsb.originaldata");
        System.out.println(ttt.toString());
        JSONArray json = new JSONArray();
//        StringBuffer sql = new StringBuffer();
//        sql.append("SELECT ");
//        sql.append("DISTINCT b0.farm AS 农场,");
//        sql.append("CASE WHEN SUM (b1.mcczl) ISNULL THEN 0 ELSE SUM (b1.mcczl) END AS \"毛菜采摘量(Kg)\",");
//        sql.append("CASE WHEN SUM (jpczkc) ISNULL THEN 0 ELSE SUM(jpczkc) END AS \"精品菜库存量(Kg)\",");
//        sql.append("CASE WHEN SUM (jpccksl) ISNULL THEN 0 ELSE SUM (jpccksl) END AS \"精品菜出库量(Kg)\",");
//        sql.append("CASE WHEN SUM (spczk) ISNULL THEN 0 ELSE SUM (spczk) END AS \"商品菜库存量(Kg)\",");
//        sql.append("CASE WHEN SUM (spccksl) ISNULL THEN 0 ELSE SUM (spccksl) END AS \"商品菜出库量(Kg)\",");
//        sql.append("CASE WHEN sum(b3.mrccl)  ISNULL THEN 0 ELSE sum(b3.mrccl) END as \"明日预计出菜数量(Kg)\" ");
//        sql.append("FROM pl,pm ");
//        sql.append("LEFT JOIN mrcsb.b2mrcsb b0 ON pm. ID = b0.pmdm ");
//        sql.append("LEFT JOIN mrcsb.b2mcczb b1 ON pm. ID = b1.pmdm ");
//        sql.append("LEFT JOIN mrcsb.mryjcc b3 ON pm. ID = b3.pmdm ");
//        sql.append("WHERE ");
//        sql.append("b0.parent_id IN (SELECT id FROM mrcsb.originaldata WHERE insert_time ~* '"+time+"' ");
//
//        sql.append(")");
//        sql.append(" AND b0.pmdm = pm. ID ");
//        sql.append("AND pm.pl_id = pl. ID ");
//        if( pl.length() > 0 ){
//            sql.append("AND pl.id = '"+pl+"' ");
//        }
//        sql.append("GROUP BY 1");
        StringBuffer code = new StringBuffer();
        code.append("<table class=\"uk-table uk-table-striped uk-table-condensed uk-text-nowrap\">");
        code.append("<thead>");
        code.append("<tr><th></th><th>毛菜采摘量(Kg)</th><th>精品菜库存量(Kg)</th><th>精品菜出库量(Kg)</th>");
        code.append("<th>商品菜库存量(Kg)</th><th>商品菜出库量(Kg)</th><th>明日预计出菜数量(Kg)</th>");
        code.append("</tr></thead><tbody>");
        Dic_farm df = new Dic_farm();
        ArrayList farm = df.get();
        for (int i = 0; i < farm.size(); i++) {
            String sFarm = farm.get(i).toString();
            StringBuffer sql = new StringBuffer();
//            sql.append("SELECT ");
//            sql.append("'"+farm[i]+"' as 农场,");
//            sql.append("CASE WHEN mcb.mcczl ISNULL THEN 0 ELSE mcb.mcczl END as \"毛菜采摘量(Kg)\",");
//            sql.append("CASE WHEN mbb.jpckcl ISNULL THEN 0 ELSE mbb.jpckcl END as \"精品菜库存量(Kg)\",");
//            sql.append("CASE WHEN mbb.jpcckl ISNULL THEN 0 ELSE mbb.jpcckl END as \"精品菜出库量(Kg)\",");
//            sql.append("CASE WHEN mbb.spckcl ISNULL THEN 0 ELSE mbb.spckcl END as \"商品菜库存量(Kg)\",");
//            sql.append("CASE WHEN mbb.spcckl ISNULL THEN 0 ELSE mbb.spcckl END as \"商品菜出库量(Kg)\",");
//            sql.append("CASE WHEN mcc.mryjcc ISNULL THEN 0 ELSE mcc.mryjcc END as \"明日预计出菜数量(Kg)\" ");
//            sql.append("FROM ");
//            sql.append("(");
//            sql.append(" SELECT ");
//            sql.append("DISTINCT mc.farm AS farm,SUM (mc.mcczl) AS mcczl");
//            sql.append(" FROM ");
//            sql.append("mrcsb.b2mcczb mc,");
//            sql.append("PUBLIC .pm pm ");
//            sql.append("WHERE ");
//            sql.append("mc.parent_id IN (SELECT ID FROM mrcsb.originaldata WHERE insert_time ~*'"+time+"') ");
//            sql.append("AND pm.pl_id = '"+pl+"' ");
//            sql.append("AND mc.pmdm = pm. ID ");
//            sql.append("AND mc.farm = '"+sFarm+"' ");
//            sql.append("GROUP BY 1");
//            sql.append(" ) AS mcb,");
//            sql.append("(");
//            sql.append("SELECT DISTINCT ");
//            sql.append("mr.farm AS farm,SUM (mr.jpczkc) AS jpckcl,SUM (mr.jpccksl) AS jpcckl,SUM (mr.spczk) AS spckcl,");
//            sql.append("SUM (mr.spccksl) AS spcckl ");
//            sql.append("FROM ");
//            sql.append("mrcsb.b2mrcsb mr,PUBLIC .pm pm ");
//            sql.append("WHERE ");
//            sql.append("mr.parent_id IN (SELECT ID FROM mrcsb.originaldata WHERE insert_time ~*'"+time+"') ");
//            sql.append("AND pm.pl_id = '"+pl+"' ");
//            sql.append("AND mr.pmdm = pm. ID ");
//            sql.append("AND mr.farm = '"+sFarm+"' ");
//            sql.append("GROUP BY 1");
//            sql.append(") AS mbb,");
//            sql.append("(");
//            sql.append("SELECT DISTINCT ");
//            sql.append("cc.farm AS farm,");
//            sql.append("SUM (cc.mrccl) AS mryjcc ");
//            sql.append("FROM ");
//            sql.append("mrcsb.mryjcc cc,PUBLIC .pm pm ");
//            sql.append("WHERE ");
//            sql.append("cc.parent_id IN (SELECT ID FROM mrcsb.originaldata WHERE insert_time ~*'"+time+"') ");
//            sql.append("AND pm.pl_id = '"+pl+"' ");
//            sql.append("AND cc.pmdm = pm. ID ");
//            sql.append("AND cc.farm = '"+sFarm+"' ");
//            sql.append("GROUP BY 1");
//            sql.append(") AS mcc ");

            sql.append(" SELECT ");
            sql.append("sum(mcczl)");
            sql.append(" FROM ");
            sql.append("mrcsb.b2mcczb mc,");
            sql.append("PUBLIC .pm pm ");
            sql.append("WHERE ");
            sql.append("mc.parent_id IN (SELECT ID FROM mrcsb.originaldata WHERE insert_time=(SELECT max(insert_time) FROM mrcsb.originaldata WHERE insert_time~*'" + time + "') and farm='" + sFarm + "') ");
            sql.append("AND pm.pl_id = '" + pl + "' ");
            sql.append("AND mc.pmdm = pm. ID ");
//            sql.append("AND mc.farm = '"+sFarm+"' ");
            System.out.println(sql.toString() + "\r\n");
            double mcczl = pc.queryDouble(sql.toString());

            StringBuffer sql1 = new StringBuffer();
            sql1.append(" SELECT ");
            sql1.append("CASE WHEN sum(jpczkc) ISNULL THEN 0 ELSE sum(jpczkc) END as \"精品菜库存量(Kg)\",");
            sql1.append("CASE WHEN sum(jpccksl) ISNULL THEN 0 ELSE sum(jpccksl) END as \"精品菜出库量(Kg)\",");
            sql1.append("CASE WHEN sum(spczk) ISNULL THEN 0 ELSE sum(spczk) END as \"商品菜库存量(Kg)\",");
            sql1.append("CASE WHEN sum(spccksl) ISNULL THEN 0 ELSE sum(spccksl) END as \"商品菜出库量(Kg)\" ");
            sql1.append("FROM ");
            sql1.append("mrcsb.b2mrcsb mr,PUBLIC .pm pm ");
            sql1.append("WHERE ");
            sql1.append("mr.parent_id IN (SELECT ID FROM mrcsb.originaldata WHERE insert_time=(SELECT max(insert_time) FROM mrcsb.originaldata WHERE insert_time~*'" + time + "') and farm='" + sFarm + "') ");
            sql1.append("AND pm.pl_id = '" + pl + "' ");
            sql1.append("AND mr.pmdm = pm. ID ");
//            sql1.append("AND mr.farm = '"+sFarm+"' ");
            System.out.println(sql1.toString() + "\r\n");
            ArrayList<Double> mrcs = pc.queryDoubleArr(sql1.toString());
            double jpczkc = (Double) mrcs.get(0);
            double jpccksl = (Double) mrcs.get(1);
            double spczk = (Double) mrcs.get(2);
            double spccksl = (Double) mrcs.get(3);

            StringBuffer sql2 = new StringBuffer();
            sql2.append("SELECT ");
            sql2.append("CASE WHEN sum(cc.mrccl) ISNULL THEN 0 ELSE sum(cc.mrccl) END as \"明日预计出菜数量(Kg)\" ");
            sql2.append("FROM ");
            sql2.append("mrcsb.mryjcc cc,PUBLIC .pm pm ");
            sql2.append("WHERE ");
            sql2.append("cc.parent_id IN (SELECT ID FROM mrcsb.originaldata WHERE insert_time=(SELECT max(insert_time) FROM mrcsb.originaldata WHERE insert_time~*'" + time + "') and farm='" + sFarm + "') ");
            sql2.append("AND pm.pl_id = '" + pl + "' ");
            sql2.append("AND cc.pmdm = pm. ID ");
//            sql2.append("AND cc.farm = '"+sFarm+"' ");
            System.out.println(sql2.toString() + "\r\n");
            double mrccl = pc.queryDouble(sql2.toString());

            if (mcczl + jpczkc + jpccksl + spczk + spccksl + mrccl > 0) {
                code.append("<tr><td>" + sFarm + "</td>");
                code.append("<td>" + mcczl + "</td>");
                code.append("<td>" + jpczkc + "</td>");
                code.append("<td>" + jpccksl + "</td>");
                code.append("<td>" + spczk + "</td>");
                code.append("<td>" + spccksl + "</td>");
                code.append("<td>" + mrccl + "</td>");
                code.append("</tr>");
            }
        }
//		JSONArray json = pc.resultSetToJson(sql.toString());
//		System.out.println(json.toString());


        code.append("</tbody></table>");
        System.out.println(code);
        rst.put("hz", code.toString());
        String tmp = cb + "(" + rst + ")";
        return tmp;
    }

    private String pltj_hj(String pl, String farm, String time) {
        ArrayList farm_ar = new ArrayList();
        if (farm.length() > 0) {
            farm_ar.add(farm);
        } else {
            Dic_farm df = new Dic_farm();
            farm_ar = df.get();
        }
        double mcczl = 0, jpckcl = 0, jpcckl = 0, spckcl = 0, spcckl = 0, mrccl = 0;
        for (int i = 0; i < farm_ar.size(); i++) {
            String f = farm_ar.get(i).toString();
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT ");
            sql.append("b2.mcczl,b1.jpckcl,b1.jpcckl,b1.spckcl,b1.spcckl,b3.mrccl ");
            sql.append("FROM (");
            sql.append("SELECT ");
            sql.append("SUM (T.jpczkc) AS jpckcl,SUM (T.jpccksl) AS jpcckl,SUM (T.spczk) AS spckcl,SUM (T.spccksl) AS spcckl ");
            sql.append("FROM ");
            sql.append("mrcsb.b2mrcsb T,PUBLIC.pm P ");
            sql.append("WHERE ");
//            if (farm.length() > 0) {
            sql.append("T.parent_id IN (SELECT ID FROM mrcsb.originaldata WHERE insert_time=(SELECT max(insert_time) FROM mrcsb.originaldata WHERE insert_time~*'" + time + "') ");
            sql.append("AND farm = '" + f + "' ");
            sql.append(")");
            sql.append(" AND");
//            }
            sql.append(" T.pmdm = P.id ");
            if (pl.length() > 0) {
                sql.append("AND P.pl_id = '" + pl + "' ");
            }
//        if( farm.length() > 0 ){
//            sql.append("AND T.farm = '"+farm+"' ");
//        }
            sql.append(") AS b1,");
            sql.append("(SELECT SUM (T .mcczl) AS mcczl ");
            sql.append("FROM mrcsb.b2mcczb T,PUBLIC.pm P ");
            sql.append("WHERE ");
//            if (farm.length() > 0) {
            sql.append("T.parent_id IN (SELECT ID FROM mrcsb.originaldata WHERE insert_time=(SELECT max(insert_time) FROM mrcsb.originaldata WHERE insert_time~*'" + time + "') ");
            sql.append("AND farm = '" + f + "' ");
            sql.append(")");
            sql.append(" AND");
//            }
            sql.append(" T.pmdm = P.id ");
            if (pl.length() > 0) {
                sql.append("AND P.pl_id = '" + pl + "' ");
            }
//        if( farm.length() > 0 ){
//            sql.append("AND T.farm = '"+farm+"' ");
//        }
            sql.append(") AS b2,");
            sql.append("(SELECT SUM (T.mrccl) AS mrccl ");
            sql.append("FROM mrcsb.mryjcc T,PUBLIC.pm P ");
            sql.append("WHERE ");
//            if (farm.length() > 0) {
            sql.append("T.parent_id IN (SELECT ID FROM mrcsb.originaldata WHERE insert_time=(SELECT max(insert_time) FROM mrcsb.originaldata WHERE insert_time~*'" + time + "') ");
            sql.append("AND farm = '" + f + "' ");
            sql.append(")");
            sql.append(" AND");
//            }
            sql.append(" T.pmdm = P.id ");
            if (pl.length() > 0) {
                sql.append("AND P.pl_id = '" + pl + "' ");
            }
//        if( farm.length() > 0 ){
//            sql.append("AND T.farm = '"+farm+"' ");
//        }
            sql.append(") AS b3");

            ArrayList tmp = pc.queryDoubleArr(sql.toString());
            System.out.println("MRTJ_HJ=====" + sql.toString());
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

        StringBuffer code = new StringBuffer();
        if (result.size() > 0) {
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
            code.append("<table class=\"uk-table uk-table-striped uk-table-condensed uk-text-nowrap\">");
            code.append("<thead>");
            code.append("<tr><th></th><th>毛菜采摘量(Kg)</th><th>精品菜库存量(Kg)</th><th>精品菜出库量(Kg)</th>");
            code.append("<th>商品菜库存量(Kg)</th><th>商品菜出库量(Kg)</th><th>明日预计出菜数量(Kg)</th>");
            code.append("</tr></thead><tbody><tr><td>合计：</td>");
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

    private HashMap pltj(String pl, String farm, String time) {
        ArrayList farm_ar = new ArrayList();
        if (farm.length() > 0) {
            farm_ar.add(farm);
        } else {
            Dic_farm df = new Dic_farm();
            farm_ar = df.get();
        }
        StringBuffer data = new StringBuffer();
        for (int i = 0; i < farm_ar.size(); i++) {
            String f = farm_ar.get(i).toString();
            String maxtime = "SELECT MAX (insert_time) FROM mrcsb.originaldata WHERE insert_time ~* '" + time + "' AND farm = '" + f + "'";
//        if( farm.length() > 0 ){
//            maxtime += "AND farm = '"+farm+"' ";
//        }
            String mtime = pc.query(maxtime);
            StringBuffer sql = new StringBuffer();
//            sql.append("SELECT ROW_NUMBER() OVER() AS xh,id,pm_name,pl_name,mcczl,jpczkc,jpccksl,spczk,spccksl,mrccl");
            sql.append("SELECT id,pm_name,pl_name,mcczl,jpczkc,jpccksl,spczk,spccksl,mrccl");
            sql.append(" from ( ");
            sql.append("select pm.id,pm.pm_name,pl.pl_name");
            sql.append(",(case when b.mcczl is null then 0 else b.mcczl end) as mcczl");
            sql.append(",(case when c.jpczkc is null then 0 else c.jpczkc end) as jpczkc");
            sql.append(",(case when c.jpccksl is null then 0 else c.jpccksl end) as jpccksl");
            sql.append(",(case when c.spczk is null then 0 else c.spczk end) as spczk");
            sql.append(",(case when c.spccksl is null then 0 else c.spccksl end) as spccksl");
            sql.append(",(case when d.mrccl is null then 0 else d.mrccl end) as mrccl ");
            sql.append("FROM pl,pm ");
            sql.append("LEFT JOIN ( ");
            sql.append("select pmdm,sum(mcczl) as mcczl from ");
            sql.append("(select row_number() over(partition by farm,pmdm order by insert_time desc) as ");
            sql.append(" rownum,pmdm,mcczl,farm,insert_time from");
            sql.append("(select rtrim(pmdm) as pmdm,sum(mcczl) as mcczl,farm,insert_time from mrcsb.b2mcczb where ");
            sql.append("insert_time='" + mtime + "' ");
//            if (farm.length() > 0) {
            sql.append("AND farm = '" + f + "' ");
//            }
            sql.append(" group by farm,insert_time,pmdm ) as Test ) as T group by pmdm ) b on pm.id=b.pmdm ");
            sql.append("LEFT JOIN ( select pmdm,sum(jpcjgsl) as jpcjgsl,sum(spcjgsl) as spcjgsl,sum(jpczkc) as jpczkc,sum(jpccksl) as ");
            sql.append("jpccksl,sum(spccksl) as spccksl,sum(spczk) as spczk");
            sql.append(" from ( select row_number() over(partition by farm,pmdm order by insert_time desc) as");
            sql.append(" rownum,pmdm,jpcjgsl,spcjgsl,jpczkc,jpccksl,spccksl,spczk");
            sql.append(" from mrcsb.b2mrcsb where ");
            sql.append("insert_time='" + mtime + "' ");
//            if (farm.length() > 0) {
            sql.append("AND farm = '" + f + "' ");
//            }
            sql.append(") as T group by pmdm ) c on pm.id=c.pmdm ");
            sql.append("LEFT JOIN ( select pmdm,sum(mrccl) as mrccl ");
            sql.append(" from ( select row_number() over(partition by farm,pmdm order by insert_time desc) as");
            sql.append(" rownum,pmdm,mrccl");
            sql.append(" from mrcsb.mryjcc where ");
            sql.append("insert_time='" + mtime + "' ");
//            if (farm.length() > 0) {
            sql.append("AND farm = '" + f + "' ");
//            }
            sql.append(") as T group by pmdm ) d on pm.id=d.pmdm ");
            sql.append("WHERE ");
            sql.append("pm.pl_id = pl. ID ");
            if (pl.length() > 0) {
                sql.append("AND pl.id = '" + pl + "' ");
            }
//        sql.append("GROUP BY 1,2,3");
//        sql.append("LIMIT 10 OFFSET "+offset);
            sql.append(" order by pm.id");
            sql.append(" ) as ta where mcczl+jpczkc+jpccksl+spczk+spccksl+mrccl>0");
            System.out.println(sql);
            JSONArray json = pc.resultSetToJson(sql.toString());
            if (json.size() > 0) {
                for (int j = 0; j < json.size(); j++) {
                    JSONObject js = (JSONObject) json.get(j);

                    Iterator<?> obj = js.keys();
                    data.append("<tr>");
                    while (obj.hasNext()) {// 遍历JSONObject
                        String key = (String) obj.next().toString();

                        String d = js.getString(key);
                        data.append("<td>" + d + "</td>");
                    }

                    data.append("</tr>");
                }

            }
        }
        StringBuffer code = new StringBuffer();
        if (data.length() > 0) {
            code.append("<table id=\"tttt\" class=\"uk-table uk-table-striped uk-table-condensed uk-text-nowrap\">");
            code.append("<thead>");
            code.append("<tr><th>品名代码</th><th>品名</th><th>品类</th><th>毛菜采摘量(Kg)</th><th>精品菜库存量(Kg)</th><th>精品菜出库量(Kg)</th>");
            code.append("<th>商品菜库存量(Kg)</th><th>商品菜出库量(Kg)</th><th>明日预计出菜数量(Kg)</th>");
            code.append("</tr></thead><tbody id=\"datagrid\">");
            code.append(data.toString());
            code.append("</tbody></table>");
//            if(json.size() > 9){
//                code.append("<p align=\"right\"><button class=\"uk-button-link uk-button-small\" type=\"button\">下一页</button></p>");
//            }
        }
        System.out.println("pltj:" + code.toString());
        HashMap map = new HashMap();
        map.put("code", code.toString());
//        map.put("offset",offset);
        return map;
    }

    @Path("plhz")
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public String getJson(@QueryParam("pl") String pl, @QueryParam("rq") String time, @QueryParam("farm") String farm, @QueryParam("callback") String cb) {
        JSONObject rst = new JSONObject();

        time = time.replaceAll("-", "");
        try {
            pl = URLDecoder.decode(pl, "UTF-8");
            farm = URLDecoder.decode(farm, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {

        }
        if ("全部".equals(pl)) {
            pl = "";
        } else if ("根茎类".equals(pl)) {
            pl = "PL02";
        } else if ("叶菜".equals(pl)) {
            pl = "PL04";
        } else if ("果菜".equals(pl)) {
            pl = "PL01";
        } else if ("玉米类".equals(pl)) {
            pl = "PL06";
        } else if ("豆类".equals(pl)) {
            pl = "PL07";
        }
        if ("全国".equals(farm)) {
            farm = "";
        }
        String hj = pltj_hj(pl, farm, time);
        rst.put("hj", hj);
        HashMap tj = pltj(pl, farm, time);
        rst.put("tj", tj.get("code"));
        String tmp = cb + "(" + rst + ")";
        return tmp;
    }
}