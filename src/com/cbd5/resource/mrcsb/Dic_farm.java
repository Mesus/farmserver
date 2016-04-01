package com.cbd5.resource.mrcsb;

import com.cbd5.Application;
import com.cbd5.PostgresCommon;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by CBD5 on 3/3/16.
 */
public class Dic_farm {

    PostgresCommon pc = Application.pc;

    //全局农场字典
    public static ArrayList farm_dic = new ArrayList();

    public ArrayList get() {
        if (farm_dic.size() == 0) {
            farm_dic = pc.queryArr("select farm from farm");
        }
        return farm_dic;
    }


}
