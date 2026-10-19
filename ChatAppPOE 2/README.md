# ChatApp POE (Part 1 and Part 2)

Console chat app: registration and login (Part 1), then QuickChat messaging (Part 2).

## Run in VS Code
1. Install JDK 17+, Maven, and the "Extension Pack for Java" in VS Code.
2. File > Open Folder > select the `ChatAppPOE` folder (the one containing `pom.xml`).
3. Run the app: open `Main.java` and click "Run" above `main`.
4. Run the tests: Testing panel (beaker icon), or `mvn test` in a terminal.

## Structure
- `src/main/java/com/chatapp/Login.java`    - registration and login logic (Part 1)
- `src/main/java/com/chatapp/Message.java`  - message logic, hash, JSON storage (Part 2)
- `src/main/java/com/chatapp/Main.java`     - console app
- `src/test/java/com/chatapp/LoginTest.java`   - Part 1 tests
- `src/test/java/com/chatapp/MessageTest.java` - Part 2 tests
- `.github/workflows/maven.yml` - GitHub Actions: runs the tests on every push

## Git (Part 2 feature branch)
    git checkout -b QuickChat
    git add .
    git commit -m "Add Message class"
    git push -u origin QuickChat
