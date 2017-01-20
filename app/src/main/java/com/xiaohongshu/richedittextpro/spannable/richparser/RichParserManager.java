package com.xiaohongshu.richedittextpro.spannable.richparser;

import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import com.xiaohongshu.richedittextpro.spannable.richparser.base.AbstractRichParser;
import com.xiaohongshu.richedittextpro.spannable.richparser.base.IRichPaser4Local;
import com.xiaohongshu.richedittextpro.spannable.richparser.base.IRichPaser4Server;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wupengjian on 17/1/12.
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
        for (IRichPaser4Local paser4Local : mParserList) {
            paser4Local.setString4Local(str);
            if (paser4Local.containsRichStr4Local()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将 服务器返回的 一长串富文本的字符串转换成 本地可解析 的富文本字符串
     *
     * @param string
     * @return
     */
    public String parseStr4Local(final String string) {

        StringBuilder result = new StringBuilder();

        String restStr = string;
        String richStr = getFirstRichStr4Server(restStr);
        while (!TextUtils.isEmpty(richStr)) {
            for (AbstractRichParser baseParser : mParserList) {
                IRichPaser4Server paser4Server = baseParser;
                paser4Server.setStr4Server(richStr);
                if (paser4Server.containsRichStr4Server()) {
                    //截前
                    int index = restStr.indexOf(richStr);
                    String startStr = restStr.substring(0, index);
                    //加中间
                    String richStr4Local = baseParser.parseStrServer2Local(richStr);
                    result.append(startStr + richStr4Local);
                    //取后
                    restStr = restStr.substring(index + richStr.length(), restStr.length());
                    richStr = getFirstRichStr4Server(restStr);
                    //停止循环
                    break;
                }
            }
        }
        if (!TextUtils.isEmpty(restStr)) {
            result.append(restStr);
        }
        return result.toString();
    }

    /**
     * 将 本地编辑的 一长串富文本的字符串转换成 服务器可解析 的富文本字符串
     *
     * @param string
     * @return
     */
    public String parseStr4Server(String string) {

        StringBuilder result = new StringBuilder();

        String restStr = string;
        String richStr = getFirstRichStr4Local(restStr);
        while (!TextUtils.isEmpty(richStr)) {
            for (AbstractRichParser baseParser : mParserList) {
                IRichPaser4Local paser4Local = baseParser;
                paser4Local.setString4Local(richStr);
                if (paser4Local.containsRichStr4Local()) {
                    //截前
                    int index = restStr.indexOf(richStr);
                    String startStr = restStr.substring(0, index);
                    //加中间
                    String richStr4Local = baseParser.parseStrLocal2Server(richStr);
                    result.append(startStr + richStr4Local);
                    //取后
                    restStr = restStr.substring(index + richStr.length(), restStr.length());
                    richStr = getFirstRichStr4Local(restStr);
                    //停止循环
                    break;
                }
            }
        }
        if (!TextUtils.isEmpty(restStr)) {
            result.append(restStr);
        }
        return result.toString();
    }

    /**
     * 解析字符串中的富文本并返回一个经过格式化的富文本串
     *
     * @param targetStr
     * @return
     */
    public SpannableStringBuilder parseRichItems(Context context, String targetStr) {
        final String str = targetStr;
        if (!RichParserManager.getManager().containsRichStr(str)) {
            return new SpannableStringBuilder(str);
        }
        String tempStr = str;
        String richStr = getFirstRichStr4Local(tempStr);
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        while (!TextUtils.isEmpty(richStr)) {

            //start string
            int index = tempStr.indexOf(richStr);
            String startStr = tempStr.substring(0, index);
            ssb.append(startStr);
            //rich string
            ssb.append(formateRichStr(context, richStr));
            //循环
            tempStr = tempStr.substring(index + richStr.length(), tempStr.length());
            richStr = getFirstRichStr4Local(tempStr);
        }
        //end String
        ssb.append(tempStr);

        return ssb;
    }

    /**
     * 获取字符串中的最后一个富文本串
     *
     * @param targetStr
     * @return 最后一个"话题"或者最后一个"@"或者其他,如果没有富文本串,则返回空字符串("")
     */
    public String getFirstRichStr4Server(String targetStr) {

        final String str = targetStr;
        int index = Integer.MAX_VALUE;
        IRichPaser4Server iRichParser = null;
        for (IRichPaser4Server richItem : mParserList) {

            //遍历mRichItems进行各种操作时,一定要重置targetStr
            richItem.setStr4Server(str);

            int temp = richItem.getFirstRichStrIndex4Server();
            if (temp < index && temp != -1) {
                index = temp;
                iRichParser = richItem;
            }
        }
        return iRichParser == null ? "" : iRichParser.getFirstRichStr4Server();
    }

    /**
     * 获取字符串中的最后一个富文本串
     *
     * @param targetStr
     * @return 最后一个"话题"或者最后一个"@"或者其他,如果没有富文本串,则返回空字符串("")
     */
    public String getFirstRichStr4Local(String targetStr) {

        final String str = targetStr;
        int index = Integer.MAX_VALUE;
        IRichPaser4Local iRichParser = null;
        for (IRichPaser4Local richItem : mParserList) {

            //遍历mRichItems进行各种操作时,一定要重置targetStr
            richItem.setString4Local(str);

            int temp = richItem.getFirstRichStrIndex4Local();
            if (temp < index && temp != -1) {
                index = temp;
                iRichParser = richItem;
            }
        }
        return iRichParser == null ? "" : iRichParser.getFirstRichStr4Local();
    }

    /**
     * 获取字符串中的最后一个富文本串
     *
     * @param targetStr
     * @return 最后一个"话题"或者最后一个"@"或者其他,如果没有富文本串,则返回空字符串("")
     */
    public String getLastRichStr4Local(String targetStr) {

        final String str = targetStr;
        int index = -1;
        IRichPaser4Local iRichParser = null;
        for (IRichPaser4Local richItem : mParserList) {

            //遍历mRichItems进行各种操作时,一定要重置targetStr
            richItem.setString4Local(str);

            int temp = richItem.getLastRichStrIndex4Local();
            if (temp > index) {
                index = temp;
                iRichParser = richItem;
            }
        }
        return iRichParser == null ? "" : iRichParser.getLastRichStr4Local();
    }

    /**
     * 是否以富文本开头
     *
     * @param targetStr
     * @return
     */
    public boolean isStartWithRichItem(String targetStr) {

        final String str = targetStr;
        if (!RichParserManager.getManager().containsRichStr(str)) {
            return false;
        }
        String firstTopic = RichParserManager.getManager().getFirstRichStr4Local(str);
        return str.startsWith(firstTopic);
    }

    public boolean isEndWithRichItem(String targetStr) {

        final String str = targetStr;
        if (!RichParserManager.getManager().containsRichStr(str)) {
            return false;
        }
        String lastTopic = RichParserManager.getManager().getLastRichStr4Local(str);
        return str.endsWith(lastTopic);
    }

    private SpannableString formateRichStr(Context context, String richStr) {

        final String str = richStr;
        for (IRichPaser4Local richItem : mParserList) {

            //遍历mRichItems进行各种操作时,一定要重置targetStr
            richItem.setString4Local(richStr);

            if (richItem.containsRichStr4Local()) {
                return richItem.parse2SpannableStr(context, richStr);
            }
        }
        return new SpannableString(str);
    }

    public void registerParser(AbstractRichParser parser) {
        //相同类型的解析器避免重复添加
        for (AbstractRichParser baseParser : mParserList) {
            if (parser.getClass().isAssignableFrom(baseParser.getClass())) {
                return;
            }
        }
        //#话题# 的解析要放在 #[类型]话题# 的前面,否则将 #[类型]话题# 转成  #话题# 之后,后面解析
        // #话题# 时又会将 #[类型]话题# 当成  #话题# 解析了
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
