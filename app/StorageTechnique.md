# Kiến Trúc Toàn Diện: Ứng Dụng Nhắn Tin Bảo Mật Cao (Production-Ready)

Tài liệu này tổng hợp toàn bộ vòng đời dữ liệu, kiến trúc hạ tầng Server và các tiêu chuẩn bảo mật khắt khe (OWASP MASVS-STORAGE & MASVS-RESILIENCE) dành cho một ứng dụng nhắn tin thực tế, tối ưu hóa cho nền tảng **Android (Kotlin / Jetpack / NDK)** và **Backend (Go / PostgreSQL / Redis)**.

---

## Phần I: Vòng Đời Lưu Trữ & Tải Dữ Liệu Hiệu Năng Cao

### 1. Giai Đoạn Đóng Gói & Truyền Tải (Serialization & Transport)
* **Binary Serialization:** Sử dụng **Protobuf (Protocol Buffers)** hoặc **FlatBuffers** thay vì JSON để nén cấu trúc dữ liệu thành các chuỗi byte siêu nhỏ, tăng tốc độ parse gấp 5–10 lần.
* **Delta Encoding & Compression:** Chỉ gửi phần dữ liệu thay đổi (ví dụ: trạng thái `đã xem` kèm `message_id`). Nén văn bản bằng thuật toán **Zstandard (zstd)** hoặc **LZ4** với từ điển nén huấn luyện sẵn.
* **Connection Multiplexing:** Dùng **gRPC (Bidirectional Streaming trên HTTP/2)** hoặc **WebSocket (WSS) + Protobuf** trên nền **TLS 1.3** để truyền tin nhắn, media và đồng bộ trạng thái trên cùng một kết nối mà không bị nghẽn cổ chai.

### 2. Giai Đoạn Lưu Trữ Trên Máy Chủ (Backend Storage & Zero-Knowledge)
* **Kiến trúc Hybrid Database (Polyglot Persistence):**
    * **PostgreSQL 16+ (RDBMS):** Lưu trữ thông tin tài khoản, danh sách bạn bè/nhóm chat và đặc biệt là **E2EE Pre-Key Bundles**. Đảm bảo tính nguyên tử (**ACID**) tuyệt đối khi cấp phát và xóa ngay lập tức *One-Time Pre-Key* để không làm hỏng tính bảo mật *Forward Secrecy*. Áp dụng kỹ thuật **Table Partitioning** (chia bảng theo thời gian) và **BRIN Index** cho bảng lưu tin nhắn chờ.
    * **Redis (In-Memory):** Quản lý trạng thái Online/Offline, định tuyến phiên kết nối và hàng đợi tin nhắn tức thì với độ trễ tính bằng micro-giây.
    * **NoSQL Phân Tán (Tùy chọn khi mở rộng quy mô hàng triệu user):** Sử dụng **ScyllaDB** (viết bằng C++, kiến trúc *Shard-per-Core*, cấu trúc **LSM-Tree** tối ưu cho tốc độ ghi nối tiếp *Append-only*) thay vì Apache Cassandra (tránh hiện tượng khựng do JVM Garbage Collection).
    * **Object Storage (Cloudflare R2 / MinIO):** Lưu trữ file media (ảnh, video, ghi âm) đã được mã hóa từ thiết bị người dùng; phân phối qua CDN gần người dùng nhất.
* **Nguyên tắc Zero-Knowledge & Ephemeral Storage:**
    * Server chỉ đóng vai trò là **người đưa thư mù (Blind Relay)**, toàn bộ nội dung tin nhắn lưu trên server đều là bản mã (**Ciphertext**).
    * **Acknowledge & Delete:** Ngay khi thiết bị người nhận xác nhận (ACK) đã tải và lưu tin nhắn an toàn vào cơ sở dữ liệu cục bộ, Server sẽ **xóa vĩnh viễn** bản ghi tin nhắn đó khỏi database.
    * **Bảo vệ Metadata (Sealed Sender):** Mã hóa luôn cả danh tính người gửi bằng khóa công khai của người nhận để server chỉ biết địa chỉ định tuyến gói tin.

### 3. Giai Đoạn Đồng Bộ Hóa Dữ Liệu (Synchronization Engine)
* **Sequence ID / Logical Clock:** Đồng bộ chênh lệch (Sync Delta) dựa trên số thứ tự tăng dần (`seq_id`) của từng cuộc hội thoại.
* **Outbox Pattern:** Lưu tin nhắn gửi lúc mất mạng vào bảng tạm cục bộ kèm `client_generated_uuid` để chống gửi trùng lặp (Idempotency) khi có mạng trở lại.
* **Data-Only Push Notification (FCM):** Khi người nhận tắt màn hình hoặc bị hệ điều hành ngắt kết nối ngầm, Server gửi một gói tin FCM rỗng (chỉ chứa cờ báo hiệu `type: "NEW_MESSAGE"`, **không chứa nội dung tin nhắn** để tránh rò rỉ qua máy chủ Google). App nhận tín hiệu, tự đánh thức tiến trình ngầm (`WorkManager`), kéo gói tin mã hóa về, giải mã và hiển thị Local Notification.

### 4. Giai Đoạn Lưu Trữ Dưới Điện Thoại (Local Device Storage)
* **Full-Text Search (FTS5):** Tạo bảng chỉ mục ngược ngay trên SQLite để tìm kiếm từ khóa trong hàng trăm nghìn tin nhắn dưới 50ms.
* **Storage Denormalization:** Phi chuẩn hóa dữ liệu (lưu thẳng tên, avatar vào bảng hội thoại) để tránh lệnh `JOIN` tốn kém khi cuộn danh sách.
* **Progressive Media & BlurHash:** Nhúng chuỗi **BlurHash** (< 2KB) trực tiếp vào gói tin văn bản để hiển thị ảnh mờ ngay lập tức (0ms) trong khi ảnh gốc tải ngầm.
* **LRU Cache & Vacuuming:** Tự động dọn dẹp file media cũ ít truy cập và chạy `PRAGMA optimize` / `VACUUM` bằng `WorkManager` khi thiết bị đang cắm sạc và nhàn rỗi.

### 5. Giai Đoạn Tải Lên Bộ Nhớ & Hiển Thị (In-Memory & UI Loading)
* **Multi-tier Memory Cache:** L1 Cache trên RAM (`LruCache`) lưu 5–10 cuộc hội thoại gần nhất; L2 Cache đọc từ SQLite bằng **Cursor-based Pagination** (`WHERE message_id < ? LIMIT 30`).
* **Prefetching & View Recycling:** Sử dụng `RecyclerView` / `LazyColumn` (Jetpack Compose) kết hợp `DiffUtil` và `Prefetch` để tải đón đầu dữ liệu và tái sử dụng thành phần giao diện ở tốc độ 60Hz–120Hz.

---

## Phần II: Kiến Trúc Bảo Mật & Mật Mã Học Chuyên Sâu

### 1. Mã Hóa Đầu Cuối (E2EE) & Tính Năng Thực Tế
* **Giao thức Signal (`libsignal-android`):**
    * **X3DH (Extended Triple Diffie-Hellman):** Thiết lập khóa phiên bí mật chung ngay cả khi người nhận đang offline thông qua bộ khóa công khai trên Server.
    * **Double Ratchet Algorithm:** Tự động xoay vòng khóa sau mỗi tin nhắn gửi và nhận, đảm bảo đồng thời **Forward Secrecy** (lộ khóa hiện tại không giải mã được tin cũ) và **Post-Compromise Security** (tự phục hồi bảo mật cho các tin nhắn tương lai).
* **Mã hóa Media Đầu cuối:** Sinh ngẫu nhiên một khóa đối xứng `AES-256-GCM` trên Android để mã hóa file ảnh/video trước khi upload lên Cloudflare R2/MinIO, sau đó gửi URL file kèm khóa giải mã qua kênh tin nhắn E2EE.
* **Xác thực danh tính chống giả mạo (Safety Number / QR Code):** Băm (hash) khóa công khai định danh (`Identity Key`) của hai người dùng thành chuỗi 60 chữ số và mã QR để quét xác thực trực tiếp, chống tấn công Man-in-the-Middle (MITM) từ phía máy chủ.
* **Zero-Knowledge Cloud Backup:** Cho phép người dùng đặt Mã PIN khôi phục hoặc cấp chuỗi 24 từ khóa bí mật, dùng hàm dẫn xuất khóa **Argon2id** để mã hóa toàn bộ file database `SQLCipher` và `Identity Key` trước khi sao lưu lên đám mây.

### 2. Quản Lý Khóa Bằng Phần Cứng (Android Keystore & StrongBox)
Tuyệt đối không hardcode khóa mật mã trong mã nguồn Kotlin/Java hay file `gradle.properties`.
* **StrongBox Keymaster:** Ưu tiên lưu khóa trong chip bảo mật vật lý độc lập (HSM trên điện thoại) nếu phần cứng hỗ trợ thông qua `.setIsStrongBoxBacked(true)`.
* **Ràng buộc xác thực người dùng:** Cấu hình `KeyGenParameterSpec` với `.setUserAuthenticationRequired(true)` để buộc hệ điều hành chỉ mở khóa khi có xác thực sinh trắc học (`BiometricPrompt`) hoặc mã PIN thiết bị.
* **Kiến trúc Envelope Encryption (Mã hóa phong bì):**
    1. Sinh ngẫu nhiên **DEK (Data Encryption Key - 256-bit)** bằng `SecureRandom` để mã hóa database/file.
    2. Dùng **KEK (Key Encryption Key - AES-256-GCM)** nằm trong `AndroidKeyStore` để mã hóa (wrap) chính DEK đó.
    3. Lưu `Wrapped_DEK` kèm `IV` (Initialization Vector) vào ổ cứng. Khi mở app, đưa `Wrapped_DEK` vào Keystore để giải mã nạp DEK lên RAM.

### 3. Mã Hóa Toàn Bộ Cơ Sở Dữ Liệu & File (Data-at-Rest Encryption)
* **Cơ sở dữ liệu (Room + SQLCipher):**
    * Tích hợp thư viện `net.zetetic:sqlcipher-android` kết hợp với Jetpack **Room** thông qua `SupportFactory`.
    * Toàn bộ trang dữ liệu (page-level) của SQLite được mã hóa bằng **AES-256-CBC/GCM**, che giấu hoàn toàn nội dung lẫn metadata (số lượng tin nhắn, thời gian, cấu trúc bảng).
* **Lưu trữ Key-Value nhạy cảm (Token, Session):**
    * Sử dụng thư viện **Google Tink** (`AndroidKeysetManager`) kết hợp với **Jetpack DataStore** để mã hóa xác thực (AEAD - `AES256_GCM`).
* **Mã hóa luồng cho Media (Streaming Encryption):**
    * Sử dụng `StreamingAead` của **Google Tink** (`AES256_CTR_HMAC_SHA256_4KB`) để mã hóa ảnh, video, file ghi âm theo từng khối 4KB.
    * Giải mã trực tiếp từng chunk trên RAM và đẩy thẳng vào trình phát (`ExoPlayer` / `ImageDecoder`), **tuyệt đối không bao giờ ghi file đã giải mã (plaintext) xuống thư mục `cacheDir`**.

### 4. Bảo Vệ Dữ Liệu Trên Bộ Nhớ Tạm (In-Memory Protection)
Chống lại kỹ thuật dump RAM bằng công cụ như `Fridump` trên thiết bị đã Root.
* **Loại bỏ kiểu `String` cho dữ liệu nhạy cảm:** `String` trong JVM là bất biến (immutable) và nằm trong String Pool cho đến khi Garbage Collector thu gom. Hãy lưu DEK, mật khẩu, và nội dung tin nhắn sau giải mã dưới dạng `ByteArray` hoặc `CharArray`.
* **Xóa sạch vùng nhớ ngay lập tức (Zeroization):** Ngay sau khi hoàn tất thao tác mã hóa/giải mã hoặc đóng màn hình chat, gọi `Arrays.fill(sensitiveByteArray, 0.toByte())`.
* **Native Memory Locking (Android NDK - C/C++):** Với các khóa mật mã cốt lõi, xử lý trực tiếp tại tầng C/C++ thông qua JNI, sử dụng hàm hệ thống `mlock()` để cấm hệ điều hành đẩy vùng RAM này xuống file swap, và dùng `sodium_memzero()` (thư viện **Libsodium**) để xóa sạch bộ nhớ mà không bị trình biên dịch tối ưu hóa bỏ qua.
* **Xóa khóa khi xuống nền (Lifecycle-Aware Locking):** Đăng ký `DefaultLifecycleObserver` theo dõi sự kiện `onStop()` của tiến trình; tự động xóa DEK khỏi RAM và khóa database nếu người dùng thoát app quá một khoảng thời gian quy định.

### 5. Chống Rò Rỉ Qua Hệ Điều Hành, Đường Truyền & Kiểm Soát Môi Trường (Hardening & RASP)
* **Certificate Pinning:** Nhúng trực tiếp mã băm (`SHA-256`) chứng chỉ SSL của Server vào cấu hình mạng Android (`network_security_config.xml` / OkHttp / gRPC) để chống nghe lén qua Proxy giả mạo (Burp Suite, Charles Proxy).
* **Vô hiệu hóa mọi cơ chế sao lưu của Android (`AndroidManifest.xml`):**
    * Đặt `android:allowBackup="false"` và `android:fullBackupContent="false"`.
    * Cấu hình thuộc tính `android:dataExtractionRules` (Android 12+) để chặn hoàn toàn lệnh `adb backup` và đồng bộ Google Cloud trích xuất thư mục `databases/` và `files/`.
* **Chống rò rỉ giao diện (UI Leakage Prevention):**
    * Gắn cờ `window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)` trên các `Activity` nhạy cảm để chặn chụp màn hình, quay màn hình và ngăn hệ điều hành chụp ảnh thu nhỏ lưu vào màn hình đa nhiệm (Recent Apps).
    * Thiết lập `EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING` cho ô nhập tin nhắn để ngăn bàn phím bên thứ 3 học và lưu lại từ khóa người dùng gõ.
    * Gắn cờ `ClipDescription.EXTRA_IS_SENSITIVE` (Android 13+) khi sao chép dữ liệu nhạy cảm vào Clipboard và tự động xóa Clipboard sau 30 giây.
* **Tự bảo vệ khi chạy (RASP - Runtime Application Self-Protection):**
    * **Root & Tamper Detection:** Kiểm tra sự tồn tại của binary `su`, Magisk, kiểm tra chữ ký ứng dụng (`PackageInfo.SIGNING_CERTIFICATES`) lúc khởi động và xác thực tính toàn vẹn thiết bị qua **Google Play Integrity API**.
    * **Anti-Frida & Anti-Debugging:** Kiểm tra cờ `Debug.isDebuggerConnected()`, rà soát `/proc/self/maps` và các cổng TCP mặc định (27042) tại tầng Native (C/C++) để phát hiện tiến trình tiêm mã (Frida/Xposed). Nếu phát hiện môi trường không an toàn, lập tức hủy khóa DEK trên RAM và từ chối mở database.

---

## Phần III: Tech Stack Toàn Diện Khuyên Dùng Cho Dự Án Thực Tế

| Tầng Kiến Trúc | Thành phần | Công nghệ / Thư viện đề xuất | Mục đích |
| :--- | :--- | :--- | :--- |
| **Android Client** | **Ngôn ngữ & UI** | `Kotlin` + `Jetpack Compose` | Xây dựng giao diện phản hồi nhanh, quản lý vòng đời chặt chẽ. |
| **Android Client** | **Mật mã học E2EE** | `libsignal-android` + `Libsodium` | Triển khai X3DH, Double Ratchet và xóa RAM an toàn ở tầng Native. |
| **Android Client** | **Database & Mã hóa DB** | `Room` + `SQLCipher for Android` | Lưu trữ quan hệ tốc độ cao, mã hóa toàn bộ file DB chuẩn AES-256. |
| **Android Client** | **Mã hóa File & Stream** | `Google Tink` (`tink-android`) | Quản lý khóa Keystore chuẩn xác, mã hóa luồng file media không để lại file tạm. |
| **Android Client** | **Lưu trữ Token/Config** | `Jetpack DataStore` + `Google Tink` | Thay thế SharedPreferences, lưu trữ bất đồng bộ an toàn tuyệt đối. |
| **Android Client** | **Xác thực sinh trắc học** | `androidx.biometric:biometric` | Mở khóa `CryptoObject` gắn liền với phần cứng `AndroidKeyStore`. |
| **Android Client** | **Bảo vệ mã nguồn** | `R8` (Full Mode) + `NDK (C/C++)` | Làm rối mã nguồn (Obfuscation), thu gọn code và giấu logic RASP xuống tầng Native. |
| **Network** | **Giao thức & Đóng gói** | `gRPC` / `WebSocket` + `Protobuf` + `TLS 1.3` | Truyền tải nhị phân siêu nhẹ hai chiều, bật Certificate Pinning. |
| **Backend Server** | **Ngôn ngữ & Framework** | `Go (Golang)` | Chịu tải hàng chục nghìn kết nối đồng thời cực nhẹ RAM, biên dịch 1 binary duy nhất. |
| **Backend Server** | **CSDL Chính & Khóa** | `PostgreSQL 16+` | Đảm bảo ACID tuyệt đối cho kho khóa Pre-Key Bundles, tài khoản và tin nhắn chờ. |
| **Backend Server** | **Cache & Hàng đợi** | `Redis` | Quản lý trạng thái Online/Offline và hàng đợi đẩy tin nhắn tức thì. |
| **Backend Server** | **Lưu trữ Media & Push** | `Cloudflare R2` / `MinIO` + `FCM` | Lưu file media mã hóa chi phí thấp và đánh thức app nhận tin khi tắt màn hình. |

---

## Phần IV: Lộ Trình Triển Khai & Kiểm Thử Thực Tế (8 – 10 Tuần)

1. **Giai đoạn 1 (Tuần 1 – 2): Hạ tầng Backend & Giao thức lõi**
    * Dựng VPS (Docker, PostgreSQL, Redis, Nginx cấu hình SSL/TLS 1.3).
    * Viết Backend bằng **Go** hỗ trợ xác thực (JWT ngắn hạn + Refresh Token xoay vòng) và kênh giao tiếp thời gian thực **gRPC/WebSocket với Protobuf**.
2. **Giai đoạn 2 (Tuần 3 – 5): Tích hợp Mật mã Đầu cuối & Bảo mật Local**
    * Triển khai API quản lý **Pre-Key Bundles** trên PostgreSQL.
    * Tích hợp `libsignal-android` để thực hiện bắt tay **X3DH** và mã hóa **Double Ratchet**.
    * Lưu trữ tin nhắn đã giải mã vào **Room + SQLCipher** kết hợp **Android Keystore (Envelope Encryption)**.
3. **Giai đoạn 3 (Tuần 6 – 7): Tính năng Production (Media, Push & QR Verification)**
    * Tích hợp **Cloudflare R2 / MinIO** cho luồng gửi ảnh/file mã hóa đầu cuối.
    * Tích hợp **FCM Data-only Push** kết hợp `WorkManager` để nhận tin nhắn khi tắt màn hình.
    * Xây dựng màn hình quét **Mã QR (Safety Number)** chống tấn công MITM và tính năng **Zero-Knowledge Backup (Argon2id)**.
4. **Giai đoạn 4 (Tuần 8 – 10): Kiểm thử xâm nhập (Pentest) & Đóng gói**
    * **Kiểm thử đường truyền:** Dùng **Burp Suite** chặn bắt gói tin để kiểm chứng hiệu quả của *Certificate Pinning* và *E2EE*.
    * **Kiểm thử bộ nhớ & thiết bị:** Dùng **ADB** trích xuất file `.db` trên máy đã Root để kiểm chứng mã hóa *SQLCipher*; dùng **Frida / Fridump** để kiểm tra rò rỉ dữ liệu nhạy cảm trên RAM và cơ chế *RASP*.
    * Bật **R8 Obfuscation** và đóng gói bản phát hành cài đặt trên thiết bị vật lý.