# 1072 Scouting App 2018 - Android
The 1072 2018 Scouting App for Android, made for the FRC PowerUp challenge.
Made by Akshay Manglik with contributions from Carl Gross.

## How to install

Download the release APK from the release folder (https://github.com/HarkerRobo/scouting-android/blob/master/Scouting2018/app/release/app-release.apk) on your phone. From there, you should click on the downloaded APK, and your phone should take you through the installation procedures.

## Usage

### Signing In
Once you launch the app, it should take you to a landing page with the 1072 logo. From there, you can click the Login with Google button to sign in with your student account. After that, enter a valid round number (that correlates with the server) that you want to scout.

### Auton
After you enter a valid round number, the app should take you to the initial auton screen. At the top of the screen displayed is the number of the team that you are scouting and their color (red or blue). Once the auton period of the game that you are scouting starts, press the buttons that correlate to their actions, such as Home Switch (if they put a block on their home switch). Make sure to indicate where they started using the slider on the bottom and toggle whether or not they crossed the baseline!

### Teleop
Once Teleop starts, press the teleop button. This should take you to a similar screen. Press the buttons that associate with their actions as they do them. Once you are finished, press done. If you make a mistake, simply press the undo button.

### Final Notes
Once you get to the final screen, you can indicate their lifting capabilities and whether or not they ended the round on the platform. There is also a section for round comments. Please do not just skip over this - in depth comments will always help us know what happened in the round that can't be described quantitatively. Once you are done, press the finish button, which will take you to the landing screen to start the process all over again.

### Dealing with Bugs
If the app crashes after you enter a new round number, that may be due to login token expiration. To remedy this, simply press the disconnect button and login again. If after a couple of times this does not solve the problem, or you are experiencing a different issue, open up a new issue in the issues tab of this repo (https://github.com/HarkerRobo/scouting-android/issues).

## More Info
For more info about the backend of this app, see the docs for David's API (https://github.com/HarkerRobo/robotics-website/blob/main/doc/scouting.md) or Aydin's app if you know Swift (https://github.com/HarkerRobo/scouting1072).
