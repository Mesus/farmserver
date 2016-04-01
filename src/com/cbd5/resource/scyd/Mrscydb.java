package com.cbd5.resource.scyd;

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
import java.util.UUID;

/**
 * Created by Administrator on 2016/2/29 0029.
 */
@Path("/table4")
public class Mrscydb {
    PostgresCommon pc = Application.pc;
    HashMap tab = Application.tab;

    @Path("getscmc")
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public String getScmc(@QueryParam("jddm") String jddm, @QueryParam("callback") String cb) {
        JSONObject j = new JSONObject();

        StringBuffer sql = new StringBuffer();
        sql.append("select distinct t1.sm_code as scbm,t2.sm_abbr as scmc from scyd.jddm_sm t1 left join scyd.dic_sm t2 on t1.sm_code=t2.sm_code where jddm_code='"+jddm+"'");
        JSONArray array = pc.resultSetToJson(sql.toString());
        if (array == null || array.size() == 0) {
            j.put("arr", 0);
        } else {
            j.put("arr", array);
        }
        String tmp = cb + "(" + j + ")";
        return tmp;
    }


    @Path("gettable")
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public String getTable(@QueryParam("where") String condition,@QueryParam("callback") String cb) {
        JSONObject j = new JSONObject();

        StringBuffer sql = new StringBuffer();

        sql.append("select order_id as id,jddm_name,pmdm,pm,pl,sm_abbr,available,ava_margin,margin,is_overrun,operation,(case when pack_num is null then 0 else pack_num end) as pack_num from scyd.order where is_del=false");

        JSONObject where = null;
        where = JSONObject.fromObject(condition);

        Iterator<?> obj=where.keys();
        while (obj.hasNext()) {
            String key = (String) obj.next().toString();
            String data = where.getString(key);
            sql.append(" and " + key + "='" + data + "'");
        }

        JSONArray array = pc.resultSetToJson(sql.toString());
        if (array == null || array.size() == 0) {
            j.put("arr", 0);
        } else {
            j.put("arr", array);
        }
        String tmp = cb + "(" + j + ")";
        return tmp;
    }


    @Path("getdata")
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public String getData(@QueryParam("id") String id,@QueryParam("callback") String cb) {
        JSONObject j = new JSONObject();

        StringBuffer sql = new StringBuffer();

        sql.append("select jddm_name,pmdm,pm,pl,sm_abbr,sm_code,jddm,jddm_name,available,ava_margin,margin,is_overrun,operation,pack_num from scyd.order where order_id='"+id+"'");

        JSONArray array = pc.resultSetToJson(sql.toString());
        if (array == null || array.size() == 0) {
            j.put("arr", 0);
        } else {
            j.put("arr", array);
        }
        String tmp = cb + "(" + j + ")";
        return tmp;
    }



    @Path("ins")
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public String getInsert(@QueryParam("form") String form, @QueryParam("callback") String cb) {
        JSONObject j = new JSONObject();

        StringBuffer sql = new StringBuffer();
        String field = "(order_id,";
        String uuid = UUID.randomUUID().toString();
        String value = "('"+uuid+"',";

        JSONObject fields = null;
        fields = JSONObject.fromObject(form);
        Iterator<?> obj=fields.keys();
        while (obj.hasNext()) {
            String key = (String) obj.next().toString();
            String data = fields.getString(key);
            field += key + ",";
            if(key.equals("id_del"))
                value += "false,";
            else if(key.equals("available")||key.equals("ava_margin")||key.equals("margin")||key.equals("is_overrun"))
                value += data + ",";
            else
                value +="'" + data + "',";
        }
        field += "is_del)";
        value += "false)";

        sql.append("insert into scyd.order"+field+" values"+value);

        //return sql.toString();
        int rst = pc.insert(sql.toString());
        j.put("result", "" + rst);
        String tmp = cb + "(" + j + ")";
        return tmp;
    }

    @Path("upd")
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public String getUpdate(@QueryParam("form") String form,@QueryParam("id") String id,@QueryParam("callback") String cb) {
        JSONObject j = new JSONObject();

        StringBuffer sql = new StringBuffer();

        String field="";
        JSONObject fields = null;
        fields = JSONObject.fromObject(form);
        Iterator<?> obj=fields.keys();
        while (obj.hasNext()) {
            String key = (String) obj.next().toString();
            String data = fields.getString(key);
            if(key.equals("available")||key.equals("ava_margin")||key.equals("margin")||key.equals("is_overrun"))
                field += key + "=" + data + ",";
            else
                field += key + "='" + data + "',";
        }
        field = field.substring(0, field.length() - 1);

        sql.append("update scyd.order set "+field+" where order_id='"+id+"'");

        int rst = pc.insert(sql.toString());
        j.put("result", "" + rst);
        String tmp = cb + "(" + j + ")";
        return tmp;
    }



    @Path("del")
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public String getDelete(@QueryParam("id") String id,@QueryParam("callback") String cb) {
        JSONObject j = new JSONObject();

        StringBuffer sql = new StringBuffer();
        sql.append("update scyd.order set is_del=true where order_id='"+id+"'");

        int rst = pc.insert(sql.toString());
        j.put("result", "" + rst);
        String tmp = cb + "(" + j + ")";
        return tmp;
    }



    @Path("getpmdata")
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public String getPmData(@QueryParam("pmdm") String pmdm,@QueryParam("jddm") String jddm,@QueryParam("time") String time,@QueryParam("callback") String cb) {
        JSONObject j = new JSONObject();

        String farm="";
        if (jddm.equals("010001"))
            farm="北京";
        if (jddm.equals("010002"))
            farm="扬州";
        if (jddm.equals("010003"))
            farm="云南";
        if (jddm.equals("010004"))
            farm="上海";
        if (jddm.equals("010005"))
            farm="山东";
        if (jddm.equals("010006"))
            farm="广州";
        if (jddm.equals("010007"))
            farm="成都";
        if (jddm.equals("010008"))
            farm="惠州";
        if (jddm.equals("010009"))
            farm="顺义";
        if (jddm.equals("010010"))
            farm="承德";
        if (jddm.equals("010011"))
            farm="长沙";

        StringBuffer sql = new StringBuffer();
        sql.append("select pm_name as pm,(case when pl_id='PL01' then '果菜' when pl_id='PL02' then '根茎类' when pl_id='PL04' then '叶菜' end) as pl,(jpczkc+mrccl) as kdyzl,(jpczkc+mrccl-margin) as kydl" +
                    " from pm left join" +
                    " (select pmdm,(case when jpczkc is null then 0 else jpczkc end) as jpczkc from mrcsb.b2mrcsb where sheet=4 and pmdm='"+pmdm+"' and insert_time in (select max(insert_time) from mrcsb.originaldata where insert_time>='"+time+"00' and insert_time<='"+time+"24' and farm='"+farm+"')" +
                    ") t1 on pm.id=t1.pmdm left join" +
                    " (select pmdm,(case when mrccl is null then 0 else mrccl end) as mrccl from mrcsb.mryjcc where pmdm='"+pmdm+"' and insert_time in (select max(insert_time) from mrcsb.originaldata where insert_time>='"+time+"00' and insert_time<='"+time+"24' and farm='"+farm+"')" +
                    ")t2 on pm.id=t2.pmdm left join" +
                    " (select pmdm,(case when sum(margin) is null then 0 else sum(margin) end) as margin from scyd.order where book_date='"+time+"' and jddm='"+jddm+"' and pmdm='"+pmdm+"' group by pmdm" +
                    ")t3 on pm.id=t3.pmdm where pm.id='"+pmdm+"'");
        JSONArray array = pc.resultSetToJson(sql.toString());
        if (array == null || array.size() == 0) {
            j.put("arr", 0);
        } else {
            j.put("arr", array);
        }
        String tmp = cb + "(" + j + ")";
        return tmp;
    }

    @Path("copy")
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public String getCopy(@QueryParam("jddm") String jddm,@QueryParam("scid") String scid,@QueryParam("beforetime") String beforetime,@QueryParam("time") String time,@QueryParam("username") String name,@QueryParam("callback") String cb) {
        JSONObject j = new JSONObject();

        String farm="";
        if (jddm.equals("010001"))
            farm="北京";
        if (jddm.equals("010002"))
            farm="扬州";
        if (jddm.equals("010003"))
            farm="云南";
        if (jddm.equals("010004"))
            farm="上海";
        if (jddm.equals("010005"))
            farm="山东";
        if (jddm.equals("010006"))
            farm="广州";
        if (jddm.equals("010007"))
            farm="成都";
        if (jddm.equals("010008"))
            farm="惠州";
        if (jddm.equals("010009"))
            farm="顺义";
        if (jddm.equals("010010"))
            farm="承德";
        if (jddm.equals("010011"))
            farm="长沙";

        StringBuffer sql = new StringBuffer();
        sql.append("insert into scyd.order(order_id,pmdm,pm,pl,sm_abbr,sm_code,available,ava_margin,margin,is_overrun,jddm,jddm_name,operation,is_del,bz,book_date,pack_num)" +
                " select mrcsb.uuid_generate_v1(),table2.pmdm,pm,pl,sm_abbr,sm_code,kdyzl as available,(case when kydl is null then kdyzl else kydl end) as ava_margin,margin,is_overrun,jddm,jddm_name,operation,is_del,bz,'"+time+"',pack_num" +
                " from scyd.order table2 left join (" +
                    "select id as pmdm,(case when (jpczkc+mrccl) is null then 0 else (jpczkc+mrccl) end) as kdyzl,(jpczkc+mrccl-margin) as kydl from pm left join" +
                    " (select pmdm,(case when jpczkc is null then 0 else jpczkc end) as jpczkc from mrcsb.b2mrcsb where sheet=4 and insert_time in (select max(insert_time) from mrcsb.originaldata where insert_time>='"+time+"00' and insert_time<='"+time+"24' and farm='"+farm+"')) t1 on pm.id=t1.pmdm left join" +
                    " (select pmdm,(case when mrccl is null then 0 else mrccl end) as mrccl from mrcsb.mryjcc where insert_time in (select max(insert_time) from mrcsb.originaldata where insert_time>='"+time+"00' and insert_time<='"+time+"24' and farm='"+farm+"'))t2 on pm.id=t2.pmdm left join" +
                    " (select pmdm,(case when sum(margin) is null then 0 else sum(margin) end) as margin from scyd.order where book_date='"+time+"' and jddm='"+jddm+"' group by pmdm)t3 on pm.id=t3.pmdm" +
                ")table1 on table1.pmdm=table2.pmdm where is_del=false and book_date='"+beforetime+"' and jddm='"+jddm+"' and sm_code='"+scid+"' and operation='"+name+"'");

        int rst = pc.insert(sql.toString());
        j.put("result", "" + rst);
        String tmp = cb + "(" + j + ")";
        return tmp;
    }
}
