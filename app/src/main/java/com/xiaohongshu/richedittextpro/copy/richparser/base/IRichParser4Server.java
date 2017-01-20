package com.xiaohongshu.richedittextpro.copy.richparser.base;

/**
 * Created by wupengjian on 17/1/17.
 */
public interface IRichParser4Server {

    void setString(String str);

    String getPattern4Server();

    boolean containsRichStr4Server();

    String getFirstRichStr4Server();

    int getFirstRichStrIndex4Server();

    String getType4Server();

    String getContent4Server(String str);
}
