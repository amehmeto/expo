package expo.modules.observe

import android.app.Application
import android.content.Context
import expo.modules.appmetrics.ObserveConsent
import expo.modules.core.interfaces.ApplicationLifecycleListener
import expo.modules.core.interfaces.Package

class ObservePackage : Package {
  override fun createApplicationLifecycleListeners(context: Context?): List<ApplicationLifecycleListener> =
    listOf(ObserveApplicationLifecycleListener())
}

internal class ObserveApplicationLifecycleListener : ApplicationLifecycleListener {
  override fun onCreate(application: Application) {
    // The order of the package listeners is not fixed, so this loads the consent too. Both loads
    // read the same embedded value before any code can change it, so they agree.
    ObserveConsent.load(application)
    // Same condition and process step as the app metrics launch cleanup, so no setting outlives
    // the deleted data, also in a process that never starts React.
    if (!ObserveConsent.isGranted) {
      application.deleteDatabase(LEGACY_DATABASE_NAME)
      ObservePreferences.deleteStoredSettings(application)
    }
  }
}
