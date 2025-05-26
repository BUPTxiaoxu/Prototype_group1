# Prototype_group1
This project is a fully functional financial management software, designed to help users manage their personal finances effectively. Below are the instructions for setting up, configuring and running the software.

## System Requirements
- Operating Systems : Windows 10/11, macOS, Linux
- Java Runtime Environment : Java 11 or higher version
- Memory : At least 4GB of RAM
- Disk space : At least 100MB of available space

## Installation of the Java Runtime Environment
1. Access [Oracle's official website](https://www.oracle.com/java/technologies/javase-downloads.html) to download the latest version of the Java runtime environment.
2. Run the installation program and follow the prompts to complete the installation.
3. Verify whether the installation is successful. Enter 'java -version' in the command line and make sure the installed version is displayed.

## Clone the Project Repository
1. Open a command-line tool (such as CMD or PowerShell).
2. Clone the project repository: `git clone https://github.com/yourusername/your-repo-name.git`
3. Switch to the project directory: `cd your-repo-name`

## Project Configuration
1. Open the 'JAVA_project.iml' file and configure the version of the project SDK to Java 11 or a higher version.
2. Ensure that the 'lib' folder of the project contains all necessary third-party libraries (such as JUnit 5).
3. If you need to use the database function, you need to configure the database connection information, usually in the 'application.properties' or' database-config.properties' file.

## Building and Running the Project
1. Run the 'mvn clean install' command in the command line to build the project and ensure that all dependencies have been downloaded correctly and built successfully.
2. After the build is completed, run the Main class to start the project: `java -cp target/classes your.project.main.Class`, usually the main class is' main.java'.

## Data Import (CSV)
1. Prepare the CSV file to be imported and ensure that the file format meets the software requirements.
2. Find and click the "Import CSV" button in the project interface.
3. Select the CSV file and click "Open". The software will read the file content and import the data.