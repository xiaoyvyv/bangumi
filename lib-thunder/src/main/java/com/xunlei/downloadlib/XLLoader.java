package com.xunlei.downloadlib;

import android.content.Context;

import androidx.annotation.Keep;

import com.xunlei.downloadlib.parameter.BtIndexSet;
import com.xunlei.downloadlib.parameter.BtSubTaskDetail;
import com.xunlei.downloadlib.parameter.BtTaskStatus;
import com.xunlei.downloadlib.parameter.DcdnPeerResParam;
import com.xunlei.downloadlib.parameter.GetBooleanParam;
import com.xunlei.downloadlib.parameter.GetDownloadHead;
import com.xunlei.downloadlib.parameter.GetDownloadLibVersion;
import com.xunlei.downloadlib.parameter.GetFileName;
import com.xunlei.downloadlib.parameter.GetTaskId;
import com.xunlei.downloadlib.parameter.MaxDownloadSpeedParam;
import com.xunlei.downloadlib.parameter.ThunderUrlInfo;
import com.xunlei.downloadlib.parameter.TorrentInfo;
import com.xunlei.downloadlib.parameter.UploadControlParam;
import com.xunlei.downloadlib.parameter.UploadInfo;
import com.xunlei.downloadlib.parameter.UrlQuickInfo;
import com.xunlei.downloadlib.parameter.XLConstant;
import com.xunlei.downloadlib.parameter.XLFirstMediaState;
import com.xunlei.downloadlib.parameter.XLPremiumResInfo;
import com.xunlei.downloadlib.parameter.XLRangeInfo;
import com.xunlei.downloadlib.parameter.XLSessionInfo;
import com.xunlei.downloadlib.parameter.XLTaskInfo;
import com.xunlei.downloadlib.parameter.XLTaskInfoEx;
import com.xunlei.downloadlib.parameter.XLTaskLocalUrl;

/**
 * XLLoader 下载 JNI 封装
 *
 * @noinspection ALL
 */
@Keep
public class XLLoader {

    static {
        System.loadLibrary("thunder");
    }

    public native int addBatchDcdnPeerRes(long taskId, int i, long l, DcdnPeerResParam[] dcdnPeerResParamArr);

    public native int addBtTrackerNodes(long taskId, String tracker);

    public native int addPeerResource(long taskId, String peerId, long userId, String jmpKey, String vipCdnAuth, int internalIp, int tcpPort, int udpPort, int resLevel, int resPriority, int capabilityFlag, int resType);

    public native int addServerResource(long taskId, int subIndex, String url, String refUrl, String cookie, int resType, int strategy, int comeFrom);

    public native int btAddPeerResource(long taskId, int subIndex, String peerId, long userId, String jmpKey, String vipCdnAuth, int internalIp, int tcpPort, int udpPort, int resLevel, int resPriority, int capabilityFlag, int resType);

    public native int btRemoveAddedResource(long taskId, int subIndex, int i11);

    public native int changeOriginRes(long taskId, String url);

    public native int clearTaskFile(String filePath);

    public native int createBtMagnetTask(String url, String filePath, String fileName, GetTaskId getTaskId);

    public native int createBtTask(String torrentPath, String filePath, int maxConcurrent, int createMode, int seqId, GetTaskId getTaskId);

    public native int createCDNTask(String url, String refUrl, String cookie, String user, String pass, String filePath, String fileName, int createMode, int seqId, GetTaskId getTaskId);

    public native int createCIDTask(String cid, String gCid, String bCid, String filePath, String fileName, long fileSize, int createMode, int seqId, GetTaskId getTaskId);

    public native int createEmuleTask(String url, String filePath, String fileName, int createMode, int seqId, GetTaskId getTaskId);

    public native int createHLSTask(String url, String refUrl, String cookie, String user, String pass, String filePath, String fileName, long bandwidth, int maxConcurrent, int createMode, int seqId, GetTaskId getTaskId);

    public native int createP2spTask(String url, String refUrl, String cookie, String user, String pass, String filePath, String fileName, int createMode, int seqId, GetTaskId getTaskId);

    public native int createShortVideoTask(String url, String filePath, String fileName, String title, int createMode, int seqId, int i, GetTaskId getTaskId);

    public native int createVodTask(String url, String refUrl, String cookie, String user, String pass, String filePath, String fileName, int createMode, int seqId, int i12, GetTaskId getTaskId);

    public native int deselectBtSubTask(long taskId, BtIndexSet btIndexSet);

    public native int enterPrefetchMode(long taskId);

    public native int getBtSubTaskInfo(long taskId, int subIndex, BtSubTaskDetail btSubTaskDetail);

    public native int getBtSubTaskStatus(long taskId, BtTaskStatus btTaskStatus, int i1, int count);

    public native int getDownloadHeader(long taskId, GetDownloadHead getDownloadHead);

    public native int getDownloadLibVersion(GetDownloadLibVersion getDownloadLibVersion);

    public native int getDownloadRangeInfo(long taskId, int subIndex, XLRangeInfo xLRangeInfo);

    public native int getFileNameFromUrl(String url, GetFileName getFileName);

    public native int getFirstMediaState(long taskId, int subIndex, XLFirstMediaState xLFirstMediaState);

    public native int getLocalUrl(String filePath, XLTaskLocalUrl xLTaskLocalUrl);

    public native int getMaxDownloadSpeed(MaxDownloadSpeedParam maxDownloadSpeedParam);

    public native int getNameFromUrl(String url, String defaultName);

    public native int getPremiumResInfo(long taskId, int subIndex, XLPremiumResInfo xLPremiumResInfo);

    public native int getSessionInfoByUrl(String url, XLSessionInfo xLSessionInfo);

    public native int getSettingBoolean(String str, String str2, GetBooleanParam getBooleanParam, boolean z10);

    public native int getTaskInfo(long taskId, int subIndex, XLTaskInfo xLTaskInfo);

    public native int getTaskInfoEx(long taskId, XLTaskInfoEx xLTaskInfoEx);

    public native int getTorrentInfo(String filePath, TorrentInfo torrentInfo);

    public native int getUploadInfo(UploadInfo uploadInfo);

    public native int getUrlQuickInfo(long taskId, UrlQuickInfo urlQuickInfo);

    public native int init(Context context, String appVersion, String str2, String peerId, String guid, String statSavePath, String statCfgSavePath, String logSavePath, int netType, int permissionLevel);

    public native boolean isLogTurnOn();

    public native int notifyNetWorkType(int networkType);

    public native int notifyUploadFileChanged(String str, String str2, long j);

    public native int parserThunderUrl(String url, ThunderUrlInfo thunderUrlInfo);

    public native int playShortVideoBegin(long taskId);

    public native int releaseTask(long taskId);

    public native int removeAccelerateToken(long taskId, int subIndex);

    public native int removeAddedResource(long taskId, int subIndex);

    public native int removeAddedServerResource(long taskId, int subIndex);

    public native int requeryIndex(long taskId);

    public native int resetUploadInfo();

    public native int selectBtSubTask(long taskId, BtIndexSet btIndexSet);

    public native int setAccelerateToken(long taskId, int subIndex, long appTaskId, int accelerateType, String token);

    public native int setBtPriorSubTask(long taskId, int fileIndex);

    public native int setBtSwitch(@XLConstant.BtSwitch int btSwitch);

    public native int setCandidateResSpeed(long taskId, int candidateResSpeed);

    public native int setDownloadTaskOrigin(long taskId, String origin);

    public native int setEmuleSwitch(@XLConstant.EmuleSwitch int emuleSwitch);

    public native int setFileName(long taskId, String fileName);

    public native int setHttpHeaderProperty(long taskId, String name, String value);

    public native int setImei(String imei);

    public native int setIndexInfo(long taskId, String cid, String gCid, String bCid, long fileSize, int gCidLevel);

    public native int setLocalHostResolve(String hostInfo, String infoFormat);

    public native int setLocalProperty(String name, String value);

    public native int setMac(String mac);

    public native int setMiUiVersion(String miuiVersion);

    public native int setNotifyNetWorkCarrier(int carrier);

    public native int setNotifyWifiBSSID(String wifiSSID);

    public native int setOriginUserAgent(long taskId, String userAgent);

    public native int setPlayerMode(long taskId, int mode);

    public native int setReleaseLog(int i, String str, int i11, int i12);

    public native int setSlowAccelerateSpeed(long taskId, long speed);

    public native int setSpeedLimit(long download, long upload);

    public native int setStatReportSwitch(boolean statSwitch);

    public native int setSubTaskConcurrency(long taskId, int concurrency);

    public native int setTaskAllowUseResource(long taskId, int resourceType);

    public native int setTaskAppInfo(long taskId, String str, String str2, String str3);

    public native int setTaskGsState(long taskId, int subIndex, int en);

    public native int setTaskLxState(long taskId, int subIndex, int en);

    public native int setTaskSpeedLimit(long taskId, long speedLimit);

    public native int setTaskUid(long taskId, int uid);

    public native int setUploadControlParam(UploadControlParam uploadControlParam);

    public native int setUploadInfo(UploadInfo uploadInfo);

    public native int setUploadSwitch(boolean enable);

    public native int setUserId(String userId);

    public native int setVipType(String vipType);

    public native int startDcdn(long taskId, int subIndex, String sessionId, String productType, String verifyInfo);

    public native int startTask(long taskId, boolean z10);

    public native int statExternalInfo(long taskId, int fileIndex, String key, String value);

    public native int statExternalInfoU64(long taskId, int fileIndex, String key, long value, int statType);

    public native int stopDcdn(long taskId, int subIndex);

    public native int stopTask(long taskId);

    public native int stopTaskWithReason(long taskId, int status);

    public native int switchOriginToAllResDownload(long l);

    public native int unInit();
}
