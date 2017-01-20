package com.xiaohongshu.richedittextpro.spannable.richparser.base;

/**
 * Created by wupengjian on 17/1/12.
 */
public interface IRichPaser4Server {

    /**
     * 服务器返回的str
     *
     * @param str
     */
    void setStr4Server(String str);

    /**
     * 获取正则表达式
     *
     * @return
     */
    String getRichPattern4Server();

    /**
     * 话题、音乐、电影、商品等等。。。。
     *
     * @return
     */
    String getType4Server();

    /**
     * 从富文本中解析出来内容
     *
     * @param str
     * @return
     */
    String getContentFromRichStr4Server(String str);

    /**
     * 是否包含Spannable
     *
     * @return
     */
    boolean containsRichStr4Server();

    /**
     * 获取第一个Spannable
     *
     * @return
     */
    String getFirstRichStr4Server();

    /**
     * 获取第一个Spannable的索引
     *
     * @return
     */
    int getFirstRichStrIndex4Server();
}
