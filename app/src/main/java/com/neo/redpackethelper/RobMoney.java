package com.neo.redpackethelper;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

public class RobMoney extends AccessibilityService {
    public static final String TAG = "RobMoney";
    private List<AccessibilityNodeInfo> infos = new ArrayList<>();
    public RobMoney() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"service:started");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        switch (eventType) {
            //监听通知栏消息
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                List<CharSequence> texts = event.getText();
                if (!texts.isEmpty()) {
                   for (CharSequence text : texts) {
                       String content = text.toString();
                       Log.i(TAG,"text:"+content);
                       if (content.contains("[微信红包]")) {
                           //模拟打开通知栏消息
                           if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
                               Notification notification = (Notification) event.getParcelableData();
                               PendingIntent pendingIntent = notification.contentIntent;
                               try {
                                   pendingIntent.send();
                               } catch (PendingIntent.CanceledException e) {
                                   e.printStackTrace();
                               }
                           }
                       }
                   }
                }
                break;
            //监听是否进入微信红包消息界面
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String className = event.getClassName().toString();
                Log.i(TAG,"classsName:"+className);
                if (className.equals("com.tencent.mm.ui.LauncherUI")) {

                    Log.i(TAG,"开始抢红包");
                    getPacket();
                } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")) {
                    //打开红包
                    Log.i(TAG,"准备打开红包");
                    openPacket();
                }

                break;
        }
    }

    private void getPacket() {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        recycle(rootNode);
        if(infos.size() > 0){
            AccessibilityNodeInfo nodeInfo = infos.get(infos.size() - 1);
            if(null != nodeInfo.getText() && "领取红包".equals(nodeInfo.getText().toString())){
                AccessibilityNodeInfo parent = nodeInfo.getParent();
                if(null != parent && parent.isClickable()){
                    parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            } else if(null != nodeInfo.getText() && nodeInfo.getText().toString().contains("你领取了")) {
                Log.i(TAG,"没有未领取的红包....");
            }

        }
    }

    private void openPacket() {

        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            for (int i = 0;i<nodeInfo.getChildCount();i++){
                nodeInfo.getChild(i).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
//            List<AccessibilityNodeInfo> list = nodeInfo
//                    .findAccessibilityNodeInfosByText("红包");
//            for (AccessibilityNodeInfo n : list) {
//                n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                Log.i(TAG,"child:"+parent.getChildCount());
//            }
        }
    }

    /**
     * 打印一个节点的结构
     * @param info
     */
    public void recycle(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
            if(info.getText() != null){
                Log.i(TAG, "recycleText:"+info.getText().toString());
                if("领取红包".equals(info.getText().toString())){
                    //需要找到一个可以点击的View
                    Log.i(TAG, "Click"+",isClick:"+info.isClickable());
//                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    infos.add(info);
                    AccessibilityNodeInfo parent = info.getParent();
                    while(parent != null){
                        Log.i(TAG, "parent isClick:"+parent.isClickable());
                        if(parent.isClickable()){
//                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            break;
                        }
                        parent = parent.getParent();
                    }

                } else if(info.getText().toString().contains("你领取了")) {
                    infos.add(info);
                }
            }

        } else {
             for (int i = 0; i < info.getChildCount(); i++) {
                if(info.getChild(i)!=null){
                    recycle(info.getChild(i));
                }
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

//    @Override
//    protected void onServiceConnected() {
//        AccessibilityServiceInfo info = getServiceInfo();
//        //这里可以设置多个包名，监听多个应用
//        info.packageNames = new String[]{"com.tencent.mm"};
//        setServiceInfo(info);
//        super.onServiceConnected();
//    }
}
