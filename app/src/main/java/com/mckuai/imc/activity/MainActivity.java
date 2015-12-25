package com.mckuai.imc.activity;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mckuai.imc.R;
import com.mckuai.imc.adapter.FragmentAdapter;
import com.mckuai.imc.bean.ForumInfo;
import com.mckuai.imc.fragment.BaseFragment;
import com.mckuai.imc.fragment.Chat;
import com.mckuai.imc.fragment.Forums;
import com.mckuai.imc.fragment.Live;
import com.mckuai.imc.fragment.MCSildingMenu;
import com.mckuai.imc.fragment.Recommend;
import com.mckuai.imc.until.NetworkEngine;
import com.mckuai.imc.widget.slidingmenu.SlidingMenu;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

public class MainActivity extends BaseActivity implements OnPageChangeListener, OnTabChangeListener,
        RongIMClient.OnReceiveMessageListener, OnClickListener, RongIMClient.ConnectionStatusListener, NetworkEngine.onRcLoginListener {

    private FragmentTabHost mTabHost;
    private FragmentAdapter mAdapter;
    private List<BaseFragment> list = new ArrayList<BaseFragment>();
    private int imageViewArray[] = {R.drawable.btn_recommend_selector, R.drawable.btn_live_selector,
            R.drawable.btn_chat_selector, R.drawable.btn_forum_selector};

    private LayoutInflater layoutInflater;
    private String textViewArray[];
    private ViewPager vp;
    private TextView title;
    private Spinner spinner;
    private ImageButton btn_right;

    private String TAG = "MainActivity";
    private MyApplication application;
    private Gson mGson;
    private SlidingMenu mySlidingMenu;
    private MCSildingMenu menu;
    private int backKeyClickCount;
    private boolean isMenuShowing = false;
    private static MainActivity instance;

    private RongIM rongIM;

    // private AppUpdate updateChecker;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setNotificationViewGroup(R.id.pager);
        this.application = MyApplication.getInstance();
        initSlidingMenu();
        mGson = new Gson();
        textViewArray = getResources().getStringArray(R.array.main_fragment);
        instance = this;
        mHandler.sendMessageDelayed(mHandler.obtainMessage(1), 1500);
        rongIM = application.getRC();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (null == vp) {
            initView();
            initPage();
            initRC();
        }
        setTitleBar(vp.getCurrentItem());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mckuai.imc.BaseActivity#onDestroy()
     */
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        application.getCache().saveCacheFile();
        application.saveUserInfo();
        super.onDestroy();
    }

    private void initSlidingMenu() {
        menu = new MCSildingMenu();
        int width = getWindowManager().getDefaultDisplay().getWidth();
        width = (int) (width / 3.5);
        mySlidingMenu = new SlidingMenu(this, null);
        mySlidingMenu.setMode(SlidingMenu.LEFT);
        mySlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        mySlidingMenu.setBehindOffsetRes(R.dimen.com_margin);
        mySlidingMenu.setFadeDegree(0.42f);
        mySlidingMenu.setMenu(R.layout.frame_menu);
        mySlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        mySlidingMenu.setBackgroundResource(R.mipmap.background_slidingmenu);
        mySlidingMenu.setBehindOffset(width);
        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, menu).commitAllowingStateLoss();
        mySlidingMenu.setBehindCanvasTransformer(new SlidingMenu.CanvasTransformer() {

            @Override
            public void transformCanvas(Canvas canvas, float percentOpen) {
                // TODO Auto-generated method stub
                float scale = (float) (percentOpen * 0.25 + 0.75);
                canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
            }
        });
        mySlidingMenu.setAboveCanvasTransformer(new SlidingMenu.CanvasTransformer() {
            @Override
            public void transformCanvas(Canvas canvas, float percentOpen) {
                // TODO Auto-generated method stub
                float scale = (float) (1 - percentOpen * 0.25);
                canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
            }
        });
        mySlidingMenu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
            @Override
            public void onOpened() {
                // TODO Auto-generated method stub
                menu.callOnResumeForUpdate();
                menu.showData();
                isMenuShowing = true;
            }
        });
        mySlidingMenu.setOnCloseListener(new SlidingMenu.OnCloseListener() {
            @Override
            public void onClose() {
                // TODO Auto-generated method stub
                isMenuShowing = false;
                if (0 == vp.getCurrentItem()) {
                    menu.callOnPauseForUpdate();
                }
                hideKeyboard(mySlidingMenu);
                Recommend r = (Recommend) list.get(0);
                r.showUser();
            }
        });
    }

    private void checkUpdate() {
        menu.callOnResumeForUpdate();
        menu.checkUpdate(true);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        vp = (ViewPager) findViewById(R.id.pager);
        vp.setOnPageChangeListener(this);
        spinner = (Spinner) findViewById(R.id.sp_menu);
        MyApplication.getInstance().setSpinner(spinner);
        layoutInflater = LayoutInflater.from(this);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.pager);
        // mTabHost.setup(this, getSupportFragmentManager(), R.id.tabcontent);

        mTabHost.setOnTabChangedListener(this);
        btn_right = (ImageButton) findViewById(R.id.btn_right);
        btn_right.setOnClickListener(this);
        Class fragmentArray[] = {Fragment.class, Fragment.class, Fragment.class, Fragment.class};// 用基类是由于这里的不会被显示并且能避免状态变化时的干扰
        for (int i = 0; i < fragmentArray.length; i++) {
            TabSpec tabSpec = mTabHost.newTabSpec(textViewArray[i]).setIndicator(getTabItemView(i));
            // TabSpec tabSpec = mTabHost.newTabSpec(textViewArray[i]);
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
        }
        mTabHost.getTabWidget().setDividerDrawable(null);
        findViewById(R.id.btn_left).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mySlidingMenu.toggle();
            }
        });
    }

    /**
     * 将fragment添加到viewpager
     */
    private void initPage() {
        list.add(new Recommend());
        list.add(new Live());
        list.add(new Chat());
        list.add(new Forums());
        mAdapter = new FragmentAdapter(getSupportFragmentManager(), list);
        vp.setAdapter(mAdapter);
    }

    /**
     * getTabItemView:设置tab标签的文字<br>
     *
     * @param i 标签的序号
     * @return
     */
    private View getTabItemView(int i) {
        View view = layoutInflater.inflate(R.layout.tab_content, null);
        ImageView mImageView = (ImageView) view.findViewById(R.id.tab_imageview);
        TextView mTextView = (TextView) view.findViewById(R.id.tab_textview);
        mImageView.setBackgroundResource(imageViewArray[i]);
        mTextView.setText(textViewArray[i]);
        // if (2 == i)
        // {
        // newMessageView = (View) view.findViewById(R.id.newmessage);
        // }
        return view;
    }

    public void initRC() {
        if (null != rongIM) {
            //application.initRc();
            application.networkEngine.loginToRC(this);
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int position) {
        mTabHost.setCurrentTab(position);
        setTitleBar(position);
    }

    @Override
    public void onTabChanged(String tabId) {
        int position = mTabHost.getCurrentTab();
        vp.setCurrentItem(position);
    }

    private void setTitleBar(int position) {
        if (null == title) {
            title = (TextView) findViewById(R.id.tv_title);
            spinner = (Spinner) findViewById(R.id.sp_menu);
        }
        spinner.setVisibility(View.INVISIBLE);
        btn_right.setVisibility(View.INVISIBLE);
        switch (position) {
            case 1:
                spinner.setVisibility(View.VISIBLE);
                break;
            case 2:
                btn_right.setImageResource(R.mipmap.btn_newchat);
                btn_right.setVisibility(View.VISIBLE);
                // newMessageView.setVisibility(View.GONE);
                break;
            case 3:
                // btn_right.setBackgroundResource(R.drawable.btn_post_publish);
                btn_right.setImageResource(R.mipmap.btn_post_publish);
                btn_right.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }
        title.setText(textViewArray[position]);
    }

    public Spinner getSpinner() {
        return spinner;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * io.rong.imkit.RongIM.OnReceiveMessageListener#onReceived(io.rong.imlib
     * .RongIMClient.Message, int)
     */
    @Override
    public boolean onReceived(Message message, int arg1) {
        if (null != message) {

            return true;
        } else {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_right:
                switch (vp.getCurrentItem()){
                    case 2:
                        Toast.makeText(this,"11111",Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Forums forums = (Forums) list.get(3);
                        if (null != forums.getmForums()) {
                            Intent intent = new Intent(this, PublishPostActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("FORUM_LIST",forums.getmForums() );
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        break;
                }

            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            mySlidingMenu.toggle();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isMenuShowing) {
                mySlidingMenu.toggle();
                return true;
            }
            switch (backKeyClickCount++) {
                case 0:
                    // showNotification("再按一次退出");
                    Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(0), 2000);
                    return true;
                case 1:
                    break;

                default:
                    break;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void toggleMenu() {
        if (null != mySlidingMenu) {
            mySlidingMenu.toggle();
        }
        Recommend r = (Recommend) list.get(0);
        r.showUser();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    backKeyClickCount = 0;
                    break;

                case 1:
                    checkUpdate();
                    break;

                default:
                    MyApplication.getInstance().getCache().saveCacheFile();// 保存缓存中的内容
                    MobclickAgent.onKillProcess(MainActivity.this);// 保存统计信息
                    System.exit(0);
                    break;
            }
        }

        ;
    };

    public void onActivityReenter(int resultCode, android.content.Intent data) {
        if (RESULT_OK == resultCode && !application.isLoginRC()) {
            application.networkEngine.loginToRC(this);
        }
    }

    /*网络变化的监听*/
    @Override
    public void onChanged(ConnectionStatus connectionStatus) {
        switch (connectionStatus) {
            case CONNECTED:
                Toast.makeText(MainActivity.this, "已连接上聊天服务器！", Toast.LENGTH_SHORT).show();
                break;
            case DISCONNECTED:
                Toast.makeText(MainActivity.this, "聊天服务器断开！", Toast.LENGTH_SHORT).show();
                break;
            case CONNECTING:
                Toast.makeText(MainActivity.this, "正在连接...", Toast.LENGTH_SHORT).show();
                break;
            case NETWORK_UNAVAILABLE:
                Toast.makeText(MainActivity.this, "网络不可用！", Toast.LENGTH_SHORT).show();
                break;
            case KICKED_OFFLINE_BY_OTHER_CLIENT:
                Toast.makeText(MainActivity.this, "账号在其它设备上登录！", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    @Override
    public void onSuccess() {
        if (RongIM.getInstance() != null && RongIM.getInstance().getRongIMClient() != null) {
            RongIM.getInstance().getRongIMClient().setConnectionStatusListener(this);
            RongIM.setOnReceiveMessageListener(this);
            RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
                @Override
                public UserInfo getUserInfo(String id) {
                /*    if (null != application.friend && !application.friend.isEmpty()) {
                        for (User user : application.friend) {
                            if (user.getToken().equals(id)) {
                                UserInfo userInfo = new UserInfo(user.getToken(), user.getNike(), Uri.parse(user.getHeadImg()));
                                return userInfo;
                            }
                        }
                    }*/
                    return null;
                }
            }, true);
        }
    }

    @Override
    public void onFalse(String msg) {

    }
}
