package com.mckuai.imc.until;

import android.content.Context;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mckuai.imc.R;
import com.mckuai.imc.activity.MyApplication;
import com.mckuai.imc.bean.Post;
import com.mckuai.imc.bean.RecommendBean;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * Created by kyly on 2015/12/25.
 */
public class NetworkEngine {
    private MyApplication mApplication;
    //private AsyncHttpClient mClient;
    //private Gson mGson;

    public NetworkEngine() {
        mApplication = MyApplication.getInstance();
        //this.mClient = mApplication.getClient();
        //this.mGson = new Gson();
    }

    public interface onRcLoginListener {
        public void onSuccess();

        public void onFalse(String msg);
    }

    public void loginToRC(final onRcLoginListener l) {
        if (mApplication.isLogin()) {
            RongIM.connect(mApplication.getUser().getToken(), new RongIMClient.ConnectCallback() {

                @Override
                public void onTokenIncorrect() {
                    if (null != l) {
                        l.onFalse("令牌无效！");
                    }
                }

                @Override
                public void onSuccess(String arg0) {
                    // TODO Auto-generated method stub
                    if (null != l) {
                        mApplication.setIsLoginRC(true);
                        l.onSuccess();
                    }
                }

                @Override
                public void onError(RongIMClient.ErrorCode arg0) {
                    // TODO Auto-generated method stub
                    if (null != l) {
                        l.onFalse(arg0.getMessage());
                    }
                }
            });
        } else {

            if (null != l) {
                l.onFalse("尚未登录，请先登录！");
            }
        }
    }
}
