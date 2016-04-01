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
 * 蔬菜采收表订单统计
 *
 * @author vicent
 */
@Path("/ddtj")
public class Ddtj {

    PostgresCommon pc = Application.pc;


    private String pltj_hj(String pl, String farm, String time) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT ");
        sql.append("b2.mcczl,b1.jpckcl,b1.jpcckl,b1.spckcl,b1.spcckl,b3.mrccl ");
        sql.append("FROM (");
        sql.append("SELECT ");
        sql.append("SUM (T.jpczkc) AS jpckcl,SUM (T.jpccksl) AS jpcckl,SUM (T.spczk) AS spckcl,SUM (T.spccksl) AS spcckl ");
        sql.append("FROM ");
        sql.append("mrcsb.b2mrcsb T,PUBLIC.pm P ");
        sql.append("WHERE ");
        sql.append("T.parent_id IN (SELECT id FROM mrcsb.originaldata WHERE insert_time ~* '" + time + "') ");
        sql.append("AND T.pmdm = P.id ");
        if (pl.length() > 0) {
            sql.append("AND P.pl_id = '" + pl + "' ");
        }
        if (farm.length() > 0) {
            sql.append("AND T.farm = '" + farm + "' ");
        }
        sql.append(") AS b1,");
        sql.append("(SELECT SUM (T .mcczl) AS mcczl ");
        sql.append("FROM mrcsb.b2mcczb T,PUBLIC.pm P ");
        sql.append("WHERE ");
        sql.append("T.parent_id IN (SELECT id FROM mrcsb.originaldata WHERE insert_time ~* '" + time + "') ");
        sql.append("AND T.pmdm = P.id ");
        if (pl.length() > 0) {
            sql.append("AND P.pl_id = '" + pl + "' ");
        }
        if (farm.length() > 0) {
            sql.append("AND T.farm = '" + farm + "' ");
        }
        sql.append(") AS b2,");
        sql.append("(SELECT SUM (T.mrccl) AS mrccl ");
        sql.append("FROM mrcsb.mryjcc T,PUBLIC.pm P ");
        sql.append("WHERE ");
        sql.append("T.parent_id IN (SELECT id FROM mrcsb.originaldata WHERE insert_time ~* '" + time + "') ");
        sql.append("AND T.pmdm = P.id ");
        if (pl.length() > 0) {
            sql.append("AND P.pl_id = '" + pl + "' ");
        }
        if (farm.length() > 0) {
            sql.append("AND T.farm = '" + farm + "' ");
        }
        sql.append(") AS b3");
        ArrayList result = pc.queryDoubleArr(sql.toString());

        StringBuffer code = new StringBuffer();
        if (result.size() > 0) {
            code.append("<table class=\"uk-table uk-table-striped uk-table-condensed uk-text-nowrap\">");
            code.append("<thead>");
            code.append("<tr><th></th><th>毛菜采摘量(Kg)</th><th>精品菜库存量(Kg)</th><th>精品菜出库量(Kg)</th>");
            code.append("<th>商品菜库存量(Kg)</th><th>商品菜出库量(Kg)</th><th>明日预计出菜数量(Kg)</th>");
            code.append("</tr></thead><tbody><tr><td>合计：</td>");
            code.append("<td>" + result.get(0) + "</td>");
            code.append("<td>" + result.get(1) + "</td>");
            code.append("<td>" + result.get(2) + "</td>");
            code.append("<td>" + result.get(3) + "</td>");
            code.append("<td>" + result.get(4) + "</td>");
            code.append("<td>" + result.get(5) + "</td>");
            code.append("</tr></tbody></table>");
        }
        return code.toString();
    }

    private HashMap pltj(String pl, String farm, String time, String order) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT ");
        sql.append("DISTINCT ");
        sql.append("t1." + pl + ",");
        sql.append("t1.pmdm,");
        sql.append("pm.pm_name,");
        sql.append("pl.pl_name,");
        if ("xsdd".equals(pl) || "dbdd".equals(pl)) {
            sql.append("CASE WHEN SUM(t1.drspcck_1) ISNULL THEN 0 ELSE SUM(t1.drspcck_1) END as drsp ");
            sql.append("FROM mrcsb.b2mrcsb t1,");
        } else {
            sql.append("CASE WHEN SUM(t1.mcczl) ISNULL THEN 0 ELSE SUM(t1.mcczl) END as mcczl ");
            sql.append("FROM mrcsb.b2mcczb t1,");
        }
        sql.append("pm,pl ");
        sql.append("WHERE ");
        sql.append("t1.pmdm = pm.id ");
        sql.append("and pl.id=pm.pl_id");
        if (order.length() > 0) {
            sql.append(" and " + pl + "='" + order + "'");
        } else {
            sql.append(" and length(" + pl + ")>0");
        }
        if (time.length() > 0) {
            sql.append(" AND t1.insert_time ~* '" + time + "' ");
        }
        if (farm.length() > 0) {
            sql.append("AND t1.farm = '" + farm + "' ");
        }
        sql.append(" GROUP BY 1,2,3,4");
//        sql.append("LIMIT 10 OFFSET "+offset);
        System.out.println(sql);
        JSONArray json = pc.resultSetToJson(sql.toString());

        StringBuffer code = new StringBuffer();
        if (json.size() > 0) {
            code.append("<table id=\"tttt\" class=\"uk-table uk-table-striped uk-table-condensed uk-text-nowrap\">");
            code.append("<thead>");
            code.append("<tr><th>订单号</th><th>品名代码</th><th>品名</th><th>品类</th><th>数量</th>");
            code.append("</tr></thead><tbody id=\"datagrid\">");
            for (int i = 0; i < json.size(); i++) {
                JSONObject j = (JSONObject) json.get(i);

                Iterator<?> obj = j.keys();
                code.append("<tr>");
                while (obj.hasNext()) {// 遍历JSONObject
                    String key = (String) obj.next().toString();

                    String data = j.getString(key);
                    code.append("<td>" + data + "</td>");
                }

                code.append("</tr>");
            }
            code.append("</tbody></table>");
//            if(json.size() > 9){
//                code.append("<p align=\"right\"><button class=\"uk-button-link uk-button-small\" type=\"button\">下一页</button></p>");
//            }
        } else {
            code.append("没有查询到相关数据");
        }
        System.out.println("ddtj:" + code.toString());
        HashMap map = new HashMap();
        map.put("code", code.toString());
//        map.put("offset",offset);
        return map;
    }

    @Path("q")
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public String getJson(@QueryParam("order") String pl, @QueryParam("rq") String time, @QueryParam("farm") String farm, @QueryParam("val") String order_val, @QueryParam("callback") String cb) {
        JSONObject rst = new JSONObject();

        time = time.replaceAll("-", "");
        try {
            pl = URLDecoder.decode(pl, "UTF-8");
            farm = URLDecoder.decode(farm, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {

        }
        if ("生产订单".equals(pl)) {
            pl = "scdd";
        } else if ("销售订单".equals(pl)) {
            pl = "xsdd";
        } else if ("调拨订单".equals(pl)) {
            pl = "dbdd";
        }
        if ("全国".equals(farm)) {
            farm = "";
        }
//        String hj = pltj_hj(pl,farm,time);
//        rst.put("hj",hj);
        HashMap tj = pltj(pl, farm, time, order_val);
        rst.put("tj", tj.get("code"));
        String tmp = cb + "(" + rst + ")";
        return tmp;
    }
}