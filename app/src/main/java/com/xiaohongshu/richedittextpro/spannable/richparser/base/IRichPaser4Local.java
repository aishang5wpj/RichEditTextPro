package com.xiaohongshu.richedittextpro.spannable.richparser.base;

import android.content.Context;
import android.text.SpannableString;

/**
 * Created by wupengjian on 17/1/12.
 */
public interface IRichPaser4Local {

    /**
     * 输入框中的str
     *
     * @param str
     */
    void setString4Local(String str);

    /**
     * 获取正则表达式
     *
     * @return
     */
    String getRichPattern4Local();

    /**
     * 从富文本中解析出来内容
     *
     * @param str '#有何不可 '
     * @return '有何不可'
     */
    String getContentFromRichStr4Local(String str);

    /**
     * String中是否包含可以转成Spannable的内容
     *
     * @return
     */
    boolean containsRichStr4Local();

    /**
     * 获取String中第一个可以转成Spannable的内容
     *
     * @return
     */
    String getFirstRichStr4Local();

    /**
     * 获取String中第一个可以转成Spannable的内容的索引
     *
     * @return
     */
    int getFirstRichStrIndex4Local();

    /**
     * 获取String中最后一个可以转成Spannable的内容
     *
     * @return
     */
    String getLastRichStr4Local();

    /**
     * 获取String中最后一个可以转成Spannable的内容的索引
     *
     * @return
     */
    int getLastRichStrIndex4Local();

    /**
     * 转成富文本
     *
     * @param str
     * @return
     */
    SpannableString parse2SpannableStr(Context context, String str);
}
