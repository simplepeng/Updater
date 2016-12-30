# Updater

基于DownloadManager封装的更新器，更简单的操作，更丰富的自定义，一行代码搞定应用更新，断点续传完全交给系统，再也不用自己操心。零依赖，不必再添加其他网络框架的依赖，DownloadManager在Android SDK中就已经集成。

## 样例

<img src="https://raw.githubusercontent.com/simplepeng/Updater/master/image/updater_1.png" width = "150" height = "250">
<img src="https://raw.githubusercontent.com/simplepeng/Updater/master/image/updater_2.png" width = "150" height = "250">

这里notification上的图标是Android小机器人可以不用担心，DownloadManager封装的
notification会自己找该应用的icon，也就是说还是会变成你应用的icon的。
## 添加依赖

> compile 'com.simplepeng:updaterlibrary:1.0.0'

## 使用

### 开始下载

```java
updater = new Updater.Builder(getApplicationContext())
                     .setDownloadUrl(url)
                     .setApkName("test.apk")
                     .setNotificationTitle("updater")
                     .start();
```

默认下载路径在sd的Download目录(推荐做法)。<p>
如果想自定义目录（默认是sd下的目录），可以调用
> setApkDir(String dirName)

示例：setApkDir("test")

或者自定义全路径
> setApkPath(String apkPath)

示例：setApkPath(Environment.getExternalStorageDirectory().getAbsolutePath())

### 注册监听下载完成的广播，会自动安装apk（非静默）

* 推荐在manifest中静态注册

```xml
<receiver android:name="com.simplepeng.updaterlibrary.DownloadReceiver">
     <intent-filter >
          <action android:name="android.intent.action.DOWNLOAD_COMPLETE"/>
     </intent-filter>
</receiver>
```

* 动态注册（不推荐）

```java
updater.registerDownloadReceiver();
```

如果需要解绑监听器，记得调用该方法

```java
 updater.unRegisterDownloadReceiver();
```

### 需要的权限

```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<!--调用hideNotification不显示notification需要的权限-->
<uses-permission android:name="android.permission.DOWNLOAD_WITHOUT_NOTIFICATION" />
```

### 监听下载进度

一般不需要，看自己业务需求，notification上已经有进度显示了

```java
updater.addProgressListener(new ProgressListener() {
         @Override
          public void onProgressChange(long totalBytes, long curBytes, int progress) {
                        
                    }
                });
```

### 方法概括

Updater.Builder中的方法

* setApkName(String apkName) 设置下载下来的apk文件名
* setApkPath(String apkPath) 设置apk下载的路径（全路径）
* setApkDir(String dirName) 设置下载apk的文件目录
* setDownloadUrl(String downloadUrl)  设置下载的链接地址
* setNotificationTitle(String title) 通知栏显示的标题
* hideNotification() 隐藏通知栏
* debug() 是否为debug模式，会输出很多log信息（手动斜眼）
* allowedOverRoaming() 允许漫游网络可下载
* start() 开始下载

Updater中的方法

* registerDownloadReceiver 注册下载完成的监听
* unRegisterDownloadReceiver() 解绑下载完成的监听
* addProgressListener(ProgressListener progressListener) 添加下载进度回调
* removeProgressListener(ProgressListener progressListener) 移除下载进度回调

## 关于

* 邮箱 ：simple19930611@gmail.com
* QQ : 383559698
* QQ群 ：Android进阶开发 274306954

## License

Copyright 2016 simple peng

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. 