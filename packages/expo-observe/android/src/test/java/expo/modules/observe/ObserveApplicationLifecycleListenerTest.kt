package expo.modules.observe

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import expo.modules.appmetrics.ObserveConsent
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [28])
class ObserveApplicationLifecycleListenerTest {
  private val application: Application = ApplicationProvider.getApplicationContext()

  @Before
  fun setUp() {
    application.getSharedPreferences("dev.expo.observe", Context.MODE_PRIVATE).edit().clear().commit()
    ObservePreferences.setLastDispatchedMetricId(application, 42)
    mockkObject(ObserveConsent)
    // The test app has no embedded app config, so the test sets the loaded consent.
    every { ObserveConsent.load(any()) } just Runs
  }

  @After
  fun tearDown() {
    unmockkAll()
  }

  @Test
  fun `a launch without consent loads the consent, then deletes the settings and the legacy database`() {
    val legacyDatabase = application.getDatabasePath("eas_observe").apply {
      parentFile?.mkdirs()
      writeText("db")
    }
    every { ObserveConsent.isGranted } returns false

    ObserveApplicationLifecycleListener().onCreate(application)

    verify { ObserveConsent.load(application) }
    assertTrue(application.getSharedPreferences("dev.expo.observe", Context.MODE_PRIVATE).all.isEmpty())
    assertFalse(legacyDatabase.exists())
  }

  @Test
  fun `a launch with consent keeps the settings`() {
    every { ObserveConsent.isGranted } returns true

    ObserveApplicationLifecycleListener().onCreate(application)

    assertEquals(42L, ObservePreferences.getLastDispatchedMetricId(application))
  }
}
