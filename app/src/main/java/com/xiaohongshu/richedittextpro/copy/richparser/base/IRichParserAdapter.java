package com.xiaohongshu.richedittextpro.copy.richparser.base;

import android.content.Context;
import android.text.SpannableStringBuilder;

/**
 * Created by wupengjian on 17/1/17.
 */
public interface IRichParserAdapter {

    /**
     * @param str '#[音乐]有何不可#'
     * @return '#有何不可#'
     */
    SpannableStringBuilder parseStr2Spannable(Context context, String str);

    /**
     * @param str '#有何不可#'
     * @return '#[音乐]有何不可#'
     */
    String parseSpannable2Str(SpannableStringBuilder str);
}
