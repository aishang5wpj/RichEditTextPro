package com.xiaohongshu.richedittextpro.spannable;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.xiaohongshu.richedittextpro.spannable.richparser.RichParserManager;

/**
 * Created by wupengjian on 17/1/12.
 */
public class RichEditTextPro extends EditText implements View.OnKeyListener {

    private SpannableStringBuilder mCurrentSpannableStr;
    /**
     * 光标之前的选中位置
     */
    private int mOldSelStart, mOldSelEnd;
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
                final String startStr = getText().toString().substring(0, startPos);

                //获取话题,并计算话题长度
                String richItem = RichParserManager.getManager().getLastRichStr4Local(startStr);
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
        final String startStr = getText().toString().substring(0, startPos);
        if (!RichParserManager.getManager().containsRichStr(startStr)) {
            return false;
        }

        String lastTopic = RichParserManager.getManager().getLastRichStr4Local(startStr);
        return startStr.endsWith(lastTopic);
    }

    /**
     * 插入字符串
     */
    public void insert(String string) {

        //setText()的操作会导致selection的改变,所以要先记录selection的位置
        int currentPos = getSelectionStart();

        String text = getText().toString();
        //截取光标前的字符串
        String startStr;
        if (0 == currentPos) {

            startStr = "";
        } else {

            startStr = text.substring(0, currentPos);
        }
        //截图光标后的字符串
        String endStr;
        if (currentPos == text.length()) {

            endStr = "";
        } else {

            endStr = text.substring(currentPos, text.length());
        }
        //插入话题
        String richText = RichParserManager.getManager().parseStr4Local(string);
        SpannableStringBuilder ssb = RichParserManager.getManager().parseRichItems(getContext(), startStr + richText + endStr);
        setText(ssb);
        setSelection(currentPos + richText.length());
    }

//    @Override
//    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
//        super.onTextChanged(text, start, lengthBefore, lengthAfter);
//        if (!TextUtils.equals(mCurrentSpannableStr, text)) {
//
//            String tempStr = RichParserManager.getManager().parseStr4Local(text.toString());
//            SpannableStringBuilder stringBuilder = RichParserManager.getManager().parseStr2Spannable(getContext(), tempStr);
//            mCurrentSpannableStr = stringBuilder;
//            setText(mCurrentSpannableStr);
//        }
//    }

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
        if (0 == selStart && 0 == selEnd) {
            mOldSelStart = selStart;
            mOldSelEnd = selEnd;
            return;
        }
        //避免下面的setSelection()触发onSelectionChanged()造成死循环
        if (selStart == mNewSelStart && selEnd == mNewSelEnd) {
            mOldSelStart = selStart;
            mOldSelEnd = selEnd;
            return;
        }
        int targetStart = selStart, targetEnd = selEnd;
        String text = getText().toString();
        //如果用户不是通过左移右移来改变位置,而是直接用手指点击文字使光标的位置发生改变
        if (selStart == selEnd && Math.abs(selStart - mOldSelStart) > 1) {

            //如果移到了话题内,则改变移动到其他合理的地方
            int pos = getRecommendSelection(selStart);
            if (-1 != pos) {
                setSelection(pos, pos);
                return;
            }
        } else {
            //光标左边往右
            if (mOldSelStart < selStart) {
                //事实上,onSelectionChanged()回调时位置已经改变过了
                // ,所以当光标左边往右移动时,如果需要判断光标当前位置pos后是否是一个话题时
                // ,应该判断pos-1时候的位置来判断(或者oldPos,但是oldPos是自己计算出来的,并不一定精准所以)
                int startPos = selStart - 1;
                String endStr = text.substring(startPos, text.length());
                if (RichParserManager.getManager().isStartWithRichItem(endStr)) {

                    String richStr = RichParserManager.getManager().getFirstRichStr4Local(endStr);
                    targetStart = startPos + richStr.length();
                }
            }
            //光标左边往左
            else if (mOldSelStart > selStart) {

                int startPos = selStart + 1;
                //逐个删除文字时,selStart + 1会导致数组越界
                startPos = startPos < text.length() ? startPos : text.length();
                String startStr = text.substring(0, startPos);
                if (RichParserManager.getManager().isEndWithRichItem(startStr)) {

                    String richStr = RichParserManager.getManager().getLastRichStr4Local(startStr);
                    targetStart = startPos - richStr.length();
                }
            }

            //光标右边往右
            if (mOldSelEnd < selEnd) {

                int endPos = selEnd - 1;
                String endStr = text.substring(endPos, text.length());
                if (RichParserManager.getManager().isStartWithRichItem(endStr)) {

                    String richStr = RichParserManager.getManager().getFirstRichStr4Local(endStr);
                    targetEnd = endPos + richStr.length();
                }
            }
            //光标右边往左
            else if (mOldSelEnd > selEnd) {

                int endPos = selEnd + 1;
                String startStr = text.substring(0, endPos);
                if (RichParserManager.getManager().isEndWithRichItem(startStr)) {

                    String richStr = RichParserManager.getManager().getLastRichStr4Local(startStr);
                    targetEnd = endPos - richStr.length();
                }
            }
        }
        //保存旧值
        mOldSelStart = selStart;
        mOldSelEnd = selEnd;
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

        String text = getText().toString();
        if (TextUtils.isEmpty(text)) {
            return -1;
        }

        //取前面字符串中最后一个富文本
        String startStr = text.substring(0, pos);
        String richStr = RichParserManager.getManager().getLastRichStr4Local(startStr);
        //start默认指向最前
        int start = 0;
        //如果点击的是最前面的话题,则richStr可能为空
        if (!TextUtils.isEmpty(richStr)) {

            start = startStr.lastIndexOf(richStr) + richStr.length();
        }

        //取后面字符串中第一个富文本
        String endStr = text.substring(pos, text.length());
        richStr = RichParserManager.getManager().getFirstRichStr4Local(endStr);
        //end默认指向最后
        int end = text.length();
        //如果点击的是最后面的话题,则richStr可能为空
        if (!TextUtils.isEmpty(richStr)) {

            end = startStr.length() + endStr.indexOf(richStr);
        }
        String middleStr = text.substring(start, end);
        richStr = RichParserManager.getManager().getLastRichStr4Local(middleStr);
        if (TextUtils.isEmpty(richStr)) {
            return -1;
        }
        //"01 #456# 9",这种话题的start并不是从0,而是2
        start = start + middleStr.indexOf(richStr);
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
        boolean handle = false;
        switch (id) {
            case android.R.id.cut:

                handle = true;
                int start = getSelectionStart();
                int end = getSelectionEnd();
                String content = getText().subSequence(start, end).toString();
                String startStr = getText().subSequence(0, start).toString();
                String endStr = getText().subSequence(end, getText().length()).toString();
                setText(startStr + endStr);
                //复制内容到剪切板
                copy(content);
                break;
            case android.R.id.copy:

                handle = true;
                //复制内容到剪切板
                copy(getText().toString());
                break;
            case android.R.id.paste:

                handle = true;
                ClipboardManager manager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = manager.getPrimaryClip();
                String text = clipData.getItemAt(clipData.getItemCount() - 1).getText().toString();
                String result = RichParserManager.getManager().parseStr4Local(text);
                insert(result);
                break;
        }
        return handle ? true : super.onTextContextMenuItem(id);
    }

    public void copy(String content) {
        ClipboardManager manager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        String result = RichParserManager.getManager().parseStr4Server(content);
        ClipData data = new ClipData(ClipData.newPlainText(null, result));
        manager.setPrimaryClip(data);
    }
}
