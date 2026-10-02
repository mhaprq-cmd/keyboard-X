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
- ✅ بنية مشروع نظيفة وقابلة للتوسع
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
│   │   │   │           ├── KeyboardIMEService.kt
│   │   │   │           ├── KeyboardView.kt
│   │   │   │           └── KeyboardLayout.kt
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
└── gradle.properties
```

## المتطلبات

- Android SDK 24+ (Min SDK)
- Target SDK 34
- Kotlin 1.9.22
- Gradle 8.2.0
- Java 8+

## الاعتماديات

- **AndroidX Core**: `androidx.core:core-ktx`
- **AndroidX AppCompat**: `androidx.appcompat:appcompat`
- **Testing**: JUnit 4 و Espresso

## كيفية البناء

سيتم إعداد البناء الفعلي عبر GitHub Actions في المرحلة 2.

للبناء المحلي:

```bash
./gradlew build
```

## التطوير المستقبلي

### المرحلة 2: إعداد GitHub Actions
- تكوين خط أنابيب CI/CD
- بناء وتجميع التطبيق تلقائيًا
- اختبار التطبيق

### المراحل اللاحقة
- تطوير واجهة لوحة المفاتيح
- إضافة تخطيطات لوحة المفاتيح
- دعم اللغات المتعددة
- ميزات متقدمة (تصحيح تلقائي، اقتراحات، إلخ)

## القيود الحالية

في هذه المرحلة:
- لا توجد واجهة رسومية فعالة للوحة المفاتيح
- لا توجد ميزات متقدمة
- لا توجد قاعدة بيانات
- لا تحليلات
- لا Firebase أو Supabase

## الترخيص

سيتم تحديد الترخيص لاحقًا.

## المطور

Mohammed Nasser