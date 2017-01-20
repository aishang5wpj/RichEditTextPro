package com.xiaohongshu.richedittextpro.copy.richparser;

import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import com.xiaohongshu.richedittextpro.copy.richparser.base.AbstractRichParser;
import com.xiaohongshu.richedittextpro.copy.richparser.base.IRichParser4Local;
import com.xiaohongshu.richedittextpro.copy.richparser.base.IRichParser4Server;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wupengjian on 17/1/17.
 */
public class RichParserManager {

    private List<AbstractRichParser> mParserList;

    private RichParserManager() {
        mParserList = new ArrayList<>();
    }

    public static final RichParserManager getManager() {
        return LazyHolder.INSTANCE;
    }

    /**
     * 判断是否包含富文本
     *
     * @param str
     * @return
     */
    public boolean containsRichStr(String str) {
        for (IRichParser4Server parser4Server : mParserList) {
            parser4Server.setString(str);
            if (parser4Server.containsRichStr4Server()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否包含富文本
     *
     * @param str
     * @return
     */
    public boolean containsRichSpannable(SpannableStringBuilder str) {
        for (IRichParser4Local parser4Local : mParserList) {
            parser4Local.setSpannable(str);
            if (parser4Local.containsRichSpannable()) {
                return true;
            }
        }
        return false;
    }

    /**
     * String -> SpannableStringBuilder
     *
     * @param targetStr
     * @return
     */
    public SpannableStringBuilder parseStr2Spannable(Context context, String targetStr) {
        final String str = targetStr;
        if (!RichParserManager.getManager().containsRichStr(str)) {
            return new SpannableStringBuilder(str);
        }
        String tempStr = str;
        String richStr = getFirstRichItem4Str(tempStr);
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        while (!TextUtils.isEmpty(richStr)) {

            //start string
            int index = tempStr.indexOf(richStr);
            String startStr = tempStr.substring(0, index);
            ssb.append(startStr);
            //rich string
            ssb.append(formateStr2Spannable(context, richStr));
            //循环
            tempStr = tempStr.substring(index + richStr.length(), tempStr.length());
            richStr = getFirstRichItem4Str(tempStr);
        }
        //end String
        ssb.append(tempStr);

        return ssb;
    }


    /**
     * SpannableStringBuilder -> String
     *
     * @param spannableStringBuilder
     * @return
     */
    public String parseSpannable2Str(SpannableStringBuilder spannableStringBuilder) {
        final SpannableStringBuilder str = spannableStringBuilder;
        if (!RichParserManager.getManager().containsRichSpannable(str)) {
            return "";
        }
        SpannableStringBuilder tempStr = str;
        SpannableStringBuilder richStr = getFirstRichItem4Spannable(tempStr);
        StringBuilder stringBuilder = new StringBuilder();
        while (!TextUtils.isEmpty(richStr)) {

            //start string
            int index = tempStr.toString().indexOf(richStr.toString());
            String startStr = tempStr.subSequence(0, index).toString();
            stringBuilder.append(startStr);
            //rich string
            stringBuilder.append(formateSpannable2Str(richStr));
            //循环
            tempStr = (SpannableStringBuilder) tempStr.subSequence(index + richStr.length(), tempStr.length());
            richStr = getFirstRichItem4Spannable(tempStr);
        }
        //end String
        stringBuilder.append(tempStr);

        return stringBuilder.toString();
    }

    /**
     * 从String中取出第一个富文本
     *
     * @param targetStr
     * @return
     */
    public String getFirstRichItem4Str(String targetStr) {

        final String str = targetStr;
        int index = Integer.MAX_VALUE;
        IRichParser4Server iRichParser = null;
        for (IRichParser4Server richItem : mParserList) {

            //遍历mRichItems进行各种操作时,一定要重置targetStr
            richItem.setString(str);

            int temp = richItem.getFirstRichStrIndex4Server();
            if (temp < index && temp != -1) {
                index = temp;
                iRichParser = richItem;
            }
        }
        return iRichParser == null ? "" : iRichParser.getFirstRichStr4Server();
    }

    /**
     * 从String中取出第一个富文本
     *
     * @param ssb
     * @return
     */
    public SpannableStringBuilder getFirstRichItem4Spannable(SpannableStringBuilder ssb) {

        final SpannableStringBuilder str = ssb;
        int index = Integer.MAX_VALUE;
        IRichParser4Local iRichParser = null;
        for (IRichParser4Local richItem : mParserList) {

            //遍历mRichItems进行各种操作时,一定要重置targetStr
            richItem.setSpannable(str);

            int temp = richItem.getFirstIndex4RichSpannable();
            if (temp < index && temp != -1) {
                index = temp;
                iRichParser = richItem;
            }
        }
        return iRichParser == null ? new SpannableStringBuilder() : iRichParser.getFirstRichSpannable();
    }

    /**
     * 从String中取出第一个富文本
     *
     * @param ssb
     * @return
     */
    public SpannableStringBuilder getLastRichItem4Spannable(SpannableStringBuilder ssb) {

        final SpannableStringBuilder str = ssb;
        int index = -1;
        IRichParser4Local iRichParser = null;
        for (IRichParser4Local richItem : mParserList) {

            //遍历mRichItems进行各种操作时,一定要重置targetStr
            richItem.setSpannable(str);

            int temp = richItem.getLastIndex4RichSpannable();
            if (temp > index) {
                index = temp;
                iRichParser = richItem;
            }
        }
        return iRichParser == null ? new SpannableStringBuilder() : iRichParser.getLastRichSpannable();
    }

    /**
     * @param context
     * @param richStr #[类型]内容#
     * @return #内容
     */
    private SpannableStringBuilder formateStr2Spannable(Context context, String richStr) {

        final String str = richStr;
        for (AbstractRichParser richItem : mParserList) {

            //遍历mRichItems进行各种操作时,一定要重置targetStr
            richItem.setString(richStr);

            if (richItem.containsRichStr4Server()) {
                return richItem.parseStr2Spannable(context, richStr);
            }
        }
        return new SpannableStringBuilder(str);
    }

    /**
     * @param richStr #内容
     * @return #[类型]内容#
     */
    private String formateSpannable2Str(SpannableStringBuilder richStr) {

        final SpannableStringBuilder str = richStr;
        for (AbstractRichParser richItem : mParserList) {

            //遍历mRichItems进行各种操作时,一定要重置targetStr
            richItem.setSpannable(richStr);

            if (richItem.containsRichSpannable()) {
                return richItem.parseSpannable2Str(richStr);
            }
        }
        return "";
    }

    public boolean isStartWithRichSpannable(SpannableStringBuilder ssb) {

        SpannableStringBuilder spannableStr = getFirstRichItem4Spannable(ssb);
        return spannableStr.toString().startsWith(spannableStr.toString());
    }

    public boolean isEndWithRichSpannable(SpannableStringBuilder ssb) {

        SpannableStringBuilder spannableStr = getLastRichItem4Spannable(ssb);
        return spannableStr.toString().endsWith(spannableStr.toString());
    }

    public void registerParser(AbstractRichParser parser) {
        //相同类型的解析器避免重复添加
        for (AbstractRichParser baseParser : mParserList) {
            if (parser.getClass().isAssignableFrom(baseParser.getClass())) {
                return;
            }
        }
        mParserList.add(parser);
    }

    public void unregisterParser(AbstractRichParser parser) {
        mParserList.remove(parser);
    }

    public void clearParser() {
        mParserList.clear();
    }

    private static final class LazyHolder {
        private static final RichParserManager INSTANCE = new RichParserManager();
    }
}
