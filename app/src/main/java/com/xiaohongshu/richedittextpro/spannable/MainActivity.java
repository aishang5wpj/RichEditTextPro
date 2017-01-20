package com.xiaohongshu.richedittextpro.spannable;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaohongshu.richedittextpro.R;
import com.xiaohongshu.richedittextpro.spannable.richparser.strategy.NormalRichParser;
import com.xiaohongshu.richedittextpro.spannable.richparser.RichParserManager;
import com.xiaohongshu.richedittextpro.spannable.richparser.strategy.SimpleRichParser;

public class MainActivity extends AppCompatActivity {

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

        RichParserManager.getManager().registerParser(new NormalRichParser());
        RichParserManager.getManager().registerParser(new SimpleRichParser());
    }

    public void server2Local(View view) {

        String text = RichParserManager.getManager().parseStr4Local(mEditText.getText().toString());
        SpannableStringBuilder ssb = RichParserManager.getManager().parseRichItems(this, text);
        mTvServer2Local.setText(ssb);
    }

    public void local2Server(View view) {

        String text = RichParserManager.getManager().parseStr4Server(mTvServer2Local.getText().toString());
        mTvLocal2Server.setText(text);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RichParserManager.getManager().clearParser();
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
}
