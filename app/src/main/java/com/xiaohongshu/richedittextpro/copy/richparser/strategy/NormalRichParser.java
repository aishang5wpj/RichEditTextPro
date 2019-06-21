package com.xiaohongshu.richedittextpro.copy.richparser.strategy;

import android.graphics.Color;
import android.util.Pair;

import com.xiaohongshu.richedittextpro.R;
import com.xiaohongshu.richedittextpro.copy.richparser.base.AbstractRichParser;
import com.xiaohongshu.richedittextpro.copy.richparser.base.OnSpannableClickListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        //#\[位置\]\[[^#\[\]]+\][^#\[\]]+#
        return String.format("#\\[%s\\]\\[[^#\\[\\]]+\\][^#\\[\\]]+#", getType4Server());
    }

    @Override
    public String getType4Server() {
        return "音乐";
    }

    @Override
    public Pair<String, String> parseInfo4Server(String str) {
        String extraPattern = "[^#\\[\\]]+"; // [^#\[\]]+
        Pattern pattern = Pattern.compile(extraPattern);
        Matcher matcher = pattern.matcher(str);
        String[] infoArr = new String[3];
        int i = 0;
        while (matcher.find()) {
            infoArr[i++] = matcher.group();
        }
        return new Pair<>(infoArr[1], infoArr[2]);
    }
}
