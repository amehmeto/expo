---
'expo-modules-jsi': minor
---

[iOS] Make `JavaScriptRuntime.Scheduler` public, with `runtime.scheduler`, `schedule(priority:_:)` and `mayBeAlive`, so native code can schedule work on the JS thread of a runtime from any thread without keeping the runtime alive.
