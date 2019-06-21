package com.xiaohongshu.richedittextpro.copy.richparser.strategy;

import android.graphics.Color;

import com.xiaohongshu.richedittextpro.R;
import com.xiaohongshu.richedittextpro.copy.richparser.base.AbstractRichParser;
import com.xiaohongshu.richedittextpro.copy.richparser.base.OnSpannableClickListener;

/**
 * Created by wupengjian on 17/1/17.
 */
public class PoiRichParser extends NormalRichParser {

    public PoiRichParser() {
        this(null);
    }

    public PoiRichParser(OnSpannableClickListener listener) {
        super(listener);
    }

    @Override
    protected int getColor() {
        return Color.parseColor("#ff33b5e5");
    }

    @Override
    protected int getDrawableId() {
        return R.mipmap.poi;
    }

    @Override
    public String getType4Server() {
        return "位置";
    }
}
