package com.xiaohongshu.richedittextpro.copy.richparser.base;

import android.text.SpannableStringBuilder;

/**
 * Created by wupengjian on 17/1/17.
 */
public interface IRichParser4Local {

    Object[] parseFirstRichSpannable(SpannableStringBuilder ssb);

    Object[]  getLastRichSpannable(SpannableStringBuilder ssb);
}
