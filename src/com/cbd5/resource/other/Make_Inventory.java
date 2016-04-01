package com.cbd5.resource.other;

import java.util.ArrayList;
import java.util.Iterator;

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

/**
 * 盘库设置接口
 * 
 * @author vicent
 *
 */
@Path("/config")
public class Make_Inventory {

	PostgresCommon pc = Application.pc;

	@Path("s")
	@GET
	@Produces(value = MediaType.TEXT_PLAIN)
	public String getJson(@QueryParam("time") String time) {
		String rst = "";
		StringBuffer update = new StringBuffer();
		String state_q = "select value from config where name='盘库完成'";
		String state = pc.query(state_q);
		if(time != null){
			time = time.replaceAll("/", "");
			
			if ("Y".equalsIgnoreCase(state)) {
				update.append("update config set value='N' where name='盘库完成';");
				update.append("update config set value = '" + time + "' where name='盘库日';");
				
				String qt = "select distinct t1.table_name from information_schema.columns t1,"
						+ "(select value as val from public.config where name='盘库日') t2 where "
						+ "t1.TABLE_NAME  ~*t2.val";
				ArrayList list = pc.querySingle(qt);
				for(int i=0; i<list.size(); i++){
					String drop = "drop table sctrb.\""+list.get(i).toString()+"\"";
					update.append(drop+";");
				}
				rst = "盘库开始。。。";
			}
			
		}
		if ("N".equalsIgnoreCase(state)) {
			update.append("update config set value='Y' where name='盘库完成';");
			rst = "盘库完成";
		}
		int r = pc.insert(update.toString());
		if(r <= 0){
			rst = "";
		}
		return rst;
	}

	@Path("q")
	@GET
	@Produces(value = MediaType.TEXT_PLAIN)
	public String status() {
		String state_q = "select value from config where name='盘库完成'";
		String state = pc.query(state_q);
		
		return state;
	}
}