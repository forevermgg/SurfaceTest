
```
#include <cpu-features.h>
bool cpuSupportsSimd() {
    AndroidCpuFamily family = android_getCpuFamily();
    uint64_t features = android_getCpuFeatures();

    if (family == ANDROID_CPU_FAMILY_ARM && (features & ANDROID_CPU_ARM_FEATURE_NEON)) {
        // ALOGI("Arm with Neon");
        return true;
    } else if (family == ANDROID_CPU_FAMILY_ARM64 && (features & ANDROID_CPU_ARM64_FEATURE_ASIMD)) {
        // ALOGI("Arm64 with ASIMD");
        return true;
    } else if ((family == ANDROID_CPU_FAMILY_X86 || family == ANDROID_CPU_FAMILY_X86_64) &&
               (features & ANDROID_CPU_X86_FEATURE_SSSE3)) {
        // ALOGI("x86* with SSE3");
        return true;
    }
    // ALOGI("Not simd");
    return false;
}

```
