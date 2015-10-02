package com.github.elufimov.reusablerwd

import java.io.{ FileInputStream, ObjectInputStream, FileOutputStream, ObjectOutputStream }
import java.lang.reflect.Field
import java.net.URL

import better.files.File
import org.openqa.selenium.Capabilities
import org.openqa.selenium.remote._

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
  def initSession(url: URL, capabilities: DesiredCapabilities): RemoteWebDriver = {
    File("./session").delete(true)
    if (!File("./session").exists)
      File("./session").createDirectory()

    val driver = new RemoteWebDriver(url, capabilities)
    val returnedCapabilities = driver.getClass.getDeclaredField("capabilities")
    returnedCapabilities.setAccessible(true)

    File("./session/id") < driver.getSessionId.toString
    File("./session/url") < url.toString

    val os = new ObjectOutputStream(new FileOutputStream("./session/capabilities"))
    os.writeObject(returnedCapabilities.get(driver).asInstanceOf[Capabilities])
    os.close()

    driver
  }

  def loadSession(): RemoteWebDriver = {
    val id = File("./session/id").contentAsString
    val url = new URL(File("./session/url").contentAsString)
    val is = new ObjectInputStream(new FileInputStream("./session/capabilities"))
    val capabilities = is.readObject().asInstanceOf[Capabilities]
    is.close()
    val driver = new ReusableRWD(url, capabilities)

    val sessionId: Field = driver.getClass.getSuperclass.getDeclaredField("sessionId")
    sessionId.setAccessible(true)
    sessionId.set(driver, new SessionId(id))

    val returnedCapabilities = driver.getClass.getSuperclass.getDeclaredField("capabilities")
    returnedCapabilities.setAccessible(true)
    returnedCapabilities.set(driver, capabilities)

    driver
  }
}