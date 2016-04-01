package com.cbd5.resource;
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

@Path("/login")
public class Login {

	PostgresCommon pc = Application.pc;
	@Path("verify")
	@GET
	@Produces(value=MediaType.APPLICATION_JSON)
    public String getJson(@QueryParam("un") String name,@QueryParam("pw") String pw,@QueryParam("callback") String cb){
		JSONObject j = new JSONObject();
		if("cbd5.com".equals(name) && "123456".equals(pw)){
			j.put("result", "s");
		}else{
			j.put("result", "f");
		}
		String tmp = cb+"("+j+")";
    	return tmp;
    }
	
}