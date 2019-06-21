package com.xiaohongshu.richedittextpro.copy.richparser.base;

import android.util.Pair;

/**
 * Created by wupengjian on 17/1/17.
 */
public interface OnSpannableClickListener {

    void onClick(AbstractRichParser parser, String type, Pair<String, String> content, String sourceStr);
}
