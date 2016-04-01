package com.cbd5.resource.yjkg;

import com.cbd5.Application;
import com.cbd5.PostgresCommon;

import java.util.HashMap;

/**
 * Created by CBD5 on 3/21/16.
 */
public class Pm_Entity {
    static PostgresCommon pc = Application.pc;
    private static HashMap pm_map = new HashMap();

    public static HashMap getPm() {
        if (pm_map.size() == 0) {
            pm_map = pc.resultToMap("SELECT t1.id,t1.pm_name,t2.pl_name FROM pm t1,pl t2 WHERE t1.pl_id=t2.id");
        }
        return pm_map;
    }
}
