package com.giant.watsonapp.watson;

import android.content.Context;

import com.giant.watsonapp.App;
import com.giant.watsonapp.Const;
import com.giant.watsonapp.R;
import com.giant.watsonapp.models.GreenDaoManager;
import com.giant.watsonapp.models.VrClassifier;
import com.giant.watsonapp.utils.L;
import com.ibm.watson.developer_cloud.alchemy.v1.model.Concept;
import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechModel;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.giant.watsonapp.Const.WATSON_CONV_PWD_DEFAULT;
import static com.giant.watsonapp.Const.WATSON_CONV_USER_DEFAULT;
import static com.giant.watsonapp.Const.WATSON_STT_USER_DEFAULT;
import static com.giant.watsonapp.Const.WATSON_TTS_PWD_DEFAULT;
import static com.giant.watsonapp.Const.WATSON_TTS_USER_DEFAULT;
import static com.giant.watsonapp.Const.WATSON_VR_API_DEFAULT;
import static com.giant.watsonapp.models.GreenDaoManager.getDaoInstant;

/**
 * Created by Jorble on 2017/7/13.
 */

public class WatsonServiceManager {

    /**
     * 语音转文本
     * @return
     */
    public static SpeechToText initSpeechToTextService() {
        SpeechToText service = new SpeechToText();
        String username = App.getCacheString(Const.WATSON_STT_USER_KEY, WATSON_STT_USER_DEFAULT);
        String password = App.getCacheString(Const.WATSON_STT_PWD_KEY, Const.WATSON_STT_PWD_DEFAULT);
        String url = Const.WATSON_STT_URL;
        service.setUsernameAndPassword(username, password);
        service.setEndPoint(url);
        return service;
    }

    /**
     * 文本转语音
     * @return
     */
    public static TextToSpeech initTextToSpeechService(){
        TextToSpeech service = new TextToSpeech();
        String username = App.getCacheString(Const.WATSON_TTS_USER_KEY, WATSON_TTS_USER_DEFAULT);
        String password = App.getCacheString(Const.WATSON_TTS_PWD_KEY, WATSON_TTS_PWD_DEFAULT);
        String url = Const.WATSON_TTS_URL;
        service.setUsernameAndPassword(username, password);
        service.setEndPoint(url);
        return service;
    }

    /**
     * 对话
     * @return
     */
    public static ConversationService initConversationService() {
        ConversationService service = new ConversationService(ConversationService.VERSION_DATE_2016_09_20);
        String username = App.getCacheString(Const.WATSON_CONV_USER_KEY, WATSON_CONV_USER_DEFAULT);
        String password = App.getCacheString(Const.WATSON_CONV_PWD_KEY, WATSON_CONV_PWD_DEFAULT);
        String url = Const.WATSON_CONV_URL;
        service.setUsernameAndPassword(username, password);
        service.setEndPoint(url);
        return service;
    }

    /**
     * 图像识别
     * @return
     */
    public static VisualRecognition initVisualRecognitionService() {
        VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
        String apiKey = App.getCacheString(Const.WATSON_VR_API_KEY, WATSON_VR_API_DEFAULT);
        service.setApiKey(apiKey);
        return service;
    }

    /**
     * 图像识别配置
     * @return
     */
    public static ClassifyImagesOptions getVisualRecognitionOptions(String imgPath) {
        //获取所有分类器ID
        List<String> classifierIds=new ArrayList<>();
        //查询数据库有没有,如果没有则添加默认的
        List<VrClassifier> list=GreenDaoManager.getDaoInstant().getVrClassifierDao().loadAll();
        if(list!=null && list.size()>0){
            for(VrClassifier vrClassifier:list){
                classifierIds.add(vrClassifier.getClassifierId());
            }
        }else {
            classifierIds.add(Const.WATSON_VR_CLASSID_DEFAULT_1);
            //保存到数据库
            VrClassifier vrClassifier=new VrClassifier();
            vrClassifier.setName(Const.WATSON_VR_CLASSNAME_DEFAULT_1);
            vrClassifier.setClassifierId(Const.WATSON_VR_CLASSID_DEFAULT_1);
            GreenDaoManager.getDaoInstant().getVrClassifierDao().insertOrReplaceInTx(vrClassifier);
            L.i("添加默认图像分类器");
        }

        return new ClassifyImagesOptions.Builder()
                .images(new File(imgPath))//要进行分类的图片路径
                .classifierIds(classifierIds)//选择分类器进行分类
                .build();
    }

    /**
     * 语音转文本配置
     * @return
     */
    public static RecognizeOptions getSttRecognizeOptions() {
        return new RecognizeOptions.Builder()
                .continuous(true)
                .contentType(ContentType.OPUS.toString())
                //.model("en-UK_NarrowbandModel")
                .model(SpeechModel.ZH_CN_NARROWBANDMODEL.getName())//语言配置
                .interimResults(false)//识别只返回最后一条final
                .inactivityTimeout(2000)
                //TODO: Uncomment this to enable Speaker Diarization
                //.speakerLabels(true)
                .build();
    }
}
