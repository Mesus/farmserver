package com.cbd5.resource;
import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.cbd5.Application;
import com.cbd5.PostgresCommon;
import com.cbd5.TestBean;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Path("/dkhchk")
public class DkhCheck {

    PostgresCommon pc = Application.pc;
    HashMap tab = Application.tab;

    @Path("c")
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public String getJson(@QueryParam("jddm") String jddm,@QueryParam("dkh") String dkh,@QueryParam("callback") String cb){
        JSONObject j = new JSONObject();
        if (jddm == null) {
            j.put("error", "jddm is empty");
            String tmp = cb + "(" + j + ")";
            return tmp;
        }
        if (dkh == null) {
            j.put("error", "dkh is empty");
            String tmp = cb + "(" + j + ")";
            return tmp;
        }

        String sql = "select count(*) from dic_dk t1 where t1.jddm='" + jddm + "' and t1.dkh='" + dkh + "'";
        int e = Integer.parseInt(pc.query(sql));
        if (e > 0) {
            j.put("check", "pass");
        } else {
            j.put("check", "reject");
        }
        String tmp = cb + "(" + j + ")";
        return tmp;
    }

    @Path("q")
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public String querydata(@QueryParam("jddm") String jddm, @QueryParam("name") String name,
                            @QueryParam("callback") String cb) {
        JSONObject j = new JSONObject();

        StringBuffer sql = new StringBuffer();
        /*sql.append("select ");
		sql.append(
				"t1.dkh as dkh ");
		sql.append("from ");
		sql.append("dic_dk t1 ");
		sql.append("where ");
		sql.append("t1.jddm='"+jddm+"' ");*/
        String jddmtj = " and jddm='" + jddm + "'";
        //sql.append("select distinct dkh from dic_dk where jddm='"+jddm+"' and dkh in (select distinct dkh from humitrue.wsdjc t1 where t1.enable=true"+jddmtj+") or dkh in (select distinct dkh from humitrue.trjcb t2 where t2.enable=true"+jddmtj+") or dkh in (select distinct dkh from humitrue.scjyb t3 where t3.enable=true"+jddmtj+") order by dkh");
        sql.append("select distinct dkh from dic_dk where jddm='" + jddm + "' and dkh in (select distinct dkh from humitrue.\"" + name + "\" where enable=true and jddm='" + jddm + "') order by dkh");
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