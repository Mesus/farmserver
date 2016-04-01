package com.cbd5.resource.other;
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
 * 蔬菜采收表上报信息汇总
 * @author vicent
 *
 */
@Path("/log")
public class Sctrb_log {

	PostgresCommon pc = Application.pc;
	@Path("sctr")
	@GET
	@Produces(value=MediaType.TEXT_PLAIN)
    public String getJson(@QueryParam("time_s") String time,@QueryParam("time_e") String timee){
		time = time.replaceAll("/", "-");
		timee = timee.replaceAll("/", "-");
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
		sql.append("l_msg as 日志内容,l_time as 发生时间 from manage.s_log where l_type='1' and ");
		if(time.equals(timee)){
			sql.append("l_time>='"+time+"'");
		}else{
			sql.append("l_time>='"+time+"' and l_time<='"+timee+"'");
		}
		JSONArray json = pc.resultSetToJson(sql.toString());
//		System.out.println(json.toString());
		
		
		StringBuffer div = new StringBuffer();
		div.append("<table class=\"uk-table\">");
		div.append("<thead>");
		div.append("<tr>");
		
		StringBuffer tbody = new StringBuffer();
		tbody.append("<tbody>");
		for(int i=0; i<json.size(); i++){
			JSONObject j = (JSONObject)json.get(i);
//			System.out.println(j.toString());
			
			Iterator<?> obj=j.keys(); 
			tbody.append("<tr>");
			while (obj.hasNext()) {// 遍历JSONObject
				String key = (String) obj.next().toString();
				if( i == 0 ){
					div.append("<th>"+key+"</th>");
				}
				String data = j.getString(key);
				tbody.append("<td>"+data+"</td>");
			}
			if( i == 0 ){
				div.append("</tr>");
			}
			tbody.append("</tr>");
		}
		tbody.append("</tbody>");
		div.append(tbody.toString());
		div.append("</table>");
		
    	return div.toString();
    }
	
}