package com.xiaohongshu.richedittextpro.copy.richparser.base;

import android.text.SpannableStringBuilder;
import android.util.Pair;

/**
 * Created by wupengjian on 17/1/17.
 */
public interface IRichParser4Local {

    boolean containsRichSpannable(SpannableStringBuilder ssb);

    Pair<Integer, SpannableStringBuilder> getFirstRichSpannable(SpannableStringBuilder ssb);

    Pair<Integer, SpannableStringBuilder>  getLastRichSpannable(SpannableStringBuilder ssb);
}
