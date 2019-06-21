package com.xiaohongshu.richedittextpro.copy.richparser.base;

import android.util.Pair;

/**
 * Created by wupengjian on 17/1/17.
 */
public interface IRichParser4Server {

    String getPattern4Server();

    Pair<Integer, String> getFirstRichStr4Server(String str);

    String getType4Server();

    Pair<String, String> parseInfo4Server(String str);
}
