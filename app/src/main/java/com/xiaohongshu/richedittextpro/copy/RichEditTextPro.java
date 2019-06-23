package com.xiaohongshu.richedittextpro.copy;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xiaohongshu.richedittextpro.copy.richparser.RichParserManager;

/**
 * Created by wupengjian on 17/1/12.
 */
public class RichEditTextPro extends EditText implements View.OnKeyListener {

    /**
     * 为了避免死循环触发onSelectionChanged(),设置的两个标志变量
     */
    private int mNewSelStart, mNewSelEnd;

    public RichEditTextPro(Context context) {
        super(context);
        init();
    }

    public RichEditTextPro(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOnKeyListener(this);
    }

    /**
     * 监听删除按键，执行删除动作
     */
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        //按下键盘时会出发动作，弹起键盘时同样会触发动作
        if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {

            if (startStrEndWithRichItem() && getSelectionStart() == getSelectionEnd()) {

                int startPos = getSelectionStart();
                final SpannableStringBuilder ssb = (SpannableStringBuilder) getText().subSequence(0, startPos);
                //获取话题,并计算话题长度
                String richItem = RichParserManager.getManager().getLastRichItem4Spannable(ssb).toString();
                int lenth = richItem.length();

                clearFocus();
                requestFocus();

                //方案1: 先选中,不直接删除
                setSelection(startPos - lenth, startPos);

                //方案2: 直接删除该话题
//                String temp = startStr.substring(0, startStr.length() - lenth);
//                setText(temp + toString().substring(startPos, toString().length()));
//                setSelection(temp.length());

                return true;
            }
        }
        return false;
    }

    /**
     * 判断光标前面是否是一个"话题"
     * 1.字符串结尾是话题后缀
     * 2.在该字符串中找得到与之匹配的话题前缀
     * <p/>
     * 注意这种是不合法的: " #话题# asdfads# "
     * <p/>
     * 先找出字符串中所有话题,取最后一个话题的index,如果index不等于当前光标的位置
     * ,说明当前光标位置前面的字符串不是一个话题
     *
     * @return
     */
    public boolean startStrEndWithRichItem() {

        int startPos = getSelectionStart();
        final SpannableStringBuilder ssb = (SpannableStringBuilder) getText().subSequence(0, startPos);
        final SpannableStringBuilder lastSSB = RichParserManager.getManager().getLastRichItem4Spannable(ssb);
        if (TextUtils.isEmpty(lastSSB)) {
            return false;
        }
        String lastTopic = lastSSB.toString();
        return ssb.toString().endsWith(lastTopic);
    }

    /**
     * 插入字符串
     *
     * @param string
     */
    public void insert(SpannableStringBuilder string) {

        //setText()的操作会导致selection的改变,所以要先记录selection的位置
        int start = getSelectionStart();
        int end = getSelectionEnd();

        //截取光标前的字符串
        SpannableStringBuilder startStr;
        if (0 == start) {

            startStr = new SpannableStringBuilder("");
        } else {

            startStr = (SpannableStringBuilder) getText().subSequence(0, start);
        }
        //截图光标后的字符串
        SpannableStringBuilder endStr;
        if (end == getText().length()) {

            endStr = new SpannableStringBuilder("");
        } else {

            endStr = (SpannableStringBuilder) getText().subSequence(end, getText().length());
        }
        //插入话题
        SpannableStringBuilder richText = RichParserManager.getManager().parseStr2Spannable(getContext(), string.toString());
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb
                .append(startStr)
                .append(richText)
                .append(endStr);
        setText(ssb);
        setSelection(start + richText.length());
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if (!TextUtils.isEmpty(text)) {
            setSelection(text.length());
        }
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        try {

            selectChanged(selStart, selEnd);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectChanged(int selStart, int selEnd) {
        //调用setText()会导致先触发onSelectionChanged()并且start和end均为0,然后才是正确的start和end的值
        if (0 == selStart && 0 == selEnd
                //避免下面的setSelection()触发onSelectionChanged()造成死循环
                || selStart == mNewSelStart && selEnd == mNewSelEnd) {
            return;
        }
        //校准左边光标
        int targetStart = getRecommendSelection(selStart);
        targetStart = targetStart == -1 ? selStart : targetStart;
        //校准右边光标
        int targetEnd = getRecommendSelection(selEnd);
        targetEnd = targetEnd == -1 ? selEnd : targetEnd;
        //保存新值
        mNewSelStart = targetStart;
        mNewSelEnd = targetEnd;
        //更新选中区域
        setSelection(targetStart, targetEnd);
    }

    /**
     * 掐头去尾,取中间字符串中的富文本
     *
     * @param pos
     * @return 由于富文本无法选中, 所以返回一个合适的位置(返回-1表示不做特殊处理)
     */
    private int getRecommendSelection(int pos) {

        if (TextUtils.isEmpty(getText())) {
            return -1;
        }
        //取前面字符串中最后一个富文本
        SpannableStringBuilder startStr = (SpannableStringBuilder) getText().subSequence(0, pos);
        SpannableStringBuilder richStr = RichParserManager.getManager().getLastRichItem4Spannable(startStr);
        //start默认指向最前
        int start = 0;
        //如果点击的是最前面的话题,则richStr可能为空
        if (!TextUtils.isEmpty(richStr)) {

            start = startStr.toString().lastIndexOf(richStr.toString()) + richStr.length();
        }

        //取后面字符串中第一个富文本
        SpannableStringBuilder endStr = (SpannableStringBuilder) getText().subSequence(pos, getText().length());
        Object[] firstRichSpan = RichParserManager.getManager().getFirstRichItem4Spannable(endStr);
        //end默认指向最后
        int end = getText().length();
        //如果点击的是最后面的话题,则richStr可能为空
        if (firstRichSpan != null) {
            int index = (int) firstRichSpan[2];
            end = startStr.length() + index;
        }
        SpannableStringBuilder middleStr = (SpannableStringBuilder) getText().subSequence(start, end);
        richStr = RichParserManager.getManager().getLastRichItem4Spannable(middleStr);
        if (TextUtils.isEmpty(richStr)) {
            return -1;
        }
        //"01 #456# 9",这种话题的start并不是从0,而是2
        start = start + middleStr.toString().indexOf(richStr.toString());
        end = start + richStr.length();
        //将光标移动离当前位置较近的地方
        return (pos - start < end - pos) ? start : end;
    }

    @Override
    public void setSelection(int start, int stop) {
        if (0 <= start && stop <= getText().toString().length()) {
            super.setSelection(start, stop);
        }
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        int start = getSelectionStart();
        int end = getSelectionEnd();
        SpannableStringBuilder content = (SpannableStringBuilder) getText().subSequence(start, end);
        String startStr = getText().subSequence(0, start).toString();
        String endStr = getText().subSequence(end, getText().length()).toString();
        boolean handle = false;
        switch (id) {
            case android.R.id.cut:

                handle = true;
                setText(startStr + endStr);
                //复制内容到剪切板
                copy(content);
                break;
            case android.R.id.copy:

                handle = true;
                //复制内容到剪切板
                copy(content);
                break;
            case android.R.id.paste:

                handle = true;
                ClipboardManager manager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = manager.getPrimaryClip();
                String text = clipData.getItemAt(clipData.getItemCount() - 1).getText().toString();
                insert(new SpannableStringBuilder(text));
                break;
        }
        return handle ? true : super.onTextContextMenuItem(id);
    }

    public void copy(SpannableStringBuilder content) {
        ClipboardManager manager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        String result = RichParserManager.getManager().parseSpannable2Str(content);
        ClipData data = new ClipData(ClipData.newPlainText(null, result));
        manager.setPrimaryClip(data);
    }
}
