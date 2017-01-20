package com.xiaohongshu.richedittextpro.spannable.richparser.strategy;

import android.graphics.Color;

import com.xiaohongshu.richedittextpro.R;
import com.xiaohongshu.richedittextpro.spannable.richparser.base.AbstractRichParser;

/**
 * Created by wupengjian on 17/1/12.
 */
public class SimpleRichParser extends AbstractRichParser {

    ////////////////////////////////////////////////////////// 下面是为Local提供的各种操作 ///////////////////////////////////////////////////////////////

    @Override
    protected String getFlag() {
        return "×";
    }

    /**
     * ^有何不可^
     *
     * @return
     */
    @Override
    public String getRichPattern4Local() {
        return String.format("%s[^%s]+%s", getFlag(), getFlag(), getFlag());
    }

    protected int getDrawableId() {
        return R.mipmap.poi;
    }

    protected int getColor() {
        return Color.parseColor("#ffff4444");
    }

    ////////////////////////////////////////////////////////// 下面是为Server提供的各种操作 ///////////////////////////////////////////////////////////////

    /**
     * #有何不可#
     *
     * @return
     */
    @Override
    public String getRichPattern4Server() {
        return "#[^#\\[\\]]{1,}#";
    }

    @Override
    public String getType4Server() {
        return "";
    }

    @Override
    public String getContentFromRichStr4Server(String str) {

        //掐头去尾,不要前面的#和后面的#
        return str.substring(1, str.length() - 1);
    }

    ////////////////////////////////////////////////////////// 下面是为Local和Server之间提供适配的各种操作 ///////////////////////////////////////////////////////////////
}
