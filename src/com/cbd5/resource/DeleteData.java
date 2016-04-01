package com.cbd5.resource;

import com.cbd5.PostgresCommon;

public class DeleteData {

	PostgresCommon pc = new PostgresCommon();
	
	private void sctrb(String farm,String time){
		String[] arr = farm.split(",");
		pc.conn();
		for(int i=0; i<arr.length; i++){
			String sql = "DELETE FROM sctrb.sctrb_rk WHERE s_orid in (SELECT o_id FROM sctrb.ori_data WHERE o_name ~*'"+arr[i]+"' and o_timeflag ~*'"+time+"')";
			System.out.println(sql);
			pc.insert(sql);
			sql = "DELETE FROM sctrb.sctrb_ck WHERE s_orid in (SELECT o_id FROM sctrb.ori_data WHERE o_name ~*'"+arr[i]+"' and o_timeflag ~*'"+time+"')";
			System.out.println(sql);
			pc.insert(sql);
			sql = "DELETE FROM sctrb.sczz WHERE s_orid in (SELECT o_id FROM sctrb.ori_data WHERE o_name ~*'"+arr[i]+"' and o_timeflag ~*'"+time+"')";
			System.out.println(sql);
			pc.insert(sql);
			sql = "DELETE FROM sctrb.yjscym WHERE s_orid in (SELECT o_id FROM sctrb.ori_data WHERE o_name ~*'"+arr[i]+"' and o_timeflag ~*'"+time+"')";
			System.out.println(sql);
			pc.insert(sql);
			sql = "DELETE FROM sctrb.ori_data WHERE  o_name ~*'"+arr[i]+"' and o_timeflag ~*'"+time+"'";
			System.out.println(sql);
			pc.insert(sql);
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DeleteData dd = new DeleteData();
		dd.sctrb("成都","2016012018");
	}

}
