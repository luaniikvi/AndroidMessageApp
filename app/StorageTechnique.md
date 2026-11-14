# Kiến Trúc Lưu Trữ & Tải Dữ Liệu Toàn Diện Trong Ứng Dụng Nhắn Tin

Tài liệu này tổng hợp toàn bộ các kỹ thuật từ **Backend (Server)** đến **Client (Mobile App)** giúp hệ thống xử lý hàng tỷ tin nhắn mà vẫn đảm bảo độ trễ thấp và mượt mà.

---

## 1. Giai Đoạn Đóng Gói & Truyền Tải (Serialization & Transport)
Trước khi được lưu trữ, dữ liệu phải được thu nhỏ tối đa để tiết kiệm băng thông và bộ nhớ.

* **Binary Serialization (Tuần tự hóa nhị phân):** Không dùng JSON (vì chứa nhiều ký tự thừa như dấu ngoặc, tên trường lặp lại). Thay vào đó dùng **Protobuf (Protocol Buffers)**, **FlatBuffers**, **MessagePack** hoặc **Thrift** để nén cấu trúc dữ liệu thành các chuỗi byte siêu nhỏ, giúp đọc/ghi nhanh hơn JSON gấp 5–10 lần.
* **Delta Encoding & Compression (Nén sai phân):** Chỉ gửi phần dữ liệu thay đổi (ví dụ: chỉ gửi trạng thái `đã xem` kèm `message_id` thay vì gửi lại cả gói tin). Các tin nhắn văn bản dài được nén bằng thuật toán **Zstandard (zstd)** hoặc **LZ4** với từ điển nén (Compression Dictionary) huấn luyện sẵn cho ngôn ngữ chat.
* **Connection Multiplexing (Đa luồng trên 1 kết nối):** Sử dụng HTTP/3 (QUIC) hoặc giao thức tự chế trên nền TCP/UDP để gửi tin nhắn chữ, tải ảnh và đồng bộ trạng thái trên cùng một kết nối vật lý mà không bị nghẽn cổ chai (Head-of-Line Blocking).

---

## 2. Giai Đoạn Lưu Trữ Trên Máy Chủ (Backend Storage)
Server phải chịu tải hàng triệu lượt ghi (Write) và đọc (Read) mỗi giây.

* **LSM-Tree Databases (Log-Structured Merge-Tree):** Tin nhắn mới sinh ra liên tục nên thao tác **Ghi (Write)** chiếm đa số. Các hệ thống như **Apache Cassandra**, **ScyllaDB**, **RocksDB** hoặc **HBase** được ưu tiên thay vì SQL truyền thống vì chúng biến mọi thao tác ghi thành ghi nối tiếp (Append-only) trên ổ cứng, đạt tốc độ cực cao.
* **Database Sharding & Partitioning (Phân mảnh dữ liệu):** Dữ liệu không nằm trên một máy chủ mà được chia nhỏ theo `conversation_id` (ID cuộc trò chuyện) kết hợp với `time_bucket` (ví dụ: chia theo từng tháng). Nhờ đó, tin nhắn của cùng một nhóm chat trong một tháng luôn nằm liền kề nhau trên cùng một cụm ổ cứng vật lý.
* **Phân tầng lưu trữ (Hot - Warm - Cold Storage):**
    * **Hot Storage (RAM / Redis / Memcached):** Lưu tin nhắn trong vài giờ hoặc vài ngày gần nhất để truy xuất tức thì.
    * **Warm Storage (SSD / NVMe):** Lưu lịch sử vài tháng gần đây.
    * **Cold Storage (Object Storage như AWS S3, Ceph):** Tin nhắn và file cũ hàng năm trời được gom nén thành các khối lớn (Archive) và chuyển sang ổ cứng giá rẻ, chỉ giải nén khi người dùng cuộn tới.
* **Content-Addressable Storage (CAS) & Deduplication (Chống trùng lặp):** Khi 10.000 người cùng chia sẻ một bức ảnh hoặc video viral, server băm (hash) nội dung file bằng **SHA-256**. File thực tế chỉ được lưu **1 bản duy nhất** trên hệ thống lưu trữ; 10.000 tin nhắn kia chỉ lưu mã hash trỏ tới file đó.
* **CDN & Edge Caching:** Toàn bộ hình ảnh, voice, video, sticker được đẩy ra các máy chủ biên (CDN) đặt gần vị trí địa lý của người dùng nhất để tải về với tốc độ tối đa.

---

## 3. Giai Đoạn Đồng Bộ Hóa Dữ Liệu (Synchronization Engine)
Làm sao để app biết mình đang thiếu tin nhắn nào sau khi tắt mạng hoặc đổi điện thoại?

* **Sequence ID / Logical Clock (Bộ đếm tuần tự):** Mỗi tài khoản hoặc mỗi cuộc hội thoại có một số thứ tự tăng dần (`pts` trong Telegram hoặc `seq_id`). Khi mở app, điện thoại chỉ cần báo server: *"Tôi đang có tới số 1050"*, server sẽ trả về đúng phần chênh lệch từ `1051` đến hiện tại (Sync Delta).
* **Sync Queue & Outbox Pattern (Hàng đợi đồng bộ):** Khi mất mạng, tin nhắn bạn bấm gửi được lưu vào một bảng tạm (`Outbox`) dưới máy với một `client_generated_uuid`. Khi có mạng lại, tiến trình chạy ngầm tự động đẩy lần lượt lên server và dùng UUID đó để chống gửi trùng lặp (Idempotency).
* **Background Fetch & Silent Push:** Hệ điều hành (iOS/Android) cấp quyền cho app nhận các gói tin thông báo ngầm (Silent Push Notification). Ngay khi có tin nhắn tới, app được đánh thức trong vài giây để tải trước nội dung vào database local trước cả khi bạn kịp mở khóa màn hình.

---

## 4. Giai Đoạn Lưu Trữ Dưới Điện Thoại (Local Device Storage)
Tối ưu hóa cho bộ nhớ flash của điện thoại vốn chậm hơn server và dễ bị đầy.

* **Full-Text Search Engine (FTS5):** Để tìm kiếm một từ khóa trong 500.000 tin nhắn chỉ mất 0.05 giây, app sử dụng module **SQLite FTS5** (tạo bảng chỉ mục ngược - Inverted Index) ngay trên điện thoại.
* **Storage Denormalization (Phi chuẩn hóa dữ liệu):** Thay vì chia nhỏ nhiều bảng và dùng lệnh `JOIN` (gây chậm khi dữ liệu lớn), database dưới máy cố tình lưu dư thừa dữ liệu (ví dụ: lưu thẳng tên và avatar người gửi vào bảng danh sách hội thoại) để chỉ cần đọc 1 dòng duy nhất là vẽ xong màn hình chính.
* **Progressive Media & Thumbnail Embedding (Nhúng ảnh siêu nhỏ):**
    * Khi nhận một bức ảnh 5MB, server gửi kèm một chuỗi **BlurHash** hoặc ảnh thumbnail siêu nhỏ (dưới 2KB) nhúng thẳng vào gói tin nhắn văn bản. Ảnh mờ hiện lên ngay lập tức trong 0ms, trong khi ảnh gốc tải ngầm phía sau.
    * Chia nhỏ video thành các đoạn ngắn (HLS / DASH streaming) để phát ngay lập tức mà không cần đợi tải hết cả file video.
* **Storage Eviction & Garbage Collection (Tự động dọn rác):** Định kỳ quét và xóa các file media cũ trong thư mục cache đã quá hạn (ví dụ: sau 30 ngày không mở lại), nhưng vẫn giữ lại metadata và thumbnail để khi người dùng bấm vào thì tải lại từ cloud.
* **Database Vacuuming & Encryption (Mã hóa & Chống phân mảnh):** Dùng **SQLCipher** để mã hóa toàn bộ database dưới máy bằng AES-256 (chống đọc trộm dữ liệu) và chạy lệnh `VACUUM` / `PRAGMA optimize` lúc điện thoại đang cắm sạc và rảnh rỗi để chống phân mảnh file database.

---

## 5. Giai Đoạn Tải Lên Bộ Nhớ & Hiển Thị (In-Memory & UI Loading)
Bước cuối cùng để đưa dữ liệu từ ổ cứng điện thoại lên màn hình 60Hz–120Hz.

* **Multi-tier Memory Cache (Bộ nhớ đệm đa tầng trên RAM):**
    * **L1 Cache (RAM - LRU Cache):** Giữ sẵn dữ liệu của 5–10 cuộc trò chuyện gần nhất trên RAM. Bấm vào là hiện ngay không cần chạm tới ổ cứng (SQLite).
    * **L2 Cache (Disk / SQLite):** Đọc khi người dùng mở các cuộc trò chuyện cũ hơn.
* **Prefetching (Tải đón đầu):**
    * Khi bạn đang ở màn hình danh sách chat, app âm thầm tải trước 20 tin nhắn cuối của 3 người đứng đầu danh sách vào RAM.
    * Khi bạn đang đọc tin nhắn thứ 50 từ dưới lên, app đã âm thầm đọc sẵn từ ổ cứng các tin nhắn từ 51 đến 100 trước khi ngón tay bạn kịp vuốt tới.
* **Zero-Copy Parsing (Đọc dữ liệu không cần sao chép):** Sử dụng **FlatBuffers** hoặc **Memory-mapped files (`mmap`)** cho phép ứng dụng đọc trực tiếp dữ liệu từ file trên ổ cứng như thể nó đang nằm trên RAM mà không tốn thời gian giải mã (deserialize) sang object trung gian.
* **Text Shaping & Glyph Caching:** Việc tính toán ngắt dòng, hiển thị emoji và font chữ rất tốn CPU. Các app tự xây dựng bộ đệm lưu sẵn kích thước từng đoạn văn bản đã đo đạc và dùng bảng vẽ ký tự (Glyph Atlas) trên GPU để vẽ chữ nhanh nhất có thể.