package com.github.elufimov.reusablerwd

import java.io.{ ObjectOutputStream, FileOutputStream, NotSerializableException, ObjectInputStream, FileInputStream }
import java.lang.reflect.Field
import java.net.URL

import better.files._
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
  private val rwdHomeDir = s"${System.getProperty("user.home")}" / ".reusableRemoteWebDriver"
  private val sessionDir = rwdHomeDir / File("").name
  private val returnedCapabilitiesDir = sessionDir / "returnedCapabilities"

  def initSession(url: URL, capabilities: DesiredCapabilities): RemoteWebDriver = {
    rwdHomeDir.createIfNotExists(asDirectory = true)
    if (sessionDir.exists)
      sessionDir.delete(true)
    if (!sessionDir.exists) {
      sessionDir.createIfNotExists(asDirectory = true)
      returnedCapabilitiesDir.createIfNotExists(asDirectory = true)
    }

    val driver = new RemoteWebDriver(url, capabilities)

    sessionDir / "id" < driver.getSessionId.toString
    sessionDir / "url" < url.toString

    (sessionDir / "desiredCapabilities").createIfNotExists(asDirectory = false)
    val desiredCapabilitiesOS = new ObjectOutputStream(new FileOutputStream((sessionDir / "desiredCapabilities").toJava))
    desiredCapabilitiesOS.writeObject(capabilities)
    desiredCapabilitiesOS.close()

    val returnedCapabilities = driver.getClass.getDeclaredField("capabilities")
    returnedCapabilities.setAccessible(true)
    returnedCapabilities.get(driver).asInstanceOf[DesiredCapabilities].asMap().foreach { dc ⇒
      (returnedCapabilitiesDir / dc._1).createIfNotExists(asDirectory = false)
      val os = new ObjectOutputStream(new FileOutputStream((returnedCapabilitiesDir / dc._1).toJava))
      try {
        os.writeObject(dc._2)
        os.close()
      } catch {
        case e: NotSerializableException ⇒
          println(s"Can't serialize ${dc._1}")
          os.close()
          (returnedCapabilitiesDir / dc._1).delete()
      }
    }
    driver
  }

  def loadSession(): RemoteWebDriver = {
    val id = (sessionDir / "id").contentAsString
    val url = new URL((sessionDir / "url").contentAsString)

    val desiredCapabilitiesIS = new ObjectInputStream(new FileInputStream((sessionDir / "desiredCapabilities").toJava))
    val desiredCapabilities = desiredCapabilitiesIS.readObject().asInstanceOf[DesiredCapabilities]
    desiredCapabilitiesIS.close()

    val driver = new ReusableRWD(url, desiredCapabilities)

    val sessionId: Field = driver.getClass.getSuperclass.getDeclaredField("sessionId")
    sessionId.setAccessible(true)
    sessionId.set(driver, new SessionId(id))

    val returnedCapabilities = new DesiredCapabilities()

    returnedCapabilitiesDir.children.foreach { file ⇒
      returnedCapabilities.setCapability(
        file.name,
        new ObjectInputStream(
          new FileInputStream((returnedCapabilitiesDir / file.name).toJava)
        ).readObject()
      )
    }
    val returnedCapabilitiesField = driver.getClass.getSuperclass.getDeclaredField("capabilities")
    returnedCapabilitiesField.setAccessible(true)
    returnedCapabilitiesField.set(driver, returnedCapabilities)

    driver
  }
}