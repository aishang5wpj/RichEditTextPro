package com.xiaohongshu.richedittextpro.copy.richparser.base;

import android.text.TextUtils;

/**
 * Created by wupengjian on 17/1/18.
 */
public class RichItemBean {

    private static final String FLAG = "::";
    private String mType;
    private String mContent;
    private String mExtra;

    private RichItemBean() {
        this("", "", "");
    }

    private RichItemBean(String type, String extra, String content) {
        mType = type;
        mContent = content;
        mExtra = extra;
    }

    public static RichItemBean createRichItem(String type, String extra, String content) {
        RichItemBean itemBean = new RichItemBean(type, content, extra);
        return itemBean;
    }

    public static RichItemBean parseRichItem(String source) {
        final String str = source;
        RichItemBean itemBean = new RichItemBean();
        if (TextUtils.isEmpty(str) || !str.contains(FLAG)) {
            return itemBean;
        }
        String[] strArr = str.split(FLAG);
        itemBean.mType = strArr[0];
        itemBean.mExtra = strArr[1];
        itemBean.mContent = strArr[2];
        return itemBean;
    }

    public String getType() {
        return mType;
    }

    public String getContent() {
        return mContent;
    }

    public String getExtra() {
        return mExtra;
    }

    @Override
    public String toString() {
        return String.format("%s%s%s%s%s", mType, FLAG, mExtra, FLAG, mContent);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RichItemBean) {
            RichItemBean itemBean = (RichItemBean) o;
            return TextUtils.equals(mType, itemBean.getType())
                    && TextUtils.equals(mContent, itemBean.getContent())
                    && TextUtils.equals(mExtra, itemBean.getExtra());
        }
        return super.equals(o);
    }
}
