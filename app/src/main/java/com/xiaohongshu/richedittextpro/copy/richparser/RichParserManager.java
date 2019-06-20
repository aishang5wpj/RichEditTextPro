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
     * String -> SpannableStringBuilder
     *
     * @param targetStr
     * @return
     */
    public SpannableStringBuilder parseStr2Spannable(Context context, String targetStr) {
        final String str = targetStr;
        Object[] firstRichSpan = getFirstRichItem4Str(str);
        if (firstRichSpan == null) {
            return new SpannableStringBuilder(str);
        }
        String tempStr = str;
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        while (firstRichSpan[0] != null) {
            //start string
            int index = (int) firstRichSpan[0];
            String startStr = tempStr.substring(0, index);
            ssb.append(startStr);
            //rich string
            String richStr = (String) firstRichSpan[1];
            AbstractRichParser richParser = (AbstractRichParser) firstRichSpan[2];
            ssb.append(richParser.parseStr2Spannable(context, richStr));
            //循环
            tempStr = tempStr.substring(index + richStr.length());
            firstRichSpan = getFirstRichItem4Str(tempStr);
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
        Object[] firstRichSpan = getFirstRichItem4Spannable(str);
        if (firstRichSpan[0] == null) {
            return "";
        }
        SpannableStringBuilder tempStr = str;
        StringBuilder stringBuilder = new StringBuilder();
        while (firstRichSpan[0] != null) {

            //start string
            int index = (int) firstRichSpan[0];
            String startStr = tempStr.subSequence(0, index).toString();
            stringBuilder.append(startStr);
            //rich string
            SpannableStringBuilder richStr = (SpannableStringBuilder) firstRichSpan[1];
            AbstractRichParser richParser = (AbstractRichParser) firstRichSpan[2];
            stringBuilder.append(richParser.parseSpannable2Str(richStr));
            //循环
            tempStr = (SpannableStringBuilder) tempStr.subSequence(index + richStr.length(), tempStr.length());
            firstRichSpan = getFirstRichItem4Spannable(tempStr);
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
    private Object[] getFirstRichItem4Str(String targetStr) {

        final String str = targetStr;
        int index = Integer.MAX_VALUE;
        Object[] result = new Object[3];
        for (AbstractRichParser richPaser : mParserList) {

            Pair<Integer, String> temp = richPaser.getFirstRichStr4Server(str);
            if (temp != null && temp.first < index && temp.first != -1) {
                index = temp.first;
                result[0] = temp.first;
                result[1] = temp.second;
                result[2] = richPaser;
            }
        }
        return result;
    }

    /**
     * 从String中取出第一个富文本
     *
     * @param ssb
     * @return
     */
    public Object[] getFirstRichItem4Spannable(SpannableStringBuilder ssb) {

        final SpannableStringBuilder str = ssb;
        int index = Integer.MAX_VALUE;
        Object[] result = new Object[3];
        for (AbstractRichParser richParser : mParserList) {

            Pair<Integer, SpannableStringBuilder> temp = richParser.getFirstRichSpannable(str);
            if (temp != null && temp.first < index && temp.first != -1) {
                index = temp.first;
                result[0] = temp.first;
                result[1] = temp.second;
                result[2] = richParser;
            }
        }
        return result;
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
        for (IRichParser4Local richParser : mParserList) {

            Pair<Integer, SpannableStringBuilder> temp = richParser.getLastRichSpannable(str);
            if (temp != null && temp.first > index) {
                index = temp.first;
                result = temp;
            }
        }
        return result == null ? new SpannableStringBuilder() : result.second;
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
