# XAsync
XAsync,kit for android aysnc development


不创建ViewRootImpl在子线程更新UI
我们OnCreate()里直接开启一个子线程去更新UI，并没有创建单独的ViewRootImpl对象啊？
原因就在于ViewRootImpl的建立时间，它是在ActivityThread.Java的final void handleResumeActivity(IBinder token, boolean clearHide, boolean isForward)里创建的。

~~~
final void handleResumeActivity(IBinder token,
            boolean clearHide, boolean isForward, boolean reallyResume, int seq, String reason) {
        ······
        if (r != null) {
            final Activity a = r.activity;

            if (localLOGV) Slog.v(
                TAG, "Resume " + r + " started activity: " +
                a.mStartedActivity + ", hideForNow: " + r.hideForNow
                + ", finished: " + a.mFinished);

            final int forwardBit = isForward ?
                    WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION : 0;


            boolean willBeVisible = !a.mStartedActivity;
            if (!willBeVisible) {
                try {
                    willBeVisible = ActivityManagerNative.getDefault().willActivityBeVisible(
                            a.getActivityToken());
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
            if (r.window == null && !a.mFinished && willBeVisible) {
                r.window = r.activity.getWindow();
                View decor = r.window.getDecorView();
                decor.setVisibility(View.INVISIBLE);
                ViewManager wm = a.getWindowManager();
                WindowManager.LayoutParams l = r.window.getAttributes();
                a.mDecor = decor;
                l.type = WindowManager.LayoutParams.TYPE_BASE_APPLICATION;
                l.softInputMode |= forwardBit;
                if (r.mPreserveWindow) {
                    a.mWindowAdded = true;
                    r.mPreserveWindow = false;
                    ViewRootImpl impl = decor.getViewRootImpl();
                    if (impl != null) {
                        impl.notifyChildRebuilt();
                    }
                }
             }
                ······
 }

~~~

原因就是在Activity的onResume之前ViewRootImpl实例没有建立，所以没有ViewRootImpl.checkThread检查。而text.setText时设定的文本却保留了下来，所以当ViewRootImpl真正去刷新界面时，就把"不是通过UI线程"刷了出来！
