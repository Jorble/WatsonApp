package com.giant.watsonapp;

/**
 * Created by Jorble on 2017/7/27.
 */

public class Const {
    /**
     * MySQL
     */
    public static final String MYSQL_URL_KEY="MYSQL_URL_KEY";
    public static final String MYSQL_USER_KEY="MYSQL_USER_KEY";
    public static final String MYSQL_PWD_KEY="MYSQL_PWD_KEY";
    public static final String MYSQL_DRIVER_KEY="MYSQL_DRIVER_KEY";

    public static final String MYSQL_URL_DEFAULT="jdbc:mysql://183.62.42.146:24406/watson_app";
    public static final String MYSQL_USER_DEFAULT="root";
    public static final String MYSQL_PWD_DEFAULT="";
    public static final String MYSQL_DRIVER_DEFAULT="com.mysql.jdbc.Driver";

    public static final String MYSQL_QUERY_ALL="SELECT id, content, img, url FROM conversation";
    public static final String MYSQL_QUERY_WHERE="SELECT id, content, img, url FROM conversation where ";
    public static final String MYSQL_FIELD_ID="id";
    public static final String MYSQL_FIELD_CONTENT="content";
    public static final String MYSQL_FIELD_DETAIL="detail";
    public static final String MYSQL_FIELD_IMG="img";
    public static final String MYSQL_FIELD_URL="url";

    /**
     * watson翻译
     */
    public static final String WATSON_TRANS_USER_KEY="WATSON_TRANS_USER_KEY";
    public static final String WATSON_TRANS_PWD_KEY="WATSON_TRANS_PWD_KEY";

    public static final String WATSON_TRANS_USER_DEFAULT="bfc7b265-223e-4796-9d98-3a459e159ddc";
    public static final String WATSON_TRANS_PWD_DEFAULT="bcHMQ04oKyFa";
    public static final String WATSON_TRANS_URL="https://gateway.watsonplatform.net/language-translator/api";

    /**
     * watson 语音转文本
     */
    public static final String WATSON_STT_USER_KEY="WATSON_STT_USER_KEY";
    public static final String WATSON_STT_PWD_KEY="WATSON_STT_PWD_KEY";

    public static final String WATSON_STT_USER_DEFAULT="e588e89d-a5c9-4fb9-96c9-13df3af5800c";
    public static final String WATSON_STT_PWD_DEFAULT="gsoHtZJqy81M";
    public static final String WATSON_STT_URL="https://stream.watsonplatform.net/speech-to-text/api";

    /**
     * watson 文本转语音
     */
    public static final String WATSON_TTS_USER_KEY="WATSON_TTS_USER_KEY";
    public static final String WATSON_TTS_PWD_KEY="WATSON_TTS_PWD_KEY";

    public static final String WATSON_TTS_USER_DEFAULT="5bcbd918-060b-49f6-9ac9-83dea348f01c";
    public static final String WATSON_TTS_PWD_DEFAULT="8LCaAPDfU5LN";
    public static final String WATSON_TTS_URL="https://stream.watsonplatform.net/text-to-speech/api";

    /**
     * watson 对话
     */
    public static final String WATSON_CONV_USER_KEY="WATSON_CONV_USER_KEY";
    public static final String WATSON_CONV_PWD_KEY="WATSON_CONV_PWD_KEY";
    public static final String WATSON_CONV_WSID_KEY="WATSON_CONV_WSID_KEY";

    public static final String WATSON_CONV_USER_DEFAULT="c4dfaedb-7867-4004-8f87-81e38451d414";
    public static final String WATSON_CONV_PWD_DEFAULT="Zjbb4JHxLUCP";
    public static final String WATSON_CONV_WSID_DEFAULT="6c7183bd-c1b0-420e-9b4d-2301187a3345";
    public static final String WATSON_CONV_URL="https://gateway.watsonplatform.net/conversation/api";

    /**
     * watson 图像识别
     */
    public static final String WATSON_VR_API_KEY="WATSON_VR_API_KEY";

    public static final String WATSON_VR_API_DEFAULT="b50d98b8beffe96a6c172773a26c5f503376178f";
    public static final String WATSON_VR_URL="https://gateway-a.watsonplatform.net/visual-recognition/api";

    public static final String WATSON_VR_CLASSNAME_DEFAULT_1="island";
    public static final String WATSON_VR_CLASSID_DEFAULT_1="island_1142199889";

    /**
     * EventBus标识常量
     */
    public static final int BUS_VOICE_PLAY=0;
    public static final int BUS_VOICE_STOP=1;
}
