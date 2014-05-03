# java-point-of-sale-project

Install sample projects to support programming exercises for a training course in Test-Driven Development and Object-Oriented Design in Java. These instructions are used by an instructor preparing a VM to be copied and distributed to participants in the class.

### Prerequisites

These instructions assume you are starting with a VM that has been provisioned using provision-java-dev-training-vm, available on Github. Alternatively, you may provision a target environment manually in any way you wish, provided the environment can run the course project(s).

## Steps

### Step 1 - Clone this repo

Load the configuration scripts for the course projects:

```shell
cd
git clone https://github.com/neopragma/java-point-of-sale-project
```

### Step 2 - Run the setup script

Run the setup script.

```shell
cd ~/provision-point-of-sale-vm
./setup
```

### Step 4 - Verify the "credit authorization server" will run

In a Terminal window: 

```shell
cd
authserver start
```

In a browser, navigate to ```http://localhost:4567```. If the server is running, it will serve a page with the text, ```Credit authorization service```. 

Leave the server running while you complete the configuration and verification of Eclipse.

#### Troubleshooting

The authserver is a Ruby application installed at ```$HOME/workspace/point-of-sale/credit-authorization```. The startup script runs this command to start the server: ```$HOME/workspace/point-of-sale/credit-authorization/app/ruby authserver.rb```.

Possible causes of problems:

* ```setup``` script failed to run ```bundle install``` in directory ```$HOME/workspace/point-of-sale/credit-authorization```.
* RVM was not installed correctly
* Ruby was not installed correctly
* ```setup``` script failed to add ```$HOME/bin``` to ```PATH```.
* Need to source ```.bashrc``` or start a new Terminal session to pick up the modified ```PATH``` value
* Port 4567 is in use by another process

### Step 5 - Install m2e Eclipse plugin

This manual step is necessary because of a problem in the way the slf4j api bundle is packaged. Headless install using Equinox p2 director fails because it cannot resolve the dependency on slf4j. 

Start Eclipse and go to ```Help => Install New Software...``` on the top menu bar. Add the repository ```http://download.eclipse.org/technology/m2e/releases```. The name should appear as ```Maven Integration for Eclipse```. Follow the prompts to install the m2e plugin. Restart Eclipse.

### Step 6 - Import the class project

In Eclipse, open the Java perspective. Right-click in the project panel (on the left) and choose ```Import...``` Then choose ```Import existing project into workspace```. Navigate to ```~/workspace/point-of-sale``` and import the project.

### Step 7 - Point Eclipse and m2e to the correct version of Java

In Eclipse, ```Window => Preferences => Java Build Path => Compiler```, set the options to Java 1.7.

In Eclipse, ```[double-check where]```, set the JRE for Maven to use to Java 1.7.

### Step 8 - Verify the build will run from the command line

All the supplied unit tests should pass:

```shell
cd "$HOME/workspace/point-of-sale"
mvn test
```

All the supplied integration tests should pass:

```shell
cd "$HOME/workspace/point-of-sale"
mvn failsafe:integration-test
```

### Step 9 - Verify the build will run from within Eclipse

In Eclipse, right-click on the point-of-sale ```pom``` and choose Run As => Maven install from the context menu. The build should run the same as it did when you ran it from the command line.







