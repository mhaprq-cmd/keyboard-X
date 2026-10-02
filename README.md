# Keyboard X

## نظرة عامة

Keyboard X هو تطبيق لوحة مفاتيح Android Native حقيقي، مكتوب بلغة Kotlin. يوفر خدمة إدخال فعلية (IME - Input Method Editor) تدعم اللغات العربية والإنجليزية.

## المشروع في المرحلة الأولى

هذا المشروع حاليًا في **المرحلة الأولى (Phase 1)** - مرحلة تأسيس الهيكل الأساسي.

### معايير النجاح للمرحلة الأولى

- ✅ مشروع Android Native حقيقي
- ✅ اسم الحزمة: `com.keyboardx.app`
- ✅ لغة البرمجة: Kotlin
- ✅ إصدار التطبيق: 0.1.0
- ✅ خدمة InputMethodService معرفة
- ✅ تكوين رسمي صحيح في AndroidManifest.xml
- ✅ ملفات موارد IME المطلوبة
- ✅ بنية مشروع نظيفة وقابلة ل��توسع
- ✅ لا توجد WebView أو PWA
- ✅ اعتماديات محدودة وضرورية فقط
- ✅ جاهز لربط GitHub Actions في المرحلة 2

## بنية المشروع

```
keyboard-X/
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
└── README.md
```

## متطلبات البناء

| الأداة | الإصدار | الملاحظات |
|-------|---------|----------|
| Android Gradle Plugin (AGP) | 8.7.0 | الأحدث والمستقر |
| Gradle | ✓ مدار بـ wrapper | متوافق مع AGP 8.7.0 |
| Kotlin | 2.0.10 | متوافق مع AGP 8.7.0 |
| Compile SDK | 35 | أحدث إصدار مستقر |
| Target SDK | 35 | أحدث إصدار مستقر |
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

### البناء المحلي

```bash
./gradlew build
```

### بناء APK للتطوير

```bash
./gradlew assembleDebug
```

### بناء Release

```bash
./gradlew assembleRelease
```

**ملاحظة:** البناء التلقائي والتوقيع والنشر سيتم إعدادها عبر GitHub Actions في **المرحلة 2**.

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

### المرحلة 2: إعداد نظام البناء عبر GitHub Actions
- تكوين خط أنابيب CI/CD
- بناء وتجميع التطبيق تلقائيًا
- تشغيل الاختبارات الآلية
- توقيع التطبيق تلقائيًا
- نشر artifacts (APK/AAB)

### المراحل اللاحقة
- تطوير واجهة لوحة المفاتيح (Keyboard View)
- إضافة تخطيطات مفاتيح متعددة
- دعم اللغات المتعددة بشكل كامل
- ميزات متقدمة:
  - تصحيح تلقائي
  - اقتراحات الكلمات
  - ثيمات مخصصة
  - إعدادات مستخدم
  - إحصائيات الاستخدام

## القيود الحالية (Phase 1)

- لا توجد واجهة رسومية فعالة للوحة المفاتيح
- لا توجد ميزات متقدمة
- لا توجد قاعدة بيانات
- لا توجد تحليلات
- لا Firebase أو Supabase
- لا GitHub Actions بعد

هذه ستُضاف تدريجيًا في المراحل اللاحقة.

## معايير الجودة والتوافقية

- ✅ توافقية كاملة مع أدوات البناء الحديثة
- ✅ لا توجد إعدادات قديمة أو مهجورة
- ✅ IME حقيقي Native (بدون WebView أو PWA)
- ✅ بدون اعتماديات غير ضرورية
- ✅ كود نظيف وقابل للصيانة

## الترخيص

سيتم تحديد الترخيص لاحقًا.

## المطور

Mohammed Nasser

---

**آخر تحديث:** Phase 1 Foundation Finalized - 2026-10-02
