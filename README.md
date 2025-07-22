# SchoolRunner

SchoolRunner is an Android application that facilitates campus delivery services, allowing students to create and receive delivery orders within the campus.

## Features

- User authentication (login/register)
- Create delivery orders with different types:
  - Express Pickup
  - Food Delivery
  - Document Printing
  - Other Services
- Smart route suggestions between campus locations
- Order management and tracking
- Profile management
- Real-time order status updates
- Integrated contact system

## Prerequisites

- Android Studio (latest version recommended)
- JDK 8 or higher
- Android SDK API 21 or higher
- Gradle 8.11.1 or higher

## Building the Project

1. Clone the repository:
```sh
git clone https://github.com/daniel159357/SchoolRunner.git
```

2. Open the project in Android Studio:
   - Launch Android Studio
   - Select "Open an existing Android Studio project"
   - Navigate to the cloned directory and select it

3. Build the project:
   - Wait for Gradle sync to complete
   - Select `Build > Make Project` or press `Ctrl+F9` (Windows/Linux) or `Cmd+F9` (macOS)

4. Run the application:
   - Connect an Android device or start an emulator
   - Select `Run > Run 'app'` or press `Shift+F10` (Windows/Linux) or `Control+R` (macOS)

## Gradle Build

Alternatively, you can build the project from the command line:

```sh
# On Windows
.\gradlew.bat assembleDebug

# On Linux/macOS
./gradlew assembleDebug
```

The APK file will be generated at: `app/build/outputs/apk/debug/app-debug.apk`

## Project Structure

```
app/
├── src/
│   └── main/
│       ├── java/com/example/schoolrunner/
│       │   ├── common/          # Common utilities and base classes
│       │   ├── dao/             # Data Access Objects
│       │   ├── model/           # Data models and entities
│       │   └── ui/             # UI components (activities, fragments)
│       └── res/                # Android resources
├── build.gradle                # App level build configuration
└── proguard-rules.pro         # ProGuard rules
```

## Database Schema

The application uses SQLite for data persistence with two main tables:
- `tb_student`: Stores student information
- `tb_order`: Stores order information

## Signing the Application

To create a signed APK for release:

1. In Android Studio, select `Build > Generate Signed Bundle/APK`
2. Choose APK
3. Use the existing keystore file: `SchoolRunner.jks`
4. Follow the wizard to complete the signing process

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Contact

Daniel TAM - [yunlam159@gmail.com](mailto:yunlam159@gmail.com)

Project Link: [https://github.com/daniel159357/SchoolRunner](https://github.com/daniel159357/SchoolRunner)
