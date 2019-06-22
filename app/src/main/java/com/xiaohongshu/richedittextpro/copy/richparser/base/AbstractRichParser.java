package com.xiaohongshu.richedittextpro.copy.richparser.base;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Pair;
import android.view.View;

import com.xiaohongshu.richedittextpro.copy.VerticalImageSpan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wupengjian on 17/1/17.
 */
public abstract class AbstractRichParser implements IRichParser4Local, IRichParser4Server, IRichParserAdapter {

    private OnSpannableClickListener mOnClickListener;
    private List<RichItemBean> mTargetRichItems = new ArrayList<>();
    private static final ImageSpanComparator IMAGE_SPAN_COMPARATOR = new ImageSpanComparator();

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
    public Object[] parseFirstRichSpannable(SpannableStringBuilder spannableStringBuilder) {
        SpannableStringBuilder ssb = spannableStringBuilder;
        ImageSpan[] imageSpen = ssb.getSpans(0, ssb.length(), ImageSpan.class);
        if (imageSpen == null || imageSpen.length <= 0) {
            return null;
        }
        IMAGE_SPAN_COMPARATOR.setSsb(ssb);
        Arrays.sort(imageSpen, IMAGE_SPAN_COMPARATOR);
        // 还是得遍历全部span，找到可以处理的span，否则如果是一个emoji等处理不了的span，则无法进行下一步了
        for (ImageSpan imageSpan : imageSpen) {
            String source = imageSpan.getSource();
            Pair<Integer, String> richSpan = getFirstRichStr4Server(source);
            if (richSpan == null) {
                return null;
            }
            int imageStart = ssb.getSpanStart(imageSpan);
            ForegroundColorSpan[] colorSpans = ssb.getSpans(imageStart, ssb.length(), ForegroundColorSpan.class);
            int start = ssb.getSpanStart(colorSpans[0]);
            int end = ssb.getSpanEnd(colorSpans[0]);
            // 判断传进来的富文本是不是完整的富文本，在输入框中光标移到富文本中间后，得到的富文本不是完整的富文本
            Pair<String, String> sourceInfo = parseInfo4Server(source);
            final String str = String.format("#%s", sourceInfo.second);
            final String richSpanStr = ssb.subSequence(start, end).toString();
            if (!TextUtils.equals(str, richSpanStr)) {
                continue;
            }
            Object[] result = new Object[4];
            result[0] = richSpan.second;
            result[1] = ssb.subSequence(start, end);
            result[2] = start;
            result[3] = end - start;
            return result;
        }
        return null;
    }

    @Override
    public Object[] getLastRichSpannable(SpannableStringBuilder spannableStringBuilder) {
        SpannableStringBuilder ssb = spannableStringBuilder;
        ImageSpan[] imageSpen = ssb.getSpans(0, ssb.length(), ImageSpan.class);
        if (imageSpen == null || imageSpen.length <= 0) {
            return null;
        }
        IMAGE_SPAN_COMPARATOR.setSsb(ssb);
        Arrays.sort(imageSpen, IMAGE_SPAN_COMPARATOR);

        // 还是得遍历全部span，找到可以处理的span，否则如果是一个emoji等处理不了的span，则无法进行下一步了
        for (int i = imageSpen.length - 1; i >= 0; i--) {
            String source = imageSpen[i].getSource();
            Pair<Integer, String> richSpan = getFirstRichStr4Server(source);
            if (richSpan == null) {
                return null;
            }
            int imageStart = ssb.getSpanStart(imageSpen[i]);
            ForegroundColorSpan[] colorSpans = ssb.getSpans(imageStart, ssb.length(), ForegroundColorSpan.class);
            int start = ssb.getSpanStart(colorSpans[0]);
            int end = ssb.getSpanEnd(colorSpans[0]);
            // 判断传进来的富文本是不是完整的富文本，在输入框中光标移到富文本中间后，得到的富文本不是完整的富文本
            Pair<String, String> sourceInfo = parseInfo4Server(source);
            final String str = String.format("#%s", sourceInfo.second);
            final String richSpanStr = ssb.subSequence(start, end).toString();
            if (!TextUtils.equals(str, richSpanStr)) {
                continue;
            }
            Object[] result = new Object[4];
            result[0] = richSpan.second;
            result[1] = ssb.subSequence(start, end);
            result[2] = start;
            result[3] = end - start;
            return result;
        }
        return null;
    }

    ////////////////////////////////////////////////////////// 下面是为Server提供的各种操作 ///////////////////////////////////////////////////////////////

    @Override
    public Pair<Integer, String> getFirstRichStr4Server(String str) {
        Pattern pattern = Pattern.compile(getPattern4Server());
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {

            String richStr = matcher.group();
            String type = getType4Server();
            Pair<String, String> content = parseInfo4Server(richStr);

            RichItemBean richItemBean = RichItemBean.createRichItem(type, content.first, content.second);
            if (mTargetRichItems.isEmpty() || mTargetRichItems.contains(richItemBean)) {
                int index = TextUtils.isEmpty(str) ? -1 : str.indexOf(richStr);
                return new Pair<>(index, richStr);
            }
        }
        return null;
    }

    protected abstract int getColor();

    protected abstract int getDrawableId();

    ////////////////////////////////////////////////////////// 下面是为Local和Server之间提供适配的各种操作 ///////////////////////////////////////////////////////////////

    /**
     * @param context
     * @param richStr #[类型]内容#
     * @return #内容
     */
    @Override
    public SpannableStringBuilder parseStr2Spannable(Context context, final String richStr) {

        final String type = getType4Server();
        final Pair<String, String> info = parseInfo4Server(richStr);

        final String str = String.format("#%s", info.second);

        SpannableStringBuilder spannableStr = new SpannableStringBuilder(str);

        int drawableId = getDrawableId();
        if (drawableId != 0) {
            Drawable drawable = getDrawable(context, drawableId);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            ImageSpan imageSpan = new VerticalImageSpan(drawable, richStr, ImageSpan.ALIGN_BOTTOM);
            spannableStr.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getColor());
        spannableStr.setSpan(colorSpan, 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {

                if (null != mOnClickListener) {
                    mOnClickListener.onClick(AbstractRichParser.this, type, info, richStr);
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
        return imageSpen[0].getSource();
    }

    private Drawable getDrawable(Context context, int drawableId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getDrawable(drawableId);
        }
        return context.getResources().getDrawable(drawableId);
    }

    static class ImageSpanComparator implements Comparator<ImageSpan> {

        public void setSsb(SpannableStringBuilder ssb) {
            this.mSsb = ssb;
        }

        private SpannableStringBuilder mSsb;

        @Override
        public int compare(ImageSpan lhs, ImageSpan rhs) {
            int index0 = mSsb.getSpanStart(lhs);
            int index1 = mSsb.getSpanStart(rhs);
            return (index0 - index1);
        }
    }
}
