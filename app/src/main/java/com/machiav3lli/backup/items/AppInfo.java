/*
 * OAndBackupX: open-source apps backup and restore app.
 * Copyright (C) 2020  Antonios Hazim
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.machiav3lli.backup.items;

import android.app.usage.StorageStats;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class AppInfo implements Parcelable {
    public static final int MODE_UNSET = 0;
    public static final int MODE_APK = 1;
    public static final int MODE_DATA = 2;
    public static final int MODE_BOTH = 3;
    public static final Parcelable.Creator<AppInfo> CREATOR = new Parcelable.Creator<AppInfo>() {
        public AppInfo createFromParcel(Parcel in) {
            return new AppInfo(in);
        }

        public AppInfo[] newArray(int size) {
            return new AppInfo[size];
        }
    };

    private Bitmap icon;
    private LogFile logInfo;
    private String label, packageName, versionName, sourceDir, dataDir, deviceProtectedDataDir;
    private String[] splitSourceDirs;
    private int versionCode, backupMode;
    private long appSize, dataSize, cacheSize;
    private boolean system, installed, checked, disabled;

    public AppInfo(String packageName, String label, String versionName, int versionCode, String sourceDir, String[] splitSourceDirs, String dataDir, String deviceProtectedDataDir, boolean system, boolean installed) {
        this.label = label;
        this.packageName = packageName;
        this.versionName = versionName;
        this.versionCode = versionCode;
        this.sourceDir = sourceDir;
        this.splitSourceDirs = splitSourceDirs;
        this.dataDir = dataDir;
        this.deviceProtectedDataDir = deviceProtectedDataDir;
        this.system = system;
        this.installed = installed;
        this.backupMode = MODE_UNSET;
    }

    protected AppInfo(Parcel in) {
        logInfo = in.readParcelable(getClass().getClassLoader());
        label = in.readString();
        packageName = in.readString();
        versionName = in.readString();
        sourceDir = in.readString();
        splitSourceDirs = in.createStringArray();
        dataDir = in.readString();
        deviceProtectedDataDir = in.readString();
        versionCode = in.readInt();
        backupMode = in.readInt();
        boolean[] bools = new boolean[4];
        in.readBooleanArray(bools);
        system = bools[0];
        installed = bools[1];
        checked = bools[2];
        icon = in.readParcelable(getClass().getClassLoader());
        appSize = in.readLong();
        dataSize = in.readLong();
        cacheSize = in.readLong();
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getLabel() {
        return label;
    }

    public String getVersionName() {
        return versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public String[] getSplitSourceDirs() {
        return splitSourceDirs;
    }

    public String getDataDir() {
        return dataDir;
    }

    public String getDeviceProtectedDataDir() {
        return deviceProtectedDataDir;
    }

    public File getExternalFilesPath(Context context) {
        // Uses the context to get own external data directory
        // e.g. /storage/emulated/0/Android/data/com.machiav3lli.backup/files
        // Goes to the parent two times to the leave own directory
        // e.g. /storage/emulated/0/Android/data
        String externalFilesPath = context.getExternalFilesDir(null).getParentFile().getParentFile().getAbsolutePath();
        // Add the package name to the path assuming that if the name of dataDir does not equal the
        // package name and has a prefix or a suffix to use it.
        return new File(externalFilesPath, new File(this.dataDir).getName());
    }

    public File getObbFilesPath(Context context) {
        // Uses the context to get own obb data directory
        // e.g. /storage/emulated/0/Android/obb/com.machiav3lli.backup
        // Goes to the parent two times to the leave own directory
        // e.g. /storage/emulated/0/Android/obb
        String obbFilesPath = context.getObbDir().getParentFile().getAbsolutePath();
        // Add the package name to the path assuming that if the name of dataDir does not equal the
        // package name and has a prefix or a suffix to use it.
        return new File(obbFilesPath, new File(this.dataDir).getName());
    }

    public int getBackupMode() {
        return backupMode;
    }

    public void setBackupMode(int modeToAdd) {
        // add only if both values are different and neither is MODE_BOTH
        if (backupMode == MODE_BOTH || modeToAdd == MODE_BOTH)
            backupMode = MODE_BOTH;
        else if (modeToAdd != backupMode)
            backupMode += modeToAdd;
    }

    public long getAppSize() {
        return appSize;
    }

    public long getDataSize() {
        return dataSize;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public LogFile getLogInfo() {
        return logInfo;
    }

    public void setLogInfo(LogFile newLogInfo) {
        logInfo = newLogInfo;
        backupMode = logInfo.getBackupMode();
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isSystem() {
        return system;
    }

    public boolean isInstalled() {
        return installed;
    }

    public boolean isSplit() {
        return splitSourceDirs != null && splitSourceDirs.length > 0;
    }

    // list of single files used by special backups - only for compatibility now
    public String[] getFilesList() {
        return new String[0];
    }

    // should ideally be removed once proper polymorphism is implemented
    public boolean isSpecial() {
        return false;
    }

    @NotNull
    public String toString() {
        return String.format("%s [%s]", this.packageName, this.label);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(logInfo, flags);
        out.writeString(label);
        out.writeString(packageName);
        out.writeString(versionName);
        out.writeString(sourceDir);
        out.writeStringArray(splitSourceDirs);
        out.writeString(dataDir);
        out.writeString(deviceProtectedDataDir);
        out.writeInt(versionCode);
        out.writeInt(backupMode);
        out.writeBooleanArray(new boolean[]{system, installed, checked});
        out.writeParcelable(icon, flags);
        out.writeLong(appSize);
        out.writeLong(dataSize);
        out.writeLong(cacheSize);
    }

    public void addSizes(StorageStats storageStats) {
        appSize = storageStats.getAppBytes();
        cacheSize = storageStats.getCacheBytes();
        dataSize = storageStats.getDataBytes() - cacheSize;
    }
}
