@file:Suppress("unused", "SpellCheckingInspection", "MemberVisibilityCanBePrivate")

package com.xunlei.downloadlib

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.text.TextUtils
import com.xunlei.downloadlib.android.XLLog
import com.xunlei.downloadlib.android.XLTool.getNetWorkCarrier
import com.xunlei.downloadlib.android.XLTool.getNetworkType
import com.xunlei.downloadlib.android.XLTool.parseJsonMap
import com.xunlei.downloadlib.parameter.BtIndexSet
import com.xunlei.downloadlib.parameter.BtSubTaskDetail
import com.xunlei.downloadlib.parameter.BtTaskParam
import com.xunlei.downloadlib.parameter.BtTaskStatus
import com.xunlei.downloadlib.parameter.CIDTaskParam
import com.xunlei.downloadlib.parameter.DcdnPeerResParam
import com.xunlei.downloadlib.parameter.EmuleTaskParam
import com.xunlei.downloadlib.parameter.ErrorCodeToMsg
import com.xunlei.downloadlib.parameter.GetBooleanParam
import com.xunlei.downloadlib.parameter.GetDownloadHead
import com.xunlei.downloadlib.parameter.GetDownloadLibVersion
import com.xunlei.downloadlib.parameter.GetFileName
import com.xunlei.downloadlib.parameter.GetTaskId
import com.xunlei.downloadlib.parameter.HLSTaskParam
import com.xunlei.downloadlib.parameter.InitParam
import com.xunlei.downloadlib.parameter.MagnetTaskParam
import com.xunlei.downloadlib.parameter.MaxDownloadSpeedParam
import com.xunlei.downloadlib.parameter.P2spTaskParam
import com.xunlei.downloadlib.parameter.PeerResourceParam
import com.xunlei.downloadlib.parameter.ServerResourceParam
import com.xunlei.downloadlib.parameter.SetIndexInfoParam
import com.xunlei.downloadlib.parameter.ThunderUrlInfo
import com.xunlei.downloadlib.parameter.TorrentInfo
import com.xunlei.downloadlib.parameter.UploadControlParam
import com.xunlei.downloadlib.parameter.UploadInfo
import com.xunlei.downloadlib.parameter.UrlQuickInfo
import com.xunlei.downloadlib.parameter.XLConstant
import com.xunlei.downloadlib.parameter.XLConstant.XLManagerStatus
import com.xunlei.downloadlib.parameter.XLFirstMediaState
import com.xunlei.downloadlib.parameter.XLPremiumResInfo
import com.xunlei.downloadlib.parameter.XLRangeInfo
import com.xunlei.downloadlib.parameter.XLSessionInfo
import com.xunlei.downloadlib.parameter.XLTaskInfo
import com.xunlei.downloadlib.parameter.XLTaskInfoEx
import com.xunlei.downloadlib.parameter.XLTaskLocalUrl
import java.util.Objects
import java.util.concurrent.Executors


/**
 * 下载管理器
 */
class XLDownloadManager private constructor() {
    private lateinit var mContext: Context
    private lateinit var mReceiver: NetworkChangeReceiver

    private val xLLoader by lazy { XLLoader() }

    private val mSingleThreadExecutor = Executors.newSingleThreadExecutor()

    val isLogTurnOn: Boolean
        get() {
            increaseRefCount()
            val turnOn = if (managerStatus != XLManagerStatus.MANAGER_RUNNING) false
            else xLLoader.isLogTurnOn()
            decreaseRefCount()
            return turnOn
        }

    @Synchronized
    fun init(context: Context, initParam: InitParam): Int {
        var code = XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        if (initParam.checkMemberVar()) {
            XLLog.canLog = initParam.mDebug
            mContext = context

            if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
                XLLog.i(TAG, "XLDownloadManager is already init")
                return XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
            }
            code = xLLoader.init(
                Objects.requireNonNull(context),
                Objects.requireNonNull(initParam.mAppVersion),
                "",
                Objects.requireNonNull(initParam.mPeerId),
                Objects.requireNonNull(initParam.mGuid),
                Objects.requireNonNull(initParam.mStatSavePath),
                Objects.requireNonNull(initParam.mStatCfgSavePath),
                Objects.requireNonNull(initParam.mLogSavePath),
                Objects.requireNonNull(getNetworkType(context)),
                Objects.requireNonNull(initParam.mPermissionLevel)
            )

            if (code != XLConstant.XLErrorCode.NO_ERROR) {
                managerStatus = XLManagerStatus.MANAGER_INIT_FAIL
                XLLog.e(TAG, "XLDownloadManager init failed ret = [$code]")
            } else {
                XLLog.d(TAG, "XLDownloadManager init success")

                managerStatus = XLManagerStatus.MANAGER_RUNNING
                doMonitorNetworkChange()
                setLocalProperty("PhoneModel", Build.MODEL)
            }
        }

        return code
    }

    @Synchronized
    fun unInit(): Int {
        var code = XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        if (mRunningRefCount != 0) {
            XLLog.i(TAG, "Some function of XLDownloadManager is running, unInit failed!")
            return XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }

        // 释放
        if (managerStatus != XLManagerStatus.MANAGER_UNINIT) {
            if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
                undoMonitorNetworkChange()
            }
            code = xLLoader.unInit()
            managerStatus = XLManagerStatus.MANAGER_UNINIT
        }
        return code
    }

    @Synchronized
    private fun decreaseRefCount() {
        mRunningRefCount--
    }

    @Synchronized
    private fun increaseRefCount() {
        mRunningRefCount++
    }

    private fun doMonitorNetworkChange() {
        XLLog.d(TAG, "doMonitorNetworkChange()")
        this.mReceiver = NetworkChangeReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        XLLog.d(TAG, "register Receiver")
        mContext.registerReceiver(this.mReceiver, intentFilter)
    }

    private fun undoMonitorNetworkChange() {
        mContext.unregisterReceiver(mReceiver)
    }


    @Suppress("SameParameterValue")
    private fun setLocalProperty(name: String, value: String): Int {
        return if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.setLocalProperty(name, value)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
    }


    fun addBatchDcdnPeerRes(
        taskId: Long,
        i: Int,
        l: Long,
        dcdnPeerResParamArr: Array<DcdnPeerResParam>,
    ): Int {
        increaseRefCount()
        val code = xLLoader.addBatchDcdnPeerRes(taskId, i, l, dcdnPeerResParamArr)
        decreaseRefCount()
        return code
    }

    fun addBtTrackerNodes(taskId: Long, tracker: String): Int {
        return xLLoader.addBtTrackerNodes(taskId, tracker)
    }

    fun addPeerResource(taskId: Long, peerResourceParam: PeerResourceParam): Int {
        var code = XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        if (peerResourceParam.checkMemberVar()) {
            increaseRefCount()
            if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
                code = xLLoader.addPeerResource(
                    taskId,
                    peerResourceParam.mPeerId,
                    peerResourceParam.mUserId,
                    peerResourceParam.mJmpKey,
                    peerResourceParam.mVipCdnAuth,
                    peerResourceParam.mInternalIp,
                    peerResourceParam.mTcpPort,
                    peerResourceParam.mUdpPort,
                    peerResourceParam.mResLevel,
                    peerResourceParam.mResPriority,
                    peerResourceParam.mCapabilityFlag,
                    peerResourceParam.mResType
                )
            }
            decreaseRefCount()
        }
        return code
    }

    fun addServerResource(taskId: Long, subIndex: Int, param: ServerResourceParam): Int {
        var code = XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        if (param.checkMemberVar()) {
            increaseRefCount()
            if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
                code = xLLoader.addServerResource(
                    taskId,
                    subIndex,
                    param.mUrl,
                    param.mRefUrl,
                    param.mCookie,
                    param.mResType,
                    param.mStrategy,
                    param.mComeFrom
                )
            }
            decreaseRefCount()
        }
        return code
    }

    fun btAddPeerResource(taskId: Long, subIndex: Int, peerResourceParam: PeerResourceParam): Int {
        var code = XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        if (peerResourceParam.checkMemberVar()) {
            increaseRefCount()
            if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
                code = xLLoader.btAddPeerResource(
                    taskId,
                    subIndex,
                    peerResourceParam.mPeerId,
                    peerResourceParam.mUserId,
                    peerResourceParam.mJmpKey,
                    peerResourceParam.mVipCdnAuth,
                    peerResourceParam.mInternalIp,
                    peerResourceParam.mTcpPort,
                    peerResourceParam.mUdpPort,
                    peerResourceParam.mResLevel,
                    peerResourceParam.mResPriority,
                    peerResourceParam.mCapabilityFlag,
                    peerResourceParam.mResType
                )
            }
            decreaseRefCount()
        }
        return code
    }

    /**
     * i = 3
     */
    fun btRemoveAddedResource(taskId: Long, subIndex: Int, i: Int): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.btRemoveAddedResource(taskId, subIndex, i)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun changeOriginRes(taskId: Long, url: String): Int {
        var code = XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            runCatching {
                code = xLLoader.changeOriginRes(taskId, url)
            }
        }
        return code
    }

    fun clearTaskFile(filePath: String): Int {
        if (TextUtils.isEmpty(filePath)) {
            return XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        return xLLoader.clearTaskFile(filePath)
    }

    fun createBtMagnetTask(magnetTaskParam: MagnetTaskParam, getTaskId: GetTaskId): Int {
        var code = XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        if (magnetTaskParam.checkMemberVar()) {
            increaseRefCount()
            if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
                code = xLLoader.createBtMagnetTask(
                    magnetTaskParam.mUrl,
                    magnetTaskParam.mFilePath,
                    magnetTaskParam.mFileName,
                    getTaskId
                )
            }
            decreaseRefCount()
        }
        return code
    }

    fun createBtTask(btTaskParam: BtTaskParam, getTaskId: GetTaskId): Int {
        var code = XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        if (btTaskParam.checkMemberVar()) {
            increaseRefCount()
            if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
                code = xLLoader.createBtTask(
                    btTaskParam.mTorrentPath,
                    btTaskParam.mFilePath,
                    btTaskParam.mMaxConcurrent,
                    btTaskParam.mCreateMode,
                    btTaskParam.mSeqId,
                    getTaskId
                )
            }
            decreaseRefCount()
        }
        return code
    }

    fun createCDNTask(p2spTaskParam: P2spTaskParam, getTaskId: GetTaskId): Int {
        var code = XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        if (p2spTaskParam.checkMemberVar()) {
            increaseRefCount()
            if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
                code = xLLoader.createCDNTask(
                    p2spTaskParam.mUrl,
                    p2spTaskParam.mRefUrl,
                    p2spTaskParam.mCookie,
                    p2spTaskParam.mUser,
                    p2spTaskParam.mPass,
                    p2spTaskParam.mFilePath,
                    p2spTaskParam.mFileName,
                    p2spTaskParam.mCreateMode,
                    p2spTaskParam.mSeqId,
                    getTaskId
                )
            }
            decreaseRefCount()
        }
        return code
    }

    fun createCIDTask(cidTaskParam: CIDTaskParam, getTaskId: GetTaskId): Int {
        var code = XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        if (!cidTaskParam.checkMemberVar()) {
            return XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            increaseRefCount()
            code = xLLoader.createCIDTask(
                cidTaskParam.mCid,
                cidTaskParam.mGcid,
                cidTaskParam.mBcid,
                cidTaskParam.mFilePath,
                cidTaskParam.mFileName,
                cidTaskParam.mFileSize,
                cidTaskParam.mCreateMode,
                cidTaskParam.mSeqId,
                getTaskId
            )
            decreaseRefCount()
        }
        return code
    }

    fun createEmuleTask(emuleTaskParam: EmuleTaskParam, getTaskId: GetTaskId): Int {
        var code = XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        if (emuleTaskParam.checkMemberVar()) {
            increaseRefCount()
            if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
                code = xLLoader.createEmuleTask(
                    emuleTaskParam.mUrl,
                    emuleTaskParam.mFilePath,
                    emuleTaskParam.mFileName,
                    emuleTaskParam.mCreateMode,
                    emuleTaskParam.mSeqId,
                    getTaskId
                )
            }
            decreaseRefCount()
        }
        return code
    }

    fun createHLSTask(hlsTaskParam: HLSTaskParam, getTaskId: GetTaskId): Int {
        var code = XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        if (hlsTaskParam.checkMemberVar()) {
            increaseRefCount()
            if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
                code = xLLoader.createHLSTask(
                    hlsTaskParam.mUrl,
                    hlsTaskParam.mRefUrl,
                    hlsTaskParam.mCookie,
                    hlsTaskParam.mUser,
                    hlsTaskParam.mPass,
                    hlsTaskParam.mFilePath,
                    hlsTaskParam.mFileName,
                    hlsTaskParam.mBandwidth,
                    hlsTaskParam.mMaxConcurrent,
                    hlsTaskParam.mCreateMode,
                    hlsTaskParam.mSeqId,
                    getTaskId
                )
            }
            decreaseRefCount()
        }
        return code
    }

    fun createP2spTask(p2spTaskParam: P2spTaskParam, getTaskId: GetTaskId): Int {
        var code = XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        if (p2spTaskParam.checkMemberVar()) {
            increaseRefCount()
            if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
                code = xLLoader.createP2spTask(
                    p2spTaskParam.mUrl,
                    p2spTaskParam.mRefUrl,
                    p2spTaskParam.mCookie,
                    p2spTaskParam.mUser,
                    p2spTaskParam.mPass,
                    p2spTaskParam.mFilePath,
                    p2spTaskParam.mFileName,
                    p2spTaskParam.mCreateMode,
                    p2spTaskParam.mSeqId,
                    getTaskId
                )
            }
            decreaseRefCount()
        }
        return code
    }

    fun createShortVideoTask(
        url: String,
        filePath: String,
        fileName: String,
        title: String,
        createMode: Int,
        seqId: Int,
        i: Int,
        getTaskId: GetTaskId,
    ): Int {
        return xLLoader.createShortVideoTask(
            url,
            filePath,
            fileName,
            title.ifBlank { "Default Title" },
            createMode,
            seqId,
            i,
            getTaskId
        )
    }

    fun createVodTask(p2spTaskParam: P2spTaskParam, i10: Int, getTaskId: GetTaskId): Int {
        var code = XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            runCatching {
                code = xLLoader.createVodTask(
                    p2spTaskParam.mUrl,
                    p2spTaskParam.mRefUrl,
                    p2spTaskParam.mCookie,
                    p2spTaskParam.mUser,
                    p2spTaskParam.mPass,
                    p2spTaskParam.mFilePath,
                    p2spTaskParam.mFileName,
                    p2spTaskParam.mCreateMode,
                    p2spTaskParam.mSeqId,
                    i10,
                    getTaskId
                )
            }
        }
        return code
    }

    fun deselectBtSubTask(taskId: Long, btIndexSet: BtIndexSet): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.deselectBtSubTask(taskId, btIndexSet)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun enterPrefetchMode(taskId: Long): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.enterPrefetchMode(taskId)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun getBtSubTaskInfo(taskId: Long, subIndex: Int, btSubTaskDetail: BtSubTaskDetail): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.getBtSubTaskInfo(taskId, subIndex, btSubTaskDetail)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun getBtSubTaskStatus(taskId: Long, btTaskStatus: BtTaskStatus, i10: Int, count: Int): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.getBtSubTaskStatus(taskId, btTaskStatus, i10, count)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun getDownloadHeader(taskId: Long, getDownloadHead: GetDownloadHead?): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.getDownloadHeader(taskId, getDownloadHead)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun getDownloadLibVersion(getDownloadLibVersion: GetDownloadLibVersion): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.getDownloadLibVersion(getDownloadLibVersion)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun getDownloadRangeInfo(taskId: Long, subIndex: Int, xLRangeInfo: XLRangeInfo): Int {
        return xLLoader.getDownloadRangeInfo(taskId, subIndex, xLRangeInfo)
    }

    fun getErrorCodeMsg(code: Int): String {
        return mErrCodeStringMap[code.toString()]?.toString().orEmpty()
    }

    fun getFileNameFromUrl(url: String, getFileName: GetFileName): Int {
        return xLLoader.getFileNameFromUrl(url, getFileName)
    }

    fun getFirstMediaState(taskId: Long, subIndex: Int, xLFirstMediaState: XLFirstMediaState): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.getFirstMediaState(taskId, subIndex, xLFirstMediaState)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }


    fun getLocalUrl(filePath: String, xLTaskLocalUrl: XLTaskLocalUrl): Int {
        var code = XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        increaseRefCount()
        if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            runCatching {
                code = xLLoader.getLocalUrl(filePath, xLTaskLocalUrl)
            }
            decreaseRefCount()
            return code
        }
        decreaseRefCount()
        return code
    }

    fun getMaxDownloadSpeed(maxDownloadSpeedParam: MaxDownloadSpeedParam): Int {
        return xLLoader.getMaxDownloadSpeed(maxDownloadSpeedParam)
    }

    fun getNameFromUrl(url: String, defaultName: String): Int {
        return xLLoader.getNameFromUrl(url, defaultName)
    }

    fun getPremiumResInfo(taskId: Long, subIndex: Int, xLPremiumResInfo: XLPremiumResInfo?): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.getPremiumResInfo(taskId, subIndex, xLPremiumResInfo)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun getSessionInfoByUrl(url: String, xLSessionInfo: XLSessionInfo): Int {
        return xLLoader.getSessionInfoByUrl(url, xLSessionInfo)
    }

    fun getSettingValue(str: String, str2: String, default: Boolean): Boolean {
        val param = GetBooleanParam()
        val code = xLLoader.getSettingBoolean(str, str2, param, default)
        if (XLConstant.XLErrorCode.NO_ERROR != code) {
            XLLog.e(TAG, "XLDownloadManager::getSettingValue end, ret = [$code]")
            return default
        }
        return param.getValue()
    }

    fun getTaskInfo(taskId: Long, subIndex: Int, xLTaskInfo: XLTaskInfo): Int {
        increaseRefCount()
        val taskInfo = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.getTaskInfo(taskId, subIndex, xLTaskInfo)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return taskInfo
    }

    fun getTaskInfoEx(taskId: Long, xLTaskInfoEx: XLTaskInfoEx): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.getTaskInfoEx(taskId, xLTaskInfoEx)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun getTorrentInfo(str: String, torrentInfo: TorrentInfo): Int {
        increaseRefCount()
        val code = xLLoader.getTorrentInfo(str, torrentInfo)
        decreaseRefCount()
        return code
    }

    fun getUploadInfo(uploadInfo: UploadInfo): Int {
        var code = XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            runCatching {
                code = xLLoader.getUploadInfo(uploadInfo)
            }
        }
        return code
    }

    fun getUrlQuickInfo(taskId: Long, urlQuickInfo: UrlQuickInfo): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.getUrlQuickInfo(taskId, urlQuickInfo)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }


    fun notifyNetWorkInfo(context: Context) {
        val networkTypeComplete = getNetworkType(context)
        XLLog.d(TAG, "NetworkChangeHandlerThread NetWorkType = $networkTypeComplete")
        notifyNetWorkType(networkTypeComplete)
        val netWorkCarrier = getNetWorkCarrier(context)
        XLLog.d(TAG, "NetworkChangeHandlerThread NetWorkCarrier = $netWorkCarrier")
        notifyNetWorkCarrier(netWorkCarrier.ordinal)
    }

    fun notifyNetWorkCarrier(networkCarrier: Int): Int {
        var code = XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            runCatching {
                code = xLLoader.setNotifyNetWorkCarrier(networkCarrier)
            }
        }
        return code
    }

    fun notifyNetWorkType(networkType: Int): Int {
        var code = XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            runCatching {
                code = xLLoader.notifyNetWorkType(networkType)
            }
        }
        return code
    }

    fun notifyUploadFileChanged(str: String, str2: String, j10: Long): Int {
        var code = XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            runCatching {
                code = xLLoader.notifyUploadFileChanged(str, str2, j10)
            }
        }
        return code
    }

    fun notifyWifiBSSID(wifiSSID: String): Int {
        var code = XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            runCatching {
                code = xLLoader.setNotifyWifiBSSID(
                    if ((wifiSSID.isEmpty() || wifiSSID === "<unknown ssid>")) "" else ""
                )
            }
        }
        return code
    }

    fun parserThunderUrl(thunderUrl: String): String {
        val thunderUrlInfo = ThunderUrlInfo()
        xLLoader.parserThunderUrl(thunderUrl, thunderUrlInfo)
        return thunderUrlInfo.mUrl.orEmpty()
    }

    fun playShortVideoBegin(taskId: Long): Int {
        return xLLoader.playShortVideoBegin(taskId)
    }

    fun releaseTask(taskId: Long): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.releaseTask(taskId)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun removeAccelerateToken(taskId: Long, subIndex: Int): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.removeAccelerateToken(taskId, subIndex)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun removeAddedResource(taskId: Long, subIndex: Int): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.removeAddedResource(taskId, subIndex)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun removeServerResource(taskId: Long, subIndex: Int): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.removeAddedServerResource(taskId, subIndex)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun requeryTaskIndex(taskId: Long): Int {
        return if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.requeryIndex(taskId)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
    }

    fun resetUploadInfo(): Int {
        var code = XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            runCatching {
                code = xLLoader.resetUploadInfo()
            }
        }
        return code
    }

    fun selectBtSubTask(taskId: Long, btIndexSet: BtIndexSet?): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.selectBtSubTask(taskId, btIndexSet)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun setAccelerateToken(
        taskId: Long,
        subIndex: Int,
        appTaskId: Long,
        accelerateType: Int,
        token: String,
    ): Int {
        var code = XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        increaseRefCount()
        if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            code = xLLoader.setAccelerateToken(taskId, subIndex, appTaskId, accelerateType, token)
            decreaseRefCount()
            return code
        }
        decreaseRefCount()
        return code
    }

    fun setBtPriorSubTask(taskId: Long, fileIndex: Int): Int {
        return xLLoader.setBtPriorSubTask(taskId, fileIndex)
    }

    fun setBtSwitch(@XLConstant.BtSwitch btSwitch: Int): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.setBtSwitch(btSwitch)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun setCandidateResSpeed(taskId: Long, candidateResSpeed: Int): Int {
        var code = XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            runCatching {
                code = xLLoader.setCandidateResSpeed(taskId, candidateResSpeed)
            }
        }
        return code
    }

    fun setDownloadTaskOrigin(taskId: Long, origin: String): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.setDownloadTaskOrigin(taskId, origin)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun setEmuleSwitch(@XLConstant.EmuleSwitch enable: Int): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.setEmuleSwitch(enable)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun setFileName(taskId: Long, fileName: String?): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.setFileName(taskId, fileName)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun setHttpHeaderProperty(taskId: Long, name: String, value: String): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.setHttpHeaderProperty(taskId, name, value)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun setImei(imei: String): Int {
        return if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.setImei(imei)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
    }

    fun setIndexInfo(taskId: Long, setIndexInfoParam: SetIndexInfoParam): Int {
        var code = XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        increaseRefCount()
        if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            code = xLLoader.setIndexInfo(
                taskId,
                setIndexInfoParam.mCid,
                setIndexInfoParam.mGcid,
                setIndexInfoParam.mBcid,
                setIndexInfoParam.mFileSize,
                setIndexInfoParam.mGcidLevel
            )
        }
        decreaseRefCount()
        return code
    }

    fun setLocalHostResolve(hostInfo: String, infoFormat: String): Int {
        return xLLoader.setLocalHostResolve(hostInfo, infoFormat)
    }

    fun setMac(mac: String): Int {
        return if (managerStatus != XLManagerStatus.MANAGER_RUNNING) {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        } else {
            xLLoader.setMac(mac)
        }
    }

    fun setOSVersion(osVersion: String): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.setMiUiVersion(osVersion)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun setOriginUserAgent(taskId: Long, userAgent: String): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.setOriginUserAgent(taskId, userAgent)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun setPlayerMode(taskId: Long, mode: Int): Int {
        var code = XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            runCatching {
                code = xLLoader.setPlayerMode(taskId, mode)
            }
        }
        return code
    }

    fun setReleaseLog(boolean: Boolean, str: String): Int {
        return setReleaseLog(boolean, str, 0, 0)
    }

    fun setReleaseLog(boolean: Boolean, str: String, i10: Int, i11: Int): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            if (boolean) {
                xLLoader.setReleaseLog(1, str, i10, i11)
            } else {
                xLLoader.setReleaseLog(0, null, 0, 0)
            }
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun setSlowAccelerateSpeed(taskId: Long, speed: Long): Int {
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.setSlowAccelerateSpeed(taskId, speed)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        return code
    }

    fun setSpeedLimit(download: Long, upload: Long): Int {
        return xLLoader.setSpeedLimit(download, upload)
    }

    fun setStatReportSwitch(enable: Boolean): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.setStatReportSwitch(enable)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun setSubTaskConcurrency(taskId: Long, concurrency: Int): Int {
        return xLLoader.setSubTaskConcurrency(taskId, concurrency)
    }

    /**
     * 0,1,2
     */
    fun setTaskAllowUseResource(taskId: Long, @XLConstant.ResourceType resourceType: Int): Int {
        increaseRefCount()
        val code = if (managerStatus != XLManagerStatus.MANAGER_RUNNING) {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        } else {
            xLLoader.setTaskAllowUseResource(taskId, resourceType)
        }
        decreaseRefCount()
        return code
    }

    /**
     * 试用 DCDN 0,1,2
     *
     * 2 为启用
     */
    fun setTaskGsState(taskId: Long, subIndex: Int, i: Int): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.setTaskGsState(taskId, subIndex, i)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    /**
     * i == 1
     */
    fun setTaskLxState(taskId: Long, subIndex: Int, i: Int): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.setTaskLxState(taskId, subIndex, i)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun setTaskSpeedLimit(takId: Long, limit: Long): Int {
        return xLLoader.setTaskSpeedLimit(takId, limit)
    }

    fun setTaskUid(taskId: Long, uid: Int): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.setTaskUid(taskId, uid)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun setUploadControlParam(uploadControlParam: UploadControlParam): Int {
        var code = XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            runCatching {
                code = xLLoader.setUploadControlParam(uploadControlParam)
            }
        }
        return code
    }

    fun setUploadInfo(uploadInfo: UploadInfo): Int {
        var code = XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            runCatching {
                code = xLLoader.setUploadInfo(uploadInfo)
            }
        }
        return code
    }

    fun setUploadSwitch(enable: Boolean): Int {
        var code = XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            runCatching {
                code = xLLoader.setUploadSwitch(enable)
            }
        }
        return code
    }

    fun setUserId(userId: String): Int {
        var code = XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            runCatching {
                code = xLLoader.setUserId(userId)
            }
        }
        return code
    }

    fun setVipType(vipType: String): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.setVipType(vipType)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun startDcdn(
        taskId: Long,
        subIndex: Int,
        sessionId: String,
        productType: String,
        verifyInfo: String,
    ): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.startDcdn(taskId, subIndex, sessionId, productType, verifyInfo)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun startTask(taskId: Long): Int {
        return startTask(taskId, false)
    }

    fun startTask(taskId: Long, extStorage: Boolean): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.startTask(taskId, extStorage)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun statExternalInfo(taskId: Long, fileIndex: Int, key: String, value: Int): Int {
        return statExternalInfo(taskId, fileIndex, key, value.toString())
    }

    fun statExternalInfo(taskId: Long, fileIndex: Int, key: String, value: String): Int {
        return xLLoader.statExternalInfo(taskId, fileIndex, key, value)
    }

    fun statExternalInfoU64(
        taskId: Long,
        fileIndex: Int,
        key: String,
        value: Long,
        statType: Int,
    ): Int {
        return xLLoader.statExternalInfoU64(taskId, fileIndex, key, value, statType)
    }

    fun stopDcdn(taskId: Long, subIndex: Int): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.stopDcdn(taskId, subIndex)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun stopTask(taskId: Long): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.stopTask(taskId)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    /**
     * 停止任务
     *
     * @param status 示例: [com.xunlei.download.Downloads.Impl.STATUS_PAUSED_BY_APP]
     */
    fun stopTaskWithReason(taskId: Long, status: Int): Int {
        increaseRefCount()
        val code = if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.stopTaskWithReason(taskId, status)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
        decreaseRefCount()
        return code
    }

    fun switchOriginToAllResDownload(taskId: Long): Int {
        return if (managerStatus == XLManagerStatus.MANAGER_RUNNING) {
            xLLoader.switchOriginToAllResDownload(taskId)
        } else {
            XLConstant.XLErrorCode.DOWNLOAD_MANAGER_ERROR
        }
    }


    companion object {
        internal const val TAG = "XLDownloadManager"

        private val mErrCodeStringMap: Map<String, Any> by lazy {
            parseJsonMap(ErrorCodeToMsg.ERR_CODE_TO_MSG)
        }

        private var managerStatus = XLManagerStatus.MANAGER_UNINIT
        private var mRunningRefCount = 0

        @JvmStatic
        @get:Synchronized
        val instance by lazy { XLDownloadManager() }
    }

    /**
     * 网络状态变化
     */
    inner class NetworkChangeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.orEmpty() == "android.net.conn.CONNECTIVITY_CHANGE") {
                mSingleThreadExecutor.execute { notifyNetWorkInfo(context) }
            }
        }
    }
}
