# 📱 SinChat Client Documentation

## Table of Contents

- [Overview](#overview)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Features](#features)
- [Views & UI](#views--ui)
- [Network Layer](#network-layer)
- [WebSocket Integration](#websocket-integration)
- [Configuration](#configuration)
- [Building & Running](#building--running)
- [Design System](#design-system)

---

## Overview

The SinChat Client is a **JavaFX desktop application** that provides a modern, dark-themed chat interface. It communicates with the SinChat Server via both **HTTP REST API** (for CRUD operations) and **WebSocket** (for real-time messaging and typing indicators).

The client is built as a **Maven project** and can be launched locally via the `javafx-maven-plugin`.

---

## Technology Stack

| Layer | Technology | Version | Purpose |
|---|---|---|---|
| **Language** | Java | 21 (LTS) | Core runtime |
| **GUI Framework** | [JavaFX](https://openjfx.io/) | 21 | Desktop UI components |
| **JavaFX Controls** | javafx-controls | 21 | Buttons, text fields, layouts, scroll panes |
| **JavaFX FXML** | javafx-fxml | 21 | FXML layout support (available, UI is code-built) |
| **HTTP Client** | `java.net.http.HttpClient` | JDK built-in | REST API calls (Java 11+ HTTP client) |
| **WebSocket** | [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket) | 1.5.7 | Real-time messaging connection |
| **JSON** | [Gson](https://github.com/google/gson) | 2.10.1 | JSON parsing & serialisation |
| **Build Tool** | [Apache Maven](https://maven.apache.org/) | 3.x | Dependency management & build |
| **Maven Plugin** | javafx-maven-plugin | 0.0.8 | JavaFX application launcher |

---

## Architecture

The client follows a simplified **MVC-like** pattern where views handle both UI layout and controller logic:

```
┌─────────────────────────────────────────────────────┐
│                   Main.java                          │
│              (JavaFX Application)                    │
│         Launches LoginView as initial scene          │
├─────────────────────────────────────────────────────┤
│                                                      │
│   ┌──────────────┐         ┌──────────────────┐     │
│   │  LoginView   │ ──────► │    ChatView      │     │
│   │              │ success │                    │     │
│   │  • Login     │         │  • Conversations  │     │
│   │  • Register  │         │  • Messages       │     │
│   │  • Forgot PW │         │  • Typing         │     │
│   └──────┬───────┘         └────────┬──────────┘     │
│          │                          │                 │
├──────────┴──────────────────────────┴─────────────────┤
│                                                       │
│   ┌──────────────────┐    ┌────────────────────────┐ │
│   │  ChatApiClient   │    │  ChatWebSocketClient   │ │
│   │  (HTTP REST)     │    │  (WebSocket)           │ │
│   │                  │    │                        │ │
│   │  • login()       │    │  • connectAsync()      │ │
│   │  • register()    │    │  • sendMessage()       │ │
│   │  • resetPwd()    │    │  • sendTyping()        │ │
│   │  • getMessages() │    │  • disconnect()        │ │
│   │  • getConvos()   │    │  • onNewMessage()      │ │
│   └────────┬─────────┘    └───────────┬────────────┘ │
│            │                          │               │
├────────────┴──────────────────────────┴───────────────┤
│              Server (HTTP + WebSocket)                 │
└───────────────────────────────────────────────────────┘
```

### Key Design Decisions

1. **Programmatic UI** — All UI is built in Java code (no FXML files), giving full control over layout and styling
2. **Async API calls** — HTTP requests run on background threads via `CompletableFuture` to keep the UI responsive
3. **Platform.runLater()** — All UI updates from WebSocket callbacks are marshalled to the JavaFX Application Thread
4. **Dual communication** — REST API for request/response operations; WebSocket for push-based real-time events

---

## Project Structure

```
Code/Client/
├── pom.xml                                 # Maven configuration & dependencies
└── src/main/java/
    ├── Main.java                           # JavaFX Application entry point
    ├── LoginView.java                      # Login / Register / Forgot Password UI
    ├── ChatView.java                       # Main chat interface (conversations + messages)
    ├── ChatApiClient.java                  # HTTP REST API client
    └── ChatWebSocketClient.java            # WebSocket client wrapper
```

### File Descriptions

| File | Lines | Description |
|---|---|---|
| `Main.java` | ~25 | JavaFX `Application` subclass. Sets up the stage with `LoginView` as the initial scene |
| `LoginView.java` | ~520 | Multi-view login screen with login, registration, forgot password, and password reset flows |
| `ChatView.java` | ~650 | Three-panel chat interface with contact list, message area, and profile sidebar |
| `ChatApiClient.java` | ~200 | HTTP client using `java.net.http.HttpClient` with lightweight JSON parsing via regex |
| `ChatWebSocketClient.java` | ~170 | WebSocket client wrapper that auto-joins on connect and dispatches events to JavaFX thread |

---

## Features

### ✅ Implemented

| Feature | Description |
|---|---|
| **User Login** | Authenticate with username/password via REST API |
| **User Registration** | Create new account with username, password, email |
| **Forgot Password** | Request a 6-digit reset code and set a new password |
| **Conversation List** | View all conversations the user belongs to with last message preview |
| **Real-time Messaging** | Send and receive messages instantly via WebSocket |
| **Message History** | Load past messages for any conversation via REST API |
| **Typing Indicators** | See when another user is typing (with auto-hide after 3 seconds) |
| **Typing Throttle** | Typing notifications are throttled to max 1 per second |
| **Start New Chat** | Create or open a conversation with any user by their ID |
| **Dark Theme UI** | Fully styled dark theme with accent colours |
| **Password Visibility Toggle** | Eye button to show/hide password fields |
| **Async Operations** | All network calls are non-blocking with loading states |

### 🔲 Planned (Not Yet Implemented)

| Feature | Status |
|---|---|
| Change password from profile | Planned |
| Change username | Planned |
| Change avatar | Planned (API ready on server) |
| Send image/video/voice messages | Planned |
| Group chat | Planned (DB schema ready) |
| Voice call | Planned (DB schema ready) |
| Video call | Planned |
| Screen sharing | Planned |
| File sharing | Planned |

---

## Views & UI

### LoginView

A single `BorderPane` that dynamically switches between **four sub-views** using a shared form layout:

#### 1. Login View
- Username/email text field
- Password field with visibility toggle (eye button)
- "Đăng nhập" (Login) button
- Links to "Quên mật khẩu?" (Forgot Password) and "Chưa có tài khoản?" (Register)

#### 2. Register View
- Username, email, password, confirm password fields
- "Đăng ký" (Register) button
- Link back to login

#### 3. Forgot Password — Step 1 (Enter Username)
- Username field
- "Gửi mã xác nhận" (Send Reset Code) button
- Receives a 6-digit code from the server

#### 4. Forgot Password — Step 2 (Enter Code + New Password)
- Reset code field (pre-filled from Step 1 in dev mode)
- New password + confirm password fields
- "Đặt lại mật khẩu" (Reset Password) button

### ChatView

A three-panel layout (`BorderPane` with `left`, `center`, `right`):

```
┌──────────────┬──────────────────────────┬──────────────┐
│              │                          │              │
│   Left       │       Center             │    Right     │
│   Panel      │       Panel              │    Panel     │
│              │                          │              │
│  ┌────────┐  │  ┌────────────────────┐  │  ┌────────┐ │
│  │ Search │  │  │   Chat Header      │  │  │ Avatar │ │
│  └────────┘  │  │   (Contact Name)   │  │  │ Circle │ │
│              │  ├────────────────────┤  │  ├────────┤ │
│  ┌────────┐  │  │                    │  │  │ User   │ │
│  │Contact1│  │  │   Messages Area    │  │  │ Name   │ │
│  ├────────┤  │  │   (ScrollPane)     │  │  │        │ │
│  │Contact2│  │  │                    │  │  │ Status │ │
│  ├────────┤  │  │   ┌──────────┐    │  │  │ Info   │ │
│  │Contact3│  │  │   │ Bubble   │    │  │  │        │ │
│  ├────────┤  │  │   └──────────┘    │  │  └────────┘ │
│  │  ...   │  │  │        ┌────────┐ │  │              │
│  └────────┘  │  │        │ Bubble │ │  │              │
│              │  │        └────────┘ │  │              │
│  ┌────────┐  │  ├────────────────────┤  │              │
│  │+ New   │  │  │ Typing indicator   │  │              │
│  │  Chat  │  │  ├────────────────────┤  │              │
│  └────────┘  │  │ [Message Input] [➤]│  │              │
│              │  └────────────────────┘  │              │
└──────────────┴──────────────────────────┴──────────────┘
      250px          Flexible width            220px
```

#### Left Panel (250px)
- **Search bar** — for searching contacts (UI ready, filter logic can be extended)
- **Contact list** — scrollable list of conversations, each showing:
  - Circle avatar (colour-coded by initials)
  - Username
  - Last message preview (truncated)
  - Timestamp
- **"+ Cuộc trò chuyện mới"** button — opens a dialog to start a new conversation by user ID

#### Center Panel (Flexible)
- **Header** — displays the current chat partner's name
- **Messages area** — scrollable `VBox` with message bubbles:
  - **Sent messages** (right-aligned, accent colour `#7c5cfc`)
  - **Received messages** (left-aligned, dark background `#1a1a2e`)
  - Timestamps displayed in `HH:mm` format
- **Typing indicator** — shows "User đang gõ..." with auto-hide after 3 seconds
- **Input bar** — text field + send button (or Enter key)

#### Right Panel (220px)
- **User profile sidebar** — displays the current chat partner's avatar and profile info

---

## Network Layer

### ChatApiClient

A lightweight HTTP client built on `java.net.http.HttpClient` (Java 11+).

#### Key Design

- **No external JSON library for parsing responses** — uses regex-based field extraction for the small, predictable server responses
- **Configurable base URL** via:
  - System property: `-Dchatapp.api.baseUrl=https://...`
  - Environment variable: `CHATAPP_API_BASE_URL=https://...`
  - Default: `http://localhost:3000`
- **Timeouts:** 5s connect timeout, 10s request timeout
- **JSON escaping** built-in for safe request body construction

#### API Methods

```java
// Authentication
ApiResponse register(String username, String password, String email)
ApiResponse login(String username, String password)

// Password Reset
ApiResponse requestPasswordResetCode(String username)
ApiResponse resetPassword(String code, String password)

// Messaging
ApiResponse getConversations(long userId)
ApiResponse getMessages(long conversationId)
```

#### ApiResponse Record

```java
public record ApiResponse(
    int statusCode,       // HTTP status code
    String status,        // "success" or "error"
    String message,       // Human-readable message
    String code,          // Reset code (if applicable)
    Long userId,          // User ID (from login)
    String rawBody        // Full response body
) {
    public boolean isSuccess() {
        return statusCode >= 200 && statusCode < 300
            && !"error".equalsIgnoreCase(status);
    }
}
```

---

## WebSocket Integration

### ChatWebSocketClient

A wrapper around `org.java_websocket.client.WebSocketClient` that integrates with the JavaFX thread model.

#### Connection Lifecycle

1. **`connectAsync()`** — Initiates WebSocket connection
2. **`onOpen`** — Automatically sends a `join` action with the user's ID
3. **Messages** — Parsed as JSON and dispatched based on `action` field
4. **`disconnect()`** — Gracefully closes the connection

#### Event Callbacks

All callbacks are invoked on the **JavaFX Application Thread** via `Platform.runLater()`:

```java
ws.setOnConnected(() -> { /* WebSocket connected */ });
ws.setOnNewMessage(json -> { /* new_message received */ });
ws.setOnUserTyping(json -> { /* user_typing received */ });
ws.setOnDisconnected(reason -> { /* connection closed */ });
ws.setOnError(errorMsg -> { /* error occurred */ });
```

#### Sending Messages

```java
// Send a chat message
ws.sendMessage(conversationId, "Hello!");

// Send typing indicator
ws.sendTyping(conversationId);
```

#### Typing Indicator Logic

The client implements **smart typing throttle** to avoid flooding the server:

- **Throttle:** Maximum 1 typing event per second (`TYPING_THROTTLE_MS = 1000`)
- **Auto-hide:** The typing label disappears after 3 seconds of no typing events from the other user
- **Scheduler:** Uses a `ScheduledExecutorService` (daemon thread) for delayed hide tasks

---

## Configuration

### Server Connection

The client connects to the server at `http://localhost:3000` by default. This can be changed without modifying code:

#### Option 1 — System Property
```bash
java -Dchatapp.api.baseUrl=https://network-programming-project.onrender.com -jar client.jar
```

#### Option 2 — Environment Variable
```bash
export CHATAPP_API_BASE_URL=https://network-programming-project.onrender.com
java -jar client.jar
```

### WebSocket Connection

The WebSocket URL is set in `ChatView.java` and defaults to `ws://localhost:8887`.

---

## Building & Running

### Prerequisites

- **Java 21** (or later) with JavaFX support
- **Maven 3.x**
- The **SinChat Server** running (locally or remotely)

### Build

```bash
cd Code/Client
mvn clean compile
```

### Run (via Maven)

```bash
cd Code/Client
mvn javafx:run
```

This uses the `javafx-maven-plugin` configured in `pom.xml` with `Main` as the main class.

### Run (Manual)

```bash
cd Code/Client
mvn clean package
java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml \
     -Dchatapp.api.baseUrl=http://localhost:3000 \
     -jar target/sinchat-client-1.0-SNAPSHOT.jar
```

### Connect to Production Server

```bash
mvn javafx:run -Dchatapp.api.baseUrl=https://network-programming-project.onrender.com
```

---

## Design System

The client uses a consistent **dark theme** with the following design tokens:

### Colour Palette

| Token | Hex | Usage |
|---|---|---|
| `BG_BLACK` | `#000000` | Root background |
| `PANEL_DARK` | `#111111` | Panel backgrounds |
| `BORDER_COLOR` | `#333333` | Borders and dividers |
| `TEXT_WHITE` | `#ffffff` | Primary text |
| `TEXT_MUTED` | `#888888` | Secondary/muted text |
| `TEXT_DIM` | `#555555` | Dim text (timestamps, placeholders) |
| `INPUT_BORDER` | `#444444` | Input field borders |
| `ACCENT` | `#7c5cfc` | Primary accent (buttons, sent messages, links) |
| Error | `#ff7777` | Error messages |
| Success | `#77ff77` | Success messages |

### UI Patterns

- **Rounded corners** — 20px radius on message bubbles, 8-10px on input fields and buttons
- **Smooth transitions** — Hover effects on buttons (opacity changes)
- **Avatar circles** — Colour-coded by first character of username with white text initials
- **Responsive panels** — Left and right panels have fixed widths; center panel is flexible
- **Inline CSS** — All styling is applied via JavaFX inline styles (`.setStyle()`)

### Typography

- **Primary font:** System default (JavaFX default font)
- **Font sizes:**
  - Titles: 20-24px, bold
  - Subtitles: 13px
  - Body text: 14px
  - Timestamps: 10px
  - Message bubbles: 14px
