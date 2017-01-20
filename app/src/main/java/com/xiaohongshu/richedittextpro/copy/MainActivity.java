package com.xiaohongshu.richedittextpro.copy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaohongshu.richedittextpro.R;
import com.xiaohongshu.richedittextpro.copy.richparser.RichParserManager;
import com.xiaohongshu.richedittextpro.copy.richparser.base.AbstractRichParser;
import com.xiaohongshu.richedittextpro.copy.richparser.base.OnSpannableClickListener;
import com.xiaohongshu.richedittextpro.copy.richparser.base.RichItemBean;
import com.xiaohongshu.richedittextpro.copy.richparser.strategy.NormalRichParser;
import com.xiaohongshu.richedittextpro.copy.richparser.strategy.SimpleRichParser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnSpannableClickListener {

    private EditText mEditText;
    private TextView mTvServer2Local, mTvLocal2Server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText = (EditText) findViewById(R.id.edittext);
        mTvServer2Local = (TextView) findViewById(R.id.server2Local);
        mTvLocal2Server = (TextView) findViewById(R.id.local2Server);

        mTvServer2Local.setMovementMethod(LinkMovementMethod.getInstance());
        mTvServer2Local.setHighlightColor(getResources().getColor(android.R.color.transparent));

        RichParserManager.getManager().registerParser(
                new SimpleRichParser(this)
                        .setRichItems(createSimpleRichItemList()));
        RichParserManager.getManager().registerParser(
                new NormalRichParser(this)
                        .setRichItems(createNormalRichItemList()));
    }

    private List<RichItemBean> createSimpleRichItemList() {
        List<RichItemBean> richItemBeen = new ArrayList<>();
        richItemBeen.add(RichItemBean.createRichItem("", "fade"));
        richItemBeen.add(RichItemBean.createRichItem("", "try"));
        return richItemBeen;
    }

    private List<RichItemBean> createNormalRichItemList() {
        List<RichItemBean> richItemBeen = new ArrayList<>();
        richItemBeen.add(RichItemBean.createRichItem("音乐", "我以为"));
        richItemBeen.add(RichItemBean.createRichItem("音乐", "会长大的幸福"));
        richItemBeen.add(RichItemBean.createRichItem("音乐", "带空格 的话题"));
        return richItemBeen;
    }

    public void server2Local(View view) {

        String text = mEditText.getText().toString();
        SpannableStringBuilder ssb = RichParserManager.getManager().parseStr2Spannable(this, text);
        mTvServer2Local.setText(ssb);
    }

    public void local2Server(View view) {

        SpannableString ss = (SpannableString) mTvServer2Local.getText();
        SpannableStringBuilder ssb = new SpannableStringBuilder(ss);
        String str = RichParserManager.getManager().parseSpannable2Str(ssb);
        mTvLocal2Server.setText(str);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void t(String msg) {
        final String text = msg;
        if (!TextUtils.isEmpty(text)) {
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        }
    }

    private void v(String msg) {
        final String text = msg;
        if (!TextUtils.isEmpty(text)) {
            Log.v("spannableStr", text);
        }
    }

    @Override
    public void onClick(AbstractRichParser parser, String type, String content, String sourceStr) {
        Toast.makeText(this, String.format("sourceStr: %s,type: %s,content: %s", sourceStr, type, content), Toast.LENGTH_SHORT).show();
    }
}
