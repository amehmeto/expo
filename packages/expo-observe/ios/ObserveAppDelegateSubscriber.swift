// Copyright 2025-present 650 Industries. All rights reserved.

import ExpoAppMetrics
import ExpoModulesCore

public class ObserveAppDelegateSubscriber: ExpoAppDelegateSubscriber {
  public func appDelegateWillBeginInitialization() {
    // Same condition as the app metrics launch cleanup, so no setting outlives the deleted data.
    if !ObserveConsent.isGranted {
      ObserveUserDefaults.deleteStoredSettings()
    }
  }

  public func applicationWillResignActive(_ application: UIApplication) {
    AppMetricsActor.isolated {
      await ObservabilityManager.dispatch()
    }
  }

  public func applicationWillTerminate(_ application: UIApplication) {
    AppMetricsActor.isolated {
      await ObservabilityManager.dispatch()
    }
  }
}
