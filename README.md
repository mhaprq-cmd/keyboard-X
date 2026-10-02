# Keyboard X

## نظرة عامة

Keyboard X هو تطبيق لوحة مفاتيح Android Native حقيقي، مكتوب بلغة Kotlin. يوفر خدمة إدخال فعلية (IME - Input Method Editor) تدعم اللغات المختلفة.

## حالة المشروع الحالية

هذا المشروع حاليًا في **المرحلة الثانية (Phase 2)** - مرحلة إعداد نظام البناء عبر GitHub Actions.

### معايير النجاح للمرحلة الأولى (مكتملة ✅)

- ✅ مشروع Android Native حقيقي
- ✅ اسم الحزمة: `com.keyboardx.app`
- ✅ لغة البرمجة: Kotlin
- ✅ إصدار التطبيق: 0.1.0
- ✅ خدمة InputMethodService معرفة
- ✅ تكوين رسمي صحيح في AndroidManifest.xml
- ✅ ملفات موارد IME المطلوبة
- ✅ بنية مشروع نظيفة وقابلة للتوسع
- ✅ لا توجد WebView أو PWA
- ✅ اعتماديات محدودة وضرورية فقط

### معايير النجاح للمرحلة الثانية (مكتملة ✅)

- ✅ تكوين GitHub Actions Workflow
- ✅ بناء تلقائي عند كل push و pull request
- ✅ إعداد أدوات البناء تلقائيًا (Java 17، Android SDK 35، Gradle)
- ✅ بناء Debug APK بنجاح
- ✅ رفع APK كـ Artifact قابل للتنزيل
- ✅ رفع سجلات البناء عند الفشل

## بنية المشروع

```
keyboard-X/
├── .github/
│   └── workflows/
│       └── build.yml
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/
│   │   │   │   └── com/keyboardx/app/
│   │   │   │       ├── MainActivity.kt
│   │   │   │       └── ime/
│   │   │   │           └── KeyboardIMEService.kt
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   ├── values/
│   │   │   │   ├── xml/
│   │   │   │   └── mipmap/
│   │   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── PHASE_1_CHECKLIST.md
└── README.md
```

## متطلبات البناء

| الأداة | الإصدار | الملاحظات |
|-------|---------|----------|
| Android Gradle Plugin (AGP) | 8.7.0 | متوافق مع Gradle 8.9 |
| Gradle | ✓ مُدار بواسطة wrapper | متوافق مع AGP 8.7.0 |
| Kotlin | 2.0.10 | متوافق مع AGP 8.7.0 |
| Compile SDK | 35 | Android SDK المُكوّن في CI |
| Target SDK | 35 | مطابق لملف Gradle الحالي |
| Min SDK | 24 | Android 7.0 |
| Java/JVM | 17 | متوافق مع أدوات البناء الحديثة |

## الاعتماديات

```kotlin
// AndroidX Core - Kotlin Extensions
androidx.core:core-ktx:1.13.1

// AndroidX AppCompat - Backward compatibility
androidx.appcompat:appcompat:1.7.0

// Testing Libraries
junit:junit:4.13.2
androidx.test.ext:junit:1.1.5
androidx.test.espresso:espresso-core:3.5.1
```

**ملاحظة:** جميع الاعتماديات هي الحد الأدنى المطلوب لتشغيل تطبيق IME حقيقي. لا توجد اعتماديات خارجية غير ضرورية.

## كيفية البناء

### البناء عبر GitHub Actions

يتم تشغيل Workflow تلقائيًا عند كل push أو pull request على الفروع: `main`, `master`, `develop`.

**ما يحدث في الـ Workflow:**

1. يتم إعداد Java 17 من Temurin
2. يتم تثبيت Android SDK وplatforms;android-35 وbuild-tools;35.0.0
3. يتم إعداد Gradle تلقائيًا
4. يتم بناء Debug APK
5. يتم رفع APK كـ Artifact بـ اسم `keyboardx-debug-apk` (قابل للتنزيل لمدة 30 يوم)
6. في حالة الفشل، يتم رفع سجلات البناء

**للوصول إلى الـ Artifacts:**

- اذهب إلى GitHub Actions في المستودع
- افتح آخر workflow run
- حمّل الـ Artifact المسمى `keyboardx-debug-apk`

### البناء المحلي

```bash
./gradlew assembleDebug
```

## IME Configuration

يتم تعريف خدمة IME بشكل صحيح في:

- **AndroidManifest.xml**: تعريف الخدمة مع Intent Filter الصحيح
- **res/xml/method.xml**: ملف تعريف IME مع الإعدادات الأساسية
- **KeyboardIMEService.kt**: تنفيذ InputMethodService الأساسي

### تفعيل لوحة المفاتيح

بعد تثبيت التطبيق:

1. اذهب إلى Settings → Language & Input → Virtual keyboard
2. ابحث عن "Keyboard X"
3. فعّله كلوحة مفاتيح افتراضية

## التطوير المستقبلي

### المرحلة 3: تطوير واجهة لوحة المفاتيح
- تطوير Keyboard View الأساسية
- إضافة تخطيطات مفاتيح أساسية
- إضافة دعم إدخال النصوص

### المراحل اللاحقة
- دعم اللغات المتعددة بشكل كامل
- ميزات متقدمة:
  - تصحيح تلقائي
  - اقتراحات الكلمات
  - ثيمات مخصصة
  - إعدادات مستخدم
  - إحصائيات الاستخدام

## القيود الحالية (Phase 2)

- لا توجد واجهة رسومية فعالة للوحة المفاتيح
- لا توجد ميزات متقدمة
- لا توجد قاعدة بيانات
- لا توجد تحليلات
- لا Firebase أو Supabase

هذه ستُضاف تدريجيًا في المراحل اللاحقة.

## معايير الجودة والتوافقية

- ✅ توافقية كاملة مع أدوات البناء الحديثة
- ✅ لا توجد إعدادات قديمة أو مهجورة
- ✅ IME حقيقي Native (بدون WebView أو PWA)
- ✅ بدون اعتماديات غير ضرورية
- ✅ كود نظيف وقابل للصيانة
- ✅ CI/CD محسّن عبر GitHub Actions

## الترخيص

سيتم تحديد الترخيص لاحقًا.

## المطور

Mohammed Nasser

---

**آخر تحديث:** Phase 2 CI/CD Integration Complete - 2026-10-02
