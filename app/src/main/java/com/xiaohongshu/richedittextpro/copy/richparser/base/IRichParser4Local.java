package com.xiaohongshu.richedittextpro.copy.richparser.base;

import android.text.SpannableStringBuilder;

/**
 * Created by wupengjian on 17/1/17.
 */
public interface IRichParser4Local {

    void setSpannable(SpannableStringBuilder ssb);

    boolean containsRichSpannable();

    SpannableStringBuilder getFirstRichSpannable();

    int getFirstIndex4RichSpannable();

    SpannableStringBuilder getLastRichSpannable();

    int getLastIndex4RichSpannable();
}
