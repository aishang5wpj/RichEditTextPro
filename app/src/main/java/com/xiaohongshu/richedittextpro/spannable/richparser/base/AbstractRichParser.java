package com.xiaohongshu.richedittextpro.spannable.richparser.base;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.View;

import com.xiaohongshu.richedittextpro.R;
import com.xiaohongshu.richedittextpro.spannable.VerticalImageSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wupengjian on 17/1/12.
 */
public abstract class AbstractRichParser implements IRichPaser4Local, IRichPaser4Server, IRichParseAdapter {

    protected String mStr4Local, mStr4Server;
    protected OnSpannableClickListener mOnClickListener;

    public AbstractRichParser() {
        this(null);
    }

    public AbstractRichParser(OnSpannableClickListener listener) {
        mOnClickListener = listener;
    }

    protected abstract String getFlag();

    ////////////////////////////////////////////////////////// 下面是为Local提供的各种操作 ///////////////////////////////////////////////////////////////

    @Override
    public void setString4Local(String str) {
        mStr4Local = new String(str);
    }

    @Override
    public String getContentFromRichStr4Local(String str) {

        //掐头去尾,不要前面的#和后面的#
        int start = str.indexOf(getFlag());
        return str.substring(start + getFlag().length(), str.length() - 1);
    }

    @Override
    public boolean containsRichStr4Local() {

        Pattern pattern = Pattern.compile(getRichPattern4Local());
        Matcher matcher = pattern.matcher(mStr4Local);
        return matcher.find();
    }

    @Override
    public String getFirstRichStr4Local() {
        final String str = mStr4Local;
        if (containsRichStr4Local()) {
            Pattern pattern = Pattern.compile(getRichPattern4Local());
            Matcher matcher = pattern.matcher(str);
            if (matcher.find()) {
                return matcher.group();
            }
        }
        return "";
    }

    @Override
    public int getFirstRichStrIndex4Local() {
        if (containsRichStr4Local()) {
            return mStr4Local.indexOf(getFirstRichStr4Local());
        }
        return -1;
    }

    @Override
    public String getLastRichStr4Local() {
        String richStr = "";
        if (containsRichStr4Local()) {
            Pattern pattern = Pattern.compile(getRichPattern4Local());
            Matcher matcher = pattern.matcher(mStr4Local);
            while (matcher.find()) {
                richStr = matcher.group();
            }
        }
        return richStr;
    }

    @Override
    public int getLastRichStrIndex4Local() {
        if (containsRichStr4Local()) {
            return mStr4Local.indexOf(getLastRichStr4Local());
        }
        return -1;
    }

    @Override
    public SpannableString parse2SpannableStr(final Context context, String string) {

        final String str = string;
        SpannableString spannableStr = new SpannableString(str);

        int drawableId = getDrawableId();
        if (drawableId != 0) {
            Drawable drawable = getDrawable(context, drawableId);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            ImageSpan imageSpan = new VerticalImageSpan(drawable, getType4Server(), ImageSpan.ALIGN_BOTTOM);
            spannableStr.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        //覆盖最后的hashtag
        ImageSpan imageSpan = new VerticalImageSpan(context, R.mipmap.transparent);
        spannableStr.setSpan(imageSpan, str.length() - getFlag().length(), str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getColor());
        spannableStr.setSpan(colorSpan, 1, str.length() - getFlag().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {

                if (null != mOnClickListener) {
                    String content = getContentFromRichStr4Local(str);
                    String sourceStr = parseStrLocal2Server(str);
                    mOnClickListener.onClick(getType4Server(), content, sourceStr);
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

    protected abstract int getColor();

    protected abstract int getDrawableId();

    ////////////////////////////////////////////////////////// 下面是为Server提供的各种操作 ///////////////////////////////////////////////////////////////

    @Override
    public void setStr4Server(String str) {
        mStr4Server = new String(str);
    }

    @Override
    public boolean containsRichStr4Server() {

        Pattern pattern = Pattern.compile(getRichPattern4Server());
        Matcher matcher = pattern.matcher(mStr4Server);
        return matcher.find();
    }

    @Override
    public String getFirstRichStr4Server() {
        final String str = mStr4Server;
        if (containsRichStr4Server()) {
            Pattern pattern = Pattern.compile(getRichPattern4Server());
            Matcher matcher = pattern.matcher(str);
            if (matcher.find()) {
                return matcher.group();
            }
        }
        return "";
    }

    @Override
    public int getFirstRichStrIndex4Server() {
        if (containsRichStr4Server()) {
            return mStr4Server.indexOf(getFirstRichStr4Server());
        }
        return -1;
    }

    private Drawable getDrawable(Context context, int drawableId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getDrawable(drawableId);
        }
        return context.getResources().getDrawable(drawableId);
    }

    ////////////////////////////////////////////////////////// 下面是为Local和Server之间提供适配的各种操作 ///////////////////////////////////////////////////////////////

    /**
     * @param str '#[音乐]有何不可#'
     * @return '#有何不可#'
     */
    @Override
    public String parseStrServer2Local(String str) {
        String content = getContentFromRichStr4Server(str);
        return String.format("%s%s%s", getFlag(), content, getFlag());
    }

    /**
     * @param str '#有何不可#'
     * @return '#[音乐]有何不可#'
     */
    @Override
    public String parseStrLocal2Server(String str) {

        String type = getType4Server();
        String content = getContentFromRichStr4Local(str);
        if (TextUtils.isEmpty(type)) {

            return String.format("#%s#", content);
        } else {

            return String.format("#[%s]%s#", type, content);
        }
    }
}
