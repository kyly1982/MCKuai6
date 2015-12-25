/**
 *
 */
package com.mckuai.imc.activity;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.Spinner;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mckuai.imc.R;
import com.mckuai.imc.bean.User;
import com.mckuai.imc.until.JsonCache;
import com.mckuai.imc.until.NetworkEngine;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import io.rong.imkit.RongIM;

/**
 * @author kyly
 */
public class MyApplication extends Application {
    private static MyApplication instance;
    private User user;
    private String mCacheDir;
    private DisplayImageOptions circleOptions;
    private DisplayImageOptions normalOptions;
    private AsyncHttpClient client;
    private static final String TAG = "MyApplication";
    private Spinner mSpinner;
    private boolean isLoginRC = false;

    private static final int MEM_CACHE_SIZE = 8 * 1024 * 1024;// 内存缓存大小
    private static final int CONNECT_TIME = 15 * 1000;// 连接时间
    private static final int TIME_OUT = 30 * 1000;// 超时时间
    private static final int IMAGE_POOL_SIZE = 3;// 线程池数量
    private JsonCache mCache;
    private Tencent mTencent;

    private OnHttpResopnseListener mRecommendListener;
    private OnHttpResopnseListener mForumsListener;
    private OnHttpResopnseListener mFriendsListener;
    private OnHttpResopnseListener mLoginRCListener;
    public static final int recCode = 0;
    public static final int forumCode = 1;
    public static final int friendCode = 2;
    public static final int loginRC = 3;

    public String recommend;
    public String forums;
    public String friends;
    public NetworkEngine networkEngine;

    public interface OnHttpResopnseListener {
        public void onHttpResponse(boolean isSucess, String msg, int requestCode);
    }


    /*
     * (non-Javadoc)
     *
     * @see android.app.Application#onCreate()
     */
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        this.instance = this;
        this.client = new AsyncHttpClient();
        this.mCache = new JsonCache(this);
        this.networkEngine = new NetworkEngine();
        initConfig();
        initOption();
        initRc();
        initUM();
        // appUpdate = AppUpdateService.getAppUpdate(this);
        mTencent = Tencent.createInstance("101155101", getApplicationContext());
        getUserInfo();
    }

    public RongIM getRC() {
        return RongIM.getInstance();
    }

    public void initRc() {
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
                "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {

            /**
             * IMKit SDK调用第一步 初始化
             */
            RongIM.init(this);
        }
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public boolean isLogin() {
        return null != user && 0 != user.getId();
    }

    private void initUM() {
        MobclickAgent.openActivityDurationTrack(false);// 禁止默认的页面统计方式，不会再自动统计Activity
    }

    @SuppressWarnings("deprecation")
    private void initConfig() {
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCacheExtraOptions(750, 480)
                .threadPoolSize(IMAGE_POOL_SIZE)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                        // 对于同一url只缓存一个图
                .memoryCache(new UsingFreqLimitedMemoryCache(MEM_CACHE_SIZE)).memoryCacheSize(MEM_CACHE_SIZE)
                .discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.FIFO)
                        //.discCache(new UnlimitedDiscCache(new File(getImageCacheRoot())))
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(getApplicationContext(), CONNECT_TIME, TIME_OUT))
                .writeDebugLogs().build();
        ImageLoader.getInstance().init(configuration);
    }

    private void initOption() {
        circleOptions = new DisplayImageOptions.Builder()
                // 加载过程中显示的图片
                .showStubImage(R.mipmap.background_user_cover_default)
                .showImageForEmptyUri(R.mipmap.background_user_cover_default)
                .showImageOnFail(R.mipmap.background_user_cover_default).cacheInMemory(true).cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.RGB_565).delayBeforeLoading(150)
                .displayer(new RoundedBitmapDisplayer(10))// 此处需要修改大小
                .build();

        normalOptions = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.background_loading)
                        // 加载过程中显示的图片
                .showImageForEmptyUri(R.mipmap.background_loading).showImageOnFail(R.mipmap.background_loading)
                .cacheInMemory(true).cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565).delayBeforeLoading(150).build();

    }

    private String getCacheRoot() {
        if (null == mCacheDir) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    || !Environment.isExternalStorageRemovable()) {
                if (null != getExternalCacheDir()) {
                    mCacheDir = getExternalCacheDir().getPath() + File.separator + getString(R.string.jsoncache_dir);
                } else {
                    mCacheDir = getCacheDir().getPath() + File.separator
                            + getString(R.string.jsoncache_dir);
                }
            } else {
                mCacheDir = getCacheDir().getPath() + File.separator
                        + getString(R.string.jsoncache_dir);
            }
        }
        return mCacheDir;
    }

    public String getJsonCacheRoot() {
        return getCacheRoot() + File.separator + getString(R.string.jsoncache_dir);
    }

    public String getImageCacheRoot() {
        return getCacheRoot() + File.separator + getString(R.string.imagecache_dir);
    }

    public void clearDiskCache() {
        ImageLoader.getInstance().clearDiskCache();
    }

    public void clearMemoryCache() {
        ImageLoader.getInstance().clearMemoryCache();
    }

    public DisplayImageOptions getCircleOptions() {
        return circleOptions;
    }

    public DisplayImageOptions getNormalOptions() {
        return normalOptions;
    }

    public AsyncHttpClient getClient() {
        return client;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (null != user && null != user.getHeadImg() && 10 < user.getHeadImg().length()) {
            // headimage =
            // ImageLoader.getInstance().loadImageSync(user.getHeadImg());
        }
    }

    public JsonCache getCache() {
        return mCache;
    }

    public boolean isFirstBoot() {
        SharedPreferences preferences = this.getSharedPreferences(getString(R.string.preferences_file), 0);
        return preferences.getBoolean("FirstBoot", true);
    }

    public void setFirstBoot() {
        SharedPreferences preferences = this.getSharedPreferences(getString(R.string.preferences_file), 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("FirstBoot", false);
        editor.commit();
    }

    public Spinner getSpinner() {
        return mSpinner;
    }

    public void setSpinner(Spinner mSpinner) {
        this.mSpinner = mSpinner;
    }

    public void loadRecommend(OnHttpResopnseListener i) {
        this.mRecommendListener = i;
        loadingRecommend();
    }

    public void loadForums(OnHttpResopnseListener i) {
        this.mForumsListener = i;
        loadingForums();
    }

    public void loadFirends(OnHttpResopnseListener i) {
        this.mFriendsListener = i;
        loadingFriends();
    }

    private void loadingRecommend() {
        final String url = getString(R.string.interface_domainName) + getString(R.string.interface_recommend);
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                super.onStart();
                recommend = mCache.get(url);
                if (null != recommend && !recommend.isEmpty()) {
                    mRecommendListener.onHttpResponse(true, "cache", recCode);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // TODO Auto-generated method stub
                super.onSuccess(statusCode, headers, response);
                if (isSuccess(response)) {
                    try {
                        recommend = response.getString("dataObject");
                        mRecommendListener.onHttpResponse(true, null, recCode);
                        mCache.put(url, recommend);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        mRecommendListener.onHttpResponse(false, "返回数据不正确!", recCode);
                    }
                } else {
                    mRecommendListener.onHttpResponse(false, "操作失败!", recCode);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                // TODO Auto-generated method stub
                super.onFailure(statusCode, headers, responseString, throwable);
                mRecommendListener.onHttpResponse(false, throwable.getLocalizedMessage() + "", recCode);
            }
        });
    }

    private void loadingForums() {
        final String url = getString(R.string.interface_domainName) + getString(R.string.interface_forumList);
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                super.onStart();
                forums = mCache.get(url);
                if (null != forums && !forums.isEmpty()) {
                    mForumsListener.onHttpResponse(true, "cache", forumCode);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // TODO Auto-generated method stub
                super.onSuccess(statusCode, headers, response);
                if (isSuccess(response)) {
                    forums = response.toString();
                    mCache.put(url, forums);
                    mForumsListener.onHttpResponse(true, null, forumCode);
                } else {
                    mForumsListener.onHttpResponse(false, "操作失败!", forumCode);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                // TODO Auto-generated method stub
                super.onFailure(statusCode, headers, responseString, throwable);
                mFriendsListener.onHttpResponse(false, throwable.getLocalizedMessage() + "", forumCode);
            }
        });
    }

    private void loadingFriends() {
        if (null != user && 0 != user.getId()) {
            final String url = getString(R.string.interface_domainName) + getString(R.string.interface_fellow) + "&id="
                    + user.getId();
            client.get(url, new JsonHttpResponseHandler() {
                @Override
                public void onStart() {
                    // TODO Auto-generated method stub
                    super.onStart();
                    friends = mCache.get(url);
                    if (null != friends && !friends.isEmpty() && null != mFriendsListener) {
                        mFriendsListener.onHttpResponse(true, "cache", friendCode);
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // TODO Auto-generated method stub
                    super.onSuccess(statusCode, headers, response);
                    if (isSuccess(response)) {
                        try {
                            friends = response.getString("dataObject");
                            if (null != mFriendsListener) {
                                mFriendsListener.onHttpResponse(true, null, friendCode);
                            }
                            mCache.put(url, friends);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            if (null != mFriendsListener) {
                                mFriendsListener.onHttpResponse(false, "返回数据格式不正确!", friendCode);
                            }
                        }
                    } else {
                        if (null != mFriendsListener) {
                            mFriendsListener.onHttpResponse(false, "操作失败!", friendCode);
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    // TODO Auto-generated method stub
                    super.onFailure(statusCode, headers, responseString, throwable);
                    mFriendsListener.onHttpResponse(false, throwable.getLocalizedMessage(), friendCode);
                }
            });
        }
    }

    private boolean isSuccess(JSONObject response) {
        if (response.has("state") && response.has("dataObject")) {
            try {
                if (response.getString("state").equalsIgnoreCase("ok")) {
                    return true;
                }
            } catch (Exception e) {
                // TODO: handle exception
                return false;
            }
        }
        return false;
    }




    public boolean isLoginRC() {
        return isLoginRC;
    }

    public void setIsLoginRC(boolean isLoginRC) {
        this.isLoginRC = isLoginRC;
    }

    public boolean LogOut() {
        if (isLogin()) {
            if (isLoginRC) {
                RongIM.getInstance().disconnect(true);
                isLoginRC = false;
            }
            if (null != mTencent && !mTencent.isSessionValid()) {
                mTencent.logout(this);
            }
            this.user = null;
            SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences_file), 0);
            SharedPreferences.Editor editor = preferences.edit();
            // 将id和有效期置为0
            editor.putInt(getString(R.string.mc_id), 0);
            editor.putLong(getString(R.string.tokenExpires), 0);
            editor.commit();
        }
        return true;
    }

    private void getUserInfo() {
        if (null == user) {
            user = new User();
        }
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences_file), 0);
        user.setLevel(preferences.getInt("MC_LEVEL", 0));
        user.setProcess(preferences.getFloat("MC_PROCESS", 0f));
        user.setAddr(preferences.getString("MC_ADDR", null));
        user.setId(preferences.getInt(getString(R.string.mc_id), 0));
        user.setName(preferences.getString(getString(R.string.qq_OpenId), null));// qq_OpenId用于融云的用户名
        user.setHeadImg(preferences.getString(getString(R.string.mc_userFace), null));// 用户头像
        user.setNike(preferences.getString(getString(R.string.mc_nick), null));// 显示名
        user.setGender(preferences.getString(getString(R.string.mc_gender), null));// 性别
        user.setToken(preferences.getString(getString(R.string.rcToken), null));// 融云令牌
        if (0 != user.getId()) {
            this.isLoginRC = true;
        }
    }

    public void saveUserInfo() {
        if (isLoginRC) {
            SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences_file), 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("MC_LEVEL", user.getLevel());
            editor.putFloat("MC_PROCESS", user.getProcess());
            editor.putString("MC_ADDR", user.getAddr());
            editor.putBoolean(getString(R.string.enable_autologin), true);
            editor.putString(getString(R.string.qq_OpenId), user.getName() + "");
            editor.putString(getString(R.string.mc_userFace), user.getHeadImg() + "");
            editor.putString(getString(R.string.mc_nick), user.getNike() + "");
            editor.putString(getString(R.string.mc_gender), user.getGender() + "");
            editor.putString(getString(R.string.rcToken), user.getToken() + "");
            editor.putInt(getString(R.string.mc_id), user.getId());
            editor.commit();
        }
    }

    public Tencent getTencent() {
        return mTencent;
    }


    /**
     * 获得当前进程的名字
     *
     * @param context
     * @return 进程号
     */
    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    public String getFriends() {
        return friends;
    }

    public String getForums() {
        return forums;
    }

    public String getRecommend() {
        return recommend;
    }
}