DOST AI v0.4 — Android Assistant Starter
========================================

Is ZIP mein Android Studio/Gradle source code hai; yeh APK file nahi hai.

Implemented in source:
- Hindi (hi-IN) speech recognition using the Android recognizer intent.
- Hindi text-to-speech replies (device TTS support par depend karta hai).
- Android default-assistant role request + VoiceInteractionService/session starter.
- YouTube, WhatsApp, Maps, Google/web search shortcuts.
- Time, date, battery percentage, dialer, alarm list, contacts, music, Bluetooth/Wi-Fi and Settings shortcuts.
- UPI/banking/payment commands are explicitly refused; calls are opened in the dialer so user must confirm.
- GitHub Actions workflow to build and upload a debug APK artifact.

Important limitations (not falsely marked as complete):
- Custom always-listening “Hey Dost” wake word is NOT implemented. Android restricts background microphone use and this needs a separate on-device wake-word engine plus testing/power policy work.
- AI chat backend is NOT connected. There is no API key or server in this source. Never put private API keys in Android source.
- Voice biometric identity / unlocking the phone is NOT implemented and should not be used as a replacement for Android's secure lock screen.
- Galaxy Watch / earbuds companion is NOT implemented.
- Not yet built or device-tested in this environment. Android SDK is required for an APK build.

Build from phone (GitHub Actions):
1. Create a GitHub repository named DOST-AI (or another name you choose).
2. Upload/extract ALL files and folders from this ZIP into the repository root; do not upload only the ZIP.
3. Make sure .github/workflows/build-apk.yml is included.
4. Open the repository's Actions tab, choose “Build DOST AI debug APK”, and tap “Run workflow”.
5. Wait for a green successful run. Open that run and download the artifact named DOST-AI-debug-APK.
6. Extract the artifact ZIP to get app-debug.apk, download it on Android, and install it. Android may ask to allow installs from the browser/files app.
7. Open DOST, grant microphone permission, then choose “DOST ko Default Assistant banao”. Confirm the Android role prompt.
8. Test voice commands on the actual phone. If build fails, check the Actions logs before installing.

GitHub permissions / account:
- Repository access must be granted to the GitHub connection for in-chat repository operations.
- This build workflow runs on GitHub's hosted runner; it does not install an app on your phone automatically.

Security:
- No UPI/payment transaction automation.
- No API secrets in client code.
- Always review what DOST is about to open/do; a spoken command can be misheard.
