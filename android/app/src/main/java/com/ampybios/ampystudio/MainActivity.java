package com.ampybios.ampystudio;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Display;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.webkit.*;
import android.widget.FrameLayout;
import android.widget.Toast;
import java.io.*;
import java.nio.charset.StandardCharsets;

public final class MainActivity extends Activity {
    private static final String ORIGIN = "https://appassets.androidplatform.net/";
    private static final int OPEN = 20, SAVE = 21;
    private WebView web;
    private ValueCallback<Uri[]> openCallback;
    private String pendingSave;
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(9,19,22));
        getWindow().setNavigationBarColor(Color.rgb(9,19,22));
        preferHighRefresh();
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.rgb(9,19,22));
        web = new WebView(this);
        root.addView(web, new FrameLayout.LayoutParams(-1,-1));
        setContentView(root);
        root.setOnApplyWindowInsetsListener((v,insets) -> {
            if (android.os.Build.VERSION.SDK_INT >= 30) {
                android.graphics.Insets safe = insets.getInsets(WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout() | WindowInsets.Type.ime());
                v.setPadding(safe.left,safe.top,safe.right,safe.bottom);
            } else {
                v.setPadding(insets.getSystemWindowInsetLeft(),insets.getSystemWindowInsetTop(),insets.getSystemWindowInsetRight(),insets.getSystemWindowInsetBottom());
            }
            return WindowInsets.CONSUMED;
        });
        root.requestApplyInsets();
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true);
        s.setAllowFileAccess(false); s.setAllowContentAccess(false);
        s.setAllowFileAccessFromFileURLs(false); s.setAllowUniversalAccessFromFileURLs(false);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        s.setSupportZoom(false);
        web.setBackgroundColor(Color.rgb(9,19,22));
        web.addJavascriptInterface(new LocalFiles(), "AmpyAndroid");
        web.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView w, WebResourceRequest r) { return true; }
            @Override public WebResourceResponse shouldInterceptRequest(WebView w, WebResourceRequest r) {
                Uri u=r.getUrl();
                if ("https".equals(u.getScheme()) && "appassets.androidplatform.net".equals(u.getHost()) && "/index.html".equals(u.getPath())) {
                    try { return new WebResourceResponse("text/html","UTF-8",getAssets().open("index.html")); } catch(IOException ignored) {}
                }
                return new WebResourceResponse("text/plain","UTF-8",403,"Blocked",java.util.Collections.emptyMap(),new ByteArrayInputStream(new byte[0]));
            }
            @Override public void onPageFinished(WebView w,String url) {
                if (!url.equals(ORIGIN+"index.html")) return;
                try (InputStream in=getAssets().open("mobile.js")) {
                    ByteArrayOutputStream bytes=new ByteArrayOutputStream();
                    byte[] buf=new byte[4096]; int n; while((n=in.read(buf))!=-1) bytes.write(buf,0,n);
                    w.evaluateJavascript(new String(bytes.toByteArray(),StandardCharsets.UTF_8),null);
                } catch(IOException e) { toast("Mobile UI unavailable"); }
            }
        });
        web.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onShowFileChooser(WebView w, ValueCallback<Uri[]> callback, FileChooserParams params) {
                if(openCallback!=null) openCallback.onReceiveValue(null);
                openCallback=callback;
                Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE).setType("*/*");
                try { startActivityForResult(i,OPEN); } catch(Exception e) { openCallback.onReceiveValue(null); openCallback=null; }
                return true;
            }
        });
        web.loadUrl(ORIGIN+"index.html");
    }
    private void preferHighRefresh() {
        Display display=getWindowManager().getDefaultDisplay();
        Display.Mode current=display.getMode(),best=current;
        for(Display.Mode m:display.getSupportedModes()) if(m.getPhysicalWidth()==current.getPhysicalWidth() && m.getPhysicalHeight()==current.getPhysicalHeight() && m.getRefreshRate()>best.getRefreshRate()) best=m;
        WindowManager.LayoutParams a=getWindow().getAttributes(); a.preferredDisplayModeId=best.getModeId(); getWindow().setAttributes(a);
    }
    private void toast(String text){ Toast.makeText(this,text,Toast.LENGTH_SHORT).show(); }
    final class LocalFiles {
        @JavascriptInterface public void save(String text,String name) {
            if(text==null || text.length()>8000000 || !text.trim().startsWith("<svg")) return;
            runOnUiThread(()->{
                if(pendingSave!=null){toast("Save already in progress");return;}
                pendingSave=text;
                String filename=name!=null && name.endsWith(".svg")?"AmpyProject.svg":"AmpyProject.jsvg";
                Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE)
                    .setType(filename.endsWith(".svg")?"image/svg+xml":"application/octet-stream").putExtra(Intent.EXTRA_TITLE,filename);
                try {startActivityForResult(i,SAVE);} catch(Exception ex){pendingSave=null;toast("Cannot open save picker");}
            });
        }
    }
    @Override protected void onActivityResult(int request,int result,Intent data){
        super.onActivityResult(request,result,data);
        Uri uri=result==RESULT_OK && data!=null?data.getData():null;
        if(request==OPEN && openCallback!=null){
            openCallback.onReceiveValue(uri==null?null:new Uri[]{uri});openCallback=null;
        }
        if(request==SAVE){
            String content=pendingSave;pendingSave=null;
            if(uri!=null && content!=null){
                try(OutputStream out=getContentResolver().openOutputStream(uri,"wt")){
                    if(out==null)throw new IOException("No stream");out.write(content.getBytes(StandardCharsets.UTF_8));toast("Project saved");
                }catch(Exception e){toast("Save failed: "+e.getClass().getSimpleName());}
            }
        }
    }
    @Override protected void onPause(){super.onPause();if(web!=null){web.onPause();web.pauseTimers();}}
    @Override protected void onResume(){super.onResume();if(web!=null){web.onResume();web.resumeTimers();}}
    @Override protected void onDestroy(){if(openCallback!=null)openCallback.onReceiveValue(null);if(web!=null){web.removeJavascriptInterface("AmpyAndroid");web.destroy();}super.onDestroy();}
}
