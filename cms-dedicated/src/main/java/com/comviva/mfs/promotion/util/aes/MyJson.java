package com.comviva.mfs.promotion.util.aes;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by tarkeshwar.v on 2/16/2017.
 */
@Getter
@Setter
public class MyJson {
    private String var1;
    private String var2;

    public MyJson(String var1, String var2) {
        this.var1 = var1;
        this.var2 = var2;
    }

    public MyJson() {
    }
}
