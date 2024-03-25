///*
//package com.xunlei.download;
//
//import android.content.ContentResolver;
//import android.content.ContentUris;
//import android.content.ContentValues;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.net.NetworkInfo;
//import android.net.Uri;
//import android.os.Environment;
//import android.os.SystemClock;
//import android.os.storage.StorageManager;
//import android.text.TextUtils;
//import android.util.Pair;
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Future;
//
//*/
///* renamed from: com.xunlei.download.proguard.b *//*
//
//*/
///* loaded from: classes3.dex *//*
//
//public class DownloadInfo {
//
//    */
///* renamed from: a *//*
//
//    public static final String COLUMN_WIFI_REQUIRED = "isWifiRequired";
//
//    */
///* renamed from: aP *//*
//
//    private static DownloadSchedule downloadSchedule = null;
//
//    */
///* renamed from: b *//*
//
//    public static final String column_total_bytes = "total_bytes";
//
//    */
///* renamed from: A *//*
//
//    public int uid;
//
//    */
///* renamed from: B *//*
//
//    public int scanned;
//
//    */
///* renamed from: C *//*
//
//    public boolean canceled;
//
//    */
///* renamed from: D *//*
//
//    public String mediaprovider_uri;
//
//    */
///* renamed from: E *//*
//
//    public boolean isPublicApi;
//
//    */
///* renamed from: F *//*
//
//    public int allowedNetworkTypes;
//
//    */
///* renamed from: G *//*
//
//    public int allow_res_types;
//
//    */
///* renamed from: H *//*
//
//    public boolean allowRoaming;
//
//    */
///* renamed from: I *//*
//
//    public boolean allowMetered;
//
//    */
///* renamed from: J *//*
//
//    public String title;
//
//    */
///* renamed from: K *//*
//
//    public String description;
//
//    */
///* renamed from: L *//*
//
//    public int bypass_reccommended_size_limit;
//
//    */
///* renamed from: M *//*
//
//    public String cid;
//
//    */
///* renamed from: N *//*
//
//    public String gcid;
//
//    */
///* renamed from: O *//*
//
//    public String bt_select_set;
//
//    */
///* renamed from: P *//*
//
//    public HashSet<Long> btset;
//
//    */
///* renamed from: Q *//*
//
//    public boolean is_vip_speedup;
//
//    */
///* renamed from: R *//*
//
//    public boolean is_lx_speedup;
//
//    */
///* renamed from: S *//*
//
//    public boolean is_dcdn_speedup;
//
//    */
///* renamed from: T *//*
//
//    public boolean syncro_lx_task_to_server;
//
//    */
///* renamed from: U *//*
//
//    public int vip_status;
//
//    */
///* renamed from: V *//*
//
//    public int vip_trial_status;
//
//    */
///* renamed from: W *//*
//
//    public int lx_status;
//
//    */
///* renamed from: X *//*
//
//    public long lx_progress;
//
//    */
///* renamed from: Y *//*
//
//    public long create_time;
//
//    */
///* renamed from: Z *//*
//
//    public long max_download_speed;
//
//    */
///* renamed from: aA *//*
//
//    public String lanPeerInfo;
//
//    */
///* renamed from: aB *//*
//
//    public int play_mode;
//
//    */
///* renamed from: aC *//*
//
//    public int orgin_errcode;
//
//    */
///* renamed from: aD *//*
//
////    public DownloadManager.TaskTypeExt task_type_text;
//
//    */
///* renamed from: aE *//*
//
//    public String change_orgin_url;
//
//    */
///* renamed from: aF *//*
//
//    public int seq_id;
//
//    */
///* renamed from: aG *//*
//
//    public int randInt1001;
//
//    */
///* renamed from: aH *//*
//
//    private long time;
//
//    */
///* renamed from: aI *//*
//
//    private List<Pair<String, String>> requestHeaders;
//
//    */
///* renamed from: aJ *//*
//
//    private Future<?> future;
//
//    */
///* renamed from: aK *//*
//
//    private Runnable runnable;
//
//    */
///* renamed from: aL *//*
//
//    private final Context context;
//
//    */
///* renamed from: aM *//*
//
////    private final SystemFacade systemFacade;
//
//    */
///* renamed from: aN *//*
//
//    private final StorageManager storageManager;
//
//    */
///* renamed from: aO *//*
//
//    private final DownloadNotifier downloadNotifier;
//
//    */
///* renamed from: aa *//*
//
//    public DownloadManager.TaskType task_type;
//
//    */
///* renamed from: ab *//*
//
//    public long download_duration;
//
//    */
///* renamed from: ac *//*
//
//    public long vip_receive_size;
//
//    */
///* renamed from: ad *//*
//
//    public long lx_receive_size;
//
//    */
///* renamed from: ae *//*
//
//    public long p2s_receive_size;
//
//    */
///* renamed from: af *//*
//
//    public long p2p_receive_size;
//
//    */
///* renamed from: ag *//*
//
//    public long dcdn_receive_size;
//
//    */
///* renamed from: ah *//*
//
//    public long origin_receive_size;
//
//    */
///* renamed from: ai *//*
//
//    public long group_id;
//
//    */
///* renamed from: aj *//*
//
//    public String xl_orgin;
//
//    */
///* renamed from: ak *//*
//
//    public int is_visible_in_downloads_ui;
//
//    */
///* renamed from: al *//*
//
//    public String group_priority;
//    public String am;
//
//    */
///* renamed from: an *//*
//
//    public String task_token;
//
//    */
///* renamed from: ao *//*
//
//    public int task_acc_type;
//
//    */
///* renamed from: ap *//*
//
//    public String task_card_id;
//
//    */
///* renamed from: aq *//*
//
//    public long slow_acc_speed;
//
//    */
///* renamed from: ar *//*
//
//    public int slow_acc_status;
//
//    */
///* renamed from: as *//*
//
//    public int slow_acc_errno;
//
//    */
///* renamed from: at *//*
//
//    public int first_media_state;
//
//    */
///* renamed from: au *//*
//
//    public int cdn_speed;
//
//    */
///* renamed from: av *//*
//
//    public boolean premuim_emergency;
//
//    */
///* renamed from: aw *//*
//
//    public long premuim_bytes;
//
//    */
///* renamed from: ax *//*
//
//    public int premuim_using;
//
//    */
///* renamed from: ay *//*
//
//    public int premuim_count;
//
//    */
///* renamed from: az *//*
//
//    public int lan_acc_state;
//
//    */
///* renamed from: c *//*
//
//    public long downloadId;
//
//    */
///* renamed from: d *//*
//
//    public String uri;
//
//    */
///* renamed from: e *//*
//
//    public boolean noIntegrity;
//
//    */
///* renamed from: f *//*
//
//    public String fileNameHint;
//
//    */
///* renamed from: g *//*
//
//    public String fileName;
//
//    */
///* renamed from: h *//*
//
//    public String mimeType;
//
//    */
///* renamed from: i *//*
//
//    public int destination;
//
//    */
///* renamed from: j *//*
//
//    public int visibility;
//
//    */
///* renamed from: k *//*
//
//    public int control;
//
//    */
///* renamed from: l *//*
//
//    public int status;
//
//    */
///* renamed from: m *//*
//
//    public int numFailed;
//
//    */
///* renamed from: n *//*
//
//    public int retryAfter;
//
//    */
///* renamed from: o *//*
//
//    public long lastModifer;
//
//    */
///* renamed from: p *//*
//
//    public String packageName;
//
//    */
///* renamed from: q *//*
//
//    public String targetClsName;
//
//    */
///* renamed from: r *//*
//
//    public String notifyExtraStr;
//
//    */
///* renamed from: s *//*
//
//    public String mCookies;
//
//    */
///* renamed from: t *//*
//
//    public String userAgent;
//
//    */
///* renamed from: u *//*
//
//    public String referer;
//
//    */
///* renamed from: v *//*
//
//    public long total_bytes;
//
//    */
///* renamed from: w *//*
//
//    public long currentBytes;
//
//    */
///* renamed from: x *//*
//
//    public long total_file_count;
//
//    */
///* renamed from: y *//*
//
//    public long download_file_count;
//
//    */
///* renamed from: z *//*
//
//    public String etag;
//
//    */
///* compiled from: DownloadInfo.java *//*
//
//    */
///* renamed from: com.xunlei.download.proguard.b$b *//*
//
//    */
///* loaded from: classes3.dex *//*
//
//    public enum EnumDownloadStatus {
//        OK,
//        NO_CONNECTION,
//        UNUSABLE_DUE_TO_SIZE,
//        RECOMMENDED_UNUSABLE_DUE_TO_SIZE,
//        CANNOT_USE_ROAMING,
//        TYPE_DISALLOWED_BY_REQUESTOR,
//        BLOCKED
//    }
//
//    private int b(int i10) {
//        if (i10 != 0) {
//            if (i10 != 1) {
//                if (i10 != 7) {
//                    return i10 != 9 ? 0 : 2;
//                }
//                return 4;
//            }
//            return 2;
//        }
//        return 1;
//    }
//
//    */
///* renamed from: n *//*
//
//    private boolean allowRoamingOrDestEq3() {
//        if (this.isPublicApi) {
//            return this.allowRoaming;
//        }
//        return this.destination != 3;
//    }
//
//    private boolean o() {
//        if (this.status != 192) {
//            this.status = Downloads.Impl.STATUS_RUNNING;
//            this.control = 0;
//            ContentValues contentValues = new ContentValues();
//            contentValues.put(Downloads.Impl.COLUMN_CONTROL, (Integer) 0);
//            try {
//                return this.context.getContentResolver().update(downloadOrGroupUri(), contentValues, "deleted=0", null) > 0;
//            } catch (Exception e10) {
//                e10.printStackTrace();
//                XLLog.printStackTrace(e10);
//            }
//        }
//        return true;
//    }
//
//    private void p() {
//        f.a().a(500L);
//    }
//
//    */
///* renamed from: c *//*
//
//    public void notifyComplete() {
//        Intent intent;
//        if (this.packageName == null) {
//            return;
//        }
//        if (this.isPublicApi) {
//            intent = new Intent(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
//            intent.setPackage(this.packageName);
//            intent.putExtra(DownloadManager.EXTRA_DOWNLOAD_ID, this.downloadId);
//        } else if (this.targetClsName == null) {
//            return;
//        } else {
//            intent = new Intent(Downloads.Impl.ACTION_DOWNLOAD_COMPLETED);
//            intent.setClassName(this.packageName, this.targetClsName);
//            String str = this.notifyExtraStr;
//            if (str != null) {
//                intent.putExtra(Downloads.Impl.COLUMN_NOTIFICATION_EXTRAS, str);
//            }
//            intent.setData(downloadCompleteUri());
//        }
//        this.systemFacade.startIntent(intent);
//    }
//
//    public boolean d() {
//        int i10 = this.control;
//        if (i10 == 1 || i10 == 2) {
//            return false;
//        }
//        int i11 = this.status;
//        if (i11 == 0 || i11 == 190) {
//            return true;
//        }
//        if (i11 != 192) {
//            if (i11 != 199) {
//                switch (i11) {
//                    case Downloads.Impl.STATUS_WAITING_TO_RETRY */
///* 194 *//*
//:
//                        long a10 = this.systemFacade.a();
//                        return a(a10) <= a10;
//                    case Downloads.Impl.STATUS_WAITING_FOR_NETWORK */
///* 195 *//*
//:
//                    case Downloads.Impl.STATUS_QUEUED_FOR_WIFI */
///* 196 *//*
//:
//                        break;
//                    default:
//                        return false;
//                }
//            } else {
//                return Environment.getExternalStorageState().equals("mounted");
//            }
//        } else if (this.task_type == DownloadManager.TaskType.GROUP) {
//            return true;
//        }
//        EnumDownloadStatus status = c(this.total_bytes);
//        a("isReadyToDownload() state = " + status + ", mTotalBytes= " + this.total_bytes);
//        return status == EnumDownloadStatus.OK;
//    }
//
//    */
///* renamed from: e *//*
//
//    public boolean isComplete() {
//        return Downloads.Impl.isStatusCompleted(this.status) && this.visibility == 1;
//    }
//
//    */
///* renamed from: f *//*
//
//    public boolean isActiveTask() {
//        Future<?> future = this.future;
//        boolean z10 = (future == null || future.isDone()) ? false : true;
//        Future<?> future2 = this.future;
//        if (future2 != null && future2.isDone()) {
//            StringBuilder a10 = android.support.v4.media.e.a("isDone() ret=");
//            a10.append(this.future.isDone());
//            a(a10.toString());
//            try {
//                this.future.get();
//            } catch (Exception e10) {
//                StringBuilder a11 = android.support.v4.media.e.a("ce ");
//                a11.append(e10.toString());
//                a(a11.toString());
//                XLLog.printStackTrace(e10);
//            }
//        }
//        a(androidx.activity.f.a("isActiveTask() ret=", z10));
//        return z10;
//    }
//
//    public boolean g() {
//        int i10 = this.destination;
//        return i10 == 1 || i10 == 5 || i10 == 3 || i10 == 2;
//    }
//
//    */
///* renamed from: h *//*
//
//    public Uri downloadCompleteUri() {
//        return DownloadManager.getInstanceFor(this.context).getDownloadUri(this.downloadId);
//    }
//
//    */
///* renamed from: i *//*
//
//    public Uri downloadOrGroupUri() {
//        if (this.group_id == 0) {
//            return DownloadManager.getInstanceFor(this.context).getDownloadUri(this.downloadId);
//        }
//        return ContentUris.withAppendedId(DownloadManager.getInstanceFor(this.context).getTaskGroupUri(this.group_id), this.downloadId);
//    }
//
//    public boolean j() {
//        int i10;
//        return this.scanned == 0 && ((i10 = this.destination) == 0 || i10 == 4 || i10 == 6) && Downloads.Impl.isStatusSuccess(this.status);
//    }
//
//    */
///* renamed from: k *//*
//
//    public String downloadFilePath() {
//        int i10 = this.destination;
//        if (i10 != 0) {
//            if (i10 == 4) {
//                return this.fileNameHint;
//            }
//            if (i10 != 6) {
//                return downloadOrGroupUri().toString();
//            }
//        }
//        if (this.fileName != null) {
//            return Uri.fromFile(new File(this.fileName)).toString();
//        }
//        return null;
//    }
//
//    public long l() {
//        Runnable runnable = this.runnable;
//        if (runnable instanceof t) {
//            return ((t) runnable).k();
//        }
//        return -1L;
//    }
//
//    public void m() {
//        if (isActiveTask()) {
//            return;
//        }
//        Runnable runnable = this.runnable;
//        if (runnable instanceof t) {
//            ((t) runnable).c();
//        }
//    }
//
//    private DownloadInfo(Context context, SystemFacade systemFacade, StorageManager storageManager, DownloadNotifier downloadNotifier) {
//        this.time = 0L;
//        this.requestHeaders = new ArrayList();
//        this.context = context;
//        this.systemFacade = systemFacade;
//        this.storageManager = storageManager;
//        this.downloadNotifier = downloadNotifier;
//        this.randInt1001 = Helpers.random.nextInt(1001);
//        if (downloadSchedule == null) {
//            downloadSchedule = new DownloadSchedule();
//        }
//    }
//
//    */
///* renamed from: a *//*
//
//    public static void clearSchedule() {
//        DownloadSchedule downloadSchedule2 = downloadSchedule;
//        if (downloadSchedule2 != null) {
//            downloadSchedule2.clear();
//        }
//    }
//
//    */
///* renamed from: b *//*
//
//    public Collection<Pair<String, String>> wrapList() {
//        return Collections.unmodifiableList(this.requestHeaders);
//    }
//
//    public boolean b(long j10) {
//        HashSet<Long> hashSet = this.btset;
//        if (hashSet == null) {
//            return false;
//        }
//        return hashSet.contains(Long.valueOf(j10));
//    }
//
//    */
///* compiled from: DownloadInfo.java *//*
//
//    */
///* renamed from: com.xunlei.download.proguard.b$c *//*
//
//    */
///* loaded from: classes3.dex *//*
//
//    public static class DownloadStore {
//
//        */
///* renamed from: a *//*
//
//        private ContentResolver contentResolver;
//
//        */
///* renamed from: b *//*
//
//        private Cursor cursor;
//
//        public DownloadStore(ContentResolver contentResolver, Cursor cursor) {
//            this.contentResolver = contentResolver;
//            this.cursor = cursor;
//        }
//
//        */
///* renamed from: b *//*
//
//        private void fillHeaders(DownloadInfo downloadInfo) {
//            Cursor cursor;
//            downloadInfo.requestHeaders.clear();
//            try {
//                cursor = this.contentResolver.query(Uri.withAppendedPath(downloadInfo.downloadOrGroupUri(), Downloads.Impl.RequestHeaders.URI_SEGMENT), null, null, null, null);
//            } catch (Exception e10) {
//                e10.printStackTrace();
//                XLLog.printStackTrace(e10);
//                cursor = null;
//            }
//            if (cursor != null) {
//                try {
//                    int columnIndexOrThrow = cursor.getColumnIndexOrThrow("header");
//                    int columnIndexOrThrow2 = cursor.getColumnIndexOrThrow("value");
//                    cursor.moveToFirst();
//                    while (!cursor.isAfterLast()) {
//                        addRequestHeader(downloadInfo, cursor.getString(columnIndexOrThrow), cursor.getString(columnIndexOrThrow2));
//                        cursor.moveToNext();
//                    }
//                } finally {
//                    cursor.close();
//                }
//            }
//            String str = downloadInfo.mCookies;
//            if (str != null) {
//                addRequestHeader(downloadInfo, HttpClient.HEADER_COOKIE, str);
//            }
//            String str2 = downloadInfo.referer;
//            if (str2 != null) {
//                addRequestHeader(downloadInfo, HttpClient.HEADER_REFERRER, str2);
//            }
//            String str3 = downloadInfo.userAgent;
//            if (str3 != null) {
//                addRequestHeader(downloadInfo, "User-Agent", str3);
//            }
//        }
//
//        */
///* renamed from: c *//*
//
//        private Long getLong(String str) {
//            Cursor cursor = this.cursor;
//            return Long.valueOf(cursor.getLong(cursor.getColumnIndexOrThrow(str)));
//        }
//
//        */
///* renamed from: a *//*
//
//        public DownloadInfo newDownloadInfo(Context context, SystemFacade systemFacade, StorageManager storageManager, DownloadNotifier downloadNotifier) {
//            DownloadInfo downloadInfo = new DownloadInfo(context, systemFacade, storageManager, downloadNotifier);
//            fillData(downloadInfo);
//            fillHeaders(downloadInfo);
//            StringBuilder a10 = android.support.v4.media.e.a("info.mid=");
//            a10.append(String.valueOf(downloadInfo.downloadId));
//            a10.append("info.mTotalBytes=");
//            a10.append(String.valueOf(downloadInfo.total_bytes));
//            XLLog.d("DownloadManager", a10.toString());
//            return downloadInfo;
//        }
//
//        */
///* renamed from: a *//*
//
//        public void fillData(DownloadInfo downloadInfo) {
//            downloadInfo.downloadId = getLong(DownloadManager.COLUMN_ID).longValue();
//            downloadInfo.uri = getString("uri");
//            downloadInfo.noIntegrity = getInt(Downloads.Impl.COLUMN_NO_INTEGRITY).intValue() == 1;
//            downloadInfo.fileNameHint = getString(Downloads.Impl.COLUMN_FILE_NAME_HINT);
//            downloadInfo.fileName = getString(Downloads.Impl._DATA);
//            downloadInfo.mimeType = ak.a(getString(Downloads.Impl.COLUMN_MIME_TYPE));
//            downloadInfo.destination = getInt("destination").intValue();
//            downloadInfo.visibility = getInt("visibility").intValue();
//            downloadInfo.status = getInt("status").intValue();
//            downloadInfo.numFailed = getInt(Downloads.Impl.COLUMN_FAILED_CONNECTIONS).intValue();
//            downloadInfo.retryAfter = getInt("method").intValue() & 268435455;
//            downloadInfo.lastModifer = getLong(Downloads.Impl.COLUMN_LAST_MODIFICATION).longValue();
//            downloadInfo.packageName = getString(Downloads.Impl.COLUMN_NOTIFICATION_PACKAGE);
//            downloadInfo.targetClsName = getString(Downloads.Impl.COLUMN_NOTIFICATION_CLASS);
//            downloadInfo.notifyExtraStr = getString(Downloads.Impl.COLUMN_NOTIFICATION_EXTRAS);
//            downloadInfo.mCookies = getString(Downloads.Impl.COLUMN_COOKIE_DATA);
//            downloadInfo.userAgent = getString(Downloads.Impl.COLUMN_USER_AGENT);
//            downloadInfo.referer = getString("referer");
//            downloadInfo.total_bytes = getLong("total_bytes").longValue();
//            downloadInfo.currentBytes = getLong(Downloads.Impl.COLUMN_CURRENT_BYTES).longValue();
//            downloadInfo.total_file_count = getLong("total_file_count").longValue();
//            downloadInfo.download_file_count = getLong("download_file_count").longValue();
//            downloadInfo.etag = getString("etag");
//            downloadInfo.uid = getInt(a.f6286f).intValue();
//            downloadInfo.scanned = getInt("scanned").intValue();
//            downloadInfo.canceled = getInt(Downloads.Impl.COLUMN_DELETED).intValue() == 1;
//            downloadInfo.mediaprovider_uri = getString("mediaprovider_uri");
//            downloadInfo.isPublicApi = getInt(Downloads.Impl.COLUMN_IS_PUBLIC_API).intValue() != 0;
//            downloadInfo.allowRoaming = getInt(Downloads.Impl.COLUMN_ALLOW_ROAMING).intValue() != 0;
//            downloadInfo.allowMetered = getInt(Downloads.Impl.COLUMN_ALLOW_METERED).intValue() != 0;
//            downloadInfo.title = getString("title");
//            downloadInfo.description = getString("description");
//            downloadInfo.bypass_reccommended_size_limit = getInt(Downloads.Impl.COLUMN_BYPASS_RECOMMENDED_SIZE_LIMIT).intValue();
//            downloadInfo.cid = getString("cid");
//            downloadInfo.gcid = getString("gcid");
//            downloadInfo.create_time = getLong("create_time").longValue();
//            downloadInfo.is_vip_speedup = getInt("is_vip_speedup").intValue() != 0;
//            downloadInfo.is_lx_speedup = getInt("is_lx_speedup").intValue() != 0;
//            downloadInfo.is_dcdn_speedup = getInt("is_dcdn_speedup").intValue() != 0;
//            downloadInfo.syncro_lx_task_to_server = getInt(Downloads.Impl.COLUMN_SYNCRO_LX_TASK_TO_SERVER).intValue() != 0;
//            downloadInfo.lx_progress = getLong("lx_progress").longValue();
//            int intValue = getInt("lx_status").intValue();
//            downloadInfo.lx_status = intValue;
//            if (intValue == 0) {
//                downloadInfo.lx_status = Downloads.Impl.STATUS_PENDING;
//            }
//            int intValue2 = getInt("vip_status").intValue();
//            downloadInfo.vip_status = intValue2;
//            if (intValue2 == 0) {
//                downloadInfo.vip_status = Downloads.Impl.STATUS_PENDING;
//            }
//            int intValue3 = getInt("vip_trial_status").intValue();
//            downloadInfo.vip_trial_status = intValue3;
//            if (intValue3 == 0) {
//                downloadInfo.vip_trial_status = Downloads.Impl.STATUS_PENDING;
//            }
//            downloadInfo.bt_select_set = getString("bt_select_set");
//            downloadInfo.task_type = DownloadManager.TaskType.values()[getInt("task_type").intValue()];
//            downloadInfo.download_duration = getLong("download_duration").longValue();
//            downloadInfo.vip_receive_size = getLong("vip_receive_size").longValue();
//            downloadInfo.lx_receive_size = getLong("lx_receive_size").longValue();
//            downloadInfo.p2s_receive_size = getLong("p2s_receive_size").longValue();
//            downloadInfo.p2p_receive_size = getLong("p2p_receive_size").longValue();
//            downloadInfo.origin_receive_size = getLong("origin_receive_size").longValue();
//            downloadInfo.dcdn_receive_size = getLong("dcdn_receive_size").longValue();
//            downloadInfo.group_id = getLong("group_id").longValue();
//            downloadInfo.btset = ag.a(downloadInfo.bt_select_set);
//            downloadInfo.xl_orgin = getString(Downloads.Impl.COLUMN_XL_ORIGIN);
//            int intValue4 = getInt(Downloads.Impl.COLUMN_ALLOWED_NETWORK_TYPES).intValue();
//            if (downloadInfo.group_id == 0 && XlTaskHelper.a().a(downloadInfo.downloadId)) {
//                downloadInfo.allowedNetworkTypes = intValue4 | 1;
//            } else {
//                downloadInfo.allowedNetworkTypes = intValue4;
//            }
//            downloadInfo.allow_res_types = getInt(Downloads.Impl.COLUMN_ALLOW_RES_TYPES).intValue();
//            synchronized (this) {
//                downloadInfo.control = getInt(Downloads.Impl.COLUMN_CONTROL).intValue();
//            }
//            downloadInfo.is_visible_in_downloads_ui = getInt(Downloads.Impl.COLUMN_IS_VISIBLE_IN_DOWNLOADS_UI).intValue();
//            downloadInfo.group_priority = getString(Downloads.Impl.COLUMN_GROUP_PRIORITY);
//            downloadInfo.task_token = getString(Downloads.Impl.COLUMN_TASK_TOKEN);
//            downloadInfo.task_acc_type = getInt(Downloads.Impl.COLUMN_TASK_ACC_TYPE).intValue();
//            downloadInfo.task_card_id = getString(Downloads.Impl.COLUMN_TASK_CARD_ID);
//            downloadInfo.slow_acc_speed = getLong(Downloads.Impl.COLUMN_SLOW_ACC_SPEED).longValue();
//            downloadInfo.slow_acc_status = getInt(Downloads.Impl.COLUMN_SLOW_ACC_STATUS).intValue();
//            downloadInfo.slow_acc_errno = getInt(Downloads.Impl.COLUMN_SLOW_ACC_ERRNO).intValue();
//            downloadInfo.first_media_state = getInt(Downloads.Impl.COLUMN_FIRST_MEDIA_STATE).intValue();
//            downloadInfo.cdn_speed = getInt(Downloads.Impl.COLUMN_CDN_SPEED).intValue();
//            downloadInfo.lan_acc_state = getInt(Downloads.Impl.COLUMN_LAN_ACC_STATE).intValue();
//            downloadInfo.lanPeerInfo = getString(Downloads.Impl.COLUMN_LAN_PEER_INFO);
//            downloadInfo.premuim_emergency = getInt(Downloads.Impl.COLUMN_PREMIUM_EMERGENCY).intValue() != 0;
//            downloadInfo.premuim_bytes = getLong(Downloads.Impl.COLUMN_PREMIUM_BYTES).longValue();
//            downloadInfo.premuim_count = getInt(Downloads.Impl.COLUMN_PREMIUM_COUNT).intValue();
//            downloadInfo.premuim_using = getInt(Downloads.Impl.COLUMN_PREMIUM_USING).intValue();
//            downloadInfo.play_mode = getInt(Downloads.Impl.COLUMN_PLAY_MODE).intValue();
//            downloadInfo.orgin_errcode = getInt(Downloads.Impl.COLUMN_ORIGIN_ERRCODE).intValue();
//            downloadInfo.task_type_text = DownloadManager.TaskTypeExt.values()[getInt(Downloads.Impl.COLUMN_TASK_TYPE_EXT).intValue()];
//            downloadInfo.change_orgin_url = getString(Downloads.Impl.COLUMN_CHANGE_ORIGIN_URL);
//            downloadInfo.max_download_speed = getLong(Downloads.Impl.COLUMN_TASK_MAX_DOWNLOAD_SPEED).longValue();
//            downloadInfo.seq_id = getInt(Downloads.Impl.COLUMN_SEQ_ID).intValue();
//            StringBuilder a10 = android.support.v4.media.e.a("updateFromDatabase: mId=");
//            a10.append(String.valueOf(downloadInfo.downloadId));
//            a10.append(", Uri=");
//            a10.append(downloadInfo.uri);
//            a10.append(", token=");
//            a10.append(downloadInfo.task_token);
//            a10.append(", cardId=");
//            a10.append(downloadInfo.task_card_id);
//            a10.append(", seqid=");
//            a10.append(String.valueOf(downloadInfo.seq_id));
//            XLLog.d("DownloadManager", a10.toString());
//        }
//
//        */
///* renamed from: b *//*
//
//        private Integer getInt(String str) {
//            Cursor cursor = this.cursor;
//            return Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(str)));
//        }
//
//        */
///* renamed from: a *//*
//
//        private void addRequestHeader(DownloadInfo downloadInfo, String str, String str2) {
//            downloadInfo.requestHeaders.add(Pair.create(str, str2));
//        }
//
//        */
///* renamed from: a *//*
//
//        private String getString(String str) {
//            String string = this.cursor.getString(this.cursor.getColumnIndexOrThrow(str));
//            if (TextUtils.isEmpty(string)) {
//                return null;
//            }
//            return string;
//        }
//    }
//
//    private EnumDownloadStatus b(int i10, long j10) {
//        Long e10;
//        if (j10 <= 0) {
//            return EnumDownloadStatus.OK;
//        }
//        if (ai.a(i10)) {
//            if (this.task_type != DownloadManager.TaskType.GROUP && this.group_id == 0) {
//                Long d10 = this.systemFacade.d();
//                if (d10 != null && d10.longValue() >= 0 && j10 > d10.longValue()) {
//                    return EnumDownloadStatus.UNUSABLE_DUE_TO_SIZE;
//                }
//                if (this.bypass_reccommended_size_limit == 0 && (e10 = this.systemFacade.e()) != null && e10.longValue() >= 0 && j10 > e10.longValue()) {
//                    return EnumDownloadStatus.RECOMMENDED_UNUSABLE_DUE_TO_SIZE;
//                }
//            } else {
//                Long d11 = this.systemFacade.d();
//                if (d11 != null && d11.longValue() >= 0 && this.bypass_reccommended_size_limit == 0) {
//                    return EnumDownloadStatus.UNUSABLE_DUE_TO_SIZE;
//                }
//            }
//        }
//        return EnumDownloadStatus.OK;
//    }
//
//    public void a(int i10) {
//        Intent intent = new Intent(DownloadManager.ACTION_DOWNLOAD_START_OR_COMPLETE);
//        intent.setPackage(this.packageName);
//        intent.putExtra(DownloadManager.EXTRA_DOWNLOAD_ID, this.downloadId);
//        intent.putExtra(DownloadManager.EXTRA_DOWNLOAD_STATE, DownloadManager.translateStatus(i10));
//        intent.putExtra(DownloadManager.EXTRA_DOWNLOAD_STATE_ORIGINAL, i10);
//        this.systemFacade.startIntent(intent);
//    }
//
//    public long d(long j10) {
//        if (Downloads.Impl.isStatusCompleted(this.status)) {
//            return Long.MAX_VALUE;
//        }
//        if (this.status != 194) {
//            return 0L;
//        }
//        long a10 = a(j10);
//        if (a10 <= j10) {
//            return 0L;
//        }
//        return a10 - j10;
//    }
//
//    public long a(long j10) {
//        int i10 = this.numFailed;
//        if (i10 == 0) {
//            return j10;
//        }
//        int i11 = this.retryAfter;
//        if (i11 > 0) {
//            return this.lastModifer + i11;
//        }
//        return this.lastModifer + ((this.randInt1001 + 1000) * 30 * (1 << (i10 - 1)));
//    }
//
//    public EnumDownloadStatus c(long j10) {
//        NetworkInfo a10 = this.systemFacade.a(this.uid);
//        if (a10 != null && a10.isConnected()) {
//            if (this.systemFacade.c() && !allowRoamingOrDestEq3()) {
//                return EnumDownloadStatus.CANNOT_USE_ROAMING;
//            }
//            if (this.systemFacade.b() && !this.allowMetered) {
//                return EnumDownloadStatus.TYPE_DISALLOWED_BY_REQUESTOR;
//            }
//            if (XlTaskHelper.a().a(a10)) {
//                return EnumDownloadStatus.NO_CONNECTION;
//            }
//            return a(a10.getType(), j10);
//        }
//        return EnumDownloadStatus.NO_CONNECTION;
//    }
//
//    private EnumDownloadStatus a(int i10, long j10) {
//        if (this.isPublicApi) {
//            int b10 = b(i10);
//            a(c.a.a("checkIsNetworkTypeAllowed() networktype: ", i10, " flag: ", b10));
//            int i11 = this.allowedNetworkTypes;
//            if (!(i11 == -1) && (i11 & b10) == 0) {
//                return EnumDownloadStatus.TYPE_DISALLOWED_BY_REQUESTOR;
//            }
//        }
//        return EnumDownloadStatus.OK;
//    }
//
//    */
///* compiled from: DownloadInfo.java *//*
//
//    */
///* renamed from: com.xunlei.download.proguard.b$a *//*
//
//    */
///* loaded from: classes3.dex *//*
//
//    public static class DownloadSchedule {
//
//        */
///* renamed from: a *//*
//
//        private static final String TAG = "DownloadManager.DownloadSchedule";
//
//        */
///* renamed from: b *//*
//
//        private int f6355b = 0;
//
//        */
///* renamed from: c *//*
//
//        private int f6356c = 0;
//
//        */
///* renamed from: d *//*
//
//        private int f6357d = 0;
//
//        */
///* renamed from: e *//*
//
//        private int f6358e = 0;
//
//        */
///* renamed from: f *//*
//
//        private int f6359f = 0;
//
//        */
///* renamed from: g *//*
//
//        private HashSet<Long> f6360g = new HashSet<>();
//
//        */
///* JADX WARN: Code restructure failed: missing block: B:25:0x0079, code lost:
//            if ((((r8.f6355b + r2) + r8.f6356c) + r3) < r0) goto L33;
//         *//*
//
//        */
///* JADX WARN: Code restructure failed: missing block: B:33:0x00a8, code lost:
//            if ((((r2 + r8.f6357d) + r3) + r8.f6358e) < r0) goto L33;
//         *//*
//
//        */
///* JADX WARN: Code restructure failed: missing block: B:34:0x00aa, code lost:
//            r2 = true;
//         *//*
//
//        */
///*
//            Code decompiled incorrectly, please refer to instructions dump.
//            To view partially-correct code enable 'Show inconsistent code' option in preferences
//        *//*
//
//        public boolean a(com.xunlei.download.proguard.DownloadInfoB r9) {
//            */
///*
//                Method dump skipped, instructions count: 308
//                To view this dump change 'Code comments level' option to 'DEBUG'
//            *//*
//
//            throw new UnsupportedOperationException("Method not decompiled: com.xunlei.download.proguard.DownloadInfoB.DownloadSchedule.a(com.xunlei.download.proguard.b):boolean");
//        }
//
//        public void b(DownloadInfo downloadInfo) {
//            if (downloadInfo.is_visible_in_downloads_ui == 0) {
//                return;
//            }
//            if (downloadInfo.task_type != DownloadManager.TaskType.GROUP) {
//                this.f6359f++;
//                if (0 == downloadInfo.group_id) {
//                    if (u.a(downloadInfo.task_type_text)) {
//                        this.f6358e++;
//                    } else {
//                        this.f6356c++;
//                    }
//                }
//            } else {
//                if (u.a(downloadInfo.task_type_text)) {
//                    this.f6357d++;
//                } else {
//                    this.f6355b++;
//                }
//                this.f6360g.add(Long.valueOf(downloadInfo.downloadId));
//            }
//            StringBuilder a10 = android.support.v4.media.e.a("addTask id:");
//            a10.append(downloadInfo.downloadId);
//            a10.append(",mGroupTaskCount = ");
//            a10.append(this.f6355b);
//            a10.append(",mNormalTaskCount = ");
//            a10.append(this.f6356c);
//            a10.append(",mVodGroupTaskCount = ");
//            a10.append(this.f6357d);
//            a10.append(",mVodNormalTaskCount = ");
//            a10.append(this.f6358e);
//            a10.append(",mRealTaskCount = ");
//            a10.append(this.f6359f);
//            XLLog.d(TAG, a10.toString());
//        }
//
//        */
///* renamed from: a *//*
//
//        public void clear() {
//            this.f6355b = 0;
//            this.f6356c = 0;
//            this.f6357d = 0;
//            this.f6358e = 0;
//            this.f6359f = 0;
//            this.f6360g.clear();
//        }
//    }
//
//    public boolean a(ExecutorService executorService) {
//        synchronized (this) {
//            this.time = SystemClock.elapsedRealtime();
//            boolean z10 = true;
//            if (this.status == 190 && this.control == 1) {
//                a("stop pending task");
//                Uri downloadUri = DownloadManager.getInstanceFor(this.context).getDownloadUri(this.downloadId);
//                ContentValues contentValues = new ContentValues();
//                contentValues.put("status", Integer.valueOf((int) Downloads.Impl.STATUS_PAUSED_BY_APP));
//                this.context.getContentResolver().update(downloadUri, contentValues, null, null);
//                return false;
//            }
//            boolean d10 = d();
//            if (!d10 || !downloadSchedule.a(this)) {
//                z10 = false;
//            }
//            a("isReady = " + d10 + ", scheduleOK = " + z10 + ", groupId = " + this.group_id + ",taskid:" + this.downloadId + ", uri = " + this.uri + ", task type = " + this.task_type);
//            if (z10) {
//                if (!isActiveTask()) {
//                    long status = getStatus(this.context, this.downloadId);
//                    if (status != this.status) {
//                        a("create task: task status changed. mStatus = " + this.status + "status=" + status);
//                        p();
//                        return false;
//                    } else if (!o()) {
//                        return false;
//                    } else {
//                        a(this.status);
//                        t a10 = h.a(this.context, this.systemFacade, this, this.storageManager, this.downloadNotifier);
//                        if (a10 == null) {
//                            a10 = new t(this.context, this.systemFacade, this, this.storageManager, this.downloadNotifier);
//                        }
//                        this.runnable = a10;
//                        this.future = executorService.submit(a10);
//                        a("create mSubmittedTask = " + this.future + ",uri = " + this.uri);
//                    }
//                } else {
//                    o();
//                }
//                if (this.status == 192) {
//                    downloadSchedule.b(this);
//                }
//            } else if (d10 && !z10) {
//                int i10 = this.status;
//                if (i10 == 192) {
//                    a("queue task: " + this.title);
//                    ContentValues contentValues2 = new ContentValues();
//                    contentValues2.put(Downloads.Impl.COLUMN_CONTROL, (Integer) 10);
//                    this.context.getContentResolver().update(downloadOrGroupUri(), contentValues2, "control<>1 and deleted<>1", null);
//                } else if (i10 != 190) {
//                    ContentValues contentValues3 = new ContentValues();
//                    contentValues3.put("status", Integer.valueOf((int) Downloads.Impl.STATUS_PENDING));
//                    this.context.getContentResolver().update(downloadOrGroupUri(), contentValues3, null, null);
//                }
//            }
//            return isActiveTask();
//        }
//    }
//
//    public boolean a(e eVar) {
//        boolean j10;
//        synchronized (this) {
//            j10 = j();
//            if (j10) {
//                eVar.a(this);
//            }
//        }
//        return j10;
//    }
//
//    public void a(k kVar) {
//        kVar.println("DownloadInfo:");
//        kVar.a();
//        kVar.a("mId", Long.valueOf(this.downloadId));
//        kVar.a("mLastMod", Long.valueOf(this.lastModifer));
//        kVar.a("mPackage", this.packageName);
//        kVar.a("mUid", Integer.valueOf(this.uid));
//        kVar.println();
//        kVar.a("mUri", this.uri);
//        kVar.println();
//        kVar.a("mMimeType", this.mimeType);
//        kVar.a("mCookies", this.mCookies != null ? "yes" : "no");
//        kVar.a("mReferer", this.referer == null ? "no" : "yes");
//        kVar.a("mUserAgent", this.userAgent);
//        kVar.println();
//        kVar.a("mFileName", this.fileName);
//        kVar.a("mDestination", Integer.valueOf(this.destination));
//        kVar.println();
//        kVar.a("mStatus", Downloads.Impl.statusToString(this.status));
//        kVar.a("mCurrentBytes", Long.valueOf(this.currentBytes));
//        kVar.a("mTotalBytes", Long.valueOf(this.total_bytes));
//        kVar.a("mDownloadFileCount", Long.valueOf(this.download_file_count));
//        kVar.a("mTotalFileCount", Long.valueOf(this.total_file_count));
//        kVar.println();
//        kVar.a("mNumFailed", Integer.valueOf(this.numFailed));
//        kVar.a("mRetryAfter", Integer.valueOf(this.retryAfter));
//        kVar.a("mETag", this.etag);
//        kVar.a("mIsPublicApi", Boolean.valueOf(this.isPublicApi));
//        kVar.println();
//        kVar.a("mAllowedNetworkTypes", Integer.valueOf(this.allowedNetworkTypes));
//        kVar.a("mAllowRoaming", Boolean.valueOf(this.allowRoaming));
//        kVar.a("mAllowMetered", Boolean.valueOf(this.allowMetered));
//        kVar.println();
//        kVar.b();
//    }
//
//    */
///* renamed from: a *//*
//
//    public void startSizeLimitActivity(boolean z10, long totalBytes) {
//        if (this.task_type == DownloadManager.TaskType.GROUP || this.group_id != 0) {
//            return;
//        }
//        Intent intent = new Intent("android.intent.action.VIEW");
//        intent.setData(downloadOrGroupUri());
//        intent.setClassName(this.packageName, SizeLimitActivity.class.getName());
//        intent.setFlags(268435456);
//        intent.putExtra(COLUMN_WIFI_REQUIRED, z10);
//        intent.putExtra("total_bytes", totalBytes);
//        this.context.startActivity(intent);
//    }
//
//    */
///* renamed from: a *//*
//
//    public static int getStatus(Context context, long taskId) {
//        Cursor cursor;
//        try {
//            cursor = context.getContentResolver().query(DownloadManager.getInstanceFor(context).getDownloadUri(taskId), new String[]{"status"}, null, null, null);
//        } catch (Exception e10) {
//            e10.printStackTrace();
//            XLLog.printStackTrace(e10);
//            cursor = null;
//        }
//        if (cursor != null) {
//            try {
//                return cursor.moveToFirst() ? cursor.getInt(0) : Downloads.Impl.STATUS_PENDING;
//            } finally {
//                cursor.close();
//            }
//        }
//        return Downloads.Impl.STATUS_PENDING;
//    }
//
//    private void a(String str) {
//        long elapsedRealtime = SystemClock.elapsedRealtime();
//        long j10 = elapsedRealtime - this.time;
//        if (j10 > a.f6304x) {
//            StringBuilder a10 = android.support.v4.media.e.a("[");
//            a10.append(this.downloadId);
//            a10.append("] ");
//            a10.append(j10);
//            a10.append(" ");
//            a10.append(str);
//            XLLog.w("DownloadManager", a10.toString());
//        } else {
//            StringBuilder a11 = android.support.v4.media.e.a("[");
//            a11.append(this.downloadId);
//            a11.append("] ");
//            a11.append(j10);
//            a11.append(" ");
//            a11.append(str);
//            XLLog.d("DownloadManager", a11.toString());
//        }
//        this.time = elapsedRealtime;
//    }
//
//    public void a(Runnable runnable) {
//        Runnable runnable2 = this.runnable;
//        if (runnable2 instanceof t) {
//            ((t) runnable2).a(runnable);
//        }
//    }
//}*/
