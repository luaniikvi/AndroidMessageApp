# Kiến Trúc Lưu Trữ, Tải Dữ Liệu & Bảo Mật Chuyên Sâu Trên Android

Tài liệu này tổng hợp toàn bộ vòng đời dữ liệu của một ứng dụng nhắn tin hiệu năng cao kết hợp với các tiêu chuẩn bảo mật khắt khe (OWASP MASVS-STORAGE & MASVS-RESILIENCE), được tối ưu hóa riêng cho nền tảng **Android (Kotlin / Jetpack / NDK)**.

---

## Phần I: Vòng Đời Lưu Trữ & Tải Dữ Liệu Hiệu Năng Cao

### 1. Giai Đoạn Đóng Gói & Truyền Tải (Serialization & Transport)
* **Binary Serialization:** Sử dụng **Protobuf (Protocol Buffers)** hoặc **FlatBuffers** thay vì JSON để nén cấu trúc dữ liệu thành các chuỗi byte siêu nhỏ, tăng tốc độ parse gấp 5–10 lần.
* **Delta Encoding & Compression:** Chỉ gửi phần dữ liệu thay đổi (ví dụ: trạng thái `đã xem` kèm `message_id`). Nén văn bản bằng thuật toán **Zstandard (zstd)** hoặc **LZ4** với từ điển nén huấn luyện sẵn.
* **Connection Multiplexing:** Dùng HTTP/3 (QUIC), WebSocket hoặc MQTT để truyền tin nhắn, media và đồng bộ trạng thái trên cùng một kết nối mà không bị nghẽn cổ chai.

### 2. Giai Đoạn Lưu Trữ Trên Máy Chủ (Backend Storage)
* **LSM-Tree Databases:** Sử dụng **Apache Cassandra**, **ScyllaDB** hoặc **RocksDB** tối ưu cho tốc độ ghi nối tiếp (Append-only) cực cao.
* **Database Sharding:** Phân mảnh dữ liệu theo `conversation_id` + `time_bucket` để các tin nhắn trong cùng nhóm chat luôn nằm liền kề nhau về mặt vật lý.
* **Phân tầng lưu trữ (Hot - Warm - Cold):** RAM/Redis cho tin nhắn tức thì $\rightarrow$ SSD/NVMe cho tin nhắn vài tháng $\rightarrow$ Object Storage (S3/Ceph) nén khối cho lịch sử nhiều năm.
* **Deduplication (CAS) & CDN:** Băm file media bằng `SHA-256` để chỉ lưu 1 bản gốc duy nhất trên hệ thống, phân phối qua CDN gần người dùng nhất.

### 3. Giai Đoạn Đồng Bộ Hóa Dữ Liệu (Synchronization Engine)
* **Sequence ID / Logical Clock:** Đồng bộ chênh lệch (Sync Delta) dựa trên số thứ tự tăng dần (`seq_id`) của từng cuộc hội thoại.
* **Outbox Pattern:** Lưu tin nhắn gửi lúc mất mạng vào bảng tạm cục bộ kèm `client_generated_uuid` để chống gửi trùng lặp (Idempotency) khi có mạng trở lại.
* **Firebase Cloud Messaging (FCM) High-Priority Data Message:** Đánh thức ứng dụng chạy ngầm trong vài giây để tải trước, giải mã và lưu tin nhắn vào database ngay khi thông báo tới.

### 4. Giai Đoạn Lưu Trữ Dưới Điện Thoại (Local Device Storage)
* **Full-Text Search (FTS5):** Tạo bảng chỉ mục ngược ngay trên SQLite để tìm kiếm từ khóa trong hàng trăm nghìn tin nhắn dưới 50ms.
* **Storage Denormalization:** Phi chuẩn hóa dữ liệu (lưu thẳng tên, avatar vào bảng hội thoại) để tránh lệnh `JOIN` tốn kém khi cuộn danh sách.
* **Progressive Media & BlurHash:** Nhúng chuỗi **BlurHash** (< 2KB) trực tiếp vào gói tin văn bản để hiển thị ảnh mờ ngay lập tức (0ms) trong khi ảnh gốc tải ngầm.
* **LRU Cache & Vacuuming:** Tự động dọn dẹp file media cũ ít truy cập và chạy `PRAGMA optimize` / `VACUUM` bằng `WorkManager` khi thiết bị đang cắm sạc và nhàn rỗi.

### 5. Giai Đoạn Tải Lên Bộ Nhớ & Hiển Thị (In-Memory & UI Loading)
* **Multi-tier Memory Cache:** L1 Cache trên RAM (`LruCache`) lưu 5–10 cuộc hội thoại gần nhất; L2 Cache đọc từ SQLite bằng **Cursor-based Pagination** (`WHERE message_id < ? LIMIT 30`).
* **Prefetching & View Recycling:** Sử dụng `RecyclerView` kết hợp `DiffUtil` và `Prefetch` để tải đón đầu dữ liệu và tái sử dụng ViewHolder ở tốc độ 60Hz–120Hz.

---

## Phần II: Kiến Trúc Bảo Mật Dữ Liệu Cục Bộ (Android Studio Implementation)

Để chống lại các kịch bản trích xuất vật lý (Physical Extraction), phân tích tĩnh (Static Analysis) và tấn công bộ nhớ động (Dynamic Instrumentation/Frida), hệ thống phải triển khai bảo mật đa lớp:

### 1. Quản Lý Khóa Bằng Phần Cứng (Android Keystore & StrongBox)
Tuyệt đối không hardcode khóa mật mã trong mã nguồn Kotlin/Java hay file `gradle.properties`.

* **StrongBox Keymaster:** Ưu tiên lưu khóa trong chip bảo mật vật lý độc lập (HSM trên điện thoại) nếu phần cứng hỗ trợ thông qua `.setIsStrongBoxBacked(true)`.
* **Ràng buộc xác thực người dùng:** Cấu hình `KeyGenParameterSpec` với `.setUserAuthenticationRequired(true)` để buộc hệ điều hành chỉ mở khóa khi có xác thực sinh trắc học (`BiometricPrompt`) hoặc mã PIN thiết bị.
* **Kiến trúc Envelope Encryption (Mã hóa phong bì):**
    1. Sinh ngẫu nhiên **DEK (Data Encryption Key - 256-bit)** bằng `SecureRandom` để mã hóa database/file.
    2. Dùng **KEK (Key Encryption Key - AES-256-GCM)** nằm trong `AndroidKeyStore` để mã hóa (wrap) chính DEK đó.
    3. Lưu `Wrapped_DEK` kèm `IV` (Initialization Vector) vào ổ cứng. Khi mở app, đưa `Wrapped_DEK` vào Keystore để giải mã nạp DEK lên RAM.

### 2. Mã Hóa Toàn Bộ Cơ Sở Dữ Liệu & File (Data-at-Rest Encryption)
* **Cơ sở dữ liệu (Room + SQLCipher):**
    * Tích hợp thư viện `net.zetetic:sqlcipher-android` kết hợp với Jetpack **Room** thông qua `SupportFactory`.
    * Toàn bộ trang dữ liệu (page-level) của SQLite được mã hóa bằng **AES-256-CBC/GCM**, che giấu hoàn toàn nội dung lẫn metadata (số lượng tin nhắn, thời gian, cấu trúc bảng).
* **Lưu trữ Key-Value nhạy cảm (Token, Session):**
    * Sử dụng thư viện **Google Tink** (`AndroidKeysetManager`) kết hợp với **Jetpack DataStore** (thay thế cho `EncryptedSharedPreferences` đã bị deprecated ở các phiên bản Android mới) để mã hóa xác thực (AEAD - `AES256_GCM`).
* **Mã hóa luồng cho Media (Streaming Encryption):**
    * Sử dụng `StreamingAead` của **Google Tink** (`AES256_CTR_HMAC_SHA256_4KB`) để mã hóa ảnh, video, file ghi âm theo từng khối 4KB.
    * Khi người dùng xem ảnh hoặc phát video, giải mã trực tiếp từng chunk trên RAM và đẩy thẳng vào trình phát (`ExoPlayer` / `ImageDecoder`), **tuyệt đối không bao giờ ghi file đã giải mã (plaintext) xuống thư mục `cacheDir`**.

### 3. Bảo Vệ Dữ Liệu Trên Bộ Nhớ Tạm (In-Memory Protection)
Chống lại kỹ thuật dump RAM bằng công cụ như `Fridump` hoặc `GameGuardian` trên thiết bị đã Root.

* **Loại bỏ kiểu `String` cho dữ liệu nhạy cảm:** `String` trong JVM là bất biến (immutable) và nằm trong String Pool cho đến khi Garbage Collector thu gom. Hãy lưu DEK, mật khẩu, và nội dung tin nhắn sau giải mã dưới dạng `ByteArray` hoặc `CharArray`.
* **Xóa sạch vùng nhớ ngay lập tức (Zeroization):** Ngay sau khi hoàn tất thao tác mã hóa/giải mã hoặc đóng màn hình chat, gọi `Arrays.fill(sensitiveByteArray, 0.toByte())`.
* **Native Memory Locking (Android NDK - C/C++):** Với các khóa mật mã cốt lõi (như khóa phiên Signal Protocol), xử lý trực tiếp tại tầng C/C++ thông qua JNI, sử dụng hàm hệ thống `mlock()` để cấm hệ điều hành đẩy vùng RAM này xuống file swap, và dùng `sodium_memzero()` (thư viện **Libsodium**) để xóa sạch bộ nhớ mà không bị trình biên dịch tối ưu hóa bỏ qua.
* **Xóa khóa khi xuống nền (Lifecycle-Aware Locking):** Đăng ký `DefaultLifecycleObserver` theo dõi sự kiện `onStop()` của tiến trình; tự động xóa DEK khỏi RAM và khóa database nếu người dùng thoát app quá một khoảng thời gian quy định (ví dụ: 30 giây).

### 4. Chống Rò Rỉ Qua Hệ Điều Hành & Kiểm Soát Môi Trường (Hardening & RASP)
* **Vô hiệu hóa mọi cơ chế sao lưu của Android (`AndroidManifest.xml`):**
    * Đặt `android:allowBackup="false"` và `android:fullBackupContent="false"`.
    * Cấu hình thuộc tính `android:dataExtractionRules` (Android 12+) để chặn hoàn toàn lệnh `adb backup` và đồng bộ Google Cloud trích xuất thư mục `databases/` và `files/`.
* **Chống rò rỉ giao diện (UI Leakage Prevention):**
    * Gắn cờ `window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)` trên các `Activity` nhạy cảm để chặn chụp màn hình, quay màn hình và ngăn hệ điều hành chụp ảnh thu nhỏ (thumbnail) lưu vào màn hình đa nhiệm (Recent Apps).
    * Thiết lập `EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING` cho ô nhập tin nhắn để ngăn bàn phím bên thứ 3 học và lưu lại từ khóa người dùng gõ.
    * Gắn cờ `ClipDescription.EXTRA_IS_SENSITIVE` (Android 13+) khi sao chép dữ liệu nhạy cảm vào Clipboard và tự động xóa Clipboard sau 30 giây.
* **Tự bảo vệ khi chạy (RASP - Runtime Application Self-Protection):**
    * **Root & Tamper Detection:** Kiểm tra sự tồn tại của binary `su`, Magisk, kiểm tra chữ ký ứng dụng (`PackageInfo.SIGNING_CERTIFICATES`) lúc khởi động và xác thực tính toàn vẹn thiết bị qua **Google Play Integrity API**.
    * **Anti-Frida & Anti-Debugging:** Kiểm tra cờ `Debug.isDebuggerConnected()`, rà soát `/proc/self/maps` và các cổng TCP mặc định (27042) tại tầng Native (C/C++) để phát hiện tiến trình tiêm mã (Frida/Xposed). Nếu phát hiện môi trường không an toàn, lập tức hủy khóa DEK trên RAM và từ chối mở database.

---

## Phần III: Lộ Trình Thư Viện Khuyên Dùng Trên Android Studio

| Thành phần | Thư viện / Công nghệ đề xuất | Mục đích |
| :--- | :--- | :--- |
| **Database & Mã hóa DB** | `Room` + `SQLCipher for Android` | Lưu trữ quan hệ tốc độ cao, mã hóa toàn bộ file DB chuẩn AES-256. |
| **Mã hóa File & Stream** | `Google Tink` (`tink-android`) | Quản lý khóa Keystore chuẩn xác, mã hóa luồng file media không để lại file tạm. |
| **Lưu trữ Token/Config** | `Jetpack DataStore` + `Google Tink` | Thay thế SharedPreferences, lưu trữ bất đồng bộ an toàn tuyệt đối. |
| **Xác thực sinh trắc học** | `androidx.biometric:biometric` | Mở khóa `CryptoObject` gắn liền với phần cứng `AndroidKeyStore`. |
| **Mật mã học E2EE** | `libsignal-android` / `Libsodium` | Triển khai giao thức mã hóa đầu cuối (Double Ratchet) và xóa RAM an toàn. |
| **Đóng gói dữ liệu** | `Protobuf (Wire / Lite)` | Tuần tự hóa dữ liệu nhị phân siêu nhẹ, tối ưu cho Android. |
| **Tác vụ ngầm & Dọn rác** | `Jetpack WorkManager` | Chạy tác vụ `VACUUM` DB, dọn cache media định kỳ khi máy sạc pin. |
| **Bảo vệ mã nguồn** | `R8` (Full Mode) + `NDK (C/C++)` | Làm rối mã nguồn (Obfuscation), thu gọn code và giấu logic kiểm tra bảo mật xuống tầng Native. |