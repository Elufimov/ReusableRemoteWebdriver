# Reusable Selenium Remote Webdriver

It's a simple library for solving a single problem - loss of context on reloading REAPL. 


## Install

`sbt`

```scala
libraryDependencies ++= Seq(
  "com.github.elufimov" %% "reusableremotewebdriver" % "0.5"
)
```

## Using

* Start selenium hub and selenium node
* Allocate remote webdriver session

```scala
import java.net.URL

import com.github.elufimov.reusablerwd.ReusableRWD
import org.openqa.selenium.remote.DesiredCapabilities

val cap = new DesiredCapabilities()
cap.setBrowserName("firefox")
// Will create `HOME/.reusableRemoteWebDriver/Current_Dir/default` and store serialized instance of Webdriver with browser name `firefox`
ReusableRWD.initSession(new URL("http://localhost:4444/wd/hub"), cap)

val capChrome = new DesiredCapabilities()
capChrome.setBrowserName("chrome")
// Will create `HOME/.reusableRemoteWebDriver/Current_Dir/ch` and store serialized instance of Webdriver with browser name `chrome`
ReusableRWD.initSession(new URL("http://localhost:4444/wd/hub"), capChrome, "ch")
```

It will create `HOME/.reusableRemoteWebDriver/Current_Dir/tag` folder with information on remote session. For default tag is "default".

* Restore session 

```scala
//Will load `default` session
val driver = ReusableRWD.loadSession()

//Will load `ch` session
val driver = ReusableRWD.loadSession("ch")
```

## License
The MIT License (MIT)

Copyright (c) 2015 Michael Elufimov

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
