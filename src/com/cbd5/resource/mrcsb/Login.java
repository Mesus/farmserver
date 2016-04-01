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

/**
 * Created by Administrator on 2016/2/26 0026.
 */
@Path("/login")
public class Login {
    PostgresCommon pc = Application.pc;
    HashMap tab = Application.tab;

    @Path("bctable")
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public String getPm(@QueryParam("user") String name,@QueryParam("pwd") String pwd, @QueryParam("callback") String cb) {
        JSONObject j = new JSONObject();

        StringBuffer sql = new StringBuffer();
        sql.append("");
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
