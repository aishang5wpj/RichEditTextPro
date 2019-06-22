package com.xiaohongshu.richedittextpro.copy.richparser;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.util.Pair;

import com.xiaohongshu.richedittextpro.copy.richparser.base.AbstractRichParser;
import com.xiaohongshu.richedittextpro.copy.richparser.base.IRichParser4Local;

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
        if (firstRichSpan == null) {
            return "";
        }
        SpannableStringBuilder tempStr = str;
        StringBuilder stringBuilder = new StringBuilder();
        while (firstRichSpan != null) {

            //start string
            int index = (int) firstRichSpan[2];
            String startStr = tempStr.subSequence(0, index).toString();
            stringBuilder.append(startStr);
            //rich string
            stringBuilder.append(firstRichSpan[0]);
            //循环
            int lenght = (int) firstRichSpan[3];
            tempStr = (SpannableStringBuilder) tempStr.subSequence(index + lenght, tempStr.length());
            firstRichSpan = getFirstRichItem4Spannable(tempStr);
        }
        //end String
        stringBuilder.append(tempStr);

        return stringBuilder.toString();
    }

    private Object[] getFirstRichItem4Str(String targetStr) {

        final String str = targetStr;
        int index = Integer.MAX_VALUE;
        Object[] result = new Object[3];
        for (AbstractRichParser richParser : mParserList) {

            Pair<Integer, String> temp = richParser.getFirstRichStr4Server(str);
            if (temp != null && temp.first < index && temp.first != -1) {
                index = temp.first;
                result[0] = temp.first;
                result[1] = temp.second;
                result[2] = richParser;
            }
        }
        return result;
    }

    public Object[] getFirstRichItem4Spannable(SpannableStringBuilder ssb) {
        Object[] result = null;
        for (AbstractRichParser richParser : mParserList) {
            Object[] temp = richParser.parseFirstRichSpannable(ssb);
            if (temp == null) {
                continue;
            }
            if (result == null) {
                result = temp;
            } else {
                int index = (int) result[3];
                int tempIndex = (int) temp[3];
                if (tempIndex <= index) {
                    result = temp;
                }
            }
        }
        return result;
    }

    public SpannableStringBuilder getLastRichItem4Spannable(SpannableStringBuilder ssb) {

        final SpannableStringBuilder str = ssb;
        Object[] result = null;
        for (IRichParser4Local richParser : mParserList) {

            Object[] temp = richParser.getLastRichSpannable(str);
            if (temp != null) {
                result = temp;
                break;
            }
        }
        return result == null ? new SpannableStringBuilder() : (SpannableStringBuilder) result[1];
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
