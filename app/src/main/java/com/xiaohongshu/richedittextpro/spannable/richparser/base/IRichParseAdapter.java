package com.xiaohongshu.richedittextpro.spannable.richparser.base;

/**
 * Created by wupengjian on 17/1/14.
 */
public interface IRichParseAdapter {

    /**
     * @param str '#[音乐]有何不可#'
     * @return '#有何不可#'
     */
    String parseStrServer2Local(String str);

    /**
     * @param str '#有何不可#'
     * @return '#[音乐]有何不可#'
     */
    String parseStrLocal2Server(String str);
}
