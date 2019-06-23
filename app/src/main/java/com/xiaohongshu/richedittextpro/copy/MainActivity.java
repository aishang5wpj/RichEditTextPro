package com.xiaohongshu.richedittextpro.copy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaohongshu.richedittextpro.R;
import com.xiaohongshu.richedittextpro.copy.richparser.RichParserManager;
import com.xiaohongshu.richedittextpro.copy.richparser.base.AbstractRichParser;
import com.xiaohongshu.richedittextpro.copy.richparser.base.OnSpannableClickListener;
import com.xiaohongshu.richedittextpro.copy.richparser.strategy.NormalRichParser;
import com.xiaohongshu.richedittextpro.copy.richparser.strategy.PoiRichParser;

public class MainActivity extends AppCompatActivity implements OnSpannableClickListener {

    private EditText mEditText;
    private EditText mEditTextNormal, mEditTextPro;
    private TextView mTvServer2Local, mTvLocal2Server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText = (EditText) findViewById(R.id.edittext);
        mEditTextNormal = (EditText) findViewById(R.id.edittextNormal);
        mEditTextPro = (EditText) findViewById(R.id.edittextPro);
        mTvServer2Local = (TextView) findViewById(R.id.server2Local);
        mTvLocal2Server = (TextView) findViewById(R.id.local2Server);

        mTvServer2Local.setMovementMethod(LinkMovementMethod.getInstance());
        mTvServer2Local.setHighlightColor(getResources().getColor(android.R.color.transparent));

        RichParserManager.getManager().registerParser(new PoiRichParser(this));
        RichParserManager.getManager().registerParser(new NormalRichParser(this));

        StringBuilder builder = new StringBuilder();
        String jsonStr = "" +
                "{" +
                "    \"id\":1," +
                "    \"latitude\":116.46," +
                "    \"longitude\":39.92" +
                "}";
        String text = String.format("#[位置][%s]测试#", jsonStr);

        builder.append(text);

        builder.append("普通的一句话没有富文本");

        jsonStr = "" +
                "{" +
                "    \"id\":2," +
                "    \"latitude\":116.46," +
                "    \"longitude\":39.92" +
                "}";
        text = String.format("#[位置][%s]测试#", jsonStr);

        builder.append(text);

        mEditTextNormal.setText(RichParserManager.getManager().parseStr2Spannable(this, builder.toString()));
        mEditTextPro.setText(RichParserManager.getManager().parseStr2Spannable(this, builder.toString()));
        mEditTextPro.requestFocus();

        mEditText.setText(builder);
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
    public void onClick(AbstractRichParser parser, String type, Pair<String, String> content, String sourceStr) {
        Toast.makeText(this, String.format("sourceStr: %s,type: %s,content: %s", sourceStr, type, content), Toast.LENGTH_SHORT).show();
    }
}
