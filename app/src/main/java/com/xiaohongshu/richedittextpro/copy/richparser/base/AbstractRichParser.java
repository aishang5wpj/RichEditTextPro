package com.xiaohongshu.richedittextpro.copy.richparser.base;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.View;

import com.xiaohongshu.richedittextpro.copy.VerticalImageSpan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wupengjian on 17/1/17.
 */
public abstract class AbstractRichParser implements IRichParser4Local, IRichParser4Server, IRichParserAdapter {

    private String mStr;
    private SpannableStringBuilder mSsb;
    private OnSpannableClickListener mOnClickListener;
    private List<RichItemBean> mTargetRichItems = new ArrayList<>();

    public AbstractRichParser() {
        this(null);
    }

    public AbstractRichParser(OnSpannableClickListener listener) {
        mOnClickListener = listener;
    }

    public AbstractRichParser setRichItems(List<RichItemBean> richItems) {
        mTargetRichItems.clear();
        mTargetRichItems.addAll(richItems);
        return this;
    }

    ////////////////////////////////////////////////////////// 下面是为Local提供的各种操作 ///////////////////////////////////////////////////////////////

    @Override
    public void setSpannable(SpannableStringBuilder ssb) {
        mSsb = ssb;
    }

    /**
     * 富文本中包含ImageSpan或者ColorSpan并不说明包含一个完整的富文本
     * ,有可能这个富文本被截断了(用户点击富文本时,光标落在富文本中间时的情况)
     *
     * @return
     */
    @Override
    public boolean containsRichSpannable() {

        ImageSpan[] imageSpen = mSsb.getSpans(0, mSsb.length(), ImageSpan.class);
        if (null != imageSpen && imageSpen.length > 0) {
            for (ImageSpan imageSpan : imageSpen) {

                int imageStart = mSsb.getSpanStart(imageSpan);
                ForegroundColorSpan[] colorSpen = mSsb.getSpans(imageStart, mSsb.length(), ForegroundColorSpan.class);
                if (colorSpen != null && colorSpen.length > 0) {
                    for (ForegroundColorSpan colorSpan : colorSpen) {

                        //从ColorSpan中解析出来的内容信息
                        int colorStart = mSsb.getSpanStart(colorSpan);
                        int colorEnd = mSsb.getSpanEnd(colorSpan);
                        CharSequence charSequence = mSsb.subSequence(colorStart, colorEnd);
                        //ImageSpan中包含的content信息
                        String source = imageSpan.getSource();
                        RichItemBean itemBean = RichItemBean.parseRichItem(source);
                        String richStr = "#" + itemBean.getContent();
                        //如果两个信息相等,说明这是一个完整的富文本
                        if (TextUtils.equals(richStr, charSequence)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 可能拿到不完整的富文本!!
     * 用ImageSpan中的source转成RichItemBean,来判断是不完整的富文本
     *
     * @return
     */
    @Override
    public SpannableStringBuilder getFirstRichSpannable() {
        ImageSpan[] imageSpen = mSsb.getSpans(0, mSsb.length(), ImageSpan.class);
        if (null != imageSpen && imageSpen.length > 0) {
            for (ImageSpan imageSpan : imageSpen) {

                int imageStart = mSsb.getSpanStart(imageSpan);
                ForegroundColorSpan[] colorSpen = mSsb.getSpans(imageStart, mSsb.length(), ForegroundColorSpan.class);
                if (colorSpen != null && colorSpen.length > 0) {
                    for (ForegroundColorSpan colorSpan : colorSpen) {

                        //从ColorSpan中解析出来的内容信息
                        int colorStart = mSsb.getSpanStart(colorSpan);
                        int colorEnd = mSsb.getSpanEnd(colorSpan);
                        CharSequence charSequence = mSsb.subSequence(colorStart, colorEnd);
                        //ImageSpan中包含的content信息
                        String source = imageSpan.getSource();
                        RichItemBean itemBean = RichItemBean.parseRichItem(source);
                        String richStr = "#" + itemBean.getContent();
                        //如果两个信息相等,说明这是一个完整的富文本
                        if (TextUtils.equals(richStr, charSequence)) {
                            return (SpannableStringBuilder) charSequence;
                        }
                    }
                }
            }
        }
        return new SpannableStringBuilder();
    }

    @Override
    public int getFirstIndex4RichSpannable() {
        return mSsb.toString().indexOf(getFirstRichSpannable().toString());
    }

    /**
     * 可能拿到不完整的富文本!!
     * 用ImageSpan中的source转成RichItemBean,来判断是不完整的富文本
     *
     * @return
     */
    @Override
    public SpannableStringBuilder getLastRichSpannable() {
        ImageSpan[] imageSpen = mSsb.getSpans(0, mSsb.length(), ImageSpan.class);
        if (null != imageSpen && imageSpen.length > 0) {
            for (int i = imageSpen.length - 1; i >= 0; i--) {

                int imageStart = mSsb.getSpanStart(imageSpen[i]);
                ForegroundColorSpan[] colorSpen = mSsb.getSpans(imageStart, mSsb.length(), ForegroundColorSpan.class);
                if (colorSpen != null && colorSpen.length > 0) {
                    for (ForegroundColorSpan colorSpan : colorSpen) {

                        //从ColorSpan中解析出来的内容信息
                        int colorStart = mSsb.getSpanStart(colorSpan);
                        int colorEnd = mSsb.getSpanEnd(colorSpan);
                        CharSequence charSequence = mSsb.subSequence(colorStart, colorEnd);
                        //ImageSpan中包含的content信息
                        String source = imageSpen[i].getSource();
                        RichItemBean itemBean = RichItemBean.parseRichItem(source);
                        String richStr = "#" + itemBean.getContent();
                        //如果两个信息相等,说明这是一个完整的富文本
                        if (TextUtils.equals(richStr, charSequence)) {
                            return (SpannableStringBuilder) charSequence;
                        }
                    }
                }
            }
        }
        return new SpannableStringBuilder();
    }

    @Override
    public int getLastIndex4RichSpannable() {
        return mSsb.toString().indexOf(getLastRichSpannable().toString());
    }

    ////////////////////////////////////////////////////////// 下面是为Server提供的各种操作 ///////////////////////////////////////////////////////////////

    @Override
    public void setString(String str) {
        mStr = str;
    }

    /**
     * 除了匹配正则表达式,而且还要满足匹配到的富文本在服务器的富文本列表中
     * ,如果匹配到的富文本在服务器中不存在,则不允许高亮
     *
     * @return
     */
    @Override
    public boolean containsRichStr4Server() {
        Pattern pattern = Pattern.compile(getPattern4Server());
        Matcher matcher = pattern.matcher(mStr);
        while (matcher.find()) {

            String richStr = matcher.group();
            String type = getType4Server();
            String content = getContent4Server(richStr);

            RichItemBean richItemBean = RichItemBean.createRichItem(type, content);
            if (mTargetRichItems.contains(richItemBean)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getFirstRichStr4Server() {
        Pattern pattern = Pattern.compile(getPattern4Server());
        Matcher matcher = pattern.matcher(mStr);
        while (matcher.find()) {

            String richStr = matcher.group();
            String type = getType4Server();
            String content = getContent4Server(richStr);

            RichItemBean richItemBean = RichItemBean.createRichItem(type, content);
            if (mTargetRichItems.contains(richItemBean)) {
                return richStr;
            }
        }
        return "";
    }

    @Override
    public int getFirstRichStrIndex4Server() {
        String str = getFirstRichStr4Server();
        return TextUtils.isEmpty(str) ? -1 : mStr.indexOf(str);
    }

    @Override
    public String getContent4Server(String str) {
        //判断是否有类型
        int end = TextUtils.isEmpty(getType4Server()) ? str.length() - 1 : str.indexOf('[');
        return str.substring(1, end);
    }

    protected abstract int getColor();

    protected abstract int getDrawableId();

    ////////////////////////////////////////////////////////// 下面是为Local和Server之间提供适配的各种操作 ///////////////////////////////////////////////////////////////

    /**
     * @param context
     * @param string  #[类型]内容#
     * @return #内容
     */
    @Override
    public SpannableStringBuilder parseStr2Spannable(Context context, final String string) {

        final String type = getType4Server();
        final String content = getContent4Server(string);

        final String str = String.format("#%s", content);

        SpannableStringBuilder spannableStr = new SpannableStringBuilder(str);

        int drawableId = getDrawableId();
        if (drawableId != 0) {
            RichItemBean itemBean = RichItemBean.createRichItem(type, getContent4Server(string));
            String source = itemBean.toString();

            Drawable drawable = getDrawable(context, drawableId);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            ImageSpan imageSpan = new VerticalImageSpan(drawable, source, ImageSpan.ALIGN_BOTTOM);
            spannableStr.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getColor());
        spannableStr.setSpan(colorSpan, 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {

                if (null != mOnClickListener) {
                    mOnClickListener.onClick(AbstractRichParser.this, type, content, string);
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        spannableStr.setSpan(clickableSpan, 0, spannableStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableStr;
    }

    @Override
    public String parseSpannable2Str(SpannableStringBuilder str) {
        ImageSpan[] imageSpen = str.getSpans(0, str.length(), ImageSpan.class);
        RichItemBean itemBean = RichItemBean.parseRichItem(imageSpen[0].getSource());
        String type = itemBean.getType();
        String content = itemBean.getContent();
        if (TextUtils.isEmpty(type)) {

            return String.format("#%s#", content);
        }
        return String.format("#%s[%s]#", content, type);
    }

    private Drawable getDrawable(Context context, int drawableId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getDrawable(drawableId);
        }
        return context.getResources().getDrawable(drawableId);
    }
}
