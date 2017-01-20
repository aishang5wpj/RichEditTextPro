package com.xiaohongshu.richedittextpro.copy.richparser.strategy;

import android.graphics.Color;

import com.xiaohongshu.richedittextpro.R;
import com.xiaohongshu.richedittextpro.copy.richparser.base.AbstractRichParser;
import com.xiaohongshu.richedittextpro.copy.richparser.base.OnSpannableClickListener;

/**
 * Created by wupengjian on 17/1/17.
 */
public class NormalRichParser extends AbstractRichParser {

    public NormalRichParser() {
        this(null);
    }

    public NormalRichParser(OnSpannableClickListener listener) {
        super(listener);
    }

    @Override
    protected int getColor() {
        return Color.parseColor("#ffff4444");
    }

    @Override
    protected int getDrawableId() {
        return R.mipmap.hashtag;
    }

    @Override
    public String getPattern4Server() {
        //￼舍不得
        return String.format("#[^#\\[\\]]{1,}(\\[%s\\])#", getType4Server());
    }

    @Override
    public String getType4Server() {
        return "音乐";
    }
}
