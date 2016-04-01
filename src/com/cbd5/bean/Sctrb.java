package com.cbd5.bean;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Sctrb {

	private int id;  
	private String pl;
	private String pm;
	private String zzpz;
	
	@Id  
    public int getId() {  
        return id;  
    }  
  
    public void setId(int id) {  
        this.id = id;  
    }  
	public String getPl(){
		return pl;
	}
	
	public void setPl(String pl){
		this.pl = pl;
	}
	
	public String getPm(){
		return pm;
	}
	
	public void setPm(String pm){
		this.pm = pm;
	}
	
	public String getZzpz(){
		return zzpz;
	}
	
	public void setZzpz(String zzpz){
		this.zzpz = zzpz;
	}
}
