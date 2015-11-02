package com.github.elufimov.reusablerwd

import java.io._
import java.lang.reflect.Field
import java.net.URL

import better.files.File
import org.openqa.selenium.Capabilities
import org.openqa.selenium.remote._

import collection.JavaConversions._

/**
 * Created by elufimov on 01/10/15.
 */
class ReusableRWD(
  remoteAddress: URL,
  desiredCapabilities: Capabilities
) extends RemoteWebDriver(
  new HttpCommandExecutor(remoteAddress),
  desiredCapabilities,
  null
) {
  override protected def startSession(desiredCapabilities: Capabilities, requiredCapabilities: Capabilities) {}
}

object ReusableRWD {
  private val sessionDir = "./session"
  private val returnedCapabilitiesDir = s"$sessionDir/returnedCapabilities"

  def initSession(url: URL, capabilities: DesiredCapabilities): RemoteWebDriver = {
    if (File(sessionDir).exists)
      File(sessionDir).delete(true)
    if (!File(sessionDir).exists) {
      File(sessionDir).createIfNotExists(asDirectory = true)
      File(returnedCapabilitiesDir).createIfNotExists(asDirectory = true)
    }

    val driver = new RemoteWebDriver(url, capabilities)

    File(s"$sessionDir/id") < driver.getSessionId.toString
    File(s"$sessionDir/url") < url.toString

    File(s"$sessionDir/desiredCapabilities").createIfNotExists(asDirectory = false)
    val desiredCapabilitiesOS = new ObjectOutputStream(new FileOutputStream(s"$sessionDir/desiredCapabilities"))
    desiredCapabilitiesOS.writeObject(capabilities)
    desiredCapabilitiesOS.close()

    val returnedCapabilities = driver.getClass.getDeclaredField("capabilities")
    returnedCapabilities.setAccessible(true)
    returnedCapabilities.get(driver).asInstanceOf[DesiredCapabilities].asMap().foreach { dc ⇒
      File(s"$returnedCapabilitiesDir/${dc._1}").createIfNotExists(asDirectory = false)
      val os = new ObjectOutputStream(new FileOutputStream(s"$returnedCapabilitiesDir/${dc._1}"))
      try {
        os.writeObject(dc._2)
        os.close()
      } catch {
        case e: NotSerializableException ⇒
          println(s"Can't serialize ${dc._1}")
          os.close()
          File(s"$returnedCapabilitiesDir/${dc._1}").delete()
      }
    }
    driver
  }

  def loadSession(): RemoteWebDriver = {
    val id = File(s"$sessionDir/id").contentAsString
    val url = new URL(File(s"$sessionDir/url").contentAsString)

    val desiredCapabilitiesIS = new ObjectInputStream(new FileInputStream(s"$sessionDir/desiredCapabilities"))
    val desiredCapabilities = desiredCapabilitiesIS.readObject().asInstanceOf[DesiredCapabilities]
    desiredCapabilitiesIS.close()

    val driver = new ReusableRWD(url, desiredCapabilities)

    val sessionId: Field = driver.getClass.getSuperclass.getDeclaredField("sessionId")
    sessionId.setAccessible(true)
    sessionId.set(driver, new SessionId(id))

    val returnedCapabilities = new DesiredCapabilities()

    File(returnedCapabilitiesDir).children.foreach { file ⇒
      returnedCapabilities.setCapability(file.name, new ObjectInputStream(new FileInputStream(s"$returnedCapabilitiesDir/${file.name}")).readObject())
    }
    val returnedCapabilitiesField = driver.getClass.getSuperclass.getDeclaredField("capabilities")
    returnedCapabilitiesField.setAccessible(true)
    returnedCapabilitiesField.set(driver, returnedCapabilities)

    driver
  }
}