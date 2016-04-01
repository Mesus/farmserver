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

@Path("/")
public class FileManage {

	PostgresCommon pc = Application.pc;
	@Path("fl")
	@GET
	@Produces(value=MediaType.APPLICATION_JSON)
    public JSONArray getJson(){
		JSONArray array = new JSONArray();
		array = pc.resultSetToJson("select * from sctrb.ori_data");
		return array;
    }
	
}