# AppStore 模块 —— 第三方集成文档

## 一、模块简介

AppStore 是一个 Android Library，为宿主 App 提供**企业级应用市场**功能，支持应用展示、下载安装、更新管理、搜索、评论评分等能力。

- **命名空间**: `com.oortcloud.appstore`
- **artifactId**: `AppStore`
- **技术栈**: RxJava2 + Retrofit2 + LitePal + Glide

---

## 二、集成方式

AppStore 模块打包在 `omm-lib` 中，提供了以下四种集成方式。

### 2.1 方式一：GitHub + JitPack（公开仓库）

适用于 GitHub 公开仓库，无需认证。

**前置条件：创建 GitHub Release**

1. 访问 https://github.com/Bean-V/WorkUp-SDK/releases
2. 点击 **Create a new release**
3. 选择 Tag version（如 `v1.0.0`），填写 Release title 和描述
4. 点击 **Publish release**
5. 访问 https://jitpack.io/#Bean-V/WorkUp-SDK 触发构建
6. 等待构建完成（首次约 5-10 分钟）

> ⚠️ 必须先创建 Release 并在 JitPack 构建成功，才能添加依赖

**1. 添加仓库**

在项目根目录 `build.gradle` 中：

```gradle
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

**2. 添加依赖**

在 App 模块 `build.gradle` 中：

```gradle
dependencies {
    implementation 'com.github.Bean-V.WorkUp-SDK:omm-lib:1.0.0'
}
```

---

### 2.2 方式二：Nexus Maven（企业内网）

适用于企业内部网络，私有部署。

**1. 添加仓库**

```gradle
allprojects {
    repositories {
        maven {
            url = "http://192.168.88.218:8403/repository/workup-sdk-releases/"
            allowInsecureProtocol = true
        }
    }
}
```

**2. 添加依赖**

```gradle
dependencies {
    implementation 'com.oort.workup-sdk:omm-lib:1.0.0'
}
```

---

### 2.3 方式三：GitLab Maven（备用方案）

适用于使用 GitLab Raw URL 访问独立的 Maven Git 仓库。

**1. 添加仓库**

```gradle
allprojects {
    repositories {
        maven {
            url = "http://192.168.88.125/zhangzhijun/workup-sdk-maven/raw/master"
            allowInsecureProtocol = true

            // 如果仓库是私有的，需要添加认证
            credentials(HttpHeaderCredentials) {
                name = "Private-Token"
                value = "your-gitlab-token"
            }

            authentication {
                header(HttpHeaderAuthentication)
            }
        }
    }
}
```

**2. 添加依赖**

```gradle
dependencies {
    implementation 'com.oort.workup-sdk:omm-lib:1.0.0'
}
```

---

### 2.4 方式四：本地 AAR 文件（离线集成）

直接使用 `omm-lib-1.0.0.aar` 文件进行本地集成。

**1. 放置 AAR 文件**

将 `omm-lib-1.0.0.aar` 复制到 App 模块的 `libs/` 目录下。

**2. 添加依赖**

```gradle
// 在 App 模块 build.gradle 中
repositories {
    flatDir {
        dirs 'libs'
    }
}

dependencies {
    implementation files('libs/omm-lib-1.0.0.aar')
    // 或
    // implementation(name: 'omm-lib-1.0.0', ext: 'aar')
}
```

---

### 2.5 前置依赖

AppStore（包含在 `omm-lib` 中）依赖以下模块，使用远程依赖时这些会通过 POM 自动引入。使用本地 AAR 时需确保宿主已引入：

| 依赖模块 | 说明 |
|---------|------|
| `:core` | 基础框架（用户信息、网络常量等） |
| `:ooortCloudDisk` | 云盘模块（应用市场内打开云盘时使用） |

---

## 三、初始化

### 3.1 设置 Application

在你的 `Application.onCreate()` 中：

```java
AppStoreInit.setApplication(this);
```

### 3.2 初始化数据

用户登录成功后，调用 `initData()` 初始化应用市场数据（安装记录、模块列表）：

```java
AppStoreInit.getInstance().initData(token, uuid);
```

参数说明：

| 参数 | 类型 | 说明 | 获取方式 |
|------|------|------|---------|
| `token` | String | 用户身份令牌 | 登录接口返回 |
| `uuid` | String | 用户唯一标识 | `UserInfo.getOort_uuid()` |

### 3.3 配置 API 地址

后端接口地址通过 `Constant.BASE_URL` 配置，该常量位于 `:core` 模块中。确保在项目初始化时已正确设置。

```java
// 示例：在 Application 中设置
Constant.BASE_URL = "https://your-server.com/";
```

---

## 四、页面跳转 API

### 4.1 打开应用详情页

```java
// AppInfo 可从列表接口获取或自行构建
AppDetailedActivity.actionStart(context, appInfo);
```

`AppInfo` 关键字段说明：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `uid` | String | 是 | 应用唯一标识 |
| `applabel` | String | 是 | 应用显示名称 |
| `apppackage` | String | 是 | 应用包名 |
| `versioncode` | int | 是 | 版本号 |
| `terminal` | int | 是 | 终端类型（0=APK / 1=H5 / 2=Web） |
| `apk_url` | String | 按需 | APK 下载地址（terminal=0 时必填） |
| `icon_url` | String | 按需 | 图标地址 |
| `oneword` | String | 否 | 一句话简介 |
| `apply_status` | int | 否 | 申请状态（0=不可用/1=可用/2=可申请/3=已申请） |

### 4.2 打开搜索页

```java
Intent intent = new Intent(context, SearchActivity.class);
context.startActivity(intent);
```

### 4.3 打开应用管理页

```java
// 方式一：通过 Intent Action
Intent intent = new Intent("com.oortcloud.appstore.app.appManager");
context.startActivity(intent);

// 方式二：直接跳转 Activity
Intent intent = new Intent(context, AppManagerActivity.class);
context.startActivity(intent);
```

### 4.4 打开分类全部应用页

```java
Intent intent = new Intent(context, TypeAppAllActivity.class);
context.startActivity(intent);
```

---

## 五、网络请求 API

所有 API 通过 `HttpRequestCenter` 调用，返回 `Observable<String>`，由调用方自行订阅解析。

### 5.1 应用列表

```java
// 获取推荐页数据
HttpRequestCenter.postRecommend().subscribe(observer);

// 获取分类列表
HttpRequestCenter.postClassifyList().subscribe(observer);

// 获取分类下应用列表（分页）
HttpRequestCenter.postClassifyAppMore(classifyUid, pageNum, pageSize).subscribe(observer);

// 搜索应用
HttpRequestCenter.postSearch("关键词").subscribe(observer);

// 当月最新应用
HttpRequestCenter.monthNewApp(pageNum, pageSize).subscribe(observer);

// 我的应用
HttpRequestCenter.myApp(pageNum, pageSize).subscribe(observer);
```

### 5.2 安装管理

```java
// 记录应用安装
HttpRequestCenter.appInstall(label, packageName, classify, uid, versionCode, terminal)
    .subscribe(observer);

// 获取已安装应用列表
HttpRequestCenter.appInstallList(classify).subscribe(observer);

// 获取可更新应用列表
HttpRequestCenter.appUpdateList().subscribe(observer);

// 检测版本更新（返回最新版本信息）
HttpRequestCenter.verifyversioncode(packageName, currentVersionCode).subscribe(observer);
```

### 5.3 评论评分

```java
// 获取评论列表
HttpRequestCenter.replySystemList(pageNum, appUid).subscribe(observer);

// 获取评分
HttpRequestCenter.getGrade(appUid).subscribe(observer);

// 发表评论
HttpRequestCenter.replySystemAdd(content, parentId, replyType, appUid).subscribe(observer);

// 评分
HttpRequestCenter.replySystemGrade(replyTargetId, score).subscribe(observer);
```

### 5.4 响应格式

所有接口统一返回 JSON 格式（由 `Result<T>` 封装）：

```json
{
    "code": 200,
    "message": "success",
    "data": { ... }
}
```

| 常用 code | 含义 |
|-----------|------|
| 200 | 成功 |
| 50010 | 已是最新版本（版本校验场景） |
| 50011 | 有新版本可用 |

---

## 六、下载与安装

### 6.1 直接触发下载安装

```java
// 初始化下载管理器
DownloadManager downloadManager = DownloadManager.getInstance();

// 创建下载监听
DownloadListener listener = new DownloadListener(appInfo, progressButton);

// 启动下载
downloadManager.startDownload(appInfo, listener);
```

### 6.2 通过 AppEventUtil 自动处理

`AppEventUtil.onClick()` 封装了完整的应用处理逻辑，会根据 `terminal` 类型自动执行下载/安装/打开：

```java
// 适用于应用列表/详情页的点击事件
AppEventUtil.onClick(appInfo, downloadListener);
```

其内部处理逻辑：

| terminal | 行为 |
|----------|------|
| 0 (APK) | 检查本地是否已安装 → 检查 APK 文件是否存在 → 下载 → 安装 |
| 1 (H5) | 检查 ZIP 文件是否存在 → 下载 → 解压 → 打开 |
| 2 (Web) | 直接通过 WebView 打开 URL |
| 3 (PC) | 提示"手机端不能使用" |

---

## 七、权限声明

模块已在 AndroidManifest.xml 中声明以下权限，宿主**无需重复声明**：

| 权限 | 用途 |
|------|------|
| `android.permission.INTERNET` | 网络请求 |
| `android.permission.READ_EXTERNAL_STORAGE` | 读取下载文件 |
| `android.permission.WRITE_EXTERNAL_STORAGE` | 写入下载文件 |
| `android.permission.REQUEST_INSTALL_PACKAGES` | APK 安装 |
| `android.permission.REQUEST_DELETE_PACKAGES` | APK 卸载 |
| `android.permission.VIBRATE` | 振动反馈 |

宿主 App 需在**运行时**主动申请存储权限和安装权限（模块已封装权限检查逻辑，会在必要时弹窗引导用户）。

---

## 八、常见问题

**Q: 初始化时提示 token/uuid 为空？**

A: 确保在调用 `initData()` 之前，用户已成功登录，且 `FastSharedPreferences` 中已保存 `token` 信息。

**Q: 应用详情页打开后立即关闭？**

A: 检查 `appInfo` 对象是否为 null。详情页启动时要求传入非空的 `AppInfo` 实例。

**Q: 下载后无法安装 APK？**

A: Android 8+ 需要引导用户开启「安装未知应用」权限；Android 11+ 还可能需申请 `MANAGE_EXTERNAL_STORAGE` 权限。模块已内置相关逻辑，用户按提示操作即可。

**Q: 切换用户后数据未刷新？**

A: 切换用户后需重新调用 `AppStoreInit.getInstance().initData(newToken, newUuid)` 刷新本地数据。

**Q: 模块使用了系统签名？**

A: 模块 Manifest 中声明了 `android:sharedUserId="android.uid.system"`，如需卸载或独立使用该功能，请移除该属性。
