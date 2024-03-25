
package com.xunlei.download;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;

import com.xunlei.downloadlib.XLDownloadManager;
import com.xunlei.downloadlib.android.XLTool;
import com.xunlei.downloadlib.parameter.BtIndexSet;
import com.xunlei.downloadlib.parameter.BtSubTaskDetail;
import com.xunlei.downloadlib.parameter.BtTaskParam;
import com.xunlei.downloadlib.parameter.EmuleTaskParam;
import com.xunlei.downloadlib.parameter.GetFileName;
import com.xunlei.downloadlib.parameter.GetTaskId;
import com.xunlei.downloadlib.parameter.InitParam;
import com.xunlei.downloadlib.parameter.MagnetTaskParam;
import com.xunlei.downloadlib.parameter.P2spTaskParam;
import com.xunlei.downloadlib.parameter.TorrentFileInfo;
import com.xunlei.downloadlib.parameter.TorrentInfo;
import com.xunlei.downloadlib.parameter.UploadInfo;
import com.xunlei.downloadlib.parameter.XLTaskInfo;
import com.xunlei.downloadlib.parameter.XLTaskLocalUrl;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by oceanzhang on 2017/7/27.
 *
 * @noinspection ALL
 */
public class XLTaskHelper {
    private static final String TAG = "XLTaskHelper";

    public static void init(Context context) {
        XLDownloadManager instance = XLDownloadManager.getInstance();
        InitParam initParam = new InitParam();
        initParam.mAppVersion = "1.04.0.2084";
        initParam.mStatSavePath = context.getFilesDir().getPath();
        initParam.mStatCfgSavePath = context.getFilesDir().getPath();
        initParam.mLogSavePath = context.getFilesDir().getPath();
        initParam.mPermissionLevel = 3;
        initParam.mGuid = XLTool.getFetchGuid();
        initParam.mPeerId = XLTool.getFetchPeerId();
        instance.init(context, initParam);
        instance.setOSVersion(Build.VERSION.INCREMENTAL);
        instance.setSpeedLimit(-1, -1);
        instance.setVipType("1");
        instance.setUserId("2000017962");

        UploadInfo uploadInfo = new UploadInfo();
        uploadInfo.uploadIntervalStart = 671081577;
        instance.setUploadInfo(uploadInfo);
        instance.setUploadSwitch(false);
    }

    private AtomicInteger seq = new AtomicInteger(0);

    private XLTaskHelper() {
    }

    private static volatile XLTaskHelper instance = null;

    public static XLTaskHelper instance() {
        if (instance == null) {
            synchronized (XLTaskHelper.class) {
                if (instance == null) {
                    instance = new XLTaskHelper();
                }
            }

        }
        return instance;
    }


    /**
     * 添加迅雷链接任务 支持thunder:// ftp:// ed2k:// http:// https:// 协议
     *
     * @param url
     * @param savePath 下载文件保存路径
     * @param fileName 下载文件名 可以通过 getFileName(url) 获取到,为空默认为getFileName(url)的值
     * @return
     */
    public synchronized long addThunderTask(String url, String savePath, String fileName) {
        if (url.startsWith("thunder://"))
            url = XLDownloadManager.getInstance().parserThunderUrl(url);
        final GetTaskId getTaskId = new GetTaskId();
        if (TextUtils.isEmpty(fileName)) {
            GetFileName getFileName = new GetFileName();
            XLDownloadManager.getInstance().getFileNameFromUrl(url, getFileName);
            fileName = getFileName.getFileName();
        }
        if (url.startsWith("ftp://") || url.startsWith("http://") || url.startsWith("https://")) {
            P2spTaskParam taskParam = new P2spTaskParam();
            taskParam.setCreateMode(1);
            taskParam.setFileName(fileName);
            taskParam.setFilePath(savePath);
            taskParam.setUrl(url);
            taskParam.setSeqId(seq.incrementAndGet());
            taskParam.setCookie("");
            taskParam.setRefUrl("");
            taskParam.setUser("");
            taskParam.setPass("");
            XLDownloadManager.getInstance().createP2spTask(taskParam, getTaskId);
        } else if (url.startsWith("ed2k://")) {
            EmuleTaskParam taskParam = new EmuleTaskParam();
            taskParam.setFilePath(savePath);
            taskParam.setFileName(fileName);
            taskParam.setUrl(url);
            taskParam.setSeqId(seq.incrementAndGet());
            taskParam.setCreateMode(1);
            XLDownloadManager.getInstance().createEmuleTask(taskParam, getTaskId);
        }

        XLDownloadManager.getInstance().setDownloadTaskOrigin(getTaskId.getTaskId(), "out_app/out_app_paste");
        XLDownloadManager.getInstance().setOriginUserAgent(getTaskId.getTaskId(), "AndroidDownloadManager/4.4.4 (Linux; U; Android 4.4.4; Build/KTU84Q)");
        XLDownloadManager.getInstance().startTask(getTaskId.getTaskId(), false);
        XLDownloadManager.getInstance().setTaskLxState(getTaskId.getTaskId(), 0, 1);
        XLDownloadManager.getInstance().startDcdn(getTaskId.getTaskId(), 0, "", "", "");
        return getTaskId.getTaskId();
    }

    /**
     * 通过链接获取文件名
     *
     * @param url
     * @return
     */
    public synchronized String getFileName(String url) {
        if (url.startsWith("thunder://"))
            url = XLDownloadManager.getInstance().parserThunderUrl(url);
        GetFileName getFileName = new GetFileName();
        XLDownloadManager.getInstance().getFileNameFromUrl(url, getFileName);
        return getFileName.getFileName();
    }

    /**
     * 添加磁力链任务
     *
     * @param url      磁力链接 magnet:? 开头
     * @param savePath
     * @param fileName
     * @return
     * @throws Exception
     */
    public synchronized long addMagentTask(final String url, final String savePath, String fileName) throws Exception {
        if (url.startsWith("magnet:?")) {
            if (TextUtils.isEmpty(fileName)) {
                final GetFileName getFileName = new GetFileName();
                XLDownloadManager.getInstance().getFileNameFromUrl(url, getFileName);
                fileName = getFileName.getFileName();
            }
            MagnetTaskParam magnetTaskParam = new MagnetTaskParam();
            magnetTaskParam.setFileName(fileName);
            magnetTaskParam.setFilePath(savePath);
            magnetTaskParam.setUrl(url);
            final GetTaskId getTaskId = new GetTaskId();
            XLDownloadManager.getInstance().createBtMagnetTask(magnetTaskParam, getTaskId);

            XLDownloadManager.getInstance().setTaskLxState(getTaskId.getTaskId(), 0, 2);
            XLDownloadManager.getInstance().startDcdn(getTaskId.getTaskId(), 0, "", "", "");
            XLDownloadManager.getInstance().startTask(getTaskId.getTaskId(), false);
            return getTaskId.getTaskId();
        } else {
            throw new Exception("url illegal.");
        }
    }

    /**
     * 获取种子详情
     *
     * @param torrentPath
     * @return
     */
    public synchronized TorrentInfo getTorrentInfo(String torrentPath) {
        TorrentInfo torrentInfo = new TorrentInfo();
        XLDownloadManager.getInstance().getTorrentInfo(torrentPath, torrentInfo);
        return torrentInfo;
    }

    /**
     * 添加种子下载任务,如果是磁力链需要先通过addMagentTask将种子下载下来
     *
     * @param torrentPath 种子地址
     * @param savePath    保存路径
     * @param indexs      需要下载的文件索引
     * @return
     * @throws Exception
     */
    public synchronized long addTorrentTask(String torrentPath, String savePath, int[] indexs) throws Exception {
        TorrentInfo torrentInfo = new TorrentInfo();
        XLDownloadManager.getInstance().getTorrentInfo(torrentPath, torrentInfo);
        TorrentFileInfo[] fileInfos = torrentInfo.mSubFileInfo;
        BtTaskParam taskParam = new BtTaskParam();
        taskParam.setCreateMode(1);
        taskParam.setFilePath(savePath);
        taskParam.setMaxConcurrent(10);
        taskParam.setSeqId(seq.incrementAndGet());
        taskParam.setTorrentPath(torrentPath);
        GetTaskId getTaskId = new GetTaskId();
        XLDownloadManager.getInstance().createBtTask(taskParam, getTaskId);
        if (fileInfos.length > 1 && indexs != null && indexs.length > 0) {
            BtIndexSet btIndexSet = new BtIndexSet(indexs.length);
            int i = 0;
            for (int index : indexs) {
                btIndexSet.mIndexSet[i++] = index;
            }
            XLDownloadManager.getInstance().selectBtSubTask(getTaskId.getTaskId(), btIndexSet);
        }
        XLDownloadManager.getInstance().setTaskLxState(getTaskId.getTaskId(), 0, 1);
//        XLDownloadManager.getInstance().startDcdn(getTaskId.getTaskId(), currentFileInfo.mRealIndex, "", "", "");
        XLDownloadManager.getInstance().startTask(getTaskId.getTaskId(), false);
//        XLDownloadManager.getInstance().setBtPriorSubTask(getTaskId.getTaskId(),currentFileInfo.mRealIndex);
//        XLTaskLocalUrl localUrl = new XLTaskLocalUrl();
//        XLDownloadManager.getInstance().getLocalUrl(savePath+"/" +(TextUtils.isEmpty(currentFileInfo.mSubPath) ? "" : currentFileInfo.mSubPath+"/")+ currentFileInfo.mFileName,localUrl);
//        currentFileInfo.playUrl = localUrl.mStrUrl;
//        currentFileInfo.hash = torrentInfo.mInfoHash;
//        return currentFileInfo;
        return getTaskId.getTaskId();
    }

    /**
     * 获取某个文件的本地proxy url,如果是音视频文件可以实现变下边播
     *
     * @param filePath
     * @return
     */
    public synchronized String getLoclUrl(String filePath) {
        XLTaskLocalUrl localUrl = new XLTaskLocalUrl();
        XLDownloadManager.getInstance().getLocalUrl(filePath, localUrl);
        return localUrl.mStrUrl;
    }

    /**
     * 删除一个任务，会把文件也删掉
     *
     * @param taskId
     * @param savePath
     */
    public synchronized void deleteTask(long taskId, final String savePath) {
        stopTask(taskId);
        new Handler(Daemon.looper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    new LinuxFileCommand(Runtime.getRuntime()).deleteDirectory(savePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 停止任务 文件保留
     *
     * @param taskId
     */
    public synchronized void stopTask(long taskId) {
        XLDownloadManager.getInstance().stopTask(taskId);
        XLDownloadManager.getInstance().releaseTask(taskId);
    }

    /**
     * 获取任务详情， 包含当前状态，下载进度，下载速度，文件大小
     * mDownloadSize:已下载大小  mDownloadSpeed:下载速度 mFileSize:文件总大小 mTaskStatus:当前状态，0连接中1下载中 2下载完成 3失败 mAdditionalResDCDNSpeed DCDN加速 速度
     *
     * @param taskId
     * @return
     */
    public synchronized XLTaskInfo getTaskInfo(long taskId) {
        XLTaskInfo taskInfo = new XLTaskInfo();
        XLDownloadManager.getInstance().getTaskInfo(taskId, 1, taskInfo);
        return taskInfo;
    }

    /**
     * 获取种子文件子任务的详情
     *
     * @param taskId
     * @param fileIndex
     * @return
     */
    public synchronized BtSubTaskDetail getBtSubTaskInfo(long taskId, int fileIndex) {
        BtSubTaskDetail subTaskDetail = new BtSubTaskDetail();
        XLDownloadManager.getInstance().getBtSubTaskInfo(taskId, fileIndex, subTaskDetail);
        return subTaskDetail;
    }

    /**
     * 开启dcdn加速
     *
     * @param taskId
     * @param btFileIndex
     */
    public synchronized void startDcdn(long taskId, int btFileIndex) {
        XLDownloadManager.getInstance().startDcdn(taskId, btFileIndex, "", "", "");
    }

    /**
     * 停止dcdn加速
     *
     * @param taskId
     * @param btFileIndex
     */
    public synchronized void stopDcdn(long taskId, int btFileIndex) {
        XLDownloadManager.getInstance().stopDcdn(taskId, btFileIndex);
    }
}