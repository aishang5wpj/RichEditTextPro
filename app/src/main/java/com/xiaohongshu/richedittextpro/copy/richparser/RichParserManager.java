package com.xiaohongshu.richedittextpro.copy.richparser;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Pair;

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
            if (parser4Server.containsRichStr4Server(str)) {
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
            if (parser4Local.containsRichSpannable(str)) {
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
        if (!containsRichStr(str)) {
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
            ssb.append(formatStr2Spannable(context, richStr));
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
        if (!containsRichSpannable(str)) {
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
            stringBuilder.append(formatSpannable2Str(richStr));
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
    private String getFirstRichItem4Str(String targetStr) {

        final String str = targetStr;
        int index = Integer.MAX_VALUE;
        Pair<Integer, String> result = null;
        for (IRichParser4Server richItem : mParserList) {

            Pair<Integer, String> temp = richItem.getFirstRichStr4Server(str);
            if (temp != null && temp.first < index && temp.first != -1) {
                index = temp.first;
                result = temp;
            }
        }
        return result == null ? "" : result.second;
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
        Pair<Integer, SpannableStringBuilder> result = null;
        for (IRichParser4Local richItem : mParserList) {

            Pair<Integer, SpannableStringBuilder> temp = richItem.getFirstRichSpannable(str);
            if (temp != null && temp.first < index && temp.first != -1) {
                index = temp.first;
                result = temp;
            }
        }
        return result == null ? new SpannableStringBuilder() : result.second;
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
        Pair<Integer, SpannableStringBuilder> result = null;
        for (IRichParser4Local richItem : mParserList) {

            Pair<Integer, SpannableStringBuilder> temp = richItem.getLastRichSpannable(str);
            if (temp != null && temp.first > index) {
                index = temp.first;
                result = temp;
            }
        }
        return result == null ? new SpannableStringBuilder() : result.second;
    }

    /**
     * @param context
     * @param richStr #[类型]内容#
     * @return #内容
     */
    private SpannableStringBuilder formatStr2Spannable(Context context, String richStr) {

        final String str = richStr;
        for (AbstractRichParser richItem : mParserList) {

            if (richItem.containsRichStr4Server(str)) {
                return richItem.parseStr2Spannable(context, richStr);
            }
        }
        return new SpannableStringBuilder(str);
    }

    /**
     * @param richStr #内容
     * @return #[类型]内容#
     */
    private String formatSpannable2Str(SpannableStringBuilder richStr) {

        final SpannableStringBuilder str = richStr;
        for (AbstractRichParser richItem : mParserList) {

            if (richItem.containsRichSpannable(str)) {
                return richItem.parseSpannable2Str(str);
            }
        }
        return "";
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
