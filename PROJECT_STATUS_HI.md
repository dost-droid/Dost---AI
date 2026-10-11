# DOST AI v0.4 — सत्यापित स्थिति

## Source changes in this package
- Version metadata updated to 0.4 / versionCode 4.
- Added date and battery percentage commands.
- Added dialer shortcut using ACTION_DIAL (does not place calls directly).
- Added contacts, music, Bluetooth and Wi-Fi shortcuts.
- Added package visibility declarations for known app shortcuts.
- Expanded Hindi help and safety replies for payment-related commands.
- Preserved Android default-assistant role request and GitHub Actions APK build workflow.

## Not verified / still requires real environment
- APK has not been compiled here: Android SDK/Gradle build environment is not available in this workspace.
- GitHub Actions workflow has not been run against the user's repository.
- No real Nothing Phone (2) install or runtime test has happened.
- Custom always-listening wake word, cloud AI backend, secure voice identity, watch companion are not implemented.

## Acceptance checks to run on GitHub/phone
1. GitHub Actions job succeeds and uploads `DOST-AI-debug-APK`.
2. APK installs on Android 16 / Nothing Phone (2).
3. Microphone permission and Hindi recognition work.
4. Default assistant role prompt appears and can be selected.
5. Quick actions launch correctly; battery/date/time answers are correct.
6. Verify assistant does not launch or control UPI/banking apps.
