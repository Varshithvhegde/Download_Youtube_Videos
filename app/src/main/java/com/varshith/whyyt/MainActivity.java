package com.varshith.whyyt;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class MainActivity extends AppCompatActivity {
    EditText url;
    ProgressDialog dialog;
    String videoUrl;
    String yUri3 = "https://youtu.be/";
    String yUri4 = "https://www.youtube.com/watch?v=";
    String yUri1 = "https://m.youtube.com/watch?v=";
    String yUri2 = "https://www.youtube.com/shorts/";
    String yUri5 = "https://youtube.com/shorts/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        url = findViewById(R.id.url);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading...");
        dialog.setMessage("Please Wait....");
        dialog.setCanceledOnTouchOutside(false);
        url.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                url.performClick();
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);
                return true;
            }
            return false;
        });
    }


    public void download(View view) {
        videoUrl = url.getText().toString();
        if (videoUrl.isEmpty()) {
            toast("Please Enter Url");
        } else if (videoUrl.contains(yUri2)) {
            extactDownload(replaceString(yUri2));
        } else if (videoUrl.contains(yUri5)) {
            extactDownload(replaceString(yUri5));
        } else if (videoUrl.contains(yUri1)) {
            extactDownload(replaceString(yUri1));
        } else if (videoUrl.contains(yUri3)) {
            extactDownload(replaceString(yUri3));
        } else if (videoUrl.contains(yUri4)) {
            extactDownload(videoUrl);
        } else {
            dialog.dismiss();
            toast("Invalid Url");
        }

    }

    public String replaceString(String a) {
        String f = "?feature=share";
        return yUri4 + videoUrl.replace(f, "").replace(a, "");
    }

    @SuppressLint("StaticFieldLeak")
    private void extactDownload(String videoUrl) {
        Log.d("hello", videoUrl);
        dialog.show();
        new YouTubeExtractor(this) {
            @Override
            protected void onExtractionComplete(@Nullable SparseArray<YtFile> ytFiles, @Nullable VideoMeta videoMeta) {
                if (ytFiles != null) {
                    for (int i = 0, tag; i < ytFiles.size(); i++) {
                        tag = ytFiles.keyAt(i);
                        String title = Objects.requireNonNull(videoMeta).getTitle();
                        String u = ytFiles.get(tag).getUrl();
                        if (u != null) {
                            setDownloadUrl(u, title);
                            toast("Start Downloading...");
                            return;
                        }
                    }
                } else {
                    dialog.dismiss();
                    toast("Please try again...");
                }
            }
        }.extract(videoUrl);
    }

    public void emptyUrl() {
        url.setText("");
    }

    public void setDownloadUrl(String uri, String videoTitle) {
        Uri uri1 = Uri.parse(uri);
        Log.d("hello", uri1.toString());
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(uri));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setTitle(videoTitle);
        request.setVisibleInDownloadsUi(true);
        request.allowScanningByMediaScanner();
        request.setShowRunningNotification(true);
        request.setAllowedOverRoaming(true);
        request.setAllowedOverMetered(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                videoTitle + ".mp4");
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
        dialog.dismiss();
        emptyUrl();
    }

    public void clean(View view) {
        emptyUrl();
    }

    public void toast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog builder = new AlertDialog.Builder(this).setNegativeButton("No", (dialog, which) -> dialog.dismiss()).setPositiveButton("Yes", (dialog, which) -> finish()).setTitle("Alert").setMessage("Are you sure you can exit").create();
        builder.show();
    }


    public void paste(View view) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        CharSequence i = clipboardManager.getText();
        url.setText(i);
    }
}